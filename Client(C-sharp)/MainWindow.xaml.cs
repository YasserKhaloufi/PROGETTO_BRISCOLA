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

        // Elementi di gioco
        ObservableCollection<Carta> mano = new ObservableCollection<Carta>(); // Mano del giocatore
        Carta briscola = new Carta();
        Carta cartaGiocata = new Carta();

        // La seguente property è necessaria per il binding grafico delle carte in mano al giocatore e della briscola
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

        private List<Button> buttons; // Lista di pulsanti generati dinamicamente

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

            // Finito con la finestra iniziale mostro quella di attesa
            WindowAttesa windowAttesa = new WindowAttesa();
            windowAttesa.ShowDialog(); // L'utente attende che si colleghi almeno un altro giocatore poi può cliccare su start game

            this.Show(); // Finalmente viene mostrata la finestra principale, quella di gioco

            riceviBriscola();
            riceviMano();
            nominaPulsanti();

            Task.Run(() => riceviComando()); // Mi metto in ascolto di comandi dal server



            //Server.Disconnect(); // Mi disconnetto dal server per debugging
            //Application.Current.Shutdown(); // Chiudo l'applicazione per debug 
        }

        private void riceviComando()
        {
            while (true)
            {
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
                                abilitaPulsanti();
                            else
                            {
                                disabilitaPulsanti();
                                txtDebug.Text = "Turno di " + argomento;
                            }
                        });
                        break;
                    
                    case "Carta":
                        Dispatcher.Invoke(() =>
                        {
                            argomento = XMLserializer.getArgomento(ricevuto);
                            Carta c = XMLserializer.ReadFromString(argomento).ElementAt(0);
                            cartaGiocata = c;
                            txtDebug.Text = "Carta giocata: " + c.ToString();
                        });
                        break;
                }
            }
        }

        private void btnCarta_click(object sender, RoutedEventArgs e)
        {
            Button b = sender as Button;
            String temp = b.Name;
            int indice = int.Parse(b.Name.Substring(b.Name.Length - 1)); // Ricavo l'indice della carta cliccata
            GiocaCarta(mano.ElementAt(indice));
            Mano.RemoveAt(indice);
        }

        // Ricava dal server la briscola e la mostra
        private void riceviBriscola()
        {
            Briscola = Server.getBriscola();
            Server.ackowledge();
        }

        //Ricava dal server la mano e la mostra
        private void riceviMano()
        {
            Mano = new ObservableCollection<Carta>(Server.getMano());
            Server.ackowledge();
        }

        public void GiocaCarta(Carta c)
        {
            Server.InviaCarta(c);
        }

        // I pulsanti presenti nella finestra sono generati dinamicamente, quindi non hanno un nome, perciò glielo assegno
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
