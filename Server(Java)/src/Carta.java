import org.w3c.dom.Element;

public class Carta{

    private String seed;
    private char number;
    private int value;
    private String img_path;
    
    public Carta(String seed, char number, int value, String img_path) {
        this.seed = seed;
        this.number = number;
        this.value = value;
        this.img_path = img_path;
    }
    
    //costruttore parser XML
    public Carta(Element e){
        this.seed = XMLserializer.parseTagName(e, "seed");
        this.number = XMLserializer.parseTagName(e, "number");
        this.value = Integer.parseInt(XMLserializer.parseTagName(e, "value"));
        this.img_path = XMLserializer.parseTagName(e, "img_path");
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