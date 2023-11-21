import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Server {    

    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException, SAXException
    {
        // Elementi di gioco
        List<Giocatore> giocatori =  new ArrayList<Giocatore>(); // Da 2 a 4 giocatori 

        // Sentinelle
        boolean spegni = false; // Sentinella di spegnimento server
        boolean gameStarted = false; // Sentinella partita iniziata
        
        // Codice per generare il mazzo in formato XML
        {
        /*String[] semi = {"bastoni", "coppe", "denari", "spade"}; // Semi napoletani
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
        XMLserializer.saveLista("./Server(Java)/src/Mazzo.xml", mazzo);*/}

        // Elementi di comunicazione
        ServerSocket serverSocket = new ServerSocket(Settings.porta); System.out.println("Server in esecuzione...");

        String ricevuto;     // Stringa ricevuta, ricavata a partire dal contenuto del buffer in ricezione
        String comando = ""; // Da ricavare a partire dal soprastante

        try 
        {
            while (!spegni) 
            {
                if(!gameStarted) // FASE DI RICERCA GIOCATORI
                {    
                    Socket connectionSocket = serverSocket.accept(); // Aspetto finchè un client si connette
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); //  Creo  il flusso di ricezione
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); // Creo  il flusso di invio

                    ricevuto = ricevi(inFromClient); // Aspetto di ricevere un messaggio dal client che si è connesso 
                    System.out.println("Ricevuto: " + ricevuto); // Per debug
                    comando = XMLserializer.getComando(ricevuto); // Parsing messaggio ricevuto, ricavando il comando

                    // SPECIFICA QUI QUALE SARA' IL COMPORTAMENTO DEL SERVER IN BASE AL COMANDO RICEVUTO
                    /* TO DO: gestire il caso in cui il client decida di disconnettersi (il server riceve un apposito comando e identifica 
                       il client da rimuovere dalla lista di giocatori connessi, attuando un procedimento simile alla parte iniziale di Connect) 
                       cercandolo all'interno della stessa...
                    */
                    switch (comando) {

                        default:
                            break;
                        
                        case "Connect": // Ogni volta che si connette un nuovo client invio a tutti i giocatori connessi il numero di giocatori attualmente connessi
                            
                            Giocatore g = new Giocatore(connectionSocket, inFromClient, outToClient); // Il client è nuovo giocatore
                            String username = XMLserializer.getUsername(ricevuto); g.setUsername(username);

                            giocatori.add(g); // aggiungo il client alla lista di giocatori connessi per poterlo ricontattare in futuro
                            notificaGiocatori(giocatori, "Joined"); // Comunico a tutti i client che un nuovo giocatore si è unito alla partita
                                                   
                            // TO DO: inviare al client che si è connesso un feedback (per debug)
                            System.out.println("Giocatore " + username +" inzializzato con successo"); // Debug

                            break;
                        
                        case "Start":
                            gameStarted = true;
                            notificaGiocatori(giocatori, "Start"); // Comunico a tutti i client che la partita è iniziata
                            
                            System.out.println("Partita iniziata"); // Debug
                            break;
                        
                        case "Exit":
                            spegni = true;
                            break;
                    }

                    // INVIO
                    /*if(inviaRisposta)
                        invia(outToClient, risposta);*/
                }
                else // FASE DI GIOCO
                {
                    Partita partita = new Partita(giocatori);
                    partita.start();
                    partita.join();
                }
            }
        } 
        catch (Exception e) 
        {
            System.out.println("Connessione interrotta");
        }
        
        serverSocket.close();
    }

    //
    public static void connect()
    {
        
    }

    // Invia un messaggio a un client con il quale è stato instaurato un flusso di invio
    public static void invia(DataOutputStream outToClient, String messaggio) throws IOException
    {
        outToClient.writeBytes(messaggio + "\n"); // \n Perchè se non viene rilevato un new line il messaggio non viene "raccolto"
    }

    // Riceve un messaggio da un client con il quale è stato instaurato un flusso di ricezione
    public static String ricevi(BufferedReader inFromClient) throws IOException
    {
        return inFromClient.readLine();
    }

    // Avvisa tutti i giocatori connessi di un evento
    public static void notificaGiocatori(List<Giocatore> giocatori, String notifica) throws IOException
    {
        for (Giocatore g : giocatori) 
            invia(g.outToClient, "<" + notifica + ">" + giocatori.size() + "</" +  notifica + ">");
    }
}
