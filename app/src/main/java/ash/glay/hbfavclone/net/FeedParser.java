package ash.glay.hbfavclone.net;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;

import static org.xmlpull.v1.XmlPullParser.*;

import ash.glay.hbfavclone.model.Channel;
import ash.glay.hbfavclone.model.Item;
import ash.glay.hbfavclone.model.RDF;

/**
 * Created by quesera2 on 2014/11/23.
 */
public class FeedParser {

    public static RDF parse(Reader response){
        RDF rdf = null;

        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(response);

            int eventType;
            while ((eventType = parser.getEventType()) != END_DOCUMENT) {
                if(eventType == START_TAG && "rdf:RDF".equals(parser.getName())){
                    rdf = new RDF();
                    parseRDF(rdf, parser);
                }
                parser.next();
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }finally{
            if(response != null){
                try {
                    response.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }

        return rdf;
    }

    private static void parseRDF(RDF rdf, XmlPullParser parser) throws XmlPullParserException, IOException{
        int eventType = parser.next();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch(eventType){
                case START_TAG:
                    if("channel".equals(parser.getName())){
                        parseChannel(parser, rdf);
                    }else if("item".equals(parser.getName())){
                        addItem(parser, rdf);
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    private static void parseChannel(XmlPullParser parser, RDF rdf) throws XmlPullParserException, IOException{
        Channel channel = new Channel();

        int eventType = parser.next();
        while (eventType != END_TAG && !"channel".equals(parser.getName())) {
            switch(eventType){
                case START_TAG:
                    if("title".equals(parser.getName())){
                        channel.title = parser.nextText();
                    }else if("link".equals(parser.getName())){
                        channel.link = parser.nextText();
                    }else if("description".equals(parser.getName())){
                        channel.description = parser.nextText();
                    }

                    break;
            }
            eventType = parser.next();
        }
        rdf.channel = channel;
    }

    private static void addItem(XmlPullParser parser, RDF rdf) throws XmlPullParserException, IOException {
        Item item = new Item();
        item.about = parser.getAttributeValue(null, "rdf:about");

        int eventType = parser.next();
        while (eventType != END_TAG && !"channel".equals(parser.getName())) {
            switch (eventType) {
                case START_TAG:
                    if ("title".equals(parser.getName())) {
                        item.title = parser.nextText();
                    } else if ("link".equals(parser.getName())) {
                        item.link = parser.nextText();
                    } else if ("description".equals(parser.getName())) {
                        item.description = parser.nextText();
                    } else if ("content:encoded".equals(parser.getName())) {
                        item.contents = URLDecoder.decode(parser.nextText(), "utf-8");
                    } else if ("dc:creator".equals(parser.getName())) {
                        item.creator = parser.nextText();
                    } else if ("dc:date".equals(parser.getName())) {
                        item.date = parser.nextText();
                    } else if ("hatena:bookmarkcount".equals(parser.getName())) {
                        item.bookmarkcount = Integer.parseInt(parser.nextText());
                    }
                break;
            }
            eventType = parser.next();
        }
        rdf.items.add(item);
    }
}
