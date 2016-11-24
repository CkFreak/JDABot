package handler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.AudioTimestamp;

/**
 * Diese Klasse handelt den gesamten Musik und Audio Trafic des Bots
 * @author Timbo
 *
 */
public class AudioHandler extends ListenerAdapter
{
    /**
     * Das standart Volumen für den Player
     */
    private static final float DEFAULT_VOLUME = 0.35f;

    /**
     * Eine Instanz des MusicPlayer der zur Ton wiedergabe verwendet wird
     */
    private MusicPlayer player;

    /**
     * Initialisiert einen AudioHandler mit MusicPlayer und allem was zur
     * Soundwiedergabe nötig ist
     * @param event Das Event, das den AudioManager hält
     */
    public AudioHandler(MessageReceivedEvent event)
    {
        AudioManager manager = event.getGuild()
            .getAudioManager();
        player = new MusicPlayer();
        if (manager.getSendingHandler() == null)
        {
            player = new MusicPlayer();
            player.setVolume(DEFAULT_VOLUME);
            manager.setSendingHandler(player);
        }
        else
        {
            player = (MusicPlayer) manager.getSendingHandler();
        }
    }

    /**
     * Spielt einen Song von Youtube ab
     * @param URL Die URL zum Song
     * @param event Das Event mit dem Channel
     */
    public void play(String URL, MessageReceivedEvent event)
    {
        String url = URL;
        Playlist playlist;
        try
        {
            playlist = Playlist.getPlaylist(url);
            List<AudioSource> sources = new LinkedList<AudioSource>(
                    playlist.getSources());
            //        AudioSource source = new RemoteSource(url);
            //        AudioSource source = new LocalSource(new File(url));
            //        AudioInfo info = source.getInfo();   //Preload the audio info.
            if (sources.size() > 1)
            {
                event.getChannel()
                    .sendMessage("Playlist mit **" + sources.size()
                            + "** Einträgen gefunden.\n"
                            + "Voranschreiten die Quellen einzuordnen, das kann ein wenig Dauern...");
                final MusicPlayer fPlayer = player;
                Thread thread = new Thread()
                {
                    @Override
                    public void run()
                    {
                        for (Iterator<AudioSource> it = sources.iterator(); it
                            .hasNext();)
                        {
                            AudioSource source = it.next();
                            AudioInfo info = source.getInfo();
                            List<AudioSource> queue = fPlayer.getAudioQueue();
                            if (info.getError() == null)
                            {
                                queue.add(source);
                                if (fPlayer.isStopped()) fPlayer.play();
                            }
                            else
                            {
                                event.getChannel()
                                    .sendMessage(
                                            "Error detected, skipping source. Error:\n"
                                                    + info.getError());
                                it.remove();
                            }
                        }
                        event.getChannel()
                            .sendMessage(
                                    "Angegeben Playlist erfolgreich aufgenommen. Erfolgreich **"
                                            + sources.size()
                                            + "** Quellen eingefügt");
                    }
                };
                thread.start();
            }
            else
            {
                AudioSource source = sources.get(0);
                AudioInfo info = source.getInfo();

                Thread download = new Thread()
                {
                    @Override
                    public void run()
                    {
                        String msg = "";
                        if (info.getError() == null)
                        {
                            player.getAudioQueue()
                                .add(source);
                            msg += "Die angegebene URL wurde in die Wiedergabeliste eingefügt";
                            if (player.isStopped())
                            {
                                player.play();
                                msg += " und der Player hat angefangen zu spielen";
                            }
                            event.getChannel()
                                .sendMessage(msg + ".");
                        }
                    }
                };
                download.start();

                if (info.getError() != null)
                {
                    event.getChannel()
                        .sendMessage(
                                "There was an error while loading the provided URL.\n"
                                        + "Error: " + info.getError());
                }
            }
        }
        catch (NullPointerException e)
        {
            event.getChannel()
                .sendMessage(
                        "Leider kam es zu einem Fehler, beim laden der Video Infos. Der Prozess wurde abgebrochen. "
                                + "Bitte versuche es erneut oder versuche ein neues Video.");
        }
    }

