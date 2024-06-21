package net.hk.hk97.Commands.SlashCommands.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

public class OptIn {

    public static void optInRoles(SlashCommandInteraction interaction) {
        if (interaction.getCommandName().equalsIgnoreCase("optin")) {
            System.out.println("optin command active");
            boolean hasCommunityRole = false;
            Role communityRole = interaction.getApi().getRoleById("1241901191823102004").get();

            Server server = interaction.getServer().get();
            List<Role> roles = interaction.getUser().getRoles(server);
            for (Role role : roles) {
                System.out.println("role: " + role.getName());
                if (role.getIdAsString().equals("1241901191823102004")) {
                    hasCommunityRole = true;
                }
            }

            if (hasCommunityRole) {
                System.out.println("trying to remove");
                interaction.getUser().removeRole(communityRole);
                interaction.createFollowupMessageBuilder().setContent("Community Role has been removed.").send();
            } else {
                System.out.println("trying to add");
                interaction.getUser().addRole(communityRole);
                interaction.createFollowupMessageBuilder().setContent("Community Role has been added.").send();
            }
        }
    }
}
