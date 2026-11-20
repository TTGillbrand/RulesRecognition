package rulesrecognition;

import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Decoder implements Runnable {

    private final BlockingQueue<byte[]> audioQueue;
    private final AtomicBoolean recording;

    private final String modelPath;
    private final float sampleRate;

    public Decoder(BlockingQueue<byte[]> audioQueue, AtomicBoolean recording, String modelPath, float sampleRate) {
            this.audioQueue=audioQueue;
            this.recording = recording;
            this.modelPath = modelPath;
            this.sampleRate = sampleRate;
    }
    
    //decode Audio
    @Override 
    public void run(){
        try (Model model = new Model(modelPath);
            Recognizer recognizer = new Recognizer(model, sampleRate)){
            System.out.println("Vosk started");

            while (recording.get() || !audioQueue.isEmpty()){
                byte[] audioData = audioQueue.poll(100, TimeUnit.MILLISECONDS);
                System.out.println("queueSize: " + audioQueue.size());
                
                if(audioData == null){
                    continue;
                }

                if(recognizer.acceptWaveForm(audioData, audioData.length)){
                    String result = recognizer.getResult();

                    System.out.println("Recognized: " + result);
                } else {
                    String partialResult = recognizer.getPartialResult();

                    System.out.println("Partial: " + partialResult);
                }
            }

            String finalResult = recognizer.getFinalResult();

            System.out.println("Final: " + finalResult);

            System.out.println("Vosk stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String[] jsonToStrings(String json){
        return json.split(" ");
    }

}
