package com.excilys.kataspoker;

import java.util.Comparator;

public class ComparateurValeur implements Comparator<Carte> {

    public int compare(Carte c1, Carte c2) {
        return c1.getValeur().compareTo(c2.getValeur());
    }
}
