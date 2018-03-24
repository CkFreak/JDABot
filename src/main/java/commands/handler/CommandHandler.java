package commands.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import commands.CommandService;
import net.dv8tion.jda.core.entities.Message;
import notes.NoteService;
import org.json.simple.parser.ParseException;
import poll.PollService;
import theGame.IJustLostTheGameService;
import tournament.enums.TournamentMode;
import music.managers.GuildMusicManager;
import music.managers.MusicControlManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import tournament.services.TournamentService;
import weather.WeatherService;

/**
 * This class takes all user input and processes it. It holds all commands but no knowledge about them.
 *
 * @author CkFreak
 * @version 1.12.2016
 */
public class CommandHandler implements Observer
{

    private final static String SALT_EMOJI = "src/main/res/saltEmoji.jpg";

    private final static String SUGAR_EMOJI = "src/main/res/zucker.jpg";

    private final static String JUST_RIGHT_MEME = "src/main/res/justright.gif";

    private final static String FUCK_OFF = "src/main/res/fuckoff.gif";

    private final static String LIKE = "src/main/res/like.png";

    private final static String BITCH_PLEASE = "src/main/res/bitchplease.jpg";

    private final static String COMMANDS_HELP_COMMAND_FILE = "src/main/res/commands.txt";

    private final static String EMOJI_HELP_COMMAND_FILE = "src/main/res/emojiCommands.txt";

    private final static String POLL_HELP_COMMAND_FILE = "src/main/res/pollCommands.txt";

    private final static String GENERAL_HELP_COMMAND_FILE = "src/main/res/generalCommands.txt";

    private final static String MUSIC_HELP_COMMAND_FILE = "src/main/res/musicCommands.txt";

    private final static String TOURNAMENT_HELP_COMMAND_FILE = "src/main/res/tournamentCommands.txt";

    private final static String NOTE_HELP_COMMAND_FILE = "src/main/res/noteCommands.txt";

    private final static String WEATHER_HELP_FILE = "src/main/res/weatherCommands.txt";

    private static final String INSUFFICIENT_RIGHTS_MESSAGE = "You do not have sufficient permissions to do that";

    /**
     * The Service that executes the commands
     */
    private CommandService _commander;

    /**
     * The JDA instance of this bot
     */
    private JDA _jda;

    /**
     * The MusicControlManager to get GuildMusicManagers
     */
    private MusicControlManager _musicControlManager;

    /**
     * The IJustLostTheGameService that makes sure the game is lost in random intervals (Dammit I just lost the Game)
     */
    private IJustLostTheGameService _loseGameService;

    /**
     * The PollService of this CommandHandler
     */
    private PollService _pollService;

    /**
     * A MessageReceivedEvent
     */
    private MessageReceivedEvent _event;

    /**
     * A Tournament Service that starts Tournaments
     */
    private TournamentService _tournamentService;

    /**
     * The NoteService of the system ceeping track of all notes by users
     */
    private NoteService _noteService;

    /**
     * The WeatherService of the system, offering current weather information
     */
    private WeatherService _weatherService;

    /**
     * The command hash map
     */
    private HashMap<String, Runnable> _commands;

    private String[] messageContent;

    /**
     * Initializes a CommandHandler and all its services
     */
    public CommandHandler(JDA jda)
    {
        _event = null;
        _jda = jda;
        _commander = new CommandService(jda);
        _pollService = new PollService();
        _tournamentService = new TournamentService();
        _loseGameService = new IJustLostTheGameService();
        _loseGameService.addObserver(this);
        _musicControlManager = new MusicControlManager();
        _noteService = new NoteService(_jda.getUsers());
        _weatherService = new WeatherService();
        _commands = new HashMap<>();
    }

