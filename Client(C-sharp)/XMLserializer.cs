using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace Client_C_sharp_
{
    internal class XMLserializer
    {
        // Questo metodo verrà principalmente usato per generare automaticamente il mazzo
        public static void SaveLista(string filePath, List<Carta> lista)
        {
            // SALVA SU FILE 
            XmlDocument d = SerializzaLista(lista);

            XmlWriterSettings settings = new XmlWriterSettings
            {
                Indent = true // Indentazione
                              //OmitXmlDeclaration = true // se volessi omettere la dichiarazione xml
            };

            using (XmlWriter writer = XmlWriter.Create(filePath, settings))
            {
                d.Save(writer);
            }
        }

        // Serializza una serie di oggetti Carta in formato XML (al client non dovrebbe servire)
        private static XmlDocument SerializzaLista(List<Carta> lista)
        {
            XmlDocument d = new XmlDocument();
            XmlElement root = d.CreateElement("Carte");
            d.AppendChild(root);

            foreach (Carta c in lista)
            {
                root.AppendChild(c.Serialize(d));
            }

            return d;
        }

        // Legge da file XML e ricava la corrispondente lista di oggetti Carta
        public static List<Carta> Read(string filePath)
        {
            XmlDocument d = new XmlDocument();
            d.Load(filePath);

            List<Carta> lista = new List<Carta>();

            if(d.DocumentElement != null) // Se esiste effettivamente una lista di carte procedi a parsarla, altrimenti ritorna una lista vuota
            {
                XmlElement root = d.DocumentElement; //<Carte>

                XmlNodeList nList = root.GetElementsByTagName("Carta");

                if (nList.Count > 0)
                {
                    foreach (XmlNode node in nList)
                    {
                        if (node is XmlElement carta)
                        {
                            Carta c = new Carta(carta);
                            lista.Add(c);
                        }
                    }
                }
            }
            
            return lista;
        }

        // Come sopra, ma legge da stringa in formato XML invece che file
        public static List<Carta> ReadFromString(string xmlString)
        {
            XmlDocument d = new XmlDocument();
            d.LoadXml(xmlString);

            List<Carta> lista = new List<Carta>();

            /* So per certo che root non sarà mai null, dato che questo metodo lo uso esclusivamente
               per parsare una lista di carte una volta ricevuta dal server, quest'ultimo non invierà
               mai una lista vuota...*/
            XmlElement root = d.DocumentElement; //<Carte>
            XmlNodeList nList = root.GetElementsByTagName("Carta");

            if (nList.Count > 0)
            {
                foreach (XmlNode node in nList) // Per ogni elemento xml carta nella lista
                {
                    if (node is XmlElement carta) // Casto il nodo in elemento
                    {
                        Carta c = new Carta(carta); // Creo un oggetto carta a partire da quell'elemento
                        lista.Add(c);
                    }
                }
            }
            return lista;
        }

        // Come sopra, ma legge da stringa in formato XML invece che file
        public static List<XmlElement> ReadFromStringRawElements(string xmlString)
        {
            XmlDocument d = new XmlDocument();
            d.LoadXml(xmlString);

            List<XmlElement> lista = new List<XmlElement>();

            /* So per certo che root non sarà mai null, dato che questo metodo lo uso esclusivamente
               per parsare una lista di carte una volta ricevuta dal server, quest'ultimo non invierà
               mai una lista vuota...*/
            XmlElement root = d.DocumentElement; //<Carte>
            XmlNodeList nList = root.GetElementsByTagName("Carta");

            if (nList.Count > 0)
            {
                foreach (XmlNode node in nList) // Per ogni elemento xml carta nella lista
                {
                    lista.Add((XmlElement)node);
                }
            }
            return lista;
        }

        // Serve alla finestra di attesa, per capire che istruzioni eseguire in base al comando ricevuto dal server
        public static string getComando(string xmlString)
        {
            XmlDocument d = new XmlDocument();
            d.LoadXml(xmlString);
            XmlElement root = d.DocumentElement; // Il comando inviato dal server è sempre il primo elemento del documento
            return root.Name; // Ritorno il nome del comando
        }

        // Serve ad estrarre l'eventuale argomento del comando ricevuto
        public static string getArgomento(string xmlString)
        {
            XmlDocument d = new XmlDocument();
            d.LoadXml(xmlString);
            XmlElement root = d.DocumentElement;
            return root.InnerText;
        }

        // Metodi meno utilizzati:
        public static string Stringfy(XmlDocument d)
        {
            StringWriter writer = new StringWriter();
            XmlWriterSettings settings = new XmlWriterSettings
            {
                Indent = true // Indentazione
                              //OmitXmlDeclaration = true // se volessi omettere la dichiarazione xml
            };

            using (XmlWriter xmlWriter = XmlWriter.Create(writer, settings))
            {
                d.Save(xmlWriter);
            }

            return writer.ToString();
        }

        public static void SaveOggetto(Carta c, string filePath)
        {
            XmlDocument d = c.Serialize();
            string xmlString = Stringfy(d);

            using (StreamWriter sw = File.AppendText(filePath))
            {
                sw.Write(xmlString);
            }
        }

        public static string ParseTagName(XmlElement oggetto, string tagName)
        {
            XmlNodeList tmp = oggetto.GetElementsByTagName(tagName);
            if (tmp.Count == 0)
                return null;

            return tmp[0].InnerText;
        }

        public static string ParseAttribute(XmlElement oggetto, string attributeName)
        {
            string attribute = oggetto.GetAttribute(attributeName);
            return attribute;
        }
    }
}
