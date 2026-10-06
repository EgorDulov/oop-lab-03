package dictionary;

import exception.FileReadException;
import exception.InvalidFileFormatException;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DictionaryLoader {

    private static final String BOM = "\uFEFF";

    private DictionaryLoader() {
    }

    public static Dictionary load(Path path) throws FileReadException, InvalidFileFormatException {
        Objects.requireNonNull(path, "Путь к файлу не может быть null");
        return parse(readLines(path));
    }

    public static Dictionary parse(List<String> lines) throws InvalidFileFormatException {
        Objects.requireNonNull(lines, "Строки словаря не могут быть null");
        Map<String, String> entries = new LinkedHashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i == 0 && line.startsWith(BOM)) {
                line = line.substring(BOM.length());
            }
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new InvalidFileFormatException(i + 1, line);
            }
            entries.put(Dictionary.normalize(parts[0]), parts[1].strip());
        }
        return new Dictionary(entries);
    }

    private static List<String> readLines(Path path) throws FileReadException {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new FileReadException("Файл словаря не найден: " + path, e);
        } catch (AccessDeniedException e) {
            throw new FileReadException("Нет доступа к файлу словаря: " + path, e);
        } catch (MalformedInputException e) {
            throw new FileReadException("Файл словаря должен быть в кодировке UTF-8: " + path, e);
        } catch (IOException e) {
            throw new FileReadException("Не удалось прочитать файл словаря " + path + ": " + e.getMessage(), e);
        }
    }
}