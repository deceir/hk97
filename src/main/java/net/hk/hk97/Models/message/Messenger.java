package net.hk.hk97.Models.message;


import com.goebl.david.Webb;
import net.hk.hk97.Config;

public class Messenger {

    public static boolean sendMessage(long recipient, String subject, String message) {

        try {

            Webb webb = Webb.create();
            webb.post("https://politicsandwar.com/api/send-message/")
                    .param("key", Config.itachiPnwKey)
                    .param("to", recipient)
                    .param("subject", subject)
                    .param("message", message)
                    .ensureSuccess()
                    .asVoid();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void sendMessagePnw(long recipient, String subject, String message, String apiKey) {

        try {

            Webb webb = Webb.create();
            webb.post("https://politicsandwar.com/api/send-message/")
                    .param("key", apiKey)
                    .param("to", recipient)
                    .param("subject", subject)
                    .param("message", message)
                    .ensureSuccess()
                    .asVoid();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
