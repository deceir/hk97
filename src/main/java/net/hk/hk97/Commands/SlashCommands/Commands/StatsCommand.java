package net.hk.hk97.Commands.SlashCommands.Commands;

import net.dv8tion.jda.api.events.ExceptionEvent;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StatsCommand {

    public static void getStats(SlashCommandInteraction interaction, UserRepository userRepository) throws IOException, JSONException, InterruptedException {

        if (interaction.getOptionByName("aarev").isPresent()) {
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


                for (int i = list.size() - 1; i >= 0; i--) {
                    NationRevenue revenue = list.get(i);
                    System.out.println("Appending " + revenue.getName());
                    bw.append(rank + ". " + revenue.getName() + " / " + revenue.getLeader() + ": " + n.format((revenue.getRevenue() / 365))  + " C:" + revenue.getCities());
                    bw.newLine();
                    rank++;
                }
                bw.close();
                System.out.println("revenue completed... starting per city");

                interaction.createFollowupMessageBuilder().addAttachment(logfile).send();


//                File logfilePerCity = new File(interaction.getOptionByName("aarev-percity").get().getOptionLongValueByName("id").get() + "-" + aaName + "-rev" + ".txt");
//                FileWriter fileWriter = new FileWriter(logfilePerCity.getAbsoluteFile(), true);
//                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//                bufferedWriter.append(aaName + " Revenue Rankings");
//                bufferedWriter.newLine();
//                int rankPerCity = 1;
//
//                for (int i = list.size() - 1; i >= 0; i--) {
//                    NationRevenue revenue = list.get(i);
//                    System.out.println("Appending " + revenue.getName());
//                    bufferedWriter.append(rankPerCity + ". " + revenue.getName() + " / " + revenue.getLeader() + ": " + n.format(((revenue.getRevenue() / 365) / revenue.getCities())));
//                    bufferedWriter.newLine();
//                    rankPerCity++;
//                }
//                bufferedWriter.close();
//                System.out.println("per city revenue completed.");
//
//                System.out.println("Sending file...");
//                interaction.createFollowupMessageBuilder().addAttachment(logfilePerCity).send();
                TimeUnit.SECONDS.sleep(5);
                logfile.delete();
//                logfilePerCity.delete();
            } catch (Exception e) {
                interaction.createFollowupMessageBuilder().setContent("There was an error executing that command.\n" + e);
            }

        } else if (interaction.getOptionByName("aarevbycity").isPresent()) {
            System.out.println("correct method");

            NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);

            List<NationRevenue> list = StatsUtil.getAllianceNationRevenue(interaction.getOptionByName("aarevbycity").get().getOptionLongValueByName("id").get());
            String aaName = StatsUtil.getAaName(interaction.getOptionByName("aarevbycity").get().getOptionLongValueByName("id").get());

            File logfilePerCity = new File(interaction.getOptionByName("aarevbycity").get().getOptionLongValueByName("id").get() + "-" + aaName + "-revbycity" + ".txt");
            FileWriter fileWriter = new FileWriter(logfilePerCity.getAbsoluteFile(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.append(aaName + " Revenue Rankings Per City");
            bufferedWriter.newLine();

            for (NationRevenue nationRevenue : list) {
                nationRevenue.setRevenue((nationRevenue.getRevenue() / 365) / nationRevenue.getCities());
            }

            list.sort(Comparator.comparing(NationRevenue::getRevenue));

            int rankPerCity = 1;

            for (int i = list.size() - 1; i >= 0; i--) {
                NationRevenue revenue = list.get(i);
                System.out.println("Appending " + revenue.getName());
                bufferedWriter.append(rankPerCity + ". " + revenue.getName() + " / " + revenue.getLeader() + ": " + n.format(revenue.getRevenue()) + " C:" + revenue.getCities());
                bufferedWriter.newLine();
                rankPerCity++;
            }
            bufferedWriter.close();
            System.out.println("per city revenue completed.");

            System.out.println("Sending file...");
            interaction.createFollowupMessageBuilder().addAttachment(logfilePerCity).send();
            TimeUnit.SECONDS.sleep(5);
            logfilePerCity.delete();

        }
    }
}
