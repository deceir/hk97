package net.hk.hk97.Commands.Listeners.Implementations;

import net.hk.hk97.Commands.Listeners.InterviewFileLogListener;
import net.hk.hk97.Config;
import net.hk.hk97.Models.Interview;
import net.hk.hk97.Repositories.InterviewRepository;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
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



    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {


        DiscordApi api = messageCreateEvent.getApi();

//        Optional<ChannelCategory> interviewCategory = api.getChannelCategoryById("1016487611780571156");


        if (messageCreateEvent.getServerTextChannel().get().getCategory().get().getName().equalsIgnoreCase("internal affairs")) {

            Optional<Server> server = api.getServerById(Config.mainServerId);
            Optional<Channel> loggingChannel = api.getChannelById("1128045466878226503");
            Role ia = api.getRoleById(Config.mainserverIaRoleId).get();


            try {
                long channelId = messageCreateEvent.getChannel().getId();
                boolean needsLogging = false;

//                for (ServerChannel channel : interviewCategory.get().getChannels()) {
//                    if (channel.getId() == channelId) {
//                        needsLogging = true;
//                    }
//                }


//                File file = new File((channelId) + ".txt");
                File file = new File(messageCreateEvent.getServerTextChannel().get().getName() + ".txt");

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
                    if (role.getIdAsString().equals(Config.mainServerGovId) || role.getIdAsString().equals(Config.mainServerLowGovId) || role.getIdAsString().equals("404940752691396608") || role.getIdAsString().equals("404941145966116874")){
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


                    List<Message> list = messageCreateEvent.getServerTextChannel().get().getMessagesAsStream().collect(Collectors.toList());

                    File logfile = new File(messageCreateEvent.getServerTextChannel().get().getName() + ".txt");

                    FileWriter fw = new FileWriter(logfile.getAbsoluteFile(), true);
                    BufferedWriter bw = new BufferedWriter(fw);



                    for (int i = list.size() - 1; i >= 0; i--) {

                        Message message = list.get(i);

                        String content = message.getAuthor().getDisplayName() + ": " + message.getContent() + "\n";

                        bw.append(content);
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
