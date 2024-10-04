package org.foxesworld.foxesSound;

public interface PlaybackCallback {
    void onPlaybackStarted(String filename);
    void onPlaybackCompleted(String filename);
    void onPlaybackError(String filename, String errorMessage);
    void onPlaybackPosition(String filename, int position);
    void onAllPlaybackCompleted();
}
