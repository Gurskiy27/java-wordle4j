import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsUtf8DictionaryAndFiltersInvalidLines()
            throws Exception {

        Path file = tempDir.resolve("dictionary.txt");

        Files.write(
                file,
                List.of(
                        "СЛОВО",
                        "ЁРШИК",
                        "дом",
                        "word1",
                        "стена"
                ),
                StandardCharsets.UTF_8
        );

        WordleDictionary dictionary =
                new WordleDictionaryLoader()
                        .load(file.toString());

        assertEquals(
                List.of(
                        "слово",
                        "ершик",
                        "стена"
                ),
                dictionary.getWords()
        );
    }

    @Test
    void missingFileProducesDictionaryException() {

        Path file = tempDir.resolve("missing.txt");

        assertThrows(
                DictionaryException.class,
                () -> new WordleDictionaryLoader()
                        .load(file.toString())
        );
    }

    @Test
    void emptySuitableDictionaryProducesException()
            throws Exception {

        Path file = tempDir.resolve("empty.txt");

        Files.write(
                file,
                List.of(
                        "дом",
                        "abc",
                        "12345"
                ),
                StandardCharsets.UTF_8
        );

        assertThrows(
                DictionaryException.class,
                () -> new WordleDictionaryLoader()
                        .load(file.toString())
        );
    }
}
