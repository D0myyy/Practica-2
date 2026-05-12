package controller;

import dao.BiletDAO;
import dao.PasagerDAO;
import dao.PlataDAO;
import dao.RezervareDAO;
import dao.ZborDAO;
import java.util.Map;
import service.ReportService;

public class MainController {
	private final PasagerDAO pasagerDAO = new PasagerDAO();
	private final ZborDAO zborDAO = new ZborDAO();
	private final RezervareDAO rezervareDAO = new RezervareDAO();
	private final BiletDAO biletDAO = new BiletDAO();
	private final PlataDAO plataDAO = new PlataDAO();
	private final ReportService reportService = new ReportService();

	public int totalPasageri() { return pasagerDAO.findAll().size(); }
	public int totalZboruri() { return zborDAO.findAll().size(); }
	public int totalRezervari() { return rezervareDAO.findAll().size(); }
	public int totalBilete() { return biletDAO.findAll().size(); }
	public int totalPlati() { return plataDAO.findAll().size(); }
	public Map<String, Number> raportGeneral() { return reportService.genereazaRezumat(); }
}
