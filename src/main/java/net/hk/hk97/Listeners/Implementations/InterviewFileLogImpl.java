package net.hk.hk97.Listeners.Implementations;

import net.hk.hk97.Config;
import net.hk.hk97.Interview;
import net.hk.hk97.Listeners.InterviewFileLogListener;
import net.hk.hk97.Repositories.InterviewRepository;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class InterviewFileLogImpl implements InterviewFileLogListener {

    @Autowired
    InterviewRepository interviewRepository;

    String token = Config.discordToken;

//    @Value("discord-api")
//    DiscordApi api;

    DiscordApi api = new DiscordApiBuilder().setToken(token)
            .setAllNonPrivilegedIntents()
            .login()
            .join();

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {



        Optional<ChannelCategory> interviewCategory = api.getChannelCategoryById("1016487611780571156");


        if (messageCreateEvent.getServerTextChannel().get().getCategory() == interviewCategory) {

            Optional<Server> server = api.getServerById("1016240494948397066");
            Optional<Channel> loggingChannel = api.getChannelById("1016487721646166106");
            Role ia = api.getRoleById("1016479793648250940").get();

            try {
                long channelId = messageCreateEvent.getChannel().getId();
                boolean needsLogging = false;

                for (ServerChannel channel : interviewCategory.get().getChannels()) {
                    if (channel.getId() == channelId) {
                        needsLogging = true;
                    }
                }

                File file = new File((channelId) + ".txt");

                if (needsLogging) {


                    String content = messageCreateEvent.getMessageAuthor().getDisplayName() + ": " + messageCreateEvent.getMessageContent();

//                FileWriter fw = new FileWriter(file.getAbsoluteFile());
//                BufferedWriter bw = new BufferedWriter(fw);
//                bw.append(content);
//                bw.close(); // Be sure to close BufferedWriter
//                System.out.println("Logged: " + content);

                }


                boolean isIA = false;
                List<Role> roleList = messageCreateEvent.getMessageAuthor().asUser().get().getRoles(server.get());
                for (Role role : roleList) {
                    if (role.getIdAsString().equalsIgnoreCase("1016528192447717407") || role.getIdAsString().equals("1016448673825161327")) {
                        isIA = true;
                    }
                }

                if (messageCreateEvent.getMessageContent().equalsIgnoreCase("+int end") && isIA) {

//                File logfile = new File((channelId)+".txt");
//
//                loggingChannel.get().asTextChannel().get().sendMessage(logfile);
//
//                if (file.exists()) {
//                    file.delete();
//                }

                    messageCreateEvent.getChannel().sendMessage("Deleting channel soon.");
                    Channel channel = api.getChannelById(messageCreateEvent.getChannel().getId()).get();
                    ServerChannel serverChannel = api.getServerChannelById(messageCreateEvent.getChannel().getId()).get();

                    List<Message> list = channel.asServerTextChannel().get().getMessagesAsStream().collect(Collectors.toList());

                    File logfile = new File(messageCreateEvent.getChannel().getIdAsString() + ".txt");

                    FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    for (int i = list.size() - 1; i >= 0; i--) {

                        Message message = list.get(i);

                        String content = message.getCreationTimestamp() + " " + message.getAuthor().getDisplayName() + ": " + message.getContent() + "\n";

                        bw.write(content);


                    }
                    bw.close(); // Be sure to close BufferedWriter

                    loggingChannel.get().asServerTextChannel().get().sendMessage("<@" + interviewRepository.findInterviewByChannelId(channelId).getId() + "> interview: ");
                    loggingChannel.get().asServerTextChannel().get().sendMessage(logfile);

                    TimeUnit.SECONDS.sleep(5);

                    //deleting server
                    channel.asServerTextChannel().get().delete();
                    //deleting file
                    logfile.delete();
                    Interview interview = interviewRepository.findInterviewByChannelId(channelId);
                    interviewRepository.delete(interview);
                }


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
