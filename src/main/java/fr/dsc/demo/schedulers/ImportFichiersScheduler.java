package fr.dsc.demo.schedulers;

import fr.dsc.demo.dao.FichierDao;
import fr.dsc.demo.models.Message;
import fr.dsc.demo.utilities.XmlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

@Component
public class ImportFichiersScheduler {

    @Autowired
    FichierDao fichierDao;
    private int cpt = 0;
    @Autowired
    private XmlHelper xmlHelper;

    @Scheduled(fixedRate = 30000)
    public void process() {
        System.out.println("Im here hahahahoho " + (cpt++));
        try {

            ClassPathResource cpr = new ClassPathResource("/static/imports/");
            ClassPathResource cprXsd = new ClassPathResource("/static/XSD_MTBC.xsd");
            final File folder = cpr.getFile();
            System.out.println(cpr.getPath());
            System.out.println(cpr.getFile().getPath());
            if (folder.isDirectory()) {
                for (final File fileEntry : folder.listFiles()) {
                    System.out.println("file name: " + fileEntry.getName());
                    if ((int) fileEntry.getTotalSpace() <= Message.MAX_SIZE) {
                        if (fichierDao.existsByFicId(xmlHelper.getFicId(fileEntry.getPath())) == 0) {
                            if (xmlHelper.validateAgainstXSD(new DataInputStream(new FileInputStream(fileEntry)), new DataInputStream(new FileInputStream(cprXsd.getFile())))) {
                                System.out.println("Valiiiiid");
                                if (xmlHelper.checkNumAuthAndCheckSum(fileEntry.getPath())) {
                                    System.out.println("test passed");
                                    xmlHelper.processFile(fileEntry.getPath());
                                } else {
                                    System.out.println("test not passed ...");
                                }
                            } else
                                System.out.println("not valid :(");
                        } else
                            System.out.println("fichier deja traite ");
                    } else
                        System.out.println("Max Size passed sry bb");

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
