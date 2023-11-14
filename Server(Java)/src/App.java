import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class App {

    // DEFINISCI QUI LE EVENTUALI LISTE DA MANTENERE IN RAM 
    /*List<Oggetto> lista =  new ArrayList<Oggetto>();*/

    // Settings generali
    static int porta = 777;
    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException
    {
        // Codice per generare il mazzo
        /*String[] semi = {"cuori", "quadri", "fiori", "picche"}; // Semi italiani o francesi
        int[] numeri = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}; // 1=asso, 8=fante, 9=cavallo, 10=re
        int[] valori = {11, 0, 10, 0, 0, 0, 0, 2, 3, 4}; // Valori di presa

        List<Carta> mazzo = new ArrayList<>();

        for (String seme : semi) {
            for (int i = 0; i < numeri.length; i++) {
                int numero = numeri[i];
                int valore = valori[i];
                String img_path = "/path/to/images/" + seme + "/" + numero + ".jpg";

                Carta carta = new Carta(seme, numero, valore, img_path);
                mazzo.add(carta);
            }
        }
        XMLserializer.saveLista("./Server(Java)/src/Mazzo.xml", mazzo);*/

        // Definizione buffer e packet
        byte[] buffer = new byte[1500];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        // Elementi di ricezione
        DatagramSocket socketRecieve = new DatagramSocket(porta);
        String recieved;    // Stringa ricevuta, ricavata a partire dal contenuto del buffer in ricezione
        
        // Definisci qui eventuali elementi che verranno inizializzati parsando recieved
        String command = "";
        Object argomento;

        // Elementi di invio
        DatagramSocket socketSend = new DatagramSocket();
        String answer = "";  // La risposta cambierà in base a ciò che chiede il client
        DatagramPacket reply;

        // Sentinella per dettare lo spegnimento del server in caso ci fosse lato client un pulsante che permetta di farlo (exit)
        boolean shut = false;

        while (!shut) {

            // A ogni ciclo effettuo la pulizia del buffer e quindi del packet
            buffer = new byte[1500];
            packet = new DatagramPacket(buffer, buffer.length);

            // RICEZIONE
            socketRecieve.receive(packet);
            recieved = new String(buffer).replace("\0", ""); // Decodifico i byte nel buffer in una serie di caratteri ASCII quindi stringa

            // Nel caso volessi fare qualche test di parsing:
            /*
              String temp = "<set><persona><nome>John</nome><cognome>Doe</cognome><annoNascita>1990</annoNascita><numeroTelefono>1234567890</numeroTelefono></persona></set>";
              // Crea il Document con input source temp ecc...
             */

            // EFFETTUA QUI IL PARSING DELLA STRINGA ricevuta
            // 1. Individuazione comando/istruzione
            // 2. Individuazione eventuale argomento dell'comando/istruzione

            command = ""; //XMLserializer.getCommand(recieved);
            argomento = ""; //XMLserializer.getArgomento(recieved); // Forse è meglio farlo quando sei sicuro che esiste, ovvero nell'opportuno blocco dello switch

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
            buffer = answer.getBytes();
            reply = new DatagramPacket(buffer, buffer.length);
            reply.setAddress(packet.getAddress());
            reply.setPort(packet.getPort());
            socketSend.send(reply);
        }

        socketSend.close();
        socketRecieve.close();
    }

    // DEFINISCI QUA EVENTUALI METODI CHE CERCANO/OPERANO SULLA LISTA RAM
}