    /**
     * A Method that catches every Message and checks for the command escape charackter
     *
     * @param event The MessageReceivedEvent with the message inside
     */
    public void handleIncomingMessages(MessageReceivedEvent event)
    {
        _event = event;
        _commander.initializeRoles(event);

        String message = event.getMessage().getContentRaw();

        if (message.startsWith("#"))
        {
            //splits the message at spaces
            messageContent = message.split("\\s+");
            event.getChannel()
                    .sendTyping()
                    .queue(s -> event.getMessage().delete().queue());

            switch (messageContent[0].substring(1))
            {
                case "promote":
                    if (messageContent.length < 3)
                    {
                        event.getChannel().sendMessage("You have to specify a user and a role").queue();
                        break;
                    }
                    if (_commander.isModerator() || _commander.isAdmin())
                    {
                        if (_commander.promoteUser(event, messageContent[1], messageContent[2]))
                        {
                            event.getChannel().sendMessage("User " + messageContent[1] + " has been promoted to "
                                    + messageContent[2]).queue();
                        }
                    }
                    else
                    {
                        event.getChannel().sendMessage(INSUFFICIENT_RIGHTS_MESSAGE).queue();
                    }
                    break;

                case "delete":
                    int amount = Integer.valueOf(messageContent[1]);
                    _commander.deleteChannelMessages(event, amount);
                    event.getChannel()
                            .sendMessage(
                                    amount + " Messages have been deleted.")
                            .queue();
                    break;

                case "userInfo":

                    if (messageContent.length < 2)
                    {
                        event.getChannel().sendMessage("A User has to be specified").queue();
                        break;
                    }
                    String messageForUser = _commander.getUserInfo(messageContent[1], event.getGuild());
                    event.getChannel()
                            .sendMessage(messageForUser)
                            .queue();
                    break;

                case "salt":
                    _commander.sendEmoji(event, SALT_EMOJI);
                    break;

                case "justright":
                    _commander.sendEmoji(event, JUST_RIGHT_MEME);
                    break;

                case "sugar":
                    _commander.sendEmoji(event, SUGAR_EMOJI);
                    break;

                case "fuckoff":
                    _commander.sendEmoji(event, FUCK_OFF);
                    break;

                case "like":
                    _commander.sendEmoji(event, LIKE);
                    break;

                case "please":
                    event.getChannel()
                            .sendTyping()
                            .queue();
                    _commander.sendEmoji(event, BITCH_PLEASE);
                    break;

                //            case "deleteAll":
                //                _commander.deleteAllMessages(event);
                //                break;
                case "play":
                    if (messageContent.length < 2)
                    {
                        event.getChannel().sendMessage("You have to enter a URL with that command").queue();
                    }
                    else
                    {
                        GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                        guildMusicManager.connectToAudioChannel(null, null, event.getMember());
                        guildMusicManager.getScheduler().registerNewTrack(messageContent[1], _musicControlManager.getPlayerManager(), event);
                    }
                    break;

                case "pause":
                {
                    GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                    guildMusicManager.getScheduler().pausePlayer();
                    event.getChannel()
                            .sendMessage("Playback has been paused")
                            .queue();
                }
                break;

                /*case "volume":
                {
                    GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                    guildMusicManager.getScheduler().setVolume(Integer.parseInt(messageContent[1]));
                    event.getChannel().sendMessage("Volume has been set to " + messageContent[1]).queue();
                }
                break;
                */

                case "stop":
                {
                    GuildMusicManager gMM = getGuildMusicManager(event);
                    gMM.getScheduler().stopPlayer();
                    event.getChannel().sendMessage("Playback has been stopped").queue();
                }
                break;

                case "resume":
                {
                    GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                    guildMusicManager.getScheduler().resumePlayer();
                }
                break;

                case "skip":
                {
                    GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                    guildMusicManager.getScheduler().skip();
                }
                event.getChannel().sendMessage("The playing track has been skipped");
                break;

                case "playlist":
                {
                    GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                    ArrayList<Message> playlist = guildMusicManager.getScheduler().getPlaylist();

                    for (int i = 0; i < playlist.size(); ++i)
                    {
                        event.getChannel()
                                .sendMessage(playlist.get(i))
                                .queue();
                    }
                }
                break;

                case "songInfo":
                {
                    GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                    event.getChannel().sendMessage(guildMusicManager.getScheduler().songInfo()).queue();
                }

                case "jump":
                {
                    GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                    guildMusicManager.getScheduler().startSpecificTrack(Integer.valueOf(messageContent[1]));
                    event.getChannel().sendMessage("Track " + messageContent[1] + " is now playing").queue();
                    break;
                }

                case "restart":
                {
                    GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                    guildMusicManager.getScheduler().restartSong();
                    event.getChannel().sendMessage("The Song has been restarted").queue();
                }
                break;

                case "reset":
                    if (_commander.isAdmin())
                    {
                        GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                        guildMusicManager.getScheduler().resetPlayer();
                        event.getChannel().sendMessage("The Player has been reset").queue();
                    }
                    else
                    {
                        event.getChannel()
                                .sendMessage(INSUFFICIENT_RIGHTS_MESSAGE)
                                .queue();
                    }
                    break;

                case "shuffle":
                    boolean enable = false;
                    if (messageContent[1].equals("1"))
                    {
                        enable = true;
                    }
                    else if (messageContent[1].equals("0"))
                    {
                        enable = false;
                    }

                {
                    GuildMusicManager guildMusicManager = getGuildMusicManager(event);
                    guildMusicManager.getScheduler().setShuffle(enable);
                    if (enable)
                    {
                        event.getChannel().sendMessage("The Player will shuffle").queue();
                    }
                    else
                    {
                        event.getChannel().sendMessage("The player will not shuffle").queue();
                    }
                }
                break;

                case "join":
                {
                    if (messageContent.length == 2)
                    {
                        _musicControlManager.getGuildMusicManager(event.getGuild(), event).connectToAudioChannel(messageContent[1],
                                event.getGuild(), event.getMember());
                    }
                }
                break;

                case "leave":
                    _musicControlManager.getGuildMusicManager(event.getGuild(), event).leaveVoiceChannel();
                    break;

                case "shutdown":
                    event.getChannel().sendMessage("Going down for maintenance").queue();
                    _commander.reactToShutdown(event);
                    break;

                case "mods":
                    event.getChannel()
                            .sendMessage(_commander.getMods(event))
                            .queue();
                    break;

                case "changeGame":
                    _commander.changeGame(_jda, getGameName(messageContent));
                    break;

                case "startPVote":
                    _pollService.startPPoll(getPollName(messageContent),
                            event.getAuthor(), _event, getOptions(messageContent, 0));
                    break;

                case "startAVote":
                    _pollService.startAPoll(getPollName(messageContent),
                            event.getAuthor(), event, getOptions(messageContent, 0));
                    break;

                case "getStatus":
                    event.getChannel()
                            .sendMessage(
                                    _pollService.getStatus(getPollName(messageContent)))
                            .queue();
                    break;

                case "endVote":
                    event.getChannel()
                            .sendMessage(_pollService.endPoll(
                                    getPollName(messageContent), event.getAuthor()))
                            .queue();
                    break;

                case "vote":
                    event.getChannel()
                            .sendMessage(_pollService.vote(getPollName(messageContent),
                                    getOptions(messageContent, 0), event.getAuthor(),
                                    event))
                            .queue();
                    break;

                case "listVotes":
                    event.getChannel()
                            .sendMessage(_pollService.getCurrentVotes())
                            .queue();
                    break;

                case "startTournament":
                    TournamentMode mode = null;
                    String name = getPollName(messageContent);
                    String tournamentMode = getOptions(messageContent, 0).get(0).toLowerCase().replaceAll(" ", "");
                    switch (tournamentMode)
                    {
                        case "singleelimination":
                            mode = TournamentMode.SINGE_ELIMINATION;
                            break;
                        case "doubleelimination":
                            mode = TournamentMode.DOUBLE_ELIMINATION;
                            break;
                        case "trippleelimination":
                            mode = TournamentMode.TRIPLE_ELIMINATION;
                            break;
                        case "roundrobin":
                            mode = TournamentMode.ROUND_ROBIN;
                            break;
                        default:
                            event.getChannel().sendMessage("The Tournament type does not match any known modes").queue();
                            break;

                    }

                    event.getChannel()
                            .sendMessage(_tournamentService
                                    .initializeTournament(name, mode, getOptions(messageContent, 2)))
                            .queue();
                    break;

                case "registerLoss":
                    if (messageContent.length < 3)
                    {
                        event.getChannel().sendMessage("Please enter the name of the tournament").queue();
                        break;
                    }
                    event.getChannel().sendMessage(_tournamentService
                            .registerLoss(getPollName(messageContent), getOptions(messageContent, 1).get(0)))
                            .queue();
                    break;

                case "addNote":
                    if (_noteService.addNote(event.getAuthor(), getGameName(messageContent)))
                    {
                        event.getChannel().sendMessage("Note successfully created").queue();
                    }
                    else
                    {
                        event.getChannel().sendMessage("The note could not be created please contact a dev").queue();
                    }
                    break;

                case "showNote":
                    event.getChannel().sendMessage(_noteService.getNoteforUser(event.getAuthor()).getContent()).queue();
                    break;

                case "weather":
                    if (messageContent.length < 2)
                    {
                        event.getChannel().sendMessage("Please enter a city you want the weather data for").queue();
                        break;
                    }
                    try
                    {
                        event.getChannel().sendMessage(_weatherService.getCompleteWeatherData(getGameName(messageContent))).queue();
                    }
                    catch (IOException e)
                    {
                        event.getChannel().sendMessage("OpenWeatherMap returned an error. Please consult a Dev for fixing this").queue();
                    }
                    catch (ParseException e)
                    {
                        event.getChannel().sendMessage("The JSON passed by OpenWeatherMap had an unexpected format. Please consult a Dev").queue();
                    }
                    break;

                case "temperature":
                    if (messageContent.length < 2)
                    {
                        event.getChannel().sendMessage("Please enter a city you want the temperature data for").queue();
                        break;
                    }
                    try
                    {
                        event.getChannel().sendMessage(_weatherService.getTemperatureData(getGameName(messageContent))).queue();
                    }
                    catch (IOException e)
                    {
                        event.getChannel().sendMessage("OpenWeatherMap returned an error. Please consult a Dev for fixing this").queue();
                    }
                    catch (ParseException e)
                    {
                        event.getChannel().sendMessage("The JSON passed by OpenWeatherMap had an unexpected format. Please consult a Dev").queue();

                    }
                    break;

                case "wind":
                    if (messageContent.length < 2)
                    {
                        event.getChannel().sendMessage("Please enter a city you want the wind data for").queue();
                        break;
                    }
                    try
                    {
                        event.getChannel().sendMessage(_weatherService.getWindData(getGameName(messageContent))).queue();
                    }
                    catch (IOException e)
                    {
                        event.getChannel().sendMessage("OpenWeatherMap returned an error. Please consult a Dev for fixing this").queue();
                    }
                    catch (ParseException e)
                    {
                        event.getChannel().sendMessage("The JSON passed by OpenWeatherMap had an unexpected format. Please consult a Dev").queue();

                    }
                    break;


                default:
                    event.getChannel()
                            .sendMessage("Sorry but this command is not defined!")
                            .queue();
                    break;

            }
        }
    }

