/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocole;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author ghor
 */
public class Main {
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
        int error = Integer.parseInt(param.getProperty("error"));
        
        File file = new File(inputDir);
        Support support = new Support(1, 2);
        Station station1 = new Station(support, bufferSize, frameSize, 1);
        Station station2 = new Station(support, bufferSize, frameSize, 2);
 
        station1.setOutputDir(outputDir);
        station2.setOutputDir(outputDir);
         station1.start();
         station2.start();
         support.start();
        station1.sendFile(file, station2.getStationNumber());
 
        
//        FrameGenerator test = new FrameGenerator(file, frameSize);
//        
//        Frame frame1 = test.getFrame(1);
//        Frame frame2 = test.getFrame(2);
//        Frame frame3 = test.getFrame(3);
//        Frame frame4 = test.getFrame(4);
//        
//        frame1.readData();
//        frame2.readData();
//        frame3.readData();
//        frame4.readData();
    }
    
}
