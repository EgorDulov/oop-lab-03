package demo;

import dictionary.Dictionary;
import dictionary.DictionaryLoader;
import exception.FileReadException;
import exception.InvalidFileFormatException;
import translator.Translator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

public final class Main {

    private static final String DEFAULT_DICTIONARY = "dictionary.txt";
    private static final String PROMPT = "Введите текст для перевода (пустая строка — выход): ";

    private Main() {
    }

    public static void main(String[] args) {
        Path dictionaryPath = Path.of(args.length > 0 ? args[0] : DEFAULT_DICTIONARY);

        Dictionary dictionary;
        try {
            dictionary = DictionaryLoader.load(dictionaryPath);
        } catch (FileReadException | InvalidFileFormatException e) {
            System.out.println("Ошибка: " + e.getMessage());
            System.exit(1);
            return;
        }

        Translator translator = new Translator(dictionary);
        System.out.println("Словарь загружен: " + dictionary.size() + " записей (" + dictionaryPath + ")");

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.print(PROMPT);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    break;
                }
                System.out.println("Перевод: " + translator.translate(line));
                System.out.print(PROMPT);
            }
        }
        System.out.println("Выход.");
    }
}