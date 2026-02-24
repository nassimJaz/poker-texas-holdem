package com.excilys.kataspoker.strategy;

import com.excilys.kataspoker.model.Actions;

/**
 * Interface stratégie pour découpler la prise de décision du joueur.
 * Implémentée par StrategieHumaine (Scanner) et StrategieBot (IA).
 */
public interface StrategieJoueur {

    Actions choisirAction(ContexteDecision ctx, int miseActuelle);

    int choisirMise(ContexteDecision ctx, int miseMinimale);
}
