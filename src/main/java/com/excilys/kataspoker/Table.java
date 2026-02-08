package com.excilys.kataspoker;


import java.util.ArrayList;
import java.util.List;

public class Table {

    private List<Joueur> joueurs = new ArrayList<Joueur>();
    private int nbJoueurs = 0;
    private Paquet paquet = new Paquet();

    private List<Carte> board = new ArrayList<Carte>();

    public Table(List<Joueur> joueurs) {
        this.joueurs = joueurs;
        this.nbJoueurs = this.joueurs.size();
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

    public void initialiserManche() {
        paquet.shuffle();
        nbCartesParJoueur(3); // La table distribue 3 cartes par joueur
        nbCartesBoard(3);
    }






}
