package controller;

import dao.BiletDAO;
import java.util.List;
import model.Bilet;

public class BiletController {
	private final BiletDAO biletDAO = new BiletDAO();

	public List<Bilet> lista() { return biletDAO.findAll(); }
	public Bilet gaseste(int idBilet) { return biletDAO.findById(idBilet); }
	public int adauga(Bilet bilet) { return biletDAO.create(bilet); }
	public boolean actualizeaza(Bilet bilet) { return biletDAO.update(bilet); }
	public boolean sterge(int idBilet) { return biletDAO.delete(idBilet); }
}
