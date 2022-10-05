package net.hk.hk97.SlashCommands.Commands;

import net.hk.hk97.Models.Message.Messenger;
import net.hk.hk97.Models.User;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

public class AccountCommand {
    public static void account(SlashCommandInteraction interaction, UserRepository userRepository) {
        if (interaction.getOptionByName("register").isPresent()) {
            //initial registration

            SlashCommandInteractionOption option = interaction.getOptionByName("register").get();

            if (userRepository.findById(interaction.getUser().getIdAsString()).isPresent()) {
                interaction.createFollowupMessageBuilder().setContent("An account with your discord id already exists. Verify using `/account verify` if you have not already, otherwise your account is completed!").send();
            } else {

                try {
                    String leaderName = MilUtil.getLeaderName(option.getOptionLongValueByName("id").get());
                    User user = new User();
                    user.setName(interaction.getUser().getName());
                    user.setDiscordid(interaction.getUser().getIdAsString());
                    user.setNationid(option.getOptionLongValueByName("id").get());
                    userRepository.save(user);

                    try {
                        Messenger.sendMessage(option.getOptionLongValueByName("id").get(), "Necron Verification", ("Your Necron verification code is: " + user.getVerification()));

                        interaction.createFollowupMessageBuilder().setContent("Your account has been created. \nA DM with your verification code has been sent in-game. Use that token with `/account verify` to complete your registration.").send();
                    } catch (Exception e) {
                        interaction.createFollowupMessageBuilder().setContent("There was an error sending your code in-game.").send();
                    }
                } catch (Exception e) {
                    interaction.createFollowupMessageBuilder().setContent("The id you have entered has returned invalid. Please try again with the correct id.").send();
                }
            }
        } else if (interaction.getOptionByName("verify").isPresent()) {
            //using token
            SlashCommandInteractionOption option = interaction.getOptionByName("verify").get();
            User user = userRepository.findById(interaction.getUser().getIdAsString()).get();
            if (option.getOptionLongValueByName("token").get() == user.getVerification()) {
                user.setRegistered(true);
                userRepository.save(user);
                interaction.createFollowupMessageBuilder().setContent("You have successfully verified your account.").send();
            } else {
                interaction.createFollowupMessageBuilder().setContent("Your token is incorrect.").send();
            }

        }

    }
}
