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
