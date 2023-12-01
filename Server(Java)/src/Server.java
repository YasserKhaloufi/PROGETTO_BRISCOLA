import java.io.BufferedReader;
import java.io.DataOutputStream;
// Comunicazione TCP
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

// Liste
import java.util.ArrayList;
import java.util.List;

// XML parsing
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * Waits for players to connect and starts the game when requested.
 * <p>
 * <ul>
 * <li> Provides various methods for communication with the clients. </li>
 * <li> Uses TCP sockets for communication with the clients. </li>
 * <li> Acts as a shared class among the different threads during the game phase. </li>
 * </ul>
 * 
 * @author Yasser Khaloufi
 * @version 1.0
 */
public class Server {    

    /**
     * List of client handlers representing connected players.<br>
     * This list is shared with threadPartita.<br>
     */
    public static List<clientHandler> giocatori =  new ArrayList<clientHandler>(); // Predispongo una lista per memorizzare i giocatori connessi, verrà anche utilizzata dal threadPartita

    
    /**
     * Indicates whether the game has started or not.
     * It is shared among the client handlers.
     */
    public static boolean gameStarted = false; // Sentinella partita iniziata (condivisa tra i clientHandler)
    /**
     * Indicates whether the game has ended or not.
     * It is shared among the client handlers.
     */
    public static boolean endGame = false; // Sentinella partita iniziata (condivisa tra i clientHandler)
    
