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

    static List<clientHandler> giocatori =  new ArrayList<clientHandler>(); // Predispongo una lista per memorizzare i giocatori connessi

    /* Sentinelle */
    static boolean spegni = false; // Sentinella di spegnimento server

    // Sentinelle condivise tra i thread Giocatore
    static boolean gameStarted = false; // Sentinella partita iniziata
    
    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException, SAXException
    {
        
        // Codice per generare il mazzo in formato XML
        
        // String[] semi = {"bastoni", "coppe", "denari", "spade"}; // Semi napoletani
        // char[] numeri = {'A', '2', '3', '4', '5', '6', '7', 'F', 'C', 'R'}; // 1=asso, 8=fante, 9=cavallo, 10=re
        // int[] valori = {11, 0, 10, 0, 0, 0, 0, 2, 3, 4}; // Valori di presa

        // List<Carta> mazzo = new ArrayList<>();

        // for (String seme : semi) {
        //     for (int i = 0; i < numeri.length; i++) {
        //         char numero = numeri[i];
        //         int valore = valori[i];
        //         String img_path = numero + "-" + seme + ".jpg";

        //         Carta carta = new Carta(seme, numero, valore, img_path);
        //         mazzo.add(carta);
        //     }
        // }
        // XMLserializer.saveLista("./Server(Java)/src/Mazzo.xml", mazzo);

        ServerSocket serverSocket = new ServerSocket(Settings.porta); System.out.println("Server in esecuzione...\n"); // Creo la socket sulla quale il server ascolterà le connessioni dei client

        try 
        {
            while (!spegni) 
            {
                if(!gameStarted) // FASE DI RICERCA GIOCATORI
                {    
                    System.out.println("In attesa di giocatori...\n"); // Debug
                    cercaGiocatori(serverSocket);
                }
                else // FASE DI GIOCO
                {
                    System.out.println("Partita iniziata\n"); // Debug
                    threadPartita partita = new threadPartita(giocatori); // Creo un thread per gestire la partita
                    partita.start(); // Avvio il thread
                    partita.join(); // Aspetto che il thread termini
                }
                //System.out.println("esecuzione in corso");
            }
        } 
        catch (Exception e) 
        {
            System.out.println("Connessione interrotta\n");
        }
        
        serverSocket.close();
    }
    
    // TO DO: spostare i seguenti metodi in una classe statica adibita, in modo da sintetizzare il codice del main

    public static void cercaGiocatori(ServerSocket serverSocket) throws IOException, ParserConfigurationException, SAXException
    {
        while(!gameStarted){
            try {
                serverSocket.setSoTimeout(1000);
                String ricevuto; // Predispongo la variabile per memorizzare, ricavata a partire dal contenuto del buffer in ricezione
                
                Socket connectionSocket = serverSocket.accept(); // Aspetto finchè un client si connette
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); //  Creo  il flusso di ricezione
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); // Creo  il flusso di invio

                ricevuto = ricevi(inFromClient); // Aspetto di ricevere un messaggio dal client che si è connesso 
                clientHandler g = new clientHandler(connectionSocket, inFromClient, outToClient); // Il client è nuovo giocatore, creo un thread separato per ascoltare i suoi messaggi
                String username = XMLserializer.getUsername(ricevuto); g.setUsername(username); // Prima di aggiungerlo ai giocatori connessi, ne estrapolo il nome utente assegnandoglielo

                giocatori.add(g); // aggiungo il client alla lista di giocatori connessi per poterlo ricontattare in futuro
                g.start(); // Avvio il thread per ascoltare i messaggi del client
                notificaNgiocatori(); // Comunico a tutti i client già connessi che un nuovo giocatore si è unito alla partita
                                                        
                // TO DO: inviare al client che si è connesso un feedback (per debug)
                System.out.println(username +" si è unito\n"); // Debug
            } catch (Exception e) {
                continue;
            }
        }     
    }

    // I SEGUENTI METODI SONO CONDIVISI CON I THREAD clientHandler e threadPartita

    /* Metodi per la comunicazione di un evento specifico ai client*/
    public static void notificaInizioPartita() throws IOException
    {
        notificaGiocatori("Start", "");
    }

    // Avvisa tutti i giocatori connessi di una disconnessione/connesione, aggiornandoli sul numero di giocatori connessi
    public static void notificaNgiocatori() throws IOException
    {
        notificaGiocatori("NumeroGiocatori", Integer.toString(giocatori.size()));
    }

    /* Metodi per comunicazione generica */

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

    public static void notificaGiocatori(String notifica, String messaggio) throws IOException
    {
        for (clientHandler g : Server.giocatori) 
            invia(g.outToClient, "<" + notifica + ">" + messaggio + "</" +  notifica + ">");
    }
}
