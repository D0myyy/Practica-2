package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.interfaces.Exportable;

public class Rezervare implements Exportable {
	private int idRezervare;
	private String codRezervare;
	private LocalDateTime dataRezervare;
	private final List<Bilet> bilete;

	public Rezervare() {
		this.bilete = new ArrayList<>();
		this.dataRezervare = LocalDateTime.now();
	}

	public Rezervare(int idRezervare, String codRezervare, LocalDateTime dataRezervare) {
		this();
		setIdRezervare(idRezervare);
		setCodRezervare(codRezervare);
		setDataRezervare(dataRezervare);
	}

	public int getIdRezervare() {
		return idRezervare;
	}

	public void setIdRezervare(int idRezervare) {
		if (idRezervare < 0) {
			throw new IllegalArgumentException("Id rezervare invalid.");
		}
		this.idRezervare = idRezervare;
	}

	public String getCodRezervare() {
		return codRezervare;
	}

	public void setCodRezervare(String codRezervare) {
		if (codRezervare == null || codRezervare.trim().isEmpty()) {
			throw new IllegalArgumentException("Codul rezervarii este obligatoriu.");
		}
		this.codRezervare = codRezervare.trim().toUpperCase();
	}

	public LocalDateTime getDataRezervare() {
		return dataRezervare;
	}

	public void setDataRezervare(LocalDateTime dataRezervare) {
		if (dataRezervare == null) {
			throw new IllegalArgumentException("Data rezervarii este obligatorie.");
		}
		this.dataRezervare = dataRezervare;
	}

	public List<Bilet> getBilete() {
		return Collections.unmodifiableList(bilete);
	}

	public void adaugaBilet(Bilet bilet) {
		if (bilet == null) {
			throw new IllegalArgumentException("Biletul nu poate fi null.");
		}
		bilet.setRezervare(this);
		bilete.add(bilet);
	}

	public boolean stergeBilet(Bilet bilet) {
		return bilete.remove(bilet);
	}

	public double calculeazaTotal() {
		return bilete.stream().mapToDouble(Bilet::getPret).sum();
	}

	@Override
	public String toCSV() {
		return String.format("%d,%s,%s,%.2f,%d", idRezervare, codRezervare, dataRezervare, calculeazaTotal(), bilete.size());
	}

	@Override
	public String toTXT() {
		return String.format("Rezervare #%d (%s) | data: %s | total: %.2f | bilete: %d",
				idRezervare, codRezervare, dataRezervare, calculeazaTotal(), bilete.size());
	}
}
