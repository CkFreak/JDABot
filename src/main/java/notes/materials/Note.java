package notes.materials;

import net.dv8tion.jda.core.entities.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Timbo on 16/03/2017.
 *
 * This class represents a Note Object that can be used by the NoteService.
 * A Note has a content and an Author
 */
public class Note
{
    private static final String BASE_PATH = "src/main/res/notes/";

    private static final String ERROR = "Error";

    private static final String IOErrorMessage = "There has been an IOException while trying to retrieve the note for a user with the ID of: ";

    private String _noteContent;

    private User _author;

    private File _noteFile;

    /**
     * Constructor for the Note class
     * @param author the author of the note
     */
    private Note(User author)
    {
        _author = author;
        try
        {
            _noteContent = getNoteFile();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("There will be a new Textfile for user: " + _author.getName());
            try
            {
                new File(BASE_PATH + _author.getId() + ".txt").createNewFile();
            }
            catch (IOException e1)
            {
                System.out.println(IOErrorMessage + _author.getId() + " the file for this user was not created");
                e1.printStackTrace();
            }
        }
        _noteFile = getFileForInit();
    }

    /**
     * The factory method for this Material class
     * @param author the author of the note
     * @return The note object collected from the constructor
     */
    public static Note getNoteForUser(User author)
    {
        return new Note(author);
    }

    /**
     * Deletes the file and rewrites it with new content
     * @param note The new content that is to be added to the note
     */
    public void addNote(String note)
    {
        //TODO The new File that is created is actually empty! Fix that
        String path = _noteFile.getAbsolutePath();
        _noteFile.delete();
        _noteContent += "\n" + note;
        try
        {
            //_noteFile.createNewFile();
                Writer writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(path), "utf-8"));

                {
                    writer.write(_noteContent);
                    System.out.println("The File was created at: " + path);
                }
        }
        catch (IOException e)
        {
            System.out.println(IOErrorMessage + _author.getId());
        }
    }

    /**
     * Deletes the note from the invoking user.
     */
    public boolean deleteNote()
    {
        Path pathToNote = Paths.get(BASE_PATH + _author.getId() + ".txt");
        File note = pathToNote.toFile();
        return note.delete();
    }

    /**
     * Gets the NoteFile for the user
     * @return The User's NoteFile
     */
    private File getFileForInit()
    {
        return new File(BASE_PATH + _author.getId() + ".txt");
    }


    /**
     * Retrieves the users Note File and packs the content in a String
     * @return the content of the Note File as String
     */
    private String getNoteFile() throws FileNotFoundException
    {
        String content = ERROR;
        try
        {
            List<String> allLines = Files.readAllLines(Paths.get(BASE_PATH + _author.getId() + ".txt"));

            content = "";

            for (String line : allLines)
            {
                content += line + "\n";
            }
        }
        catch (IOException e)
        {
            System.out.println
                    (IOErrorMessage + _author.getId());
        }
        if (content.equals(ERROR))
        {
            throw new FileNotFoundException("The user does not have a file yet... \n creating user note file");
        }
        return content;
    }

    /**
     * @return The Author of the note
     */
    public User getAuthor()
    {
        return _author;
    }

    /**
     * @return The Content of the note
     */
    public String getContent()
    {
        return _noteContent;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Note)
        {
            if (this._author.equals(((Note) other)._author) && this._noteFile.equals(((Note) other)._noteFile))
            {
                return true;
            }
        }
        return false;
    }
}
