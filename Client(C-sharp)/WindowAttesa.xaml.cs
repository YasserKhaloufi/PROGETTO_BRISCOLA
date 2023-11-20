using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Text.RegularExpressions;

namespace Client_C_sharp_
{
    public partial class WindowAttesa : Window
    {
        bool ContinuaCiclo = true;
        public WindowAttesa()
        {
            InitializeComponent();
            btnStartGame.IsEnabled = false;

            /* Affido il compito di aggiornare la GUI ascoltando il server ad un altro thread, cosi da poter permettere la pressione dei vari pulsanti nel frattempo*/
            Task.Run(() => RicercaGiocatori());
        }

        public void RicercaGiocatori()
        {
            String ricevuto = "";
            while (ContinuaCiclo) // Finchè la partita non inizia
            {
                ricevuto = Server.Receive(); // Mi faccio aggiornare dal server sullo stato di preparazione della partita
                String comando = XMLserializer.getComando(ricevuto); // Ricevendo comandi da esso

                switch (comando)
                {
                    case "Joined":
                        Dispatcher.Invoke(() => txtNgiocatori.Text = XMLserializer.getArgomento(ricevuto)); // Aggiorno il contatore

                        if (int.Parse(txtNgiocatori.Text) >= 2) // Se è connesso un minimo di due giocatori
                            Dispatcher.Invoke(() => btnStartGame.IsEnabled = true); // Do la possibilita di iniziare la partita premendo l'apposito pulsante
                        break;

                    case "Start":
                        ContinuaCiclo = false;
                        break;
                }
            }
            this.Close();
        }

        private void btnStartGame_Click(object sender, RoutedEventArgs e)
        {
            // (Anche se sono io stesso a premere il pulsante, affiderò al server il compito di dirmi cosa devo fare (passerò comunque per lo switch soprastante), quindi non c'è bisogno di fare nulla qui)
            Server.Send("<Start></Start>");
        }

        // Da vedere
        /*public void EstraiNumeroUtenti(string input)
        {
            string pattern = @"<utenti>(\d+)</utenti>"; // Pattern per trovare il numero tra <utenti> e </utenti>
            int nGiocatori = 0;
            Regex regex = new Regex(pattern);
            Match match = regex.Match(input);

            if (match.Success)
            {
                // Se c'è una corrispondenza, restituisci il valore trovato nel gruppo corrispondente
                nGiocatori= int.Parse(match.Groups[1].Value);
                if (nGiocatori > 1)
                {
                    lblAttesa.Visibility = Visibility.Collapsed;
                    txtNgiocatori.Text = nGiocatori.ToString();
                    btnStartGame.IsEnabled = true;
                }  
            }
            
        }*/
    }
}
