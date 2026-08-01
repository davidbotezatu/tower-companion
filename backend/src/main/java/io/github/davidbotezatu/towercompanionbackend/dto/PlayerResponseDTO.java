package io.github.davidbotezatu.towercompanionbackend.dto;

public class PlayerResponseDTO {
    private int highestTier;
    private long lifetimeCoins;

    public PlayerResponseDTO(int highestTier, long lifetimeCoins) {
        this.highestTier = highestTier;
        this.lifetimeCoins = lifetimeCoins;
    }

    public int getHighestTier() {
        return highestTier;
    }

    public long getLifetimeCoins() {
        return lifetimeCoins;
    }
}