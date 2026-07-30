import javax.sound.sampled.*;
import java.io.File;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecorderGUI extends Frame{
    private Label title;
    private Button recordButton;
    private TextField log;
    boolean isRecording = false;
    public AudioFormat audioFormat;
    public AudioRecorder recorder;

    public RecorderGUI() {
        setLayout(new FlowLayout());

        title = new Label("Rule Recognizer");
        add(title);

        recordButton = new Button("Record");
        add(recordButton);

        recordButton.addActionListener(new BtnRecordToggle());

        log = new TextField(isRecording ? "Recording" : "Stopped Recording", 15);
        log.setEditable(false);
        add(log);

        setTitle("Recognizer");
        setSize(500, 500);

        setVisible(true);

         audioFormat = new AudioFormat
                (
                        AudioFormat.Encoding.PCM_SIGNED,
                        44100.0f,
                        16,
                        2,
                        4,
                        441100.0f,
                        true);
         recorder = new AudioRecorder();


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
}


