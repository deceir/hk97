package net.hk.hk97.Commands.SlashCommands.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;

import javax.swing.*;

public class BankButtonsCommand {

    public static void getBankButtons(SlashCommandInteraction interaction) {
        String title = "__**TGH Banking**__";

        new MessageBuilder()
                .setContent(title)
                .addComponents(
                        ActionRow.of(Button.success("bankBalance", "Balance"),Button.success("bankDeposit", "Deposit"), Button.success("payloan", "Loan Deposit") )

                ).send(interaction.getChannel().get());

    }

    public static void getBankButtons(ButtonClickEvent interaction) {
        String title = "__**TGH Banking**__";

        new MessageBuilder()
                .setContent(title)
                .addComponents(
                        ActionRow.of(Button.success("bankBalance", "Balance"),Button.success("bankDeposit", "Deposit"), Button.success("payloan", "Loan Deposit") )


                ).send(interaction.getInteraction().getChannel().get());

    }
}
