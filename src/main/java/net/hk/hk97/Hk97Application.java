package net.hk.hk97;


import net.hk.hk97.CommandLoader.CommandAdd;
import net.hk.hk97.Listeners.ApplicationListener;
import net.hk.hk97.Listeners.InterviewFileLogListener;
import net.hk.hk97.Listeners.MilComListener;
import net.hk.hk97.SlashCommands.SlashCommandHandler;
import net.hk.hk97.bank.BankButtonListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.user.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class Hk97Application {


    @Autowired
    private ApplicationListener applicationListener;

    @Autowired
    private InterviewFileLogListener interviewFileLogListener;

    @Autowired
    private SlashCommandHandler slashCommandHandler;

    @Autowired
    private CommandAdd commandAdd;



    @Autowired
    private MilComListener milComListener;

    public static void main(String[] args) {
        SpringApplication.run(Hk97Application.class, args);


    }


    @Bean
    @ConfigurationProperties(value = "discord-api")
    public DiscordApi discordApi() {
        String token = Config.discordToken;
        DiscordApi api = new DiscordApiBuilder().setToken(token)
                .setAllNonPrivilegedIntents()
                .addSlashCommandCreateListener(slashCommandHandler)
                .addMessageCreateListener(interviewFileLogListener)
                .addMessageCreateListener(commandAdd)
                .addMessageCreateListener(milComListener)
                .addMessageCreateListener(applicationListener)
                .login()
                .join();

        api.updateStatus(UserStatus.DO_NOT_DISTURB);
//        ServerTextChannel channel = api.getServerTextChannelById("1024026875007340576").get();
//        channel.addButtonClickListener(bankButtonListener);



        return api;
    }
}



