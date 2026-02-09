package com.excilys.kataspoker;


import java.util.ArrayList;
import java.util.List;
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
        /* Récupère le joueur en rebouclant automatiquement (avec le modulo)
         * si l'index dépasse de la liste */
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
        for (int i = 0; i < nbCartes*this.nbJoueurs; i++) {
            Carte carte = paquet.piocher();
            this.getJoueur(i).getHand().addHand(carte);
        }
    }

    public void transfertMise(int mise, Joueur j) {
        int montant = j.miser(mise);
        if(montant < mise) {
            // Le joueur a all-in
            // Le joueur peut uniquement avoir le pot actuel, ne peut pas avoir un pot supérieur
        }
        pot += montant;
    }

    public void initialiserMises() {
        Joueur joueurPetiteBlinde = getJoueurSuivant(indexDealer); // Petite blinde vient après le dealer
        Joueur joueurGrosseBlinde = getJoueurSuivant(indexDealer, 2); // Grosse blinde 2 fois après le dealer

        this.transfertMise(PRIX_BLINDE, joueurPetiteBlinde);
        this.transfertMise(PRIX_GROSSE_BLINDE, joueurGrosseBlinde);

    }

    public void initialiserManche() {
        paquet.shuffle();
        nbCartesParJoueur(3); // La table distribue 3 cartes par joueur
        initialiserMises();
    }

    public void mancheTable() {
        initialiserMises();
        // Tour de pré-flop
        int indexPremierParole = (indexDealer + 3) % nbJoueurs;
        tourDeMise(indexPremierParole, PRIX_GROSSE_BLINDE);
        // Tour de flop
        nbCartesBoard(3);

    }

    public void resetActionsJoueurs() {
        for (int i = 0; i < nbJoueurs; i++) {
            getJoueur(i).setAction(null);
        }
    }

    public void demanderAction(Joueur joueur, Scanner scanner) {
        System.out.println(joueur.getPseudo() + ", choisis ton action :");
        System.out.println("1 - CHECKER");
        System.out.println("2 - MISER");
        System.out.println("3 - SUIVRE");
        System.out.println("4 - RELANCER");
        System.out.println("5 - PASSER");

        int choix = scanner.nextInt();

        switch (choix) {
            case 1 :
                joueur.setAction(Actions.CHECKER);
                break;
            case 2 :
                joueur.setAction(Actions.MISER);
                break;
            case 3 :
                joueur.setAction(Actions.SUIVRE);
                break;
            case 4 :
                joueur.setAction(Actions.RELANCER);
                break;
            case 5 :
                joueur.setAction(Actions.PASSER);
                break;
            default :
                throw new IllegalArgumentException("Choix invalide");
        }
    }

    public void tourDeMise(int indexPremierJoueur, int miseActuelle) {

        boolean relance = true;

        // Tant qu'il y a une relance, le tour continue
        while (relance) {
            relance = false;

            for (int i = 0; i < nbJoueurs; i++) {
                Joueur joueur = getJoueur(indexPremierJoueur + i);

                // Joueur all-in ou déjà couché => on ignore
                if (joueur.getEtatAllIn() || joueur.getAction() == Actions.PASSER) {
                    continue;
                }

                demanderAction(joueur, scanner); // A améliorer si erreur
                Actions action = joueur.getAction();

                switch (action) {

                    case CHECKER:
                        // Autorisé uniquement si aucune mise
                        if (miseActuelle > 0) {
                            throw new IllegalStateException("Impossible de checker, une mise existe");
                        }
                        break;

                    case MISER:
                        // Première mise du tour
                        miseActuelle = PRIX_GROSSE_BLINDE;
                        transfertMise(miseActuelle, joueur);
                        relance = true;
                        break;

                    case SUIVRE:
                        transfertMise(miseActuelle, joueur);
                        break;

                    case RELANCER:
                        int nouvelleMise = miseActuelle + PRIX_GROSSE_BLINDE;
                        miseActuelle = nouvelleMise;
                        transfertMise(miseActuelle, joueur);
                        relance = true;
                        break;

                    case PASSER:
                        // Le joueur est hors du coup
                        break;
                }
            }
        }

        // Nettoyage pour le prochain tour
        resetActionsJoueurs();
    }






}
