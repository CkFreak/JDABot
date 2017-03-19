package services;

import materials.Note;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timbo on 16/03/2017.
 *
 * The NoteService handles Notes from users. It also offers access to operations of the Note class.
 */
public class NoteService
{
    private List<Note> _notes;

    public NoteService(List<User> serverUsers)
    {
        _notes = new ArrayList<>();
        initNoteList(serverUsers);
    }

    /**
     * Adds a note the the users note file
     * @param author the author whoms note is to be edited
     * @param note the note that the user wants to add
     * @return true, if the note was created successfully, false otherwise
     */
    public boolean addNote(User author, String note)
    {
        for (Note remark : _notes)
        {
            if (remark.getAuthor().equals(author))
            {
                remark.addNote(note);
                return true;
            }
        }
        return false;
    }

    /**
     * Gives the note for a user if it exist. If the note cannot be found, an empty note will be returned
     * @param author The authors whoms note is being searched
     * @return the authors note either with content if there is any an empty note otherwise
     */
    public Note getNoteforUser(User author)
    {
        for (Note note : _notes)
        {
            if (note.getAuthor().equals(author))
            {
                return note;
            }
        }
        return Note.getNoteForUser(author);
    }

    /**
     * Initialzes the note list of this class
     * @param serverUsers all the users from the server this note list belongs to.
     */
    private void initNoteList(List<User> serverUsers)
    {
        for (User user : serverUsers)
        {
            _notes.add(Note.getNoteForUser(user));
        }
    }
}
