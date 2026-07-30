public class SpeechRecognizer {

    private final Recognizer recognizer;

    public SpeechRecognizer(Model model){
        recognizer = new Recognizer(model, 16000)
    }
}
