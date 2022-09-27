package net.hk.hk97.bank;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.w3c.dom.Text;

import java.nio.channels.Channel;

public class BankButtonListener implements ButtonClickListener {

    @Override
    public void onButtonClick(ButtonClickEvent event) {

        Interaction interaction = event.getInteraction();


        if (event.getButtonInteraction().getCustomId().equals("deposit")) {

            event.getInteraction().respondLater();

            DiscordApi api = event.getApi();

            event.getInteraction().createFollowupMessageBuilder().setContent("Your deposit has been registered!").setFlags(MessageFlag.EPHEMERAL).send();
        } else if (event.getButtonInteraction().getCustomId().equals("withdraw")) {
            DiscordApi api = event.getApi();

//            api.addMessageComponentCreateListener(event1 -> {
//                event1.getInteraction().respondWithModal("withdrawmodal", "Bank Withdrawal Form",
//                        ActionRow.of(TextInput.create("cash", "Cash from deposits", TextInputStyle.SHORT)));
//            });

//            ButtonInteraction buttonInteraction = event.getButtonInteraction();
//            buttonInteraction.respondWithModal("bankmodal", "Support Key",
//                    ActionRow.of(TextInput.create("Your Support Key", "TEXT_FIELD_SUPPORT_KEY", TextInputStyle.SHORT))
//            );
        }

    }
}

