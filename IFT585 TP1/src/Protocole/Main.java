package Protocole;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    /**
     * Classe principale d'exécution du programmme. Récupération des paramètres
     * depuis un fichier externe dans le dossier ressources
     *
     * @param args Paramètres d'exécutions
     */
    public static void main(String[] args) throws IOException {
        Properties param = new Properties();

        try {
            FileInputStream in = new FileInputStream("ressources/Parameters.properties");
            param.load(in);
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        String inputDir = param.getProperty("inputDir");
        String outputDir = param.getProperty("outputDir");
        int frameSize = Integer.parseInt(param.getProperty("frameSize"));
        int code = Integer.parseInt(param.getProperty("code"));
        int reject = Integer.parseInt(param.getProperty("reject"));
        int bufferSize = Integer.parseInt(param.getProperty("bufferSize"));
        int sTimeOut = Integer.parseInt(param.getProperty("sTimeOut"));
        int rTimeOut = Integer.parseInt(param.getProperty("rTimeOut"));
        int sDelay = Integer.parseInt(param.getProperty("sDelay"));
        int error = Integer.parseInt(param.getProperty("error"));

        File file = new File(inputDir);
        Support support = new Support(1, 2, sDelay, error);
        Station station1 = new Station(support, bufferSize, frameSize, 1);
        Station station2 = new Station(support, bufferSize, frameSize, 2);

        station1.setOutputDir(outputDir);
        station2.setOutputDir(outputDir);
        station1.setTempo(sTimeOut);
        station2.setTempo(rTimeOut);
        station1.start();
        station2.start();
        support.start();
        station1.sendFile(file, station2.getStationNumber());
    }
}
