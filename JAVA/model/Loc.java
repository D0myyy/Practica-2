package model;

import model.enums.ClasaLoc;

public class Loc {
	private int idLoc;
	private int idAvion;
	private String numarLoc;
	private ClasaLoc clasaLoc;

	public Loc() {}

	public Loc(int idLoc, int idAvion, String numarLoc, ClasaLoc clasaLoc) {
		setIdLoc(idLoc);
		setIdAvion(idAvion);
		setNumarLoc(numarLoc);
		setClasaLoc(clasaLoc);
	}

	public int getIdLoc() {
		return idLoc;
	}

	public void setIdLoc(int idLoc) {
		if (idLoc < 0) {
			throw new IllegalArgumentException("Id loc invalid.");
		}
		this.idLoc = idLoc;
	}

	public int getIdAvion() {
		return idAvion;
	}

	public void setIdAvion(int idAvion) {
		if (idAvion < 0) {
			throw new IllegalArgumentException("Id avion invalid pentru loc.");
		}
		this.idAvion = idAvion;
	}

	public String getNumarLoc() {
		return numarLoc;
	}

	public void setNumarLoc(String numarLoc) {
		if (numarLoc == null || !numarLoc.trim().matches("\\d{1,2}[A-Fa-f]")) {
			throw new IllegalArgumentException("Numar loc invalid. Exemplu valid: 12A.");
		}
		this.numarLoc = numarLoc.trim().toUpperCase();
	}

	public ClasaLoc getClasaLoc() {
		return clasaLoc;
	}

	public void setClasaLoc(ClasaLoc clasaLoc) {
		if (clasaLoc == null) {
			throw new IllegalArgumentException("Clasa locului este obligatorie.");
		}
		this.clasaLoc = clasaLoc;
	}

	@Override
	public String toString() {
		return numarLoc + " - " + clasaLoc;
	}
}
