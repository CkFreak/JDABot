package materials;

import net.dv8tion.jda.core.entities.User;

import java.io.*;
import java.nio.file.Files;
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

    public Note(User author)
    {
        _author = author;
        try
        {
            _noteContent = getNoteFile();
        }
        catch (FileNotFoundException e)
        {
            try
            {
                new File(BASE_PATH + _author.getId()).createNewFile();
            }
            catch (IOException e1)
            {
                System.out.println(IOErrorMessage + _author.getId() + " the file for this user was not created");
            }
        }
        _noteFile = getFileForInit();
    }

    /**
     * Deletes the file and rewrites it with new content
     * @param note The new content that is to be added to the note
     */
    public void addNote(String note)
    {
        String path = _noteFile.getAbsolutePath();
        _noteFile.delete();
        _noteContent += note;
        try
        {
            _noteFile.createNewFile();
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
     * Gets the NoteFile for the user
     * @return The User's NoteFile
     */
    private File getFileForInit()
    {
        return new File(BASE_PATH + _author.getId());
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
            List<String> allLines = Files.readAllLines(Paths.get(BASE_PATH + _author.getId()));

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
    private User getAuthor()
    {
        return _author;
    }

    /**
     * @return The Content of the note
     */
    private  String getContent()
    {
        return _noteContent;
    }
}
