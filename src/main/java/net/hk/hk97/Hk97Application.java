package net.hk.hk97;

import net.hk.hk97.Listeners.ApplicationListener;
import net.hk.hk97.Listeners.InterviewFileLogListener;
import net.hk.hk97.SlashCommands.SlashCommandHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;


@SpringBootApplication
public class Hk97Application {


    @Autowired
    private ApplicationListener applicationListener;

    @Autowired
    private InterviewFileLogListener interviewFileLogListener;

    @Autowired
    private SlashCommandHandler slashCommandHandler;

    public static void main(String[] args) {
        SpringApplication.run(Hk97Application.class, args);


     }


     @Bean
    @ConfigurationProperties(value = "discord-api")
    public DiscordApi discordApi() throws ExecutionException, InterruptedException {
         String token = Config.discordToken;
         DiscordApi api = new DiscordApiBuilder().setToken(token)
                 .setAllNonPrivilegedIntents()
                 .login()
                 .join();




         SlashCommand appraise =
                 SlashCommand.with("appraise", "Utilize the appraisal function.")
                                 .addOption(SlashCommandOption.create(SlashCommandOptionType.INTEGER, "id", "Nation id.", false))

                         .createForServer(api.getServerById("1016240494948397066").get())
                                                 .join();

         // server id test: 829738477419495495


         SlashCommand apply =
                 SlashCommand.with("apply", "Apply for an interview room.")
                         .addOption(SlashCommandOption.create(SlashCommandOptionType.INTEGER, "id", "Enter your nation id here.", true))
                         .createForServer(api.getServerById("1016240494948397066").get())
                         .join();

//         SlashCommand calc =
//                 SlashCommand.with("calc", "Calculation functions.",
//                         Arrays.asList(
//                                 SlashCommandOption.createWithOptions()
//                         ))

//
//         Server server = api.getServerById("1016240494948397066").get();
//         long id = Long.parseLong("1016757111268577290");
//
//         api.getServerSlashCommandById(server, id).get().deleteForServer(server);







//         api.addMessageCreateListener(applicationListener);
         api.addMessageCreateListener(interviewFileLogListener);
         api.addSlashCommandCreateListener(slashCommandHandler);



         return api;
     }
}
