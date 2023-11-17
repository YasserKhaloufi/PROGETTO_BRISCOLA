using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace Client_C_sharp_
{
    class Server
    {
        public String IP { get; set; }
        public int PORT { get; set; }

        // Elementi per trasferimento file
        private TcpClient client;
        private NetworkStream stream;

        public Server(string indirizzoIP, int numPorta)
        {
            IP = indirizzoIP;
            PORT = numPorta;
            client = new TcpClient(IP, PORT);
            stream = client.GetStream();
        }

        public void Send(String messaggio)
        {
            // Converti il messaggio (una stringa) in un array di byte
            byte[] data = Encoding.ASCII.GetBytes(messaggio);
            // Invia il messaggio al server
            stream.Write(data, 0, data.Length);
        }
        public String Receive()
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
                completeMessage.AppendFormat("{0}", Encoding.ASCII.GetString(data, 0, numberOfBytesRead));
            }
            while (stream.DataAvailable);

            return completeMessage.ToString();
        }

        public void close()
        {
            // Chiudi la connessione e il flusso di rete
            stream.Close();
            client.Close();
        }
    }
}
