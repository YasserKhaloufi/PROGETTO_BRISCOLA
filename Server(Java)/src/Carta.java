import java.nio.file.Paths;

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
    public String Img_path;
    
    public Carta() {
        seme = "null";
        numero = '0';
        valore = 0;
        Img_path = "null";
    }

    public Carta(String seme, char numero, int valore, String img_path) {
        this.seme = seme;
        this.numero = numero;
        this.valore = valore;
        this.Img_path = img_path;
    }

    //costruttore parser XML
    public Carta(Element e){
        this.seme = XMLserializer.parseTagName(e, "seme");
        this.numero =  XMLserializer.parseTagName(e, "numero").charAt(0);
        this.valore = Integer.parseInt(XMLserializer.parseTagName(e, "valore"));
        this.Img_path = XMLserializer.parseTagName(e, "Img_path");
    }

    // Per rendere facile la serializzazione di liste di oggetti di questo tipo
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

    // Potrebbe servire ritornare un Document invece che un element, ad esempio quando vuoi scrivere l'oggetto subito su file
    public Document serialize() throws ParserConfigurationException
    {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document d = b.newDocument();

        Element root = serialize(d);
     
        d.appendChild(root);
        
        return d;   
    }

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
