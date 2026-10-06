package dictionary;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public final class Dictionary {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);

    private final Map<String, String> entries;

    public Dictionary(Map<String, String> entries) {
        Objects.requireNonNull(entries, "Записи словаря не могут быть null");
        Map<String, String> normalized = new LinkedHashMap<>();
        entries.forEach((phrase, translation) -> normalized.put(
                normalize(phrase), Objects.requireNonNull(translation, "Перевод не может быть null")));
        this.entries = Collections.unmodifiableMap(normalized);
    }

    public static String normalize(String phrase) {
        Objects.requireNonNull(phrase, "Фраза не может быть null");
        return WHITESPACE.matcher(phrase.strip()).replaceAll(" ").toLowerCase(Locale.ROOT);
    }

    public Optional<String> find(String phrase) {
        return Optional.ofNullable(entries.get(normalize(phrase)));
    }

    public Set<String> phrases() {
        return entries.keySet();
    }

    public int size() {
        return entries.size();
    }
}