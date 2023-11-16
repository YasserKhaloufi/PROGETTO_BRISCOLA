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
        public TcpClient client;

        public Server(string indirizzoIP, int numPorta)
        {
            IP = indirizzoIP;
            PORT = numPorta;
            client = new TcpClient(IP, PORT);
        }

        public void Send(String messaggio)
        {
            // Converti il messaggio in un array di byte
            byte[] data = Encoding.ASCII.GetBytes(messaggio);
            // Ottieni il flusso di rete dal client
            NetworkStream stream = client.GetStream();
            // Invia il messaggio al server
            stream.Write(data, 0, data.Length);
            // Chiudi la connessione e il flusso di rete
            stream.Close();
        }
        public String Receive()
        {
            NetworkStream stream = client.GetStream();
            byte[] data = new byte[1024];
            int bytes = stream.Read(data, 0, data.Length);
            string receivedMessage = Encoding.ASCII.GetString(data, 0, bytes);
            stream.Close();
            return receivedMessage;
        }
    }
}
