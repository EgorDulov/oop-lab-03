package translator;

import java.util.ArrayList;
import java.util.List;

final class Tokenizer {

    record Token(String text, boolean word, int start) {

        int end() {
            return start + text.length();
        }
    }

    private Tokenizer() {
    }

    static boolean isWordChar(char c) {
        return Character.isLetterOrDigit(c) || c == '\'' || c == '\u2019' || c == '-';
    }

    static List<Token> tokenize(String text) {
        List<Token> tokens = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            boolean word = isWordChar(text.charAt(start));
            int end = start + 1;
            while (end < text.length() && isWordChar(text.charAt(end)) == word) {
                end++;
            }
            tokens.add(new Token(text.substring(start, end), word, start));
            start = end;
        }
        return tokens;
    }

    static int countWords(String phrase) {
        return (int) tokenize(phrase).stream().filter(Token::word).count();
    }
}