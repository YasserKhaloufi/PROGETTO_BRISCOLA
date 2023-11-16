using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
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
    /// Logica di interazione per Impostazioni.xaml
    /// </summary>
    public partial class Impostazioni : Window
    {
        public List<String> ipAndPort { get; set; }
        public Impostazioni()
        {
            InitializeComponent();
        }

        private void btnConferma_Click(object sender, RoutedEventArgs e)
        {
            ipAndPort = new List<String>(); //Se non inizializzi non funziona

            String IP = txtIP.Text;
            String porta = txtPort.Text;

            if (ValidateIPAndPort())
            {
                ipAndPort.Add(IP);
                ipAndPort.Add(porta);
                this.Close();
            }
        }

        public bool ValidateIPAndPort()
        {
            /* 
             * ringrazio chatGPT per avermi dato un accenno sulle regex
             * 
             * La @ indica che da li in poi i caratteri di escape saranno trattati come normali caratteri;
             * 
             * ^ e $ marcano rispettivamente l'inizio e la fine di una stringa;
             * 
             * \d indica che quel carattere della stringa dovrà essere un numero da 0 a 9;
             * 
             * In questo caso {1,3} indica che \d (il carattere da 0-9) potrà ripetersi da 1 a 3 volte;
             * 
             * il "." è usato nelle regex per indicare le zone in cui va bene qualsiasi carattere e siccome
             * a noi serve così com'è scriviamo un \ prima di esso;
             * 
             * La regex per l'IP ad esempio, nel suo complesso, permette quattro gruppi di uno o tre cifre separati da un punto
             * 
             */

            string ipAddressPattern = @"^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$";
            string portPattern = @"^\d{1,5}$";

            if (!Regex.IsMatch(txtIP.Text, ipAddressPattern))
            {
                MessageBox.Show("Inserisci un indirizzo IP valido.");
                return false;
            }
            else if (!Regex.IsMatch(txtPort.Text, portPattern))
            {
                MessageBox.Show("Inserisci un numero di porta valido.");
                return false;
            }

            return true;
        }
    }
}
