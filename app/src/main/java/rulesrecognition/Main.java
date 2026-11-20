package rulesrecognition;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        RecorderGUI mainGUI = new RecorderGUI();
        mainGUI.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override 
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (mainGUI.isRecording) {
                    mainGUI.recorder.stopRecording();
                }
                System.exit(0);
            }
        });
    }

}