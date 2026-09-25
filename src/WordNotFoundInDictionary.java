public class WordNotFoundInDictionary extends WordleException {

    public WordNotFoundInDictionary(String word) {
        super("Слово \"" + word + "\" отсутствует в словаре.");
    }
}
