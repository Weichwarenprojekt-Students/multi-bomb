package General.Sound;

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
    public Clip clip;

    /**
     * variable for handling the volume dB
     */
    private float gain;

    /**
     * Volume control
     */
    FloatControl volumeControl;

    /**
     * Store playing sounds in ArrayList
     * Avoids losing the reference to an looped clip
     */
    ArrayList<Clip> sound = new ArrayList<>();

    /**
     * Constructor to get the SoundEffect and the URL
     *
     * @param effectName name of the effect
     */
    public void playSoundEffect(SoundEffect effectName, URL url, boolean isLoop) {
        try {

            //Set up an audio input stream with file from url
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);

            //Get resource for clip
            clip = AudioSystem.getClip();


            //Open audio clip and load sample from input stream
            clip.open(audioInputStream);

            //check if mute
            volumeControl = (FloatControl) clip.getControl((FloatControl.Type.MASTER_GAIN));
            volumeControl.setValue(gain);

            //Check if clip is already running, stop it, get to the position 0 and start
            if (clip.isRunning()) {
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
            }

            //check if isLoop is true, then loop, if not play the clip for one time
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
    public void loop(Clip clip) {
        sound.add(clip);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }


    /**
     * Adjusts the running clip when the volume has changed
     */
    public void adjustRunning() {
        for (Clip value : sound) {
            volumeControl = (FloatControl) value.getControl((FloatControl.Type.MASTER_GAIN));
            volumeControl.setValue(gain);
        }
    }

    /**
     * get the volume
     *
     * @return tmp
     */
    public int getVolume() {
        float tmp = (gain - volumeControl.getMinimum()) * 100 / (volumeControl.getMaximum() - volumeControl.getMinimum());
        return (int) tmp;
    }

    /**
     * Method to stop the soundeffect
     */
    public void stop() {
        clip.stop();
        clip.setFramePosition(0);
    }

    /**
     * Mute the Audio
     */
    public void muteAudio() {
        volumeControl.setValue(volumeControl.getMinimum());
        gain = volumeControl.getMinimum();
        adjustRunning();
        System.out.println("Volume muted");
    }

    /**
     * Unmute the Audio
     */
    public void unmuteAudio() {
        volumeControl.setValue(volumeControl.getMaximum());
        gain = (volumeControl.getMaximum() / 2);
        adjustRunning();
        System.out.println("Volume unmuted");
    }

    /**
     * Change the volume of the running clip
     * <p>
     * minDB = -80.0f and maxDB = 6.0206
     *
     * @param volume the volume
     */
    public void changeVolume(int volume) {
        gain = (float) ((volumeControl.getMaximum() - volumeControl.getMinimum()) * (volume / 100.0)) + volumeControl.getMinimum();
        volumeControl.setValue(gain);
        adjustRunning();
        System.out.println("The gain is: " + gain + " dB");

    }
}
