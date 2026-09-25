import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {

        PrintWriter log = null;

        try {
            log = new PrintWriter(
                    LOG_FILE,
                    StandardCharsets.UTF_8
            );

            Scanner scanner = new Scanner(
                    System.in,
                    StandardCharsets.UTF_8
            );

            runGame(scanner, log);

            scanner.close();

        } catch (Exception e) {

            if (log != null) {
                e.printStackTrace(log);
                log.flush();
            }

        } finally {

            if (log != null) {
                log.close();
            }
        }
    }

    private static void runGame(
            Scanner scanner,
            PrintWriter log)
            throws Exception {

        WordleDictionaryLoader loader =
                new WordleDictionaryLoader();

        WordleDictionary dictionary =
                loader.load(DICTIONARY_FILE);

        WordleGame game =
                new WordleGame(dictionary, log);

        System.out.println("Добро пожаловать в Wordle!");
        System.out.println(
                "Угадайте существительное из пяти букв."
        );
        System.out.println(
                "+ — буква на правильном месте"
        );
        System.out.println(
                "^ — буква есть, но место неправильное"
        );
        System.out.println(
                "- — буквы нет в слове"
        );
        System.out.println(
                "Нажмите Enter для автоматической подсказки."
        );
        System.out.println();

        while (!game.isFinished()) {

            System.out.print("> ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine();

            if (input.trim().isEmpty()) {

                try {
                    System.out.println(
                            "Подсказка: " + game.getHint()
                    );

                } catch (RuntimeException e) {

                    log.println(
                            "Ошибка подсказки: "
                                    + e.getMessage()
                    );

                    log.flush();

                    System.out.println(
                            "Не удалось получить подсказку."
                    );
                }

                continue;
            }

            try {

                String result = game.makeAttempt(input);

                System.out.println(
                        WordleDictionary.normalize(input)
                );

                System.out.println(result);

                if (game.isWon()) {

                    System.out.println(
                            "Поздравляем! Вы угадали слово!"
                    );

                } else if (game.isFinished()) {

                    System.out.println(
                            "Попытки закончились."
                    );
                }

            } catch (InvalidWordException e) {

                System.out.println(e.getMessage());

            } catch (WordNotFoundInDictionary e) {

                System.out.println(
                        "Такого слова нет в словаре."
                );
            }
        }

        System.out.println();
        System.out.println(
                "Загаданное слово: " + game.getAnswer()
        );
    }
}