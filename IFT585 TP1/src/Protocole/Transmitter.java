/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quentin
 */
public class Transmitter extends Station implements Runnable{
    
    private String inputDir;
    private int frameSize;
    private int code;
    private int sTimeOut;
   
    
    public Transmitter(String inputDir, int frameSize, int code, int sTimeOut) {
        this.inputDir = inputDir;
        this.frameSize = frameSize;
        this.code = code;
        this.sTimeOut = sTimeOut;
        
            
        
    }

    @Override
    public void run() {
        readFile(inputDir);
    }

    private void readFile(String p) {
       Path path = Paths.get(p);
        byte[] data;
        try {
            data = Files.readAllBytes(path);
               for(int i = 0; i < data.length; i++ ){
                   System.err.println(data[i]);
               }
            
        }
            
        catch (IOException ex) {
            Logger.getLogger(Transmitter.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }
    
}
