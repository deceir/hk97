package net.hk.hk97.Commands.TextCommands.Commands;

import net.hk.hk97.Models.Nation;
import net.hk.hk97.Models.WarRange;
import net.hk.hk97.Services.Util.CounterUtil;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class CounterCommand {

    public static void Counter(MessageCreateEvent messageCreateEvent) {

        if (messageCreateEvent.getChannel().getIdAsString().equals("1016449238567223406")) {
            System.out.println("Invalid channel.");
        } else {
            String messageContent = messageCreateEvent.getMessageContent();
            messageContent = messageContent.replace(" ", ",");
            List<String> list = Arrays.asList(messageContent.split(","));


            if (list.size() == 2) {

                try {
                    System.out.println(list.get(1));
                    long score = Long.parseLong(list.get(1));

                    WarRange wr = new WarRange();
                    wr.calculateRange(score);
                    long min = wr.getMin_def();
                    long max = wr.getMax_def();
                    wr.formatScore();

                    Message msg = messageCreateEvent.getMessage().getChannel().sendMessage("Max Range: " + wr.getMax_def_f() + "\nMin Range: " + wr.getMin_def_f()).get();

                    //calling to get list of nations
                    List<Nation> nations = CounterUtil.getCounters(max, min);

                    int endIndex = Math.min(10, nations.size());
                    List<Nation> sublist = nations.subList(0, endIndex);

                    String str = "";

                    for (Nation nation : sublist) {
                        str += "[" + nation.getLeader() + " of " + nation.getNation() + "](https://politicsandwar.com/nation/id=" + nation.getId() + ") (" + nation.getId() + ") " + nation.getCities() + " cities, " + nation.getScore() + "\n";
                    }

                    if (str.isEmpty()) {
                        str = "No nations in range.";
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("Counters Command")
                            .setAuthor(messageCreateEvent.getMessageAuthor())
                            .setColor(Color.CYAN)
                            .setDescription("Top ten potential counters for the given score.")
                            .addField("Counters", str);


                    msg.reply(embedBuilder);


                } catch (Exception e) {
                    messageCreateEvent.getMessage().getChannel().sendMessage("You have entered this command incorrectly.\nThe command should be `+counter <score>`.");
                }

            } else {
                messageCreateEvent.getMessage().getChannel().sendMessage("You have entered this command incorrectly.\nThe command should be `+counter <score>`.");
            }
        }
    }
}
