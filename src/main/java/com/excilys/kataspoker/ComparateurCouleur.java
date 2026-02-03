package com.excilys.kataspoker;

import java.util.Comparator;

public class ComparateurCouleur implements Comparator<Carte> {

    public int compare(Carte c1, Carte c2) {
        return c1.getCouleur().compareTo(c2.getCouleur());
    }
}
