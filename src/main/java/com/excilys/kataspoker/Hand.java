package com.excilys.kataspoker;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private static final int TAILLE_MAX = 5;
    private List<Carte> hand = new ArrayList<Carte>();

    public Hand() {
    }

    public List<Carte> getHand() {
        return hand;
    }

    public void addHand(List<Carte> cartesPioches) {
        if(this.hand.size() + cartesPioches.size() > TAILLE_MAX) {
            System.out.println("Erreur : La main du joueur est supérieur au maximum autorisé !");
            return;
        }
        this.hand.addAll(cartesPioches);
    }

    public void afficher() {
        System.out.println(">>  Cartes piochés :");
        for (int i = 0; i < this.hand.size(); i++) {
            System.out.println(i + 1 + " - " + this.hand.get(i).getValeur() + " de " + this.hand.get(i).getCouleur());
        }
    }

}
