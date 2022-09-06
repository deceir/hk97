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
import org.javacord.api.entity.permission.PermissionState;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
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

        String token = Config.discordToken;
        DiscordApi api = new DiscordApiBuilder().setToken(token)
                .setAllNonPrivilegedIntents()
                .login()
                .join();

        Optional<Server> server = api.getServerById("1016240494948397066");
        Optional<ChannelCategory> interviewCategory = api.getChannelCategoryById("1016487611780571156");

        if (messageCreateEvent.getChannel().getIdAsString().equals("1016449238567223406")) {
            if (messageCreateEvent.getMessageContent().startsWith("+apply")) {

                if(interviewRepository.findById(messageCreateEvent.getMessageAuthor().getId()).isPresent()) {
                    messageCreateEvent.getChannel().sendMessage("Error. You are only allowed a single interview room. Please check your existing room.");
                    return;
                }

                String messageContent = messageCreateEvent.getMessageContent();
                messageContent = messageContent.replace(" ", ",");
                List<String> list = Arrays.asList(messageContent.split(","));

                if (list.size() != 2) {
                    messageCreateEvent.getChannel().sendMessage("Error. Please enter only your nation id.");

                } else {
                    int nationId = 0;

                    try {

                        nationId = Integer.parseInt(list.get(1));

                    } catch (Exception e) {
                        messageCreateEvent.getChannel().sendMessage("Error. Please enter only your nation id.");
                        e.printStackTrace();

                    }

                    PoliticsAndWar pnw = new PoliticsAndWarBuilder().setApiKey(Config.itachiPnwKey).build();

                    try {
                        Nation nation = pnw.getNation(nationId);


                        List<ServerTextChannel> listOfChannels = server.get().getTextChannelsByName(nation.getNationid() + "-" + nation.getName());

                        boolean channelExists = false;

                        for (ServerTextChannel channel : listOfChannels) {
                            if (channel.getName().equals(nation.getNationid() + "-" + nation.getName())) {
                                channelExists = true;
                            }
                        }



                        Permissions permissions = new PermissionsBuilder().setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).build();


                        Channel interview = new ServerTextChannelBuilder(server.get())
                                .setName(nation.getNationid() + "-" + nation.getName())
                                .addPermissionOverwrite(server.get().getEveryoneRole(), new PermissionsBuilder().setDenied(PermissionType.READ_MESSAGES).build())
                                .addPermissionOverwrite(messageCreateEvent.getMessageAuthor().asUser().get(), new PermissionsBuilder().setAllowed(PermissionType.READ_MESSAGES).build())
                                .addPermissionOverwrite(server.get().getRoleById("1016528192447717407").get(), new PermissionsBuilder().setAllowed(PermissionType.READ_MESSAGES).build())
                                .addPermissionOverwrite(server.get().getRoleById("1016448673825161327").get(), new PermissionsBuilder().setAllowed(PermissionType.READ_MESSAGES).build())


                                .setCategory(interviewCategory.get()).create().join();


//                        File file = new File(interview.asTextChannel().get().getIdAsString() + ".txt");
//                        if (!file.exists()) {
//                            file.createNewFile();
//                        } else {
//                            System.out.println("File already exists.");
//
//                        }

                        messageCreateEvent.getChannel().sendMessage("<@" + messageCreateEvent.getMessageAuthor().getIdAsString() + "> has been allotted an interview room.");


                        EmbedBuilder eb = new EmbedBuilder()
                                .setAuthor(messageCreateEvent.getMessageAuthor())
                                .setDescription("[" + nation.getName() + " led by  " + nation.getLeadername() + "](https://politicsandwar.com/nation/id=" + nation.getNationid() + ")")
                                .addInlineField("City Count", nation.getCities() + "")
                                .addInlineField("Score", nation.getScore() + "")
                                .addInlineField("Alliance", nation.getAlliance())
                                .addField("Militarization", ":military_helmet: " + nation.getSoldiers() + " :bus: " + nation.getTanks() + " :airplane: " + nation.getAircraft() + " :ship: " + nation.getShips())
                                .addField("WMD's", ":rocket: " + nation.getMissiles() + " :radioactive: " + nation.getNukes())

                                .setFooter("HK-97 Internal Command");

                        interview.asTextChannel().get().sendMessage(eb);
                        TimeUnit.SECONDS.sleep(2);


                        interview.asTextChannel().get().sendMessage("Thank you for applying, <@" + messageCreateEvent.getMessageAuthor().getId() + ">! Please refrain from declaring any offensive wars at this time! Let us know when you are ready to begin!");


                        Interview interview1 = new Interview();
                        interview1.setId(messageCreateEvent.getMessageAuthor().getId());
                        interview1.setActive(true);
                        interview1.setChannelId(interview.getId());
                        interviewRepository.save(interview1);


                    } catch (IOException e) {
                        messageCreateEvent.getChannel().sendMessage("Error. Please enter only your nation id.");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

        }

    }

}

