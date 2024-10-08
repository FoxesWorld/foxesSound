package org.foxesworld.foxesSound;

import org.foxesworld.foxesSound.decoder.JavaLayerException;
import org.foxesworld.foxesSound.player.Player;
import org.foxesworld.foxesSound.player.URLplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.SysUtils.sendErr;

/**
 * PlaySound class for audio playback
 */
public class PlaySound {
    private Player player;
    private static final Thread[] threads = new Thread[4];
    private int num = 0;
    public static int UPDATE_RATE = 1000;
    private PlaybackCallback callback;
    private final AtomicInteger activeSounds;

    public PlaySound() {
        this.activeSounds = new AtomicInteger(0);
    }

    public void playExternalSound(String file, float volume, PlaybackCallback callback) {
        this.callback = callback;
        if (file.contains("http:") || file.contains("https:")) {
            playSoundFromURL(file);
        } else {
            playSoundFromFile(file, volume);
        }
    }

    private void playSoundFromURL(String url) {
        String[] urlArgs = {"-url", url};
        Thread musPlay = new Thread(() -> {
            callback.onPlaybackStarted(url);
            try {
                URLplayer.main(urlArgs);
                callback.onPlaybackCompleted(url);
            } catch (Exception e) {
                Logger.getLogger(PlaySound.class.getName()).log(Level.SEVERE, "Error playing URL: " + url, e);
                callback.onPlaybackError(url, e.getMessage());
            }
            checkAndNotifyCompletion();
        });
        startThread(musPlay);
    }

    private void playSoundFromFile(String filePath, float volume) {
        try {
            File file = new File(filePath);
            player = new Player(new FileInputStream(file));
            player.setGain(volume);

            // Start the playback in a new thread
            Thread musPlay = new Thread(() -> {
                callback.onPlaybackStarted(filePath);
                try {
                    player.play();
                    // Continuously update the position while the sound is playing
                    while (!player.isComplete()) {
                        callback.onPlaybackPosition(filePath, player.getPosition());
                        Thread.sleep(UPDATE_RATE); // Update position every second
                    }
                    callback.onPlaybackCompleted(filePath);
                } catch (JavaLayerException | InterruptedException ex) {
                    Logger.getLogger(PlaySound.class.getName()).log(Level.SEVERE, "Error playing file: " + filePath, ex);
                    callback.onPlaybackError(filePath, ex.getMessage());
                } finally {
                    // Ensure completion check is always called
                    checkAndNotifyCompletion();
                }
            });

            // Start the playback thread and increment active sounds count
            startThread(musPlay);
        } catch (FileNotFoundException | JavaLayerException e) {
            Logger.getLogger(PlaySound.class.getName()).log(Level.SEVERE, "File not found or error initializing player: " + filePath, e);
            callback.onPlaybackError(filePath, e.getMessage());
        }
    }

    public void playInternalSound(String filename, float volume, PlaybackCallback callback) {
        this.callback = callback;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            if (is == null) {
                String errorMsg = "Internal sound file not found: " + filename;
                Logger.getLogger(PlaySound.class.getName()).log(Level.SEVERE, errorMsg);
                callback.onPlaybackError(filename, errorMsg);
                return;
            }
            player = new Player(is);
            player.setGain(volume);
        } catch (JavaLayerException e) {
            sendErr("Problem playing file " + filename);
        }

        threads[num] = new Thread(() -> {
            callback.onPlaybackStarted(filename);
            try {
                player.play();
                while (!player.isComplete()) {
                    callback.onPlaybackPosition(filename, player.getPosition());
                    Thread.sleep(UPDATE_RATE); // Update position every second
                }
                callback.onPlaybackCompleted(filename);
            } catch (JavaLayerException | InterruptedException e) {
                Logger.getLogger(PlaySound.class.getName()).log(Level.SEVERE, "Problem playing sound " + filename, e);
                callback.onPlaybackError(filename, e.getMessage());
            }
            checkAndNotifyCompletion();
        });
        startThread(threads[num]);
    }

    private void startThread(Thread thread) {
        synchronized (threads) {
            threads[num] = thread;
            num = (num + 1) % 4;
        }
        activeSounds.incrementAndGet();
        thread.start();
    }

    private void checkAndNotifyCompletion() {
        if (activeSounds.decrementAndGet() == 0) {
            callback.onAllPlaybackCompleted();
        }
    }
}