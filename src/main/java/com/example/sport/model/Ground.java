package com.example.sport.model;

import java.time.LocalTime;

import jakarta.persistence.*;

@Entity
@Table(name = "ground")
public class Ground {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private String address;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    private String groundType; // Indoor/Outdoor
    private int playerCapacity;
    private String surfaceType; // Grass, Turf, etc.
    private boolean floodlightsAvailable;
    private boolean changingRooms;
    private LocalTime openingTime; // New field for opening time
    private LocalTime closingTime;

    private String imageFileName;
    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = true)
    private Coach coach ;  // Selected Coach

   
    public Ground() {}
    
    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }
   
    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getGroundType() {
        return groundType;
    }

    public void setGroundType(String groundType) {
        this.groundType = groundType;
    }

    public int getPlayerCapacity() {
        return playerCapacity;
    }

    public void setPlayerCapacity(int playerCapacity) {
        this.playerCapacity = playerCapacity;
    }

    public String getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(String surfaceType) {
        this.surfaceType = surfaceType;
    }

    public boolean isFloodlightsAvailable() {
        return floodlightsAvailable;
    }

    public void setFloodlightsAvailable(boolean floodlightsAvailable) {
        this.floodlightsAvailable = floodlightsAvailable;
    }

    public boolean isChangingRooms() {
        return changingRooms;
    }

    public void setChangingRooms(boolean changingRooms) {
        this.changingRooms = changingRooms;
    }
    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }
}