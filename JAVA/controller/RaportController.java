package controller;

import java.util.Map;
import service.ReportService;

public class RaportController {
	private final ReportService reportService = new ReportService();

	public Map<String, Number> raportGeneral() { return reportService.genereazaRezumat(); }
	public String raportText() { return reportService.raportText(); }
}
