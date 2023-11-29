#nullable enable

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using System.Xml.Serialization;

namespace Client_C_sharp_
{
    public class Carta : INotifyPropertyChanged
    {
        public string seme {get; set;}
        public char numero { get; set; }
        public int valore   { get; set; }
        private string img_path;

        // Per binding della mano con la GUI
        public string Img_path
        {
            get { return System.IO.Path.Combine(Environment.CurrentDirectory, "img", img_path); } // Ritorna il path completo in base alla disposizione del progetto
            set
            {
                if (img_path != value)
                {
                    img_path = value;
                    OnPropertyChanged("Img_path");
                }
            }
        }

        protected virtual void OnPropertyChanged(string propertyName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

        public event PropertyChangedEventHandler PropertyChanged;


        public Carta(string seme, char numero, int valore, string img_path)
        {
            this.seme = seme;
            this.numero = numero;
            this.valore = valore;
            this.img_path = img_path;
        }
        public Carta()
        {
            seme = "";
            numero = '0';
            valore = 0;
            img_path = "";
        }

        public Carta(XmlElement e)
        {
            this.seme = XMLserializer.ParseTagName(e, "seme");
            this.numero = XMLserializer.ParseTagName(e, "numero")[0];
            this.valore = int.Parse(XMLserializer.ParseTagName(e, "valore"));
            this.img_path = XMLserializer.ParseTagName(e, "Img_path");
        }

        // Serializzazione in elemento XML per append a documento superiore
        public XmlElement Serialize(XmlDocument d)
        {
            XmlElement elementoCarta = d.CreateElement("Carta");

            XmlElement elementoSeed = d.CreateElement("seme");
            elementoSeed.AppendChild(d.CreateTextNode(seme));

            XmlElement elementoNumber = d.CreateElement("numero");
            elementoNumber.AppendChild(d.CreateTextNode(numero.ToString()));

            XmlElement elementoValue = d.CreateElement("valore");
            elementoValue.AppendChild(d.CreateTextNode(valore.ToString()));

            XmlElement elementoPath = d.CreateElement("Img_path");
            elementoPath.AppendChild(d.CreateTextNode(img_path));

            elementoCarta.AppendChild(elementoSeed);
            elementoCarta.AppendChild(elementoNumber);
            elementoCarta.AppendChild(elementoValue);
            elementoCarta.AppendChild(elementoPath);

            return elementoCarta;
        }

        // Serializzazione in documento XML
        public XmlDocument Serialize()
        {
            XmlDocument d = new XmlDocument();
            XmlElement root = d.CreateElement("Carta");

            root.AppendChild(Serialize(d));

            d.AppendChild(root);

            return d;
        }

        // to XML string con dichiarazione per invio a server 
        public String serialize()
        {
            String message = "";
            XmlSerializer serializer = new XmlSerializer(this.GetType());
            StringWriter sw = new StringWriter();
            XmlWriter xw = XmlWriter.Create(sw);
            serializer.Serialize(xw, this);
            message = sw.ToString();
            return message;
        }

        // to XML string senza dichiarazione per invio a server
        public String toXML()
        {
            String xmlString = "";
            XmlSerializer serializer = new XmlSerializer(this.GetType()); //Crea un serializzatore per questa classe
            StringWriter sw = new StringWriter();

            /* 
            * Non mi servono la dichiarazione XML e i namespaces nella
            * mia stringa XML (più che altro non mi piacciono),
            * rimuoverli potrebbe portare a dei problemi, questo se effettuassi
            * la deserializzazione lato server in maniera automatica, ma tanto
            * la effettuo a mano...
            * 
            * Quindi personalizzo l'xml writer definendo le seguenti impostazioni
            */
            XmlWriterSettings settings = new XmlWriterSettings();

            /*
            * La seguente opzione serve a permettere di scrivere un singolo frammento di XML
            * invece che scriverlo tutto (cioè con tanto di dichiarazione), cosicchè io
            * possa scrivere solo la parte che mi serve (ovvero <Prodotto>...</Prodotto>
            */
            settings.ConformanceLevel = ConformanceLevel.Fragment;

            // settings.Indent = true; // non sono sicuro che questo attivi effettivamente l'indentazione

            /* L'XMLWriter serve a scrivere una stringa in formato xml,
            * scrivendo all'interno di uno stringwriter, è anche possibile utilizzare impostazioni specifiche */
            using (XmlWriter xw = XmlWriter.Create(sw, settings))
            {
                // Definisco dei namespace nulli
                XmlSerializerNamespaces ns = new XmlSerializerNamespaces();
                ns.Add("", ""); // In modo tale da ometterli (verranno scritti vuoti)

                /*
                * Siccome vogliamo scrivere solamente il frammento dell'XML contenente i dati da inviare, l'XMLWriter si aspetta che la prima cosa 
                * che viene scritta sia un elemento (ben formato) o un commento, di conseguenza la dichiarazione non è più considerata un costrutto valido:
                * 
                * <Elemento [eventuali attributi=""]>...</Elemento>  {SI}
                * <!-- Commento -->                                  {SI}
                * <?xml version="1.0" encoding="utf-16"?>            {NO}
                * 
                * Tuttavia quando il metodo Serialize del Serializer viene chiamato, la prima cosa
                * che prova a fare è proprio scrivere la dichiarazione all'inzio del documento, questo non è permesso quando il ConformanceLevel è 
                * settato a fragment, quindi parte un eccezione.
                * Per evitare che il serializer tenti di scrivere la dichiarazione e quindi evitare l'eccezione,
                * bisogna che il writer esegui la prima operazione di scrittura, per questo motivo prima di chiamare serialize scrivo uno spazio vuoto.
                */
                xw.WriteWhitespace("");

                serializer.Serialize(xw, this, ns);
            }

            xmlString = sw.ToString();
            return xmlString;
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
    }
}
