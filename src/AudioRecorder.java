import javax.sound.sampled.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioRecorder {

    private TargetDataLine targetLine;
    private final AtomicBoolean recording = new AtomicBoolean(false);
    private Thread recordingThread;


    /** start Recording
    * begins recording audio from format into an output file
     * outputFilePath - path to the file we are writing the recording to(wav)
     * format - the specfic audio format that we are using
    **/
    public void startRecording(String outputFilePath, AudioFormat format)
        throws LineUnavailableException{
        if (recording.get()) {
            throw new LineUnavailableException("Recording already started");
        }

        //configures and opens a DataLine to receive audio from
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Line " + info + " not supported");
        }

        //begins our recording
        targetLine = (TargetDataLine) AudioSystem.getLine(info);
        targetLine.open(format);
        targetLine.start();

        recording.set(true);

        recordingThread = new Thread(() -> {
            try {
                recordAudio(outputFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        recordingThread.start();

    }

    private void recordAudio(String outputFilePath) throws IOException {
        File outputFile = new File(outputFilePath);
        try (AudioInputStream audioStream = new AudioInputStream(targetLine)){
            AudioSystem.write(
                    audioStream,
                    AudioFileFormat.Type.WAVE,//indicating a .wav file
                    /*Alternatives:
                    * AU
                    * AIFF
                    * AIFC
                    * SND
                    * */
                    outputFile
            );
        }
    }

    public void stopRecording() {
        if(!recording.get()) {
            return;
        }

        recording.set(false);

        if(targetLine != null) {
            targetLine.stop();
            targetLine.close();
        }


        if (recordingThread != null) {
            //catching the currently running thread
            try {
                recordingThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isRecording() {
        return recording.get();
    }

}
