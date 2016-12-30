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
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the Audioplayer. It contains the list of tracks.
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
    public void queue(AudioTrack track)
    {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (_player.getPlayingTrack() == null) {
            _player.playTrack(track);
            _tracks.add(track);
            _currentlyPlayingTrack = track;
        }
        else
        {
            _tracks.add(track);
        }
    }

    /**
     * Gets the next track in the list unless it is the last track then this gives the first
     * If Shuffleing is enabled a random track will be selected form the track list
     *
     * @param currentTrack The track that is currently playing so we can indentify the one following it in the list
     * @return The next track or if the last one was last in the list the first list element
     */
    public AudioTrack getNextTrack(AudioTrack currentTrack)
    {
        if (_shuffle)
        {
            Random random = new Random();
            return _tracks.get(random.nextInt(_tracks.size() - 1 ));
        }
        else
        {
            return _tracks.get(_tracks.indexOf(_currentlyPlayingTrack) + 1 );
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext)
        {
            _player.startTrack(getNextTrack(track), false);
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
    public ArrayList<AudioTrack> getTrackList()
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
        } else
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
    public Message getPlaylist()
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
    public void setShuffle(boolean enabled)
    {
        _shuffle = enabled;
    }

    public void restartSong()
    {
        replaceTrack(_player.getPlayingTrack());
        _player.startTrack(_currentlyPlayingTrack, false);
    }

    public void resetPlayer()
    {
        _tracks = new ArrayList<>();
        _currentlyPlayingTrack = null;
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
    public void pausePlayer()
    {
        _player.setPaused(true);
    }

    /**
     * Stops the playback of the current track.
     * After calling this method the _player will start at the first song from the playlist
     */
    public void stopPlayer()
    {
        _player.stopTrack();
        _currentlyPlayingTrack = null;
    }

    /**
     * Resumes the playler from pause otherwise just starts it with the next song in the list
     */
    public void resumePlayer()
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

    public void skip()
    {
        _player.startTrack(getNextTrack(_currentlyPlayingTrack), false);
        replaceTrack(_currentlyPlayingTrack);
        _currentlyPlayingTrack = _player.getPlayingTrack();
    }

    /**
     * Starts a track from the playlist at a certain position
     *
     * @param trackNumber the specified position of the track
     */
    public void startSpecificTrack(int trackNumber)
    {
        _player.startTrack(getSpecificTrack(trackNumber), false);
        replaceTrack(_currentlyPlayingTrack);
        _currentlyPlayingTrack = _player.getPlayingTrack();
    }

    /**
     * Sets the Volume of the player (Interval from 0 to 150)
     * @param volume The desired Volume
     */
    public void setVolume(int volume)
    {
        if (volume > 0 && volume <= 150)
        {
            _player.setVolume(volume);
        }
    }

    public Message songInfo()
    {
        MessageBuilder builder = new MessageBuilder();
        int position = _tracks.indexOf(_currentlyPlayingTrack);

        builder.append("Position: " + position + "*** " + _currentlyPlayingTrack.getInfo().title + "***"
                + "˜\n" +_currentlyPlayingTrack.getPosition()
                + " / " + _currentlyPlayingTrack.getDuration() + " minutes" +  "\n");

        return builder.build();
    }

     /**
     * Registers a track from many sources including youtube, soundcloud and vimeo
     * @param src The URL to the track that should be played
     * @param event The MessageReceivedEvent that belongs to the message
     */
     public void registerNewTrack(String src, AudioPlayerManager manager, MessageReceivedEvent event)
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
                         if (event.getAuthor().)
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
