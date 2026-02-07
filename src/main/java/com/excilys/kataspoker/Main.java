package com.excilys.kataspoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        /*
        Paquet paquet = new Paquet();

        //paquet.afficher();
        paquet.shuffle();
        //paquet.afficher();


        Hand main = new Hand();
        List<Carte> pioches = paquet.piocher(3);
        main.addHand(pioches);
        main.afficher();
         */

        Scanner sc = new Scanner(System.in);
        List<Joueur> joueurstest = new ArrayList<Joueur>();
        for (int i = 0; i < 3; i++) {
           System.out.println("Le pseudo du joueur [" + i + "] : ");
           String pseudo = sc.nextLine();
           joueurstest.add(new Joueur(pseudo));
        }
        Table table = new Table(joueurstest);

        table.initialiserManche();

        for (int i = 0; i < joueurstest.size(); i++) {
            System.out.println("[" + i + "] Cartes du Joueur : " + joueurstest.get(i).getPseudo());
            joueurstest.get(i).getHand().afficher();
        }

        

    }
}