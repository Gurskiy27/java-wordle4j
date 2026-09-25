import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    public WordleDictionary load(String fileName)
            throws IOException, DictionaryException {

        if (fileName == null || fileName.isBlank()) {
            throw new DictionaryException(
                    "Не указано имя файла словаря."
            );
        }

        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            throw new DictionaryException(
                    "Файл словаря не найден: " + fileName
            );
        }

        List<String> words = new ArrayList<>();

        try (BufferedReader reader =
                     Files.newBufferedReader(
                             path,
                             StandardCharsets.UTF_8
                     )) {

            String line;

            while ((line = reader.readLine()) != null) {

                String word =
                        WordleDictionary.normalize(line);

                if (WordleDictionary.isValidWord(word)) {
                    words.add(word);
                }
            }

        } catch (IOException e) {
            throw new DictionaryException(
                    "Ошибка чтения словаря: " + fileName,
                    e
            );
        }

        if (words.isEmpty()) {
            throw new DictionaryException(
                    "Словарь не содержит подходящих слов."
            );
        }

        return new WordleDictionary(words);
    }
}
