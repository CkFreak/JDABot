package music.managers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import music.AudioPlayerSendHandler;
import music.TrackScheduler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.List;

/**
 * Holder for both the _player and a track _scheduler for one guild.
 */
public class GuildMusicManager
{
  /**
   * Audio _player for the guild.
   */
  private final AudioPlayer _player;
  /**
   * Track _scheduler for the _player.
   */
  private final TrackScheduler _scheduler;
    /**
     * The AudioManager of this instance
     */
    private final AudioManager _audioManager;

  /**
   * Creates a _player and a track _scheduler.
   * @param manager Audio _player manager to use for creating the _player.
   */
  public GuildMusicManager(AudioPlayerManager manager, AudioManager audioManager)
  {
    _player = manager.createPlayer();
    _scheduler = new TrackScheduler(_player);
    _player.addListener(_scheduler);
    _audioManager = audioManager;
    _audioManager.setSendingHandler(getSendHandler());
  }

  /**
   * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
   */
  public AudioPlayerSendHandler getSendHandler() {
    return new AudioPlayerSendHandler(_player);
  }

    /**
     * @return The track scheduler of this Guild Music Manager
     */
  public TrackScheduler getScheduler()
  {
      return _scheduler;
  }


    /**
     * Connects the bot to an AudioChannel
     * @param channel The channel the bots needs to connect to
     * @param guild The guild the @channel belongs to
     */
  public void connectToAudioChannel(String channel, Guild guild, Member member)
  {
      if (member != null)
      {
          if (member.getVoiceState().getChannel() != null)
          {
              _audioManager.openAudioConnection(member.getVoiceState().getChannel());
          }
      }
      else
      {
          if (channel != null && guild != null)
          {
              List<VoiceChannel> voiceChannels = guild.getVoiceChannels();
              for (VoiceChannel voiceChannel : voiceChannels)
              {
                  if (voiceChannel.getName().equalsIgnoreCase(channel))
                  {
                      _audioManager.openAudioConnection(voiceChannel);
                  }
              }
          }
      }
  }

  public void leaveVoiceChannel()
  {
      _audioManager.closeAudioConnection();
  }

}
