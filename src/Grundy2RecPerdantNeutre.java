import java.util.*;

/**
 * Game of Grundy2
 *
 * This program makes it possible to play versus the computer which seeks to
 * gain in an optimal way.
 * 
 * This version contains :
 * - the AI that shearch the winning move
 * - a save of the losing situations
 * - a save of the winning situations
 * - remove losing situations (P+P = P & P+G = G)
 * - a test of efficiency of the AI
 * 
 * @author B. GUERNY et J. Perrot
 */
class Grundy2RecPerdantNeutre {
    // Variables globales
    long cpt; // compteur d'appels récursifs
    int NB_MAX_EFFI = 45; // nombre d'allumettes max pour le test d'efficacite
    ArrayList<ArrayList<Integer>> posPerdantes = new ArrayList<ArrayList<Integer>>(); // situations perdantes
    ArrayList<ArrayList<Integer>> posGagnantes = new ArrayList<ArrayList<Integer>>(); // situations perdantes

    /**
     * Main method
     */
    void principal() {
        // joueurContreMachine();
        // testJouerGagnant();
        // testPremier();
        // testSuivant();
        // testSimplifier();
        // testEstConnu();
        // testAjouterEssai();
        testJouerGagnantEfficacite();
    }

    /**
     * Remove each pile where value is less than 2 and sort the try
     * 
     * @param essai try to simplify
     * @return an ArrayList which contains only piles with value > 2
     */
    ArrayList<Integer> simplifier(ArrayList<Integer> essai) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        boolean unPerdant = false;
        boolean unGagnant = false;
        boolean unNeutre = false;

        // Première passe qui analyse le contenu de l'essai

        int i = 0;
        while (i < essai.size() && !(unPerdant && (unGagnant || unNeutre))) {
            ArrayList<Integer> tas = new ArrayList<Integer>();
            tas.add(essai.get(i));
            if (estConnu(tas, posGagnantes)) {
                unGagnant = true;
            } else if (estConnu(tas, posPerdantes)) {
                unPerdant = true;
            } else {
                unNeutre = true;
            }
            i++;
        }

        // Deuxième passe qui supprime les tas perdants
        if (unPerdant) { // Si on a au moins un perdant, on les supprimes tous
            if (unGagnant || unNeutre) { // On retire tous les perdants et les < 2
                i = 0;
                while (i < essai.size()) {
                    ArrayList<Integer> tas = new ArrayList<Integer>();
                    tas.add(essai.get(i));
                    if (!estConnu(tas, posPerdantes) && essai.get(i) > 2) {
                        ret.add(essai.get(i));
                    }
                    i++;
                }
            } else { // Si on a que des perdants, on garde le 1
                ret.add(1);
            }
        } else {
            i = 0;
            while (i < essai.size()) {
                if (essai.get(i) > 2) {
                    ret.add(essai.get(i));
                }
                i++;
            }
        }
        Collections.sort(ret);
        
