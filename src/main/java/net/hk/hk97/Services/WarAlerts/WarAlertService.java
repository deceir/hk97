package net.hk.hk97.Services.WarAlerts;


import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.PoliticsAndWarBuilder;
import io.github.adorableskullmaster.pw4j.domains.subdomains.SWarContainer;
import net.hk.hk97.Config;
import net.hk.hk97.Hk97Application;
import net.hk.hk97.Models.Military;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.War;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WarRepository;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@Service
@Configuration
@EnableScheduling
public class WarAlertService {


    @Autowired
    private UserRepository userDao;

    @Autowired
    private WarRepository warDao;



//    @Scheduled(cron = "0/30 * * * * *")
    public void getWarAlerts(DiscordApi api) throws IOException, JSONException {

//        DiscordApi api = new DiscordApiBuilder().setToken(Config.discordToken)
//                .login()
//                .join();




    }
}
