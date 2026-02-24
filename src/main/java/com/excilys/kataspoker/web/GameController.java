package com.excilys.kataspoker.web;

import com.excilys.kataspoker.web.dto.EtatJeuDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final PokerService pokerService;

    public GameController(PokerService pokerService) {
        this.pokerService = pokerService;
    }

    @PostMapping("/new")
    public EtatJeuDTO creerPartie(@RequestBody Map<String, Object> body) {
        String mode = (String) body.getOrDefault("mode", "SOLO_BOTS");

        if ("MULTIJOUEUR".equals(mode)) {
            @SuppressWarnings("unchecked")
            List<String> pseudos = (List<String>) body.get("pseudos");
            return pokerService.creerPartieMultijoueur(pseudos);
        } else {
            String pseudo = (String) body.get("pseudo");
            int nbBots = (int) body.getOrDefault("nbAdversaires", 3);
            return pokerService.creerPartie(pseudo, nbBots, mode);
        }
    }

    @PostMapping("/{id}/next")
    public EtatJeuDTO mancheSuivante(@PathVariable String id) {
        return pokerService.lancerManche(id);
    }

    @PostMapping("/{id}/action")
    public EtatJeuDTO jouerAction(@PathVariable String id, @RequestBody Map<String, Object> body) {
        String action = (String) body.get("action");
        int montant = body.containsKey("montant") ? ((Number) body.get("montant")).intValue() : 0;
        return pokerService.jouerAction(id, action, montant);
    }

    @PostMapping("/{id}/ready")
    public EtatJeuDTO confirmerPassage(@PathVariable String id) {
        return pokerService.confirmerPassage(id);
    }

    @GetMapping("/{id}")
    public EtatJeuDTO getEtat(@PathVariable String id) {
        return pokerService.getEtat(id);
    }
}
