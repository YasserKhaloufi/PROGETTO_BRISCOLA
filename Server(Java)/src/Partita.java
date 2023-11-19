import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Partita extends Thread {
    List<Giocatore> giocatori;
    Boolean endGame;

    public Partita(List<Giocatore> giocatori) {
        this.giocatori = giocatori;
        endGame = false;
    }

    @Override
    public void run() {
        
        List<Carta> Mazzo = new ArrayList<Carta>();
        try {
            Mazzo = XMLserializer.read("./Server(Java)/src/Mazzo.xml");
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Collections.shuffle(Mazzo); // Mischio il mazzo
        
        while (!endGame) {
            
        }
    }
}
