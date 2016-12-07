package managers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import handler.AudioPlayerSendHandler;
import handler.TrackScheduler;

/**
 * Holder for both the _player and a track _scheduler for one guild.
 */
public class GuildMusicManager {
  /**
   * Audio _player for the guild.
   */
  private final AudioPlayer _player;
  /**
   * Track _scheduler for the _player.
   */
  private final TrackScheduler _scheduler;

  /**
   * Creates a _player and a track _scheduler.
   * @param manager Audio _player manager to use for creating the _player.
   */
  public GuildMusicManager(AudioPlayerManager manager) {
    _player = manager.createPlayer();
    _scheduler = new TrackScheduler(_player);
    _player.addListener(_scheduler);
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
}
