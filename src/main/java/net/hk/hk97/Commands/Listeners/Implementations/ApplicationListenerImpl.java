package net.hk.hk97.Commands.Listeners.Implementations;

import net.hk.hk97.Commands.Listeners.ApplicationListener;
import net.hk.hk97.Config;
import net.hk.hk97.Repositories.InterviewRepository;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.permission.*;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


public class ApplicationListenerImpl implements ApplicationListener {


    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {

        if (messageCreateEvent.isPrivateMessage() && messageCreateEvent.getMessageAuthor().isBotOwner() && messageCreateEvent.getMessageContent().equals("+interviewSetup")) {


            TextChannel channel = messageCreateEvent.getApi().getTextChannelById(Config.applicationsChannelId).get();

            new MessageBuilder()
                    .setContent("__**The Golden Horde**__ is home only to warriors, united by the single purpose of expanding the dominion of The Golden Horde and bringing glory and honor to the Great Khan.\nOnly those willing to fight to their final pixel should apply.")
                    .addComponents(
                            ActionRow.of(Button.success("applicationButton", "Apply Now"))
                    ).send(channel);
        }

    }

}




