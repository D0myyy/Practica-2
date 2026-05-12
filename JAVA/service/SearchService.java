package service;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchService {
	public <T> List<T> search(List<T> source, String query, java.util.function.Function<T, String> mapper) {
		if (source == null || source.isEmpty()) {
			return List.of();
		}
		if (query == null || query.trim().isEmpty()) {
			return source;
		}
		String needle = query.trim().toLowerCase(Locale.ROOT);
		return source.stream()
				.filter(item -> {
					String text = mapper.apply(item);
					return text != null && text.toLowerCase(Locale.ROOT).contains(needle);
				})
				.collect(Collectors.toList());
	}

	public <T> List<T> filter(List<T> source, Predicate<T> predicate) {
		if (source == null || source.isEmpty()) {
			return List.of();
		}
		return source.stream().filter(predicate).collect(Collectors.toList());
	}
}
