import javax.sound.sampled.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioRecorder {

    private TargetDataLine targetLine;
    private final AtomicBoolean recording = new AtomicBoolean(false);

    private Thread recordingThread;
    private Thread recognizerThread;

    private final BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>();


    /** start Recording
    * begins recording audio from format into an output file
     * outputFilePath - path to the file we are writing the recording to(wav)
     * format - the specfic audio format that we are using
    **/
    public void startRecording(AudioFormat format) throws LineUnavailableException{
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

        recordingThread = new Thread(this::captureAudio);
        recordingThread.start();

        recognizerThread = new Thread(() -> {
            while (recording.get()) {
                try {
                    byte[] audio = null;
                    try {
                        audio = audioQueue.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    recognizer.process(audio);
                }
            }
        });
        recognizerThread.start();

        if(recognizerThread != null){
            try {
                recognizerThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void captureAudio(){
        byte[] buffer = new byte[4096];
        while (recording.get()) {
            int bytesRead = targetLine.read(buffer, 0, buffer.length);
            //reads audio information to a buffer variable, that is copied to our queue
            if (bytesRead > 0) {
                byte[] chunk = Arrays.copyOfRange(buffer, 0, bytesRead);

                audioQueue.add(chunk);
            }
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

    public TargetDataLine getTargetLine() {
        return targetLine;
    }

//    public AudioInputStream getAudioInputStream(){
//        return AudioInputStream(targetLine);
//    }

    public BlockingQueue<byte[]> getAudioQueue() {
        return audioQueue;
    }



}
