package com.github.INIT_SGGW.MonoTanksBot.Bot.entities;

import lombok.Setter;

public class Site {
    public int index;
    @Setter
    public Owner owner;
    public float x;
    public float y;
    public int tileCount;

    public Site(int index){
        this.index=index;
        x = 0;
        y = 0;
        tileCount =0;
        owner = Owner.NONE;
    }

    public void addToX(int n){
        x+=n;
    }

    public void addToY(int n){
        y+=n;
    }

    public void addTile(int x,int y){
        addToX(x);
        addToY(y);
        tileCount++;
    }

    public void averagePosition(){
        x/=tileCount;
        y/=tileCount;
    }

    public enum Owner
    {
        NONE, US, ENEMY
    }

}
