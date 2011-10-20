package withgod.lingr;

import org.apache.commons.codec.binary.Hex;
import twitter4j.*;
import twitter4j.internal.http.HttpClientWrapper;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class Mention2Lingr {
    private static Logger logger = Logger.getLogger(Mention2Lingr.class.getName());
    private String secret = null;
    private String botid  = null;
    private String roomid = null;
    private String screenName = null;
    private String lingrKey = null;

    private void run() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        if (screenName == null) {
            try {
                screenName = twitterStream.getScreenName();
            } catch (TwitterException e) {
                logger.warning("init error");
                logger.warning(e.getMessage());
                System.exit(-1);
            }
            logger.info("bot screenname [" + screenName + "]");
        }

        UserStreamListener listener = new UserStreamListener() {
            public void onStatus(Status status) {
                String text = status.getText();
                //logger.info("onStatus:" + status.getText());
                //logger.info(botid + "/" + secret + "/" + roomid + "/" + screenName);
                if (text.startsWith("@" + screenName)) {
                    say(botid, secret, roomid, status);
                }
            }
            public void onException(Exception ex) {
                logger.warning(ex.getMessage());
                logger.info("onException:" + ex.getMessage());
            }
            public void onDeletionNotice(long l, long l1) { }
            public void onFriendList(long[] longs) { }
            public void onFavorite(User user, User user1, Status status) { }
            public void onUnfavorite(User user, User user1, Status status) { }
            public void onFollow(User user, User user1) { }
            public void onRetweet(User user, User user1, Status status) { }
            public void onDirectMessage(DirectMessage directMessage) { }
            public void onUserListMemberAddition(User user, User user1, UserList userList) { }
            public void onUserListMemberDeletion(User user, User user1, UserList userList) { }
            public void onUserListSubscription(User user, User user1, UserList userList) { }
            public void onUserListUnsubscription(User user, User user1, UserList userList) { }
            public void onUserListCreation(User user, UserList userList) { }
            public void onUserListUpdate(User user, UserList userList) { }
            public void onUserListDeletion(User user, UserList userList) { }
            public void onUserProfileUpdate(User user) { }
            public void onBlock(User user, User user1) { }
            public void onUnblock(User user, User user1) { }
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) { }
            public void onTrackLimitationNotice(int i) { }
            public void onScrubGeo(long l, long l1) { }
        };
        twitterStream.addListener(listener);
        twitterStream.user();
    }

    public Mention2Lingr(String secret, String roomid, String botid) {
        this.secret = secret;
        this.roomid = roomid;
        this.botid  = botid;
        this.run();
    }

    private void say(String botid, String secret, String roomid, Status status) {
        if (lingrKey == null) {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA");
            } catch (NoSuchAlgorithmException e) {
                logger.warning(e.getMessage());
            }
            lingrKey = new String(Hex.encodeHex(md.digest((botid + secret).getBytes())));
        }
        String text = status.getUser().getProfileImageURL() + " " + status.getUser().getScreenName() +  "\n";
        text += status.getText().replaceFirst("@" + screenName, "");

        String endpoint = "http://lingr.com/api/room/say?room=" + roomid + "&bot=" + botid;
        try {
            endpoint += "&bot_verifier=" + lingrKey;
            endpoint += "&text=" + URLEncoder.encode(text, "UTF-8");
            HttpClientWrapper client = new HttpClientWrapper();
            client.get(endpoint);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: Mention2Lingr lingrSecret roomId botId");
        } else {
            new Mention2Lingr(args[0], args[1], args[2]);
        }
    }
}
