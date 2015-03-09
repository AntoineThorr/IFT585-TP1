package Protocole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

/*
 Objet permettant de diviser un fichier en une série de trames.
 */
/**
 *
 * @author Antoine
 */
public class FrameFactory {

    // Taille d'une trame
    private int frameSize;

    // Liste contenant les trames du fichier
    private ArrayList<Frame> dataBlocks;

    // Compteur des trames (identifiants)
    private int frameCount = 0;

    // 
    /**
     * Constructeur de la classe FrameFactory. On divise le fichier en trames
     * dès la création de la Factory, stockées dans un l'ArrayList dataBlocks.
     * On peut ensuite accéder aux trames avec un simple get.
     *
     * @param file      Fichier déterminé en entrée
     * @param frameSize Nombre d'octets de données souhaités par trame
     */
    public FrameFactory(File file, int frameSize) {
        // Contient en ordre tous les blocks de données créés à partir du fichier
        this.dataBlocks = new ArrayList<>();

        // Taille d'une trame tel que défini dans les propriétés.
        this.frameSize = frameSize;

        //Création du tableau total d'octets du fichier
        byte[] data = readFile(file);
        //Itération tant que reste des octets à lire
        while (data.length - this.frameCount * this.frameSize > 0) {
            //Ajout à l'arrayList de chaque frame à partir du tableau d'octets et du compteur de trames
            this.dataBlocks.add(this.genFrame(data));
        }
    }

    /**
     * Méthode de lecture d'un fichier octet par octet
     *
     * @param f Fichier d'entrée spécifié
     * @return Un tableau contenant tous les octets du fichier au format byte[]
     */
    private byte[] readFile(File f) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(f.toPath()); //Enregistrement dans le format "byte" de Java : de -128 à 127
            for (int i = 0; i < data.length; i++) {
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
     * @param data Ensemble des octets du message d'entrée
     * @return Prochaine frame générée à partir du compteur
     */
    private Frame genFrame(byte[] data) {
        int start = this.frameCount * this.frameSize;
        int stop = (this.frameCount + 1) * this.frameSize;
        byte[] frameData = Arrays.copyOfRange(data, start, stop);

        // TODO - HAMMING CODE
        // Frame f = new Frame(this.frameSize, Hamming.code(frameData), true);*/
        Frame f = new Frame(this.frameSize, frameData, true);
        this.frameCount++;
        return f;
    }

    /**
     * Fonction permettant d'obtenir une trame spécifique à partir de son numéro
     *
     * TODO - Faire sans l'ajout du nombre total de frames.
     *
     * @param frameNumber Numéro de la trame voulue
     * @return La trame portant le numéro spécifié
     */
    public Frame getFrame(int frameNumber) {
        Frame frame = this.dataBlocks.get(frameNumber);
//        Frame frame = new Frame(frameSize, (byte[]) dataBlocks.get(frameNumber), true);
        frame.setFrameNumber(frameNumber);
        frame.setNumberOfFrames(frameCount);
        return frame;
    }

    /**
     * Fonction de récupération du nombre total de trames
     *
     * @return Le nombre total de trames du fichier
     */
    public int getNumberOfFrames() {
        return this.frameCount;
    }

    /**
     * Fonction de récupération de la taille d'une trame (données utiles)
     *
     * @return Le nombre d'octets de données des trames générées
     */
    public int getFrameSize() {
        return this.frameSize;
    }
}
