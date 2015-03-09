package Protocole;

/**
 * Classe représentant les tampons d'envoi et de réception
 *
 * TODO : Il serait plus "orienté object" d'en faire une classe abstraite et de
 * faire des classes séparées pour le tampon d'envoi et de réception qui
 * hériteraient de celle-ci. Ce n'est probablement pas une préoccupation par
 * contre pour un travail de télématique.
 */
public class Buffer {

    // Tampon contenant les trames
    private Frame[] buffer;

    // Tableau indiquant si l'espace i du tampon est occupé par une trame
    private boolean[] freeSpace;    // TRUE = Libre             FALSE = Occupé

    // Tableau indiquant si l'espace i du tampon a été envoyée
    private boolean[] frameSent;    // TRUE = Trame envoyée     FALSE = Trame non-envoyée

    /**
     * NON-TESTÉ En théorie, doit contenir un int qui croît à chaque fois qu'on
     * vérifie s'il y a un élément à envoyer. Voir fonction tick().
     */
    private int[] timeSent;

    /**
     * NON-TESTÉ En théorie, permet de déterminer le nombre de tick avant de
     * renvoyer. MAJ : remplacer par le timer tempo (sTimeOut et rTimeOut) prévu
     * dans Station pour l'expiration
     */
    private int timeOut = 50;

    // Taille du buffer
    private int size;

    /**
     * Constructeur de la classe Buffer
     *
     * @param size taille du buffer
     */
    public Buffer(int size) {
        buffer = new Frame[size];
        freeSpace = new boolean[size];
        frameSent = new boolean[size];
        timeSent = new int[size];

        // Par défaut, tous les espaces sont libres et aucune trame n'est envoyée
        for (int i = 0; i < size; i++) {
            freeSpace[i] = true;
            frameSent[i] = false;
            timeSent[i] = 0;
        }
        this.size = size;
    }

    /**
     * Fonction permettant de ré-initialiser le buffer. J'en ai eu besoin pour
     * faire un buffer de réception pouvant contenir toutes les trames reçues.
     * Créer un buffer avec des NULL n'était pas très pratique...
     *
     * @param size Taille du buffer à initialiser
     */
    public void initializeBuffer(int size) {
        buffer = new Frame[size];
        freeSpace = new boolean[size];
        frameSent = new boolean[size];
        for (int i = 0; i < size; i++) {
            freeSpace[i] = true;
            frameSent[i] = false;
        }
        this.size = size;
    }

    /**
     * (Utilisée dans station à l'émission) Fonction permettant de rajouter une
     * trame dans le buffer sans égards à sa position. Retourne TRUE si la trame
     * a pu être placée. FALSE, sinon.
     *
     * @param frame Frame à ajouter au buffer d'émission
     * @return Un booléen de vérification de la bonne exécution de la méthode
     */
    public boolean addFrame(Frame frame) {
        for (int i = 0; i < size; i++) {
            if (freeSpace[i]) {             // Si l'espace i est libre
                buffer[i] = frame;          // Place la trame à la position i
                freeSpace[i] = false;       // La position i est maintenant occupée
                return true;
            }
        }
        return false;
    }

    /**
     * (Utilisée dans station à la réception) Fonction permettant de rajouter
     * une trame dans le buffer à la position indiquée par son numéro de trame.
     * Retourne TRUE si la trame a pu être placée et que la position existe dans
     * le buffer. FALSE, sinon.
     *
     * Permet d'inscrire en ordre les trames lors de la réception ce qui aide le
     * traitement quand on ne les reçoit pas dans l'ordre.
     *
     * @param frame Frame à ajouter au buffer de réception
     * @return Un booléen de vérification de la bonne exécution de la méthode
     */
    public boolean addFrameAt(Frame frame) {
        int position = frame.getFrameNumber();
        if (position < size && position >= 0) {
            if (freeSpace[position]) {
                freeSpace[position] = false;
                buffer[position] = frame;
                return true;
            }
        }
        return false;
    }

    /**
     * Fonction permettant de retirer une trame selon son numéro.
     *
     * Ne détruit pas la trame afin d'éviter d'introduire des NULL. L'espace est
     * marqué comme étant libre et on ré-initialise les autres valeurs.
     *
     * @param frameNumber Numéro de la frame à supprimer du buffer
     */
    public void removeFrame(int frameNumber) {
        int i;
        for (i = 0; i < size; i++) {
            // Si l'espace est occupé, c'est qu'il y a une trame à l'emplacement
            // Évite d'appeler une fonction sur une trame s'il n'y a pas de trame...
            if (!freeSpace[i]) {
                if (buffer[i].getFrameNumber() == frameNumber) {
                    freeSpace[i] = true;
                    frameSent[i] = false;
                    timeSent[i] = 0;
                }
            }
        }
    }

    /**
     * Fonction retournant la trame se trouvant à la position i. TODO - Gestion
     * des exceptions
     *
     * @param i Indice de la frame voulue
     * @return Frame à la position i dans le buffer
     */
    public Frame getFrame(int i) {
        return buffer[i];
    }

    /**
     * Fonction vérifiant si le buffer contient encore au moins un espace libre.
     *
     * @return Un booléen de vérification de la bonne exécution de la méthode
     */
    public boolean isNotFull() {
        for (int i = 0; i < size; i++) {
            if (freeSpace[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fonction vérifiant si le buffer contient au moins un élément.
     *
     * @return true si le buffer contient au moins un élément, false sinon
     */
    public boolean isNotEmpty() {
        for (int i = 0; i < size; i++) {
            if (!freeSpace[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fonction servant à envoyer l'identifiant de la prochaine trame à envoyer.
     *
     * TODO - Il n'est pas correct d'assumer automatiquement que la trame sera
     * envoyée par la suite sur le support de transmission.
     *
     * @return -1 s'il n'y a pas de trame à envoyer dans l'immédiat, sinon le
     *         numéro de la prochaine trame
     */
    public int getNextToSend() {
        /**
         * NON-TESTÉ - À chaque appel de cette fonction, on incrémente le
         * compteur de temps des trames ayant été envoyées. Si ça fait trop
         * longtemps, la trame doit être envoyée à nouveau.
         */
        tick();

        for (int i = 0; i < size; i++) {
            /**
             * Si la trame à cette position n'a pas encore été envoyée une fois
             * ou si ça fait trop longtemps qu'elle a été envoyée, on envoie la
             * position dans le buffer de la trame à envoyer. MAJ : l'adapter
             * pour l'utilisation des timers
             */
            if ((!frameSent[i] || (frameSent[i] && timeSent[i] > timeOut)) && !freeSpace[i]) {
                frameSent[i] = true;
                timeSent[i] = 1;
                return i;
            }
        }
        return -1;
    }

    /**
     * Fonction de calcul de la taille du buffer
     *
     * @return la taille du buffer
     */
    public int getSize() {
        return size;
    }

    /**
     * NON-TESTÉ En théorie, incrémente le compteur des trames envoyées. Une
     * trame qui se trouve dans le buffer d'envoi est une trame à laquelle nous
     * n'avons pas reçu une confirmation de la réception. Quand on reçoit un ACK
     * pour une trame, la trame est supprimée du buffer.
     */
    private void tick() {
        for (int i = 0; i < size; i++) {
            if (timeSent[i] > 0) {
                timeSent[i]++;
            }
        }
    }
}