    /**
     * It is the entry point of the server application.
     * <p>
     * <ol>
     * <li>It creates a server socket and listens for client connections.</li>
     * <li>If the game has not started, it waits for players to join.</li>
     * <li>Once the game starts, it creates and starts a thread to handle the game.</li>
     * <li>It waits for the game thread to finish before proceeding.</li>
     * <li>Finally, it closes the server socket and terminates the game.</li>
     * </ol>
     *
     * @throws IOException                  if an I/O error occurs when creating the server socket.
     * @throws TransformerException         if an error occurs during XML transformation.
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created.
     * @throws SAXException                 if any parse errors occur.
     * @throws InterruptedException         if any thread has interrupted the current thread.
     */
    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException, SAXException, InterruptedException
    {
        
        /**
         * The server socket used to listen for client connections.
         */
        ServerSocket serverSocket = new ServerSocket(Settings.porta); // Creo la socket sulla quale il server ascolterà le connessioni dei client
        System.out.println("Server in esecuzione...\n"); // Debug

        while(!endGame){ 
            try 
            {

                    if(!gameStarted) // Se la partita non è ancora iniziata, aspetto che si connettano altri giocatori
                    {
                        // Fase di attesa giocatori    
                        cercaGiocatori(serverSocket); System.out.println("In attesa di giocatori...\n"); // Debug
                    }
                    else // Altrimenti, se la partita è iniziata, passo il controllo al threadPartita
                    {
                        // Fase di gioco

                        // Creo e avvio un thread per gestire la partita; dopodicchè aspetto che termini
                        threadPartita partita = new threadPartita(); partita.start(); partita.join(); System.out.println("Partita iniziata\n"); // Debug
                    }                
            } 
            catch (Exception e) 
            {
                System.out.println("Connessione interrotta\n"); // Debug
            }
        }
        
        System.out.println("Partita terminata\n"); // Debug

        serverSocket.close(); // Chiudo la socket del server
    }
    
    
    /** 
     * Keeps waiting for new players, until one of the connected players starts the game.
     * <p>
     * A listening timeout is set on the server socket, so that it can periodically check if the game has started.
     * <ol>
     * <li>When a new player connects, it creates a new client handler thread to handle the communication with the client.</li>
     * <li>It adds the client handler to the list of connected players.</li>
     * <li>It notifies all the connected players that a new player has joined the game.</li>
     * </ol>
     * 
     * @param serverSocket the server socket used to listen for client connections.
     * @throws IOException 
     * @throws ParserConfigurationException 
     * @throws SAXException if any parse errors occur, while parsing the XML message sent by a client.
     */
    private static void cercaGiocatori(ServerSocket serverSocket) throws IOException, ParserConfigurationException, SAXException
    {
        while(!gameStarted){ // Finchè la partita non è iniziata, attendo nuovi giocatori
            try {
                serverSocket.setSoTimeout(Settings.timeOut); // Imposto un timeout per l'ascolto sulla socket, per poter periodicamente controllare se la partita è iniziata
                
                String ricevuto; // Memorizza il messaggio ricevuto dal client
                
                Socket connectionSocket = serverSocket.accept(); // Aspetto finchè un client si connette
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); // Creo  il flusso di ricezione
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); // Creo il flusso di invio

                clientHandler g = new clientHandler(connectionSocket, inFromClient, outToClient); // Il client è nuovo giocatore, creo un thread separato per ascoltare i suoi messaggi
                ricevuto = ricevi(g); // Aspetto di ricevere un messaggio dal client che si è connesso 
                String username = XMLserializer.getArgomento(ricevuto); g.setUsername(username); // Prima di aggiungerlo ai giocatori connessi, ne estrapolo il nome utente assegnandoglielo

                giocatori.add(g); // aggiungo il client alla lista di giocatori connessi per poterlo ricontattare in futuro
                g.start(); // Avvio il thread per ascoltare i messaggi del client
                notificaNumeroGiocatori(); // Comunico a tutti i client già connessi che un nuovo giocatore si è unito alla partita
                                                        
                System.out.println(username +" si è unito\n"); // Debug
            } catch (Exception e) {
                continue;
            }
        }     
    }
    
    /* UTILIZZATI DA CLIENTHANDLER */

    /**
     * Notifies all connected players that the game has started.
     * <p>
     * It sends a message indicating the start of the game to all connected players using {@link #notificaBroadcast(String, String)}.
     * Uses "Start" as notification type and an empty message.
     * 
     * @throws IOException if an I/O error occurs while sending the notification.
     */
    public static void notificaInizioPartita() throws IOException
    {
        notificaBroadcast("Start", "");
    }

    /**
     * Notifies all connected players that a player has joined or left the game.
     * <p>
     * It sends a message indicating the number of connected players to all connected players using {@link #notificaBroadcast(String, String)},
     * so that they can update their user interface (the waiting window).
     * Uses "numeroGiocatori" as notification type and the number of connected players as message.
     * 
     * @throws IOException if an I/O error occurs while sending the notification
     */
    public static void notificaNumeroGiocatori() throws IOException
    {
        notificaBroadcast("NumeroGiocatori", Integer.toString(giocatori.size()));
    }

    /* COMUNICAZIONE GENERICA */

    /**
     * Sends an XML formatted message to all connected clients.
     * <p>
     * The message is wrapped as happens in {@link #notificaUnicast(clientHandler, String, String)}, but it is sent to all connected clients.
     * It does not wait for an acknoledgement from the clients.
     * 
     * @param notifica  the type of notification.
     * @param messaggio the message to be sent.
     * @throws IOException if an I/O error occurs while sending the notification.
     */
    public static void notificaBroadcast(String notifica, String messaggio) throws IOException
    {
        for (clientHandler g : giocatori) 
            invia(g, "<" + notifica + ">" + messaggio + "</" +  notifica + ">");
    }

    /**
     * Sends an XML formatted message to a specific client and waits for an acknoledgement.
     * <p>
     * <ol>
     * <li>The given message is wrapped in a given XML tag representing the notification type;</li>
     * <li>The message is then sent to the client using {@link #invia(clientHandler, String)};</li>
     * <li>Finally, it waits for the client to send an acknoledgement, by checking its response buffer.</li>
     * </ol>
     * 
     * @param g the clientHandler object representing the client to send the notification to.
     * @param notifica the type of notification.
     * @param messaggio the message to be sent.
     * @throws IOException if an I/O error occurs while sending the notification.
     * @throws InterruptedException if the thread is interrupted while waiting for the client's response.
     */
    public static void notificaUnicast(clientHandler g, String notifica, String messaggio) throws IOException, InterruptedException
    {
        invia(g, "<" + notifica + ">" + messaggio + "</" +  notifica + ">"); // Invio il messaggio incapsulato nel tag XML di notifica
        g.risposte.take(); // Aspetto che il client risponda, aspettando
    }

    /**
     * Sends a given message to the client associated with the given client handler.
     * <p>
     * <ul>
     * <li>Sends a message to a client with whose an output stream was previously estabilished.</li>
     * <li>It adds a new line character at the end of the given message so the client can properly read it.</li>
     * </ul>
     * 
     * @param g the clientHandler object representing the client to which the message has to be sent.
     * @param messaggio the message to be sent.
     * @throws IOException if an I/O error occurs while sending the message.
     */
    public static void invia(clientHandler g, String messaggio) throws IOException
    {
        g.outToClient.writeBytes(messaggio + "\n"); // \n Perchè se non viene rilevato un new line il messaggio non viene "raccolto"
    }

    /**
     * Receives a string from the client associated with the given client handler.
     * <p>
     * <ul>
     * <li>Receives a message from a client with whose an input stream was previously estabilished.</li>
     * <li>It reads the input stream until it encounters a new line character ('\n').</li>
     * <li>It is static so that it can be also used by other threads.</li>
     * </ul>
     *
     * @param g the client handler object representing the client from which the string has to be received.
     * @return the received string.
     * @throws IOException if an I/O error occurs while reading from the client.
     */
    public static String ricevi(clientHandler g) throws IOException
    {
        return g.inFromClient.readLine(); // Leggo il flusso in entrata finchè non incontro un new line ('\n')
    }
}
