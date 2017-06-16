package handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class schedules tracks for the Audioplayer. It contains the list of tracks.
 * @version 06.2017
 */
public class TrackScheduler extends AudioEventAdapter
{
    private final AudioPlayer _player;
    private ArrayList<AudioTrack> _tracks;
    private boolean _shuffle;
    private AudioTrack _currentlyPlayingTrack;

    /**
     * @param player The audio _player this _scheduler uses
     */
    public TrackScheduler(AudioPlayer player)
    {
        _shuffle = false;
        _player = player;
        _tracks = new ArrayList<>();
        _currentlyPlayingTrack = null;
    }

    /**
     * Add the next track to _tracks or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to _tracks.
     */
    private void queue(AudioTrack track)
    {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!_tracks.contains(track))
        {
            if (_player.getPlayingTrack() == null)
            {
                _player.playTrack(track);
                _tracks.add(track);
                _currentlyPlayingTrack = track;
            }
            else
            {
                _tracks.add(track);
            }
        }
    }

    /**
     * Gets the next track in the list unless it is the last track then this gives the first
     * If Shuffleing is enabled a random track will be selected form the track list
     *
     * @return The next track or if the last one was last in the list the first list element
     */
    private AudioTrack getNextTrack()
    {
        if (_shuffle)
        {
            Random random = new Random();
            replaceTrack(_currentlyPlayingTrack);
            int i = random.nextInt(_tracks.size() - 1);
            _currentlyPlayingTrack = _tracks.get(i);
            return _tracks.get(i);
        }
        else
        {
            int i = _tracks.indexOf(_currentlyPlayingTrack) + 1;
            replaceTrack(_currentlyPlayingTrack);
            _currentlyPlayingTrack = _tracks.get(i);
            return _tracks.get(i);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext)
        {
            _player.startTrack(getNextTrack(), false);
            _currentlyPlayingTrack = _player.getPlayingTrack();
        }
    }

    /**
     * Gets a specified AudioTrack as long as it exists in the TrackList of this class
     *
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
     * Gets the first track from the track list
     *
     * @return The first element of the list of tracks
     */
    public AudioTrack getFirstTrack()
    {
        return _tracks.get(0);
    }

    /**
     * Returns the track list in this instance of the class
     *
     * @return the track list
     */
    private ArrayList<AudioTrack> getTrackList()
    {
        return _tracks;
    }

    /**
     * Gets the track from the list at a certain specified location
     *
     * @param trackNumber The number of the track that is being looked for
     * @return The track at the index of the trackNumber
     * @throws IllegalArgumentException When the track number exceeds the size of the track list
     */
    private AudioTrack getSpecificTrack(int trackNumber)
            throws IllegalArgumentException
    {
        if (trackNumber <= _tracks.size() - 1)
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
     * Gives the current Playlist of the Bot
     *
     * @return The current playlist
     */
    //TODO sign limit beachten
     Message getPlaylist()
    {
        MessageBuilder builder = new MessageBuilder();
        ArrayList<AudioTrack> trackList = getTrackList();
        int counter = 0;
        if (getTrackList()
                .isEmpty())
        {
            return builder.append("Currently the playlist is empty").build();
        } else
        {

            for (AudioTrack audioTrack : trackList)
            {
                builder.append(
                        "```" + counter + " " + audioTrack.getInfo().title + "```");
                ++counter;
            }

            return builder.build();
        }
    }

    /**
     * Sets an internal boolean and enables shuffeling or disables it again
     *
     * @param enabled true to enable false to disable
     */
    void setShuffle(boolean enabled)
    {
        _shuffle = enabled;
    }

    void restartSong()
    {
        replaceTrack(_currentlyPlayingTrack);
        _player.startTrack(_currentlyPlayingTrack, false);
    }

    void resetPlayer()
    {
        _tracks = new ArrayList<>();
        _currentlyPlayingTrack = null;
        _player.destroy();
    }

    public boolean isPaused()
    {
        return _player.isPaused();
    }

    /**
     * Sets the playing track
     */
    private void setCurrentTrack(AudioTrack track)
    {
        _currentlyPlayingTrack = track;
    }

    /**
     * Pauses the _player
     */
    void pausePlayer()
    {
        _player.setPaused(true);
    }

    /**
     * Stops the playback of the current track.
     * After calling this method the _player will start at the first song from the playlist
     */
    void stopPlayer()
    {
        _player.stopTrack();
        _currentlyPlayingTrack = getFirstTrack();
    }

    /**
     * Resumes the playler from pause otherwise just starts it with the next song in the list
     */
    void resumePlayer()
    {
        if (_player.isPaused())
        {
            _player.setPaused(false);
        }
        else
        {
            _player.playTrack(_currentlyPlayingTrack);
        }
    }

    void skip()
    {
        _player.startTrack(getNextTrack(), false);
        replaceTrack(_currentlyPlayingTrack);
        _currentlyPlayingTrack = _player.getPlayingTrack();
    }

    /**
     * Starts a track from the playlist at a certain position
     *
     * @param trackNumber the specified position of the track
     */
    void startSpecificTrack(int trackNumber)
    {
        _player.startTrack(getSpecificTrack(trackNumber), false);
        replaceTrack(_currentlyPlayingTrack);
        _currentlyPlayingTrack = _player.getPlayingTrack();
    }

    /**
     * Sets the Volume of the player (Interval from 0 to 150)
     * @param volume The desired Volume
     */
    void setVolume(int volume)
    {
        if (volume > 0 && volume <= 150)
        {
            _player.setVolume(volume);
        }
    }

    Message songInfo()
    {
        MessageBuilder builder = new MessageBuilder();
        int position = _tracks.indexOf(_currentlyPlayingTrack);

        builder.append("Position: " + position + "*** " + _currentlyPlayingTrack.getInfo().title + "***"
                + "˜\n" +_currentlyPlayingTrack.getPosition()
                + " / " + _currentlyPlayingTrack.getDuration() / 360000 + " minutes" +  "\n");

        return builder.build();
    }

     /**
     * Registers a track from many sources including youtube, soundcloud and vimeo
     * @param src The URL to the track that should be played
     * @param event The MessageReceivedEvent that belongs to the message
     */
     void registerNewTrack(String src, AudioPlayerManager manager, MessageReceivedEvent event)
     {
        event.getChannel().

             sendMessage("Starting to process the URL please wait.").

             queue();
        manager.loadItem(src,new

             AudioLoadResultHandler()
             {

                 @Override
                 public void trackLoaded (AudioTrack track)
                 {
                     event.getChannel().sendMessage("Track was loaded").queue();
                     if (_player.isPaused())
                     {
                         event.getChannel().sendMessage("And the player has started to play").queue();
                     }
                     queue(track);
                 }

                 @Override
                 public void playlistLoaded (AudioPlaylist playlist)
                 {
                     for (AudioTrack track : playlist.getTracks())
                     {
                         queue(track);
                     }
                 }

                 @Override
                 public void noMatches ()
                 {
                     event.getChannel()
                             .sendMessage("I am sorry but I could not find anything.")
                             .queue();
                 }

                 @Override
                 public void loadFailed (FriendlyException exception)
                 {
                     event.getChannel()
                             .sendMessage(
                                     "Everything just blew up! Go find a bunker! NOW!")
                             .queue();

                 }
             });
     }

    /**
     * Replaces the playing cloneTrack with a copy of it, so that it can be played again to a later point in time
     * @param track The track that needs to be replaced
     */
    private void replaceTrack(AudioTrack track)
    {
        int i = _tracks.indexOf(track);
        _tracks.set(i, track.makeClone());
        _currentlyPlayingTrack = _tracks.get(i);
    }
}
