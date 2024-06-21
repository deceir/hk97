package net.hk.hk97.Commands.Listeners.Implementations;

import net.hk.hk97.Commands.Listeners.MessageDeletecListener;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageDeleteEvent;
import org.springframework.stereotype.Component;

@Component
public class MessageDeleteListener implements MessageDeletecListener {

    @Override
    public void onMessageDelete(MessageDeleteEvent messageDeleteEvent) {

        Message message = messageDeleteEvent.getMessage().get();

        if (!message.getMentionedUsers().isEmpty()) {

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor(messageDeleteEvent.getMessageAuthor().get())
                    .setTitle("Possible Ghost Ping")
                    .setDescription(message.getContent())
                    .setTimestampToNow();

            ServerTextChannel internalAffairsChannel = messageDeleteEvent.getApi().getServerTextChannelById("1129269346015907912").get();
            internalAffairsChannel.sendMessage(embedBuilder);
        }

    }
}
