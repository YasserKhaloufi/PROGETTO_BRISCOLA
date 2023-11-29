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

/* 
 * Si occupa di attendere giocatori ed iniziare la partita quando richiesto;
 * mette a disposizione vari metodi per la comunicazione con i client.
 * Durante la fase di gioco è una sorta di "classe condivisa" fra i vari thread.
 */
public class Server {    

    public static List<clientHandler> giocatori =  new ArrayList<clientHandler>(); // Predispongo una lista per memorizzare i giocatori connessi, verrà anche utilizzata dal threadPartita

    /* Sentinelle */ 
    public static boolean gameStarted = false; // Sentinella partita iniziata (condivisa tra i clientHandler)
    public static boolean endGame = false; // Sentinella partita iniziata (condivisa tra i clientHandler)
    
    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException, SAXException, InterruptedException
    {
        ServerSocket serverSocket = new ServerSocket(Settings.porta); // Creo la socket sulla quale il server ascolterà le connessioni dei client
        System.out.println("Server in esecuzione...\n"); // Debug

        while(!endGame){
            try 
            {

                    if(!gameStarted)
                    {
                        // Fase di attesa giocatori    
                        System.out.println("In attesa di giocatori...\n"); // Debug
                        cercaGiocatori(serverSocket);
                    }
                    else
                    {
                        // Fase di gioco
                        System.out.println("Partita iniziata\n"); // Debug

                        // Creo e avvio un thread per gestire la partita
                        threadPartita partita = new threadPartita(); partita.start();
                    
                        partita.join(); // Aspetto che il thread termini
                    }
                    //System.out.println("esecuzione in corso");
                
            } 
            catch (Exception e) 
            {
                System.out.println("Connessione interrotta\n");
            }
        }
        
        System.out.println("Partita terminata\n");

        serverSocket.close();
    }
    
    // TO DO: spostare i seguenti metodi in una classe statica adibita, in modo da sintetizzare il codice del main

    // Per fase du attesa giocatori
    private static void cercaGiocatori(ServerSocket serverSocket) throws IOException, ParserConfigurationException, SAXException
    {
        while(!gameStarted){
            try {
                serverSocket.setSoTimeout(1000);
                String ricevuto; // Predispongo la variabile per memorizzare, ricavata a partire dal contenuto del buffer in ricezione
                
                Socket connectionSocket = serverSocket.accept(); // Aspetto finchè un client si connette
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); //  Creo  il flusso di ricezione
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); // Creo  il flusso di invio

                clientHandler g = new clientHandler(connectionSocket, inFromClient, outToClient); // Il client è nuovo giocatore, creo un thread separato per ascoltare i suoi messaggi
                ricevuto = ricevi(g); // Aspetto di ricevere un messaggio dal client che si è connesso 
                String username = XMLserializer.getUsername(ricevuto); g.setUsername(username); // Prima di aggiungerlo ai giocatori connessi, ne estrapolo il nome utente assegnandoglielo

                giocatori.add(g); // aggiungo il client alla lista di giocatori connessi per poterlo ricontattare in futuro
                g.start(); // Avvio il thread per ascoltare i messaggi del client
                notificaNumeroGiocatori(); // Comunico a tutti i client già connessi che un nuovo giocatore si è unito alla partita
                                                        
                // TO DO: inviare al client che si è connesso un feedback (per debug)
                System.out.println(username +" si è unito\n"); // Debug
            } catch (Exception e) {
                continue;
            }
        }     
    }
    
    /* UTILIZZATI DA CLIENTHANDLER */

    // Avvisa tutti i giocatori connessi che la partita è iniziata
    public static void notificaInizioPartita() throws IOException
    {
        notificaBroadcast("Start", "");
    }

    // Avvisa tutti i giocatori connessi di una disconnessione/connesione, aggiornandoli sul numero di giocatori connessi
    public static void notificaNumeroGiocatori() throws IOException
    {
        notificaBroadcast("NumeroGiocatori", Integer.toString(giocatori.size()));
    }

    /* COMUNICAZIONE GENERICA */

    // Invia un informazione formato XML a tutti i client connessi
    public static void notificaBroadcast(String notifica, String messaggio) throws IOException
    {
        for (clientHandler g : giocatori) 
            invia(g, "<" + notifica + ">" + messaggio + "</" +  notifica + ">");
    }

    public static void notificaUnicast(clientHandler g, String notifica, String messaggio) throws IOException, InterruptedException
    {
        invia(g, "<" + notifica + ">" + messaggio + "</" +  notifica + ">");
        g.risposte.take(); // Aspetto che il client risponda
    }

    // Invia un messaggio a un client con il quale è stato instaurato un flusso di invio
    public static void invia(clientHandler g, String messaggio) throws IOException
    {
        g.outToClient.writeBytes(messaggio + "\n"); // \n Perchè se non viene rilevato un new line il messaggio non viene "raccolto"
    }

    // Riceve un messaggio da un client con il quale è stato instaurato un flusso di ricezione
    public static String ricevi(clientHandler g) throws IOException
    {
        return g.inFromClient.readLine();
    }
}
