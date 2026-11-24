package rulesrecognition;

import org.vosk.Model;
import org.vosk.Recognizer;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Decoder implements Runnable {

    private final BlockingQueue<byte[]> audioQueue;
    private final AtomicBoolean recording;

    private final String modelPath;
    private final float sampleRate;

    private final RecorderGUI recorderGUI;

    public Decoder(BlockingQueue<byte[]> audioQueue, AtomicBoolean recording, String modelPath, float sampleRate, RecorderGUI recorderGUI) {
            this.audioQueue=audioQueue;
            this.recording = recording;
            this.modelPath = modelPath;
            this.sampleRate = sampleRate;
            this.recorderGUI = recorderGUI;
    }
    
    //decode Audio
    @Override 
    public void run(){
        try (Model model = new Model(modelPath);
            Recognizer recognizer = new Recognizer(model, sampleRate)){
            System.out.println("Vosk started");

            while (recording.get() || !audioQueue.isEmpty()){
                byte[] audioData = audioQueue.poll(100, TimeUnit.MILLISECONDS);
                //System.out.println("queueSize: " + audioQueue.size());
                
                if(audioData == null){
                    continue;
                }

                if(recognizer.acceptWaveForm(audioData, audioData.length)){
                    String result = recognizer.getResult();
                    String currentWords[] = jsonToStrings(result);
                    if(currentWords[0] != null){
                        recorderGUI.addToSubs(currentWords);
                    }
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
        json = json.replace("\"", "");
        json = json.replace(":", "");
        json = json.replace("}", "");
        json = json.replace("{", "");
        String[] splitText = json.split(" ");
        ArrayList<String> relevantText = new ArrayList<>();
        System.out.println("Beginning text parsing");
        for(String txt : splitText){
            if(!(txt.equals("\"")) && 
            !(txt.equals(":")) && 
            !(txt.equals("text")) &&  
            !(txt.isBlank()) && 
            !(txt.isEmpty())){
                //for now, manually parsing the string, have to get a json parser to get the specific info out later
                relevantText.add(txt);
                System.out.println(txt);
            }
        }
        System.out.println("End text parsing");
        if(relevantText.size() > 0){
            return relevantText.toArray(new String[relevantText.size()-1]);
        } else {
            return new String[1];//returns blank string, will be ignored by the next step up
        }
        
    }

}
