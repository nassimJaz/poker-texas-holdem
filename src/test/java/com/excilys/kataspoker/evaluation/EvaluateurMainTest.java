package com.excilys.kataspoker.evaluation;

import com.excilys.kataspoker.model.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class EvaluateurMainTest {

    private Carte carte(Valeurs v, Couleur c) {
        return new Carte(v, c);
    }

    private List<Carte> main7(Carte... cartes) {
        return Arrays.asList(cartes);
    }

    @Test
    void testHauteCarte() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.DEUX, Couleur.COEUR),
                carte(Valeurs.CINQ, Couleur.PIQUE),
                carte(Valeurs.SEPT, Couleur.CARREAUX),
                carte(Valeurs.NEUF, Couleur.TREFLE),
                carte(Valeurs.VALET, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX)));
        assertEquals(Combinaison.HAUTE_CARTE, r.getCombinaison());
    }

    @Test
    void testPaire() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.DEUX, Couleur.COEUR),
                carte(Valeurs.DEUX, Couleur.PIQUE),
                carte(Valeurs.SEPT, Couleur.CARREAUX),
                carte(Valeurs.NEUF, Couleur.TREFLE),
                carte(Valeurs.VALET, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX)));
        assertEquals(Combinaison.PAIRE, r.getCombinaison());
    }

    @Test
    void testDoublePaire() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.DEUX, Couleur.COEUR),
                carte(Valeurs.DEUX, Couleur.PIQUE),
                carte(Valeurs.SEPT, Couleur.CARREAUX),
                carte(Valeurs.SEPT, Couleur.TREFLE),
                carte(Valeurs.VALET, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX)));
        assertEquals(Combinaison.DOUBLE_PAIRE, r.getCombinaison());
    }

    @Test
    void testBrelan() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.SEPT, Couleur.COEUR),
                carte(Valeurs.SEPT, Couleur.PIQUE),
                carte(Valeurs.SEPT, Couleur.CARREAUX),
                carte(Valeurs.NEUF, Couleur.TREFLE),
                carte(Valeurs.VALET, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX)));
        assertEquals(Combinaison.BRELAN, r.getCombinaison());
    }

    @Test
    void testQuinte() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.SIX, Couleur.PIQUE),
                carte(Valeurs.SEPT, Couleur.CARREAUX),
                carte(Valeurs.HUIT, Couleur.TREFLE),
                carte(Valeurs.NEUF, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX)));
        assertEquals(Combinaison.QUINTE, r.getCombinaison());
    }

    @Test
    void testCouleurFlush() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.DEUX, Couleur.COEUR),
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.SEPT, Couleur.COEUR),
                carte(Valeurs.NEUF, Couleur.COEUR),
                carte(Valeurs.VALET, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX)));
        assertEquals(Combinaison.COULEUR_FLUSH, r.getCombinaison());
    }

    @Test
    void testFull() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.SEPT, Couleur.COEUR),
                carte(Valeurs.SEPT, Couleur.PIQUE),
                carte(Valeurs.SEPT, Couleur.CARREAUX),
                carte(Valeurs.ROI, Couleur.TREFLE),
                carte(Valeurs.ROI, Couleur.COEUR),
                carte(Valeurs.DEUX, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX)));
        assertEquals(Combinaison.FULL, r.getCombinaison());
    }

    @Test
    void testCarre() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.NEUF, Couleur.COEUR),
                carte(Valeurs.NEUF, Couleur.PIQUE),
                carte(Valeurs.NEUF, Couleur.CARREAUX),
                carte(Valeurs.NEUF, Couleur.TREFLE),
                carte(Valeurs.VALET, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX)));
        assertEquals(Combinaison.CARRE, r.getCombinaison());
    }

    @Test
    void testQuinteFlush() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.SIX, Couleur.COEUR),
                carte(Valeurs.SEPT, Couleur.COEUR),
                carte(Valeurs.HUIT, Couleur.COEUR),
                carte(Valeurs.NEUF, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX)));
        assertEquals(Combinaison.QUINTE_FLUSH, r.getCombinaison());
    }

    @Test
    void testQuinteFlushRoyale() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.DIX, Couleur.PIQUE),
                carte(Valeurs.VALET, Couleur.PIQUE),
                carte(Valeurs.DAME, Couleur.PIQUE),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.AS, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.COEUR),
                carte(Valeurs.TROIS, Couleur.CARREAUX)));
        assertEquals(Combinaison.QUINTE_FLUSH_ROYALE, r.getCombinaison());
    }

    @Test
    void testComparaisonForce() {
        ResultatMain paire = EvaluateurMain.evaluer(main7(
                carte(Valeurs.AS, Couleur.COEUR), carte(Valeurs.AS, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX), carte(Valeurs.TROIS, Couleur.TREFLE),
                carte(Valeurs.CINQ, Couleur.COEUR), carte(Valeurs.SEPT, Couleur.PIQUE),
                carte(Valeurs.NEUF, Couleur.CARREAUX)));
        ResultatMain brelan = EvaluateurMain.evaluer(main7(
                carte(Valeurs.ROI, Couleur.COEUR), carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.ROI, Couleur.CARREAUX), carte(Valeurs.TROIS, Couleur.TREFLE),
                carte(Valeurs.CINQ, Couleur.COEUR), carte(Valeurs.SEPT, Couleur.PIQUE),
                carte(Valeurs.NEUF, Couleur.CARREAUX)));
        assertTrue(brelan.compareTo(paire) > 0);
    }

    @Test
    void testKickers() {
        ResultatMain paireAs = EvaluateurMain.evaluer(main7(
                carte(Valeurs.AS, Couleur.COEUR), carte(Valeurs.AS, Couleur.PIQUE),
                carte(Valeurs.ROI, Couleur.CARREAUX), carte(Valeurs.DAME, Couleur.TREFLE),
                carte(Valeurs.VALET, Couleur.COEUR), carte(Valeurs.DEUX, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX)));
        ResultatMain paireRois = EvaluateurMain.evaluer(main7(
                carte(Valeurs.ROI, Couleur.COEUR), carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.AS, Couleur.CARREAUX), carte(Valeurs.DAME, Couleur.TREFLE),
                carte(Valeurs.VALET, Couleur.COEUR), carte(Valeurs.DEUX, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX)));
        assertTrue(paireAs.compareTo(paireRois) > 0);
    }

    @Test
    void testRoueQuinte() {
        ResultatMain r = EvaluateurMain.evaluer(main7(
                carte(Valeurs.AS, Couleur.COEUR),
                carte(Valeurs.DEUX, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX),
                carte(Valeurs.QUATRE, Couleur.TREFLE),
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.NEUF, Couleur.CARREAUX)));
        assertEquals(Combinaison.QUINTE, r.getCombinaison());
        assertEquals(5, r.getValeursCles().get(0));
    }

    @Test
    void testEgaliteParfaite() {
        ResultatMain r1 = EvaluateurMain.evaluer(main7(
                carte(Valeurs.AS, Couleur.COEUR), carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.DIX, Couleur.CARREAUX), carte(Valeurs.HUIT, Couleur.TREFLE),
                carte(Valeurs.SIX, Couleur.COEUR), carte(Valeurs.QUATRE, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX)));
        ResultatMain r2 = EvaluateurMain.evaluer(main7(
                carte(Valeurs.AS, Couleur.CARREAUX), carte(Valeurs.ROI, Couleur.TREFLE),
                carte(Valeurs.DIX, Couleur.COEUR), carte(Valeurs.HUIT, Couleur.PIQUE),
                carte(Valeurs.SIX, Couleur.CARREAUX), carte(Valeurs.QUATRE, Couleur.TREFLE),
                carte(Valeurs.DEUX, Couleur.COEUR)));
        assertEquals(0, r1.compareTo(r2));
    }
}
