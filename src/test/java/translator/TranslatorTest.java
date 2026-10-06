package translator;

import dictionary.DictionaryLoader;
import exception.InvalidFileFormatException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TranslatorTest {

    private static Translator translatorOf(String... lines) throws InvalidFileFormatException {
        return new Translator(DictionaryLoader.parse(List.of(lines)));
    }

    @Test
    void translatesExampleFromTask() throws Exception {
        Translator translator = translatorOf("look | смотреть", "look forward | ожидать");

        assertEquals("dog смотреть to the window, dog ожидать",
                translator.translate("dog look to the window, dog look forward"));
    }

    @Test
    void ignoresCase() throws Exception {
        Translator translator = translatorOf("look | смотреть", "look forward | ожидать");

        assertEquals("ожидать and смотреть", translator.translate("LOOK Forward and Look"));
    }

    @Test
    void leavesUnknownWordsAndPunctuationUntouched() throws Exception {
        Translator translator = translatorOf("hello | привет");

        assertEquals("привет, unknown world!", translator.translate("Hello, unknown world!"));
    }

    @Test
    void choosesPhraseWithLongestLeftPart() throws Exception {
        Translator translator = translatorOf(
                "look | смотреть", "look forward | ожидать", "look forward to | ждать с нетерпением");

        assertEquals("ждать с нетерпением it", translator.translate("look forward to it"));
        assertEquals("ожидать it", translator.translate("look forward it"));
    }

    @Test
    void doesNotTranslatePartsOfWords() throws Exception {
        Translator translator = translatorOf("look | смотреть");

        assertEquals("looking looked", translator.translate("looking looked"));
    }

    @Test
    void punctuationBetweenWordsBreaksPhrase() throws Exception {
        Translator translator = translatorOf("look | смотреть", "look forward | ожидать");

        assertEquals("смотреть, forward", translator.translate("look, forward"));
    }

    @Test
    void toleratesWhitespaceInsidePhrase() throws Exception {
        Translator translator = translatorOf("look forward | ожидать");

        assertEquals("ожидать and ожидать", translator.translate("look   forward and look\nforward"));
    }

    @Test
    void handlesEmptyTextAndEmptyDictionary() throws Exception {
        assertEquals("", translatorOf("look | смотреть").translate(""));
        assertEquals("any text", translatorOf().translate("any text"));
    }

    @Test
    void rejectsNullText() throws Exception {
        Translator translator = translatorOf("look | смотреть");

        assertThrows(NullPointerException.class, () -> translator.translate(null));
    }
}