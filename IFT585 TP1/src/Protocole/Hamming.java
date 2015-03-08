package Protocole;

import java.util.Arrays;

/**
 *
 * @author Quentin
 */
public class Hamming {

  public static byte[] code(byte[] byteArray) {
    //boolean bitArray[] = byteToBitArray(byteArray);
    boolean bitArray[] = byteToBitArray(byteArray);
    
    int nbData = bitArray.length;
    int nbControl = (int) Math.floor(((Math.log(nbData)) / Math.log(2)) + 1); //nbControl = log2(nbdata)
    boolean bitArrayCoded[] = new boolean[nbData + nbControl];

    int j = 0;
    int temp;
    //On place les bits de données
    for (int i = 0; i < bitArrayCoded.length; i++) {

      //Si i n'est oas une puissance de 2 (pas un bit de controle)) on place le bit de données
      temp = i + 1;
      if (!((temp & -temp) == temp)) {
        bitArrayCoded[i] = bitArray[j];
        j++;

      }
    }
    //Une fois les bit de données placés, on calcul chacun des bits de controle
    for (int i = 0; i < bitArrayCoded.length; i++) {
      temp = i + 1;
      //Si c'est un bit de controle :
      if (((temp & -temp) == temp)) {
        /*
         On parcour l'ensemble des bit du tableau et si sa position correspond 
        a une valeur qui contient la puissance de 2 du bit qu'on est en train 
        de calculer alors on l'ajoute avec un XOR 
         */
        for (int k = 0; k < bitArrayCoded.length; k++) {

          if (((k + 1) & temp) != 0) {

            bitArrayCoded[i] = bitArrayCoded[i] ^ bitArrayCoded[k];
          }
        }

      }
    }
    return bitToByteArray(bitArrayCoded);

  }

  public static void verify() {

  }

  public static boolean[] byteToBitArray(byte[] bytes) {
    boolean[] bits = new boolean[bytes.length * 8];
    for (int i = 0; i < bytes.length * 8; i++) {
      if ((bytes[i / 8] & (1 << (7 - (i % 8)))) > 0) {
        bits[i] = true;
      }
    }
    return bits;
  }

  public static byte[] bitToByteArray(boolean[] bits) {
    byte[] bytes = new byte[bits.length / 8];
    int j = 0;
    for (int i = 0; i < bits.length; i += 8, j++) {
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

}
