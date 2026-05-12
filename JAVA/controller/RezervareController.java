package controller;

import dao.RezervareDAO;
import java.util.List;
import model.Rezervare;

public class RezervareController {
	private final RezervareDAO rezervareDAO = new RezervareDAO();

	public List<Rezervare> lista() { return rezervareDAO.findAll(); }
	public Rezervare gaseste(int idRezervare) { return rezervareDAO.findById(idRezervare); }
	public int adauga(Rezervare rezervare) { return rezervareDAO.create(rezervare); }
	public boolean actualizeaza(Rezervare rezervare) { return rezervareDAO.update(rezervare); }
	public boolean sterge(int idRezervare) { return rezervareDAO.delete(idRezervare); }
}
