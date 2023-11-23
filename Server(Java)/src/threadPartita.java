import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class threadPartita extends Thread {

    // Elementi principali
    private List<clientHandler> giocatori; 
    private List<Carta> mazzo;

    // Elementi di gioco
    private List<Carta> tavolo; // Carte giocate in tutto il corso della partita
    private List <Carta> carteGiocate; // E' un buffer di n carte, dove n è il numero di giocatori (tiene conto delle carte giocate in un giro)
    private Carta briscola;

    private boolean endGame; // Sentinella fine partita

    public threadPartita(List<clientHandler> giocatori) throws SAXException, IOException, ParserConfigurationException {
        this.giocatori = giocatori;
        mazzo = XMLserializer.read("./Server(Java)/src/Mazzo.xml"); // Leggo il mazzo dal file XML

        tavolo = new ArrayList<Carta>();
        carteGiocate = new ArrayList<Carta>();
        briscola = new Carta(); // Briscola di default

        endGame = false;
    }

    @Override
    public void run() {
        
        preparativi(); // Eseguo le operazioni di inizio partita

        // Distribuisco le carte ai giocatori
        try 
        {
            distribuisciCarte(); 
        } catch (TransformerException e) {
            System.out.println("Errore serializzazione delle carte");
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            System.out.println("Errore serializzazione delle carte");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Errore inoltro delle carte");
            e.printStackTrace();
        }

        // Fase di gioco
        while (!endGame) {
            
            String feedback = "";

            for (clientHandler g : giocatori) 
            {
                try 
                {
                    toccaA(g);
                    feedback = g.responses.take(); // Aspetto la risposta del giocatore
                    
                    
                } catch (IOException | InterruptedException e) 
                {
                    e.printStackTrace();
                }
            }
        }

    }

    public void preparativi()
    {
        Collections.shuffle(mazzo); // Mischio il mazzo
        briscola = mazzo.get(mazzo.size() - 1); // Ricavo la briscola
        mazzo.remove(mazzo.size() - 1); // Rimuovo la briscola dal mazzo
    }

    public void distribuisciCarte() throws TransformerException, ParserConfigurationException, IOException
    {

        int conta = 0; // Per tenere conto del progresso di distribuzione delle carte

        // Distribuisco le carte a partire da quella più in fondo nel mazzo (3 carte per giocatore)
        for (clientHandler g : giocatori) {

            String mano = ""; 
            int indice = 0;
            for(int i = 0; i < 3; i++)
            {
                indice = mazzo.size() - i - 1 - conta; // Indice della carta da assegnare
                mano += XMLserializer.stringfyOmitDeclaration(mazzo.get(indice).serialize()); // Aggiungo la carta alla mano (in formato XML)
                mazzo.remove(indice);
            }
            inviaBriscola(g); // Assieme alla mano, invio anche la briscola
            inviaMano(g,mano); // Invio la mano al giocatore
            conta += 3;
        }
    }

    private void inviaMano(clientHandler g, String mano) throws IOException
    {
        Server.invia(g.outToClient, "<Carte>" + mano + "</Carte>");
    }

    private void inviaBriscola(clientHandler g) throws IOException, TransformerException, ParserConfigurationException
    {
        Server.invia(g.outToClient, "<Carte>" + XMLserializer.stringfyOmitDeclaration(briscola.serialize()) + "</Carte>");
    }

    private void toccaA(clientHandler g) throws IOException
    {
        for (clientHandler p : giocatori) {
            if(p != g)
                Server.invia(p.outToClient, "<Turn>" + g.getUsername() + "</Turn>");
            else
                Server.invia(p.outToClient, "<Turn>Yours<Turn/>");
        }
    }
}
