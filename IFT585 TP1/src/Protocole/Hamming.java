package Protocole;

/**
 * Classe de génération du code de Hamming pour les trames créées
 */
public class Hamming {

    /**
     * Fonction d'encodage des données des trames
     *
     * @param byteArray Tableau d'octets à coder
     * @return Un tableau contenant les données ainsi que les bits de contrôle
     */
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

    /**
     * Fonction de décodage des trames encodées avec l'algorithme de Hamming
     *
     * @param byteArray   Tableau d'octets à décoder
     * @param nbDataBytes Nombre d'octets utiles
     * @return Un tableau d'octets ne contenant que les données utiles
     */
    public static byte[] decode(byte[] byteArray, int nbDataBytes) {
        boolean bitArray[] = byteToBitArray(byteArray);
        int nbControl = (int) Math.floor(((Math.log(nbDataBytes * 8)) / Math.log(2)) + 1); //nbControl = log2(nbdata)
        int usefullSize = nbControl + nbDataBytes * 8;//taille sans le remplissage
        int temp;
        //String location = "";
        int errorPos = 0;
        int j = 0;
        boolean bitArrayRes[] = new boolean[nbDataBytes * 8];
        for (int i = 0; i < usefullSize; i++) {

            temp = i + 1;
            if (((temp & -temp) == temp)) {
                if (verify(j, bitArray)) {
                    //location = location + "1";
                    errorPos = errorPos + (1 << j);

                }
                j++;
            }
        }

        //On corrige le bit erroné si besoin
        if (errorPos != 0) {
            System.out.println("Erreur détéctée et corrigée sur le bit de position " + errorPos);
            bitArray[errorPos - 1] = !bitArray[errorPos - 1];
        }

        //Récupération uniquement des bits de données
        int cmpt = 0;
        for (int i = 0; i < usefullSize; i++) {
            temp = i + 1;
            if (!((temp & -temp) == temp)) {
                bitArrayRes[cmpt] = bitArray[i];
                cmpt++;
            }
        }

        return bitToByteArray(bitArrayRes);

    }

    /**
     * Fonction de conversion d'un tableau d'octets en un tableau de bits (booléens)
     * @param bytes Tableau d'octets
     * @return Tableau de bits (booléens)
     */
    public static boolean[] byteToBitArray(byte[] bytes) {
        boolean[] bits = new boolean[bytes.length * 8];
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[i / 8] & (1 << (7 - (i % 8)))) > 0) {
                bits[i] = true;
            }
        }
        return bits;
    }

    /**
     * Fonction de conversion d'un tableau de bits (booléens) en un tableau d'octets
     * @param bits Tableau de bits (booléens)
     * @return Tableau d'octets
     */
    public static byte[] bitToByteArray(boolean[] bits) {
        byte x;
        int size = (int) Math.floor(bits.length / 8);
        boolean filling = false;
        if (bits.length % 8 > 0) {
            size++;
            filling = true;
        }

        byte[] bytes = new byte[size];
        int j = 0;
        for (int i = 0; i < bits.length; i += 8, j++) {
            //Dans le cas ou on est au dernier octet et qu'il y ai besoin de remplissage
            if (filling == true && j == size - 1) {
                int[] bit = new int[8];
                for (int k = 0; k < bits.length % 8; k++) {
                    if (bits[bits.length - bits.length % 8 + k]) {
                        bit[k] = 1;
                    } else {
                        bit[k] = 0;
                    }

                }
                for (int k = (bits.length % 8); k < 8; k++) {
                    bit[k] = 0;

                }
                x = (byte) ((bit[7]) + (bit[6] << 1) + (bit[5] << 2) + (bit[4] << 3) + (bit[3] << 4)
                        + (bit[2] << 5) + (bit[1] << 6) + (bit[0] << 7));

            } else {
                int bit0 = bits[i + 7] ? 1 : 0;
                int bit1 = bits[i + 6] ? 1 : 0;
                int bit2 = bits[i + 5] ? 1 : 0;
                int bit3 = bits[i + 4] ? 1 : 0;
                int bit4 = bits[i + 3] ? 1 : 0;
                int bit5 = bits[i + 2] ? 1 : 0;
                int bit6 = bits[i + 1] ? 1 : 0;
                int bit7 = bits[i] ? 1 : 0;
                x = (byte) ((bit0) + (bit1 * 2) + (bit2 * 4) + (bit3 * 8) + (bit4 * 16)
                        + (bit5 * 32) + (bit6 * 64) + (bit7 * 128));
            }
            bytes[j] = x;
        }
        return bytes;
    }

    /**
     * Fonction de vérification du bon décodage des données
     * @param i 
     * @param bitArray
     * @return 
     */
    private static boolean verify(int i, boolean[] bitArray) {
        boolean res = false;
        for (int j = 0; j < bitArray.length; j++) {
            //System.out.println("pow : " + (Math.pow(2, i)) + ", j+1 = " + (j + 1));
            if (((j + 1) & (1 << i)) != 0) {
                //System.out.println("Calcul de 2^" + i + ", res =" + res + " + " + bitArray[j]);
                res = res ^ bitArray[j];
            }
        }
        //System.out.println(res);
        return res;

    }

}
