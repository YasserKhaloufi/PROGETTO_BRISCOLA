import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class threadPartita extends Thread {

    // Elementi di gioco
    private static List<Carta> mazzo;
    private List <Carta> carteGiocate; // E' un buffer di n carte, dove n è il numero di giocatori (tiene conto delle carte giocate in un giro)
    private static Carta briscola; // Viene scelta all'inizio della partita

    private static boolean briscolaPescata = false;

    public threadPartita() throws SAXException, IOException, ParserConfigurationException {        
        mazzo = generaMazzo(); 
        
        carteGiocate = new ArrayList<Carta>();
        briscola = new Carta();
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
        while (!finePartita()) {
            for (clientHandler g : Server.giocatori) 
            {
                try 
                {
                    toccaA(g); // Informo i giocatori di chi è il turno
                    
                    String scelta = g.risposte.take(); // Aspetto la scelta del giocatore di turno
                    Carta c = XMLserializer.getCarta(scelta); c.Img_path = c.getImgName(); // Ricavo la carta scelta dal giocatore
                    
                    // Avviso i giocatori della carta giocata e la aggiungo al buffer del giro
                    cartaGiocata(c);
                    carteGiocate.add(c);
                    
                    System.out.println(c.ToString() + "giocata\n"); // Debug
                    
                    if(carteGiocate.size() == Server.giocatori.size()) // Se tutti i giocatori hanno giocato la propria carta
                    {   
                        notificaVincitoreGiro();
                        notificaPunteggio();
                        carteGiocate.clear(); // Svuoto il buffer delle carte giocate
                    }
                    
                    
                } catch (IOException | InterruptedException | ParserConfigurationException | SAXException | TransformerException e) 
                {
                    e.printStackTrace();
                }
            }
        }

        Server.endGame = true;

        try {
            notificaVincitore();
        } catch (IOException e) {
            System.out.println("");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    // Mischia e sceglie la briscola
    private void preparativi()
    {
        Collections.shuffle(mazzo); // Mischio il mazzo

        // Estraggo la briscola dal mazzo (l'ultima carta)
        briscola = mazzo.get(mazzo.size() - 1);
        mazzo.remove(mazzo.size() - 1);
    }

    // Distribuisce a tutti i giocatori le carte e gli informa della briscola
    private void distribuisciCarte() throws TransformerException, ParserConfigurationException, IOException, InterruptedException
    {
        int conta = 0; // Per tenere conto del progresso di distribuzione delle carte

        // Distribuisco le carte a partire da quella più in fondo nel mazzo (3 carte per giocatore)
        for (clientHandler g : Server.giocatori) {

            List<Carta> mano = new ArrayList<Carta>();
            int indice = 0;
            for(int i = 0; i < 3; i++)
            {
                indice = mazzo.size() - i - 1 - conta; // Indice della carta da assegnare
                mano.add(mazzo.get(indice));
                mazzo.remove(indice);
            }
            
            inviaBriscola(g); // Invio la briscola al giocatore
            g.risposte.take(); // Aspetto l'ACK

            inviaMano(g,mano); // Invio la mano al giocatore
            g.risposte.take(); // Aspetto l'ACK

            conta += 3;
        }
    }

    /* Invio informazione specifica relativa alla partita */

    // Informa i giocatori del se è il loro turno o meno
    private void toccaA(clientHandler g1) throws IOException, InterruptedException, TransformerException, ParserConfigurationException
    {
        // Dato un giocatore g1 a cui tocca, scorro tutti i giocatori
        for (clientHandler g2 : Server.giocatori) 
        {
            if(g2 != g1)
                Server.notificaUnicast(g2, "Turn", g1.getUsername()); // Se g2 non corrisponde g1, gli dico che non è il suo turno, inviandoli il nome del giocatore a cui tocca
            else
                Server.notificaUnicast(g2, "Turn", "Yours");; // Altrimenti gli dico che è il suo turno (Yours)
        }
    }

    private void notificaVincitore() throws IOException, InterruptedException, TransformerException, ParserConfigurationException
    {
        clientHandler vincitore = getVincitore();
        // Dato un giocatore g1 a cui tocca, scorro tutti i giocatori
        for (clientHandler g : Server.giocatori) 
        {
            if(g != vincitore)
                Server.notificaUnicast(g, "Winner_", vincitore.getUsername()); // Se g2 non corrisponde g1, gli dico che non è il suo turno, inviandoli il nome del giocatore a cui tocca
            else
                Server.notificaUnicast(g, "Winner_", "Yours");; // Altrimenti gli dico che è il suo turno (Yours)
        }
    }

    // Serve ad aggiornare tutti i giocatori sulla carta giocata da uno di essi, durante un giro
    private void cartaGiocata(Carta c) throws IOException, TransformerException, ParserConfigurationException
    {
        List<Carta> temp = new ArrayList<Carta>(); temp.add(c); // Per poter riciclare il metodo di serializzazione di più carte, inserisco la carta in una lista

        for (clientHandler g : Server.giocatori) 
            Server.invia(g, XMLserializer.stringfyNoIndent(XMLserializer.serializzaLista(temp)));
    }

    // TO DO: inviare ad ognuno il proprio punteggio 
    private void notificaVincitoreGiro() throws IOException, InterruptedException
    {
        int indiceVincitore = getVincitoreGiro();
        int presa = getValorePresa();
        clientHandler vincitore = Server.giocatori.get(indiceVincitore);
        int nuovoPunteggio = vincitore.getPunteggio() + presa;
        vincitore.setPunteggio(nuovoPunteggio);

        for (clientHandler g : Server.giocatori) {
            if(g != vincitore)
                Server.notificaUnicast(g, "Winner", vincitore.getUsername());
            else
                Server.notificaUnicast(g, "Winner", "Yours");
        }
    }

    // 
    private void notificaPunteggio() throws IOException, InterruptedException
    {        
        for (clientHandler g : Server.giocatori) {
            Server.notificaUnicast(g, "Score", Integer.toString(g.getPunteggio()));
        }
    }

    // Invia una lista di carte (la mano) ad un giocatore
    private void inviaMano(clientHandler g, List<Carta> mano) throws IOException, TransformerException, ParserConfigurationException
    {
        Server.invia(g, XMLserializer.stringfyNoIndent(XMLserializer.serializzaLista(mano)));
    }

    public static void inviaCarta(clientHandler g) throws IOException, TransformerException, ParserConfigurationException
    {
        if(mazzo.size() > 0)
        {
            // Pesco una carta e la invio al giocatore
            Carta c = mazzo.get(mazzo.size() - 1); mazzo.remove(mazzo.size() - 1); List<Carta> temp = new ArrayList<Carta>(); temp.add(c);
            Server.invia(g, XMLserializer.stringfyNoIndent(XMLserializer.serializzaLista(temp)));
        }
        else if(!briscolaPescata)
        {
            // Pesco una carta e la invio al giocatore
            List<Carta> temp = new ArrayList<Carta>(); temp.add(briscola); briscolaPescata = true;
            Server.invia(g, XMLserializer.stringfyNoIndent(XMLserializer.serializzaLista(temp)));
        }
        else
            Server.invia(g, "");
    }

    // Invia la briscola ad un dato giocatore
    private void inviaBriscola(clientHandler g) throws IOException, TransformerException, ParserConfigurationException
    {
        List<Carta> temp = new ArrayList<Carta>(); temp.add(briscola);
        Server.invia(g, XMLserializer.stringfyNoIndent(XMLserializer.serializzaLista(temp)));
    }

    /* Elaborazione */
    private int getVincitoreGiro() {

        Carta cartaVincitrice = carteGiocate.get(0); // Inizio il confronto per determinare la carta vincitrice a partire dalla prima

        String semeDiMano = cartaVincitrice.getSeme(); // Il seme che ha più priorità dopo quello della briscola è quello della prima carta giocata
        String semeBriscola = briscola.getSeme();
        
        int indiceVincitore = 0; // Indice della carta vincitrice che corrisponde all'indice del giocatore che l'ha giocata

        for (int i = 1; i < carteGiocate.size(); i++) 
        {
            Carta nuovaCarta = carteGiocate.get(i);
            if (nuovaCarta.miglioreDi(cartaVincitrice, semeBriscola, semeDiMano)) // Se la nuova carta rispetta le condizioni per essere la nuova carta vincitrice
            {
                // Aggiorno opportunamente i dati
                cartaVincitrice = nuovaCarta;
                indiceVincitore = i;
            }
        }
        return indiceVincitore;
    }

    private int getValorePresa() {
        int presa = 0;
        for (Carta c : carteGiocate) {
            presa += c.getValore();
        }
        return presa;
    }

    private clientHandler getVincitore() {
        clientHandler vincitore = Server.giocatori.get(0);
    
        for(clientHandler clientHandler : Server.giocatori) {
            if(clientHandler.getPunteggio() > vincitore.getPunteggio()) {
                vincitore = clientHandler;
            }
        }
    
        return vincitore;
    }

    private boolean finePartita() {

        for(clientHandler clientHandler : Server.giocatori) {
            if(!clientHandler.carteFinite) { // Se un giocatore ha ancora carte, la partita non è finita
                return false;
            }
        }

        return true;
    }

    // Generazione mazzo
    private List<Carta> generaMazzo() {

        String[] semi = {"bastoni", "coppe", "denari", "spade"}; // Semi napoletani
        char[] numeri = {'A', '2', '3', '4', '5', '6', '7', 'F', 'C', 'R'}; // 1=asso, 8=fante, 9=cavallo, 10=re
        int[] valori = {11, 0, 10, 0, 0, 0, 0, 2, 3, 4}; // Valori di presa

        List<Carta> mazzo = new ArrayList<>();

        for (String seme : semi) {
            for (int i = 0; i < numeri.length; i++) {
                char numero = numeri[i];
                int valore = valori[i];
                String img_path = numero + "-" + seme + ".jpg";

                Carta carta = new Carta(seme, numero, valore, img_path);
                mazzo.add(carta);
            }
        }

        // Se volessi salvarlo su file:
        // XMLserializer.saveLista("./Server(Java)/src/Mazzo.xml", mazzo);

        return mazzo;
    }
}
