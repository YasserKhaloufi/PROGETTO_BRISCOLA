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
    /// <summary>
    /// Logica di interazione per Home.xaml
    /// </summary>
    public partial class Home : Window
    {
        public Home()
        {
            InitializeComponent();
        }
        private void btnStart_Click(object sender, RoutedEventArgs e)
        {
            if (txtNome.Text != "" && txtNome.Text != "Inserisci nome:")
            {
                String messaggio = "<Username>" + txtNome.Text + "</Username>\n";
                Server.Send(messaggio);

                // Da qui in poi dovrei aspettare di ricevere la lista di room esistenti
                MessageBox.Show(Server.Receive());
            }
        }
        private void btnImpostazioni_Click(object sender, RoutedEventArgs e)
        {
            Impostazioni imp = new Impostazioni();

            this.Hide(); // Nascondo la finestra principale

            imp.ShowDialog(); //...

            this.Show(); // La rivisualizzo

            if (imp.ipAndPort != null) //Controllo nel caso chiudessi la finestra con la x
                Server.handShake(imp.ipAndPort.ElementAt(0), int.Parse(imp.ipAndPort.ElementAt(1)));
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
