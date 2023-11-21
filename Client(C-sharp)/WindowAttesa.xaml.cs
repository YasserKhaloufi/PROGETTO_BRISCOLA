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
        bool attendi = true; // true = attendi giocatori visualizzando finestra d'attesa, false = passerà alla finestra di gioco
        public WindowAttesa()
        {
            InitializeComponent();
            btnStartGame.IsEnabled = false;

            /* Affido il compito di aggiornare la GUI (in base ai messaggi del srvr) ad un altro thread, 
             * cosi da poter permettere comunque la pressione dei vari pulsanti nel frattempo*/
            Task.Run(() => RicercaGiocatori());
        }

        public void RicercaGiocatori()
        {
            String ricevuto = "";
            while (attendi) // Finchè non inizia la partita
            {
                ricevuto = Server.Receive(); // Mi faccio aggiornare dal server sullo stato di preparazione della partita
                String comando = XMLserializer.getComando(ricevuto);

                /* TO DO: gestire il caso in cui il client decida di disconnettersi tramite apposito pulsante o chiudendo la finestra
                 * (dovrà inviare un apposito comando) */
                switch (comando)
                {
                    case "Joined": // Quando un nuovo giocatore si unisce, aggiorno il contatore e abilito il btnStart se i giocatori sono almeno 2
                        Dispatcher.Invoke(() => {
                            txtNgiocatori.Text = XMLserializer.getArgomento(ricevuto);
                            if (int.Parse(txtNgiocatori.Text) >= 2)
                                btnStartGame.IsEnabled = true;
                        });
                        break;

                    case "Start": // Quando uno dei giocatori starta il gioco
                        attendi = false; // Smetti di attendere
                        break;
                }
            }
            Dispatcher.Invoke(() => this.Close()); // Quindi chiudi la finestra di attesa e passa a quella di gioco
        }

        private void btnStartGame_Click(object sender, RoutedEventArgs e)
        {
            /* NOTA:
                  Sapendo di aver premuto il pulsante potrei abilitarmi già a passare alla finestra di gioco,
                  nonostante ciò affiderò comunque al server il compito di dirmi cosa devo fare 
                  (prima di chiudere la finestra passerò comunque per lo switch soprastante ricevendo il comando Start dal server), 
                  quindi faccio nulla qui*/
            Server.startGame();
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
