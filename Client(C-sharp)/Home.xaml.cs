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

namespace Client_C_sharp_
{
    public partial class Home : Window
    {
        private bool isClosingFromButton = false; // Sentinella per controllare se la finestra si sta chiudendo tramite il pulsante "X" o meno

        // Questa finestra si aprirà prima della main window
        public Home()
        {
            InitializeComponent();
            Closing += MainWindow_Closing; // Nel caso la finestra venisse chiusa con la "X" in alto a destra, chiudo l'applicazione
            this.Background = new SolidColorBrush(Color.FromRgb(0, 255, 0)); // Setto lo sfondo (giusto per provare)
            txtNome.Text = "Giovanni"; // Per debug
        }
        private void btnStart_Click(object sender, RoutedEventArgs e)
        {
            if (txtNome.Text != "" && txtNome.Text != "Inserisci nome:")
            {
                Server.Connect(txtNome.Text); // Avviso il server che mi sto connettendo, inviando il mio nome

                // TO DO: ricevere da server un feedback e scriverlo su console (per debug)

                isClosingFromButton = true; // Setto la sentinella a true, così il processo non verrà chiuso dal metodo MainWindow_Closing
                this.Close(); // Torno al codice della main window (passando alla fase di attesa)
            }
            else
                MessageBox.Show("Inserisci username");
        }

        private void btnImpostazioni_Click(object sender, RoutedEventArgs e)
        {
            Impostazioni imp = new Impostazioni();

            this.Hide(); // Nascondo la finestra principale

            imp.ShowDialog(); //...

            this.Show(); // La rivisualizzo

            if (imp.ipAndPort != null) //Controllo nel caso chiudessi la finestra con la x
            {
                String indirizzo = imp.ipAndPort.ElementAt(0); int porta = int.Parse(imp.ipAndPort.ElementAt(1));

                if (!Server.isSameSrv(indirizzo, porta))
                    Server.handShake(indirizzo, porta);
                else
                    MessageBox.Show("Il server indicato è già connesso"); 
            }
        }

        // Roba di grafica
        private void txtNome_GotFocus(object sender, RoutedEventArgs e)
        {
            if (txtNome.Text == "Inserisci nome:")
                txtNome.Text = string.Empty;
        }

        private void txtNome_LostFocus(object sender, RoutedEventArgs e)
        {
            if (string.IsNullOrWhiteSpace(txtNome.Text))
                txtNome.Text = "Inserisci nome:";
        }

        private void MainWindow_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            if (!isClosingFromButton) // Controllo l'apposita sentinella se l'utente sta chiudendo l'app con "X" o sta procedendo a startare
                Application.Current.Shutdown();

            isClosingFromButton = false; // Resetto la sentinella
        }
    }
}
