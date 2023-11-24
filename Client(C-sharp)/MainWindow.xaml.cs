using System;
using System.Collections.Generic;
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
    public partial class MainWindow : Window
    {
        // Settings
        String username = "";
        List<Image> imageBoxes = new List<Image>();

        List<Carta> mano=new List<Carta>();
        Carta presa=null;

        public MainWindow()
        {
            // Impostazioni finestra
            InitializeComponent(); 
            preparativi();

            this.Hide(); // Nascondo la finestra principale

            // Mostro la finestra iniziale
            Home home = new Home();
            home.ShowDialog(); //...

            // Finito con la finestra iniziale mostro quella di attesa
            WindowAttesa windowAttesa = new WindowAttesa();
            windowAttesa.ShowDialog();

            this.Show();

            renderBriscola(); renderMano();

            //Server.Disconnect(); // Mi disconnetto dal server per debugging
            //Application.Current.Shutdown(); // Chiudo l'applicazione per debug 
        }

        private void renderBriscola()
        {
            String path = Server.getBriscola().GetImg_path();
            imgBoxBriscola.Source = new BitmapImage(new Uri(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "img", path)));
        }

        private void renderMano()
        {
            mano = Server.getMano();

            refresh();
            
        }

        private void MainWindow_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            Application.Current.Shutdown();
            Server.Disconnect();
        }

        private void btnCarta_click(object sender, RoutedEventArgs e)
        {
            Button b  = sender as Button; // Cast puslante
            String indice = b.Name;
            int index=int.Parse(indice.Substring(3));
            GiocaCarta(mano.ElementAt(index));

            mano.RemoveAt(index);
            mano.Add(null);

            refresh();

            
        }
        public void GiocaCarta(Carta c)
        {
            presa=c;
            Server.InviaCarta(c);
        }

        public void refresh()
        {
            // Assegno ad ogni immagine il rispettivo path, estraendolo dalla lista di carte
            for (int i = 0; i < imageBoxes.Count; i++)
            {
                String path = "carta-retro.jpg";
                if (mano.ElementAt(i) != null)
                {
                    path = mano.ElementAt(i).GetImg_path();
                    imageBoxes.ElementAt(i).Source = new BitmapImage(new Uri(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "img", path)));
                }
                else
                    imageBoxes.ElementAt(i).Source = new BitmapImage(new Uri(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "img", path)));
            }
            if(presa!=null)
                imgBoxPresa.Source = new BitmapImage(new Uri(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "img", presa.GetImg_path())));
        }

        public void preparativi()
        {
            imageBoxes.Add(imgBox1);
            imageBoxes.Add(imgBox2);
            imageBoxes.Add(imgBox3);
        }

    }
}
