package net.hk.hk97.Commands.SlashCommands.Commands;

import net.hk.hk97.Models.Stats.NationRevenue;
import net.hk.hk97.Models.User;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Services.Util.StatsUtil;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StatsCommand {

    public static void getStats(SlashCommandInteraction interaction, UserRepository userRepository) throws IOException, JSONException {

        try {

            NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);

            List<NationRevenue> list = StatsUtil.getAllianceNationRevenue(interaction.getOptionByName("aarev").get().getOptionLongValueByName("id").get());
            String aaName = StatsUtil.getAaName(interaction.getOptionByName("aarev").get().getOptionLongValueByName("id").get());

            File logfile = new File(interaction.getOptionByName("aarev").get().getOptionLongValueByName("id").get() + "-" + aaName + "-rev" + ".txt");

            FileWriter fw = new FileWriter(logfile.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(aaName + " Revenue Rankings");
            bw.newLine();
            int rank = 1;


            for(int i = list.size()-1; i >= 0; i--) {
                NationRevenue revenue = list.get(i);
                System.out.println("Appending " + revenue.getName());
                bw.append(rank + ". " + revenue.getName() + " / " + revenue.getLeader() + ": " + n.format((revenue.getRevenue() / 365)));
                bw.newLine();
                rank++;
            }
            bw.close();

            System.out.println("Sending file...");
            interaction.createFollowupMessageBuilder().addAttachment(logfile).send();
            TimeUnit.SECONDS.sleep(5);

            logfile.delete();
        } catch (Exception e) {
            interaction.createFollowupMessageBuilder().setContent("There was an error executing that command.\n" + e);
        }

    }
}
