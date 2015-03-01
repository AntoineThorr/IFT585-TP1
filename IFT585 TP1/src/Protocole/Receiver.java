package Protocole;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.apache.commons.lang3.ArrayUtils.toPrimitive;

/**
 *
 * @author Quentin
 */
public class Receiver extends Station implements Runnable {

    String outputDir;
    int frameSize;
    int code;
    int reject;
    int rTimeOut;
    Support support;
    ArrayList<Byte> fileBuffer;

    Receiver(String outputDir, int frameSize, int code, int reject, int rTimeOut, Support s) {
        this.outputDir = outputDir;
        this.frameSize = frameSize;
        this.code = code;
        this.reject = reject;
        this.rTimeOut = rTimeOut;
        this.support = s;
        this.fileBuffer = new ArrayList();
    }

    @Override
    public void run() {
        // http://www.tutorialspoint.com/java/java_thread_synchronization.htm
        // http://openclassrooms.com/courses/apprenez-a-programmer-en-java/executer-des-taches-simultanement
        synchronized (this.support) { //A vérifier
            if (!support.getReceivedDest()) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    System.out.println("Notification depuis le support ?");
                }
            }
        }
        Frame f = support.getFrameDest();
        for (int i = 0; i < f.getData().length; i++) {
            this.fileBuffer.add(f.getData()[i]);
        }
        this.writeToFile();
    }
    
    private void writeToFile() {
        byte[] data = toPrimitive(fileBuffer.toArray(new Byte[fileBuffer.size()])); //Conversion de l'ArrayList<Byte> en byte[]
        
        //Conversion en string et affichage console
        try {
            String output = new String(data,"UTF-8");
            System.out.println(output);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        
        //Ecriture du fichier de sortie
        try {
            Files.write(Paths.get(this.outputDir), data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
