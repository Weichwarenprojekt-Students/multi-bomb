package General.Sound;

import java.net.URL;

/**
 * This class provides the SoundEffects for the SoundControl class
 */
public enum SoundEffect {

    SHORT_BOMB_EXPLOSION("short_bomb_explosion.wav"),
    LONG_BOMB_EXPLOSION("long_bomb_explosion.wav"),
    SET_BOMB("set_bomb.wav"),
    COLLECT_ITEM("collect_item.wav"),
    CHARACTER_DEATH("character_death.wav"),
    IN_GAME("multi_bomb_game_sound.wav");

    /**
     * The URL for the sound file
     */
    public final URL url;

    /**
     * the constructor
     *
     * @param fileName name of the file
     */
    SoundEffect(String fileName) {
        url = SoundEffect.class.getResource("/Resources/Sound/" + fileName);
    }
}
