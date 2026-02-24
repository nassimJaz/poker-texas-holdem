package com.excilys.kataspoker;

import com.excilys.kataspoker.game.Joueur;
import com.excilys.kataspoker.game.Table;
import com.excilys.kataspoker.strategy.StrategieBot;
import com.excilys.kataspoker.strategy.StrategieHumaine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String[] NOMS_BOTS = {
            "Bot-Alice", "Bot-Bob", "Bot-Charlie", "Bot-Diana",
            "Bot-Eve", "Bot-Frank", "Bot-Grace"
    };

    public static void main(String[] args) {

        System.out.println("\u001B[1m\u001B[36m");
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║    ♠  POKER TEXAS HOLD'EM  ♥        ║");
        System.out.println("║         Version CLI                 ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("\u001B[0m");

        Scanner sc = new Scanner(System.in);

        System.out.println("  Choisis ton mode de jeu :");
        System.out.println("  1 - 👥 Multijoueur (tous humains)");
        System.out.println("  2 - 🤖 Solo vs Bots");
        System.out.print("  > ");
        int mode = sc.nextInt();
        sc.nextLine();

        List<Joueur> joueurs;

        if (mode == 2) {
            joueurs = creerPartieSoloBots(sc);
        } else {
            joueurs = creerPartieMultijoueur(sc);
        }

        if (joueurs == null || joueurs.size() < 2) {
            System.out.println("Pas assez de joueurs. Au revoir !");
            sc.close();
            return;
        }

        Table table = new Table(joueurs);

        System.out.println("\n\u001B[1m🎰 La partie commence avec " + joueurs.size() + " joueurs !\u001B[0m");
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
                sc.nextLine();
            }

            manche++;
        }

        Joueur gagnant = table.getGagnantPartie();
        System.out.println("\n\u001B[1m\u001B[32m╔══════════════════════════════════════╗\u001B[0m");
        System.out.println("\u001B[1m\u001B[32m║         🏆 FIN DE LA PARTIE 🏆      ║\u001B[0m");
        if (gagnant != null) {
            String typeGagnant = gagnant.isBot() ? " (Bot)" : "";
            System.out.println("\u001B[1m\u001B[32m║  Gagnant : " + gagnant.getPseudo() + typeGagnant + "\u001B[0m");
            System.out.println("\u001B[1m\u001B[32m║  Capital final : " + gagnant.getCapital() + " jetons\u001B[0m");
        }
        System.out.println("\u001B[1m\u001B[32m╚══════════════════════════════════════╝\u001B[0m");

        sc.close();
    }

    private static List<Joueur> creerPartieMultijoueur(Scanner sc) {
        System.out.print("\nCombien de joueurs ? (2-8) : ");
        int nbJoueurs = sc.nextInt();
        sc.nextLine();

        if (nbJoueurs < 2 || nbJoueurs > 8) {
            System.out.println("Nombre de joueurs invalide (2 à 8 requis).");
            return null;
        }

        List<Joueur> joueurs = new ArrayList<>();
        for (int i = 0; i < nbJoueurs; i++) {
            System.out.print("Pseudo du joueur " + (i + 1) + " : ");
            String pseudo = sc.nextLine();
            joueurs.add(new Joueur(pseudo, new StrategieHumaine(sc)));
        }
        return joueurs;
    }

    private static List<Joueur> creerPartieSoloBots(Scanner sc) {
        System.out.print("\nTon pseudo : ");
        String pseudo = sc.nextLine();

        System.out.print("Combien de bots ? (1-7) : ");
        int nbBots = sc.nextInt();
        sc.nextLine();

        if (nbBots < 1 || nbBots > 7) {
            System.out.println("Nombre de bots invalide (1 à 7 requis).");
            return null;
        }

        List<Joueur> joueurs = new ArrayList<>();
        joueurs.add(new Joueur(pseudo, new StrategieHumaine(sc)));

        for (int i = 0; i < nbBots; i++) {
            String nomBot = NOMS_BOTS[i];
            joueurs.add(new Joueur(nomBot, new StrategieBot(nomBot)));
        }

        System.out.println("\n🤖 Bots ajoutés :");
        for (int i = 0; i < nbBots; i++) {
            System.out.println("  - " + NOMS_BOTS[i]);
        }

        return joueurs;
    }
}