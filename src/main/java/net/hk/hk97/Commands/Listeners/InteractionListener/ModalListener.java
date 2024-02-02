package net.hk.hk97.Commands.Listeners.InteractionListener;

import net.hk.hk97.Config;
import net.hk.hk97.Models.Interview;
import net.hk.hk97.Models.Military;
import net.hk.hk97.Repositories.InterviewRepository;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.listener.interaction.ModalSubmitListener;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Component
public class ModalListener implements ModalSubmitListener {

    @Autowired
    InterviewRepository interviewRepository;

    @Override
    public void onModalSubmit(ModalSubmitEvent modalSubmitEvent) {
        System.out.println("Modal response detected...");
        String modalId = modalSubmitEvent.getModalInteraction().getCustomId();

        System.out.println(modalId);

        switch (modalId) {
            case "applicationModal":

                String response = modalSubmitEvent.getModalInteraction().getTextInputValueByCustomId("nationId").get().trim();

                Military nation = null;
                try {
                    try {
                        nation = MilUtil.getNationMilitary(Integer.parseInt(response));
                    } catch (NumberFormatException exception) {
                        modalSubmitEvent.getModalInteraction().createImmediateResponder().setContent("You entered your nation ID incorrectly. Only include the nation ID in number format.").setFlags(MessageFlag.EPHEMERAL).respond();
                    }

                    DiscordApi api = modalSubmitEvent.getApi();

                    if (nation.getNation_name() == null) {
                        modalSubmitEvent.getModalInteraction().createImmediateResponder().setContent("You entered your nation ID incorrectly. Only include the nation ID in number format.").setFlags(MessageFlag.EPHEMERAL).respond();
                    } else {

                        if (interviewRepository.findById(modalSubmitEvent.getModalInteraction().getUser().getId()).isPresent()) {

                            modalSubmitEvent.getModalInteraction().createImmediateResponder().setContent("Only one interview is allowed, please return to your original interview room.").setFlags(MessageFlag.EPHEMERAL).respond();

                        } else {
                            modalSubmitEvent.getModalInteraction().createImmediateResponder().setContent("Application room being created...").setFlags(MessageFlag.EPHEMERAL).respond();

                            System.out.println("Attempting to get interview category...");
                            ChannelCategory interviewCategory = api.getChannelCategoryById(Config.applicationsCategoryId).get();
                            System.out.println("Successfully retrieved interview category.");

                            Optional<Server> server = api.getServerById(Config.mainServerId);

                            Channel interview = new ServerTextChannelBuilder(server.get())
                                    .setName(nation.getId() + "-" + nation.getNation_name())
                                    .addPermissionOverwrite(server.get().getEveryoneRole(), new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL).build())
                                    .addPermissionOverwrite(modalSubmitEvent.getModalInteraction().getUser(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                                    .addPermissionOverwrite(server.get().getRoleById(Config.mainServerGovId).get(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                                    .addPermissionOverwrite(server.get().getRoleById(Config.mainserverIaRoleId).get(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                                    .addPermissionOverwrite(server.get().getRoleById(Config.mainServerHkRole).get(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                                    .setCategory(interviewCategory).create().join();

                            EmbedBuilder eb = new EmbedBuilder()
                                    .setAuthor(modalSubmitEvent.getModalInteraction().getUser())
                                    .setDescription("[" + nation.getNation_name() + " led by  " + nation.getLeader_name() + "](https://politicsandwar.com/nation/id=" + nation.getId() + ")")
                                    .addInlineField("City Count", nation.getCities() + "")
                                    .addInlineField("Score", nation.getScore() + "")
                                    .addInlineField("Vacation Mode:", nation.isVmode() + "")
                                    .addInlineField("Alliance:", nation.getAaname() + "")
                                    .addField("Militarization", ":military_helmet: " + nation.getSoldiers() + " :bus: " + nation.getTanks() + " :airplane: " + nation.getJets() + " :ship: " + nation.getShips())
                                    .addField("WMD's", ":rocket: " + nation.getMissiles() + " :radioactive: " + nation.getNukes())
                                    .setColor(Color.CYAN)
                                    .setFooter("Necron Internal Command");

                            try {
                                interview.asTextChannel().get().sendMessage(eb).get().pin();
                                MessageSet list = interview.asServerTextChannel().get().getMessages(0).get();
                                list.getOldestMessage().get().pin();


                                modalSubmitEvent.getModalInteraction().getUser().sendMessage("Your application room has been created, <@" + modalSubmitEvent.getModalInteraction().getUser().getIdAsString() + ">. <#" + interview.getIdAsString() + ">");

                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }

                            interview.asTextChannel().get().sendMessage("Hello and thank you for applying, <@" + modalSubmitEvent.getModalInteraction().getUser().getId() + ">! Please refrain from declaring any new offensive wars at this time. Let us know when you are ready to begin the interview. \n<@&590651219215646731>");

                            try {
                                Interview interview1 = new Interview();
                                interview1.setId(modalSubmitEvent.getModalInteraction().getUser().getId());
                                interview1.setActive(true);
                                interview1.setChannelId(interview.getId());
                                interviewRepository.save(interview1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (JSONException e) {
                    modalSubmitEvent.getModalInteraction().createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).setContent("You entered your nation ID incorrectly. Only include the nation ID in number format.").respond();
                }

                break;
        }
    }
}
