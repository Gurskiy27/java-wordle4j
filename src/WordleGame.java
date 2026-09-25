import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class WordleGame {

    public static final int MAX_ATTEMPTS = 6;

    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final String answer;

    private int remainingAttempts;
    private boolean finished;
    private boolean won;

    private final List<String> attempts = new ArrayList<>();
    private final List<String> hints = new ArrayList<>();
    private final Set<String> usedWords = new HashSet<>();
    private final Set<String> suggestedWords = new HashSet<>();

    public WordleGame(
            WordleDictionary dictionary,
            PrintWriter log) {

        if (dictionary == null) {
            throw new IllegalArgumentException(
                    "Словарь не может быть null."
            );
        }

        if (log == null) {
            throw new IllegalArgumentException(
                    "Лог не может быть null."
            );
        }

        this.dictionary = dictionary;
        this.log = log;

        int randomIndex = ThreadLocalRandom.current()
                .nextInt(dictionary.size());

        this.answer = dictionary.getWords().get(randomIndex);
        this.remainingAttempts = MAX_ATTEMPTS;

        logState("Игра создана.");
    }

    public WordleGame(
            WordleDictionary dictionary,
            PrintWriter log,
            String answer) {

        if (dictionary == null) {
            throw new IllegalArgumentException(
                    "Словарь не может быть null."
            );
        }

        if (log == null) {
            throw new IllegalArgumentException(
                    "Лог не может быть null."
            );
        }

        String normalized = WordleDictionary.normalize(answer);

        if (!dictionary.contains(normalized)) {
            throw new IllegalArgumentException(
                    "Ответ отсутствует в словаре."
            );
        }

        this.dictionary = dictionary;
        this.log = log;
        this.answer = normalized;
        this.remainingAttempts = MAX_ATTEMPTS;

        logState("Игра создана с заданным ответом.");
    }

    public String makeAttempt(String input)
            throws WordleException {

        if (finished) {
            throw new IllegalStateException(
                    "Игра уже завершена."
            );
        }

        String word = validateAndNormalize(input);

        if (!dictionary.contains(word)) {
            throw new WordNotFoundInDictionary(word);
        }

        remainingAttempts--;

        attempts.add(word);
        usedWords.add(word);

        String hint = WordleDictionary.calculateHint(
                word,
                answer
        );

        hints.add(hint);

        if (word.equals(answer)) {
            won = true;
            finished = true;
        } else if (remainingAttempts == 0) {
            finished = true;
        }

        logState(
                "Ход: "
                        + word
                        + ", результат: "
                        + hint
        );

        return hint;
    }

    private String validateAndNormalize(String input)
            throws InvalidWordException {

        if (input == null) {
            throw new InvalidWordException(
                    "Ввод не может быть null."
            );
        }

        String word = WordleDictionary.normalize(input);

        if (word.isEmpty()) {
            throw new InvalidWordException(
                    "Введите слово из пяти букв."
            );
        }

        if (!WordleDictionary.isValidWord(word)) {
            throw new InvalidWordException(
                    "Слово должно состоять из пяти русских букв."
            );
        }

        return word;
    }

    public String getHint() {

        List<String> candidates =
                new ArrayList<>(getCandidates());

        candidates.removeIf(suggestedWords::contains);

        if (candidates.isEmpty()) {
            throw new IllegalStateException(
                    "Не осталось новых слов для подсказки."
            );
        }

        String suggestion = candidates.get(
                ThreadLocalRandom.current()
                        .nextInt(candidates.size())
        );

        suggestedWords.add(suggestion);

        return suggestion;
    }

    public List<String> getCandidates() {
        return dictionary.findCandidates(
                attempts,
                hints,
                usedWords
        );
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWon() {
        return won;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getAttempts() {
        return List.copyOf(attempts);
    }

    public List<String> getHints() {
        return List.copyOf(hints);
    }

    private void logState(String message) {
        log.println(
                message
                        + " | remainingAttempts="
                        + remainingAttempts
                        + " | finished="
                        + finished
                        + " | won="
                        + won
                        + " | attempts="
                        + attempts
        );

        log.flush();
    }
}
