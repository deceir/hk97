package net.hk.hk97.Services;

import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.PoliticsAndWarBuilder;
import io.github.adorableskullmaster.pw4j.domains.Alliance;
import io.github.adorableskullmaster.pw4j.domains.subdomains.SWarContainer;
import net.hk.hk97.Config;
import net.hk.hk97.Models.Military;
import net.hk.hk97.Models.War;
import net.hk.hk97.Repositories.WarRepository;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@Service
@Configuration
@EnableScheduling
public class WarAlertService {


    private DiscordApi api = null;

    @Autowired
    private WarRepository warDao;

//    @Scheduled(cron = "*/30 * * * * *")
    public void getWarAlerts() throws IOException, JSONException {


        PoliticsAndWar pnw = new PoliticsAndWarBuilder().setApiKey(Config.itachiPnwKey).build();

        List<SWarContainer> warList = pnw.getWarsByAlliance(4829).getWars();

        for (SWarContainer war : warList) {
            if (!warDao.findById(war.getWarID()).isPresent()) {

                War newWar = new War();
                newWar.setId(war.getWarID());
                newWar.setWarType(war.getWarType());
                newWar.setDate(war.getDate());
                newWar.setAttId(war.getAttackerID());
                newWar.setDefId(war.getDefenderID());
                newWar.setAttAa(war.getAttackerAA());
                newWar.setDefAa(war.getDefenderAA());
                newWar.setStatus(war.getStatus());
//                warDao.save(newWar);

                EmbedBuilder eb = new EmbedBuilder();

                if (newWar.getAttAa().equalsIgnoreCase("The Enterprise")) {

                    if (api == null) {
                        api = new DiscordApiBuilder().setToken(Config.discordToken)
                                .login()
                                .join();
                    }

                    Military attackerMil = MilUtil.getNationMilitary(newWar.getAttId());
                    Military defenderMil = MilUtil.getNationMilitary(newWar.getDefId());

                    eb.setTitle(newWar.getAttAa() + " has attacked " + newWar.getDefAa() + "!")
                            .setColor(Color.GREEN)
                            .setDescription("[" + attackerMil.getLeader_name() + " has declared war on " + defenderMil.getLeader_name() + "!](https://politicsandwar.com/nation/war/timeline/war=" + war.getWarID() + ")")
                            .addInlineField(attackerMil.getLeader_name() + " score: " + attackerMil.getScore() + "\nCities: " + attackerMil.getCities(), ":military_helmet: " + attackerMil.getSoldiers() + " :bus: " + attackerMil.getTanks() + " :airplane: " + attackerMil.getJets() + " :ship: " + attackerMil.getShips() + "\n:rocket: " + attackerMil.getMissiles() + " :radioactive: " + attackerMil.getNukes())
                            .addInlineField(defenderMil.getLeader_name() + " score: " + defenderMil.getScore()  + "\nCities: " + attackerMil.getCities(), ":military_helmet: " + defenderMil.getSoldiers() + " :bus: " + defenderMil.getTanks() + " :airplane: " + defenderMil.getJets() + " :ship: " + defenderMil.getShips()+ "\n:rocket: " + defenderMil.getMissiles() + " :radioactive: " + defenderMil.getNukes())
                            .addField(attackerMil.getLeader_name() + " was last active", attackerMil.getLast_active())
                            .addField(defenderMil.getLeader_name() + " was last active", defenderMil.getLast_active());

                    Channel warAlertChannel = api.getTextChannelById(Long.parseLong("1004918961097424976")).get();
                    warAlertChannel.asTextChannel().get().sendMessage(eb);

                } else if (newWar.getDefAa().equalsIgnoreCase("The Enterprise")) {

                    if (api == null) {
                        api = new DiscordApiBuilder().setToken(Config.discordToken)
                                .login()
                                .join();
                    }

                    Military attackerMil = MilUtil.getNationMilitary(newWar.getAttId());
                    Military defenderMil = MilUtil.getNationMilitary(newWar.getDefId());

                    eb.setTitle(newWar.getAttAa() + " has attacked " + newWar.getDefAa() + "!")
                            .setColor(Color.RED)
                            .setDescription("[" + attackerMil.getLeader_name() + " has declared war on " + defenderMil.getLeader_name() + "!](https://politicsandwar.com/nation/war/timeline/war=" + war.getWarID() + ")")
                            .addInlineField(attackerMil.getLeader_name() + " score: " + attackerMil.getScore(), ":military_helmet: " + attackerMil.getSoldiers() + " :bus: " + attackerMil.getTanks() + " :airplane: " + attackerMil.getJets() + " :ship: " + attackerMil.getShips())
                            .addInlineField(defenderMil.getLeader_name() + " score: " + defenderMil.getScore(), ":military_helmet: " + defenderMil.getSoldiers() + " :bus: " + defenderMil.getTanks() + " :airplane: " + defenderMil.getJets() + " :ship: " + defenderMil.getShips())
                            .addInlineField(attackerMil.getLeader_name() + " WMD's", ":rocket: " + attackerMil.getMissiles() + " :radioactive: " + attackerMil.getNukes())
                            .addInlineField(defenderMil.getLeader_name() + " WMD's", ":rocket: " + defenderMil.getMissiles() + " :radioactive: " + defenderMil.getNukes())
                            .addField(attackerMil.getLeader_name() + " was last active", attackerMil.getLast_active())
                            .addField(defenderMil.getLeader_name() + " was last active", defenderMil.getLast_active());

                    Channel warAlertChannel = api.getTextChannelById(Long.parseLong("1004918961097424976")).get();
                    warAlertChannel.asTextChannel().get().sendMessage(eb);

                }


            }
        }
    }
}
