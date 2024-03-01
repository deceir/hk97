package net.hk.hk97;


import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.PoliticsAndWarBuilder;
import io.github.adorableskullmaster.pw4j.domains.subdomains.SWarContainer;
import net.hk.hk97.Commands.CommandLoader.CommandAdd;
import net.hk.hk97.Commands.Listeners.ApplicationListener;
import net.hk.hk97.Commands.Listeners.InteractionListener.ButtonListener;
import net.hk.hk97.Commands.Listeners.InteractionListener.ModalListener;
import net.hk.hk97.Commands.Listeners.InterviewFileLogListener;
import net.hk.hk97.Commands.Listeners.MessageCreateListener;
import net.hk.hk97.Commands.Listeners.MilComListener;
import net.hk.hk97.Commands.TextCommands.TextCommandHandler;
import net.hk.hk97.Commands.SlashCommands.SlashCommandHandler;
import net.hk.hk97.Models.Military;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.War;
import net.hk.hk97.Repositories.AllianceKeyRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WarRepository;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.UserStatus;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.Scheduled;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@EnableJpaRepositories
@SpringBootApplication
public class Hk97Application {


    DiscordApi api;


//    @Autowired
//    private ApplicationListener applicationListener;
//
//    @Autowired
//    private InterviewFileLogListener interviewFileLogListener;

    @Autowired
    private SlashCommandHandler slashCommandHandler;

    @Autowired
    private CommandAdd commandAdd;


    @Autowired
    MessageCreateListener messageCreateListener;

//    @Autowired
//    private MilComListener milComListener;

    @Autowired
    private TextCommandHandler textCommandHandler;

    @Autowired
    private ButtonListener buttonListener;

    @Autowired
    private ModalListener modalListener;

    @Autowired
    private WarRepository warDao;

    @Autowired
    private UserRepository userDao;

    @Autowired
    private AllianceKeyRepository allianceKeyRepository;

    public static void main(String[] args) {
        SpringApplication.run(Hk97Application.class, args);

    }


    @Bean
    @ConfigurationProperties(value = "discord-api")
    public DiscordApi discordApi() {
        String token = Config.discordToken;
        api = new DiscordApiBuilder().setToken(token)
                .setAllNonPrivilegedIntents()
                .addSlashCommandCreateListener(slashCommandHandler)
                .addMessageCreateListener(messageCreateListener)
                .addMessageCreateListener(textCommandHandler)
//                .addMessageCreateListener(interviewFileLogListener)
                .addMessageCreateListener(commandAdd)
//                .addMessageCreateListener(milComListener)
//                .addMessageCreateListener(applicationListener)
                .addButtonClickListener(buttonListener)
                .addModalSubmitListener(modalListener)
                .login()
                .join();
        api.updateStatus(UserStatus.DO_NOT_DISTURB);
//        ServerTextChannel channel = api.getServerTextChannelById("1024026875007340576").get();
//        channel.addButtonClickListener(bankButtonListener);

        return api;
    }

