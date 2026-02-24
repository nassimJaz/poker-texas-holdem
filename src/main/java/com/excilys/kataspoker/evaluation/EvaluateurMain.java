package com.excilys.kataspoker.evaluation;

import com.excilys.kataspoker.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EvaluateurMain {

    /**
     * Évalue la meilleure main de 5 cartes parmi N cartes (N >= 5).
     * Teste les C(N,5) combinaisons possibles.
     */
    public static ResultatMain evaluer(List<Carte> cartes) {
        ResultatMain meilleur = null;

        List<List<Carte>> combos = combinaisons5parmi(cartes);
        for (List<Carte> main5 : combos) {
            ResultatMain resultat = evaluerMain5(main5);
            if (meilleur == null || resultat.compareTo(meilleur) > 0) {
                meilleur = resultat;
            }
        }
        return meilleur;
    }

    /**
     * Évalue une main de exactement 5 cartes.
     */
    private static ResultatMain evaluerMain5(List<Carte> main) {
        List<Integer> rangs = main.stream()
                .map(c -> c.getValeur().getRang())
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

        boolean flush = isFlush(main);
        boolean quinte = isQuinte(rangs);
        int topQuinte = getTopQuinte(rangs);

        Map<Integer, Integer> compteur = compterRangs(rangs);

        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(compteur.entrySet());
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0)
                return cmp;
            return Integer.compare(b.getKey(), a.getKey());
        });

        List<Integer> groupes = entries.stream().map(Map.Entry::getValue).collect(Collectors.toList());
        List<Integer> rangsParFrequence = entries.stream().map(Map.Entry::getKey).collect(Collectors.toList());

        if (flush && quinte && topQuinte == 14) {
            List<Integer> cles = new ArrayList<>();
            cles.add(14);
            return new ResultatMain(Combinaison.QUINTE_FLUSH_ROYALE, cles);
        }

        if (flush && quinte) {
            List<Integer> cles = new ArrayList<>();
            cles.add(topQuinte);
            return new ResultatMain(Combinaison.QUINTE_FLUSH, cles);
        }

        if (groupes.get(0) == 4) {
            return new ResultatMain(Combinaison.CARRE, rangsParFrequence);
        }

        if (groupes.get(0) == 3 && groupes.get(1) == 2) {
            return new ResultatMain(Combinaison.FULL, rangsParFrequence);
        }

        if (flush) {
            return new ResultatMain(Combinaison.COULEUR_FLUSH, rangs);
        }

        if (quinte) {
            List<Integer> cles = new ArrayList<>();
            cles.add(topQuinte);
            return new ResultatMain(Combinaison.QUINTE, cles);
        }

        if (groupes.get(0) == 3) {
            return new ResultatMain(Combinaison.BRELAN, rangsParFrequence);
        }

        if (groupes.get(0) == 2 && groupes.get(1) == 2) {
            return new ResultatMain(Combinaison.DOUBLE_PAIRE, rangsParFrequence);
        }

        if (groupes.get(0) == 2) {
            return new ResultatMain(Combinaison.PAIRE, rangsParFrequence);
        }

        return new ResultatMain(Combinaison.HAUTE_CARTE, rangs);
    }

    private static boolean isFlush(List<Carte> main) {
        Couleur couleur = main.get(0).getCouleur();
        for (int i = 1; i < main.size(); i++) {
            if (main.get(i).getCouleur() != couleur)
                return false;
        }
        return true;
    }

    private static boolean isQuinte(List<Integer> rangsTries) {
        if (rangsTries.get(0) - rangsTries.get(4) == 4 && sontTousDifferents(rangsTries)) {
            return true;
        }
        if (rangsTries.get(0) == 14 && rangsTries.get(1) == 5
                && rangsTries.get(2) == 4 && rangsTries.get(3) == 3
                && rangsTries.get(4) == 2) {
            return true;
        }
        return false;
    }

    private static int getTopQuinte(List<Integer> rangsTries) {
        if (rangsTries.get(0) == 14 && rangsTries.get(1) == 5) {
            return 5;
        }
        return rangsTries.get(0);
    }

    private static boolean sontTousDifferents(List<Integer> rangs) {
        return rangs.stream().distinct().count() == rangs.size();
    }

    private static Map<Integer, Integer> compterRangs(List<Integer> rangs) {
        Map<Integer, Integer> compteur = new HashMap<>();
        for (int r : rangs) {
            compteur.put(r, compteur.getOrDefault(r, 0) + 1);
        }
        return compteur;
    }

    private static List<List<Carte>> combinaisons5parmi(List<Carte> cartes) {
        List<List<Carte>> result = new ArrayList<>();
        int n = cartes.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    for (int l = k + 1; l < n; l++) {
                        for (int m = l + 1; m < n; m++) {
                            List<Carte> combo = new ArrayList<>();
                            combo.add(cartes.get(i));
                            combo.add(cartes.get(j));
                            combo.add(cartes.get(k));
                            combo.add(cartes.get(l));
                            combo.add(cartes.get(m));
                            result.add(combo);
                        }
                    }
                }
            }
        }
        return result;
    }
}
