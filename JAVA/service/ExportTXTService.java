package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExportTXTService {
    public void export(String content, Path filePath) {
        try {
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Eroare la exportul TXT.", exception);
        }
    }
}
