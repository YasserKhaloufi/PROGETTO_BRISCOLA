﻿using System;
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
        private String _IPserver;
        private int _PORTserver;
        private TcpClient client = null;
        private TcpListener listener = null;

        //property
        public string IndirzzoIP
        {
            get
            {
                return _IPserver;
            }
        }
        public int NumPorta
        {
            get
            {
                return _PORTserver;
            }
        }

        public Client(string indirizzoIP, int numPorta)
        {
            _IPserver = indirizzoIP;
            _PORTserver = numPorta;
            client = new TcpClient(_IPserver, _PORTserver);
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