    @Scheduled(cron = "0/30 * * * * *")
    public void runWarAlerts() throws IOException, JSONException {

//        api.getTextChannelById("1017291329283309608").get().sendMessage("War alerts function successfully activated with Discord api.");
        PoliticsAndWar pnw = new PoliticsAndWarBuilder().setApiKey(Config.itachiPnwKey).build();
        int offshore = allianceKeyRepository.findAllianceKeysByAaName("offshore").getId().intValue();
        String offshoreName = allianceKeyRepository.findAllianceKeysByAaName("offshore").getName();

        List<SWarContainer> warList = pnw.getWarsByAlliance(4567, offshore).getWars();
        // tgh id 4567


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


                EmbedBuilder eb = new EmbedBuilder();

                if (newWar.getAttAa().equalsIgnoreCase("The Golden Horde") || newWar.getAttAa().equalsIgnoreCase(offshoreName)) {


                    System.out.println(newWar.getAttId());
                    net.hk.hk97.Models.Military attackerMil = MilUtil.getNationMilitary(newWar.getAttId());
                    net.hk.hk97.Models.Military defenderMil = MilUtil.getNationMilitary(newWar.getDefId());

                    TemporalAccessor creationAccessor = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse( attackerMil.getLast_active() );
                    Instant attinstant = Instant.from( creationAccessor );
                    Duration attduration = Duration.between(attinstant, Instant.now());

                    // def
                    creationAccessor = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse( defenderMil.getLast_active() );
                    Instant definstant = Instant.from( creationAccessor );
                    Duration defduration = Duration.between(definstant, Instant.now());




                    eb.setTitle(newWar.getAttAa() + " has attacked " + newWar.getDefAa() + "!")
                            .setColor(Color.GREEN)
                            .setDescription("[" + attackerMil.getLeader_name() + " has declared war on " + defenderMil.getLeader_name() + "!](https://politicsandwar.com/nation/war/timeline/war=" + war.getWarID() + ")")
                            .addInlineField(attackerMil.getLeader_name() + " score: " + attackerMil.getScore() + "\nCities: " + attackerMil.getCities(), ":military_helmet: " + attackerMil.getSoldiers() + " :bus: " + attackerMil.getTanks() + " :airplane: " + attackerMil.getJets() + " :ship: " + attackerMil.getShips() + "\n:rocket: " + attackerMil.getMissiles() + " :radioactive: " + attackerMil.getNukes())
                            .addInlineField(defenderMil.getLeader_name() + " score: " + defenderMil.getScore() + "\nCities: " + defenderMil.getCities(), ":military_helmet: " + defenderMil.getSoldiers() + " :bus: " + defenderMil.getTanks() + " :airplane: " + defenderMil.getJets() + " :ship: " + defenderMil.getShips() + "\n:rocket: " + defenderMil.getMissiles() + " :radioactive: " + defenderMil.getNukes())
                            .addField(attackerMil.getLeader_name() + " was last active", attduration.toMinutesPart() + "m " + attduration.toHoursPart() + "h " + attduration.toDaysPart() + "d")
                            .addField(defenderMil.getLeader_name() + " was last active", defduration.toMinutesPart() + "m " + defduration.toHoursPart() + "h " + defduration.toDaysPart() + "d");


                    Channel warAlertChannel = api.getTextChannelById(Long.parseLong("1128054316574457949")).get();
                    warAlertChannel.asTextChannel().get().sendMessage(eb);
                    warDao.save(newWar);

                } else if (newWar.getDefAa().equalsIgnoreCase("The Golden Horde") || newWar.getDefAa().equalsIgnoreCase(offshoreName)) {

                    if (api == null) {
                        api = new DiscordApiBuilder().setToken(Config.discordToken)
                                .login()
                                .join();
                    }

                    net.hk.hk97.Models.Military attackerMil = MilUtil.getNationMilitary(newWar.getAttId());
                    Military defenderMil = MilUtil.getNationMilitary(newWar.getDefId());

                    TemporalAccessor creationAccessor = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse( attackerMil.getLast_active() );
                    Instant attinstant = Instant.from( creationAccessor );
                    Duration attduration = Duration.between(attinstant, Instant.now());

                    // def
                    creationAccessor = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse( defenderMil.getLast_active() );
                    Instant definstant = Instant.from( creationAccessor );
                    Duration defduration = Duration.between(definstant, Instant.now());


                    eb.setTitle(newWar.getAttAa() + " has attacked " + newWar.getDefAa() + "!")
                            .setColor(Color.RED)
                            .setDescription("[" + attackerMil.getLeader_name() + " has declared war on " + defenderMil.getLeader_name() + "!](https://politicsandwar.com/nation/war/timeline/war=" + war.getWarID() + ")")
                            .addInlineField(attackerMil.getLeader_name() + " score: " + attackerMil.getScore() + "\nCities: " + attackerMil.getCities(), ":military_helmet: " + attackerMil.getSoldiers() + " :bus: " + attackerMil.getTanks() + " :airplane: " + attackerMil.getJets() + " :ship: " + attackerMil.getShips() + "\n:rocket: " + attackerMil.getMissiles() + " :radioactive: " + attackerMil.getNukes())
                            .addInlineField(defenderMil.getLeader_name() + " score: " + defenderMil.getScore() + "\nCities: " + defenderMil.getCities(), ":military_helmet: " + defenderMil.getSoldiers() + " :bus: " + defenderMil.getTanks() + " :airplane: " + defenderMil.getJets() + " :ship: " + defenderMil.getShips() + "\n:rocket: " + defenderMil.getMissiles() + " :radioactive: " + defenderMil.getNukes())
                            .addField(attackerMil.getLeader_name() + " was last active", attduration.toMinutesPart() + "m " + attduration.toHoursPart() + "h " + attduration.toDaysPart() + "d")
                            .addField(defenderMil.getLeader_name() + " was last active", defduration.toMinutesPart() + "m " + defduration.toHoursPart() + "h " + defduration.toDaysPart() + "d");


                    String ping = "<@&1128047839562449037>";
                    try {
                        User user = userDao.findUserByNationid(defenderMil.getId());
                        ping += " <@" + user.getDiscordid() + ">";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Channel warAlertChannel = api.getTextChannelById(Long.parseLong("1128054316574457949")).get();
                    warAlertChannel.asTextChannel().get().sendMessage(eb);
                    warAlertChannel.asTextChannel().get().sendMessage(ping);
                    warDao.save(newWar);


                }


            }
        }
    }


}