        return ret;
    }

    /**
     * Test the method simplifier
     */
    void testSimplifier() {
        System.out.println("*** testSimplifier() ***");
        ArrayList<Integer> essai = new ArrayList<Integer>();
        essai.add(1);
        essai.add(3);
        essai.add(2);
        essai.add(4);
        essai.add(5);

        ArrayList<Integer> essaiCleanAtt = new ArrayList<Integer>();
        essaiCleanAtt.add(3);
        essaiCleanAtt.add(4);
        essaiCleanAtt.add(5);

        ArrayList<Integer> essaiClean = simplifier(essai);
        if (essaiClean.equals(essaiCleanAtt)) {
            System.out.println("OK");
        } else {
            System.out.println("ERREUR");
        }
    }

    /**
     * Shearch if the try is already known
     * 
     * @param essai    try to search
     * @param sitSaved table of losing situations
     */
    boolean estConnu(ArrayList<Integer> essai, ArrayList<ArrayList<Integer>> sitSaved) {
        boolean ret = false;
        int i = 0;
        while (i < sitSaved.size() && !ret) { // Parcours la liste des situations
            if (sitSaved.get(i).equals(essai)) { // Si l'essai est trouvé
                ret = true;
            }
            i++;
        }
        return ret;
    }

    /**
     * Test the method estConnu
     */
    void testEstConnu() {
        System.out.println("*** testEstConnu() ***");
        ArrayList<Integer> essai = new ArrayList<Integer>();
        essai.add(1);
        essai.add(3);
        essai.add(2);
        essai.add(4);
        essai.add(5);

        ArrayList<Integer> essaiClean = simplifier(essai);

        posPerdantes.clear();
        if (estConnu(essaiClean, posPerdantes)) {
            System.out.println("Element non présent: ERREUR");
        } else {
            System.out.println("Element non présent: OK");
        }

        posPerdantes.add(essaiClean);
        if (estConnu(essaiClean, posPerdantes)) {
            System.out.println("Element présent: OK");
        } else {
            System.out.println("Element présent: ERREUR");
        }
    }

    /**
     * Add a sorted try to the table of losing situations
     * 
     * @param essai    try to add
     * @param sitSaved table of losing situations
     */
    void ajouterEssai(ArrayList<Integer> essai, ArrayList<ArrayList<Integer>> sitSaved) {

        ArrayList<Integer> essaiClean = simplifier(essai); // On simplifie l'essai
        if (!estConnu(essaiClean, sitSaved)) { // Si l'essai n'est pas deja connu
            sitSaved.add(essaiClean);
        }
    }

    /**
     * Test the method ajouterEssai
     */
    void testAjouterEssai() {
        System.out.println("*** testAjouterEssai() ***");
        posPerdantes.clear();
        boolean error = false;

        // Ajout d'un essai
        ArrayList<Integer> essai1 = new ArrayList<Integer>();
        essai1.add(4);

        ArrayList<ArrayList<Integer>> sitSavedAtt = new ArrayList<ArrayList<Integer>>();
        sitSavedAtt.add(essai1);
        ajouterEssai(essai1, posPerdantes);
        if (!posPerdantes.equals(sitSavedAtt)) {
            System.out.println("ERREUR : L'ajout d'un essai n'a pas fonctionné");
            error = true;
        }

        // Ajout d'un essai qui existe deja
        ajouterEssai(essai1, posPerdantes);
        if (!posPerdantes.equals(sitSavedAtt)) {
            System.out.println("ERREUR : L'ajout d'un essai qui existe déjà n'a pas fonctionné");
            error = true;
        }

        if (!error) {
            System.out.println("OK");
        }
    }

    /**
     * Play the winning move if it exists
     * 
     * @param jeu the game board
     * @return true if there is a winning move, false otherwise
     */
    boolean jouerGagnant(ArrayList<Integer> jeu) {
        boolean gagnant = false;
        if (jeu == null) {
            System.err.println("suivant(): le paramètre jeu est null");
        } else {
            ArrayList<Integer> essai = new ArrayList<Integer>();
            int ligne = premier(jeu, essai);
            // mise en oeuvre de la règle numéro2
            // Une situation (ou position) est dite gagnante pour la machine (ou le joueur,
            // peu importe), s’il existe AU MOINS UNE
            // décomposition (c-à-d UNE action qui consiste à décomposer un tas en 2 tas
            // inégaux) perdante pour l’adversaire.
            while (ligne != -1 && !gagnant) {
                ArrayList<Integer> essaiClean = simplifier(essai);

                if (estPerdante(essaiClean)) {

                    ajouterEssai(essaiClean, posPerdantes); // On sauvegarde la situation en tant que perdante
                    ajouterEssai(jeu, posGagnantes); // On sauvegarde le jeu en tant que gagnant

                    jeu.clear();
                    gagnant = true;
                    for (int i = 0; i < essai.size(); i++) {
                        jeu.add(essai.get(i));
                    }
                } else {
                    ligne = suivant(jeu, essai, ligne);
                }
            }
        }

        return gagnant;
    }

    /**
     * RECURSIVE method that indicates if the configuration (of the current game or
     * try game) is losing
     * 
     * @param jeu actual game board
     * @return true if the configuration is losing, false otherwise
     */
    boolean estPerdante(ArrayList<Integer> jeu) {
        boolean ret = true; // par défaut la configuration est perdante

        if (jeu == null) {
            System.err.println("estPerdante(): le paramètre jeu est null");
        } else {

            // si il n'y a plus que des tas de 1 ou 2 allumettes dans le plateau de jeu
            // alors la situation est forcément perdante (ret=true) = FIN de la récursivité
            if (!estPossible(jeu)) {
                ret = true;
            }

            else {
                // Si la situaiton est dans la liste des situations gagnantes
                if (estConnu(jeu, posGagnantes)) { 
                    ret = false;
                } else {

                // si la situation est déjà connue comme perdante, alors on arrete la récursivité
                if (estConnu(jeu, posPerdantes)) {
                        ret = true;

                    } else {

                        // création d'un jeu d'essais qui va examiner toutes les décompositions
                        // possibles à partir de jeu
                        ArrayList<Integer> essai = new ArrayList<Integer>(); // size = 0

                        // toute première décomposition : enlever 1 allumette au premier tas qui possède
                        // au moins 3 allumettes, ligne = -1 signifie qu'il n'y a plus de tas d'au moins
                        // 3 allumettes
                        int ligne = premier(jeu, essai);
                        while ((ligne != -1) && ret) {

                            // mise en oeuvre de la règle numéro1
                            // Une situation (ou position) est dite perdante pour la machine (ou le joueur,
                            // peu importe) si et seulement si TOUTES
                            // ses décompositions possibles (c-à-d TOUTES les actions qui consistent à
                            // décomposer un tas en 2 tas inégaux) sont
                            // TOUTES gagnantes pour l’adversaire.
                            // Si UNE SEULE décomposition (à partir du jeu) est perdante (pour l'adversaire)
                            // alors la configuration n'EST PAS perdante.
                            // Ici l'appel à "estPerdante" est RECURSIF.
                            
                            ArrayList<Integer> essaiClean = simplifier(essai);

                            if (estPerdante(essaiClean)) {
                                // si l'essai est perdant ( pour l'adversaire)
                                // alors on sauvegarde l'essai en tant que perdante
                                // on sauvegarde le jeu en tant que gagnant
                                // et on arrete la récursivité 
                                ajouterEssai(essaiClean, posPerdantes);
                                ajouterEssai(jeu, posGagnantes);
                                ret = false;

                            } else {
                                // génère la configuration d'essai suivante (c'est-à-dire UNE décomposition
                                // possible)
                                // à partir du jeu, si ligne = -1 il n'y a plus de décomposition possible
                                ligne = suivant(jeu, essai, ligne);
                            }

                            cpt++; // compteur de récursivité pour le test d'efficacite
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Short tests of the method joueurGagnant()
     */
    void testJouerGagnant() {
        System.out.println();
        System.out.println("*** testJouerGagnant() ***");

        System.out.println("Test des cas normaux");
        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(6);
        ArrayList<Integer> resJeu1 = new ArrayList<Integer>();
        resJeu1.add(4);
        resJeu1.add(2);

        testCasJouerGagnant(jeu1, resJeu1, true);
    }

    /**
     * Test of a case of the method jouerGagnant()
     *
     * @param jeu    the game board
     * @param resJeu the expected result after jouerGagnant()
     * @param res    the expected result of jouerGagnant()
     */
    void testCasJouerGagnant(ArrayList<Integer> jeu, ArrayList<Integer> resJeu, boolean res) {
        // Arrange
        System.out.print("jouerGagnant (" + jeu.toString() + ") : ");

        // Act
        boolean resExec = jouerGagnant(jeu);

        // Assert
        System.out.print(jeu.toString() + " " + resExec + " : ");
        if (jeu.equals(resJeu) && res == resExec) {
            System.out.println("OK\n");
        } else {
            System.err.println("ERREUR\n");
        }
    }

    /**
     * Divide the matches of a line of play into two piles (1 line = 1 pile)
     * 
     * @param jeu   game board
     * @param ligne line (pile) on which matches should be separated
     * @param nb    number of matches REMOVE from line after separation
     */
    void enlever(ArrayList<Integer> jeu, int ligne, int nb) {
        // traitement des erreurs
        if (jeu == null) {
            System.err.println("enlever() : le paramètre jeu est null");
        } else if (ligne >= jeu.size()) {
            System.err.println("enlever() : le numéro de ligne est trop grand");
        } else if (nb >= jeu.get(ligne)) {
            System.err.println("enlever() : le nb d'allumettes à retirer est trop grand");
        } else if (nb <= 0) {
            System.err.println("enlever() : le nb d'allumettes à retirer est trop petit");
        } else if (2 * nb == jeu.get(ligne)) {
            System.err.println("enlever() : le nb d'allumettes à retirer est la moitié");
        } else {
            // nouveau tas (case) ajouté au jeu (nécessairement en fin de tableau)
            // ce nouveau tas contient le nbre d'allumettes retirées (nb) du tas à séparer
            jeu.add(nb);
            // le tas restant avec "nb" allumettes en moins
            jeu.set(ligne, jeu.get(ligne) - nb);
        }
    }

    /**
     * Try if it is possible to separate the matches of a line 
     * 
     * @param jeu  game board
     * @return true if it is possible to separate the matches of a line
     */
    boolean estPossible(ArrayList<Integer> jeu) {
        boolean ret = false;
        if (jeu == null) {
            System.err.println("estPossible(): le paramètre jeu est null");
        } else {
            int i = 0;
            while (i < jeu.size() && !ret) {
                if (jeu.get(i) > 2) {
                    ret = true;
                }
                i = i + 1;
            }
        }
        return ret;
    }

    /**
     * Create a first configuration of try from the game
     * 
     * @param jeu      game board
     * @param jeuEssai new try game board
     * @return the number of the pile divided into two or (-1) if there is no pile 
     *         of at least 3 matches
     */
    int premier(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai) {

        int numTas = -1; // pas de tas à séparer par défaut
        int i;

        if (jeu == null) {
            System.err.println("premier(): le paramètre jeu est null");
        } else if (!estPossible((jeu))) {
            System.err.println("premier(): aucun tas n'est divisible");
        } else if (jeuEssai == null) {
            System.err.println("estPossible(): le paramètre jeuEssai est null");
        } else {
            // avant la copie du jeu dans jeuEssai il y a un reset de jeuEssai
            jeuEssai.clear();
            i = 0;

            // recopie case par case
            // jeuEssai est le même que le jeu au départ
            while (i < jeu.size()) {
                jeuEssai.add(jeu.get(i));
                i = i + 1;
            }

            i = 0;
            // rechercher un tas d'allumettes d'au moins 3 allumettes dans le jeu
            // sinon numTas = -1
            boolean trouve = false;
            while ((i < jeu.size()) && !trouve) {

                // si on trouve un tas d'au moins 3 allumettes
                if (jeuEssai.get(i) >= 3) {
                    trouve = true;
                    numTas = i;
                }

                i = i + 1;
            }

            // sépare le tas (case numTas) en un tas d'UNE SEULE allumette à la fin du
            // tableau
            // le tas en case numTas a diminué d'une allumette (retrait d'une allumette)
            // jeuEssai est le plateau de jeu qui fait apparaître cette séparation
            if (numTas != -1)
                enlever(jeuEssai, numTas, 1);
        }

        return numTas;
    }

    /**
     * Short tests of the method premier()
     */
    void testPremier() {
        System.out.println();
        System.out.println("*** testPremier()");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(10);
        jeu1.add(11);
        int ligne1 = 0;
        ArrayList<Integer> res1 = new ArrayList<Integer>();
        res1.add(9);
        res1.add(11);
        res1.add(1);
        testCasPremier(jeu1, ligne1, res1);
    }

    /**
     * Test a case of the method testPremier()
     * 
     * @param jeu   the game board
     * @param ligne the index of the first separated pile
     * @param res   the expected result
     */
    void testCasPremier(ArrayList<Integer> jeu, int ligne, ArrayList<Integer> res) {
        // Arrange
        System.out.print("premier (" + jeu.toString() + ") : ");
        ArrayList<Integer> jeuEssai = new ArrayList<Integer>();
        // Act
        int noLigne = premier(jeu, jeuEssai);
        // Assert
        System.out.println("\nnoLigne = " + noLigne + " jeuEssai = " + jeuEssai.toString());
        if (jeuEssai.equals(res) && noLigne == ligne) {
            System.out.println("OK\n");
        } else {
            System.err.println("ERREUR\n");
        }
    }

    /**
     * Generates the following try setup (i.e. ONE possible decomposition)
     * 
     * @param jeu      game board
     * @param jeuEssai try game board after the decomposition
     * @param ligne    index of the last pile to be divided
     * @return the index of the pile divided in two for the new configuration, 
     *         -1 if no more decomposition is possible
     */
    int suivant(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai, int ligne) {

        // System.out.println("suivant(" + jeu.toString() + ", " +jeuEssai.toString() +
        // ", " + ligne + ") = ");

        int numTas = -1; // par défaut il n'y a plus de décomposition possible

        int i = 0;
        // traitement des erreurs
        if (jeu == null) {
            System.err.println("suivant(): le paramètre jeu est null");
        } else if (jeuEssai == null) {
            System.err.println("suivant() : le paramètre jeuEssai est null");
        } else if (ligne >= jeu.size()) {
            System.err.println("estPossible(): le paramètre ligne est trop grand");
        }

        else {

            int nbAllumEnLigne = jeuEssai.get(ligne);
            int nbAllDernCase = jeuEssai.get(jeuEssai.size() - 1);

            // si sur la même ligne (passée en paramètre) on peut encore retirer des
            // allumettes,
            // c-à-d si l'écart entre le nombre d'allumettes sur cette ligne et
            // le nombre d'allumettes en fin de tableau est > 2, alors on retire encore
            // 1 allumette sur cette ligne et on ajoute 1 allumette en dernière case
            if ((nbAllumEnLigne - nbAllDernCase) > 2) {
                jeuEssai.set(ligne, (nbAllumEnLigne - 1));
                jeuEssai.set(jeuEssai.size() - 1, (nbAllDernCase + 1));
                numTas = ligne;
            }

            // sinon il faut examiner le tas (ligne) suivant du jeu pour éventuellement le
            // décomposer
            // on recrée une nouvelle configuration d'essai identique au plateau de jeu
            else {
                // copie du jeu dans JeuEssai
                jeuEssai.clear();
                for (i = 0; i < jeu.size(); i++) {
                    jeuEssai.add(jeu.get(i));
                }

                boolean separation = false;
                i = ligne + 1; // tas suivant
                // si il y a encore un tas et qu'il contient au moins 3 allumettes
                // alors on effectue une première séparation en enlevant 1 allumette
                while (i < jeuEssai.size() && !separation) {
                    // le tas doit faire minimum 3 allumettes
                    if (jeu.get(i) > 2) {
                        separation = true;
                        // on commence par enlever 1 allumette à ce tas
                        enlever(jeuEssai, i, 1);
                        numTas = i;
                    } else {
                        i = i + 1;
                    }
                }
            }
        }

        return numTas;
    }

    /**
     * Short tests of the method suivant()
     */
    void testSuivant() {
        System.out.println();
        System.out.println("*** testSuivant() ****");

        int ligne1 = 0;
        int resLigne1 = 0;
        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(10);
        ArrayList<Integer> jeuEssai1 = new ArrayList<Integer>();
        jeuEssai1.add(9);
        jeuEssai1.add(1);
        ArrayList<Integer> res1 = new ArrayList<Integer>();
        res1.add(8);
        res1.add(2);
        testCasSuivant(jeu1, jeuEssai1, ligne1, res1, resLigne1);

        int ligne2 = 0;
        int resLigne2 = -1;
        ArrayList<Integer> jeu2 = new ArrayList<Integer>();
        jeu2.add(10);
        ArrayList<Integer> jeuEssai2 = new ArrayList<Integer>();
        jeuEssai2.add(6);
        jeuEssai2.add(4);
        ArrayList<Integer> res2 = new ArrayList<Integer>();
        res2.add(10);
        testCasSuivant(jeu2, jeuEssai2, ligne2, res2, resLigne2);

        int ligne3 = 1;
        int resLigne3 = 1;
        ArrayList<Integer> jeu3 = new ArrayList<Integer>();
        jeu3.add(4);
        jeu3.add(6);
        jeu3.add(3);
        ArrayList<Integer> jeuEssai3 = new ArrayList<Integer>();
        jeuEssai3.add(4);
        jeuEssai3.add(5);
        jeuEssai3.add(3);
        jeuEssai3.add(1);
        ArrayList<Integer> res3 = new ArrayList<Integer>();
        res3.add(4);
        res3.add(4);
        res3.add(3);
        res3.add(2);
        testCasSuivant(jeu3, jeuEssai3, ligne3, res3, resLigne3);

    }

    /**
     * Test a case of the method suivant()
     * 
     * @param jeu      game board
     * @param jeuEssai game board after one separation
     * @param ligne    the index of the line that is last separated
     * @param resJeu   is the jeuEssai expected after separation
     * @param resLigne is the index expected from the pile which is separated
     */
    void testCasSuivant(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai, int ligne, ArrayList<Integer> resJeu,
            int resLigne) {
        // Arrange
        System.out.print("suivant (" + jeu.toString() + ", " + jeuEssai.toString() + ", " + ligne + ") : ");
        // Act
        int noLigne = suivant(jeu, jeuEssai, ligne);
        // Assert
        System.out.println("\nnoLigne = " + noLigne + " jeuEssai = " + jeuEssai.toString());
        if (jeuEssai.equals(resJeu) && noLigne == resLigne) {
            System.out.println("OK\n");
        } else {
            System.err.println("ERREUR\n");
        }
    }

    /**
     * efficiency test method of jouerGagnant()
     */
    void testJouerGagnantEfficacite() {
        System.out.println();
        System.out.println("*** testJouerGagnantEfficacite() ***");
        long debut, fin;
        double ms;

        int n = 3;
        ArrayList<Integer> jeu = new ArrayList<Integer>();

        while (n <= NB_MAX_EFFI) { // Teste l'efficacité avec un n allant de 3 à NB_MAX_EFFI
            // Reset des variables
            cpt = 0;
            posPerdantes.clear();
            posGagnantes.clear();
            jeu.clear();
            jeu.add(n);

            debut = System.nanoTime();
            jouerGagnant(jeu);
            fin = System.nanoTime();
            ms = (fin - debut) / 1000000;

            System.out
                    .println("Pour n = " + n + ": \tTemps d'exécution = " + ms + " ms \tCompteur = " + cpt);
            n++;
        }
    }

    /**
     * Show the game board
     * 
     * @param plateau game board
     */
    void afficherPlateau(ArrayList<Integer> plateau) {
        int taille = plateau.size();
        int i = 0;
        while (i < taille) { // Affiche chaque ligne du plateau
            System.out.print(i + " :");
            for (int j = 0; j < plateau.get(i); j++) {
                System.out.print(" | ");
            }
            System.out.println();
            i++;
        }
    }

    /**
     * Player turn:
     * - Asks the line to play
     * - Asks for the number to withdraw
     * - Separate the piles
     * 
     * @param plateau game board
     */
    void tourJoueur(ArrayList<Integer> plateau) {
        // Cherche si il y a une seul ligne jouable la choisir auto, sinon la demander
        int ligne = 0; // La ligne a jouer
        int nbLigneJouable = 0;

        int i = 0;
        while (i < plateau.size() && nbLigneJouable <= 2) {
            if (plateau.get(i) > 2) {
                ligne = i; // On sauvegarde la ligne a jouer auto
                nbLigneJouable += 1;
            }
            i++;
        }

        // Si il y a plus d'une ligne jouable
        if (nbLigneJouable > 1) {
            // Demande la saisie d'une ligne jouable
            ligne = SimpleInput.getInt("Ligne : ");
            while (ligne < 0 || ligne >= plateau.size() || plateau.get(ligne) <= 2) {
                ligne = SimpleInput.getInt("Ligne : ");
            }
        }

        // Le nombre saisit doit permetre de diviser en 2 partie inégale
        int nombre = SimpleInput.getInt("Nombre d’allumettes à séparer : ");
        while (nombre <= 0 || nombre >= plateau.get(ligne) || (plateau.get(ligne) - nombre) == nombre) {
            nombre = SimpleInput.getInt("Nombre d’allumettes à séparer : ");
        }

        // Modifie le plateau
        enlever(plateau, ligne, nombre);
    }

    /**
     * Computer turn:
     * - Play winner if possible
     * - Plays randomly otherwise
     * 
     * @param plateau game board
     */
    void tourOrdinateur(ArrayList<Integer> plateau) {
        boolean coupGagnant = jouerGagnant(plateau);
        if (!coupGagnant) { // Si l'ordinateur ne peut pas gagner, jouer aléatoirement

            // Cherche la première ligne jouable
            int ligne = 0;
            while (plateau.get(ligne) <= 2) {
                ligne++;
            }

            // Choisit une séparation aléatoire
            int nombre = 1 + (int) (Math.random() * plateau.get(ligne)); // Une décomposition (nb entre 1 et nbDecompo)

            enlever(plateau, ligne, nombre);
        }
    }

    /**
     * Initialize the game
     * Makes the computer and the player play alternately.
     * Stops when there is a winner and displays it.
     */
    void joueurContreMachine() {
        String nomJoueur = SimpleInput.getString("Nom du joueur : ");

        int nbAllumettes = SimpleInput.getInt("Nombre d'allumettes : ");
        while (nbAllumettes <= 2) {
            nbAllumettes = SimpleInput.getInt("Nombre d'allumettes : ");
        }

        boolean tourJoueur = SimpleInput.getBoolean("Voulez vous commencer ? (true/false) : ");

        ArrayList<Integer> plateau = new ArrayList<Integer>();
        plateau.add(nbAllumettes);
        afficherPlateau(plateau);

        while (estPossible(plateau)) {
            if (tourJoueur) {
                System.out.println("C'est au tour de : " + nomJoueur);
                tourJoueur(plateau);
            } else {
                System.out.println("C'est au tour de l'ordinateur");
                tourOrdinateur(plateau);
            }

            afficherPlateau(plateau);
            // Echange le tour des joueurs
            tourJoueur = !tourJoueur;
        }

        if (tourJoueur) { // Si le joueur ne peut plus jouer
            System.out.println("L'ordinateur a gagné.");
        } else {
            System.out.println(nomJoueur + " a gagné.");
        }
    }
}