package handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import services.CommandService;
import services.IJustLostTheGameService;
import services.TournamentService;
import values.AbstractPoll;
import values.PublicPoll;

/**
 * This class takes all user input and processes it. It holds all commands but no knowledge about them.
 * @author CkFreak
 * @version 2016
 */
public class CommandHandler implements Observer
{

    private final static String SALT_EMOJI = "src/main/res/saltEmoji.jpg";

    private final static String SUGAR_EMOJI = "src/main/res/zucker.jpg";

    private final static String JUST_RIGHT_MEME = "src/main/res/justright.gif";

    private final static String FUCK_OFF = "src/main/res/fuckoff.gif";

    private final static String LIKE = "src/main/res/like.png";

    private final static String BITCH_PLEASE = "src/main/res/bitchplease.jpg";

    private final static String HELP_COMMAND_FILE = "src/main/res/commands.txt";

    private static final String THE_GAME_INITIALIZATION = "The Game has been initialized!";

    private static final String INSUFICENT_RIGHTS_MESSAGE = "You do not have sufficent permissions to do that";

    /**
     * The Service that executes the commands
     */
    private CommandService _commander;

    /**
     * The JDA instance of this bot 
     */
    private JDA _jda;

    /**
     * The LAVA Player for music
     */
    private AudioHandlerReplacement _player;

    /**
     * The IJustLostTheGameService that makes sure the game is lost in random intervals (Dammit I just lost the Game)
     */
    private IJustLostTheGameService _loseGameService;

    /**
     * A list with all active polls
     */
    private List<AbstractPoll> _polls;

    /**
     * A MessageReceivedEvent
     */
    private MessageReceivedEvent _event;

    /**
     * A Tournament Service that starts Tournaments
     */
    private TournamentService _tournamentService;

