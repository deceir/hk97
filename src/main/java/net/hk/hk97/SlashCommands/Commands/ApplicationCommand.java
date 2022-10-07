package net.hk.hk97.SlashCommands.Commands;

import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.PoliticsAndWarBuilder;
import io.github.adorableskullmaster.pw4j.domains.Nation;
import net.hk.hk97.Config;
import net.hk.hk97.Models.Interview;
import net.hk.hk97.Repositories.InterviewRepository;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ApplicationCommand {

    public static void application(SlashCommandInteraction interaction, InterviewRepository interviewRepository) throws ExecutionException, InterruptedException {

        Optional<TextChannel> channel = interaction.getChannel();

        if (channel.get().getIdAsString().equals("1016465107468947559") || channel.get().getIdAsString().equalsIgnoreCase("1016449238567223406")) {

            if (interviewRepository.findById(interaction.getUser().getId()).isPresent()) {
                interaction.createImmediateResponder().setContent("Error. You are only allowed a single interview room. Please check your existing room.").setFlags(MessageFlag.EPHEMERAL).respond();
                return;
            }


            int nationId = Integer.parseInt(interaction.getOptionLongValueByName("id").get() + "");


            DiscordApi api = interaction.getApi();

            ChannelCategory interviewCategory = api.getChannelCategoryById(Config.applicationsChannelId).get();


            Optional<Server> server = api.getServerById(Config.mainServerId);

            PoliticsAndWar pnw = new PoliticsAndWarBuilder().setApiKey(Config.itachiPnwKey).build();


            Nation nation = null;

            try {
                nation = pnw.getNation(nationId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            Channel interview = new ServerTextChannelBuilder(server.get())
                    .setName(nation.getNationid() + "-" + nation.getName())
                    .addPermissionOverwrite(server.get().getEveryoneRole(), new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL).build())
                    .addPermissionOverwrite(interaction.getUser(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                    .addPermissionOverwrite(server.get().getRoleById("1016528192447717407").get(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                    .addPermissionOverwrite(server.get().getRoleById("1016448673825161327").get(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                    .setCategory(interviewCategory).create().join();

            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor(interaction.getUser())
                    .setDescription("[" + nation.getName() + " led by  " + nation.getLeadername() + "](https://politicsandwar.com/nation/id=" + nation.getNationid() + ")")
                    .addInlineField("City Count", nation.getCities() + "")
                    .addInlineField("Score", nation.getScore() + "")
                    .addInlineField("Alliance", nation.getAlliance())
                    .addField("Militarization", ":military_helmet: " + nation.getSoldiers() + " :bus: " + nation.getTanks() + " :airplane: " + nation.getAircraft() + " :ship: " + nation.getShips())
                    .addField("WMD's", ":rocket: " + nation.getMissiles() + " :radioactive: " + nation.getNukes())
                    .setColor(Color.CYAN)
                    .setFooter("Necron Internal Command");

            interview.asTextChannel().get().sendMessage(eb).get().pin();
            MessageSet list = interview.asServerTextChannel().get().getMessages(0).get();
            list.getOldestMessage().get().pin();

            interaction.createFollowupMessageBuilder().setContent("Your application room has been created, <@" + interaction.getUser().getIdAsString() + ">. <#" + interview.getIdAsString() + ">").setFlags(MessageFlag.EPHEMERAL).send();

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            interview.asTextChannel().get().sendMessage("Hello and thank you for applying, <@" + interaction.getUser().getId() + ">! Please refrain from declaring any offensive wars at this time. Let us know when you are ready to begin the interview.");

            Interview interview1 = new Interview();
            interview1.setId(interaction.getUser().getId());
            interview1.setActive(true);
            interview1.setChannelId(interview.getId());
            interviewRepository.save(interview1);


        } else {
            interaction.createImmediateResponder().setContent("This command cannot be used in this channel.").setFlags(MessageFlag.EPHEMERAL).respond();
        }

    }
}
