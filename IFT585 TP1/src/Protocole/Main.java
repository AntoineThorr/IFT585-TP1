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
        Support support = new Support(1, 2, error);
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

//        FrameFactory test = new FrameFactory(file, frameSize);
//        Hamming hamming = new Hamming();
//        
//        Frame frame1 = test.getFrame(1);
//        Frame frame2 = test.getFrame(2);
//        Frame frame3 = test.getFrame(3);
//        Frame frame4 = test.getFrame(4);
//        byte[] bytes = new byte[2];
//        bytes[0] = (byte) 'W';
//        bytes[1] = (byte) 'e';
//        
//        boolean[] frame1Bits = new boolean[frameSize * 8];
//        
//        frame1Bits = Hamming.byteToBitArray(frame1.getData());
//        
//        System.out.println(Arrays.toString(frame1.getData()));
//        System.out.println(Arrays.toString(frame1Bits));
//        
//        byte[] bytes2 = new byte[2];
//        bytes2 = hamming.bitToByteArray(frame1Bits);
//        System.out.println(Arrays.toString(bytes2));
//        System.out.println(bytes[0]);
//        System.out.println(bytes[1]);
//        System.out.println(Arrays.toString(bytes));
//        System.out.println(Arrays.toString(frame1Bits));
//        
//        frame1.readData();
//        frame2.readData();
//        frame3.readData();
//        frame4.readData();
    }

}