    //Current commands
    // join [name]  - Joins a voice channel that has the provided name
    // leave        - Leaves the voice channel that the bot is currently in.
    // play         - Plays songs from the current queue. Starts playing again if it was previously paused
    // play [url]   - Adds a new song to the queue and starts playing if it wasn't playing already
    // pause        - Pauses audio playback
    // stop         - Completely stops audio playback, skipping the current song.
    // skip         - Skips the current song, automatically starting the next
    // nowplaying   - Prints information about the currently playing song (title, current time)
    // list         - Lists the songs in the queue
    // volume [val] - Sets the volume of the MusicPlayer [0.0 - 1.0]
    // restart      - Restarts the current song or restarts the previous song if there is no current song playing.
    // repeat       - Makes the player repeat the currently playing song
    // reset        - Completely resets the player, fixing all errors and clearing the queue.

    /**
     * Setzt das Volumen des Players auf Werte zwischen 0.0 und 1.0
     * @param volume Das zu setzende Volumen
     * @param event Das Event mit dem Channel
     */
    public void setVolume(String volume, MessageReceivedEvent event)
    {

        float amplitude = Float.parseFloat(volume);
        amplitude = Math.min(1F, Math.max(0F, amplitude));
        player.setVolume(amplitude);
        event.getChannel()
            .sendMessage("Das Volumen wurde auf: " + volume + "gesezt");

    }

    /**
     * Gibt die Aktuelle Playliste wieder
     * @param event Das Event mit dem Channel
     */
    public void getCurrentPlaylist(MessageReceivedEvent event)
    {

        List<AudioSource> queue = player.getAudioQueue();
        if (queue.isEmpty())
        {
            event.getChannel()
                .sendMessage("Zur Zeit ist die Wiedergaebliste leer!");
            return;
        }

        MessageBuilder builder = new MessageBuilder();
        builder.appendString(
                "__Current Queue.  Entries: " + queue.size() + "__\n");
        for (int i = 0; i < queue.size() && i < 10; i++)
        {
            AudioInfo info = queue.get(i)
                .getInfo();
            //                builder.appendString("**(" + (i + 1) + ")** ");
            if (info == null)
                builder.appendString("*Leider keine Infos für diesen Song.*");
            else
            {
                AudioTimestamp duration = info.getDuration();
                builder.appendString("`[");
                if (duration == null)
                    builder.appendString("N/A");
                else
                    builder.appendString(duration.getTimestamp());
                builder.appendString("]` " + info.getTitle() + "\n");
            }
        }

        boolean error = false;
        int totalSeconds = 0;
        for (AudioSource source : queue)
        {
            AudioInfo info = source.getInfo();
            if (info == null || info.getDuration() == null)
            {
                error = true;
                continue;
            }
            totalSeconds += info.getDuration()
                .getTotalSeconds();
        }

        builder.appendString("\nAbsolute Abspielzeit: "
                + AudioTimestamp.fromSeconds(totalSeconds)
                    .getTimestamp());
        if (error) builder.appendString(
                "`Ein Fehler ist beim Zeit berechnen aufgetreten es stimmt möglicherweise nicht komplett.");
        event.getChannel()
            .sendMessage(builder.build());
    }

    /**
     * Gibt den Song der grade spielt wieder
     * @param event Das Event mit dem Channel
     */
    public void getNowPlaying(MessageReceivedEvent event)
    {
        if (player.isPlaying())
        {
            AudioTimestamp currentTime = player.getCurrentTimestamp();
            AudioInfo info = player.getCurrentAudioSource()
                .getInfo();
            if (info.getError() == null)
            {
                event.getChannel()
                    .sendMessage("**Playing:** " + info.getTitle() + "\n"
                            + "**Zeit:**    [" + currentTime.getTimestamp()
                            + " / " + info.getDuration()
                                .getTimestamp()
                            + "]");
            }
            else
            {
                event.getChannel()
                    .sendMessage("**Playing:** Info Error. Known source: "
                            + player.getCurrentAudioSource()
                                .getSource()
                            + "\n" + "**Time:**    ["
                            + currentTime.getTimestamp() + " / (N/A)]");
            }
        }
        else
        {
            event.getChannel()
                .sendMessage("Zur Zeit spielt rein garnichts!");
        }
    }

