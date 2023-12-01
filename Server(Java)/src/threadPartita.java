import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


/**
 * A thread for managing the game.
 * <p>
 * It handles the game elements, such as the deck of cards, played cards buffer, and the briscola.
 * The thread is responsible for distributing cards to players, managing turns, determining the winner of each round,
 * updating the score of each player and finally determining the winner of the game when the deck is empty.
 */
public class threadPartita extends Thread {

    // Elementi di gioco

    /**
     * The list of cards in the deck.
     */
    private static List<Carta> mazzo;

    /**
     * The list of cards played in a round (since the players can be 2 or 4, the list can contain 2 or 4 cards).
     */
    private List <Carta> carteGiocate; // E' un buffer di n carte, dove n è il numero di giocatori (tiene conto delle carte giocate in un giro)

    /**
     * The briscola card.
     */
    private static Carta briscola; // Viene scelta all'inizio della partita

    /**
     * A flag that indicates if the briscola has been drawn from the deck (it's the last card to be drawed).
     */
    private static boolean briscolaPescata = false;

    /**
     * Craetes a new game manager thread.
     * <p>
     * It initializes the deck of cards.
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public threadPartita() throws SAXException, IOException, ParserConfigurationException {        
        mazzo = generaMazzo(); 
        
        carteGiocate = new ArrayList<Carta>();
        briscola = new Carta();
    }

    /**
     * Starts the game.
     * <p>
     * It performs the following operations:
     * <ul>
     * <li>Shuffles the deck and draws the briscola.</li>
     * <li>Distributes the cards to the players.</li>
     * <li>Comunicates to the players wether it's their turn or not, using the {@link #toccaA(clientHandler)} method.</li>
     * <li>Let's the player whose turn it is to play a card, picks its choice from the response buffer of the client handler.</li>
     * <li>Notifies the other players of the card played, using the {@link #cartaGiocata(Carta)} method.</li>
     * <li>When all the players have played a card, it notifies all the players the winner of the round, using {@link #notificaVincitoreGiro()}.</li>
     * <li>It updates the score of the winner of the round, using {@link #notificaPunteggio()}.</li>
     * <li>When the the game ends, it notifies all the players the winner of the game, using {@link #notificaVincitore()}.</li>
     * 
     */
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

    /**
     * Shuffles the deck and draws the briscola.
     */
    private void preparativi()
    {
        Collections.shuffle(mazzo); // Mischio il mazzo

        // Estraggo la briscola dal mazzo (l'ultima carta)
        briscola = mazzo.get(mazzo.size() - 1);
        mazzo.remove(mazzo.size() - 1);
    }

    // Distribuisce a tutti i giocatori le carte e gli informa della briscola

    /**
     * Distributes the cards to the players and informs them of the briscola.
     * <p>
     * Uses for each player the {@link #inviaBriscola(clientHandler)} and {@link #inviaMano(clientHandler, List)} methods,
     * after both of which it waits for an ACK from the client.
     */
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

    /**
     * Informs the players wether it's their turn or not.
     * <p>
     * It sends the command "Turn" to all the players, encapsuling the name of the player whose turn it is.
     * Uses {@link server#notificaUnicast(clientHandler, String, String)} to send the notification to each player.
     * @param g1 The player whose turn it is.
     */
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

    /**
     * Notifies the winner of the game to all the players.
     * <p>
     * It sends the command "Winner" to all the players, encapsuling the name of the winner.
     * Uses {@link server#notificaUnicast(clientHandler, String, String)} to send the notification to each player.
     * 
     */
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

    /**
     * Notifies all the players of the card played by a player during a round.
     * <p>
     * It sends the command "Card" to all the players, encapsuling the card played.
     * Uses {@link server#invia(clientHandler, String)} to send the notification to each player.
     * @param c The card played.
     */
    private void cartaGiocata(Carta c) throws IOException, TransformerException, ParserConfigurationException
    {
        List<Carta> temp = new ArrayList<Carta>(); temp.add(c); // Per poter riciclare il metodo di serializzazione di più carte, inserisco la carta in una lista

        for (clientHandler g : Server.giocatori) 
            Server.invia(g, XMLserializer.stringfyNoIndent(XMLserializer.serializzaLista(temp)));
    }

