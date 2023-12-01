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

/**
 * Provides methods to serialize and parse Carta objects and messages received from clients.
 * <p>
 * The serialization and parsing methods are used to convert Carta objects into XML Documents and vice versa.
 * The methods for parsing messages received from clients are used to extract the command, the argument and the Carta object from the message.
 * <p>
 * The serialization and parsing methods are used by the {@link Carta} class to serialize and parse Carta objects.
 * The methods for parsing messages received from clients are used by the {@link clientHandler} class to parse the messages received from the clients.
 * <p>
 * The serialization and parsing methods are static, so they can be called without creating an instance of the class.
 * <p>
 * The methods for parsing messages received from clients are not static, so they can be called only by creating an instance of the class.
 * <p>
 * The methods for parsing messages received from clients are deprecated because they are not used in the project.
 */
public class XMLserializer {

    // METODI PER SERIAlIZZARE E PARSARE LE CARTE

    /**
     * Serializes a list of Carta objects into an XML Document.
     * <p>
     * <ol>
     * <li>Creates a new Document;</li>
     * <li>Uses the {@link Carta#serialize(Document d)} method to serialize each Carta object;</li>
     * <li>Then appends each serialized Carta object to the root element of the Document.</li>
     * </ol>
     * <p>
     * The resulting Document will have the following structure:
     * <pre>
     * {@code
     * <Carte>
     *      <Carta>
     *       ...
     *      </Carta>
     *      <Carta>
     *       ...
     *      </Carta>
     * </Carte>}
     * </pre>
     *
     * @param lista the list of Carta objects to be serialized
     * @return the serialized XML Document
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created
     * @throws TransformerException if an error occurs during the serialization process
     */
    public static Document serializzaLista(List<Carta> lista)
            throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document d = b.newDocument();

        // Creazione della lista sottoforma di root nel documento
        Element root = d.createElement("Carte");
        d.appendChild(root);

        for (Carta c : lista) {
            /* Utilizzo il metodo serialize(Document d) (dovrai crearlo)
               nella classe dell'oggeto per farmi restituire
               quell'oggetto serializzato, il tutto sottoforma di Document, cosicchè io 
               ne possa fare l'append in "d" facilmente (nota bene che dovrai passargli
               il document di questa funzione in modo da utilizzarlo anche nel metodo 
               serialize dell'oggetto stesso per poterne poi permettere l'append a "d")*/
            root.appendChild(c.serialize(d));
        }