    /**
     * Lässt den Bot in einen Channel joinen
     * @param chnl Der zu joinende Channel
     * @param event Das Event mit dem TextChannel
     */
    public void joinChannel(String chnl, MessageReceivedEvent event)
    {
        AudioManager manager = event.getGuild()
            .getManager();
        //Separates the name of the channel so that we can search for it
        String chanName = chnl;

        //Scans through the VoiceChannels in this Guild, looking for one with a case-insensitive matching name.
        VoiceChannel channel = event.getGuild()
            .getVoiceChannels()
            .stream()
            .filter(vChan -> vChan.getName()
                .equalsIgnoreCase(chanName))
            .findFirst()
            .orElse(null); //If there isn't a matching name, return null.
        if (channel == null)
        {
            event.getChannel()
                .sendMessage("Es Gibt leider keinen Channel mit dem Namen: '"
                        + chanName + "'");
            return;
        }
        manager.openAudioConnection(channel);

    }

    /**
     * Lässt den Bot den aktuellen Channel verlassen
     * @param event Das Event mit dem TextChannel
     */
    public void leaveChannel(MessageReceivedEvent event)
    {
        event.getMessage().deleteMessage();
        
        AudioManager manager = event.getGuild()
            .getManager();
        manager.closeAudioConnection();
    }

    /**
     * Überspringt den aktuellen Song
     * @param event Das Event mit dem TextChannel
     */
    public void skipSong(MessageReceivedEvent event)
    {
        player.skipToNext();
        event.getChannel()
            .sendMessage("Song übersprungen.");
    }

    /**
     * Wiederholt den aktuellen Song
     * @param event Das Event mit dem TextChannel
     */
    public void repeatSong(MessageReceivedEvent event)
    {
        if (player.isRepeat())
        {
            player.setRepeat(false);
            event.getChannel()
                .sendMessage("Der Player wir **nicht** mehr wiederholen.");
        }
        else
        {
            player.setRepeat(true);
            event.getChannel()
                .sendMessage("Der Player wird wiederholen.");
        }
    }

    /**
     * Pausiert den spielenden Song
     * @param event Das Event mit den TextChannel
     */
    public void pauseSong(MessageReceivedEvent event)
    {
        player.pause();
        event.getChannel()
            .sendMessage("Rückgabe pausiert.");
    }

    /**
     * Stopt die Wiedergabe komplett
     * @param event Das Event mit den TextChannel
     */
    public void stopPlayback(MessageReceivedEvent event)
    {
        player.stop();
        event.getChannel()
            .sendMessage("Rückgabe wurde komplett gestoppt.");
    }

    /**
     * Startet den aktuellen Song von vorne
     * @param event Das Event mit den TextChannel
     */
    public void restartSong(MessageReceivedEvent event)
    {
        if (player.isStopped())
        {
            if (player.getPreviousAudioSource() != null)
            {
                player.reload(true);
                event.getChannel()
                    .sendMessage("Der Vorherige Song wird wiederholt.");
            }
            else
            {
                event.getChannel()
                    .sendMessage(
                            "Der Player hat diesen Song niemals gespielt. Er kann ihn nicht spielen.");
            }
        }
        else
        {
            player.reload(true);
            event.getChannel()
                .sendMessage("Der spielende Song wird von vorne begonnen!");
        }
    }

    /**
     * Schaltet Zufallswiedergabe ein oder aus
     * @param event Das Event mit den TextChannel
     */
    public void shuffePlayer(MessageReceivedEvent event)
    {
        if (player.isShuffle())
        {
            player.setShuffle(false);
            event.getChannel()
                .sendMessage(
                        "Der Player ist **nicht** mehr auf Zufallswiedergabe.");
        }
        else
        {
            player.setShuffle(true);
            event.getChannel()
                .sendMessage("Der Player ist jetzt auf Zufallswiedergabe.");
        }

    }

    /**
     * Resettet den Player bei einer Störung, oder wenn der Admin dies möchte
     * @param event Das Event mit den TextChannel
     */
    public void resetPlayer(MessageReceivedEvent event)
    {
        AudioManager manager = event.getGuild()
            .getManager();
        player.stop();
        player = new MusicPlayer();
        player.setVolume(DEFAULT_VOLUME);
        manager.setSendingHandler(player);
        event.getChannel()
            .sendMessage("Ein kompletter reset wurde durchgeführt.");
    }

    /**
     * Nimmt die Wiedergabe wieder auf
     * @param event Das Event mit den TextChannel
     */
    public void resumeSong(MessageReceivedEvent event)
    {
        if (player.isPaused())
        {
            player.play();
        }
        else
        {
            event.getChannel()
                .sendMessage("Der Player scheint bereits zu spielen.");
        }
    }
}
