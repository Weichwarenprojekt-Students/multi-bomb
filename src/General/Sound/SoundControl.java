package General.Sound;

import General.MB;

import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class gives a control over the game sound
 */

public class SoundControl {

    /**
     * The keys for the loops
     */
    public static final String MENU_LOOP = "Menu Loop";
    /**
     * Volume control
     */
    private static FloatControl control;
    /**
     * Store playing sounds in ArrayList
     * Avoids losing the reference to an looped clip
     */
    private static final HashMap<String, Clip> sounds = new HashMap<>();

    /**
     * Constructor to get the SoundEffect and the URL
     */
    public static synchronized void playSoundEffect(SoundEffect effect) {
        try {
            // Set up an audio input stream with file from url
            AudioInputStream ais = AudioSystem.getAudioInputStream(effect.url);

            // Get resource for clip
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, ais.getFormat());
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

            // Open audio clip and load sample from input stream
            line.open(ais.getFormat());

            // Set the volume
            control = (FloatControl) line.getControl((FloatControl.Type.MASTER_GAIN));
            control.setValue(MB.settings.gain);

            // Start the clip in a new thread and hold that thread alive till it is over
            new Thread(() -> {
                try {
                    line.start();
                    int counter;
                    byte[] soundBuffer = new byte[10000];
                    while ((counter = ais.read(soundBuffer, 0, soundBuffer.length)) != -1) {
                        if (counter > 1) {
                            line.write(soundBuffer, 0, counter);
                        }
                    }
                    line.drain();
                    line.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loops a given sound effect
     *
     * @param name of the sound
     * @param sound the sound effect to be looped
     */
    public static void playLoop(String name, SoundEffect sound) {
        try {
            // Set up an audio input stream with file from url
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sound.url);

            // Get resource for clip
            Clip clip = AudioSystem.getClip();

            // Open audio clip and load sample from input stream
            clip.open(audioInputStream);

            // Set the volume
            control = (FloatControl) clip.getControl((FloatControl.Type.MASTER_GAIN));
            control.setValue(MB.settings.gain);

            // Start the loop and add it to the map
            sounds.put(name, clip);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Adjusts the running clip when the volume has changed
     */
    public static void adjustRunning() {
        for (Map.Entry<String, Clip> value : sounds.entrySet()) {
            control = (FloatControl) value.getValue().getControl((FloatControl.Type.MASTER_GAIN));
            control.setValue(MB.settings.gain);
        }
    }

    /**
     * get the volume
     *
     * @return tmp
     */
    public static int getVolume() {
        float tmp = (MB.settings.gain - control.getMinimum()) * 100 / (control.getMaximum() - control.getMinimum());
        return (int) tmp;
    }

    /**
     * Method to stop the sound effect
     *
     * @param name of the loop that should be stopped
     */
    public static void stopLoop(String name) {
        if (sounds.containsKey(name)) {
            sounds.get(name).stop();
            sounds.get(name).setFramePosition(0);
        }
    }

    /**
     * Change the volume of the running clip
     * <p>
     * minDB = -80.0f and maxDB = 6.0206
     *
     * @param volume the volume
     */
    public static void changeVolume(int volume) {
        MB.settings.gain =
                (float) ((control.getMaximum() - control.getMinimum()) * (volume / 100.0)) + control.getMinimum();
        control.setValue(MB.settings.gain);
        adjustRunning();
        MB.settings.saveSettings();
    }
}
