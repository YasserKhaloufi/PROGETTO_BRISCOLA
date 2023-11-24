using System;
using System.Collections.Generic;
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
using Path = System.IO.Path;

namespace Client_C_sharp_
{
    public partial class MainWindow : Window, INotifyPropertyChanged
    {

        // Elementi di gioco
        List<Carta> mano = new List<Carta>(); // Mano del giocatore
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

        public List<Carta> Mano
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

        public MainWindow()
        {
            InitializeComponent();
            Closing += MainWindow_Closing;
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
            
            //Server.Disconnect(); // Mi disconnetto dal server per debugging
            //Application.Current.Shutdown(); // Chiudo l'applicazione per debug 
        }

        private void btnCarta_click(object sender, RoutedEventArgs e)
        {
            Button b  = sender as Button;
            String temp = b.Name;
            int indice=int.Parse(b.Name.Substring(3));
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
            Mano = Server.getMano();
            Server.ackowledge();
        }

        public void GiocaCarta(Carta c)
        {
            cartaGiocata=c;
            Server.InviaCarta(c);
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
