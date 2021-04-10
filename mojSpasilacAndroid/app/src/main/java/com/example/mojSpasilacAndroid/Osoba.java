package com.example.mojSpasilacAndroid;

public class Osoba {

    private int brGodina;
    private int zdravstvenoStanje;

    public Osoba(int brGodina,int zdravstvenoStanje)
    {
        this.brGodina = brGodina;
        this.zdravstvenoStanje = zdravstvenoStanje;
    }


    public int getBrGodina() {
        return brGodina;
    }

    public int getZdravstvenoStanje() {
        return zdravstvenoStanje;
    }

    public void setBrGodina(int brGodina) {
        this.brGodina = brGodina;
    }

    public void setZdravstvenoStanje(int zdravstvenoStanje) {
        this.zdravstvenoStanje = zdravstvenoStanje;
    }
}
