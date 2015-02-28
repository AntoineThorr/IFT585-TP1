package Protocole;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Classe d'exécution du programme
 *
 * @author Antoine Thorr
 */
public class Main {

//    private static String inputDir, outputDir = null;
//    private static int code, windowSize, bufferSize, error, sTimeOut, rTimeOut, reject;
    /**
     * Méthode d'exécution du programme
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Properties param = new Properties();

        // load the properties file using load() and an input stream
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
        int error = Integer.parseInt(param.getProperty("error"));

        
        Support support = new Support(bufferSize,error);
        
        Transmitter transmitter = new Transmitter(inputDir, frameSize, code, sTimeOut, support);
        Receiver receiver = new Receiver(outputDir, frameSize, code, reject, rTimeOut, support);
        
        transmitter.run();
    }
}
