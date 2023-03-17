package net.hk.hk97.Commands.TextCommands.Commands;

import net.hk.hk97.Models.WarRange;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MilScoreCommand {

    public static void MilScore(MessageCreateEvent messageCreateEvent) {

        EmbedBuilder badArg = new EmbedBuilder()
                .setAuthor(messageCreateEvent.getMessageAuthor())
                .setTitle("Incorrect format.")
                .setColor(Color.CYAN)
                .setDescription("You have entered the command improperly. \n The correct format is: \n +range <score>");

        String messageContent = messageCreateEvent.getMessageContent();
        messageContent = messageContent.replace(" ", ",");
        List<String> list = Arrays.asList(messageContent.split(","));

        if (list.size() < 2) {
            messageCreateEvent.getChannel().sendMessage(badArg);

        } else if (list.size() > 2) {
            messageCreateEvent.getChannel().sendMessage(badArg);
        } else {

            String scoreString = list.get(1);
            try {
                long score = Long.parseLong(scoreString);

                WarRange wr = new WarRange();
                wr.calculateRange(score);
                wr.formatScore();

                EmbedBuilder range = new EmbedBuilder()
                        .setAuthor(messageCreateEvent.getMessageAuthor())
                        .setColor(Color.CYAN)
                        .setTitle("War range for " + score + " has been calculated.")
                        .addField("Offensive Range", wr.getMin_dec_f() + " to " + wr.getMax_dec_f())
                        .addField("Defensive Range", wr.getMin_def_f() + " to " + wr.getMax_def_f())
                        .addField("Spy Range", wr.getMin_offspy_f() + " to " + wr.getMax_offspy_f());

                messageCreateEvent.getChannel().sendMessage(range);
            } catch (Exception e) {
                messageCreateEvent.getChannel().sendMessage(badArg);
            }

        }
    }

    public static void troopScores(MessageCreateEvent messageCreateEvent) {
        EmbedBuilder troopScore = new EmbedBuilder()
                .setTitle("Mil Scores")
                .setAuthor(messageCreateEvent.getMessageAuthor())
                .setColor(Color.CYAN)
                .addInlineField("Score Per Unit", "0.0004 per soldier \n0.025 per tank \n0.3 per plane \n1 per ship")
                .addInlineField("Units Per 10 Score", "25,000 soldiers \n400 tanks \n33.3 planes \n10 ships")
                .addInlineField("Units Per 100 Score", "250,000 soldiers \n4,000 tanks \n330 planes \n100 ships");

        messageCreateEvent.getChannel().sendMessage(troopScore);
    }
}
