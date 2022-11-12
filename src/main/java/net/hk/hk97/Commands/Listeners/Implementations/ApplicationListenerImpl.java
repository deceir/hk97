package net.hk.hk97.Commands.Listeners.Implementations;

import net.hk.hk97.Commands.Listeners.ApplicationListener;
import net.hk.hk97.Repositories.InterviewRepository;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.permission.*;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class ApplicationListenerImpl implements ApplicationListener {

    @Autowired
    InterviewRepository interviewRepository;

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {

        DiscordApi api = messageCreateEvent.getApi();

        Optional<Server> server = api.getServerById("1016240494948397066");
        Optional<ChannelCategory> interviewCategory = api.getChannelCategoryById("1016487611780571156");

        if (messageCreateEvent.getChannel().getIdAsString().equals("1016449238567223406")) {

            Role govRole = api.getRoleById("1016448673825161327").get();

            List<Role> userRoles = messageCreateEvent.getMessageAuthor().asUser().get().getRoles(server.get());

            Boolean isGov = false;

            for (Role role : userRoles) {
                if (role.getIdAsString().equals(govRole.getIdAsString())) {
                    isGov = true;
                }
            }

            if (!isGov && (!messageCreateEvent.getMessageAuthor().getIdAsString().equals("1004820381586178058"))) {
                messageCreateEvent.getMessage().delete();
            }

        }

    }

}




