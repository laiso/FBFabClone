package ash.glay.hbfavclone.model;

/**
 * Created by quesera on 2014/11/23.
 */
public class Channel {
    public String title;

    public String link;

    public String description;

    @Override
    public String toString() {
        return String.format("[title = %s, link = %s, desc = %s]", title, link, description);
    }
}
