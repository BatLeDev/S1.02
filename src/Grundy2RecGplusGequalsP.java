import java.util.*;

/**
 * Jeu de Grundy2
 * Ce programme permet de jouer contre un joueur ou contre un ordinateur
 * qui cherche a gagner de maniere optimale.
 *
 * Cette version sauvegarde les situations perdantes et gagnantes deja
 * determinees, supprime les tas perdants et simplifie les tas gagnants
 * Elle contiens egalement un test d'efficacite de l'IA qui prends en compte
 * cette amelioration.
 *
 * @author B. GUERNY et J. Perrot
 */
class Grundy2RecGplusGequalsP {
    // Variables globales
    long CPT; // compteur d'appels
    int nbAlluMaxTestEfficacite = 80; // nombre d'allumettes max de départ pour le test d'efficacite
    int[] type = {0,0,0,1,0,2,1,0,2,1,0,2,1,3,2,1,3,2,4,3,0,4,3,0,4,3,0,4,1,2,3,1,2,4,1,2,4,1,2,4,1,5,4,1,5,4,1,5,4,1,0};
    ArrayList<ArrayList<Integer>> SIT_PERD = new ArrayList<ArrayList<Integer>>(); // situations perdantes
    ArrayList<ArrayList<Integer>> SIT_GAGN = new ArrayList<ArrayList<Integer>>(); // situations perdantes

    /**
     * Méthode principal du programme
     */
    void principal() {
        // partieJoueurOrdinateur();
        // testJouerGagnant();
        // testPremier();
        // testSuivant();
        // testCleanEssai();
        // testEstPresent();
        // testAjouterEssai();
        testJouerGagnantEfficacite();
    }

    /**
     * Retire les 1 et les 0 d'un essai et le tri
     *
     * @param essai l'essai a "nettoyer"
     * @return un tableau contenant uniquement des valeurs > 2
     */
    ArrayList<Integer> cleanEssai(ArrayList<Integer> essai) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for (int i = 0; i < essai.size(); i++) {
            ret.add(essai.get(i));
        }

        int i = 0;
        while (i < ret.size()) {
            if (ret.get(i) > 50) { // Si le type du tas n'est pas connu
                ArrayList<Integer> tas = new ArrayList<Integer>();
                tas.add(ret.get(i));
                if (estPresent(tas, SIT_PERD)) { // On se contente de le supprimer si il est perdant
                    ret.remove(i);
                    i--;
                }
            } else if (type[ret.get(i)] == 0) { // Si il est du type 0 (perdant)
                ret.remove(i); // On le supprime
                i--;

            } else { // si son type est connu et qu'il est gagnant
                // On va chercher a le comparer avec les autres tas
                // Si il y a un tas du meme type, on les supprime

                int j = i+1;
                while (j < ret.size()) { // Parcours des tas restants
                    if (ret.get(j) < 50) { // Si le type du tas n'est pas conn est connu
                        if (i < 0) {
                            i = 0;
                        }
                        if (type[ret.get(i)] == type[ret.get(j)]) { // Si les deux tas sont du meme type
                            ret.remove(i); // On les supprime
                            ret.remove(j - 1); // j-1 car le tas i a ete supprime
                            i--;
                        }
                    }
                    j++;
                }
            }
            i++;
        }
        if (ret.size() == 0) {
            ret.add(1);
        }

