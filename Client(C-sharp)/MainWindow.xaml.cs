﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Client_C_sharp_
{
    public partial class MainWindow : Window
    {
        // Settings
        String username = "";

        public MainWindow()
        {
            // Impostazioni finestra
            InitializeComponent();

            this.Hide(); // Nascondo la finestra principale

            // Mostro la finestra iniziale
            Home home = new Home();
            home.ShowDialog(); //...

            // Finito con la finestra iniziale mostro quella di attesa
            WindowAttesa windowAttesa = new WindowAttesa();
            windowAttesa.ShowDialog();

            this.Show();

            getBriscola();

            //MessageBox.Show("Partita iniziata");

            //Server.Disconnect(); // Mi disconnetto dal server per debugging
            //Application.Current.Shutdown(); // Chiudo l'applicazione per debug 
        }

        public void getBriscola() 
        {
            String s=Server.Receive();
            Carta c=new Carta(XMLserializer.ReadFromStringRawElements(s).ElementAt(0));
            //imgboxBriscola.Source = c.GetImg_path();
            txtDebug.Text = s;
        }

        private void MainWindow_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            Application.Current.Shutdown();
            Server.Disconnect();
        }
    }
}
