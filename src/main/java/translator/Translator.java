package translator;

import dictionary.Dictionary;
import translator.Tokenizer.Token;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Translator {

    private record Match(String translation, int lastTokenIndex) {
    }

    private final Dictionary dictionary;
    private final int maxPhraseWords;

    public Translator(Dictionary dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary, "Словарь не может быть null");
        this.maxPhraseWords = dictionary.phrases().stream()
                .mapToInt(Tokenizer::countWords)
                .max()
                .orElse(0);
    }

    public String translate(String text) {
        Objects.requireNonNull(text, "Текст не может быть null");
        List<Token> tokens = Tokenizer.tokenize(text);
        StringBuilder result = new StringBuilder();

        int index = 0;
        while (index < tokens.size()) {
            Token token = tokens.get(index);
            Optional<Match> match = token.word() ? findLongestMatch(text, tokens, index) : Optional.empty();
            if (match.isPresent()) {
                result.append(match.get().translation());
                index = match.get().lastTokenIndex() + 1;
            } else {
                result.append(token.text());
                index++;
            }
        }
        return result.toString();
    }

    private Optional<Match> findLongestMatch(String text, List<Token> tokens, int first) {
        int begin = tokens.get(first).start();
        for (int extraWords = maxPhraseWords - 1; extraWords >= 0; extraWords--) {
            int last = first + 2 * extraWords;
            if (last >= tokens.size()) {
                continue;
            }
            String phrase = text.substring(begin, tokens.get(last).end());
            Optional<String> translation = dictionary.find(phrase);
            if (translation.isPresent()) {
                return Optional.of(new Match(translation.get(), last));
            }
        }
        return Optional.empty();
    }
}