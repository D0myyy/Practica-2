package model;

public class Persoana {
    private int id;
    private String nume;
    private String prenume;

    public Persoana() {}

    public Persoana(String nume, String prenume) {
        setNume(nume);
        setPrenume(prenume);
    }

    public Persoana(int id, String nume, String prenume) {
        this(nume, prenume);
        setId(id);
    }

    public int getId() { return id; }
    public String getNume() { return nume; }
    public String getPrenume() { return prenume; }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id-ul nu poate fi negativ.");
        }
        this.id = id;
    }

    public void setNume(String nume) {
        this.nume = validateText(nume, "Numele");
    }

    public void setPrenume(String prenume) {
        this.prenume = validateText(prenume, "Prenumele");
    }

    public String getNumeComplet() {
        return nume + " " + prenume;
    }

    public String getDescrierePersoana() {
        return "Persoana: " + getNumeComplet();
    }

    private String validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " este obligatoriu.");
        }
        return value.trim();
    }
}
