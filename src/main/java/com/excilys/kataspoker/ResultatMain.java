package com.excilys.kataspoker;

import java.util.List;

public class ResultatMain implements Comparable<ResultatMain> {

    private final Combinaison combinaison;
    private final List<Integer> valeursCles; // rangs pour départager (du plus important au moins important)

    public ResultatMain(Combinaison combinaison, List<Integer> valeursCles) {
        this.combinaison = combinaison;
        this.valeursCles = valeursCles;
    }

    public Combinaison getCombinaison() {
        return combinaison;
    }

    public List<Integer> getValeursCles() {
        return valeursCles;
    }

    @Override
    public int compareTo(ResultatMain autre) {
        // D'abord comparer la force de la combinaison
        int cmp = Integer.compare(this.combinaison.getForce(), autre.combinaison.getForce());
        if (cmp != 0)
            return cmp;

        // En cas d'égalité, comparer les valeurs clés une par une
        for (int i = 0; i < Math.min(valeursCles.size(), autre.valeursCles.size()); i++) {
            cmp = Integer.compare(this.valeursCles.get(i), autre.valeursCles.get(i));
            if (cmp != 0)
                return cmp;
        }

        return 0; // Parfaite égalité
    }

    @Override
    public String toString() {
        return combinaison.getNom();
    }
}
