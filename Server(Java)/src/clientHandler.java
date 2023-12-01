import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

// Per ricevere dal e inviare messaggi al client

/**
 * Handles the communication with a client.
 * <p>
 * Depending on the command received from the client, it performs different actions.
 * It manages the client's socket connection, input and output streams and eventually closes them.
 */
public class clientHandler extends Thread{

    // Salvo le seguenti informazioni per ogni client, in modo tale da poter rilasciare le risorse impiegate quando server

    /**
     * The socket representing the connection between the client and the server.
     * <p>
     * It is private because it is not necessary to access it from outside the class.
     */
    private Socket connectionSocket;

    /**
     * The input strea from the client.
     */
    public BufferedReader inFromClient;

    /**
     * The output stream to the client.
     */
    public DataOutputStream outToClient;

    /**
     * The queue of messages received from the client.
     * <p>
     * It is primarly used to receive acks from the client.
     */
    public BlockingQueue<String> risposte;

    /**
     * The username of the client.
     */
    private String username;
    
    /**
     * The score of the player represented by this client handler.
     */
    private int punteggio;

    /**
     * A flag that indicates if the client has no more cards.
     */
    public boolean carteFinite = false; // Sentinella carte finite

    /**
     * The thread that manages the communication with a client, which is a player.
     * <p>
     * It is a thread because it needs to be able to receive messages from the client at any time, without blocking the execution of the server.
     * A new clientHandler is started by the main module of the server for each client that connects to it.
     * It keeps listening for messages from the client until it disconnects and, depending on the command received, it performs different actions.
     * <p>
     * The possible commands are:
     * <ul>
     * <li>Start: the client has started the game. The server notifies all the clients that the game has started, by calling {@link Server#notificaInizioPartita()}.</li>
     * <li>Number: the client has sent the number of cards it has. The server saves this information.</li>
     * <li>Carta: the client has played a card. The server saves this information, which will be picked up by the threadPartita.</li> 
     * <li>Draw: the client has requested a card. The clientHandler notifies the threadPartita, which will send a card to the client.</li>
     * <li>ACK: the client has received a message from the server and acknowledged. The server can now send another message to the client.</li>
     * <li>End: the client has no more cards. The server saves this information, so that when all the clients have no more cards, it can end the game.</li>
     * <li>Disconnect: the client has disconnected. The thread closes the connection with the client and removes it from the list of connected players,<br>
     *     and notifies the other players, by calling {@link Server#notificaNumeroGiocatori()}.</li>
     * </ul>
     */
    @Override
    public void run() {

        String ricevuto = ""; // Stringa ricevuta, ricavata a partire dal contenuto del buffer in ricezione
        String comando = ""; // Da ricavare dal messaggio ricevuto
        Boolean disconnesso = false;

        while (!disconnesso)  // finchè non ricevo un comando di disconnessione dal client, continuo ad ascoltare
        {
            // Continuo ad ascoltare il client
            try 
            {
                if (inFromClient.ready()) // Se il client ha inviato un messaggio
                {
                    ricevuto = inFromClient.readLine(); 
                    comando = XMLserializer.getComando(ricevuto); // Parsing messaggio ricevuto, ricavando il comando
                    
                    // SPECIFICA QUI QUALE SARA' IL COMPORTAMENTO DEL SERVER IN BASE AL COMANDO RICEVUTO
                    /* TO DO: gestire il caso in cui il client decida di disconnettersi (il server riceve un apposito comando e identifica 
                       il client da rimuovere dalla lista di giocatori connessi, attuando un procedimento simile alla parte iniziale di Connect) 
                       cercandolo all'interno della stessa...
                    */
                    switch (comando) {
                        
                        case "Start":
                            Server.notificaInizioPartita(); // Comunico a tutti i client che la partita è iniziata
                            Server.gameStarted = true;
                            System.out.println(username + " ha iniziato la partita\n"); // Debug
                            break;

                        case "Number":
                            String n = XMLserializer.getArgomento(ricevuto);
                            risposte.put(n); // Inserisco il messaggio nella coda di risposte
                            System.out.println(username + " ha" + n + " carte\n"); // Debug
                            break;

                        case "Carta":
                            risposte.put(ricevuto); 
                            break;

                        case "Draw":
                            threadPartita.inviaCarta(this); // Chiedo una carta per questo client
                            break;

                        /* Se non aspettassi un ack (feedback) da parte del client dopo averli inviato un messaggio, è probabile
                        * che il server invvi messaggi diversi troppo velocemente, accavallando le info., impedendo di parsare correttamente le singole informazioni.
                        * Perciò faccio si che il client restituisca un ack ogni volta che gli viene inviata un informazione, 
                        * per notificare al server che questi ha ricevuto e parsato correttamente l'informazioni, quindi di essere pronto ad una nuova ricezione.
                        */
                        case "ACK":
                            risposte.put("ACK");
                            System.out.println(username + " ha ricevuto\n"); // Debug
                            break;

                        case "End":
                            carteFinite = true;
                            break;

                        case "Disconnect":
                            abbattiConnessione(); // Chiudo la connessione con il client
                            disconnesso = true;
                            Server.giocatori.remove(this);

                            if(Server.giocatori.size() > 0)
                                Server.notificaNumeroGiocatori(); // Mi disconnetto, quindi aggiorno gli altri giocatori sul numero di giocatori connessi
    
                            System.out.println(username + " si è disconnesso\n"); // Debug
                            break;
                    }
                    
                }
            } 
            catch (IOException | SAXException | ParserConfigurationException | InterruptedException | TransformerException e) 
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a new client handler, which represents a player.
     * <p>
     * It is used by the server to manage the communication with a client.
     * It saves the socket connection, the input and output streams and creates a queue of messages received from the client.
     * 
     * @param connectionSocket the socket representing the connection between the client and the server
     * @param inFromClient the input stream from the client
     * @param outToClient the output stream to the client
     * @throws IOException if an I/O error occurs when creating the input and output streams
     */
    public clientHandler(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient) throws IOException {
        this.connectionSocket = connectionSocket;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        this.risposte = new LinkedBlockingQueue<>();
        punteggio = 0;
    }


    /**
     * Closes the connection with the client.
     * This method closes the connection socket, input stream, and output stream.
     * @throws IOException if an I/O error occurs while closing the connection.
     */
    public void abbattiConnessione() throws IOException {
        connectionSocket.close();
        inFromClient.close();
        outToClient.close();
    }

    // Get e set vari
    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public BufferedReader getInFromClient() {
        return inFromClient;
    }

    public DataOutputStream getOutToClient() {
        return outToClient;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }
}
