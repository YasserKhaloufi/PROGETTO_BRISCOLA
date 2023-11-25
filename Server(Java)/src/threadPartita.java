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
        } catch (InterruptedException e) {
            System.out.println("Errore attesa");
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

    /* Se non aspettassi un ack (feedback) da parte del client dopo averli inviato un messaggio, è probabile
     * che i messaggi a lui inviati dal server si accavallino, impedendoli di parsare correttamente le singole informazioni.
     * Perciò faccio si che il client restituisca un ack ogni volta che gli viene inviata un informazione, 
     * per notificare al server che questi ha ricevuto e parsato correttamente l'informazioni.
     */
    public void distribuisciCarte() throws TransformerException, ParserConfigurationException, IOException, InterruptedException
    {

        int conta = 0; // Per tenere conto del progresso di distribuzione delle carte

        // Distribuisco le carte a partire da quella più in fondo nel mazzo (3 carte per giocatore)
        for (clientHandler g : giocatori) {

            List<Carta> mano = new ArrayList<Carta>();
            int indice = 0;
            for(int i = 0; i < 3; i++)
            {
                indice = mazzo.size() - i - 1 - conta; // Indice della carta da assegnare
                mano.add(mazzo.get(indice));
                mazzo.remove(indice);
            }
            inviaBriscola(g); // Invio la briscola al giocatore
            g.responses.take(); // Aspetto l'ACK
            inviaMano(g,mano); // Invio la mano al giocatore
            g.responses.take(); // Aspetto l'ACK
            conta += 3;
        }
    }

    private void inviaMano(clientHandler g, List<Carta> mano) throws IOException, TransformerException, ParserConfigurationException
    {
        Server.invia(g.outToClient, XMLserializer.stringfyOmitDeclaration(XMLserializer.serializzaLista(mano)));
    }

    private void inviaBriscola(clientHandler g) throws IOException, TransformerException, ParserConfigurationException
    {
        List<Carta> temp = new ArrayList<Carta>(); temp.add(briscola);
        Server.invia(g.outToClient, XMLserializer.stringfyOmitDeclaration(XMLserializer.serializzaLista(temp)));
    }

    private void toccaA(clientHandler g) throws IOException
    {
        for (clientHandler p : giocatori) {
            if(p != g)
                Server.invia(p.outToClient, "<Turn>" + g.getUsername() + "</Turn>");
            else
                Server.invia(p.outToClient, "<Turn>Yours</Turn>");
        }
    }
}
