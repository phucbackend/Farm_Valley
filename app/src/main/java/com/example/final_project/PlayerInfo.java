package com.example.final_project;

public class PlayerInfo {
    public String playerName;
    public String farmName;

    public int coin ;
    public String getPlayerName() {
        return playerName;
    }
    public  PlayerInfo(){

    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }


    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

}
