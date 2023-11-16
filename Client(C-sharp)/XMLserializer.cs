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

        private static XmlDocument SerializzaLista(List<Carta> lista)
        {
            XmlDocument d = new XmlDocument();
            XmlElement root = d.CreateElement("Carte");
            d.AppendChild(root);

            foreach (Carta o in lista)
            {
                root.AppendChild(o.Serialize(d));
            }

            return d;
        }

        public static List<Carta> Read(string filePath)
        {
            XmlDocument d = new XmlDocument();
            d.Load(filePath);

            List<Carta> lista = new List<Carta>();

            XmlNodeList nList = d.GetElementsByTagName("Carte");

            if (nList.Count > 0)
            {
                foreach (XmlNode node in nList)
                {
                    if (node is XmlElement carta)
                    {
                        Carta o = new Carta(carta);
                        lista.Add(o);
                    }
                }
            }

            return lista;
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

        public static void SaveOggetto(Carta c, string filePath)
        {
            XmlDocument d = c.Serialize();
            string xmlString = Stringfy(d);

            using (StreamWriter sw = File.AppendText(filePath))
            {
                sw.Write(xmlString);
            }
        }

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
    }
}
