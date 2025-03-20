package com.example.sport.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String gameName;

    @Column(nullable = false)
    private int playerCapacity;

    @Column(nullable = false)
    private String type; // Indoor or Outdoor
    
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coach> coaches;

    
    // Constructors
    public Game() {}

    public Game(String gameName, int playerCapacity, String type) {
        this.gameName = gameName;
        this.playerCapacity = playerCapacity;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getPlayerCapacity() {
        return playerCapacity;
    }

    public void setPlayerCapacity(int playerCapacity) {
        this.playerCapacity = playerCapacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
