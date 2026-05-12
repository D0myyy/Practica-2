package model;

public class Avion {
    private int idAvion;
    private String model;
    private int capacitate;

    public Avion() {}

    public Avion(int idAvion, String model, int capacitate) {
        setIdAvion(idAvion);
        setModel(model);
        setCapacitate(capacitate);
    }

    public int getIdAvion() {
        return idAvion;
    }

    public void setIdAvion(int idAvion) {
        if (idAvion < 0) {
            throw new IllegalArgumentException("Id avion invalid.");
        }
        this.idAvion = idAvion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Modelul avionului este obligatoriu.");
        }
        this.model = model.trim();
    }

    public int getCapacitate() {
        return capacitate;
    }

    public void setCapacitate(int capacitate) {
        if (capacitate <= 0) {
            throw new IllegalArgumentException("Capacitatea trebuie sa fie un numar pozitiv.");
        }
        this.capacitate = capacitate;
    }

    @Override
    public String toString() {
        return model + " (" + capacitate + " locuri)";
    }
}
