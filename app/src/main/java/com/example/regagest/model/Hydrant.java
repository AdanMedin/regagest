package com.example.regagest.model;

public class Hydrant {

    int hydrantNumber, parcelNumber;
    String estado;

    public Hydrant() {
    }

    public Hydrant(int hydrantNumber, int parcelNumber, String estado) {
        this.hydrantNumber = hydrantNumber;
        this.parcelNumber = parcelNumber;
        this.estado = estado;
    }

    public int getHydrantNumber() {
        return hydrantNumber;
    }

    public void setHydrantNumber(int hydrantNumber) {
        this.hydrantNumber = hydrantNumber;
    }

    public int getParcelNumber() {
        return parcelNumber;
    }

    public void setParcelNumber(int parcelNumber) {
        this.parcelNumber = parcelNumber;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Hydrant{" +
                "numeroHudrante=" + hydrantNumber +
                ", nombreParcela='" + parcelNumber + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
