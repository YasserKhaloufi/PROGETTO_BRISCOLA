import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Server {

    // Settings generali
    static int porta = Settings.porta;
    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException, SAXException
    {
        
        // Codice per generare il mazzo in formato XML
        {
        /*String[] semi = {"bastoni", "coppe", "denari", "spade"}; // Semi napoletani
        char[] numeri = {'A', '2', '3', '4', '5', '6', '7', 'F', 'C', 'R'}; // 1=asso, 8=fante, 9=cavallo, 10=re
        int[] valori = {11, 0, 10, 0, 0, 0, 0, 2, 3, 4}; // Valori di presa

        List<Carta> mazzo = new ArrayList<>();

        for (String seme : semi) {ò
            for (int i = 0; i < numeri.length; i++) {
                char numero = numeri[i];
                int valore = valori[i];
                String img_path = numero + "-" + seme + ".jpg";

                Carta carta = new Carta(seme, numero, valore, img_path);
                mazzo.add(carta);
            }
        }
        XMLserializer.saveLista("./Server(Java)/src/Mazzo.xml", mazzo);*/}

        // Elementi di ricezione
        String recieved;    // Stringa ricevuta, ricavata a partire dal contenuto del buffer in ricezione
        
        // Definisci qui eventuali elementi che verranno inizializzati parsando recieved
        String command = "";
        // Object argomento;

        // Elementi di invio
        ServerSocket serverSocket = new ServerSocket(Settings.porta);
        System.out.println("Server is running and waiting for a client...");
        String answer = "";  // La risposta cambierà in base a ciò che chiede il client

        // Sentinella per dettare lo spegnimento del server in caso ci fosse lato client un pulsante che permetta di farlo (exit)
        boolean shut = false;

        while (!shut) {

            // Aspetto connessioni TCP dai client
            Socket connectionSocket = serverSocket.accept();
            // Creo il flusso di ricezione
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            // Creo  il flusso di invio
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            recieved = inFromClient.readLine();
            System.out.println("Received from client: " + recieved);

            // Parsing messaggio ricevuto
            command = XMLserializer.getCommand(recieved);
            // argomento = XMLserializer.getArgomento(recieved); // Forse è meglio farlo quando sei sicuro che esiste, ovvero nell'opportuno blocco dello switch

            // SPECIFICA QUI QUALE SARA' IL COMPORTAMENTO DEL SERVER IN BASE AL COMANDO RICEVUTO
            switch (command) {
                default:
                    // lista.add(argomento);
                    answer = "esempio\n";
                    break;

                /*    
                case "exit":
                    shut = true;
                    break; */
            }

            // INVIO
            outToClient.writeBytes(answer);

        }
        serverSocket.close();
    }
}
