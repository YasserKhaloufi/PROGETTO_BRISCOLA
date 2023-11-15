using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Client_C_sharp_
{
    internal class Client
    {
        private String _indirizzoIP;
        private int _numPorta;
        private TcpClient client=null;
        private TcpListener listener = null;

        //property
        public string IndirzzoIP
        {
            get
            {
                return _indirizzoIP;
            }
        }
        public int NumPorta
        {
            get
            {
                return _numPorta;
            }
        }

        public Client(string indirizzoIP, int numPorta)
        {
            _indirizzoIP = indirizzoIP;
            _numPorta = numPorta;
            TcpClient client = new TcpClient(_indirizzoIP, _numPorta);
            client.Connect(_indirizzoIP, _numPorta);
        }
        public void Send(String messaggio)
        {
            // Ottenere il flusso di rete dal client
            NetworkStream stream = client.GetStream();
            // Convertire il messaggio in un array di byte
            byte[] data = Encoding.ASCII.GetBytes(messaggio);
            // Invio del messaggio al server
            stream.Write(data, 0, data.Length);
        }
        public String Receive()
        {
            NetworkStream stream = client.GetStream();
            byte[] data = new byte[1024];
            int bytes = stream.Read(data, 0, data.Length);
            string receivedMessage = Encoding.ASCII.GetString(data, 0, bytes);
            return receivedMessage;
        }

        

       

    }
}
