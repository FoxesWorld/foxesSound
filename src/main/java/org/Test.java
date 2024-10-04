package org;

/**
 * @author AidenFox
 */

import org.foxesworld.foxesSound.PlaySound;
import org.foxesworld.foxesSound.PlaybackCallback;
import org.foxesworld.foxesSound.decoder.JavaLayerException;
import java.io.FileNotFoundException;
import static org.SysUtils.send;

/* 
 *   A test class
 *   playInternalSound(String, float) for an internal sound Now supports URL!!!
 *   playExternalSound(String, float) for an external sound
 */

public class Test implements PlaybackCallback {

    private Start start;

    public Test(Start start){
        this.start = start;
    }

    public void test() throws JavaLayerException, FileNotFoundException, InterruptedException {
        PlaySound snd = new PlaySound(this);
        snd.playInternalSound("snd/start.mp3", 5f);
        Thread.sleep(2100);
        snd.playInternalSound("snd/mus13.mp3", -10f);
        Integer wait = getRandomIntegerBetweenRange(10000, 25000);
        String sound;
        
        if(wait >= 20000){
            sound = "snd/amazing.mp3";
        } else {
            sound = "snd/look&amaze.mp3";
        }
        send (sound + " " + wait);
        Thread.sleep(5000);
        snd.playInternalSound(sound, 5f);   
    }
    
    public static Integer getRandomIntegerBetweenRange(Integer min, Integer max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    @Override
    public void onPlaybackStarted(String filename) {
        this.start.getBtn().setEnabled(false);
    }

    @Override
    public void onPlaybackCompleted(String filename) {

    }

    @Override
    public void onPlaybackError(String filename, String errorMessage) {

    }

    @Override
    public void onPlaybackPosition(String filename, int position) {

    }

    @Override
    public void onAllPlaybackCompleted() {
        this.start.getBtn().setEnabled(true);
    }
}