package utils;

import model.interfaces.Exportable;
import java.util.List;
import java.util.stream.Collectors;

public class CSVExporter {
	public String export(List<? extends Exportable> items) {
		if (items == null || items.isEmpty()) {
			return "";
		}
		return items.stream().map(Exportable::toCSV).collect(Collectors.joining(System.lineSeparator()));
	}
}
