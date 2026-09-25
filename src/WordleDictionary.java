import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordleDictionary {

    public static final int WORD_LENGTH = 5;

    private final List<String> words;
    private final Set<String> wordSet;

    public WordleDictionary(Collection<String> words) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException(
                    "Словарь не может быть пустым."
            );
        }

        List<String> normalizedWords = new ArrayList<>();

        for (String word : words) {
            String normalized = normalize(word);

            if (isValidWord(normalized)) {
                normalizedWords.add(normalized);
            }
        }

        if (normalizedWords.isEmpty()) {
            throw new IllegalArgumentException(
                    "В словаре нет подходящих слов."
            );
        }

        this.words = Collections.unmodifiableList(normalizedWords);
        this.wordSet = Collections.unmodifiableSet(
                new HashSet<>(normalizedWords)
        );
    }

    public static String normalize(String word) {
        if (word == null) {
            return null;
        }

        return word
                .trim()
                .toLowerCase()
                .replace('ё', 'е');
    }

    public static boolean isValidWord(String word) {
        if (word == null || word.length() != WORD_LENGTH) {
            return false;
        }

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if (c < 'а' || c > 'я') {
                return false;
            }
        }

        return true;
    }

    public boolean contains(String word) {
        String normalized = normalize(word);

        return normalized != null
                && wordSet.contains(normalized);
    }

    public List<String> getWords() {
        return words;
    }

    public int size() {
        return words.size();
    }

    public List<String> findCandidates(
            List<String> attempts,
            List<String> hints,
            Set<String> usedWords) {

        if (attempts == null
                || hints == null
                || usedWords == null) {

            throw new IllegalArgumentException(
                    "Аргументы не могут быть null."
            );
        }

        if (attempts.size() != hints.size()) {
            throw new IllegalArgumentException(
                    "Количество попыток и подсказок должно совпадать."
            );
        }

        List<String> result = new ArrayList<>();

        for (String candidate : words) {

            if (usedWords.contains(candidate)) {
                continue;
            }

            boolean matchesAll = true;

            for (int i = 0; i < attempts.size(); i++) {

                String actualHint = calculateHint(
                        attempts.get(i),
                        candidate
                );

                if (!actualHint.equals(hints.get(i))) {
                    matchesAll = false;
                    break;
                }
            }

            if (matchesAll) {
                result.add(candidate);
            }
        }

        return result;
    }

    public static String calculateHint(
            String guess,
            String answer) {

        String normalizedGuess = normalize(guess);
        String normalizedAnswer = normalize(answer);

        if (!isValidWord(normalizedGuess)
                || !isValidWord(normalizedAnswer)) {

            throw new IllegalArgumentException(
                    "Оба слова должны состоять из пяти русских букв."
            );
        }

        char[] result = new char[WORD_LENGTH];
        boolean[] usedAnswerLetters = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            result[i] = '-';
        }

        // Сначала отмечаем буквы на правильных позициях.
        for (int i = 0; i < WORD_LENGTH; i++) {

            if (normalizedGuess.charAt(i)
                    == normalizedAnswer.charAt(i)) {

                result[i] = '+';
                usedAnswerLetters[i] = true;
            }
        }

        // Затем ищем буквы, которые есть в слове,
        // но находятся на другой позиции.
        for (int i = 0; i < WORD_LENGTH; i++) {

            if (result[i] == '+') {
                continue;
            }

            char letter = normalizedGuess.charAt(i);

            for (int j = 0; j < WORD_LENGTH; j++) {

                if (!usedAnswerLetters[j]
                        && normalizedAnswer.charAt(j) == letter) {

                    result[i] = '^';
                    usedAnswerLetters[j] = true;
                    break;
                }
            }
        }

        return new String(result);
    }
}
