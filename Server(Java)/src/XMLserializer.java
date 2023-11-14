import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLserializer{

    public static String parseTagName(Element oggetto, String tagName) {
        NodeList tmp = oggetto.getElementsByTagName(tagName);
        if (tmp.getLength() == 0)
            return null;

        return tmp.item(0).getTextContent();
    }
}