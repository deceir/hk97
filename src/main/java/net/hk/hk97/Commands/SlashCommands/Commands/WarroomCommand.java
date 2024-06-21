package net.hk.hk97.Commands.SlashCommands.Commands;

import net.hk.hk97.Config;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WarroomRepository;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.RegularServerChannelUpdater;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.Arrays;

public class WarroomCommand {

    public static void getWarroomCommands(SlashCommandInteraction interaction, UserRepository userRepository, WarroomRepository warroomRepository) {

        if (interaction.getCommandName().equalsIgnoreCase("warroom")) {

            if (warroomRepository.findWarroomByChannelid(interaction.getChannel().get().getIdAsString()) != null) {

                if (interaction.getOptionByName("add").isPresent()) {
                    Channel channel = interaction.getChannel().get();
                    channel.asServerChannel().get().asServerTextChannel().get();
                    RegularServerChannelUpdater scu = channel.asServerTextChannel().get().createUpdater();

                    org.javacord.api.entity.user.User user = interaction.getOptionByName("add").get().getOptionUserValueByName("member").get();


                    Permissions permissions = new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build();


                    scu.addPermissionOverwrite(user, permissions);
                    scu.update();

                    interaction.createFollowupMessageBuilder().setContent(user.getMentionTag() + " has been added to the war room.").send();
                } else if (interaction.getOptionByName("remove").isPresent()) {
                    Channel channel = interaction.getChannel().get();
                    channel.asServerChannel().get().asServerTextChannel().get();
                    RegularServerChannelUpdater scu = channel.asServerTextChannel().get().createUpdater();

                    org.javacord.api.entity.user.User user = interaction.getOptionByName("remove").get().getOptionUserValueByName("member").get();


                    Permissions permissions = new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL).build();


                    scu.addPermissionOverwrite(user, permissions);
                    scu.update();

                    interaction.createFollowupMessageBuilder().setContent(user.getMentionTag() + " has been removed from the war room.").send();
                }


            } else {
                interaction.createFollowupMessageBuilder().setContent("This is not in a valid war room.").send();
            }

        }
    }
}
