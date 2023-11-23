import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Carta{
    //CARTA NAPOLETANA
    private String seme;
    private char numero;
    private int valore;
    private String img_path;
    
    public Carta() {
        seme = "null";
        numero = '0';
        valore = 0;
        img_path = "null";
    }

    public Carta(String seme, char numero, int valore, String img_path) {
        this.seme = seme;
        this.numero = numero;
        this.valore = valore;
        this.img_path = img_path;
    }

    //costruttore da stringa
    public Carta(String s){
        //seme;numero;valore;img_path
        String[] stringaSplittata=s.split(s, ';');
        this.seme=stringaSplittata[0];
        this.numero=stringaSplittata[1].charAt(0);
        this.valore=Integer.parseInt(stringaSplittata[2]);
        this.img_path=stringaSplittata[3];
    }

    //costruttore parser XML
    public Carta(Element e){
        this.seme = XMLserializer.parseTagName(e, "seed");
        this.numero =  XMLserializer.parseTagName(e, "number").charAt(0);
        this.valore = Integer.parseInt(XMLserializer.parseTagName(e, "value"));
        this.img_path = XMLserializer.parseTagName(e, "img_path");
    }

    // Per rendere facile la serializzazione di liste di oggetti di questo tipo
    public Element serialize(Document d) {

        // 1.Crei per ogni attributo un Element usando "d"
        // 2.Esegue le opportune append
        // 3.Ritorni l'Element finale (non devi farne l'append al "d", di quello se 
        //   ne occuperà il metodo che ha chiamato questa funzione)

        Element elementoCarta = d.createElement("Carta");
        
        Element elementoSeed = d.createElement("seed"); elementoSeed.appendChild(d.createTextNode(seme));
        
        Element elementoNumber = d.createElement("number"); elementoNumber.appendChild(d.createTextNode(String.valueOf(numero)));

        Element elementoValue = d.createElement("value"); elementoValue.appendChild(d.createTextNode(String.valueOf(valore)));
        
        Element elementoPath = d.createElement("img_path"); elementoPath.appendChild(d.createTextNode(img_path));

        elementoCarta.appendChild(elementoSeed);
        elementoCarta.appendChild(elementoNumber);
        elementoCarta.appendChild(elementoValue);
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
        return img_path;
    }
    public String ToString(){
        return seme+";"+numero+";"+valore+";"+img_path;
    }
    public Carta ToCarta(String s){
        Carta c=new Carta(s);
        return c;
    }
}
