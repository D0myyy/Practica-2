package model;

import java.util.regex.Pattern;
import model.interfaces.Exportable;

public class Pasager extends Persoana implements Exportable {
    private int idPasager;
    private String email;
    private String telefon;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Pasager() {}

    public Pasager(String nume, String prenume) {
        super(nume, prenume);
    }

    public Pasager(int idPasager, String nume, String prenume, String email, String telefon) {
        super(idPasager, nume, prenume);
        setIdPasager(idPasager);
        setEmail(email);
        setTelefon(telefon);
    }

    public int getIdPasager() {
        return idPasager;
    }

    public void setIdPasager(int idPasager) {
        if (idPasager < 0) {
            throw new IllegalArgumentException("Id pasager invalid.");
        }
        this.idPasager = idPasager;
        setId(idPasager);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email-ul este obligatoriu.");
        }
        String cleaned = email.trim();
        if (!EMAIL_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Format email invalid.");
        }
        this.email = cleaned;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        if (telefon == null || telefon.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefonul este obligatoriu.");
        }
        this.telefon = telefon.trim();
    }

    @Override
    public String getDescrierePersoana() {
        return "Pasager: " + getNumeComplet() + " (" + email + ")";
    }

    public String toCSV() {
        return String.format("%d,%s,%s,%s,%s", idPasager, getNume(), getPrenume(), email, telefon);
    }

    public String toTXT() {
        return String.format("Pasager #%d | %s | email: %s | tel: %s", idPasager, getNumeComplet(), email, telefon);
    }
}
