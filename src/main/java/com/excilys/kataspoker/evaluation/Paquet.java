package com.excilys.kataspoker.evaluation;

import com.excilys.kataspoker.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Paquet {

    private List<Carte> paquet = new ArrayList<Carte>();

    public Paquet() {
        init();
    }

    private void init() {
        for (Couleur couleur : Couleur.values()) {
            for (Valeurs valeurs : Valeurs.values()) {
                Carte carte = new Carte(valeurs, couleur);
                paquet.add(carte);
            }
        }
    }

    public List<Carte> getPaquet() {
        return paquet;
    }

    public void shuffle() {
        Collections.shuffle(paquet);
    }

    public Carte piocher() {
        Carte c = this.paquet.get(paquet.size() - 1);
        paquet.remove(c);
        return c;
    }

    public void afficher() {
        System.out.println(">>  Paquet mélangé :");
        for (int i = 0; i < paquet.size(); i++) {
            System.out.println(paquet.get(i).getValeur() + " de " + paquet.get(i).getCouleur());
        }
    }

    public void sortCouleur() {
        paquet.sort(new ComparateurCouleur());
    }

    public void sortValeur() {
        paquet.sort(new ComparateurValeur());
    }

    public void reset() {
        paquet.clear();
        init();
    }
}
