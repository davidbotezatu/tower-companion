package io.github.davidbotezatu.towercompanionbackend.controller;

import io.github.davidbotezatu.towercompanionbackend.dto.PlayerResponseDTO;
import io.github.davidbotezatu.towercompanionbackend.service.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/api/player")
    public PlayerResponseDTO getPlayer() {
        return playerService.getPlayerStats();
    }
}
