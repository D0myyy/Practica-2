package service;

import dao.BiletDAO;
import dao.PasagerDAO;
import dao.PlataDAO;
import dao.RezervareDAO;
import dao.ZborDAO;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportService {
	private final ZborDAO zborDAO = new ZborDAO();
	private final PasagerDAO pasagerDAO = new PasagerDAO();
	private final RezervareDAO rezervareDAO = new RezervareDAO();
	private final BiletDAO biletDAO = new BiletDAO();
	private final PlataDAO plataDAO = new PlataDAO();

	public Map<String, Number> genereazaRezumat() {
		Map<String, Number> raport = new LinkedHashMap<>();
		raport.put("zboruri", zborDAO.findAll().size());
		raport.put("pasageri", pasagerDAO.findAll().size());
		raport.put("rezervari", rezervareDAO.findAll().size());
		raport.put("bilete", biletDAO.findAll().size());
		raport.put("plati", plataDAO.findAll().size());
		return raport;
	}

	public String raportText() {
		Map<String, Number> raport = genereazaRezumat();
		StringBuilder builder = new StringBuilder();
		builder.append("Raport general\n");
		raport.forEach((key, value) -> builder.append(key).append(": ").append(value).append('\n'));
		return builder.toString();
	}
}
