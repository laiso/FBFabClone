package ash.glay.hbfavclone.model;

import java.util.Date;

/**
 * Created by quesera2 on 2014/11/23.
 */
public class Item {
    public String about;

    public String title;

    public String link;

    public String description;

    public String contents;

    public String creator;

    public String date;

    public int bookmarkcount;

    @Override
    public String toString() {
        return String.format("[about = %s ,\n" +
                "title = %s, \n" +
                "link = %s, \n" +
                "desc = %s, \n" +
                "contents = %s, \n" +
                "creator = %s, \n" +
                "bookmarkcount = %d]",
                about, title, link, description, contents, creator, bookmarkcount);
    }
}
