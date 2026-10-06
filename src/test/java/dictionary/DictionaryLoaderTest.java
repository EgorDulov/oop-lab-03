package dictionary;

import exception.FileReadException;
import exception.InvalidFileFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DictionaryLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesEntriesIgnoringCaseSpacesAndBlankLines() throws Exception {
        Dictionary dictionary = DictionaryLoader.parse(List.of("  Look   Forward | ожидать ", "", "HELLO|привет"));

        assertEquals(2, dictionary.size());
        assertEquals(Optional.of("ожидать"), dictionary.find("look forward"));
        assertEquals(Optional.of("привет"), dictionary.find("Hello"));
        assertEquals(Optional.empty(), dictionary.find("bye"));
    }

    @Test
    void laterEntryOverridesEarlierOne() throws Exception {
        Dictionary dictionary = DictionaryLoader.parse(List.of("look | смотреть", "LOOK | глядеть"));

        assertEquals(1, dictionary.size());
        assertEquals(Optional.of("глядеть"), dictionary.find("look"));
    }

    @Test
    void rejectsLineWithoutSeparatorAndReportsLineNumber() {
        InvalidFileFormatException exception = assertThrows(InvalidFileFormatException.class,
                () -> DictionaryLoader.parse(List.of("look | смотреть", "oops")));

        assertEquals(2, exception.getLineNumber());
    }

    @Test
    void rejectsEmptyPartsAndExtraSeparators() {
        assertThrows(InvalidFileFormatException.class, () -> DictionaryLoader.parse(List.of("| перевод")));
        assertThrows(InvalidFileFormatException.class, () -> DictionaryLoader.parse(List.of("слово |")));
        assertThrows(InvalidFileFormatException.class, () -> DictionaryLoader.parse(List.of("a | b | c")));
    }

    @Test
    void loadsUtf8FileWithBom() throws Exception {
        Path file = tempDir.resolve("dictionary.txt");
        Files.writeString(file, "\uFEFFlook | смотреть\nhello | привет\n", StandardCharsets.UTF_8);

        Dictionary dictionary = DictionaryLoader.load(file);

        assertEquals(2, dictionary.size());
        assertEquals(Optional.of("смотреть"), dictionary.find("look"));
    }

    @Test
    void reportsInvalidFormatWhenLoadingFile() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file, "look | смотреть\nbroken line\n", StandardCharsets.UTF_8);

        assertThrows(InvalidFileFormatException.class, () -> DictionaryLoader.load(file));
    }

    @Test
    void failsWhenFileDoesNotExist() {
        assertThrows(FileReadException.class, () -> DictionaryLoader.load(tempDir.resolve("missing.txt")));
    }

    @Test
    void failsWhenPathIsDirectory() {
        assertThrows(FileReadException.class, () -> DictionaryLoader.load(tempDir));
    }

    @Test
    void failsWhenFileIsNotUtf8() throws Exception {
        Path file = tempDir.resolve("latin.txt");
        Files.write(file, new byte[]{(byte) 0xC3, (byte) 0x28});

        assertThrows(FileReadException.class, () -> DictionaryLoader.load(file));
    }
}