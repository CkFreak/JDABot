package music;

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
    private Playlist playlist;

    /**
     * @param player The audio _player this _scheduler uses
     */
    public TrackScheduler(AudioPlayer player)
    {
        _player = player;
        playlist = new Playlist(new ArrayList<AudioTrack>(), false);
    }

    /**
     * Add the next track to playlist or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to playlist.
     */
    private void queue(AudioTrack track)
    {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (playlist.getPlayingSong() == null)
        {
            _player.playTrack(track);
            playlist.addSong(track);
            playlist.setPlayingTrack(track);
        }
        else
        {
            playlist.addSong(track);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext)
        {
            _player.startTrack(playlist.getNext(), false);
        }
    }

    /**
     * Gives the current Playlist of the Bot
     *
     * @return The current playlist
     */
    public ArrayList<Message> getPlaylist()
    {
        return (ArrayList<Message>) playlist.getPlaylistInfo();
    }

    /**
     * Sets an internal boolean and enables shuffeling or disables it again
     *
     * @param enabled true to enable false to disable
     */
    public void setShuffle(boolean enabled)
    {
        playlist.setShuffle(enabled);
    }

    public void restartSong()
    {

        _player.startTrack(playlist.restartTrack(), false);
    }

    public void resetPlayer()
    {
        playlist = new Playlist(new ArrayList<AudioTrack>(), false);
        _player.destroy();
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
        _player.playTrack(playlist.getSpecificTrack(0));
        _player.setPaused(true);
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
            _player.playTrack(playlist.getPlayingSong());
        }
    }

    public void skip()
    {
        _player.startTrack(playlist.skipTrack(), false);
    }

    /**
     * Starts a track from the playlist at a certain position
     *
     * @param trackNumber the specified position of the track
     */
    public void startSpecificTrack(int trackNumber)
    {
        _player.startTrack(playlist.getSpecificTrack(trackNumber), false);
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

    public Message songInfo()
    {
        return playlist.getSongInfo();
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

                        event.getChannel().sendMessage("Playlist was loaded");
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
}