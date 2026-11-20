package rulesrecognition;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class RecorderGUI extends Frame{
    private Label title;
    private Button recordButton;
    private TextField subsLabel;
    private TextField log;
    boolean isRecording = false;
    public AudioFormat audioFormat;
    public AudioRecorder recorder;

    ArrayList<String> subtitles = new ArrayList<>();

    public RecorderGUI() {
        subtitles.add("hlululululh");
        subtitles.add("hlululululh 2");
        setLayout(new FlowLayout());

        title = new Label("Rule Recognizer");
        add(title);

        recordButton = new Button("Record");
        add(recordButton);

        subsLabel = new TextField(convertSubtitlesToString(subtitles), 50);
        subsLabel.setEditable(false);
        add(subsLabel);



        recordButton.addActionListener(new BtnRecordToggle());

        log = new TextField(isRecording ? "Recording" : "Stopped Recording", 15);
        log.setEditable(false);
        add(log);

        setTitle("Recognizer");
        setSize(500, 500);

        

        setVisible(true);

        /*-----------------------------*/

         audioFormat = new AudioFormat
                (
                        AudioFormat.Encoding.PCM_SIGNED,//encoding type
                        16000.0f,//sample rate
                        16,//sample size(bits)
                        1,//# channels
                        2,//frame size
                        16000.0f,//frame rate
                        false);//endianess(little)
         recorder = new AudioRecorder();


    }

    private String convertSubtitlesToString(ArrayList<String> subtitles){
        String combinedString = "";
        combinedString = String.join(" ", subtitles);
        //System.out.println("asdasdasdasdas");
        return combinedString;
    }

    public void addToSubs(String addition){
        subtitles.add(addition);
    }

    private class BtnRecordToggle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (isRecording) {
                    recorder.stopRecording();
                } else {
                    recorder.startRecording(audioFormat);
                }
            }  catch (LineUnavailableException ex) {
                ex.printStackTrace();
            }

            isRecording = !isRecording;
            log.setText(isRecording ? "Recording" + "\n" : "Stopped Recording" + "\n");
        }
    }

//     @Override 
//     void addWindowListener(WindowAdapter w1){
//         super.addWindowListener(WindowAdapter w1);
//     }
}


