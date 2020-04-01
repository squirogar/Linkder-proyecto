package com.example.linkder.Modelos;

public class Juego {
    private int idJuego;
    private String nombreJuego;

    public Juego(int idJuego, String nombreJuego) {
        this.idJuego = idJuego;
        this.nombreJuego = nombreJuego;
    }

    public int getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(int idJuego) {
        this.idJuego = idJuego;
    }

    public String getNombreJuego() {
        return nombreJuego;
    }

    public void setNombreJuego(String nombreJuego) {
        this.nombreJuego = nombreJuego;
    }
}
