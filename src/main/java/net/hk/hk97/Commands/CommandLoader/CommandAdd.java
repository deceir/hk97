package net.hk.hk97.Commands.CommandLoader;


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
                                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "nations", "Audit Requiem nations.")

                                        ))
                                .createForServer(api.getServerById(Config.mainServerId).get())
                                .join();

                SlashCommand account = SlashCommand.with("account", "Account registration and verification.",
                                Arrays.asList(
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "register", "Initial registration step.",
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

                SlashCommand wc = SlashCommand.with("wc", "Warchest functions",
                                Arrays.asList(
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "reqs", "Get your warchest requirements.",
                                                Arrays.asList(
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "cities", "Number of cities to calculate for.", false)
                                                )),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "calc", "Retrieve estimated value for your missing warchest.",
                                                Arrays.asList(
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "cities", "Number of cities to calculate for.", false)
                                                ))
                                ))
                        .createForServer(api.getServerById(Config.mainServerId).get())
                        .join();

                SlashCommand bank = SlashCommand.with("bank", "Requiem banking services.",
                                Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "create", "Create a bank account."),
                                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "info", "View the amounts currently in your account."),
                                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "deposit", "Update your latest deposit"),
//                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "transfer", "Transfer funds to another member's account. *This is irreversible, make sure you know what you're doing.*",
//                                                Arrays.asList(
//
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "cash", "Cash to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "food", "Food to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "uranium", "Uranium to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "coal", "Coal to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "iron", "Iron to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "bauxite", "Bauxite to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "oil", "Oil to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "steel", "Steel to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "munitions", "Munitions to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "gasoline", "Gasoline to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "aluminum", "Aluminum to withdraw.", false),
//                                                        SlashCommandOption.create(SlashCommandOptionType.USER, "user", "The user you want to send contents of your bank to.", true)
//                                                )),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "withdraw", "Make a withdrawal.",
                                                Arrays.asList(
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "cash", "Cash to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "food", "Food to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "uranium", "Uranium to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "coal", "Coal to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "iron", "Iron to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "bauxite", "Bauxite to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "oil", "Oil to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "steel", "Steel to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "munitions", "Munitions to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "gasoline", "Gasoline to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "lead", "Lead to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "aluminum", "Aluminum to withdraw.", false)
                                                ))
                                ))
                        .createForServer(api.getServerById(Config.mainServerId).get())
                        .join();


                SlashCommand badmin = SlashCommand.with("badmin", "Banking admin services.",
                                Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "pending", "View pending withdrawals."),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "update", "Update specific withdrawal.",
                                                Arrays.asList(
                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "code", "Withdrawal code.", true)
                                                )),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "remove", "Remove specific withdrawal.",
                                                Arrays.asList(
                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "code", "Withdrawal code.", true)
                                                )),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "modify", "Modify a user's account.",
                                                Arrays.asList(
                                                        SlashCommandOption.create(SlashCommandOptionType.USER, "member", "Member nation to modify.", true),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "cash", "Cash to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "food", "Food to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "uranium", "Uranium to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "coal", "Coal to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "iron", "Iron to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "bauxite", "Bauxite to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "oil", "Oil to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "steel", "Steel to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "munitions", "Munitions to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "gasoline", "Gasoline to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "lead", "Lead to withdraw.", false),
                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "aluminum", "Aluminum to withdraw.", false)
                                                )),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "view", "View selected user's account.",
                                                Arrays.asList(
                                                        SlashCommandOption.create(SlashCommandOptionType.USER, "member", "Member account to view.", true)
                                                )),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "generate", "Generate member's withdrawal.",
                                                Arrays.asList(
                                                        SlashCommandOption.create(SlashCommandOptionType.USER, "member", "Member account to view.", false)
                                                )),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, "loan", "Bank admin commands related to loans.",
                                                Arrays.asList(
                                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "create", "Create a loan for a user.",
                                                                Arrays.asList(
                                                                        SlashCommandOption.create(SlashCommandOptionType.USER, "member", "User to add the new loan to.", true),
                                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "amount", "Cash amount being loaned.", true),
                                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "days", "Amount of days until loan is due.", true)
                                                                )),
                                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "remove", "Removes a loan by setting its status to inactive.",
                                                                Arrays.asList(
                                                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "loan", "The loan id being removed.", true)
                                                                ))
                                                ))


                                ))
                        .createForServer(api.getServerById(Config.mainServerId).get())
                        .join();


                SlashCommand tr = SlashCommand.with("treasure", "Treasure-related functions.",
                                Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "maxscore", "Get the top nation score."),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "hunt", "Get nations in-range of a specified color treasure (and optional continent.",
                                                Arrays.asList(
                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "color", "Color to hunt for.", true),
                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "continent", "Continent to hunt for.", false)
                                                )),
                                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "list", "Get list of spawning treasures.")
                                ))
                        .createForServer(api.getServerById(Config.mainServerId).get())
                        .join();

                SlashCommand who = SlashCommand.with("who", "Search functionality.",
                                Arrays.asList(
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "leader", "Get list of leaders.",
                                                Arrays.asList(
                                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "Name", "Leader name.", true)
                                                ))
                                ))
                        .createForServer(api.getServerById(Config.mainServerId).get())
                        .join();

                SlashCommand stats =
                        SlashCommand.with("stats", "A command dedicated to calculations",
                                        Arrays.asList(
                                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "aarev", "Stat commands.",
                                                        Arrays.asList(

                                                                SlashCommandOption.create(SlashCommandOptionType.LONG, "id", "Alliance id.", true)
                                                        ))
                                        ))
                                .createForServer(api.getServerById(Config.mainServerId).get())
                                .join();


                return;
            } catch (Exception e) {
                messageCreateEvent.getChannel().sendMessage("There was an error deploying some or all commands.\n " + e.getMessage());
                e.printStackTrace();
            }
        } else if (messageCreateEvent.getMessageAuthor().isBotOwner() && messageCreateEvent.getMessageContent().equalsIgnoreCase("+bankstart")) {

            DiscordApi api = messageCreateEvent.getApi();
            ServerTextChannel channel = api.getServerTextChannelById("1024026875007340576").get();
            messageCreateEvent.getMessage().delete();

            new MessageBuilder()
                    .setContent("__**Requiem Banking Service**__ \n*Report any and all issues to Itachi or Pablo.*\nSelecting info will show ")
                    .addComponents(
                            ActionRow.of(
                                    Button.danger("info", "Info"),
                                    Button.success("deposit", "Deposit"),
                                    Button.secondary("withdraw", "Withdrawal")

                            )).send(channel);

        }
    }
}