        Collections.sort(ret);
        return ret;
    }

    /**
     * Test la méthode cleanEssai
     */
    void testCleanEssai() {
        System.out.println("*** testCleanEssai() ***");
        ArrayList<Integer> essai = new ArrayList<Integer>();
        essai.add(1);
        essai.add(3);
        essai.add(2);
        essai.add(4);
        essai.add(5);

        ArrayList<Integer> essaiCleanAtt = new ArrayList<Integer>();
        essaiCleanAtt.add(3);
        essaiCleanAtt.add(5);

        ArrayList<Integer> essaiClean = cleanEssai(essai);
        if (essaiClean.equals(essaiCleanAtt)) {
            System.out.println("OK");
        } else {
            System.out.println("ERREUR");
        }

        essai.remove(essai.size()-1);
        essai.add(3);

        essaiCleanAtt = new ArrayList<Integer>();
        essaiCleanAtt.clear();
        essaiCleanAtt.add(1);

        essaiClean = cleanEssai(essai);
        if (essaiClean.equals(essaiCleanAtt)) {
            System.out.println("OK");
        } else {
            System.out.println("ERREUR");
        }
    }

    /**
     * Cherche si un essai trié est dans le tableau
     *
     * @param essai    essai à chercher
     * @param sitSaved tableau de situations perdantes ou gagnantes
     */
    boolean estPresent(ArrayList<Integer> essai, ArrayList<ArrayList<Integer>> sitSaved) {
        boolean ret = false;

        int i = 0;
        while (i < sitSaved.size() && !ret) { // Parcours de la liste des situations
            if (sitSaved.get(i).size() == essai.size()) {
                ret = true;
                int j = 0;
                while (j < essai.size() && ret) {
                    if (sitSaved.get(i).get(j) != essai.get(j)) {
                        ret = false;
                    }
                    j++;
                }
            }
            i++;
        }

        return ret;
    }

    /**
     * Test la méthode estPresent
     */
    void testEstPresent() {
        System.out.println("*** testEstPresent() ***");
        ArrayList<Integer> essai = new ArrayList<Integer>();
        essai.add(1);
        essai.add(3);
        essai.add(2);
        essai.add(4);
        essai.add(5);

        ArrayList<Integer> essaiClean = cleanEssai(essai);

        SIT_PERD.clear();
        if (estPresent(essaiClean, SIT_PERD)) {
            System.out.println("Element non présent: ERREUR");
        } else {
            System.out.println("Element non présent: OK");
        }

        SIT_PERD.add(essaiClean);
        if (estPresent(essaiClean, SIT_PERD)) {
            System.out.println("Element présent: OK");
        } else {
            System.out.println("Element présent: ERREUR");
        }
    }

    /**
     * Ajoute un essai trié dans le tableau
     *
     * @param essai    essai à chercher
     * @param sitSaved tableau de situations perdantes ou gagnantes
     */
    void ajouterEssai(ArrayList<Integer> essai, ArrayList<ArrayList<Integer>> sitSaved) {
        ArrayList<Integer> essaiClean = cleanEssai(essai); // On nettoie l'essai
        if (estPossible(essaiClean)) { // Si valeurs > 2
            if (!estPresent(essaiClean, sitSaved)) { // Si l'essai n'est pas deja connu
                if (sitSaved.size() == 0) { // Si le tableau est vide on le sauvegarde directement
                    sitSaved.add(essaiClean);
                } else { // Sinon on cherche ou le placer

                    // Parcours jusqu'a trouver le premier essai de meme taille
                    int i = 0;
                    while (i < sitSaved.size() && sitSaved.get(i).size() < essaiClean.size()) {
                        i++;
                    }

                    if (i == sitSaved.size()) { // Si on a pas trouve d'essai de meme taille
                        sitSaved.add(essaiClean); // On l'ajoute a la fin
                    } else { // Sinon on l'ajoute avant
                        sitSaved.add(i, essaiClean);
                    }
                }
            }
        }
    }

    /**
     * Test la méthode ajouterEssai
     */
    void testAjouterEssai() {
        System.out.println("*** testAjouterEssai() ***");
        SIT_PERD.clear();
        boolean error = false;

        // Ajout d'un essai
        ArrayList<Integer> essai1 = new ArrayList<Integer>();
        essai1.add(4);

        ArrayList<ArrayList<Integer>> sitSavedAtt = new ArrayList<ArrayList<Integer>>();
        sitSavedAtt.add(essai1);
        ajouterEssai(essai1, SIT_PERD);
        if (!SIT_PERD.equals(sitSavedAtt)) {
            System.out.println("ERREUR : L'ajout d'un essai d'un seul élément n'a pas fonctionné");
            error = true;
        }

        // Ajout d'un essai qui existe deja
        ajouterEssai(essai1, SIT_PERD);
        if (!SIT_PERD.equals(sitSavedAtt)) {
            System.out.println("ERREUR : L'ajout d'un essai d'un seul élément qui existe déjà n'a pas fonctionné");
            error = true;
        }

        // Ajout d'un essai de deux éléments
        ArrayList<Integer> essai2 = new ArrayList<Integer>();
        essai2.add(3);
        essai2.add(5);
        sitSavedAtt.add(essai2);
        ajouterEssai(essai2, SIT_PERD);
        if (!SIT_PERD.equals(sitSavedAtt)) {
            System.out.println("ERREUR : L'ajout d'un essai de deux éléments n'a pas fonctionné");
            error = true;
        }

        // Ajout d'un essai d'un élément trié avant un essai de deux éléments
        ArrayList<Integer> essai3 = new ArrayList<Integer>();
        essai3.add(5);
        sitSavedAtt.add(0, essai3);
        ajouterEssai(essai3, SIT_PERD);
        if (!SIT_PERD.equals(sitSavedAtt)) {
            System.out.println(
                    "ERREUR : L'ajout d'un essai d'un élément trié avant un essai de deux éléments n'a pas fonctionné");
            error = true;
        }

        if (!error) {
            System.out.println("Tous les tests ont fonctionné");
        }
    }

    /**
     * Joue le coup gagnant s'il existe
     *
     * @param jeu plateau de jeu
     * @return vrai s'il y a un coup gagnant, faux sinon
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
                ArrayList<Integer> essaiClean = cleanEssai(essai);

                if (estPerdante(essaiClean)) {
                    ajouterEssai(essaiClean, SIT_PERD); // On sauvegarde la situation en tant que perdante

                    jeu.clear();
                    gagnant = true;
                    for (int i = 0; i < essai.size(); i++) { // on met à jour le jeu
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
     * Méthode RECURSIVE qui indique si la configuration (du jeu actuel ou jeu
     * d'essai) est perdante
     *
     * @param jeu plateau de jeu actuel (l'état du jeu à un certain moment au cours
     *            de la partie)
     * @return vrai si la configuration (du jeu) est perdante, faux sinon
     */
    boolean estPerdante(ArrayList<Integer> jeu) {
        boolean ret = true; // par défaut la configuration est perdante

        if (jeu == null) {
            System.err.println("estPerdante(): le paramètre jeu est null");
        }

        else {
            // si il n'y a plus que des tas de 1 ou 2 allumettes dans le plateau de jeu
            // alors la situation est forcément perdante (ret=true) = FIN de la récursivité
            if (!estPossible(jeu)) {
                ret = true;
            } else {

                if (estPresent(jeu, SIT_GAGN)) { // Si la situaiton est dans la liste des situations gagnantes
                    ret = false;
                } else {
                    if (estPresent(jeu, SIT_PERD)) { // Si la situaiton est dans la liste des situations perdantes
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

                            ArrayList<Integer> essaiClean = cleanEssai(essai);

                            if (estPerdante(essaiClean)) {
                                ajouterEssai(essaiClean, SIT_PERD); // On sauvegarde l'essai en tant que perdant
                                ajouterEssai(jeu, SIT_GAGN); // On sauvegarde le jeu en tant que gagnant
                                ret = false;
                            } else {
                                // génère la configuration d'essai suivante (c'est-à-dire UNE décomposition
                                // possible)
                                // à partir du jeu, si ligne = -1 il n'y a plus de décomposition possible
                                ligne = suivant(jeu, essai, ligne);
                            }

                            CPT++;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Tests succincts de la méthode joueurGagnant()
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
     * Test d'un cas de la méthode jouerGagnant()
     *
     * @param jeu    le plateau de jeu
     * @param resJeu le plateau de jeu après avoir joué gagnant
     * @param res    le résultat attendu par jouerGagnant
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
     * Divise en deux tas les alumettes d'une ligne de jeu (1 ligne = 1 tas)
     *
     * @param jeu   tableau des alumettes par ligne
     * @param ligne ligne (tas) sur laquelle les alumettes doivent être séparées
     * @param nb    nombre d'alumettes RETIREE de la ligne après séparation
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
     * Teste s'il est possible de séparer un des tas
     *
     * @param jeu plateau de jeu
     * @return vrai s'il existe au moins un tas de 3 allumettes ou plus, faux sinon
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
     * Crée une toute première configuration d'essai à partir du jeu
     *
     * @param jeu      plateau de jeu
     * @param jeuEssai nouvelle configuration du jeu
     * @return le numéro du tas divisé en deux ou (-1) si il n'y a pas de tas d'au
     *         moins 3 allumettes
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
     * Tests succincts de la méthode premier()
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
     * Test un cas de la méthode testPremier
     *
     * @param jeu   le plateau de jeu
     * @param ligne le numéro du tas séparé en premier
     * @param res   le plateau de jeu après une première séparation
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
     * Génère la configuration d'essai suivante (c'est-à-dire UNE décomposition
     * possible)
     *
     * @param jeu      plateau de jeu
     * @param jeuEssai configuration d'essai du jeu après séparation
     * @param ligne    le numéro du tas qui est le dernier à avoir été séparé
     * @return le numéro du tas divisé en deux pour la nouvelle configuration, -1 si
     *         plus aucune décomposition n'est possible
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
     * Tests succincts de la méthode suivant()
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
     * Test un cas de la méthode suivant
     *
     * @param jeu      le plateau de jeu
     * @param jeuEssai le plateau de jeu obtenu après avoir séparé un tas
     * @param ligne    le numéro du tas qui est le dernier à avoir été séparé
     * @param resJeu   est le jeuEssai attendu après séparation
     * @param resLigne est le numéro attendu du tas qui est séparé
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
     * Tests d'efficacité de la méthode jouerGagnant()
     */
    void testJouerGagnantEfficacite() {
        System.out.println();
        System.out.println("*** testJouerGagnantEfficacite() ***");
        long debut, fin;
        double ms;

        int n = 3;
        ArrayList<Integer> jeu = new ArrayList<Integer>();

        while (n <= nbAlluMaxTestEfficacite) { // Teste l'efficacité avec un n allant de 3 à nbAlluMaxTestEfficacite
            // Reset des variables
            CPT = 0;
            SIT_PERD.clear();
            SIT_GAGN.clear();
            jeu.clear();
            jeu.add(n);

            debut = System.nanoTime();
            jouerGagnant(jeu);
            fin = System.nanoTime();
            ms = (fin - debut) / 1000000;

            System.out
                    .println("Pour n = " + n + ": \tTemps d'exécution = " + ms + " ms \tCompteur = " + CPT);
            n++;
        }
    }

    /**
     * Affiche le plateau de jeu
     *
     * @param plateau le plateau du jeu
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
     * Tour du joueur :
     * - Demande la ligne à jouer
     * - Demande le nombre de tas à séparer
     * Separe les tas
     *
     * @param plateau tableau du jeu
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
     * Tour de l'ordinateur :
     * - Joue gagnant si possible
     * - Joue aléatoirement sinon
     *
     * @param plateau tableau du jeu
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
     * Initialise la partie
     * Fait jouer tour à tour l'ordinateur et le joueur.
     * S'arrete lorsqu'il y a un gagnant et l'affiche.
     */
    void partieJoueurOrdinateur() {
        String nomJoueur = SimpleInput.getString("Nom du joueur : ");
        int nbAllumettes = SimpleInput.getInt("Nombre d'allumettes : ");
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