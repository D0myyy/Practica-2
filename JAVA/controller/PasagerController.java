package controller;

import dao.PasagerDAO;
import java.util.List;
import model.Pasager;

public class PasagerController {
	private final PasagerDAO pasagerDAO = new PasagerDAO();

	public List<Pasager> lista() { return pasagerDAO.findAll(); }
	public Pasager gaseste(int idPasager) { return pasagerDAO.findById(idPasager); }
	public int adauga(Pasager pasager) { return pasagerDAO.create(pasager); }
	public boolean actualizeaza(Pasager pasager) { return pasagerDAO.update(pasager); }
	public boolean sterge(int idPasager) { return pasagerDAO.delete(idPasager); }
	public List<Pasager> cauta(String query) { return pasagerDAO.searchByName(query); }
}
