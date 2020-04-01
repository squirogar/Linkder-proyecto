package com.example.linkder.Modelos;

public class Jugador {
    private String nick;
    private String email; //necesario para poder visitar el perfil

    public Jugador(String nick, String email) {
        this.nick = nick;
        this.email = email;
    }

    public String getNickJugador() {
        return nick;
    }

    public void setNickJugador(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
