package com.excilys.kataspoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("\u001B[1m\u001B[36m");
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║    ♠  POKER TEXAS HOLD'EM  ♥        ║");
        System.out.println("║         Version CLI                 ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("\u001B[0m");

        Scanner sc = new Scanner(System.in);

        System.out.print("Combien de joueurs ? (2-8) : ");
        int nbJoueurs = sc.nextInt();
        sc.nextLine(); // nettoyage buffer

        if (nbJoueurs < 2 || nbJoueurs > 8) {
            System.out.println("Nombre de joueurs invalide (2 à 8 requis).");
            return;
        }

        List<Joueur> joueurs = new ArrayList<>();
        for (int i = 0; i < nbJoueurs; i++) {
            System.out.print("Pseudo du joueur " + (i + 1) + " : ");
            String pseudo = sc.nextLine();
            joueurs.add(new Joueur(pseudo));
        }

        Table table = new Table(joueurs);

        System.out.println("\n\u001B[1m🎰 La partie commence avec " + nbJoueurs + " joueurs !\u001B[0m");
        System.out.println("Chaque joueur démarre avec 5000 jetons.\n");

        int manche = 1;
        while (table.nbJoueursEnVie() > 1) {
            System.out.println("\n\u001B[1m\u001B[33m════════════════════════════════════════\u001B[0m");
            System.out.println("\u001B[1m\u001B[33m           MANCHE " + manche + "\u001B[0m");
            System.out.println("\u001B[1m\u001B[33m════════════════════════════════════════\u001B[0m");

            table.mancheTable();
            table.eliminerJoueurs();

            if (table.nbJoueursEnVie() > 1) {
                System.out.print("\nAppuie sur Entrée pour la manche suivante...");
                sc.nextLine();
                sc.nextLine(); // double pour nettoyer le buffer du scanner après les int
            }

            manche++;
        }

        // Fin de la partie
        Joueur gagnant = table.getGagnantPartie();
        System.out.println("\n\u001B[1m\u001B[32m╔══════════════════════════════════════╗\u001B[0m");
        System.out.println("\u001B[1m\u001B[32m║         🏆 FIN DE LA PARTIE 🏆      ║\u001B[0m");
        if (gagnant != null) {
            System.out.println("\u001B[1m\u001B[32m║  Gagnant : " + gagnant.getPseudo() + "\u001B[0m");
            System.out.println("\u001B[1m\u001B[32m║  Capital final : " + gagnant.getCapital() + " jetons\u001B[0m");
        }
        System.out.println("\u001B[1m\u001B[32m╚══════════════════════════════════════╝\u001B[0m");

        sc.close();
    }
}