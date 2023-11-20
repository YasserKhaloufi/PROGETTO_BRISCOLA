using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace Client_C_sharp_
{
    public class Carta
    {
        private string seme;
        private char numero;
        private int valore;
        private string img_path;

        public Carta(string seme, char numero, int valore, string img_path)
        {
            this.seme = seme;
            this.numero = numero;
            this.valore = valore;
            this.img_path = img_path;
        }

        public Carta(string s)
        {
            // seme;numero;valore;img_path
            string[] stringaSplittata = s.Split(';');
            this.seme = stringaSplittata[0];
            this.numero = stringaSplittata[1][0];
            this.valore = int.Parse(stringaSplittata[2]);
            this.img_path = stringaSplittata[3];
        }

        public Carta(XmlElement e)
        {
            this.seme = XMLserializer.ParseTagName(e, "seed");
            this.numero = XMLserializer.ParseTagName(e, "number")[0];
            this.valore = int.Parse(XMLserializer.ParseTagName(e, "value"));
            this.img_path = XMLserializer.ParseTagName(e, "img_path");
        }

        public XmlElement Serialize(XmlDocument d)
        {
            XmlElement elementoCarta = d.CreateElement("Carta");

            XmlElement elementoSeed = d.CreateElement("seed");
            elementoSeed.AppendChild(d.CreateTextNode(seme));

            XmlElement elementoNumber = d.CreateElement("number");
            elementoNumber.AppendChild(d.CreateTextNode(numero.ToString()));

            XmlElement elementoValue = d.CreateElement("value");
            elementoValue.AppendChild(d.CreateTextNode(valore.ToString()));

            XmlElement elementoPath = d.CreateElement("img_path");
            elementoPath.AppendChild(d.CreateTextNode(img_path));

            elementoCarta.AppendChild(elementoSeed);
            elementoCarta.AppendChild(elementoNumber);
            elementoCarta.AppendChild(elementoValue);
            elementoCarta.AppendChild(elementoPath);

            return elementoCarta;
        }

        public XmlDocument Serialize()
        {
            XmlDocument d = new XmlDocument();
            XmlElement root = d.CreateElement("Carta");

            root.AppendChild(Serialize(d));

            d.AppendChild(root);

            return d;
        }

        public string GetSeme()
        {
            return seme;
        }

        public int GetNumero()
        {
            return numero;
        }

        public int GetValore()
        {
            return valore;
        }

        public string GetImg_path()
        {
            return img_path;
        }

        public override string ToString()
        {
            return seme + ";" + numero + ";" + valore + ";" + img_path;
        }

        public Carta ToCarta(string s)
        {
            Carta c = new Carta(s);
            return c;
        }
    }
}
