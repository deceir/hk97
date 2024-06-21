package net.hk.hk97.Commands.TextCommands;

import net.hk.hk97.Commands.TextCommands.Commands.CounterCommand;
import net.hk.hk97.Commands.TextCommands.Commands.MilScoreCommand;
import net.hk.hk97.Models.WarRange;
import net.hk.hk97.Services.Util.Mutations.WithdrawalMutationService;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.json.JSONException;
import org.springframework.stereotype.Component;
import org.w3c.dom.css.Counter;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Component
public class TextCommandHandler implements MessageCreateListener {

    final String prefix = "+";

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (messageCreateEvent.getMessageContent().startsWith(prefix)) {
            String msg = messageCreateEvent.getMessageContent();

            if (msg.startsWith(prefix + "range")) {
                MilScoreCommand.MilScore(messageCreateEvent);
            } else if (msg.startsWith(prefix + "info score")) {
                MilScoreCommand.troopScores(messageCreateEvent);
            } else if (msg.startsWith(prefix + "counter")) {
                CounterCommand.Counter(messageCreateEvent);
            }
//            else if (msg.startsWith(prefix + "mut-test") && messageCreateEvent.getMessageAuthor().isBotOwner()) {
//                try {
//                    WithdrawalMutationService.bankWithdrawal();
//                } catch (JSONException e) {
//                    messageCreateEvent.getMessage().reply("That errored out... " + e.getStackTrace());
//                }
//            }

        }

    }

}

