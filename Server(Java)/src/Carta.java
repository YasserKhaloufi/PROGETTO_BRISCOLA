// Per ripristinare path immagine
import java.nio.file.Paths;

// Parsing XML e serializzazione
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Represents a playing card.
 * <p>
 * <ul>
 * <li>It contains information about the suit, number, value, and image path of the card.<li>
 * <li>It can be serialized and deserialized from XML.</li>
 * </ul>
 * 
 */
public class Carta{

    /**
     * The suit of a playing card.
     */
    private String seme;
    
    /**
     * The number of the card.
     */
    private char numero;


    /**
     * The value of a card.
     */
    private int valore;

    /**
     * The path of the image of the card (which is a client side resource).
     * 
     * TO DO: client should build the path of the card himself, using its attributes, without having to be sent the path by the server.
     */
    public String Img_path;
    
    /**
     * Empty constructor.
     * <p>
     * Constructs a new instance of the Carta class with default values.
    */
    public Carta() {
        seme = "null";
        numero = '0';
        valore = 0;
        Img_path = "null";
    }

    /**
     * Parameterized constructor.
     * <p>
     * Constructs a new instance of the Carta class with the specified values.
     * 
     * @param seme The suit of the card.
     * @param numero The number of the card.
     * @param valore The value of the card.
     * @param img_path The path of the image of the card.
     */
    public Carta(String seme, char numero, int valore, String img_path) {
        this.seme = seme;
        this.numero = numero;
        this.valore = valore;
        this.Img_path = img_path;
    }

    /**
     * Parameterized constructor.
     * <p>
     * Constructs a new instance of the Carta from an XML Element (deserialization).
     * It is primarily used to extract cards from XML formatted messages sent by clients.
     * Uses XMLserializer to parse the XML Element nested elements, which represent the card attributes.
     * {@link XMLserializer#parseTagName(Element, String)}
     * 
     * @param e The XML Element from which to extract the card.
     */
    public Carta(Element e){
        this.seme = XMLserializer.parseTagName(e, "seme");
        this.numero =  XMLserializer.parseTagName(e, "numero").charAt(0);
        this.valore = Integer.parseInt(XMLserializer.parseTagName(e, "valore"));
        this.Img_path = XMLserializer.parseTagName(e, "Img_path");
    }


    /**
     * Serializes the Carta object into an Element, using a given document.
     * <p>
     * <ol>
     * <li>First of all, it creates a Carta Element using the given Document.</li>
     * <li>Then an Element for each attribute using is created, to which the corresponding value is appended in the form of a TextNode.</li>
     * <li>Then, each attribute Element is appended to the Carta Element.</li>
     * <li>Finally, the Carta Element is appended to the given Document.</li>
     * </ol>
     * The caller of this function should append the returned Element to the Document he is using.
     * It is primarily used to append cards to XML formatted messages sent to clients.
     * <p>
     * The resulting Element will have the following structure:
     * <pre>
     * {@code
     * <Carta>
     *   <seme>...</seme>
     *   <numero>...</numero>
     *   <valore>...</valore>
     *   <Img_path>...</Img_path>
     * </Carta>
     * }
     * </pre>
     * 
     * 
     * @param d the Document to create the Carta Element in.
     * @return the serialized Element representing the Carta object.
     */
    public Element serialize(Document d) {

        // 1.Crei per ogni attributo un Element usando "d"
        // 2.Esegue le opportune append
        // 3.Ritorni l'Element finale (non devi farne l'append al "d", di quello se 
        //   ne occuperà il metodo che ha chiamato questa funzione)

        Element elementoCarta = d.createElement("Carta");
        
        Element elementoSeed = d.createElement("seme"); elementoSeed.appendChild(d.createTextNode(seme));
        
        Element elementoNumber = d.createElement("numero"); elementoNumber.appendChild(d.createTextNode(String.valueOf(numero)));

        Element elementoValue = d.createElement("valore"); elementoValue.appendChild(d.createTextNode(String.valueOf(valore)));
        
        Element elementoPath = d.createElement("Img_path"); elementoPath.appendChild(d.createTextNode(Img_path));

        elementoCarta.appendChild(elementoSeed);
        elementoCarta.appendChild(elementoNumber);
        elementoCarta.appendChild(elementoValue);
        elementoCarta.appendChild(elementoPath);

        return elementoCarta;
    }

    /**
     * Serializes the Carta object into a Document.
     * <p>
     * Uses {@link Carta#serialize(Document)} to serialize the Carta object into an Element, then appends it to the created Document, which is returned.
     * The single XML serialized card, more precisely the returned document, can be stringified and sent to the client in the form of a string, or written to a file.
     * An example of application of this function is {@link Carta#toXML()}. 
     * 
     * @return the serialized Document representing the Carta object.
     */
    public Document serialize() throws ParserConfigurationException
    {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document d = b.newDocument();

        Element root = serialize(d);
     
        d.appendChild(root);
        
        return d;   
    }

