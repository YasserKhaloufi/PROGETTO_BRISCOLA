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
        String risposta = "";  // La risposta cambierà in base a ciò che chiede il client

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
                    switch (comando) {
                        default:
                            // lista.add(argomento);
                            risposta = "Test";
                            break;

                        case "Username": // Quando il client invia il proprio username è la prima volta che si connette
                            
                            Giocatore g = new Giocatore(connectionSocket, inFromClient, outToClient); // Il client è nuovo giocatore
                            String username = XMLserializer.getUsername(ricevuto); g.setUsername(username);
                            giocatori.add(g); // aggiungo il client alla lista di giocatori connessi per poterlo ricontattare in futuro
                        
                            risposta = "Giocatore " + username +" inzializzato con successo";
                            break;
                        
                        case "Start":
                            gameStarted = true;
                            risposta = "Partita iniziata";
                            break;
                        
                        case "Exit":
                            spegni = true;
                            risposta = "Chiusura effettuata";
                            break;
                    }

                    // INVIO
                    invia(outToClient, risposta);
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

    public static void invia(DataOutputStream outToClient, String messaggio) throws IOException
    {
        outToClient.writeBytes(messaggio + "\n"); // \n Perchè se non viene rilevato un new line il messaggio non viene "raccolto"
    }

    public static String ricevi(BufferedReader inFromClient) throws IOException
    {
        return inFromClient.readLine();
    }
}
