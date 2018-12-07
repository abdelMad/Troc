package fr.dsc.demo.utilities;

import fr.dsc.demo.dao.*;
import fr.dsc.demo.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Configurable
public class XmlHelper {
    public final String EXPORTS_PATH = this.getClass().getClassLoader().getResource("static").getPath() + "";

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private DemandeDao demandeDao;

    @Autowired
    private UtilisateurDao utilisateurDao;

    @Autowired
    private FichierDao fichierDao;

    @Autowired
    private ObjetDao objetDao;

    @Autowired
    private TrocDao trocDao;

    @Autowired
    private NotifDao notifDao;

    private XMLStreamWriter __creerHeadFichier(Fichier fichier, XMLStreamWriter xMLStreamWriter, String numAuth) throws XMLStreamException {
        xMLStreamWriter.writeStartElement("Header");
        xMLStreamWriter.writeStartElement("FicID");
        xMLStreamWriter.writeCharacters(fichier.getFicId());
        xMLStreamWriter.writeEndElement();
        xMLStreamWriter.writeStartElement("NmIE");
        xMLStreamWriter.writeCharacters(fichier.getEmetteur().getNom());
        xMLStreamWriter.writeEndElement();
        xMLStreamWriter.writeStartElement("NmIR");
        xMLStreamWriter.writeCharacters(fichier.getRecepteur().getNom());
        xMLStreamWriter.writeEndElement();
        if (numAuth.length() > 1) {
            xMLStreamWriter.writeStartElement("NumAuto");
            xMLStreamWriter.writeCharacters(numAuth);
            xMLStreamWriter.writeEndElement();
        }
        xMLStreamWriter.writeStartElement("MailDest");
        xMLStreamWriter.writeCharacters(fichier.getRecepteur().getEmail());
        xMLStreamWriter.writeEndElement();
        xMLStreamWriter.writeStartElement("MailExp");
        xMLStreamWriter.writeCharacters(fichier.getEmetteur().getEmail());
        xMLStreamWriter.writeEndElement();
        xMLStreamWriter.writeEndElement();
        return xMLStreamWriter;
    }

