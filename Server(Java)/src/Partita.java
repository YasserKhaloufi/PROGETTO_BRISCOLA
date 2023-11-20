import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Partita extends Thread {
    List<Giocatore> giocatori;
    Boolean endGame;
    List<Carta> Mazzo;

    public Partita(List<Giocatore> giocatori) throws SAXException, IOException, ParserConfigurationException {
        this.giocatori = giocatori;
        endGame = false;
        Mazzo = XMLserializer.read("./Server(Java)/src/Mazzo.xml");
    }

    @Override
    public void run() {

        // Distribuisco le carte
        try {
            distribuisciCarte();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!endGame) {
            
        }
        
    }

    public void distribuisciCarte() throws TransformerException, ParserConfigurationException, IOException
    {
        Collections.shuffle(Mazzo); // Mischio il mazzo

        int conta = 0; // Per tenere conto del progresso di distribuzione delle carte

        // Distribuisco le carte a partire da quella più in fondo nel mazzo (3 carte per giocatore)
        for (Giocatore g : giocatori) {

            String mano = ""; 
            int indice = 0;
            for(int i = 0; i < 3; i++)
            {
                indice = Mazzo.size() - i - 1 - conta; // Indice della carta da assegnare
                mano += XMLserializer.stringfy(Mazzo.get(indice).serialize()); // Aggiungo la carta alla mano (in formato XML)
                Mazzo.remove(indice);
            }
            Server.invia(g.outToClient, mano);
            conta += 3;
        }
    }
}
