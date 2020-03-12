package com.example.linkder.models;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Usuario extends RealmObject {
    @PrimaryKey
    private String mail;
    private String nick;
    private String password;
    private String descripcion;
    private String contacto;
    private String fechaRegistro;
    private int id;

    public Usuario() {

    }

    public Usuario(String mail, String nick, String password, String descripcion, String contacto
            , String fechaRegistro) {
        this.mail = mail;
        this.nick = nick;
        this.password = password;
        this.descripcion = descripcion;
        this.contacto = contacto;
        this.fechaRegistro = fechaRegistro;
        /*se agrega el id de forma automática y no se pide como parametro del constructor*/
        this.id = getNextKey();
    }

    /*metodo para generar el id correlativo de forma automática*/
    public int getNextKey() {
        try {
            Realm realm = Realm.getDefaultInstance();
            /* se consulta por el id max actual guardado*/
            Number number = realm.where(Usuario.class).max("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getContacto() {
        return contacto;
    }
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
    public String getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
