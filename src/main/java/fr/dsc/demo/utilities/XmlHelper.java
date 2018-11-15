package fr.dsc.demo.utilities;

import fr.dsc.demo.dao.DemandeDao;
import fr.dsc.demo.dao.FichierTraiteDao;
import fr.dsc.demo.dao.MessageDao;
import fr.dsc.demo.dao.UtilisateurDao;
import fr.dsc.demo.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

public class XmlHelper {
    public final String EXPORTS_PATH = this.getClass().getClassLoader().getResource("static").getPath() + "";

    @Autowired
    MessageDao messageDao;

    @Autowired
    DemandeDao demandeDao;

    @Autowired
    UtilisateurDao utilisateurDao;

    @Autowired
    FichierTraiteDao fichierTraiteDao;

    public String creerDemandeXml(Message message) {
        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter =
                    xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("Fichier");
            xMLStreamWriter.writeStartElement("Header");
            xMLStreamWriter.writeStartElement("FicID");
            xMLStreamWriter.writeCharacters(Util.generateUniqueToken());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("NmIE");
            xMLStreamWriter.writeCharacters(message.getEmetteur().getNom());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("NmIR");
            xMLStreamWriter.writeCharacters(message.getRecepteur().getNom());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("MailExp");
            xMLStreamWriter.writeCharacters(message.getEmetteur().getEmail());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("MailDest");
            xMLStreamWriter.writeCharacters(message.getRecepteur().getEmail());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();
            /**
             * start of body
             */
            xMLStreamWriter.writeStartElement("Body");
            //start of colMess
            xMLStreamWriter.writeStartElement("CollMess");
            xMLStreamWriter.writeAttribute("NbOfTxs", "1");
            //start of message
            xMLStreamWriter.writeStartElement("Message");
            xMLStreamWriter.writeAttribute("MsgId", message.getMsgId());
            xMLStreamWriter.writeStartElement("Dte");
            xMLStreamWriter.writeCharacters(message.getDateEnvoie().toString());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("DureeValideMsg");
            xMLStreamWriter.writeCharacters(message.getDureeValid());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("Dmd");
            xMLStreamWriter.writeStartElement("DescDmd");
            xMLStreamWriter.writeCharacters(message.getDemande().getDescription());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("DateDebut");
            xMLStreamWriter.writeCharacters(message.getDemande().getDateDebut().toString());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("DateFin");
            xMLStreamWriter.writeCharacters(message.getDemande().getDateFin().toString());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();

            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();
            /**
             * end of body
             */
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndDocument();

            xMLStreamWriter.flush();
            xMLStreamWriter.close();

            String xmlString = stringWriter.getBuffer().toString();

            stringWriter.close();


            return xmlString;

        } catch (Exception e) {
            e.printStackTrace();

        }

