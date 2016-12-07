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
            musicManager = new GuildMusicManager(_playerManager);
            AudioManager audioManager = new AudioManagerImpl(event.getGuild());
            audioManager.setSendingHandler(musicManager.getSendHandler());
            _musicManagers.put(guildId, musicManager);
        }

        return musicManager;
    }

    /**
     * Connects the Bot to a VoiceChannel
     * @param channel The channel the bot want to connect to
     * @param guild The guild the VoiceChannel belongs to
     */
    public void connectToVoiceChannel(String channel, Guild guild)
    {
      List<VoiceChannel> voiceChannels = guild.getVoiceChannels();
      for (VoiceChannel voiceChannel : voiceChannels)
      {
          if (voiceChannel.getName().equalsIgnoreCase(channel))
          {
              AudioManager audioManager = new AudioManagerImpl(guild);
              audioManager.openAudioConnection(voiceChannel);
          }
      }

  }

    /**
     * Disconnects the bot from a VoiceChannel if it was connected. Otherwise nothing happens
     * @param guild The Guild the VoiceChannel belongs to
     */
  public void leaveVoiceChannel(Guild guild)
  {
      AudioManager audioManager = new AudioManagerImpl(guild);
      if (audioManager.isConnected())
      {
          audioManager.closeAudioConnection();
      }
  }

    public AudioPlayerManager getPlayerManager()
    {
        return _playerManager;
    }
}
