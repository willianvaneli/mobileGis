package com.example.mobilegis.Models;

import android.graphics.Color;

import org.osmdroid.views.overlay.OverlayWithIW;

import java.util.ArrayList;
import java.util.List;

public class Camada {
    private boolean visivel;
    private List<OverlayWithIW> objetos;
    private int color;

    public Camada(){
        this.objetos = new ArrayList<OverlayWithIW>();
        this.visivel = true;
        this.color = Color.rgb(0,0,250);
    }

    public List<OverlayWithIW> getObjetos() {
        return objetos;
    }

    public void setObjetos(List<OverlayWithIW> objetos) {
        this.objetos = objetos;
    }

    public void addObjeto(OverlayWithIW objeto){
        this.objetos.add(objeto);
    }

    public void show(){
        this.visivel = true;
    }

    public void hide(){
        this.visivel = false;
    }

    public boolean isVisivel(){
        return visivel;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int red, int green,int blue ) {
        this.color = Color.rgb( red, green, blue);
    }

    public int size(){
        return objetos.size();
    }
}

