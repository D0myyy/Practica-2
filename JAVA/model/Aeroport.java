package model;

public class Aeroport {
    private int idAeroport;
    private String nume;
    private String oras;
    private String tara;
    private String codIata;

    public Aeroport() {}

    public Aeroport(int idAeroport, String nume, String oras, String tara, String codIata) {
        setIdAeroport(idAeroport);
        setNume(nume);
        setOras(oras);
        setTara(tara);
        setCodIata(codIata);
    }

    public int getIdAeroport() {
        return idAeroport;
    }

    public void setIdAeroport(int idAeroport) {
        if (idAeroport < 0) {
            throw new IllegalArgumentException("Id aeroport invalid.");
        }
        this.idAeroport = idAeroport;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = validateText(nume, "Numele aeroportului");
    }

    public String getOras() {
        return oras;
    }

    public void setOras(String oras) {
        this.oras = validateText(oras, "Orasul");
    }

    public String getTara() {
        return tara;
    }

    public void setTara(String tara) {
        this.tara = validateText(tara, "Tara");
    }

    public String getCodIata() {
        return codIata;
    }

    public void setCodIata(String codIata) {
        if (codIata == null || !codIata.trim().matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Codul IATA trebuie sa contina exact 3 litere.");
        }
        this.codIata = codIata.trim().toUpperCase();
    }

    private String validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " este obligatoriu.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return codIata + " - " + oras + " (" + tara + ")";
    }
}
