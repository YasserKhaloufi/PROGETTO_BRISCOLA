import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

// Per ricevere dal e inviare messaggi al client
public class clientHandler extends Thread{

    // Salvo le seguenti informazioni per ogni client, in modo tale da poter rilasciare le risorse impiegate quando server
    private Socket connectionSocket;
    public BufferedReader inFromClient;
    public DataOutputStream outToClient;

    public BlockingQueue<String> risposte;

    private String username;
    private int punteggio;

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

                        case "Disconnect":
                            abbattiConnessione(); // Chiudo la connessione con il client
                            disconnesso = true;
                            Server.giocatori.remove(this);
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


    public clientHandler(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient) throws IOException {
        this.connectionSocket = connectionSocket;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        this.risposte = new LinkedBlockingQueue<>();
        punteggio = 0;
    }

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
