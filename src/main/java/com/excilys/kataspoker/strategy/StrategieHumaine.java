package com.excilys.kataspoker.strategy;

import com.excilys.kataspoker.model.Actions;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

/**
 * Stratégie pour les joueurs humains.
 * Utilise le Scanner pour demander les actions et montants au joueur via la
 * CLI.
 */
public class StrategieHumaine implements StrategieJoueur {

    private final Scanner scanner;

    public StrategieHumaine(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Actions choisirAction(ContexteDecision ctx, int miseActuelle) {

        while (true) {
            System.out.println("\n  Choisis ton action :");

            int numero = 1;
            Map<Integer, Actions> actionsPossibles = new HashMap<>();

            if (miseActuelle == 0) {
                System.out.println("  " + numero + " - CHECKER");
                actionsPossibles.put(numero++, Actions.CHECKER);
                System.out.println("  " + numero + " - MISER");
                actionsPossibles.put(numero++, Actions.MISER);
            }

            if (miseActuelle > 0) {
                System.out.println("  " + numero + " - SUIVRE (" + miseActuelle + ")");
                actionsPossibles.put(numero++, Actions.SUIVRE);
                System.out.println("  " + numero + " - RELANCER");
                actionsPossibles.put(numero++, Actions.RELANCER);
            }

            System.out.println("  " + numero + " - PASSER");
            actionsPossibles.put(numero, Actions.PASSER);

            System.out.print("  > ");
            try {
                int choix = scanner.nextInt();

                if (!actionsPossibles.containsKey(choix)) {
                    System.out.println("  ❌ Choix invalide, recommence.");
                    continue;
                }

                return actionsPossibles.get(choix);

            } catch (InputMismatchException e) {
                System.out.println("  ❌ Entrée invalide.");
                scanner.nextLine();
            }
        }
    }

    @Override
    public int choisirMise(ContexteDecision ctx, int miseMinimale) {
        System.out.println("  Mise actuelle : " + miseMinimale + " | Ton capital : " + ctx.getCapital());
        System.out.print("  Montant de la mise : ");

        while (true) {
            try {
                int mise = scanner.nextInt();
                if (mise <= miseMinimale) {
                    System.out.print("  ❌ La mise doit être > " + miseMinimale + ". Réessaie : ");
                } else if (mise > ctx.getCapital()) {
                    System.out.print("  ❌ Tu n'as pas assez (max " + ctx.getCapital() + "). Réessaie : ");
                } else {
                    return mise;
                }
            } catch (InputMismatchException e) {
                System.out.print("  ❌ Nombre invalide. Réessaie : ");
                scanner.nextLine();
            }
        }
    }
}
