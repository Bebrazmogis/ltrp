package lt.ltrp.player;

import java.util.Properties;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerSettings {

    private LtrpPlayer player;
    private boolean pmDisabled;
    private boolean oocDisabled;
    private boolean newsDisabled;
    private boolean soundsDisabled;
    private boolean musicDisabled;

    public PlayerSettings(LtrpPlayer player, Properties properties) {
        this.player = player;
        pmDisabled = Boolean.parseBoolean(properties.getProperty("pm_disabled", "false"));
        oocDisabled = Boolean.parseBoolean(properties.getProperty("ooc_disabled", "false"));
        newsDisabled = Boolean.parseBoolean(properties.getProperty("news_disabled", "false"));
        soundsDisabled = Boolean.parseBoolean(properties.getProperty("sounds_disabled", "false"));
        musicDisabled = Boolean.parseBoolean(properties.getProperty("music_disabled", "false"));
    }

    public Properties toProperties() {
        Properties p = new Properties();
        p.setProperty("pm_disabled", Boolean.toString(pmDisabled));
        p.setProperty("ooc_disabled", Boolean.toString(oocDisabled));
        p.setProperty("news_disabled", Boolean.toString(newsDisabled));
        p.setProperty("sounds_disabled", Boolean.toString(soundsDisabled));
        p.setProperty("music_disabled", Boolean.toString(musicDisabled));
        return p;
    }


    public LtrpPlayer getPlayer() {
        return player;
    }

    public boolean isPmDisabled() {
        return pmDisabled;
    }

    public void setPmDisabled(boolean pmDisabled) {
        this.pmDisabled = pmDisabled;
    }

    public boolean isOocDisabled() {
        return oocDisabled;
    }

    public void setOocDisabled(boolean oocDisabled) {
        this.oocDisabled = oocDisabled;
    }

    public boolean isNewsDisabled() {
        return newsDisabled;
    }

    public void setNewsDisabled(boolean newsDisabled) {
        this.newsDisabled = newsDisabled;
    }

    public boolean isSoundsDisabled() {
        return soundsDisabled;
    }

    public void setSoundsDisabled(boolean soundsDisabled) {
        this.soundsDisabled = soundsDisabled;
    }

    public boolean isMusicDisabled() {
        return musicDisabled;
    }

    public void setMusicDisabled(boolean musicDisabled) {
        this.musicDisabled = musicDisabled;
    }

    @Override
    public String toString() {
        return "player=" + player +
                ", pmDisabled=" + pmDisabled +
                ", oocDisabled=" + oocDisabled +
                ", newsDisabled=" + newsDisabled +
                ", soundsDisabled=" + soundsDisabled +
                ", musicDisabled=" + musicDisabled;
    }
}
