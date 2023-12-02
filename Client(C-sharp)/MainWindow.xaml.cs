using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.IO;
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
using System.Xml.Linq;
using Path = System.IO.Path;

namespace Client_C_sharp_
{
    public partial class MainWindow : Window, INotifyPropertyChanged
    {

        string username = "";
        private List<Button> buttons; // Lista di pulsanti generati dinamicamente

        // Elementi di gioco
        Carta briscola = new Carta();
        ObservableCollection<Carta> mano = new ObservableCollection<Carta>(); // Mano del giocatore
        ObservableCollection<Carta> carteGiocate = new ObservableCollection<Carta>();

        // La seguenti property sono necessarie per il binding grafico delle carte in mano al giocatore,per la briscola e le carte giocate in un giro di turni dai vari giocatori
        public event PropertyChangedEventHandler PropertyChanged;

        public Carta Briscola
        {
            get { return briscola; }
            set
            {
                if (briscola != value)
                {
                    briscola = value;
                    OnPropertyChanged("Briscola");
                }
            }
        }

        public ObservableCollection<Carta> Mano
        {
            get { return mano; }
            set
            {
                if (mano != value)
                {
                    mano = value;
                    OnPropertyChanged("Mano"); // Ogni volta che la mano cambia, viene notificato, quindi si aggiorna la grafica
                }
            }
        }

        public ObservableCollection<Carta> CarteGiocate
        {
            get { return carteGiocate; }
            set
            {
                if (carteGiocate != value)
                {
                    carteGiocate = value;
                    OnPropertyChanged("CarteGiocate");
                }
            }
        }

        public MainWindow()
        {
            InitializeComponent();
            Closing += MainWindow_Closing;
            LayoutUpdated += MainWindow_LayoutUpdated;
            DataContext = this; // Necessario perchè XAML riesca a vedere "Mano" e aggiornare la carte mostrate in base ad essa

            this.Hide(); // Nascondo la finestra principale

            // Mostro la finestra iniziale
            Home home = new Home();
            home.ShowDialog(); // L'utente inserisce il nickname e nel caso cambia le impostazioni....
            username = home.username;
            

            // Finito con la finestra iniziale mostro quella di attesa
            WindowAttesa windowAttesa = new WindowAttesa();
            windowAttesa.ShowDialog(); // L'utente attende che si colleghi almeno un altro giocatore poi può cliccare su start game

            this.Show(); // Finalmente viene mostrata la finestra principale, quella di gioco

            this.Title = username; // Assegno il nome della finestra al nome del giocatore

            riceviBriscola();
            riceviMano();
            nominaPulsanti();

            Task.Run(() => riceviComando()); // Mi metto in ascolto di comandi dal server

            //Application.Current.Shutdown(); // Chiudo l'applicazione per debug 
        }

        private void riceviComando()
        {
            bool finePartita = false;
            while (!finePartita)
            {

                if(Mano.Count == 0)
                    Server.sendComando("End", "");
                    
                String ricevuto = Server.Receive();
                String comando = XMLserializer.getComando(ricevuto);
                String argomento = "";

                switch (comando)
                {
                    case "Turn":
                        Dispatcher.Invoke(() =>
                        {
                            argomento = XMLserializer.getArgomento(ricevuto);

                            if (argomento == "Yours") 
                            {
                                abilitaPulsanti();

                                if(Mano.Count < 3)
                                    pesca();
                            }
                            else
                            {
                                disabilitaPulsanti();
                                txtDebug.Text = "Turno di " + argomento;
                            }
                            Server.acknowledge();
                        });
                        break;
                    
                    case "Carte":
                        Dispatcher.Invoke(() =>
                        {
                            Carta c = XMLserializer.ReadCarteFromString(ricevuto).ElementAt(0);
                            CarteGiocate.Add(c);
                            txtDebug.Text = "Carta giocata: " + c.ToString();
                            Server.acknowledge();
                        });
                        break;

                    case "Winner":
                        Dispatcher.Invoke(() =>
                        {
                            argomento = XMLserializer.getArgomento(ricevuto);
                            txtDebug.Text = "Vincitore giro: " + argomento;
                            Server.acknowledge();
                        });
                        break;

                    case "Score":
                        Dispatcher.Invoke(() =>
                        {
                            argomento = XMLserializer.getArgomento(ricevuto);

                            // TO DO: rimuovere "hoVinto" e "username" e mandare ad ognuno il proprio punteggio
                            
                            int punteggio = int.Parse(argomento);
                            txtScore.Text = punteggio.ToString();

                            CarteGiocate.Clear();
                            Server.acknowledge();
                        });
                        break;

                    case "Winner_":
                        Dispatcher.Invoke(() =>
                        {
                            argomento = XMLserializer.getArgomento(ricevuto);

                            if (argomento == "Yours") 
                                MessageBox.Show("Hai vinto!");
                            else
                                MessageBox.Show("Ha vinto " + argomento);

                            Server.acknowledge();

                            finePartita = true;
                        });
                        break;
                }
            }

            Dispatcher.Invoke(() =>
            {
                Application.Current.Shutdown();
            });
        }

        private void btnCarta_click(object sender, RoutedEventArgs e)
        {
            Button b = sender as Button;
            String temp = b.Name;
            int indice = int.Parse(b.Name.Substring(b.Name.Length - 1)); // Ricavo l'indice della carta cliccata
            GiocaCarta(mano.ElementAt(indice));
            Mano.RemoveAt(indice);
        }

        /* Ricezione informazioni specifiche*/

        // Ricava dal server la briscola e la mostra
        private void riceviBriscola()
        {
            Briscola = Server.getCarta();
            Server.acknowledge();
        }

        // Ricava dal server la mano e la mostra
        private void riceviMano()
        {
            Mano = new ObservableCollection<Carta>(Server.getMano());
            Server.acknowledge();
        }

        private void pesca()
        {
            Carta c = Server.pesca();
            if(c != null)
                Mano.Add(c);
        }

        /* Invio informazioni specifiche */

        // Invia al server la carta giocata, che verrà poi inviata a tutti gli altri giocatori
        private void GiocaCarta(Carta c)
        {
            Server.InviaCarta(c);
        }

        /* Metodi di elaborazione per la finestra */

        // I pulsanti presenti nella finestra sono generati dinamicamente, quindi non hanno un nome, perciò glieli assegno
        public void nominaPulsanti()
        {
            int buttonCount = 0;
            foreach (Button b in buttons)
                b.Name = "btn" + buttonCount++;
        }

        public void disabilitaPulsanti()
        {
            foreach (Button b in buttons)
                b.IsEnabled = false;
        }

        public void abilitaPulsanti()
        {
            foreach (Button b in buttons)
                b.IsEnabled = true;
        }

        private void MainWindow_LayoutUpdated(object sender, EventArgs e)
        {
            // Call the function when the layout is updated
            buttons = FindButtons(ItemsControl).ToList();
            nominaPulsanti();
        }

        public IEnumerable<Button> FindButtons(DependencyObject parent)
        {
            for (int i = 0; i < VisualTreeHelper.GetChildrenCount(parent); i++)
            {
                var child = VisualTreeHelper.GetChild(parent, i);

                if (child is Button button)
                {
                    yield return button;
                }
                else
                {
                    foreach (var childButton in FindButtons(child))
                    {
                        yield return childButton;
                    }
                }
            }
        }

        private void MainWindow_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            Server.Disconnect();
            Application.Current.Shutdown();
        }

        protected virtual void OnPropertyChanged(string propertyName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
