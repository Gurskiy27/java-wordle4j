import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    private WordleDictionary dictionary() {

        return new WordleDictionary(
                List.of(
                        "слово",
                        "стена",
                        "город",
                        "герой",
                        "книга",
                        "баоба",
                        "бабак",
                        "ёлка"
                )
        );
    }

    @Test
    void normalizeConvertsYoToYeAndLowerCase() {

        assertEquals(
                "ершик",
                WordleDictionary.normalize(" ЁРШИК ")
        );
    }

    @Test
    void validWordMustContainExactlyFiveRussianLetters() {

        assertTrue(
                WordleDictionary.isValidWord("слово")
        );

        assertFalse(
                WordleDictionary.isValidWord("дом")
        );

        assertFalse(
                WordleDictionary.isValidWord("словаа")
        );

        assertFalse(
                WordleDictionary.isValidWord("word1")
        );

        assertFalse(
                WordleDictionary.isValidWord("сло-о")
        );
    }

    @Test
    void calculateHintReturnsAllPlusForCorrectWord() {

        assertEquals(
                "+++++",
                WordleDictionary.calculateHint(
                        "слово",
                        "слово"
                )
        );
    }

    @Test
    void calculateHintReturnsAllMinusWhenNoLettersMatch() {

        assertEquals(
                "-----",
                WordleDictionary.calculateHint(
                        "слово",
                        "книга"
                )
        );
    }

    @Test
    void calculateHintHandlesRepeatedLetters() {

        assertEquals(
                "++^^-",
                WordleDictionary.calculateHint(
                        "бабак",
                        "баоба"
                )
        );
    }

    @Test
    void containsNormalizesInput() {

        assertTrue(
                dictionary().contains(" СЛОВО ")
        );
    }

    @Test
    void findCandidatesUsesPreviousHint() {

        WordleDictionary dictionary =
                new WordleDictionary(
                        List.of(
                                "стена",
                                "степь",
                                "стенд",
                                "стриж",
                                "сцена"
                        )
                );

        String attempt = "сцена";

        String hint =
                WordleDictionary.calculateHint(
                        attempt,
                        "стена"
                );

        List<String> candidates =
                dictionary.findCandidates(
                        List.of(attempt),
                        List.of(hint),
                        Set.of(attempt)
                );

        assertTrue(candidates.contains("стена"));

        for (String candidate : candidates) {
            assertEquals(
                    hint,
                    WordleDictionary.calculateHint(
                            attempt,
                            candidate
                    )
            );
        }

        assertFalse(candidates.contains("сцена"));
    }

    @Test
    void findCandidatesDoesNotReturnUsedWords() {

        WordleDictionary dictionary =
                new WordleDictionary(
                        List.of(
                                "слово",
                                "слова",
                                "стена"
                        )
                );

        List<String> candidates =
                dictionary.findCandidates(
                        List.of(),
                        List.of(),
                        Set.of("слово")
                );

        assertFalse(
                candidates.contains("слово")
        );

        assertEquals(
                2,
                candidates.size()
        );
    }

    @Test
    void findCandidatesWithNoAttemptsReturnsUnusedWords() {

        WordleDictionary dictionary =
                new WordleDictionary(
                        List.of(
                                "слово",
                                "стена",
                                "город"
                        )
                );

        List<String> candidates =
                dictionary.findCandidates(
                        List.of(),
                        List.of(),
                        Set.of("стена")
                );

        assertEquals(
                List.of("слово", "город"),
                candidates
        );
    }
}