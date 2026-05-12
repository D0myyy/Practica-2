package controller;

import dao.ZborDAO;
import java.util.List;
import model.Zbor;

public class ZborController {
	private final ZborDAO zborDAO = new ZborDAO();

	public List<Zbor> lista() { return zborDAO.findAll(); }
	public Zbor gaseste(int idZbor) { return zborDAO.findById(idZbor); }
	public int adauga(Zbor zbor) { return zborDAO.create(zbor); }
	public boolean actualizeaza(Zbor zbor) { return zborDAO.update(zbor); }
	public boolean sterge(int idZbor) { return zborDAO.delete(idZbor); }
}
