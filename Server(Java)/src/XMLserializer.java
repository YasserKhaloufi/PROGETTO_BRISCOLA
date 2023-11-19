import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLserializer {

    // METODI PER SERIAlIZZARE E PARSARE LE CARTE

    public static void saveLista(String filePath, List<Carta> lista) throws TransformerException, ParserConfigurationException {
        // SALVA SU FILE 
        Document d = serializzaLista(lista);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();

        t.setOutputProperty(OutputKeys.INDENT, "yes"); // Indentazione
        //t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // se volessi omettere la dichiarazione xml

        StreamResult result = new StreamResult(new File(filePath));
        t.transform(new DOMSource(d), result);
    }

    private static Document serializzaLista(List<Carta> lista)
            throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document d = b.newDocument();

        // Creazione della lista sottoforma di root nel documento
        Element root = d.createElement("Carte");
        d.appendChild(root);

        for (Carta o : lista) {
            /* Utilizzo il metodo serialize(Document d) (dovrai crearlo)
               nella classe dell'oggeto per farmi restituire
               quell'oggetto serializzato, il tutto sottoforma di Document, cosicchè io 
               ne possa fare l'append in "d" facilmente (nota bene che dovrai passargli
               il document di questa funzione in modo da utilizzarlo anche nel metodo 
               serialize dell'oggetto stesso per poterne poi permettere l'append a "d")*/
            root.appendChild(o.serialize(d));
        }

        return d;
    }

    public static List<Carta> read(String filePath) throws SAXException, IOException, ParserConfigurationException {
        // introduzione al parsing XML
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document d = b.parse(filePath);

        List<Carta> lista = new ArrayList<Carta>();

        NodeList nList = d.getElementsByTagName("Carte"); // Lista di oggetti non parsata

        if (nList.getLength() > 0) {
            for (int i = 0; i < nList.getLength(); i++) {
                Element carta = (Element) nList.item(i);

                Carta o = new Carta(carta);

                lista.add(o);
            }
        }

        return lista;
    }

    // Per poter parsare il contenuto di un elemento a partire da un tag ben noto
    public static String parseTagName(Element oggetto, String tagName) {
        NodeList tmp = oggetto.getElementsByTagName(tagName);
        if (tmp.getLength() == 0)
            return null;

        return tmp.item(0).getTextContent();
    }

    // Per poter parsare il contenuto di un elemento a partire da un attributo ben noto
    public static String parseAttribute(Element oggetto, String attributeName) {
        String attribute = oggetto.getAttribute(attributeName);

        return attribute;
    }

    // Nel caso servisse buttare in un file direttamente un oggetto serializzato in append
    public static void saveOggetto(Carta c, String filePath)
            throws ParserConfigurationException, IOException, TransformerException {

        File file = new File(filePath);
        FileWriter fw = new FileWriter(file, true);
        Document d = c.serialize();
        fw.write(stringfy(d));
        fw.flush(); // svuota il buffer di scrittura, forza la scrittura sul disco
        fw.close(); // chiude il file, occorre riaprirlo se si vorrà fare un altra scrittura
    }

    public static String stringfy(Document d) throws TransformerException {
        // SALVA SU STRINGA
        // Creare un oggetto Transformer per la trasformazione in stringa
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();

        t.setOutputProperty(OutputKeys.INDENT, "yes"); // Indentazione
        //t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // se volessi omettere la dichiarazione xml

        StringWriter writer = new StringWriter();
        t.transform(new DOMSource(d), new StreamResult(writer));
        String xmlString = writer.toString();
        return xmlString;
    }

    // METODI PER PARSARE I MESSAGGI DEI CLIENT

    // Per ricavare il comando inviato dal client a partire dalla stringa recieved, posizionato come primo elemento
    public static String getCommand(String ricevuto) throws SAXException, IOException, ParserConfigurationException {
        
        Document d = creaDocumento(ricevuto);

        // Il primo elemento presente nell'XML inviato dal client identifica il comando
        // da eseguire
        Element root = d.getDocumentElement();

        return root.getTagName(); // Lo ricavo dal nome del tag
    }

    // Potrebbe servire per estrarre una carta (?)
    public static String getUsername(String ricevuto) throws ParserConfigurationException, SAXException, IOException {
        
        Document d = creaDocumento(ricevuto);

        // TO DO: è ancora da vedere dove sarà posizionata la carta in un comando 
        Element root = (Element) d.getDocumentElement();

        return root.getTextContent();
    }

    // Potrebbe servire per estrarre una carta (?)
    public static Carta getArgomento(String ricevuto) throws ParserConfigurationException, SAXException, IOException {
        
        Document d = creaDocumento(ricevuto);

        // TO DO: è ancora da vedere dove sarà posizionata la carta in un comando 
        Element o = (Element) d.getDocumentElement().getFirstChild();
        Carta c = new Carta(o);

        return c;
    }

    // Per creare documento a a partire dal messaggio ricevuto (così non devo sempre riscrivere sempre la stessa cosa per ogni metodo di parsing)
    private static Document creaDocumento(String ricevuto) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();

        // Il metodo parse del dbuilder lavora solo con file o stream, quindi
        // convertiamo la stringa prima di passarla
        InputSource is = new InputSource(new StringReader(ricevuto));
        Document d = b.parse(is);

        return d;
    }
}
