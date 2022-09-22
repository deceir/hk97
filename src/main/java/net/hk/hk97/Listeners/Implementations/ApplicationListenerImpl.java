package net.hk.hk97.Listeners.Implementations;

import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.PoliticsAndWarBuilder;
import io.github.adorableskullmaster.pw4j.domains.Nation;
import net.hk.hk97.Config;
import net.hk.hk97.Interview;
import net.hk.hk97.Listeners.ApplicationListener;
import net.hk.hk97.Repositories.InterviewRepository;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.*;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.Permission;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


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




