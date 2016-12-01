package listener;

import java.util.ArrayList;
import java.util.Random;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import handler.AudioHandler;

/**
 * A class that handles the audio playback and keeps track of the songs in the selection. It needs to be controlled 
 * by a AudioHandler class that can play the tracks
 * @author Timbo 
 * @version 24.11.2016
 */
public class AudioEventTrackListener extends AudioEventAdapter
        implements AudioEventListener
{

    private ArrayList<AudioTrack> _tracks;
    private AudioHandler _audioHandler;
    private boolean _shuffle;

    public AudioEventTrackListener(AudioHandler audioHandler)
    {
        _tracks = new ArrayList<>();
        _audioHandler = audioHandler;
        _shuffle = false;
    }

    @Override
    public void onEvent(AudioEvent event)
    {
        event.player.getPlayingTrack();
    }

    /**
     * Adds a track to the local playlist
     * @param track The track that is to be added to the playlist
     */
    public void add(AudioTrack track)
    {
        _tracks.add(track);
    }

    @Override
    public void onPlayerPause(AudioPlayer player)
    {
        player.setPaused(true);
    }

    @Override
    public void onPlayerResume(AudioPlayer player)
    {
        player.setPaused(false);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track)
    {
        setPlayingTrack(player);
    }
    
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
      // endReason == FINISHED: A track finished (or died by an exception) - just start the next one
      // endReason == STOPPED: The player was stopped - makes no sense to start the next track
      // endReason == REPLACED: Another track started playing while this had not finished, do nothing
      // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
      //                       clone of this back to your queue
        if (endReason.mayStartNext)
        {
            _audioHandler.playNextSong();
        }
    }

    /**
     * Gets a specified AudioTrack as long as it exists in the TrackList of this class
     * @param trackName The name of the track that is wished to be returned
     * @return The Track from the Playlist if it was found
     * @throws IllegalArgumentException When The Track has not been found
     */
    public AudioTrack getTrack(String trackName) throws IllegalArgumentException
    {
        for (AudioTrack audioTrack : _tracks)
        {
            if (audioTrack.getIdentifier()
                .equals(trackName))
            {
                return audioTrack;
            }
        }

        throw new IllegalArgumentException("The Track has not been found");
    }

    /**
     * Gets the next track in the list unless it is the last track then this gives the first 
     * If Shuffleing is enabled a random track will be selected form the track list
     * @param currentTrack The track that is currently playing so we can indentify the one following it in the list
     * @return The next track or if the last one was last in the list the first list element
     */
    public AudioTrack getNextTrack(AudioTrack currentTrack)
    {
        if (_shuffle)
        {
            Random random = new Random();
            return _tracks.get(random.nextInt(_tracks.size()));
        }
        else
        {
            int currentTrackIndex = _tracks.indexOf(currentTrack);

            if (currentTrackIndex == _tracks.size())
            {
                return _tracks.get(0);
            }

            return _tracks.get(currentTrackIndex++);
        }
    }

    /**
     * Gets the first track from the track list
     * @return The first element of the list of tracks
     */
    public AudioTrack getFirstTrack()
    {
        return _tracks.get(0);
    }

    /**
     * Returns the track list in this instance of the class
     * @return the track list
     */
    public ArrayList<AudioTrack> getTrackList()
    {
        return _tracks;
    }

    /**
     * Gets the track from the list at a certain specified location
     * @param trackNumber The number of the track that is being looked for
     * @return The track at the index of the trackNumber
     * @throws IllegalArgumentException When the track number exceeds the size of the track list
     */
    public AudioTrack getSpecificTrack(int trackNumber)
            throws IllegalArgumentException
    {
        if (trackNumber < _tracks.size() - 1)
        {
            return _tracks.get(trackNumber);
        }
        else
        {
            throw new IllegalArgumentException(
                    "The Index of the track is out of range of the Playlist");
        }
    }

    /**
     * Sets an internal boolean and enables shuffeling or disables it again
     * @param enabled true to enable false to disable
     */
    public void setShuffle(boolean enabled)
    {
        _shuffle = enabled;
    }

    /**
     * Sets the playing track on the AudioHandler instance
     */
    private void setPlayingTrack(AudioPlayer player)
    {
        _audioHandler.setCurrentTrack(player.getPlayingTrack());
    }

}
