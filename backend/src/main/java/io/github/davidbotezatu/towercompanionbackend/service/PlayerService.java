package io.github.davidbotezatu.towercompanionbackend.service;

import io.github.davidbotezatu.towercompanionbackend.dto.PlayerResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    public PlayerResponseDTO getPlayerStats() {
        return new PlayerResponseDTO(12, 9841232432000L);
    }
}