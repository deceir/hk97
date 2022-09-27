package net.hk.hk97.CommandLoader;


import net.hk.hk97.Config;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.stereotype.Component;

import java.nio.channels.Channel;
import java.time.LocalTime;
import java.util.Arrays;

@Component
public class CommandAdd implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (messageCreateEvent.getMessageAuthor().isBotOwner() && messageCreateEvent.getMessageContent().equalsIgnoreCase("+deploy")) {
            DiscordApi api = messageCreateEvent.getApi();

            try {

                SlashCommand appraise =
                        SlashCommand.with("appraise", "Utilize the appraisal function.")
                                .addOption(SlashCommandOption.create(SlashCommandOptionType.LONG, "id", "Nation id.", false))

                                .createForServer(api.getServerById("1016240494948397066").get())
                                .join();


                SlashCommand apply =
                        SlashCommand.with("apply", "Apply for an interview room.")
                                .addOption(SlashCommandOption.create(SlashCommandOptionType.LONG, "id", "Enter your nation id here.", true))
                                .createForServer(api.getServerById("1016240494948397066").get())
                                .join();


                messageCreateEvent.getChannel().sendMessage("Commands deployed.");
                messageCreateEvent.getApi().getTextChannelById("1017291329283309608").get().sendMessage("Commands deployed at " + LocalTime.now());

//                messageCreateEvent.getApi().getGlobalSlashCommandById(Long.parseLong("1017297253842550805")).get().deleteGlobal();
//                messageCreateEvent.getApi().getServerSlashCommandById(messageCreateEvent.getApi().getServerById(Long.parseLong("1016240494948397066")).get(), Long.parseLong("1017297253842550805")).get().deleteForServer(messageCreateEvent.getApi().getServerById(Long.parseLong("1016240494948397066")).get());


                SlashCommand calc =
                        SlashCommand.with("calc", "A command dedicated to calculations",
                                        Arrays.asList(
                                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "infra", "Calculates cost of infrastructure.",
                                                        Arrays.asList(

                                                                                SlashCommandOption.create(SlashCommandOptionType.LONG, "start", "Starting infrastructure level", true),
                                                                                SlashCommandOption.create(SlashCommandOptionType.LONG, "end", "Ending infrastructure level", true),
                                                                                SlashCommandOption.createWithChoices(SlashCommandOptionType.LONG, "cities", "The number of cities to calculate for", false)
                                                                        )),
                                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "land", "Calculates cost of land.",
                                                        Arrays.asList(

                                                                SlashCommandOption.create(SlashCommandOptionType.LONG, "start", "Starting land level", true),
                                                                SlashCommandOption.create(SlashCommandOptionType.LONG, "end", "Ending land level", true),
                                                                SlashCommandOption.createWithChoices(SlashCommandOptionType.LONG, "cities", "The number of cities to calculate for", false)
                                                        )),
                                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "cities", "Calculates cost of cities.",
                                                        Arrays.asList(

                                                                SlashCommandOption.create(SlashCommandOptionType.LONG, "start", "City count to purchase OR starting city if calculating multiple", true),
                                                                SlashCommandOption.create(SlashCommandOptionType.LONG, "end", "Ending cities number", false)

                                                        ))
                                                ))
                                .createGlobal(api)
                                .join();

                SlashCommand audit =
                        SlashCommand.with("audit", "Retrieve list of nations that do not meet current nation requirements.",
                                        Arrays.asList(
                                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "activity", "Get list of inactive nations."),
                                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "spies", "Get list of nations that do not currently have max spies.")
                                        ))
                                .createForServer(api.getServerById(Config.mainServerId).get())
                                .join();

                SlashCommand account = SlashCommand.with("account", "Account registration and verification.",
                                Arrays.asList(
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "register", "Calculates cost of infrastructure.",
                                                Arrays.asList(

                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "id", "Nation id. This can be found on your nation page.", true)
                                                )),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "verify", "Verify with the token sent to your nation in-game.",
                                                Arrays.asList(

                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "token", "The token as it was sent to you in-game via message.", true)
                                                ))
                                ))
                        .createForServer(api.getServerById(Config.mainServerId).get())
                        .join();

//                SlashCommand command = api.getGlobalSlashCommandById()
//                        .get().deleteGlobal();

            } catch (Exception e) {
                messageCreateEvent.getChannel().sendMessage("There was an error deploying some or all commands.\n " + e.getMessage());
            }
        } else if (messageCreateEvent.getMessageAuthor().isBotOwner() && messageCreateEvent.getMessageContent().equalsIgnoreCase("+bankstart")) {

            DiscordApi api = messageCreateEvent.getApi();
            ServerTextChannel channel = api.getServerTextChannelById("1024026875007340576").get();

            new MessageBuilder()
                    .setContent("__**Requiem Banking Service**__ \n*Report any and all issues to Itachi or Pablo.*")
                    .addComponents(
                            ActionRow.of(
                                    Button.success("deposit", "Deposit"),
                                    Button.secondary("withdraw", "Withdrawal"),
                                    Button.danger("info", "Info")
                            )).send(channel);

        }
    }
}