    // TO DO: inviare ad ognuno il proprio punteggio 

    /**
     * Notifies all the players of the score of each player.
     * <p>
     * It sends the command "Winner" to all the players, encapsuling the username of the round winner.
     * Uses {@link server#notificaUnicast(clientHandler, String, String)} to send the notification to each player.
     */
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

    /**
     * Notifies all the players of the score of each player.
     * <p>
     * It sends the command "Score" to all the players, encapsuling the score of each player.
     * Uses {@link server#notificaUnicast(clientHandler, String, String)} to send the notification to each player.
     */
    private void notificaPunteggio() throws IOException, InterruptedException
    {        
        for (clientHandler g : Server.giocatori) {
            Server.notificaUnicast(g, "Score", Integer.toString(g.getPunteggio()));
        }
    }

    /**
     * Sends a list of cards to a player.
     * <p>
     * It sends the command "Hand" to the player, encapsuling the list of cards.
     * Uses {@link XMLserializer#serializzaLista(List)} to serialize the list of cards and stringfies it using {@link XMLserializer#stringfyNoIndent(String)},
     * then sends it to the given player using {@link server#invia(clientHandler, String)}.
     * 
     * 
     * @param g The player to whom the cards are sent.
     * @param mano The list of cards to send.
     */
    private void inviaMano(clientHandler g, List<Carta> mano) throws IOException, TransformerException, ParserConfigurationException
    {
        Server.invia(g, XMLserializer.stringfyNoIndent(XMLserializer.serializzaLista(mano)));
    }

    /**
     * Sends a card to a player, its purpose is to allow the player to draw a card from the deck.
     * <p>
     * It sends the command "Card" to the player, encapsuling the stringfied serialized card.
     * If the deck is empty, it lets the player draw the briscola, if both the deck and the briscola have been drawn, it sends an empty string,
     * letting the player know that there are no more cards to draw.
     * 
     * @param g The player to whom the card is sent.
     * @param c The card to send.
     */
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

    /**
     * Sends the briscola to a player.
     * 
     * @param g The player to whom the briscola is sent.
     */
    private void inviaBriscola(clientHandler g) throws IOException, TransformerException, ParserConfigurationException
    {
        List<Carta> temp = new ArrayList<Carta>(); temp.add(briscola);
        Server.invia(g, XMLserializer.stringfyNoIndent(XMLserializer.serializzaLista(temp)));
    }

    /**
     * Determines the winner of the round based on the cards played.
     * <p>
     * It compares the cards played by the players, using the {@link Carta#miglioreDi(Carta, String, String)} method.
     * 
     * @return The index of the winning card in the played cards list.
     */
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

    /**
     * Calculates the score of the round based on the cards played.
     * <p>
     * It sums the value of each card played, which can be found in carteGiocate.
     * 
     * @return The score of the round.
     */
    private int getValorePresa() {
        int presa = 0;
        for (Carta c : carteGiocate) {
            presa += c.getValore();
        }
        return presa;
    }

    /**
     * Determines the winner of the game based on the score of each player at the end of the game.
     * <p>
     * It compares the score of each clientHandler, which represent the players.
     * 
     * @return The clientHandler corresponding to the player with the highest score.
     */
    private clientHandler getVincitore() {
        clientHandler vincitore = Server.giocatori.get(0);
    
        for(clientHandler clientHandler : Server.giocatori) {
            if(clientHandler.getPunteggio() > vincitore.getPunteggio()) {
                vincitore = clientHandler;
            }
        }
    
        return vincitore;
    }

    /**
     * Determines if the game is over.
     * <p>
     * It checks if all the players have no more cards.
     * 
     * @return True if the game is over, false otherwise.
     */
    private boolean finePartita() {

        for(clientHandler clientHandler : Server.giocatori) {
            if(!clientHandler.carteFinite) { // Se un giocatore ha ancora carte, la partita non è finita
                return false;
            }
        }

        return true;
    }

    /**
     * Generates the deck of cards based on the neapolitan settings.
     *
     * @return The deck of cards.
     */
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
