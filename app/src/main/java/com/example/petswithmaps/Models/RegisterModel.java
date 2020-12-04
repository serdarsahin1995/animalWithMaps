package com.example.petswithmaps.Models;

public class RegisterModel {
    String name,email,photo,adres;
    public RegisterModel(String name, String email, String photo,String adres) {
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.adres=adres;
    }

    public RegisterModel(String photo) {
        this.photo = photo;
    }

    public RegisterModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }
}
