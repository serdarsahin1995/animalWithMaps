package com.example.petswithmaps.Models;

public class KonumModel {
    String konum1, konum2, text, detail, resim, key, adres1, adres2, sehir, uid,ilce ;
Boolean status;
    public KonumModel(String konum1, String konum2, String text, String detail, String resim, String key, String adres1, String adres2, String sehir, String uid,Boolean status,String ilce) {
        this.konum1 = konum1;
        this.konum2 = konum2;
        this.text = text;
        this.detail = detail;
        this.resim = resim;
        this.key = key;
        this.adres1 = adres1;
        this.adres2 = adres2;
        this.sehir = sehir;
        this.uid = uid;
        this.status = status;
        this.ilce=ilce;
    }

    public KonumModel() {
    }

    public String getKonum1() {
        return konum1;
    }

    public void setKonum1(String konum1) {
        this.konum1 = konum1;
    }

    public String getKonum2() {
        return konum2;
    }

    public void setKonum2(String konum2) {
        this.konum2 = konum2;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAdres1() {
        return adres1;
    }

    public void setAdres1(String adres1) {
        this.adres1 = adres1;
    }

    public String getAdres2() {
        return adres2;
    }

    public void setAdres2(String adres2) {
        this.adres2 = adres2;
    }

    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getIlce() {
        return ilce;
    }

    public void setIlce(String ilce) {
        this.ilce = ilce;
    }
}
