package music.managers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timbo on 07/12/2016.
 *
 * This class controlls music playback
 */
public class MusicControlManager
{
    private final AudioPlayerManager _playerManager;
    private final Map<Long, GuildMusicManager> _musicManagers;

    public MusicControlManager()
    {
        _playerManager = new DefaultAudioPlayerManager();
        _musicManagers = new HashMap<>();

        AudioSourceManagers.registerRemoteSources(_playerManager);
        AudioSourceManagers.registerLocalSource(_playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild, MessageReceivedEvent event)
    {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = _musicManagers.get(guildId);

        if (musicManager == null)
        {
            AudioManager audioManager = event.getGuild().getAudioManager();
            musicManager = new GuildMusicManager(_playerManager, audioManager);
            //audioManager.setSendingHandler(musicManager.getSendHandler());
            _musicManagers.put(guildId, musicManager);
        }

        return musicManager;
    }

    public AudioPlayerManager getPlayerManager()
    {
        return _playerManager;
    }
}
