package com.phonnexgps.phonnexgps.Modelos;

public class Mo_recorridos {
    String id_reco,co_coordx,co_coordy,velocidad,placa,estado,horometro,chofer,tiempo_recorrido,direccion,bloque,voltaje,act;

    public Mo_recorridos() {
    }

    public Mo_recorridos(String id_reco, String co_coordx, String co_coordy, String velocidad, String placa, String estado, String horometro, String chofer, String tiempo_recorrido, String direccion, String bloque, String voltaje, String act) {
        this.id_reco = id_reco;
        this.co_coordx = co_coordx;
        this.co_coordy = co_coordy;
        this.velocidad = velocidad;
        this.placa = placa;
        this.estado = estado;
        this.horometro = horometro;
        this.chofer = chofer;
        this.tiempo_recorrido = tiempo_recorrido;
        this.direccion = direccion;
        this.bloque = bloque;
        this.voltaje = voltaje;
        this.act = act;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public String getId_reco() {
        return id_reco;
    }

    public void setId_reco(String id_reco) {
        this.id_reco = id_reco;
    }

    public String getCo_coordx() {
        return co_coordx;
    }

    public void setCo_coordx(String co_coordx) {
        this.co_coordx = co_coordx;
    }

    public String getCo_coordy() {
        return co_coordy;
    }

    public void setCo_coordy(String co_coordy) {
        this.co_coordy = co_coordy;
    }

    public String getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(String velocidad) {
        this.velocidad = velocidad;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHorometro() {
        return horometro;
    }

    public void setHorometro(String horometro) {
        this.horometro = horometro;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }

    public String getTiempo_recorrido() {
        return tiempo_recorrido;
    }

    public void setTiempo_recorrido(String tiempo_recorrido) {
        this.tiempo_recorrido = tiempo_recorrido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getBloque() {
        return bloque;
    }

    public void setBloque(String bloque) {
        this.bloque = bloque;
    }

    public String getVoltaje() {
        return voltaje;
    }

    public void setVoltaje(String voltaje) {
        this.voltaje = voltaje;
    }
}
