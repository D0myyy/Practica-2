package model;

import java.time.LocalDateTime;
import model.enums.MetodaPlata;
import model.enums.StatusPlata;
import model.interfaces.Exportable;

public class Plata implements Exportable {
	private int idPlata;
	private Rezervare rezervare;
	private double suma;
	private MetodaPlata metoda;
	private StatusPlata status;
	private LocalDateTime dataPlata;

	public Plata() {
		this.status = StatusPlata.IN_ASTEPTARE;
		this.dataPlata = LocalDateTime.now();
	}

	public Plata(int idPlata, Rezervare rezervare, double suma, MetodaPlata metoda, StatusPlata status, LocalDateTime dataPlata) {
		setIdPlata(idPlata);
		setRezervare(rezervare);
		setSuma(suma);
		setMetoda(metoda);
		setStatus(status);
		setDataPlata(dataPlata);
	}

	public int getIdPlata() {
		return idPlata;
	}

	public void setIdPlata(int idPlata) {
		if (idPlata < 0) {
			throw new IllegalArgumentException("Id plata invalid.");
		}
		this.idPlata = idPlata;
	}

	public Rezervare getRezervare() {
		return rezervare;
	}

	public void setRezervare(Rezervare rezervare) {
		if (rezervare == null) {
			throw new IllegalArgumentException("Rezervarea pentru plata este obligatorie.");
		}
		this.rezervare = rezervare;
	}

	public double getSuma() {
		return suma;
	}

	public void setSuma(double suma) {
		if (suma < 0) {
			throw new IllegalArgumentException("Suma nu poate fi negativa.");
		}
		this.suma = suma;
	}

	public MetodaPlata getMetoda() {
		return metoda;
	}

	public void setMetoda(MetodaPlata metoda) {
		if (metoda == null) {
			throw new IllegalArgumentException("Metoda de plata este obligatorie.");
		}
		this.metoda = metoda;
	}

	public StatusPlata getStatus() {
		return status;
	}

	public void setStatus(StatusPlata status) {
		if (status == null) {
			throw new IllegalArgumentException("Statusul platii este obligatoriu.");
		}
		this.status = status;
	}

	public LocalDateTime getDataPlata() {
		return dataPlata;
	}

	public void setDataPlata(LocalDateTime dataPlata) {
		if (dataPlata == null) {
			throw new IllegalArgumentException("Data platii este obligatorie.");
		}
		this.dataPlata = dataPlata;
	}

	public boolean proceseazaPlata() {
		if (suma >= rezervare.calculeazaTotal()) {
			status = StatusPlata.PLATIT;
			return true;
		}
		status = StatusPlata.REFUZAT;
		return false;
	}

	@Override
	public String toCSV() {
		return String.format("%d,%d,%.2f,%s,%s,%s",
				idPlata,
				rezervare.getIdRezervare(),
				suma,
				metoda,
				status,
				dataPlata);
	}

	@Override
	public String toTXT() {
		return String.format("Plata #%d | Rezervare %s | %.2f | %s | %s",
				idPlata,
				rezervare.getCodRezervare(),
				suma,
				metoda,
				status);
	}
}
