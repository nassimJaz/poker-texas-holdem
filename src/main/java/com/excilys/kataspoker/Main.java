package com.excilys.kataspoker;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        Paquet paquet = new Paquet();

        //paquet.afficher();
        paquet.shuffle();
        //paquet.afficher();


        Hand main = new Hand();
        List<Carte> pioches = paquet.piocher(3);
        main.addHand(pioches);
        main.afficher();

    }
}