    public String creerDemandeXml(Fichier fichier) {
        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter =
                    xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("Fichier");
            __creerHeadFichier(fichier, xMLStreamWriter, "");
            /**
             * start of body
             */
            xMLStreamWriter.writeStartElement("Body");
            //start of colMess
            xMLStreamWriter.writeStartElement("CollMess");
            xMLStreamWriter.writeAttribute("NbOfTxs", "1");
            //start of message
            xMLStreamWriter.writeStartElement("Message");
            xMLStreamWriter.writeAttribute("MsgId", fichier.getMessages().get(0).getMsgId());
            xMLStreamWriter.writeStartElement("Dte");
            xMLStreamWriter.writeCharacters(fichier.getMessages().get(0).getDateEnvoie().toString());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("DureeValideMsg");
            xMLStreamWriter.writeCharacters(fichier.getMessages().get(0).getDureeValid());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("Dmd");
            xMLStreamWriter.writeStartElement("DescDmd");
            xMLStreamWriter.writeCharacters(fichier.getMessages().get(0).getDemande().getDescription());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("DateDebut");
            xMLStreamWriter.writeCharacters(fichier.getMessages().get(0).getDemande().getDateDebut().toString());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("DateFin");
            xMLStreamWriter.writeCharacters(fichier.getMessages().get(0).getDemande().getDateFin().toString());
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

    public String creerPropXml(Fichier fichier, String numAuth) {
        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter =
                    xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("Fichier");
            __creerHeadFichier(fichier, xMLStreamWriter, numAuth);
            /**
             * start of body
             */
            xMLStreamWriter.writeStartElement("Body");
            //start of colMess
            xMLStreamWriter.writeStartElement("CollMess");
            xMLStreamWriter.writeAttribute("NbOfTxs", Integer.toString(fichier.getMessages().size()));
            //start of message
            for (int i = 0; i < fichier.getMessages().size(); i++) {
                Message message = fichier.getMessages().get(i);
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
                xMLStreamWriter.writeCharacters(message.getTroc().getTitre());
                xMLStreamWriter.writeEndElement();
                List<Objet> listprops;
                if (message.getTroc().getType().equals(Troc.OFFRE)) {
                    xMLStreamWriter.writeStartElement("Offre");
                    listprops = message.getTroc().getOffres();
                } else {
                    xMLStreamWriter.writeStartElement("Demande");
                    listprops = message.getTroc().getDemandes();
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
                xMLStreamWriter.writeEndElement();

                xMLStreamWriter.writeEndElement();
            }

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

    public String creerAuth(Fichier fichier) {
        try {
            Demande demande = fichier.getMessages().get(0).getDemande();
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter =
                    xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("Fichier");
            __creerHeadFichier(fichier, xMLStreamWriter, demande.getNumAuth());
            //start of body
            xMLStreamWriter.writeStartElement("Body");
            //start of colMess
            xMLStreamWriter.writeStartElement("CollMess");
            xMLStreamWriter.writeAttribute("NbOfTxs", "1");
            xMLStreamWriter.writeStartElement("Message");
            xMLStreamWriter.writeAttribute("MsgId", fichier.getMessages().get(0).getMsgId());
            xMLStreamWriter.writeStartElement("Dte");
            xMLStreamWriter.writeCharacters(fichier.getMessages().get(0).getDateEnvoie().toString());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("DureeValideMsg");
            xMLStreamWriter.writeCharacters(fichier.getMessages().get(0).getDureeValid());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("Auth");
            xMLStreamWriter.writeStartElement("Rep");
            if (Demande.ACCEPTE.equals(demande.getAuth())) {
                xMLStreamWriter.writeStartElement("AccAuth");
                xMLStreamWriter.writeEndElement();
            } else if (Demande.REFUSE.equals(demande.getAuth())) {
                xMLStreamWriter.writeStartElement("RefAuth");
                xMLStreamWriter.writeEndElement();
            }
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();
            //end of colMess
            xMLStreamWriter.writeEndElement();
            //end of body
            xMLStreamWriter.writeEndElement();
            //end of fichier
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

    public String creerAccep(Fichier fichier) {
        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter =
                    xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("Fichier");
            __creerHeadFichier(fichier, xMLStreamWriter, demandeDao.getNumAuthByEmail(fichier.getEmetteur().getEmail(), fichier.getRecepteur().getEmail()));
            //start of body
            xMLStreamWriter.writeStartElement("Body");
            //start of colMess
            xMLStreamWriter.writeStartElement("CollMess");
            xMLStreamWriter.writeAttribute("NbOfTxs", "1");
            xMLStreamWriter.writeStartElement("Message");
            xMLStreamWriter.writeAttribute("MsgId", fichier.getMessages().get(0).getMsgId());
            xMLStreamWriter.writeStartElement("Dte");
            xMLStreamWriter.writeCharacters(fichier.getMessages().get(0).getDateEnvoie().toString());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("DureeValideMsg");
            xMLStreamWriter.writeCharacters(fichier.getMessages().get(0).getDureeValid());
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("Accep");
            xMLStreamWriter.writeStartElement("MessageValid");
            // ToDo add message later
            xMLStreamWriter.writeCharacters("message a ajouter apres");
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();
            //end of colMess
            xMLStreamWriter.writeEndElement();
            //end of body
            xMLStreamWriter.writeEndElement();
            //end of fichier
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

    public String creerReponseProp(Fichier fichier, Fichier newFichier, String numAuth) {
        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter =
                    xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("Fichier");
            __creerHeadFichier(newFichier, xMLStreamWriter, numAuth);
            //start of body
            xMLStreamWriter.writeStartElement("Body");
            //start of colMess
            xMLStreamWriter.writeStartElement("CollMess");
            xMLStreamWriter.writeAttribute("NbOfTxs", Integer.toString(fichier.getMessages().size()));
            for (int i = 0; i < fichier.getMessages().size(); i++) {
                Message message = fichier.getMessages().get(i);
                xMLStreamWriter.writeStartElement("Message");
                xMLStreamWriter.writeAttribute("MsgId", message.getMsgId());
                xMLStreamWriter.writeStartElement("Dte");
                xMLStreamWriter.writeCharacters(message.getDateEnvoie().toString());
                xMLStreamWriter.writeEndElement();
                xMLStreamWriter.writeStartElement("DureeValideMsg");
                xMLStreamWriter.writeCharacters(message.getDureeValid());
                xMLStreamWriter.writeEndElement();
                xMLStreamWriter.writeStartElement("Accep");
                if (message.getTroc().getStatus().equals(Troc.MSG)) {
                    xMLStreamWriter.writeStartElement("MessageValid");
                    xMLStreamWriter.writeCharacters(message.getTroc().getMsgValid());
                    xMLStreamWriter.writeEndElement();
                } else if (message.getTroc().getStatus().equals(Troc.CONTRE_PROP)) {
                    Troc troc = trocDao.getContreProposByPropo(message.getTroc().getId());
                    xMLStreamWriter.writeStartElement("ContreProp");
                    xMLStreamWriter.writeStartElement("Prop");
                    xMLStreamWriter.writeStartElement("TitreP");
                    xMLStreamWriter.writeCharacters(troc.getTitre());
                    xMLStreamWriter.writeEndElement();
                    List<Objet> listprops;
                    if (troc.getType().equals(Troc.OFFRE)) {
                        xMLStreamWriter.writeStartElement("Offre");
                        listprops = troc.getOffres();
                    } else {
                        xMLStreamWriter.writeStartElement("Demande");
                        listprops = troc.getDemandes();
                    }
                    for (int k = 0; k < listprops.size(); k++) {
                        xMLStreamWriter.writeStartElement("Objet");
                        xMLStreamWriter.writeStartElement("Type");
                        xMLStreamWriter.writeCharacters(listprops.get(k).getType());
                        xMLStreamWriter.writeEndElement();
                        xMLStreamWriter.writeStartElement("Description");
                        xMLStreamWriter.writeStartElement("Parametre");
                        xMLStreamWriter.writeStartElement("Nom");
                        xMLStreamWriter.writeCharacters(listprops.get(k).getNom());
                        xMLStreamWriter.writeEndElement();
                        xMLStreamWriter.writeStartElement("Valeur");
                        xMLStreamWriter.writeCharacters(listprops.get(k).getValeur());
                        xMLStreamWriter.writeEndElement();
                        xMLStreamWriter.writeEndElement();
                        xMLStreamWriter.writeEndElement();
                        xMLStreamWriter.writeEndElement();
                    }
                    xMLStreamWriter.writeEndElement();
                    xMLStreamWriter.writeEndElement();
                    xMLStreamWriter.writeEndElement();
                }
                xMLStreamWriter.writeEndElement();
                xMLStreamWriter.writeEndElement();

            }
            //end of colMess
            xMLStreamWriter.writeEndElement();
            //end of body
            xMLStreamWriter.writeEndElement();
            //end of fichier
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

    public Document loadXmlFromPath(String xmlPath) throws Exception {
        File file = new File(xmlPath);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        return document;
    }

    public boolean checkNumAuthAndCheckSum(String xmlPath) {
        try {
            Document document = loadXmlFromPath(xmlPath);
            int checkSum = Integer.parseInt(((Element) document.getElementsByTagName("CollMess").item(0)).getAttribute("NbOfTxs"));
            NodeList messages = document.getElementsByTagName("Message");
            if (checkSum != messages.getLength()) {
                System.out.println("bnMessage != colmsg");
                return false;
            }

            if (document.getElementsByTagName("NumAuto").getLength() == 0) {
                if (messages.getLength() != 1 || document.getElementsByTagName("Dmd").getLength() == 0) {
                    System.out.println("problem num autho");
                    return false;
                }
            } else {

                if (document.getElementsByTagName("Dmd").getLength() != 0) {
                    System.out.println("cant have num autho in dmd");
                    return false;
                }
                if (document.getElementsByTagName("Auth").getLength() == 0) {
                    String numAuth = demandeDao.getNumAuthByEmail(document.getElementsByTagName("MailDest").item(0).getTextContent(), document.getElementsByTagName("MailExp").item(0).getTextContent());
                    System.out.println("numAuth: " + numAuth);
                    if (numAuth == null || numAuth.length() == 0) {
                        System.out.println("NumAUth non dispo :/ error");
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String getFicId(String xmlPath) {
        try {
            Document document = loadXmlFromPath(xmlPath);
            return document.getElementsByTagName("FicID").item(0).getTextContent();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void processFile(String xmlPath) {
        try {
            Document document = loadXmlFromPath(xmlPath);
            if (document.getElementsByTagName("MailExp").item(0) != null && document.getElementsByTagName("MailDest").item(0) != null) {
                String emetMail = document.getElementsByTagName("MailExp").item(0).getTextContent();
                String recepMail = document.getElementsByTagName("MailDest").item(0).getTextContent();
                Utilisateur emetteur = utilisateurDao.findByEmail(emetMail);
                Utilisateur recepteur = utilisateurDao.findByEmail(recepMail);
                System.out.println("Emeteur: " + emetMail);
                System.out.println("Recepteur: " + recepMail);
                System.out.println("emeteur is null?: "+(emetteur == null));
                System.out.println("recepteur is null?: "+(recepteur == null));
                if (emetteur == null) {
                    emetteur = new Utilisateur();
                    emetteur.setNom(document.getElementsByTagName("NmIE").item(0).getTextContent());
                    emetteur.setEmail(emetMail);
                    emetteur.setPrenom(" ");
                    emetteur.setMdp(Util.generateUniqueToken());
                    emetteur.setRegisterDate(new Date());
                }
                if (recepteur == null) {
                    recepteur = new Utilisateur();
                    recepteur.setNom(document.getElementsByTagName("NmIR").item(0).getTextContent());
                    recepteur.setEmail(recepMail);
                    recepteur.setPrenom(" ");
                    recepteur.setMdp(Util.generateUniqueToken());
                    recepteur.setRegisterDate(new Date());

                }
                NodeList messages = document.getElementsByTagName("Message");
                Date now = new Date();
                SimpleDateFormat sDate;
                Calendar calendar = Calendar.getInstance();


                Fichier fichier = new Fichier();
                fichier.setFicId(document.getElementsByTagName("FicID").item(0).getTextContent());
                fichier.setDateCreation(now);


                List<Message> messageList = new ArrayList<>();
                for (int i = 0; i < messages.getLength(); i++) {
                    Node node = messages.item(i);
                    System.out.println("Current element: " + node.getNodeName());
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;


                        Message message = new Message();
                        message.setFichier(fichier);
                        message.setMsgId(((Element) document.getElementsByTagName("FicID").item(0)).getAttribute("MsgId"));
                        message.setDureeValid(document.getElementsByTagName("DureeValideMsg").item(0).getTextContent());
                        sDate = new SimpleDateFormat(Util.determineDateFormat(((Element) node).getElementsByTagName("Dte").item(0).getTextContent()));
                        Date dateEnvoie = sDate.parse(((Element) node).getElementsByTagName("Dte").item(0).getTextContent());
                        message.setDateEnvoie(dateEnvoie);
                        calendar.setTime(dateEnvoie);
                        calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(element.getElementsByTagName("DureeValideMsg").item(0).getTextContent()));
                        message.setDateExpiration(calendar.getTime());
                        message.setMsgId(element.getAttribute("MsgId"));

                        if (element.getElementsByTagName("Dmd").getLength() == 1) {

                            System.out.println("Im in demande");
                            //traitement demande
                            Element demandeElement = (Element) element.getElementsByTagName("Dmd").item(0);
                            String dateDebut = demandeElement.getElementsByTagName("DateDebut").item(0).getTextContent();
                            String dateFin = demandeElement.getElementsByTagName("DateFin").item(0).getTextContent();
                            String descCmd = demandeElement.getElementsByTagName("DescDmd").item(0).getTextContent();
                            System.out.println("**-->"+dateDebut.length());
                            System.out.println("**-->"+dateFin.length());
                            System.out.println("**-->"+descCmd.length());
                            System.out.println((dateDebut.length() > 8 && dateFin.length() > 8 && descCmd.length() > 1 && descCmd.length() <= 1000));
                            if (dateDebut.length() > 8 && dateFin.length() > 8 && descCmd.length() > 1 && descCmd.length() <= 1000) {

                                Demande demande = new Demande();

                                message.setType(Message.DEMANDE);
                                sDate = new SimpleDateFormat(Util.determineDateFormat(dateDebut));
                                demande.setDateDebut(sDate.parse(dateDebut));
                                sDate = new SimpleDateFormat(Util.determineDateFormat(dateFin));
                                demande.setDateFin(sDate.parse(dateFin));
                                demande.setDescription(descCmd);
                                message.setDemande(demande);
                                demandeDao.save(demande);
                                fichierDao.save(fichier);
                                messageDao.save(message);
                                System.out.println("demande cree");
                                Notification notification = new Notification();
                                notification.setMessage("Demande recus de la part de: " + emetMail);
                                notification.setUrl("/mes-demandes/recus");
                                notification.setUtilisateur(recepteur);
                                utilisateurDao.save(recepteur);
                                utilisateurDao.save(emetteur);
                                notifDao.save(notification);
                            }

                        } else if (element.getElementsByTagName("Auth").getLength() == 1) {
                            System.out.println("In Auth bloc");
                            Demande demande = demandeDao.findByMsgIdAndEmails(element.getAttribute("MsgId"), emetMail);
                            System.out.println("Msg Id: " + element.getAttribute("MsgId"));
                            System.out.println("emetMail: " + emetMail);
                            if(demande == null) {
                                System.out.println("demande n existe pas pour pouvoir l accepter");
                                return;
                            }
                            if (!demande.getAuth().equals("0")) {
                                System.out.println("**** Auth deja traite *****");
                                return;
                            }
                            Element authElement = (Element) element.getElementsByTagName("Auth").item(0);
                            if ( authElement.getElementsByTagName("AccAuth").getLength() != 0) {
                                demande.setNumAuth(document.getElementsByTagName("NumAuto").item(0).getTextContent());
                                demande.setAuth(Demande.ACCEPTE);
                            } else if (authElement.getElementsByTagName("RefAuth").getLength() != 0)
                                demande.setAuth(Demande.REFUSE);
                            demandeDao.save(demande);
                            System.out.println("validation reussie");
                            Notification notification = new Notification();
                            notification.setMessage("Votre demande avec " + emetMail + " est " + (demande.getAuth().equals(Demande.ACCEPTE) ? "acceptee" : "refusee"));
                            notification.setUrl("/mes-demandes/recus");
                            notification.setUtilisateur(recepteur);
                            utilisateurDao.save(recepteur);
                            utilisateurDao.save(emetteur);
                            notifDao.save(notification);
                            return;

                        } else if (element.getElementsByTagName("Prop").getLength() > 0 && document.getElementsByTagName("Accep").getLength() == 0) {
                            System.out.println("Im in proposition");
                            message.setType(Message.PROPOSITION);
                            Troc troc = new Troc();
                            System.out.println(element);
                            System.out.println(element.getChildNodes());
                            Node propNode = element.getElementsByTagName("Prop").item(0);
                            Element propElement = (Element) propNode;

                            if (propElement.getElementsByTagName("Demande").getLength() == 1) {
                                troc.setType(Troc.DEMANDE);
                            } else if (propElement.getElementsByTagName("Offre").getLength() == 1) {
                                troc.setType(Troc.OFFRE);
                            }
                            NodeList objets = propElement.getElementsByTagName("Objet");
                            List<Objet> objetList = new ArrayList<>();
                            for (int j = 0; j < objets.getLength(); j++) {
                                Element objEl = (Element) objets.item(j);
                                Objet objet = new Objet();
                                objet.setNom(objEl.getElementsByTagName("Nom").item(0).getTextContent());
                                objet.setType(objEl.getElementsByTagName("Type").item(0).getTextContent());
                                objet.setValeur(objEl.getElementsByTagName("Valeur").item(0).getTextContent());
                                if (troc.getType().equals(Troc.OFFRE)) {
                                    objet.setOffre(troc);
                                } else if (Troc.DEMANDE.equals(troc.getType())) {
                                    objet.setDemande(troc);
                                }
                                objetList.add(objet);
                                System.out.println("Objet creer");
                            }
                            System.out.println("Il y a nb demande: " + propElement.getElementsByTagName("Demande").getLength());
                            System.out.println("Il y a nb Offre: " + propElement.getElementsByTagName("Offre").getLength());
                            if (troc.getType().equals(Troc.OFFRE)) {
                                troc.setDemandes(objetList);
                            } else if (Troc.DEMANDE.equals(troc.getType())) {
                                troc.setOffres(objetList);
                            }
                            fichierDao.save(fichier);
                            trocDao.save(troc);
                            message.setTroc(troc);
                            objetDao.saveAll(objetList);
                            messageDao.save(message);
                            messageList.add(message);
                            Notification notification = new Notification();
                            notification.setMessage("Proposition recus de la part de: " + emetMail);
                            notification.setUrl("/mes-proposition/recus");
                            notification.setUtilisateur(recepteur);
                            utilisateurDao.save(recepteur);
                            utilisateurDao.save(emetteur);
                            notifDao.save(notification);
                            System.out.println("Done.");
                        } else if (document.getElementsByTagName("Accep").getLength() > 0) {
                            Element accep = (Element) document.getElementsByTagName("Accep");
                            NodeList messageValid = accep.getElementsByTagName("MessageValid");
                            NodeList contreProp = accep.getElementsByTagName("ContreProp");
                            Message msgSrc = messageDao.findByMsgId(element.getAttribute("MsgId")).get(0);

                            if (messageValid.getLength() == 1) {
                                if (msgSrc != null) {
                                    Troc troc = msgSrc.getTroc();
                                    troc.setMsgValid(messageValid.item(0).getTextContent());
                                    troc.setStatus(Troc.MSG);
                                    trocDao.save(troc);
                                }
                            } else if (contreProp.getLength() == 1) {
                                if (msgSrc != null) {
                                    System.out.println("Im in proposition");
                                    message.setType(Message.PROPOSITION);
                                    Troc troc = new Troc();
                                    System.out.println(element);
                                    System.out.println(element.getChildNodes());
                                    Node propNode = element.getElementsByTagName("Prop").item(0);
                                    Element propElement = (Element) propNode;
                                    message.setMsgId(msgSrc.getMsgId());
                                    troc.setParent(msgSrc.getTroc());
                                    if (propElement.getElementsByTagName("Demande").getLength() == 1) {
                                        troc.setType(Troc.DEMANDE);
                                    } else if (propElement.getElementsByTagName("Offre").getLength() == 1) {
                                        troc.setType(Troc.OFFRE);
                                    }
                                    NodeList objets = propElement.getElementsByTagName("Objet");
                                    List<Objet> objetList = new ArrayList<>();
                                    for (int j = 0; j < objets.getLength(); j++) {
                                        Element objEl = (Element) objets.item(j);
                                        Objet objet = new Objet();
                                        objet.setNom(objEl.getElementsByTagName("Nom").item(0).getTextContent());
                                        objet.setType(objEl.getElementsByTagName("Type").item(0).getTextContent());
                                        objet.setValeur(objEl.getElementsByTagName("Valeur").item(0).getTextContent());
                                        if (troc.getType().equals(Troc.OFFRE)) {
                                            objet.setOffre(troc);
                                        } else if (Troc.DEMANDE.equals(troc.getType())) {
                                            objet.setDemande(troc);
                                        }
                                        objetList.add(objet);
                                        System.out.println("Objet creer");
                                    }
                                    System.out.println("Il y a nb demande: " + propElement.getElementsByTagName("Demande").getLength());
                                    System.out.println("Il y a nb Offre: " + propElement.getElementsByTagName("Offre").getLength());
                                    if (troc.getType().equals(Troc.OFFRE)) {
                                        troc.setDemandes(objetList);
                                    } else if (Troc.DEMANDE.equals(troc.getType())) {
                                        troc.setOffres(objetList);
                                    }
                                    fichierDao.save(fichier);
                                    trocDao.save(troc);
                                    message.setTroc(troc);
                                    objetDao.saveAll(objetList);
                                    messageDao.save(message);
                                    messageList.add(message);
                                    Notification notification = new Notification();
                                    notification.setMessage("vous avez eu une reponse apropos de votre Proposition avec: " + emetMail);
                                    notification.setUrl("/mes-proposition/envoye");
                                    notification.setUtilisateur(recepteur);
                                    utilisateurDao.save(recepteur);
                                    utilisateurDao.save(emetteur);
                                    notifDao.save(notification);
                                    System.out.println("Done.");
                                }
                            }

                        }
                    }
                }

                utilisateurDao.save(recepteur);
                utilisateurDao.save(emetteur);
                fichier.setEmetteur(emetteur);
                fichier.setRecepteur(recepteur);
                fichier.setMessages(messageList);
                fichierDao.save(fichier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
