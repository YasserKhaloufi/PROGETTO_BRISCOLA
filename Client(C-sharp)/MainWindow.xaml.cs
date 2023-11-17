using System;
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
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        // Settings
        Server srv = new Server("127.0.0.1", 777);
        String username = "";

        public MainWindow()
        {
            InitializeComponent();
            this.Background = new SolidColorBrush(Color.FromRgb(0, 255, 0)); // Setto lo sfondo (giusto per provare)
            txtNome.Text = "Giovanni"; // Per debug
        }

        private void btnStart_Click(object sender, RoutedEventArgs e)
        {
            if (txtNome.Text != "" && txtNome.Text != "Inserisci nome:")
            {
                String messaggio = "<Connect></Connect>\n";
                srv.Send(messaggio);

                // Da qui in poi dovrei aspettare di ricevere la lista di room esistenti
                MessageBox.Show(srv.Receive());
                srv.close();
            }
        }
        private void btnImpostazioni_Click(object sender, RoutedEventArgs e)
        {
            Impostazioni imp = new Impostazioni();

            this.Hide(); // Nascondo la finestra principale

            imp.ShowDialog(); //...

            this.Show(); // La rivisualizzo

            if (imp.ipAndPort != null) //Controllo nel caso chiudessi la finestra con la x
            {
                //srv.IP = imp.ipAndPort.ElementAt(0);
                //srv.PORT = int.Parse(imp.ipAndPort.ElementAt(1));
            }
        }

        // Roba di grafica
        private void txtNome_GotFocus(object sender, RoutedEventArgs e)
        {
            if (txtNome.Text == "Inserisci nome:")
            {
                txtNome.Text = string.Empty;
            }
        }

        private void txtNome_LostFocus(object sender, RoutedEventArgs e)
        {
            if (string.IsNullOrWhiteSpace(txtNome.Text))
            {
                txtNome.Text = "Inserisci nome:";
            }
        }
    }
}
