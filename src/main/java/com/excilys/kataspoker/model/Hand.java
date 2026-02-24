package com.excilys.kataspoker.model;

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

    public void addHand(Carte carte) {
        if (this.hand.size() + 1 > TAILLE_MAX) {
            System.out.println("Erreur : La main du joueur est supérieur au maximum autorisé !");
            return;
        }
        this.hand.add(carte);
    }

    public void addHand(List<Carte> cartes) {
        for (Carte c : cartes) {
            addHand(c);
        }
    }

    public void clear() {
        this.hand.clear();
    }

    public void afficher() {
        System.out.println(">>  Cartes possedés :");
        for (int i = 0; i < this.hand.size(); i++) {
            System.out.println(i + 1 + " - " + this.hand.get(i).getValeur() + " de " + this.hand.get(i).getCouleur());
        }
    }
}
