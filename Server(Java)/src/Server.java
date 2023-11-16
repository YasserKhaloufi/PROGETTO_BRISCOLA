import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Server {

    // DEFINISCI QUI LE EVENTUALI LISTE DA MANTENERE IN RAM 
    /*List<Oggetto> lista =  new ArrayList<Oggetto>();*/

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
        
        // Elementi di ricezione
        String recieved;    // Stringa ricevuta, ricavata a partire dal contenuto del buffer in ricezione
        
        // Definisci qui eventuali elementi che verranno inizializzati parsando recieved
        String command = "";
        // Object argomento;

        // Elementi di invio
        ServerSocket serverSocket = new ServerSocket(porta);
        String answer = "";  // La risposta cambierà in base a ciò che chiede il client

        // Sentinella per dettare lo spegnimento del server in caso ci fosse lato client un pulsante che permetta di farlo (exit)
        boolean shut = false;

        while (!shut) {

            // Aspetto connessioni TCP dai client
            Socket clientSocket = serverSocket.accept();

            // Creo il flusso di ricezione
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Creo  il flusso di output
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            recieved = in.readLine().replace("\0", "");
            System.out.println(recieved);
            // Parsing messaggio ricevuto
            command = XMLserializer.getCommand(recieved);
            // argomento = XMLserializer.getArgomento(recieved); // Forse è meglio farlo quando sei sicuro che esiste, ovvero nell'opportuno blocco dello switch

            // SPECIFICA QUI QUALE SARA' IL COMPORTAMENTO DEL SERVER IN BASE AL COMANDO RICEVUTO
            switch (command) {
                default:
                    // lista.add(argomento);
                    answer = "esempio";
                    break;

                /*    
                case "exit":
                    shut = true;
                    break; */
            }

            // INVIO
            out.println(answer);

        }
        serverSocket.close();
    }

    // DEFINISCI QUA EVENTUALI METODI CHE CERCANO/OPERANO SULLA LISTA RAM
}
