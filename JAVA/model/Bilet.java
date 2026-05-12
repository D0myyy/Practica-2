package model;

import model.enums.StatusBilet;
import model.interfaces.Exportable;

public class Bilet implements Exportable {
	private int idBilet;
	private Rezervare rezervare;
	private Pasager pasager;
	private Zbor zbor;
	private Loc loc;
	private double pret;
	private StatusBilet status;

	public Bilet() {
		this.status = StatusBilet.CONFIRMAT;
	}

	public Bilet(int idBilet, Rezervare rezervare, Pasager pasager, Zbor zbor, Loc loc, double pret, StatusBilet status) {
		setIdBilet(idBilet);
		setRezervare(rezervare);
		setPasager(pasager);
		setZbor(zbor);
		setLoc(loc);
		setPret(pret);
		setStatus(status);
	}

	public int getIdBilet() {
		return idBilet;
	}

	public void setIdBilet(int idBilet) {
		if (idBilet < 0) {
			throw new IllegalArgumentException("Id bilet invalid.");
		}
		this.idBilet = idBilet;
	}

	public Rezervare getRezervare() {
		return rezervare;
	}

	public void setRezervare(Rezervare rezervare) {
		if (rezervare == null) {
			throw new IllegalArgumentException("Rezervarea este obligatorie.");
		}
		this.rezervare = rezervare;
	}

	public Pasager getPasager() {
		return pasager;
	}

	public void setPasager(Pasager pasager) {
		if (pasager == null) {
			throw new IllegalArgumentException("Pasagerul este obligatoriu.");
		}
		this.pasager = pasager;
	}

	public Zbor getZbor() {
		return zbor;
	}

	public void setZbor(Zbor zbor) {
		if (zbor == null) {
			throw new IllegalArgumentException("Zborul este obligatoriu.");
		}
		this.zbor = zbor;
	}

	public Loc getLoc() {
		return loc;
	}

	public void setLoc(Loc loc) {
		if (loc == null) {
			throw new IllegalArgumentException("Locul este obligatoriu.");
		}
		this.loc = loc;
	}

	public double getPret() {
		return pret;
	}

	public void setPret(double pret) {
		if (pret < 0) {
			throw new IllegalArgumentException("Pretul nu poate fi negativ.");
		}
		this.pret = pret;
	}

	public StatusBilet getStatus() {
		return status;
	}

	public void setStatus(StatusBilet status) {
		if (status == null) {
			throw new IllegalArgumentException("Statusul biletului este obligatoriu.");
		}
		this.status = status;
	}

	public void anuleaza() {
		this.status = StatusBilet.ANULAT;
	}

	@Override
	public String toCSV() {
		return String.format("%d,%d,%d,%d,%d,%.2f,%s",
				idBilet,
				rezervare.getIdRezervare(),
				pasager.getIdPasager(),
				zbor.getIdZbor(),
				loc.getIdLoc(),
				pret,
				status);
	}

	@Override
	public String toTXT() {
		return String.format("Bilet #%d | %s | %s | Loc %s | %.2f | %s",
				idBilet,
				pasager.getNumeComplet(),
				zbor.getNumarZbor(),
				loc.getNumarLoc(),
				pret,
				status);
	}
}
