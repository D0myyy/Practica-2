package controller;

import dao.PlataDAO;
import java.util.List;
import model.Plata;

public class PlataController {
	private final PlataDAO plataDAO = new PlataDAO();

	public List<Plata> lista() { return plataDAO.findAll(); }
	public Plata gaseste(int idPlata) { return plataDAO.findById(idPlata); }
	public int adauga(Plata plata) { return plataDAO.create(plata); }
	public boolean actualizeaza(Plata plata) { return plataDAO.update(plata); }
	public boolean sterge(int idPlata) { return plataDAO.delete(idPlata); }
}