    private void populateCommandMap()
    {

        _commands.put("hello", () -> _commander.replyToHello(_event));
        _commands.put("help", () -> _event.getChannel().sendMessage(_commander.getCommands(COMMANDS_HELP_COMMAND_FILE)).queue());
        _commands.put("helpMusic", () -> _event.getChannel().sendMessage(_commander.getCommands(MUSIC_HELP_COMMAND_FILE)).queue());
        _commands.put("helpGeneral", () -> _event.getChannel().sendMessage(_commander.getCommands(GENERAL_HELP_COMMAND_FILE)).queue());
        _commands.put("helpPoll", () -> _event.getChannel().sendMessage(_commander.getCommands(POLL_HELP_COMMAND_FILE)).queue());
        _commands.put("helpEmoji", () -> _event.getChannel().sendMessage(_commander.getCommands(EMOJI_HELP_COMMAND_FILE)).queue());
        _commands.put("helpTournament", () -> _event.getChannel().sendMessage(_commander.getCommands(TOURNAMENT_HELP_COMMAND_FILE)).queue());
        _commands.put("helpNote", () -> _event.getChannel().sendMessage(_commander.getCommands(NOTE_HELP_COMMAND_FILE)).queue());
        _commands.put("helpWeather", () -> _event.getChannel().sendMessage((_commander.getCommands(WEATHER_HELP_FILE))).queue());
        _commands.put("admin", () -> _event.getChannel().sendMessage(_commander.getAdmin(_event)).queue());
        _commands.put("promote", () ->
        {
            if (messageContent.length < 3)
            {
                _event.getChannel().sendMessage("You have to specify a user and a role").queue();
            }
            else if (_commander.isModerator() || _commander.isAdmin())
            {
                if (_commander.promoteUser(_event, messageContent[1], messageContent[2]))
                {
                    _event.getChannel().sendMessage("User " + messageContent[1] + " has been promoted to "
                            + messageContent[2]).queue();
                }
            }
            else
            {
                _event.getChannel().sendMessage(INSUFFICIENT_RIGHTS_MESSAGE).queue();
            }
        });
        _commands.put("delete", () -> {
            int amount = Integer.valueOf(messageContent[1]);
                    _commander.deleteChannelMessages(_event, amount);
                    _event.getChannel()
                            .sendMessage(
                                    amount + " Messages have been deleted.")
                            .queue();
        });
        _commands.put("userInfo", () -> {
            if (messageContent.length < 2)
                    {
                        _event.getChannel().sendMessage("A User has to be specified").queue();
                        return;
                    }
                    String messageForUser = _commander.getUserInfo(messageContent[1], _event.getGuild());
                    _event.getChannel()
                            .sendMessage(messageForUser)
                            .queue();
        });
    }

