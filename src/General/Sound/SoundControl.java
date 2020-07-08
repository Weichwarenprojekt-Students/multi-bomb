package General.Sound;

import General.MB;

import javax.sound.sampled.*;

/**
 * This class gives a control over the game sound
 */
public class SoundControl {

    /**
     * Volume control for the music
     */
    private static FloatControl control;
    /**
     * The music clip
     */
    private static Clip music;

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
            FloatControl control = (FloatControl) line.getControl((FloatControl.Type.MASTER_GAIN));
            control.setValue(MB.settings.soundVolume);

            // Start the clip in a new thread and hold that thread alive till it is over
            new Thread(() -> {
                try {
                    line.start();
                    int counter;
                    byte[] soundBuffer = new byte[1000];
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
     * @param sound the sound effect to be looped
     */
    public static void playMusic(SoundEffect sound) {
        try {
            // Stop old music
            if (music != null && music.isRunning()) {
                music.stop();
            }

            // Set up an audio input stream with file from url
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sound.url);

            // Get resource for music
            music = AudioSystem.getClip();

            // Open audio music and load sample from input stream
            music.open(audioInputStream);

            // Set the volume
            control = (FloatControl) music.getControl((FloatControl.Type.MASTER_GAIN));
            control.setValue(MB.settings.musicVolume);

            // Start the loop and add it to the map
            music.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the percentage value for the volume
     */
    public static int getVolumePercent(float volume) {
        float tmp = (volume - control.getMinimum()) * 100 / (control.getMaximum() - control.getMinimum());
        return (int) tmp;
    }

    /**
     * Change the volume of the music
     * minDB = -80.0f and maxDB = 6.0206
     *
     * @param volume the volume
     */
    public static void changeMusicVolume(int volume) {
        // Calculate the new value
        MB.settings.musicVolume =
                (float) ((control.getMaximum() - control.getMinimum()) * (volume / 100.0)) + control.getMinimum();

        // Adjust the volumes for the active sounds
        control.setValue(MB.settings.musicVolume);

        // Save the settings
        MB.settings.saveSettings();
    }

    /**
     * Change the volume of the sound effects
     * minDB = -80.0f and maxDB = 6.0206
     *
     * @param volume the volume
     */
    public static void changeSoundVolume(int volume) {
        // Calculate the new value
        MB.settings.soundVolume =
                (float) ((control.getMaximum() - control.getMinimum()) * (volume / 100.0)) + control.getMinimum();

        // Play a sound effect to make it easier to adjust the volume
        playSoundEffect(SoundEffect.COLLECT_ITEM);

        // Save the settings
        MB.settings.saveSettings();
    }
}
