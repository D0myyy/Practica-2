package model;

public class Persoana {
    private String nume;
    private String prenume;

    public Persoana() {}

    public Persoana(String nume, String prenume) {
        this.nume = nume;
        this.prenume = prenume;
    }

    public String getNume() { return nume; }
    public String getPrenume() { return prenume; }
}
