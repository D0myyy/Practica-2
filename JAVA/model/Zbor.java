package model;

import java.time.LocalDateTime;

public class Zbor {
    private int idZbor;
    private String numarZbor;
    private Aeroport plecare;
    private Aeroport sosire;
    private Avion avion;
    private LocalDateTime dataPlecare;
    private LocalDateTime dataSosire;

    public Zbor() {}

    public Zbor(int idZbor, String numarZbor, Aeroport plecare, Aeroport sosire, Avion avion,
                LocalDateTime dataPlecare, LocalDateTime dataSosire) {
        setIdZbor(idZbor);
        setNumarZbor(numarZbor);
        setPlecare(plecare);
        setSosire(sosire);
        setAvion(avion);
        setDataPlecare(dataPlecare);
        setDataSosire(dataSosire);
    }

    public int getIdZbor() {
        return idZbor;
    }

    public void setIdZbor(int idZbor) {
        if (idZbor < 0) {
            throw new IllegalArgumentException("Id zbor invalid.");
        }
        this.idZbor = idZbor;
    }

    public String getNumarZbor() {
        return numarZbor;
    }

    public void setNumarZbor(String numarZbor) {
        if (numarZbor == null || numarZbor.trim().isEmpty()) {
            throw new IllegalArgumentException("Numarul zborului este obligatoriu.");
        }
        this.numarZbor = numarZbor.trim().toUpperCase();
    }

    public Aeroport getPlecare() {
        return plecare;
    }

    public void setPlecare(Aeroport plecare) {
        if (plecare == null) {
            throw new IllegalArgumentException("Aeroportul de plecare este obligatoriu.");
        }
        this.plecare = plecare;
        validateAeroporturiDiferite();
    }

    public Aeroport getSosire() {
        return sosire;
    }

    public void setSosire(Aeroport sosire) {
        if (sosire == null) {
            throw new IllegalArgumentException("Aeroportul de sosire este obligatoriu.");
        }
        this.sosire = sosire;
        validateAeroporturiDiferite();
    }

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        if (avion == null) {
            throw new IllegalArgumentException("Avionul este obligatoriu.");
        }
        this.avion = avion;
    }

    public LocalDateTime getDataPlecare() {
        return dataPlecare;
    }

    public void setDataPlecare(LocalDateTime dataPlecare) {
        if (dataPlecare == null) {
            throw new IllegalArgumentException("Data plecarii este obligatorie.");
        }
        this.dataPlecare = dataPlecare;
        validateInterval();
    }

    public LocalDateTime getDataSosire() {
        return dataSosire;
    }

    public void setDataSosire(LocalDateTime dataSosire) {
        if (dataSosire == null) {
            throw new IllegalArgumentException("Data sosirii este obligatorie.");
        }
        this.dataSosire = dataSosire;
        validateInterval();
    }

    private void validateAeroporturiDiferite() {
        if (plecare != null && sosire != null && plecare.getCodIata().equalsIgnoreCase(sosire.getCodIata())) {
            throw new IllegalArgumentException("Aeroportul de plecare si cel de sosire nu pot fi identice.");
        }
    }

    private void validateInterval() {
        if (dataPlecare != null && dataSosire != null && !dataSosire.isAfter(dataPlecare)) {
            throw new IllegalArgumentException("Data sosirii trebuie sa fie dupa data plecarii.");
        }
    }

    @Override
    public String toString() {
        return numarZbor + ": " + plecare.getCodIata() + " -> " + sosire.getCodIata();
    }
}
