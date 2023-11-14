import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Carta{

    private String seed;
    private int number;
    private int value;
    private String img_path;
    
    public Carta(String seed, int number, int value, String img_path) {
        this.seed = seed;
        this.number = number;
        this.value = value;
        this.img_path = img_path;
    }
    
    //costruttore parser XML
    public Carta(Element e){
        this.seed = XMLserializer.parseTagName(e, "seed");
        this.number =  Integer.parseInt(XMLserializer.parseTagName(e, "number"));
        this.value = Integer.parseInt(XMLserializer.parseTagName(e, "value"));
        this.img_path = XMLserializer.parseTagName(e, "img_path");
    }

    // Per rendere facile la serializzazione di liste di oggetti di questo tipo
    public Element serialize(Document d) {

        // 1.Crei per ogni attributo un Element usando "d"
        // 2.Esegue le opportune append
        // 3.Ritorni l'Element finale (non devi farne l'append al "d", di quello se 
        //   ne occuperà il metodo che ha chiamato questa funzione)

        Element elementoCarta = d.createElement("Carta");
        
        Element elementoSeed = d.createElement("seed"); elementoSeed.appendChild(d.createTextNode(seed));
        
        Element elementoNumber = d.createElement("number"); elementoNumber.appendChild(d.createTextNode(String.valueOf(number)));
        
        Element elementoPath = d.createElement("img_path"); elementoPath.appendChild(d.createTextNode(img_path));

        elementoCarta.appendChild(elementoSeed);
        elementoCarta.appendChild(elementoNumber);
        elementoCarta.appendChild(elementoPath);

        return elementoCarta;
    }

    // Potrebbe servire ritornare un Document invece che un element, ad esempio quando vuoi scrivere l'oggetto subito su file
    public Document serialize() throws ParserConfigurationException
    {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document d = b.newDocument();

        Element root = d.createElement("Carta");
     
        root.appendChild(serialize(d));
        
        d.appendChild(root);

        return d;   
    }


    public String getSeed() {
        return seed;
    }

    public int getNumber() {
        return number;
    }

    public int getValue() {
        return value;
    }

    public String getImg_path() {
        return img_path;
    }
}