package Protocole;

/**
 *
 * @author Quentin
 */
public class Hamming {
    
    public static void code(byte[] byteArray){
        boolean bitArray[] = byteToBitArray(byteArray);
        int nbData = bitArray.length;
        int nbControl = (int)Math.floor(((Math.log(nbData))/Math.log(2)) + 1); //nbControl = log2(nbdata)
        boolean bitArrayCoded[] = new boolean[nbData+nbControl];
        
        
    }
    
    public static void verify(){
        
    }
    
    public static boolean[] byteToBitArray(byte[] bytes) {
    boolean[] bits = new boolean[bytes.length * 8];
    for (int i = 0; i < bytes.length * 8; i++) {
      if ((bytes[i / 8] & (1 << (7 - (i % 8)))) > 0)
        bits[i] = true;
    }
    return bits;
  }
    
}
