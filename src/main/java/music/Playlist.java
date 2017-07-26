package music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A Playlist that holds all the information about the songs contained in it.
 * This Playlist Object can be restored from a database.
 * Created by Timbo on 01.07.17.
 *
 * @version 07.2017
 */
public class Playlist
{
    private List<AudioTrack> _normalTracks;

    private List<AudioTrack> _shuffeldTracks;

    private List<AudioTrack> _activePlaylist;

    private Map<String, String> _trackSources;

    private AudioTrack _playingSong;

    private boolean _isShuffled;

    public Playlist(List<AudioTrack> tracks, boolean shuffle)
    {
        _trackSources = new HashMap<>();
        _normalTracks = tracks;
        _shuffeldTracks = tracks;
        _activePlaylist = _normalTracks;
        for (AudioTrack track  : tracks)
        {
            registerTrackSource(track);
        }
        if (shuffle)
        {
            setShuffle(shuffle);
        }
        else
        {
            _isShuffled = false;
        }
    }

    /**
     * Adds a song to the playlist
     * @param track The track that is being added to the playlist
     */
    public void addSong(AudioTrack track)
    {
        _activePlaylist.add(track);
        registerTrackSource(track);
    }

    /**
     * Removes a track form the playlist
     * @param index The track that is being removed
     */
    public void removeTrackAt(int index)
    {
        AudioTrack track = _activePlaylist.get(index);
        _activePlaylist.remove(index);
        _activePlaylist.remove(track.getInfo().title);
    }

    public AudioTrack getNext()
    {
        if (_playingSong == null)
        {
            replacePlayingSong();
            _playingSong = _activePlaylist.get(0);
            return _playingSong;
        }

        replacePlayingSong();
        _playingSong = _activePlaylist.get( _activePlaylist.indexOf(_playingSong) + 1);
        return _playingSong;

    }

    /**
     * Sets the shuffle to the value of #{link: shuffle}
     * @param shuffle true if shuffle should be enabled, false otherwise
     */
    public void setShuffle(boolean shuffle)
    {
        if (shuffle)
        {
            _activePlaylist = _shuffeldTracks;
            Collections.shuffle(_shuffeldTracks);
        }
        else
        {
            _activePlaylist = _normalTracks;
        }
    }

    /**
     * Restarts the playing track
     * @return The same track just set to the beginning
     */
    public AudioTrack restartTrack()
    {
        replacePlayingSong();
        return _playingSong;
    }

    /**
     * Gives a specific track of the playlist
     * @param index The track that is to be retrieved
     * @return An AudioTrack from the playlist that was requested
     * @throws IndexOutOfBoundsException When the index specified is not within the range of the playlist
     */
    public AudioTrack getSpecificTrack(int index) throws IndexOutOfBoundsException
    {
        replacePlayingSong();
        if (index <= _activePlaylist.size())
        {
            _playingSong = _activePlaylist.get(index);
            return _playingSong;
        }
        else
        {
            throw new IndexOutOfBoundsException("The Track you want to play, is not in the index range of the Playlist!\nDon't try to break me!");
        }
    }

    public AudioTrack skipTrack()
    {
        replacePlayingSong();
        if (_activePlaylist.indexOf(_playingSong) < _activePlaylist.size())
        {
            _playingSong = _activePlaylist.get(_activePlaylist.indexOf(_playingSong) + 1);
            return _playingSong;
        }
        else
        {
            _playingSong = _activePlaylist.get(0);
            return _playingSong;
        }
    }

    public List<Message> getPlaylistInfo()
    {
        ArrayList<Message> messages = new ArrayList<>();
        MessageBuilder builder = new MessageBuilder();
        for (int i = 0; i < _activePlaylist.size();)
        {
            for (int e = 0; e <= 24; ++i, ++e)
            {
                if (_activePlaylist.size() > i)
                {
                    builder.append("```");
                    builder.append(i);
                    builder.append(" " + _activePlaylist.get(i).getInfo().title);
                    builder.append("```");
                }
                else
                {
                    break;
                }
            }

            if (!builder.isEmpty())
            {
                messages.add(builder.build());
                builder = new MessageBuilder();
            }
        }
        return messages;
    }

    public Message getSongInfo()
    {
        MessageBuilder builder = new MessageBuilder();

        builder.append("Position: " + _activePlaylist.indexOf(_playingSong) + "*** " + _playingSong.getInfo().title + "***"
                + "˜\n" + TimeUnit.MILLISECONDS.toMinutes(_playingSong.getPosition())
                + " / " + TimeUnit.MILLISECONDS.toMinutes(_playingSong.getDuration()) + " minutes" +  "\n");

        return builder.build();
    }


    public AudioTrack getPlayingSong()
    {
        return _playingSong;
    }

    public void setPlayingTrack(AudioTrack track)
    {
        _playingSong = track;
    }

    public List<AudioTrack> getTrackList()
    {
        return _activePlaylist;
    }

    /**
     * Replaces the active song so it can be played again
     */
    private void replacePlayingSong()
    {
        int index = _activePlaylist.indexOf(_playingSong);

        _activePlaylist.add(index, _playingSong.makeClone());

        _activePlaylist.remove(index);
    }

    /**
     * Puts the track into the Map so that the playlist can be restored
     * @param track The track whom's sources are being recorded
     */
    private void registerTrackSource(AudioTrack track)
    {
        _trackSources.put(track.getInfo().title, track.getInfo().uri);
    }
}