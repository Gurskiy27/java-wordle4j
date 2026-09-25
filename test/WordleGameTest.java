import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    private WordleDictionary dictionary() {

        return new WordleDictionary(
                List.of(
                        "слово",
                        "стена",
                        "город",
                        "герой",
                        "книга",
                        "баоба",
                        "бабак"
                )
        );
    }

    private PrintWriter testLog() {

        return new PrintWriter(
                new StringWriter()
        );
    }

    @Test
    void correctAttemptFinishesGameAndWins()
            throws Exception {

        WordleGame game =
                new WordleGame(
                        dictionary(),
                        testLog(),
                        "слово"
                );

        String hint =
                game.makeAttempt("слово");

        assertEquals(
                "+++++",
                hint
        );

        assertTrue(game.isFinished());
        assertTrue(game.isWon());

        assertEquals(
                5,
                game.getRemainingAttempts()
        );
    }

    @Test
    void incorrectAttemptConsumesOneAttempt()
            throws Exception {

        WordleGame game =
                new WordleGame(
                        dictionary(),
                        testLog(),
                        "слово"
                );

        game.makeAttempt("стена");

        assertFalse(game.isFinished());
        assertFalse(game.isWon());

        assertEquals(
                5,
                game.getRemainingAttempts()
        );
    }

    @Test
    void invalidWordDoesNotConsumeAttempt() {

        WordleGame game =
                new WordleGame(
                        dictionary(),
                        testLog(),
                        "слово"
                );

        assertThrows(
                InvalidWordException.class,
                () -> game.makeAttempt("abcde")
        );

        assertEquals(
                6,
                game.getRemainingAttempts()
        );

        assertTrue(
                game.getAttempts().isEmpty()
        );
    }

    @Test
    void wordOutsideDictionaryDoesNotConsumeAttempt() {

        WordleGame game =
                new WordleGame(
                        dictionary(),
                        testLog(),
                        "слово"
                );

        assertThrows(
                WordNotFoundInDictionary.class,
                () -> game.makeAttempt("лампа")
        );

        assertEquals(
                6,
                game.getRemainingAttempts()
        );
    }

    @Test
    void nullInputDoesNotConsumeAttempt() {

        WordleGame game =
                new WordleGame(
                        dictionary(),
                        testLog(),
                        "слово"
                );

        assertThrows(
                InvalidWordException.class,
                () -> game.makeAttempt(null)
        );

        assertEquals(
                6,
                game.getRemainingAttempts()
        );
    }

    @Test
    void sixValidIncorrectAttemptsFinishGame()
            throws Exception {

        WordleGame game =
                new WordleGame(
                        dictionary(),
                        testLog(),
                        "слово"
                );

        List<String> attempts =
                List.of(
                        "стена",
                        "город",
                        "герой",
                        "книга",
                        "баоба",
                        "бабак"
                );

        for (String attempt : attempts) {
            game.makeAttempt(attempt);
        }

        assertTrue(game.isFinished());
        assertFalse(game.isWon());

        assertEquals(
                0,
                game.getRemainingAttempts()
        );
    }

    @Test
    void attemptAfterGameFinishedIsRejected()
            throws Exception {

        WordleGame game =
                new WordleGame(
                        dictionary(),
                        testLog(),
                        "слово"
                );

        game.makeAttempt("слово");

        assertThrows(
                IllegalStateException.class,
                () -> game.makeAttempt("стена")
        );
    }

    @Test
    void hintDoesNotReturnAlreadyUsedWord()
            throws Exception {

        WordleGame game =
                new WordleGame(
                        dictionary(),
                        testLog(),
                        "слово"
                );

        game.makeAttempt("стена");

        String hint = game.getHint();

        assertNotEquals(
                "стена",
                hint
        );

        assertTrue(
                game.getCandidates()
                        .contains(hint)
        );
    }

    @Test
    void candidatesMatchPreviousHints()
            throws Exception {

        WordleGame game =
                new WordleGame(
                        dictionary(),
                        testLog(),
                        "слово"
                );

        game.makeAttempt("стена");

        for (String candidate :
                game.getCandidates()) {

            assertEquals(
                    game.getHints().get(0),
                    WordleDictionary.calculateHint(
                            game.getAttempts().get(0),
                            candidate
                    )
            );
        }
    }

    @Test
    void automaticHintsDoNotRepeat() {

        WordleGame game =
                new WordleGame(
                        dictionary(),
                        testLog(),
                        "слово"
                );

        String first = game.getHint();
        String second = game.getHint();

        assertNotEquals(
                first,
                second
        );
    }
}