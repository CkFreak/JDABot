package managers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.managers.impl.AudioManagerImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers.*;
import static com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers.registerLocalSource;

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

        registerRemoteSources(_playerManager);
        registerLocalSource(_playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild, MessageReceivedEvent event)
    {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = _musicManagers.get(guildId);

        if (musicManager == null)
        {
            AudioManager audioManager = new AudioManagerImpl(event.getGuild());
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
