package com.excilys.kataspoker;

/**
 * Interface stratégie pour découpler la prise de décision du joueur.
 * Implémentée par StrategieHumaine (Scanner) et StrategieBot (IA).
 */
public interface StrategieJoueur {

    /**
     * Choisit l'action à effectuer dans le contexte donné.
     *
     * @param ctx          contexte de décision (main, board, pot, etc.)
     * @param miseActuelle la mise en cours (0 si personne n'a misé)
     * @return l'action choisie
     */
    Actions choisirAction(ContexteDecision ctx, int miseActuelle);

    /**
     * Choisit le montant de la mise (pour MISER ou RELANCER).
     *
     * @param ctx          contexte de décision
     * @param miseMinimale la mise minimale à placer (doit être > à cette valeur)
     * @return le montant choisi
     */
    int choisirMise(ContexteDecision ctx, int miseMinimale);
}