        return d;
    }

    /**
     * Parses the content of an XML element from a Document.
     * <p>
     * This method is used to parse the content of an XML element from a Document, given the tag name of the element.
     * <p>
     * For example, given the following XML Document:
     * <pre>
     * {@code
     * <Carta>
     *      <seme>...</seme>
     *      ...
     * </Carta>}
     * </pre>
     * <p>
     * The following code will return the content of the {@code <seme>} element:
     * <pre>
     * {@code String nome = XMLserializer.parseTagName(carta, "seme"); }
     * </pre>
     * 
     * @param e the XML element to parse
     * @param tagName the tag name of the element to parse
     * @return the content of the XML element
     */
    public static String parseTagName(Element e, String tagName) {
        NodeList tmp = e.getElementsByTagName(tagName);
        if (tmp.getLength() == 0)
            return null;

        return tmp.item(0).getTextContent();
    }

    /**
     * Converts an XML Document into a String.
     * <p>
     * <ol>
     * <li>Creates a Transformer object to transform the Document into a StreamResult;</li>
     * <li>Sets the output properties of the Transformer to omit the XML declaration and indent the XML file;</li>
     * <li>Creates a StringWriter object to save the Document to a String.</li>
     * </ol><br>
     * The declaration is omitted, otherwise the client would not be able to parse correctly
     * 
     * @param d the Document to stringify
     * @return the Document as a String
     * @throws TransformerException if an error occurs during the serialization process
     */
    public static String stringfyNoIndent(Document d) throws TransformerException {
        // Creare un oggetto Transformer per la trasformazione in stringa
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();

        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // ometto la dichiarazione xml

        StringWriter writer = new StringWriter();
        t.transform(new DOMSource(d), new StreamResult(writer));
        String xmlString = writer.toString();
        return xmlString;
    }

    // METODI PER PARSARE I MESSAGGI DEI CLIENT

    /**
     * Parses the command from a message received from a client.
     * <p>
     * For example, given the following XML message:
     * <pre>
     * {@code
     * <Start>
     *      ...
     * </Start>}
     * </pre>
     * <p>
     * The following code will return the command name "Start":
     * <pre>
     * {@code String comando = XMLserializer.getComando(ricevuto);}
     * </pre>
     * 
     * @param ricevuto the message received from the client
     * @return the command name
     * @throws SAXException if an error occurs during the parsing process
     * @throws IOException if an error occurs during the parsing process
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created
     */
    public static String getComando(String ricevuto) throws SAXException, IOException, ParserConfigurationException {
        
        Document d = creaDocumento(ricevuto);

        // Il primo elemento presente nell'XML inviato dal client identifica il comando
        // da eseguire
        Element root = d.getDocumentElement();

        return root.getTagName(); // Lo ricavo dal nome del tag
    }

    /**
     * Parses a Carta object from a message received from a client.
     * <p>
     * For example, given the following XML message:
     * <pre>
     * {@code
     * <Carta>
     *      <seme>...</seme>
     *      ...
     * </Carta>}
     * </pre>
     * <p>
     * The following code will return a Carta object:
     * <pre>
     * {@code Carta c = XMLserializer.getCarta(ricevuto);}
     * </pre>
     * <p>
     * The {@link Carta#Carta(Element carta)} constructor is used to create the Carta object.
     * 
     * @param ricevuto the message received from the client
     * @return the Carta object
     * @throws SAXException if an error occurs during the parsing process
     * @throws IOException if an error occurs during the parsing process
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created
     */
    public static Carta getCarta(String ricevuto) throws ParserConfigurationException, SAXException, IOException {
        
        Document d = creaDocumento(ricevuto);

        Element o = (Element) d.getDocumentElement();

        Carta c = new Carta(o);

        return c;
    }

    /**
     * Extracts the argument from a command received from a client.
     * <p>
     * The argument is normally the text content of the root element of the XML message, which represents the command.
     * 
     * @param ricevuto the message received from the client
     * @return the username
     * @throws SAXException if an error occurs during the parsing process
     * @throws IOException if an error occurs during the parsing process
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created
     */
    public static String getArgomento(String ricevuto) throws ParserConfigurationException, SAXException, IOException {
        
        Document d = creaDocumento(ricevuto);

        Element o = (Element) d.getDocumentElement();

        return o.getTextContent();
    }

    // Per creare documento a a partire dal messaggio ricevuto (così non devo sempre riscrivere sempre la stessa cosa per ogni metodo di parsing)

    /**
     * Creates a Document from a message received from a client.
     * <p>
     * This method is used to create a Document from a message received from a client, so that the message can be parsed.
     * It purpose is to avoid having to write the same code for each parsing method.
     * 
     * @param ricevuto the message received from the client
     * @return the Document created from the message
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created
     * @throws SAXException if an error occurs during the parsing process
     * @throws IOException if an error occurs during the parsing process
     */
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

    // DEPRECATED METHODS:

    /**
     * Saves a Carta object to an XML file.
     * <p>
     * <ol>
     * <li>Serializes the Carta object into an XML Document using the {@link Carta#serialize()} method;</li>
     * <li>Creates a Transformer object to transform the Document into a StreamResult;</li>
     * <li>Sets the output properties of the Transformer to indent the XML file;</li>
     * <li>Creates a FileWriter object to save the Document to a file.</li>
     * </ol>
     * 
     * @deprecated this method is deprecated because it is not used in the project.
     * @param c The Carta object to serialize and save.
     * @param filePath The path of the XML file on which to save the Carta object.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     * @throws IOException If an error occurs during the serialization process.
     * @throws TransformerException If an error occurs during the serialization process.
     */
    public static void saveOggetto(Carta c, String filePath)
            throws ParserConfigurationException, IOException, TransformerException {

        // Nel caso servisse buttare in un file direttamente un oggetto serializzato in append
        File file = new File(filePath);
        FileWriter fw = new FileWriter(file, true);
        Document d = c.serialize();
        fw.write(stringfy(d));
        fw.flush(); // svuota il buffer di scrittura, forza la scrittura sul disco
        fw.close(); // chiude il file, occorre riaprirlo se si vorrà fare un altra scrittura
    }

    /**
     * Converts an XML Document into a String.
     * 
     * @deprecated this method is deprecated because it is not used in the project.
     * @param d The Document to stringify.
     * @return The Document as a String.
     * @throws TransformerException If an error occurs during the transformation process.
     */
    public static String stringfy(Document d) throws TransformerException {
        // SALVA SU STRINGA
        // Creare un oggetto Transformer per la trasformazione in stringa
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();

        t.setOutputProperty(OutputKeys.INDENT, "yes"); // Indentazione
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // Ometto la dichiarazione per poter concatenare il documento XML a un altro

        StringWriter writer = new StringWriter();
        t.transform(new DOMSource(d), new StreamResult(writer));
        String xmlString = writer.toString();
        return xmlString;
    }

    /**
     * Saves a list of Carta objects to an XML file.
     * <p>
     * <ol>
     * <li>Serializes the list of Carta objects into an XML Document using the {@link #serializzaLista(List<Carta> lista)} method;</li>
     * <li>Creates a Transformer object to transform the Document into a StreamResult;</li>
     * <li>Sets the output properties of the Transformer to indent the XML file;</li>
     * <li>Creates a StreamResult object to save the Document to a file.</li>
     * </ol>
     * 
     * @deprecated this method is deprecated because it is not used in the project.
     * @param filePath The path of the XML file on which to save the list.
     * @param lista The list of Carta objects to serialize and save.
     * @throws TransformerException If an error occurs during the transformation process.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     */
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

    /**
     * Reads a list of Carta objects from an XML file.
     * <p>
     * <ol>
     * <li>Creates a new DocumentBuilder;</li>
     * <li>Parses the XML file into a Document;</li>
     * <li>Gets the root element of the Document;</li>
     * <li>Gets the list of Carta elements from the root element;</li>
     * <li>For each Carta element, creates a new Carta object using the {@link Carta#Carta(Element carta)} constructor;</li>
     * <li>Adds the Carta object to the list.</li>
     * </ol>
     * 
     * @deprecated this method is deprecated because it is not used in the project.
     * @param filePath The path of the XML file to read.
     * @return The list of Carta objects read from the XML file.
     * @throws SAXException If an error occurs during the parsing process.
     * @throws IOException If an error occurs during the parsing process.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     */
    public static List<Carta> read(String filePath) throws SAXException, IOException, ParserConfigurationException {
        // introduzione al parsing XML
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document d = b.parse(filePath);

        List<Carta> lista = new ArrayList<Carta>();

        Element root = d.getDocumentElement(); // Lista di oggetti non parsata
        NodeList nList = root.getElementsByTagName("Carta");

        if (nList.getLength() > 0) {
            for (int i = 0; i < nList.getLength(); i++) {
                Element carta = (Element) nList.item(i);

                Carta o = new Carta(carta);

                lista.add(o);
            }
        }
        return lista;
    }

    /**
     * Parses the content of an XML element from a Document.
     * <p>
     * This method is used to parse the content of an XML element from a Document, given the name of an attribute of the element.
     * <p>
     * For example, given the following XML Document:
     * <pre>
     * {@code
     * <Carta nome="Asso">
     *      <seme>...</seme>
     *      ...
     * </Carta>
     * }
     * </pre>
     * <p>
     * The following code will return the content of the nome attribute of the {@code <Carta>} element:
     * <pre>
     * {@code
     * String nome = XMLserializer.parseAttribute(carta, "nome");
     * }
     * </pre>
     * 
     * @deprecated this method is deprecated because it is not used in the project.
     * @param oggetto the XML element to parse
     * @param attributeName the name of the attribute of the element to parse
     * @return the content of the XML element
     */
    public static String parseAttribute(Element oggetto, String attributeName) {
        String attribute = oggetto.getAttribute(attributeName);

        return attribute;
    }
}
