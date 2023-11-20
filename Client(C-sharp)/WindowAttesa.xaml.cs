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
    /// <summary>
    /// Logica di interazione per WindowAttesa.xaml
    /// </summary>
    public partial class WindowAttesa : Window
    {
        bool ContinuaCiclo = true;
        public WindowAttesa()
        {
            InitializeComponent();
            RicercaGiocatori();

        }
        public void RicercaGiocatori()
        {
            String s = "";
            while (ContinuaCiclo)
            {
                try
                {
                    s = Server.Receive();
                    //s="<utenti>...</utenti>"
                    if (s !=null&&s!="")
                    {
                        EstraiNumeroUtenti(s);
                    }
                }
                catch (Exception)
                {

                    throw;
                }
                
            }
            
        }

        private void btnStartGame_Click(object sender, RoutedEventArgs e)
        {
            ContinuaCiclo = false;
            this.Close();
        }
        public void EstraiNumeroUtenti(string input)
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
            
        }
    }
}
