import org.w3c.dom.Element;

public class Carta{

    private String seed;    //denari,coppe,spade,bastoni
    private char number;    //A, 2, 3,4,5,6,7,F,C,R
    private int value;      //11,0,10,0,0,0,0,2,3,4
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
        this.number = XMLserializer.parseTagName(e, "number").charAt(0);
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