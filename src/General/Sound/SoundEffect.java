package General.Sound;


import General.Shared.MBImage;

import java.net.URL;


/**
 * This class provides the SoundEffects for the SoundControl class
 */

public enum SoundEffect {
    SHORTBOMBEXPLOSION("shortBombexplosion.wav"),
    LONGBOMBEXPLOSION("longBombexplosion.wav"),
    SETBOMB("setBomb.wav"),
    COLLECTITEM("collecitem.wav"),
    CHARACTERDEATH("characrterDeath.wav"),
    INGAME("multibombgamesound.wav");


    //the URL for the sound file
    URL url;

    /**
     * the constructor
     *
     * @param fileName name of the file
     */
    SoundEffect(String fileName) {

        url = MBImage.class.getResource("/Resources/Sound/" + fileName);

    }

    /**
     * Method to return the sound file URL
     *
     * @return url
     */
    public URL getUrl() {
        return url;
    }
}
