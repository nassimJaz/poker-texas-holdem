package com.excilys.kataspoker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Table {

    private List<Joueur> joueurs = new ArrayList<Joueur>();
    private int nbJoueurs = 0;
    private Paquet paquet = new Paquet();

    private List<Carte> board = new ArrayList<Carte>();
    private int pot;
    private int indexDealer;
    private static final int PRIX_BLINDE = 25;
    private static final int PRIX_GROSSE_BLINDE = PRIX_BLINDE * 2;

    private Scanner scanner = new Scanner(System.in);

    public Table(List<Joueur> joueurs) {
        this.joueurs = joueurs;
        this.nbJoueurs = this.joueurs.size();
        this.indexDealer = 0;
        this.pot = 0;
    }

    public Joueur getJoueur(int indexJoueur) {
        /*
         * Récupère le joueur en rebouclant automatiquement (avec le modulo)
         * si l'index dépasse de la liste
         */
        int index = indexJoueur % nbJoueurs;
        return joueurs.get(index);
    }

    public Joueur getJoueurSuivant(int indexJoueur) {
        // Avance d'un joueur modulo appliqué
        int indexSuivant = (indexJoueur + 1) % nbJoueurs;
        return joueurs.get(indexSuivant);
    }

    public Joueur getJoueurSuivant(int indexJoueur, int decalage) {
        int index = (indexJoueur + decalage) % nbJoueurs;
        return joueurs.get(index);
    }

    public void nbCartesBoard(int nbCartes) {
        for (int i = 0; i < nbCartes; i++) {
            Carte c = paquet.piocher();
            board.add(c);
        }
    }

    public void nbCartesParJoueur(int nbCartes) {
        for (int i = 0; i < nbCartes * this.nbJoueurs; i++) {
            Carte carte = paquet.piocher();
            this.getJoueur(i).getHand().addHand(carte);
        }
    }

    public void transfertMise(int mise, Joueur j) {
        int montant = j.miser(mise);
        if (montant < mise) {
            // Le joueur a all-in
            // Le joueur peut uniquement avoir le pot actuel, ne peut pas avoir un pot
            // supérieur
        }
        pot += montant;
    }

    public void initialiserMises() {
        Joueur joueurPetiteBlinde = getJoueurSuivant(indexDealer); // Petite blinde vient après le dealer
        Joueur joueurGrosseBlinde = getJoueurSuivant(indexDealer, 2); // Grosse blinde 2 fois après le dealer

        System.out.println("\n=== BLINDES ===");
        System.out.println(joueurPetiteBlinde.getPseudo() + " pose la petite blinde : " + PRIX_BLINDE);
        this.transfertMise(PRIX_BLINDE, joueurPetiteBlinde);
        System.out.println(joueurGrosseBlinde.getPseudo() + " pose la grosse blinde : " + PRIX_GROSSE_BLINDE);
        this.transfertMise(PRIX_GROSSE_BLINDE, joueurGrosseBlinde);
        System.out.println("===============\n");

    }

    public void initialiserManche() {
        paquet.shuffle();
        nbCartesParJoueur(3); // La table distribue 3 cartes par joueur
        initialiserMises();
    }

    public void mancheTable() {
        initialiserManche();
        // Tour de pré-flop
        afficherBoard();
        int indexPremierParole = (indexDealer + 3) % nbJoueurs;
        tourDeMise(indexPremierParole, PRIX_GROSSE_BLINDE);
        // Tour de flop
        nbCartesBoard(3);
        afficherBoard();
        indexPremierParole = (indexDealer + 1) % nbJoueurs;
        tourDeMise(indexPremierParole, 0);
        // Tour de turn
        nbCartesBoard(1);
        afficherBoard();
        tourDeMise(indexPremierParole, 0);
        // Tour de rivière (dernier)
        nbCartesBoard(1);
        afficherBoard();
        tourDeMise(indexPremierParole, 0);
        indexDealer += 1;
    }

    public void resetActionsJoueurs(boolean resetHard) {
        for (int i = 0; i < nbJoueurs; i++) {
            Joueur j = getJoueur(i);
            j.setAction(null);
            if (resetHard)
                j.resetEtatAllIn();
        }
    }

    public void demanderAction(Joueur joueur, Scanner scanner, int miseActuelle) {
        System.out.println("\n*** Tour de " + joueur.getPseudo() + " ***");
        System.out.println("Capital actuel : " + joueur.getCapital());
        joueur.getHand().afficher();

        boolean choixValide = false;

        while (!choixValide) {

            System.out.println(joueur.getPseudo() + ", choisis ton action :");

            int numero = 1;
            Map<Integer, Actions> actionsPossibles = new HashMap<>();

            // CHECK possible seulement si aucune mise
            if (miseActuelle == 0) {
                System.out.println(numero + " - CHECKER");
                actionsPossibles.put(numero++, Actions.CHECKER);

                System.out.println(numero + " - MISER");
                actionsPossibles.put(numero++, Actions.MISER);
            }

            // SUIVRE possible seulement s'il y a une mise
            if (miseActuelle > 0) {
                System.out.println(numero + " - SUIVRE");
                actionsPossibles.put(numero++, Actions.SUIVRE);

                System.out.println(numero + " - RELANCER");
                actionsPossibles.put(numero++, Actions.RELANCER);
            }

            // PASSER toujours possible
            System.out.println(numero + " - PASSER");
            actionsPossibles.put(numero, Actions.PASSER);

            try {
                int choix = scanner.nextInt();

                if (!actionsPossibles.containsKey(choix)) {
                    System.out.println("❌ Choix invalide, recommence.");
                    continue;
                }

                joueur.setAction(actionsPossibles.get(choix));
                choixValide = true;

            } catch (InputMismatchException e) {
                System.out.println("Entrée invalide.");
                scanner.nextLine(); // nettoyage buffer
            }
        }
    }

    public int demanderMise(Joueur joueur, int miseEnCours, Scanner sc) {
        System.out.println("La mise actuelle est : " + miseEnCours);
        System.out.println("Ton capital actuel est : " + joueur.getCapital());
        System.out.println("Quel est le montant de la mise " + joueur.getPseudo() + " ?");
        boolean miseOK = false;
        int mise = miseEnCours;
        while (!miseOK) {
            mise = sc.nextInt();
            if (mise <= miseEnCours) {
                System.out.println("La mise doit être supérieur à celle en cours, quel est le montant de la mise ?");
                miseOK = false;
            } else if (mise > joueur.getCapital()) {
                System.out.println("La mise ne peut être supérieur à ton capital, quel est le montant de la mise ?");
                miseOK = false;
            } else
                miseOK = true;
        }
        return mise;
    }

    public void tourDeMise(int indexPremierJoueur, int miseActuelle) {

        Map<Joueur, Integer> misesTour = new HashMap<>();

        for (Joueur j : joueurs) {
            misesTour.put(j, 0);
        }

        int joueursActifs = nbJoueurs;
        int joueursARepondre = joueursActifs;

        int indexCourant = indexPremierJoueur;

        while (joueursARepondre > 0) {

            Joueur joueur = getJoueur(indexCourant);

            if (joueur.getEtatAllIn() || joueur.getAction() == Actions.PASSER) {
                indexCourant++;
                continue;
            }

            demanderAction(joueur, scanner, miseActuelle);
            Actions action = joueur.getAction();

            switch (action) {

                case CHECKER:
                    joueursARepondre--;
                    break;

                case MISER:
                    miseActuelle = demanderMise(joueur, miseActuelle, scanner);
                    transfertMise(miseActuelle, joueur);
                    misesTour.put(joueur, miseActuelle);

                    joueursARepondre = joueursActifs - 1; // tout le monde doit répondre
                    break;

                case SUIVRE:
                    int dejaMis = misesTour.get(joueur);
                    int aPayer = miseActuelle - dejaMis;

                    if (aPayer > 0) {
                        transfertMise(aPayer, joueur);
                        misesTour.put(joueur, miseActuelle);
                    }

                    joueursARepondre--;
                    break;

                case RELANCER:
                    int nouvelleMise = demanderMise(joueur, miseActuelle, scanner);

                    int dejaMisRelance = misesTour.get(joueur);
                    int aPayerRelance = nouvelleMise - dejaMisRelance;

                    transfertMise(aPayerRelance, joueur);

                    miseActuelle = nouvelleMise;
                    misesTour.put(joueur, miseActuelle);

                    joueursARepondre = joueursActifs - 1; // tout le monde doit reparler
                    break;

                case PASSER:
                    joueursActifs--;
                    joueursARepondre--;
                    break;
            }

            indexCourant++;
        }

        resetActionsJoueurs(false);
    }

    public void afficherBoard() {
        System.out.println("\n=== BOARD ===");
        if (board.isEmpty()) {
            System.out.println("Aucune carte sur la board");
        } else {
            System.out.print("Board: ");
            for (int i = 0; i < board.size(); i++) {
                Carte carte = board.get(i);
                System.out.print(carte.getValeur() + " de " + carte.getCouleur());
                if (i < board.size() - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
        }
        System.out.println("Pot: " + pot);
        System.out.println("=============\n");
    }

}
