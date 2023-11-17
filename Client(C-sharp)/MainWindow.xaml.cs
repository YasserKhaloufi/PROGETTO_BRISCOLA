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
        //Server srv = new Server("127.0.0.1", 777);

        public MainWindow()
        {
            InitializeComponent();
            this.Background = new SolidColorBrush(Color.FromRgb(0, 255, 0)); // RGB for green
            txtNome.Text = "Giovanni";
        }

        private void btnStart_Click(object sender, RoutedEventArgs e)
        {
            //if (txtNome.Text != "" && txtNome.Text != "Inserisci nome:")
            //{
            //    String messaggio = "<username>" + txtNome.Text + "</username>";
            //    //srv.Send(messaggio);
            //}

            TcpClient client = new TcpClient("localhost", 777);
            NetworkStream stream = client.GetStream();

            string message = "Hello, Server!\n";
            byte[] data = Encoding.ASCII.GetBytes(message);

            stream.Write(data, 0, data.Length);
            Console.WriteLine("Sent to server: " + message);

            data = new byte[256];
            string responseData = String.Empty;
            int bytes = stream.Read(data, 0, data.Length);
            responseData = Encoding.ASCII.GetString(data, 0, bytes);
            MessageBox.Show("Received from server: " + responseData);

            stream.Close();
            client.Close();
        }

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
    }
}
