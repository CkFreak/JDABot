package music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Playlist that holds all the information about the songs contained in it.
 * This Playlist Object can be restored from a database.
 * Created by Timbo on 01.07.17.
 */
public class Playlist
{
    private List<AudioTrack> _tracks;

    private Map<String, String> _trackSources;

    private AudioTrack _playingSong;

    private boolean _isShuffled;

    public Playlist(List<AudioTrack> tracks, String src, boolean shuffle)
    {
        _trackSources = new HashMap<>();
        _tracks = tracks;
        getTrackSources(src);
        _isShuffled = shuffle;
    }

    private void getTrackSources(String src)
    {
        for (AudioTrack track  : _tracks)
        {
            _trackSources.put(track.getInfo().title, src);
        }
    }
}
