package Protocole;

/**
 *
 * @author Quentin
 */
public class Hamming {
    
    public static void code(byte[] byteArray){
        //boolean bitArray[] = byteToBitArray(byteArray);
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
    
    public static byte[] bitToByteArray(boolean[] bits) {
        byte[] bytes = new byte[bits.length / 8];
        int j = 0;
        for (int i = 0 ; i < bits.length ; i += 8 , j++) {
            int bit0 = bits[i + 7] ? 1 : 0;
            int bit1 = bits[i + 6] ? 1 : 0;
            int bit2 = bits[i + 5] ? 1 : 0;
            int bit3 = bits[i + 4] ? 1 : 0;
            int bit4 = bits[i + 3] ? 1 : 0;
            int bit5 = bits[i + 2] ? 1 : 0;
            int bit6 = bits[i + 1] ? 1 : 0;
            int bit7 = bits[i] ? 1 : 0;
            byte x = (byte) ((bit0) + (bit1 * 2) + (bit2 * 4) + (bit3 * 8) + (bit4 * 16)
                    + (bit5 * 32) + (bit6 * 64) + (bit7 * 128));
            bytes[j] = x;
        }
        return bytes;
    }

//        public static int[] byteToBitArray(byte[] bytes) {
//    int[] bits = new int[bytes.length * 8];
//    for (int i = 0; i < bytes.length * 8; i++) {
//      if ((bytes[i / 8] & (1 << (7 - (i % 8)))) > 0){
//        bits[i] = 1;
//    }else {
//          bits[i] = 0;
//      }
//        
//    }
//    return bits;
//  }
    
}
