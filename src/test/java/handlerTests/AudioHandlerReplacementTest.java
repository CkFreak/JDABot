package handlerTests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;

import handler.AudioHandlerReplacement;
import listener.AudioEventTrackListener;

public class AudioHandlerReplacementTest
{
    
    private AudioHandlerReplacement _audioHandler;
    
    private AudioEventTrackListener _audioEventListener;
    
    private AudioTrack _testTrack;
    
    
    
    public AudioHandlerReplacementTest()
    {
        _audioHandler = new AudioHandlerReplacement();
        _audioHandler.registerNewTrack("https://www.youtube.com/watch?v=EiO9_PJ0h8Q", null);
        
        _audioEventListener = new AudioEventTrackListener(_audioHandler);
        
        _testTrack = new AudioTrack()
        {
            
            @Override
            public void stop()
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void setPosition(long position)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void setMarker(TrackMarker marker)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public AudioTrack makeClone()
            {
                return this;
            }
            
            @Override
            public boolean isSeekable()
            {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public AudioTrackState getState()
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public AudioSourceManager getSourceManager()
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public long getPosition()
            {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public AudioTrackInfo getInfo()
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getIdentifier()
            {
                
                return "Test Track";
            }
            
            @Override
            public long getDuration()
            {
                // TODO Auto-generated method stub
                return 0;
            }
        };
    }


    /**
     * Test, wheater or not the player really starts playing a track, when a track is present
     */
    @Test
    public void testPlaying()
    {
        _audioHandler.startPlaying();
        assertTrue(!_audioHandler.isPaused());
    }
    
    
    @Test
    public void testeRegisterNewSong()
    {
        _audioEventListener.add(_testTrack);
        assertTrue((_audioEventListener.getTrackList().get(0).equals(_testTrack)));
    }

}
