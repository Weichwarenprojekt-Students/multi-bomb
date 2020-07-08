package General.Sound;

import General.MB;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class gives a control over the game sound
 */

public class SoundControl {

    /**
     * Every sound effect has its own clip, with its sound file
     */
    public static Clip clip;

    /**
     * Volume control
     */
    private static FloatControl control;

    /**
     * Store playing sounds in ArrayList
     * Avoids losing the reference to an looped clip
     */
    private static final ArrayList<Clip> sound = new ArrayList<>();

    /**
     * Constructor to get the SoundEffect and the URL
     */
    public static void playSoundEffect(SoundEffect effect, boolean isLoop) {
        try {
            // Set up an audio input stream with file from url
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(effect.url);

            // Get resource for clip
            clip = AudioSystem.getClip();

            // Open audio clip and load sample from input stream
            clip.open(audioInputStream);

            // Check if mute
            control = (FloatControl) clip.getControl((FloatControl.Type.MASTER_GAIN));
            control.setValue(MB.settings.gain);

            // Check if clip is already running, stop it, get to the position 0 and start
            clip.stop();
            clip.setFramePosition(0);

            // Check if isLoop is true, then loop, if not play the clip for one time
            if (isLoop) {
                loop(clip);
            } else {
                clip.start();
                clip.setFramePosition(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to loop a clip
     * Adds the clip to the ArrayList sound
     *
     * @param clip the clip
     */
    public static void loop(Clip clip) {
        sound.add(clip);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }


    /**
     * Adjusts the running clip when the volume has changed
     */
    public static void adjustRunning() {
        for (Clip value : sound) {
            control = (FloatControl) value.getControl((FloatControl.Type.MASTER_GAIN));
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
     */
    public static void stop() {
        clip.stop();
        clip.setFramePosition(0);
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