    /**
     * @param event A MessageReceivedEvent
     * @return A GuildMusicManager for a specific Guild
     */
    private GuildMusicManager getGuildMusicManager(MessageReceivedEvent event)
    {
        return _musicControlManager.getGuildMusicManager(event.getGuild(), event);
    }


    /**
     * Returns the Game the Bot wants to play next
     *
     * @param messageContent The message contents without white spaces
     * @return The name of the next game as String
     */
    private String getGameName(String[] messageContent)
    {
        String game = "";
        int length = messageContent.length;
        for (int i = 1; i <= length - 1; ++i)
        {
            game += " " + messageContent[i];
        }
        return game;
    }

    /**
     * Returns the chosen poll's name
     *
     * @param messageContent the message contents without white spaces
     * @return the poll's name
     */
    private String getPollName(String[] messageContent)
    {
        String name = "";
        int index = 1;
        int i = 1;
        do
        {
            name += " " + messageContent[i] + " ";
            name = name.replaceAll("_", "");
            ++index;
            ++i;
        }
        while (!messageContent[index - 1].contains("_"));
        return name;
    }

    /**
     * Gives the desired poll options
     *
     * @param messageContent the message content without white spaces
     * @param startingPoint  The amount of "_" after which the message content ist to be analyzed
     * @return A list with poll options
     */
    private ArrayList<String> getOptions(String[] messageContent, int startingPoint)
    {
        ArrayList<String> options = new ArrayList<>();
        String option = "";
        int such = 1;

        //searching for the first "_" so it's definitely not in the options list.
        do
        {
            ++such;

            if (messageContent[such].contains("_"))
            {
                //now we found the first "_" and we have to check, weather or not we should continue the search
                --startingPoint;
            }
        }
        while (startingPoint > 0);

        for (int i = such; i <= messageContent.length - 1; ++i)
        {
            option += " " + messageContent[i] + " ";
            if (messageContent[i].contains("_"))
            {
                option = option.replaceAll("_", "");
                options.add(option);
                option = "";
            }

        }
        return options;
    }


    @Override
    public void update(Observable o, Object arg)
    {
        _jda.getGuilds().get(0).getDefaultChannel().sendMessage((String) arg).queue();
    }

}
