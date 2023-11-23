using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media.Imaging;

namespace Client_C_sharp_
{
    public static class Server
    {
        // Impostazioni default
        public static String IP = "127.0.0.1";
        public static int PORT = 777;

        // Elementi per trasferimento file
        private static TcpClient client = new TcpClient(IP, PORT);
        private static NetworkStream stream = client.GetStream();

        // Per potersi connettere ad un server diverso dal default
        public static void handShake(string indirizzoIP, int numPorta)
        {
            IP = indirizzoIP;
            PORT = numPorta;
            client = new TcpClient(IP, PORT);
            stream = client.GetStream();
        }

        // Metodi per invio comando specifico
        public static void Connect(String username)
        {
            sendComando("Connect", username);
        }

        public static void Disconnect()
        {
            sendComando("Disconnect", "");
        }

        public static void startGame()
        {
            sendComando("Start", "");
        }

        public static void sendComando(String comando, String argomento)
        {
            Send("<" + comando + ">" + argomento + "</" + comando + ">");
        }

        public static void Send(String messaggio)
        {
            // Converti il messaggio (una stringa) in un array di byte
            byte[] data = Encoding.ASCII.GetBytes(messaggio + "\n"); // Aggiungo l'apposito new line alla fine
            // Invia il messaggio al server
            stream.Write(data, 0, data.Length);
        }
        public static String Receive()
        {
            byte[] data = new byte[1024]; // Preparo il buffer
            StringBuilder completeMessage = new StringBuilder(); // Necessario a ricostruire sottof. di stringa il messaggio ricevuto
            int numberOfBytesRead = 0;

            /* Leggo il messaggio finchè non arrivo alla fine dello stream,
               dato che potrebbe essere più lungo del buffer*/
            do
            {
                numberOfBytesRead = stream.Read(data, 0, data.Length);
                // Ricavo i byte ricevuti sottoforma di stringa e gli aggiungo al "messaggio completo"
                completeMessage.Append(Encoding.ASCII.GetString(data, 0, numberOfBytesRead));
            }
            while (!completeMessage.ToString().EndsWith("\n"));

            return completeMessage.ToString().TrimEnd('\n');
        }

        public static void close()
        {
            // Chiudi la connessione e il flusso di rete
            stream.Close();
            client.Close();
        }

        public static bool isSameSrv(String indirizzo, int porta)
        {
            if(indirizzo == IP && porta == PORT)
                return true;
            
            return false;
        }

        public static Carta getBriscola()
        {
            String ricevuto = Server.Receive();
            Carta c = new Carta(XMLserializer.ReadFromStringRawElements(ricevuto).ElementAt(0));

            return c;
        }

        public static List<Carta> getMano()
        {
            String ricevuto = Server.Receive();

            return XMLserializer.ReadFromString(ricevuto);
        }
    }
}
