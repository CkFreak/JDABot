package handler;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration.ResamplingQuality;

import java.util.ArrayList;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import listener.AudioEventTrackListener;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Dieser AudioHandler ist in der Lage Musik von diversen Quellen zu spielen. Musik kann pausiert und gestoppt werden.
 * Auch wird eine Playlist angelegt, durch die gewechselt werden kann.
 * @author Timbo
 * @version 23.11.2016 
 */
public class AudioHandlerReplacement
{
    private AudioPlayerManager _audioManager;
    private AudioPlayer _player;
    private AudioEventTrackListener _musicEventListener;
    private AudioTrack _currentlyPlayingTrack;

    /**
     * Initialisiert einen AudioHandler, mit einem Neuen AudioManager und seinen RessourceManagern und Playern
     */
    public AudioHandlerReplacement()
    {
        //Initialize the currently playing Track to null as there is non playing
        _currentlyPlayingTrack = null;

        //Initialisieren des Audimanagers
        _audioManager = new DefaultAudioPlayerManager();

        //Den Audiomanaher konfigurieren und ihm SourceManager zuweisen
        _audioManager.getConfiguration()
            .setResamplingQuality(ResamplingQuality.LOW);
        _audioManager.registerSourceManager(new YoutubeAudioSourceManager());
        _audioManager.registerSourceManager(new VimeoAudioSourceManager());
        _audioManager.registerSourceManager(new SoundCloudAudioSourceManager());
        _audioManager.registerSourceManager(new LocalAudioSourceManager());
        _audioManager
            .registerSourceManager(new TwitchStreamAudioSourceManager());
        _audioManager.registerSourceManager(new HttpAudioSourceManager());

        //Initialisieren der Player
        _player = _audioManager.createPlayer();

        //Listener auf die Player registrieren
        _musicEventListener = new AudioEventTrackListener(this);
        _player.addListener(_musicEventListener);

    }

    /**
     * Registers a track from many sources including youtube, soundcloud and vimeo  
     * @param src The URL to the track that should be played
     * @param event The MessageReceivedEvent that belongs to the message
     */
    public void registerNewTrack(String src, MessageReceivedEvent event)
    {
        _audioManager.loadItem(src, new AudioLoadResultHandler()
        {

            @Override
            public void trackLoaded(AudioTrack track)
            {
                _musicEventListener.add(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                for (AudioTrack track : playlist.getTracks())
                {
                    _musicEventListener.add(track);
                }
            }

            @Override
            public void noMatches()
            {
                event.getChannel()
                    .sendMessage("Leider habe ich nichts gefunden.")
                    .queue();
                ;
            }

            @Override
            public void loadFailed(FriendlyException exception)
            {
                event.getChannel()
                    .sendMessage(
                            "Es ist irgendwie alles Explodiert beim Laden der Datei. "
                                    + "Es wird Zeit einen Schutzbunker aufzusuchen!")
                    .queue();
                ;
            }
        });
    }

    /**
     * Starts the player
     */
    public void startPlaying()
    {
        _player.playTrack(_musicEventListener.getFirstTrack());
        _currentlyPlayingTrack = _musicEventListener.getFirstTrack();
    }

    /**
     * Starts a track from the playlist at a certain position
     * @param trackNumber the specified position of the track
     */
    public void startSpecificTrack(int trackNumber)
    {
        _player.playTrack(_musicEventListener.getSpecificTrack(trackNumber));
        _currentlyPlayingTrack = _musicEventListener
            .getSpecificTrack(trackNumber);
    }

    /**
     * Pauses the player
     */
    public void pausePlayer()
    {
        _player.setPaused(true);
    }

    /**
     * Stops the playback of the current track. 
     * After calling this method the player will start at the first song from the playlist
     */
    public void stopPlayer()
    {
        _player.stopTrack();
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
            _player.playTrack(
                    _musicEventListener.getNextTrack(_currentlyPlayingTrack));
        }
    }

    /**
     * Sets the playing track
     */
    public void setCurrentTrack(AudioTrack track)
    {
        _currentlyPlayingTrack = track;
    }

    /**
     * Gives the current Playlist of the Bot
     * @return The current playlist
     */
    public Message getPlaylist()
    {
        MessageBuilder builder = new MessageBuilder();
        ArrayList<AudioTrack> trackList = _musicEventListener.getTrackList();
        int counter = 0;
        if (_musicEventListener.getTrackList()
            .isEmpty())
        {
            return null;
        }
        else
        {

            for (AudioTrack audioTrack : trackList)
            {
                builder.appendString(
                        "```" + counter + "```` " + audioTrack.getIdentifier());
            }

            return builder.build();
        }
    }

    /**
     * Gets The currently playing song as String
     * @return The currently playing song as String
     */
    public String getCurrentlyPlayingSong()
    {
        return _currentlyPlayingTrack.getIdentifier();
    }

    /**
     * Enables or diables shuffeling
     * @param enable true to enable false to disable
     */
    public void isShuffle(boolean enable)
    {
        _musicEventListener.setShuffle(enable);
    }

    /**
     * Sets the volume for the player
     * @param volume The desired new Volume
     */
    public void setVolume(int volume)
    {
        _player.setVolume(volume);
    }

    /**
     * Plays the next song from the queue
     */
    public void playNextSong()
    {
        _player.playTrack(_musicEventListener.getNextTrack(_currentlyPlayingTrack));
    }
    
    public void restartSong()
    {
        _player.playTrack(_currentlyPlayingTrack);
    }
    
    public void resetPlayer()
    {
        _musicEventListener = new AudioEventTrackListener(this);
    }

    //TODO Join und Leave commands bauen sobald JDA 3.x Audio wieder unterstützt!!

}
