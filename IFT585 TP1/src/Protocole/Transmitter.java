package Protocole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.queue.CircularFifoQueue;

/**
 *
 * @author Quentin
 */
public class Transmitter extends Station implements Runnable {

    String inputDir;
    int frameSize;
    int code;
    int sTimeOut;
    int frameID = 0;
    Support support;
    CircularFifoQueue buffer;

    /**
     *
     * @param inputDir
     * @param frameSize
     * @param code
     * @param sTimeOut
     * @param s
     */
    public Transmitter(String inputDir, int frameSize, int code, int sTimeOut, Support s) {
        this.inputDir = inputDir;
        this.frameSize = frameSize;
        this.code = code;
        this.sTimeOut = sTimeOut;
        this.support = s;
    }

    @Override
    public void run() {
        int bufferSize = (int) Math.pow(2, this.frameSize) - 1;
        buffer = new CircularFifoQueue(bufferSize);

        byte[] data = readFile(this.inputDir);

        while (data.length - this.frameID * this.frameSize > 0) {
            
            
            buffer.add(genFrame(data));
            
            
            if(!support.getReadyDest()){
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Transmitter.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            support.sendFrameDest((Frame) buffer.peek());
 
        }
       
        

    }

    private byte[] readFile(String p) {
        Path path = Paths.get(p);
        byte[] data = null;
        try {
            data = Files.readAllBytes(path);
            for (int i = 0; i < data.length; i++) {
                System.out.println(i + " -> " + data[i]);
            }
            return data;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    /**
     * Générateur de la prochaine frame. La méthode Arrays.copyOfRange permet
     * d'éviter l'étape de remplissage lorsque la taille de fin (stop) excède la
     * taille du tableau, puisqu'il complète automatiquement avec le byte 0.
     *
     * @param data ensemble des bytes du message d'entrée
     * @return prochaine frame générée à partir du compteur
     */
    private Frame genFrame(byte[] data) {
        int start = this.frameID * this.frameSize;
        int stop = (this.frameID + 1) * this.frameSize;
        byte[] frameData = Arrays.copyOfRange(data, start, stop);
        

        
        //TO DO ajouter code de Hamming
        //Hamming.code(frameData);
        
        Frame f = new Frame(this.frameSize, "data", this.frameID, frameData);
        this.frameID++;
        return f;
    }

  
}