        return "";
    }

    public String creerPropXml(Message message, String numAuth) {
        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter =
                    xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("Fichier");
            xMLStreamWriter.writeStartElement("Header");
            xMLStreamWriter.writeStartElement("FicID");
            xMLStreamWriter.writeCharacters(Util.generateUniqueToken());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("NmIE");
            xMLStreamWriter.writeCharacters(message.getEmetteur().getNom());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("NmIR");
            xMLStreamWriter.writeCharacters(message.getRecepteur().getNom());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("NumAuto");
            xMLStreamWriter.writeCharacters(numAuth);
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("MailExp");
            xMLStreamWriter.writeCharacters(message.getEmetteur().getEmail());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("MailDest");
            xMLStreamWriter.writeCharacters(message.getRecepteur().getEmail());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();
            /**
             * start of body
             */
            xMLStreamWriter.writeStartElement("Body");
            //start of colMess
            xMLStreamWriter.writeStartElement("CollMess");
            xMLStreamWriter.writeAttribute("NbOfTxs", Integer.toString(message.getTroc().size()));
            //start of message
            xMLStreamWriter.writeStartElement("Message");
            xMLStreamWriter.writeAttribute("MsgId", message.getMsgId());
            xMLStreamWriter.writeStartElement("Dte");
            xMLStreamWriter.writeCharacters(message.getDateEnvoie().toString());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("DureeValideMsg");
            xMLStreamWriter.writeCharacters(message.getDureeValid());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("Prop");
            xMLStreamWriter.writeStartElement("TitreP");
            xMLStreamWriter.writeCharacters(message.getTitreProp());
            xMLStreamWriter.writeEndElement();
            for (int i = 0; i < message.getTroc().size(); i++) {
                List<Objet> listprops;
                if (message.getTroc().get(i).getType().equals(Troc.OFFRE)) {
                    xMLStreamWriter.writeStartElement("Offre");
                    listprops = message.getTroc().get(i).getOffres();
                } else {
                    xMLStreamWriter.writeStartElement("Demande");
                    listprops = message.getTroc().get(i).getDemandes();
                }
                for (int j = 0; j < listprops.size(); j++) {
                    xMLStreamWriter.writeStartElement("Objet");
                    xMLStreamWriter.writeStartElement("Type");
                    xMLStreamWriter.writeCharacters(listprops.get(j).getType());
                    xMLStreamWriter.writeEndElement();
                    xMLStreamWriter.writeStartElement("Description");
                    xMLStreamWriter.writeStartElement("Parametre");
                    xMLStreamWriter.writeStartElement("Nom");
                    xMLStreamWriter.writeCharacters(listprops.get(j).getNom());
                    xMLStreamWriter.writeEndElement();
                    xMLStreamWriter.writeStartElement("Valeur");
                    xMLStreamWriter.writeCharacters(listprops.get(j).getValeur());
                    xMLStreamWriter.writeEndElement();
                    xMLStreamWriter.writeEndElement();
                    xMLStreamWriter.writeEndElement();
                    xMLStreamWriter.writeEndElement();
                }

                xMLStreamWriter.writeEndElement();
            }
            xMLStreamWriter.writeEndElement();

            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();
            /**
             * end of body
             */
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndDocument();

            xMLStreamWriter.flush();
            xMLStreamWriter.close();

            String xmlString = stringWriter.getBuffer().toString();

            stringWriter.close();


            return xmlString;

        } catch (Exception e) {
            e.printStackTrace();

        }

        return "";
    }

    public void creerFichierXml(String path, String xmlString) {
        try {
            ClassPathResource cpr = new ClassPathResource("/static/exports/");
            path = cpr.getFile().getPath() + "/" + path;
            System.out.println("new path: " + path);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

//         Write the parsed document to an xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            File file = new File(path);
            if (file.createNewFile()) {
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validateAgainstXSD(InputStream xml, InputStream xsd) {
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean checkNumAuthAndCheckSum(String xmlPath) {
        try {
            File file = new File(xmlPath);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            int checkSum = Integer.parseInt(((Element) document.getElementsByTagName("CollMess").item(0)).getAttribute("NbOfTxs"));
            NodeList messages = document.getElementsByTagName("Message");
            if (checkSum != messages.getLength())
                return false;
            if(document.getElementsByTagName("NumAuto").getLength()==0)
            {
                if(messages.getLength()!=1 || document.getElementsByTagName("Dmd").getLength()==0)
                    return false;
            }else{
                if(document.getElementsByTagName("Dmd").getLength()!=0)
                    return false;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void processFile(String xmlPath){
        try {

        File file = new File(xmlPath);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        Utilisateur emetteur = utilisateurDao.findByEmail(document.getElementsByTagName("MailExp").item(0).getTextContent()) ;
        Utilisateur recepteur = utilisateurDao.findByEmail(document.getElementsByTagName("MailDest").item(0).getTextContent());
        NodeList messages = document.getElementsByTagName("Message");

            for (int i = 0; i < messages.getLength(); i++) {
                Message message = new Message();
                Node node = messages.item(i);
                Element element = (Element)node;

                message.setEmetteur(emetteur);
                message.setRecepteur(recepteur);
                if(document.getElementsByTagName("Dmd").getLength() == 1){
                    //traitement demande
                    Element demandeElement = (Element) element.getElementsByTagName("Dmd").item(0);
                    String dateDebut = demandeElement.getElementsByTagName("DescDmd").item(0).getTextContent();
                    String dateFin = demandeElement.getElementsByTagName("DateFin").item(0).getTextContent();
                    String descCmd = demandeElement.getElementsByTagName("DescDmd").item(0).getTextContent();
                    if(dateDebut.length()>8 && dateFin.length() > 8 && descCmd.length() > 1 && descCmd .length()<=1000) {
                        Demande demande = new Demande();
                        demande.setDateDebut(new Date(dateDebut));
                        demande.setDateFin(new Date(dateFin));
                        demande.setDescription(descCmd);
                    }

                }else if(document.getElementsByTagName("Auth").getLength() == 1){
                    Demande demande = demandeDao.findByMsgId(element.getAttribute("MsgId"));
                    Element authElement = (Element) element.getElementsByTagName("Auth").item(0);
                    if(authElement.getElementsByTagName("AccAuth").getLength() != 0)
                        demande.setAuth(Demande.ACCEPTE);
                    else if(authElement.getElementsByTagName("RefAuth").getLength() != 0)
                        demande.setAuth(Demande.REFUSE);
                    demandeDao.save(demande);

                }else if(document.getElementsByTagName("Prop").getLength() > 0){

                }else if(document.getElementsByTagName("Accep").getLength()>0){

                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