    /**
     * Initializes a CommandHandler and all its services
     */
    public CommandHandler(JDA jda)
    {
        _event = null;
        _jda = jda;
        _polls = new ArrayList<>();
        _commander = new CommandService(_event, jda);
        _loseGameService = new IJustLostTheGameService();
        _loseGameService.addObserver(this);
        _player = new AudioHandlerReplacement();
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

        String message = event.getMessage()
            .getContent();

        if (message.startsWith("#"))
        {
            //splits the message at spaces
            String[] messageContent = message.split("\\s+");
            event.getMessage()
                .deleteMessage();
            event.getChannel()
                .sendTyping()
                .queue();
            ;

            switch (messageContent[0].substring(1))
            {
            case "hello":
                _commander.replyToHello(event);
                break;

            case "help":
                _commander.getHelpCommands(event, HELP_COMMAND_FILE);
                break;

            case "admin":
                event.getChannel()
                    .sendMessage(_commander.getAdmin(event)
                        .toString());
                break;

            case "delete":
                int amount = Integer.valueOf(messageContent[1]);
                _commander.deleteChannelMessages(event, amount);
                event.getChannel()
                    .sendMessage(
                            "Es wurden " + amount + " Nachrichten gelöscht.")
                    .queue();
                ;
                break;

            case "userInfo":
                String messageForUser = _commander.getUserInfo(event,
                        messageContent);
                event.getChannel()
                    .sendMessage(messageForUser)
                    .queue();
                ;
                break;

            case "salt":
                _commander.sendEmoji(event, SALT_EMOJI);
                break;

            case "justright":
                _commander.sendEmoji(event, JUST_RIGHT_MEME);
                break;

            case "zucker":
                _commander.sendEmoji(event, SUGAR_EMOJI);
                ;
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
                ;
                _commander.sendEmoji(event, BITCH_PLEASE);
                break;

            //            case "deleteAll":
            //                _commander.deleteAllMessages(event);
            //                break;

            case "add":
                _player.registerNewTrack(messageContent[1], event);
                break;

            case "play":
                event.getChannel()
                    .sendMessage(
                            "Nicht ungeduldig werden, das kann ein wenig dauern. Es wird begonnen, die URL zu verarbeiten!")
                    .queue();

                if (_player.getPlaylist() == null)
                {
                    event.getChannel()
                        .sendMessage(
                                "Currently the playlist is empty. Please use #add to add Songs to the playlist")
                        .queue();
                }
                _player.startPlaying();
                break;

            case "pause":
                _player.pausePlayer();
                event.getChannel()
                    .sendMessage("Playback has been paused")
                    .queue();
                ;
                break;

            case "volume":
                _player.setVolume(Integer.parseInt(messageContent[1]));
                break;

            case "stop":
                _player.stopPlayer();
                ;
                break;

            case "skip":
                _player.playNextSong();;
                break;

            case "playlist":
                event.getChannel()
                    .sendMessage(_player.getPlaylist())
                    .queue();
                ;
                break;

            case "restart":
                _player.restartSong();
                break;

            case "reset":
                if (_commander.isAdmin(event.getAuthor(), event))
                {
                    _player.resetPlayer();
                }
                else
                {
                    event.getChannel()
                        .sendMessage(INSUFICENT_RIGHTS_MESSAGE)
                        .queue();
                    ;
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

                _player.isShuffle(enable);
                break;

            case "join":
//                _player.joinChannel(messageContent[1], event);
                break;

            case "leave":
//                _player.leaveChannel(event);
                break;

            case "shutdown":
                _commander.reagiereAufShutdown(event);
                break;

            case "resume":
                _player.resumePlayer();
                break;

            case "mods":
                event.getChannel()
                    .sendMessage(_commander.getMods(event))
                    .queue();
                ;
                break;

            case "changeGame":
                event.getMessage()
                    .deleteMessage()
                    .queue();
                ;

                _commander.changeGame(_jda, getGameName(messageContent));
                break;

            case "startPVote":
                _commander.startPVote(getPollName(messageContent),
                        event.getAuthor(), event, _polls,
                        getOptions(messageContent));
                break;

            case "startAVote":
                _commander.startAVote(getPollName(messageContent),
                        event.getAuthor(), event, _polls,
                        getOptions(messageContent));
                break;

            case "getStatus":
                event.getChannel()
                    .sendMessage(getStatus(getPollName(messageContent)))
                    .queue();
                ;
                break;

            case "endVote":
                event.getChannel()
                    .sendMessage(_commander.endVote(getPollName(messageContent),
                            event.getAuthor(), _polls))
                    .queue();
                ;
                removePoll(getPollName(messageContent));
                break;

            case "vote":
                ArrayList<String> option = new ArrayList<>();
                option = getOptions(messageContent);
                event.getChannel()
                    .sendMessage(_commander.vote(getPollName(messageContent),
                            option.get(0), event.getAuthor(), _polls, event))
                    .queue();
                ;
                break;

            case "listVotes":
                event.getChannel()
                    .sendMessage(getCurrentVotes())
                    .queue();
                ;
                break;

            case "startGame":
                _loseGameService.executeGameLoss(event);
                event.getChannel()
                    .sendMessage(THE_GAME_INITIALIZATION)
                    .queue();
                ;
                break;

            case "gameData":
                event.getChannel()
                    .sendMessage("Next Game Loss:" + "\n"
                            + _loseGameService.getNextGameLoss()
                                .toString()
                            + "\n Now: \n" + _loseGameService.getNow()
                                .toString())
                    .queue();
                ;
                break;

            //TODO Command richtig implementieren
            case "startTournament":
                event.getChannel()
                    .sendMessage(_tournamentService
                        .initializeTournament(getOptions(messageContent)))
                    .queue();
                ;
                break;

            default:
                event.getChannel()
                    .sendMessage("Sorry but this command is not defined!")
                    .queue();
                ;
                break;

            }
        }
    }

    /**
     * Removes a poll from the poll list
     * @param pollName The poll that is to be removed from the list
     */
    private void removePoll(String pollName)
    {
        for (AbstractPoll abstractPoll : _polls)
        {
            if (abstractPoll.getName()
                .equals(pollName))
            {
                _polls.remove(abstractPoll);
            }
        }

    }

    /**
     * Returns all active polls
     * @return A Message that contains all polls
     */
    private Message getCurrentVotes()
    {
        MessageBuilder builder = new MessageBuilder();

        for (AbstractPoll abstractPoll : _polls)
        {
            builder.appendString(abstractPoll.getName() + "\n");
        }
        return builder.build();
    }

    /**
     * Returns the requested poll's status
     * @param pollName The requested poll's name
     * @return The state or an error 
     */
    private Message getStatus(String pollName)
    {
        MessageBuilder builder = new MessageBuilder();

        for (AbstractPoll abstractPoll : _polls)
        {
            if (abstractPoll.getName()
                .equals(pollName))
            {
                if (abstractPoll instanceof PublicPoll)
                {
                    return ((PublicPoll) abstractPoll)
                        .getStatus((PublicPoll) abstractPoll);
                }
            }
        }
        return builder.appendString(
                "The specified poll has not been found or is anonymous!")
            .build();
    }

    /**
     * Returns the Game the Bot wants to play next
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
     * Returns the choosens poll's name
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
     * @param messageContent the message content without white spaces
     * @return A list with poll options
     */
    private ArrayList<String> getOptions(String[] messageContent)
    {
        ArrayList<String> options = new ArrayList<>();
        String option = "";
        int such = 1;

        //searching for the first "_" so it's definetly not in the options list.
        do
        {
            ++such;
        }
        while (!messageContent[such].contains("_"));

        for (int i = such; i <= messageContent.length - 1; ++i)
        {
            System.out.println(messageContent[i]);
            option += " " + messageContent[i] + " ";
            if (messageContent[i].contains("_"))
            {
                option = option.replaceAll("_", "");
                options.add(such, option);
                option = "";
            }

        }
        return options;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        _loseGameService.executeGameLoss(_event);
    }

}