    // Confronta this carta con un'altra carta, ritornando true se, data una briscola e un seme di mano, this è migliore di altraCarta

    /**
     * Compares this card with another card, returning true if, given a briscola and a hand suit, this is better than altraCarta.
     * <p>
     * The logic with which the comparison between cards was implemented is as follows:
     * <ul>
     * <li>Cases of this:
     * <ol>
     * <li>This has the briscola suit
     * <ul>
     * <li>altraCarta can win if:
     * <ol>
     * <li>it has the briscola suit and a higher value</li>
     * </ol>
     * </li>
     * </ul>
     * </li>
     * <li>This has the hand suit
     * <ul>
     * <li>altraCarta can win if:
     * <ol>
     * <li>it has the briscola suit</li>
     * <li>it has the hand suit and a higher value</li>
     * </ol>
     * </li>
     * </ul>
     * </li>
     * <li>This has a different suit from both the briscola and the hand suit
     * <ul>
     * <li>altraCarta can win if:
     * <ol>
     * <li>it has the briscola suit</li>
     * <li>it has the hand suit</li>
     * <li>it has a higher value</li>
     * </ol>
     * </li>
     * </ul>
     * </li>
     * </ol>
     * </li>
     * </ul>
     * 
     * @param altraCarta the card to compare this card with.
     * @param semeBriscola the briscola suit.
     * @param semeDiMano the hand suit (the suit of the first card played).
     * @return true if this card is better than altraCarta, false otherwise.
     */
    public boolean miglioreDi(Carta altraCarta, String semeBriscola, String semeDiMano) 
    {
        String semeAltraCarta = altraCarta.getSeme();
        int valoreAltraCarta = altraCarta.getValore();

        /*
         * Logica con cui è stato implementato il confronto tra carte:
         *  - Casi di this:
            *  1. This ha il seme di briscola 
                    altra può vincere se:
                        - ha il seme di briscola e un valore maggiore

            *  2. This ha il seme di mano 
                    altra può vincere se:
                        - ha il seme di briscola 
                        - ha il seme di mano e un valore maggiore
                        
            *  3. This ha un seme diverso sia da quello di briscola che da quello di mano
                    altra può vincere se:
                        - ha il seme di briscola
                        - ha il seme di mano
                        - ha un valore maggiore
         */

        if (seme.equals(semeBriscola)) // Se this ha il seme della briscola
            return !semeAltraCarta.equals(semeBriscola) || valore > valoreAltraCarta; // This vince a prescindere se altraCarta non ha il seme della briscola, altrimenti vince se ha un valore maggiore
        else if (seme.equals(semeDiMano)) // Se this ha lo stesso seme della prima carta giocata (ma non della briscola)
        {
            if (semeAltraCarta.equals(semeBriscola)) // Se altraCarta ha il seme della briscola
                return false; // AltraCarta vince a prescindere
            else if (semeAltraCarta.equals(semeDiMano)) // Se altraCarta ha lo stesso seme della prima carta giocata
                return valore > valoreAltraCarta; // This vince se ha un valore maggiore
            else // Se altraCarta ha un seme diverso sia dalla briscola che dalla prima carta giocata
                return true; // This vince a prescindere
        }
        else // Se this ha un seme diverso sia dalla briscola che dalla prima carta giocata
        {
            if (semeAltraCarta.equals(semeBriscola)) // Se altraCarta ha il seme della briscola
                return false; // AltraCarta vince a prescindere
            else if (semeAltraCarta.equals(semeDiMano)) // Se altraCarta ha lo stesso seme della prima carta giocata
                return false; // AltraCarta vince a prescindere
            else // Se altraCarta ha un seme diverso sia dalla briscola che dalla prima carta giocata
                return valore > valoreAltraCarta; // This vince se ha un valore maggiore
        }
    }

    /**
     * Serializes the Carta object into a string.
     * <p>
     * Uses {@link Carta#serialize()} to serialize the Carta object into a Document, then stringifies it using {@link XMLserializer#stringfyNoIndent(Document)}.
     * It is primarily used to directly send the serialized card to the client in the form of an XML string.
     * 
     * @return the serialized string representing the Carta object.
     */
    public String toXML() throws TransformerException, ParserConfigurationException
    {
        return XMLserializer.stringfyNoIndent(serialize());
    }

    /**
     * Only returns the name of the image of the card, excluding the rest of the path.
     * <p>
     * Uses {@link Paths#getFileName()} to get the name of the image of the card from its path.
     * It is primarily used to send the name of the image of the card to the client, so that the client can build the path of the image himself.
     * 
     * @return the name of the image of the card.
     */
    public String getImgName() {
        return Paths.get(Img_path).getFileName().toString();
    }

    public String getSeme() {
        return seme;
    }

    public int getNumero() {
        return numero;
    }

    public int getValore() {
        return valore;
    }

    public String getImg_path() {
        return Img_path;
    }
    public String ToString(){
        return seme+";"+numero+";"+valore+";"+Img_path;
    }
}
