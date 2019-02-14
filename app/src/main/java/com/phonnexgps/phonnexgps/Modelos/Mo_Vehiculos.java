package com.phonnexgps.phonnexgps.Modelos;

public class Mo_Vehiculos {
    String id_vehi,no_vehi,no_identifi,id_chofer,id_historico;

    public Mo_Vehiculos() {
    }

    public Mo_Vehiculos(String id_vehi, String no_vehi, String no_identifi, String id_chofer, String id_historico) {
        this.id_vehi = id_vehi;
        this.no_vehi = no_vehi;
        this.no_identifi = no_identifi;
        this.id_chofer = id_chofer;
        this.id_historico = id_historico;
    }

    public String getId_vehi() {
        return id_vehi;
    }

    public void setId_vehi(String id_vehi) {
        this.id_vehi = id_vehi;
    }

    public String getNo_vehi() {
        return no_vehi;
    }

    public void setNo_vehi(String no_vehi) {
        this.no_vehi = no_vehi;
    }

    public String getNo_identifi() {
        return no_identifi;
    }

    public void setNo_identifi(String no_identifi) {
        this.no_identifi = no_identifi;
    }

    public String getId_chofer() {
        return id_chofer;
    }

    public void setId_chofer(String id_chofer) {
        this.id_chofer = id_chofer;
    }

    public String getId_historico() {
        return id_historico;
    }

    public void setId_historico(String id_historico) {
        this.id_historico = id_historico;
    }
}
