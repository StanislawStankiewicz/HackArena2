package com.github.INIT_SGGW.MonoTanksBot.Bot;

public class Site {
    public int index;
    public float x;
    public float y;
    public int tileCount;

    public Site(int index){
        this.index=index;
        x = 0;
        y = 0;
        tileCount =0;
    }

    public void addToX(int n){
        x+=n;
    }


}
