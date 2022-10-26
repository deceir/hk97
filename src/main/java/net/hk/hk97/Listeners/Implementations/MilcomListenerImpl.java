package net.hk.hk97.Listeners.Implementations;

import net.hk.hk97.Config;
import net.hk.hk97.Listeners.MilComListener;
import net.hk.hk97.Models.Military;
import net.hk.hk97.Models.Nation;
import net.hk.hk97.Models.WarInfo;
import net.hk.hk97.Models.Warroom;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WarroomRepository;
import net.hk.hk97.Services.Util.MilUtil;
import net.hk.hk97.Services.Util.WarUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class MilcomListenerImpl implements MilComListener {

    String govRole = Config.mainServerGovId;
    String warCategory = Config.mainServerWarroomCategoryId;
    String serverId = Config.mainServerId;
    String staffId = Config.mainServerStaffId;

    String commandsChannel = Config.mainServerCommandRoom;

    String hkRole = Config.mainServerHkRole;


    @Autowired
    private UserRepository userDao;
    @Autowired
    private WarroomRepository warRoomDao;

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {

        if (messageCreateEvent.getServerTextChannel().get().getCategory().get().getIdAsString().equalsIgnoreCase(warCategory) && messageCreateEvent.getServer().get().getIdAsString().equalsIgnoreCase(serverId)) {

            DiscordApi api = messageCreateEvent.getApi();


            if (messageCreateEvent.getMessageContent().startsWith("+wrc")) {
                String messageContent = messageCreateEvent.getMessageContent();
                messageContent = messageContent.replace(" ", ",");
                List<String> list = Arrays.asList(messageContent.split(","));

                if (list.size() == 2) {

                    int warId = Integer.parseInt(list.get(1));

                    if (warRoomDao.findById(warId).isPresent()) {
                        messageCreateEvent.getChannel().sendMessage("There is already a War Room created for that nation. Located at: <#" + warRoomDao.findById(warId).get().getChannelid() + ">");

                        return;
                    }

                    try {

                        messageCreateEvent.getChannel().type();

                        Optional<Server> server = api.getServerById(serverId);
                        System.out.println(server.get().getName());
                        System.out.println(server.get().getRoleById(govRole).get().getName());

                        Permissions deniedPerms = new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL).build();
                        Role everyoneRole = server.get().getEveryoneRole();

                        String nationName = MilUtil.getNationName(warId);



                        //create new channel

                        Channel warroomChannel = new ServerTextChannelBuilder(server.get())
                                .setName(list.get(1) + "-" + nationName)
                                .addPermissionOverwrite(server.get().getEveryoneRole(), deniedPerms)
                                .addPermissionOverwrite(server.get().getRoleById(staffId).get(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                                .addPermissionOverwrite(server.get().getRoleById(govRole).get(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                                .addPermissionOverwrite(server.get().getRoleById(hkRole).get(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                                .setCategory(api.getChannelCategoryById(warCategory).get())
                                .setTopic("+wra [mention] to add, +wrr [mention] to remove, +wrd to delete")
                                .create().join();

                        //get warinfo list

                        List<List<WarInfo>> nationWars = WarUtil.getNationWars(warId);

                        System.out.println("War info 1");
                        System.out.println("War info ");
                        List<WarInfo> offensiveWars = nationWars.get(0);
                        List<WarInfo> defensiveWars = nationWars.get(1);
                        Nation nation = WarUtil.getNationInfo(warId);

                        Warroom warroom = new Warroom();
                        warroom.setChannelid(warroomChannel.getIdAsString());
                        warroom.setId(Integer.parseInt(list.get(1)));
                        warRoomDao.save(warroom);

                        messageCreateEvent.getMessage().reply("War Room created at: <#" + warroomChannel.getIdAsString() + ">");


                        String nationInfo = "```apache\n";
                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-12s %-12s", "NAME", "AA", "SCORE", "SOLDIER", "TANK", "AIR", "SHIP", "RESIST", "MAPs");
                        nationInfo += "\n";
                        nationInfo += " ";
                        nationInfo += "\n";
                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", nation.getNation(), nation.getAcronym(), (int) nation.getScore(), nation.getSoldiers(), nation.getTanks(), nation.getAircraft(), nation.getShips(), "ag", "de", "ag", "de");
                        nationInfo += "\n";
                        nationInfo += "```";

                        String offensiveWarString = "";
                        int offWarCount = 0;
                        if (offensiveWars.size() > 0) {
                            offWarCount = offensiveWars.size();
                            offensiveWarString = "```apache\n";
                            for (int i = 0; i <= offensiveWars.size() - 1; i++) {
                                WarInfo warInfo = offensiveWars.get(i);
                                offensiveWarString += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", warInfo.getDefenderNation().getNation_name(), warInfo.getDefenderNation().getAaAcronym(), (int) warInfo.getDefenderNation().getScore(), warInfo.getDefenderNation().getSoldiers(), warInfo.getDefenderNation().getTanks(), warInfo.getDefenderNation().getJets(), warInfo.getDefenderNation().getShips(), warInfo.getAttResistance(), warInfo.getDefResistance(), warInfo.getAttmaps(), warInfo.getDefmaps());
                                offensiveWarString += "\n";
                                offensiveWarString += "|| GC: " + warInfo.getDefenderNation().getGroundcontrol() + " AS: " + warInfo.getDefenderNation().getAirsuperiority() + " NB: " + warInfo.getDefenderNation().getNavalblockade();
                                offensiveWarString += "\n";
                            }
                            offensiveWarString += "```";
                        }

                        String defensiveWarString = "";
                        int defWarCount = 0;
                        if (defensiveWars.size() > 0) {
                            defWarCount = defensiveWars.size();
                            defensiveWarString = "```apache\n";
                            for (int i = 0; i <= defensiveWars.size() - 1; i++) {
                                WarInfo warInfo = defensiveWars.get(i);
                                defensiveWarString += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", warInfo.getAttackerNation().getNation_name(), warInfo.getAttackerNation().getAaAcronym(), (int) warInfo.getAttackerNation().getScore(), warInfo.getAttackerNation().getSoldiers(), warInfo.getAttackerNation().getTanks(), warInfo.getAttackerNation().getJets(), warInfo.getAttackerNation().getShips(), warInfo.getAttResistance(), warInfo.getDefResistance(), warInfo.getAttmaps(), warInfo.getDefmaps());
                                defensiveWarString += "\n";
                                defensiveWarString += "|| GC: " + warInfo.getAttackerNation().getGroundcontrol() + " AS: " + warInfo.getAttackerNation().getAirsuperiority() + " NB: " + warInfo.getAttackerNation().getNavalblockade();
                            }
                            defensiveWarString += "```";
                            System.out.println(defensiveWarString);
                        }

                        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
                        System.out.println("Last active: " + nation.getLast_active());
                        Duration duration = nation.getActivity();

                        embedBuilder.setDescription("[" + nation.getNation() + ", " + nation.getAlliance() + "](https://politicsandwar.com/nation/id=" + warId + ") | active: " + duration.toDaysPart() + "d "+ duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago");
                        if (offensiveWars.size() > 0) {
                            String offWarsStr = "";
                            for (WarInfo warInfo : offensiveWars) {
                                duration = warInfo.getDefenderNation().getActivity();
                                offWarsStr += "[war](https://politicsandwar.com/nation/war/timeline/war=" + warInfo.getId() + ") - [" + warInfo.getDefenderNation().getNation_name() + "](https://politicsandwar.com/nation/id=" + warInfo.getDefenderNation().getId() + ") - (" + warInfo.getDefenderNation().getId() + ") | active: " + duration.toDaysPart() + "d "+ duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago";
                                offWarsStr += "\n";
                            }
                            embedBuilder.addField("Offensive Wars", offWarsStr);
                        }
                        if (defensiveWars.size() > 0) {
                            String defWarStr = "";
                            for (WarInfo warInfo : defensiveWars) {
                                duration = warInfo.getDefenderNation().getActivity();
                                defWarStr += "[war](https://politicsandwar.com/nation/war/timeline/war=" + warInfo.getId() + ") - [" + warInfo.getAttackerNation().getNation_name() + "](https://politicsandwar.com/nation/id=" + warInfo.getAttackerNation().getId() + ") - (" + warInfo.getAttackerNation().getId() + ") | active: " + duration.toDaysPart() + "d "+ duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago";
                                defWarStr += "\n";
                            }
                            embedBuilder.addField("Defensive Wars", defWarStr);

                        }

                        warroomChannel.asServerTextChannel().get().sendMessage(nationInfo);
                        warroomChannel.asServerTextChannel().get().sendMessage("**OFFENSIVE WARS** (" + offWarCount + "/6)");
                        if (offensiveWarString.length() > 0) warroomChannel.asServerTextChannel().get().sendMessage(offensiveWarString);
                        warroomChannel.asServerTextChannel().get().sendMessage("**DEFENSIVE WARS** (" + defWarCount + "/3)");
                        if (offensiveWarString.length() > 0) warroomChannel.asServerTextChannel().get().sendMessage(defensiveWarString);
                        warroomChannel.asServerTextChannel().get().sendMessage(embedBuilder);


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                } else {
                    messageCreateEvent.getChannel().sendMessage("Error creating war room. You did not enter correct arguments.\nProper syntax is:\n`+wrc [nationid]");
                }
            }
            else if (messageCreateEvent.getMessageContent().startsWith("+wra")) {


                if (!messageCreateEvent.getChannel().getIdAsString().equalsIgnoreCase(commandsChannel)) {

                    String messageContent = messageCreateEvent.getMessageContent();
                    messageContent = messageContent.replace(" ", ",");
                    List<String> list = Arrays.asList(messageContent.split(","));

                    if (list.size() != 2) {
                        messageCreateEvent.getChannel().sendMessage("You have either entered too many arguments or not enough. Please enter `+wra [user]`.");
                    } else {
                        String userId = list.get(1);
                        userId = userId.replaceAll("<", "");
                        userId = userId.replaceAll("@", "");
                        userId = userId.replaceAll(">", "");

                        System.out.println(userId);
                        try {
                            User user = api.getUserById(userId).get();


                            Channel channel = messageCreateEvent.getChannel();
                            channel.asServerChannel().get().asServerTextChannel().get();
                            RegularServerChannelUpdater scu = channel.asServerTextChannel().get().createUpdater();


                            Permissions permissions = new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build();


                            scu.addPermissionOverwrite(user, permissions);
                            scu.update();
//                            new ServerChannelUpdater(channel.asServerTextChannel().get()).addPermissionOverwrite(messageCreateEvent.getMessageAuthor().asUser().get(), permissions).update();
                            channel.asServerTextChannel().get().sendMessage("<@" + userId + "> has been added to this room.");

                        } catch (Exception e) {
                            try {

                                net.hk.hk97.Models.User member = userDao.findUserByLeadernameLike(list.get(1));
                                User user = api.getUserById(member.getDiscordid()).get();

                                boolean isMember = false;
                                List<Role> roleList = user.getRoles(api.getServerById(serverId).get());

                                for (Role role : roleList) {
                                    if (role.getIdAsString().equalsIgnoreCase("1016447169898746008")){
                                        isMember = true;
                                    }
                                }

                                if (isMember) {
                                    Channel channel = messageCreateEvent.getChannel();
                                    channel.asServerChannel().get().asServerTextChannel().get();
                                    RegularServerChannelUpdater scu = channel.asServerTextChannel().get().createUpdater();

                                    Permissions permissions = new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build();

                                    scu.addPermissionOverwrite(user, permissions);
                                    scu.update();
//                            new ServerChannelUpdater(channel.asServerTextChannel().get()).addPermissionOverwrite(messageCreateEvent.getMessageAuthor().asUser().get(), permissions).update();
                                    channel.asServerTextChannel().get().sendMessage(user.getDiscriminatedName() +" has been added to this room.");
                                }
                            } catch (Exception exception) {
                                messageCreateEvent.getChannel().sendMessage("Could not find user.");
                            }
                        }
                    }
                }
            } else if (messageCreateEvent.getMessageContent().startsWith("+wrr")) {


                if (!messageCreateEvent.getChannel().getIdAsString().equalsIgnoreCase(commandsChannel)) {
                    String messageContent = messageCreateEvent.getMessageContent();
                    messageContent = messageContent.replace(" ", ",");
                    List<String> list = Arrays.asList(messageContent.split(","));

                    if (list.size() != 2) {
                        messageCreateEvent.getChannel().sendMessage("You have either entered too many arguments or not enough. Please enter `+wrr [user]`.");
                    } else {
                        String userId = list.get(1);
                        userId = userId.replaceAll("<", "");
                        userId = userId.replaceAll("@", "");
                        userId = userId.replaceAll(">", "");

                        System.out.println(userId);
                        try {

                            User user = api.getUserById(userId).get();


                            Channel channel = messageCreateEvent.getChannel();
                            channel.asServerChannel().get().asServerTextChannel().get();
                            RegularServerChannelUpdater scu = channel.asServerTextChannel().get().createUpdater();

                            Permissions permissions = new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL).build();

                            scu.addPermissionOverwrite(user, permissions);
                            scu.update();
//                            new ServerChannelUpdater(channel.asServerTextChannel().get()).addPermissionOverwrite(messageCreateEvent.getMessageAuthor().asUser().get(), permissions).update();
                            channel.asServerTextChannel().get().sendMessage("<@" + userId + "> has been removed from this room.");

                        } catch (Exception e) {
                            messageCreateEvent.getChannel().sendMessage("Could not find user.");
                        }
                    }
                }
            } else if (messageCreateEvent.getMessageContent().equalsIgnoreCase("+w")) {

                if (warRoomDao.findWarroomByChannelid(messageCreateEvent.getChannel().getIdAsString()) == null) {
                    messageCreateEvent.getChannel().sendMessage("There was an error with your request. Invalid warroom.");
                } else {


                    //get war room update

                    Warroom warroom = warRoomDao.findWarroomByChannelid(messageCreateEvent.getChannel().getIdAsString());


                    //get warinfo list

                    int warId = warroom.getId();
                    ServerTextChannel warroomChannel = api.getServerTextChannelById(warroom.getChannelid()).get();

                    try {

                        List<List<WarInfo>> nationWars = WarUtil.getNationWars(warId);


                        List<WarInfo> offensiveWars = nationWars.get(0);
                        List<WarInfo> defensiveWars = nationWars.get(1);
                        Nation nation = WarUtil.getNationInfo(warId);

                        String nationInfo = "```apache\n";
                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-12s %-12s", "NAME", "AA", "SCORE", "SOLDIER", "TANK", "AIR", "SHIP", "RESIST", "MAPs");
                        nationInfo += "\n";
                        nationInfo += " ";
                        nationInfo += "\n";
                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", nation.getNation(), nation.getAcronym(), (int) nation.getScore(), nation.getSoldiers(), nation.getTanks(), nation.getAircraft(), nation.getShips(), "ag", "de", "ag", "de");
                        nationInfo += "\n";
                        nationInfo += "```";

                        String offensiveWarString = "";
                        int offWarCount = 0;
                        if (offensiveWars.size() > 0) {
                            offWarCount = offensiveWars.size();
                            offensiveWarString = "```apache\n";
                            for (int i = 0; i <= offensiveWars.size() - 1; i++) {
                                WarInfo warInfo = offensiveWars.get(i);
                                offensiveWarString += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", warInfo.getDefenderNation().getNation_name(), warInfo.getDefenderNation().getAaAcronym(), (int) warInfo.getDefenderNation().getScore(), warInfo.getDefenderNation().getSoldiers(), warInfo.getDefenderNation().getTanks(), warInfo.getDefenderNation().getJets(), warInfo.getDefenderNation().getShips(), warInfo.getAttResistance(), warInfo.getDefResistance(), warInfo.getAttmaps(), warInfo.getDefmaps());
                                offensiveWarString += "\n";
                                offensiveWarString += "|| GC: " + warInfo.getDefenderNation().getGroundcontrol() + " AS: " + warInfo.getDefenderNation().getAirsuperiority() + " NB: " + warInfo.getDefenderNation().getNavalblockade();
                                offensiveWarString += "\n";
                            }
                            offensiveWarString += "```";
                        }

                        String defensiveWarString = "";
                        int defWarCount = 0;
                        if (defensiveWars.size() > 0) {
                            defWarCount = defensiveWars.size();
                            defensiveWarString = "```apache\n";
                            for (int i = 0; i <= defensiveWars.size() - 1; i++) {
                                WarInfo warInfo = defensiveWars.get(i);
                                defensiveWarString += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", warInfo.getAttackerNation().getNation_name(), warInfo.getAttackerNation().getAaAcronym(), (int) warInfo.getAttackerNation().getScore(), warInfo.getAttackerNation().getSoldiers(), warInfo.getAttackerNation().getTanks(), warInfo.getAttackerNation().getJets(), warInfo.getAttackerNation().getShips(), warInfo.getAttResistance(), warInfo.getDefResistance(), warInfo.getAttmaps(), warInfo.getDefmaps());
                                defensiveWarString += "\n";
                                defensiveWarString += "|| GC: " + warInfo.getAttackerNation().getGroundcontrol() + " AS: " + warInfo.getAttackerNation().getAirsuperiority() + " NB: " + warInfo.getAttackerNation().getNavalblockade();
                            }
                            defensiveWarString += "```";
                            System.out.println(defensiveWarString);
                        }

                        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
                        System.out.println("Last active: " + nation.getLast_active());
                        Duration duration = nation.getActivity();

                        embedBuilder.setDescription("[" + nation.getNation() + ", " + nation.getAlliance() + "](https://politicsandwar.com/nation/id=" + warId + ") | active: " + duration.toDaysPart() + "d " + duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago");
                        if (offensiveWars.size() > 0) {
                            String offWarsStr = "";
                            for (WarInfo warInfo : offensiveWars) {
                                duration = warInfo.getDefenderNation().getActivity();
                                offWarsStr += "[war](https://politicsandwar.com/nation/war/timeline/war=" + warInfo.getId() + ") - [" + warInfo.getDefenderNation().getNation_name() + "](https://politicsandwar.com/nation/id=" + warInfo.getDefenderNation().getId() + ") - (" + warInfo.getDefenderNation().getId() + ") | active: " + duration.toDaysPart() + "d " + duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago";
                                offWarsStr += "\n";
                            }
                            embedBuilder.addField("Offensive Wars", offWarsStr);
                        }
                        if (defensiveWars.size() > 0) {
                            String defWarStr = "";
                            for (WarInfo warInfo : defensiveWars) {
                                duration = warInfo.getDefenderNation().getActivity();
                                defWarStr += "[war](https://politicsandwar.com/nation/war/timeline/war=" + warInfo.getId() + ") - [" + warInfo.getAttackerNation().getNation_name() + "](https://politicsandwar.com/nation/id=" + warInfo.getAttackerNation().getId() + ") - (" + warInfo.getAttackerNation().getId() + ") | active: " + duration.toDaysPart() + "d " + duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago";
                                defWarStr += "\n";
                            }
                            embedBuilder.addField("Defensive Wars", defWarStr);

                        }

                        warroomChannel.asServerTextChannel().get().sendMessage(nationInfo);
                        warroomChannel.asServerTextChannel().get().sendMessage("**OFFENSIVE WARS** (" + offWarCount + "/6)");
                        if (offensiveWarString.length() > 0)
                            warroomChannel.asServerTextChannel().get().sendMessage(offensiveWarString);
                        warroomChannel.asServerTextChannel().get().sendMessage("**DEFENSIVE WARS** (" + defWarCount + "/3)");
                        if (defensiveWarString.length() > 0)
                            warroomChannel.asServerTextChannel().get().sendMessage(defensiveWarString);
                        warroomChannel.asServerTextChannel().get().sendMessage(embedBuilder);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            } else if (messageCreateEvent.getMessageContent().equals("+wrd") && messageCreateEvent.getMessageAuthor().canManageMessagesInTextChannel() && warRoomDao.findWarroomByChannelid(messageCreateEvent.getChannel().getIdAsString()) != null) {
                messageCreateEvent.getMessage().reply("Deleting channel...");
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                warRoomDao.delete(warRoomDao.findWarroomByChannelid(messageCreateEvent.getChannel().getIdAsString()));
                messageCreateEvent.getChannel().asServerTextChannel().get().delete();

            } else if (messageCreateEvent.getMessageContent().startsWith("+w")) {

                String messageContent = messageCreateEvent.getMessageContent();
                messageContent = messageContent.replace(" ", ",");
                List<String> list = Arrays.asList(messageContent.split(","));

                if (list.size() == 2) {

                    int warId = Integer.parseInt(list.get(1));

                    try {


//                        String nationName = MilUtil.getNationName(warId);

                        List<List<WarInfo>> nationWars = WarUtil.getNationWars(warId);


                        List<WarInfo> offensiveWars = nationWars.get(0);
                        List<WarInfo> defensiveWars = nationWars.get(1);
                        Nation nation = WarUtil.getNationInfo(warId);

                        String nationInfo = "```apache\n";
                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-12s %-12s", "NAME", "AA", "SCORE", "SOLDIER", "TANK", "AIR", "SHIP", "RESIST", "MAPs");
                        nationInfo += "\n";
                        nationInfo += " ";
                        nationInfo += "\n";
                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", nation.getNation(), nation.getAcronym(), (int) nation.getScore(), nation.getSoldiers(), nation.getTanks(), nation.getAircraft(), nation.getShips(), "ag", "de", "ag", "de");
                        nationInfo += "\n";
                        nationInfo += "```";

                        String offensiveWarString = "";
                        int offWarCount = 0;
                        if (offensiveWars.size() > 0) {
                            offWarCount = offensiveWars.size();
                            offensiveWarString = "```apache\n";
                            for (int i = 0; i <= offensiveWars.size() - 1; i++) {
                                WarInfo warInfo = offensiveWars.get(i);
                                offensiveWarString += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", warInfo.getDefenderNation().getNation_name(), warInfo.getDefenderNation().getAaAcronym(), (int) warInfo.getDefenderNation().getScore(), warInfo.getDefenderNation().getSoldiers(), warInfo.getDefenderNation().getTanks(), warInfo.getDefenderNation().getJets(), warInfo.getDefenderNation().getShips(), warInfo.getAttResistance(), warInfo.getDefResistance(), warInfo.getAttmaps(), warInfo.getDefmaps());
                                offensiveWarString += "\n";
                                offensiveWarString += "|| GC: " + warInfo.getDefenderNation().getGroundcontrol() + " AS: " + warInfo.getDefenderNation().getAirsuperiority() + " NB: " + warInfo.getDefenderNation().getNavalblockade();
                                offensiveWarString += "\n";
                            }
                            offensiveWarString += "```";
                        }

                        String defensiveWarString = "";
                        int defWarCount = 0;
                        if (defensiveWars.size() > 0) {
                            defWarCount = defensiveWars.size();
                            defensiveWarString = "```apache\n";
                            for (int i = 0; i <= defensiveWars.size() - 1; i++) {
                                WarInfo warInfo = defensiveWars.get(i);
                                defensiveWarString += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", warInfo.getAttackerNation().getNation_name(), warInfo.getAttackerNation().getAaAcronym(), (int) warInfo.getAttackerNation().getScore(), warInfo.getAttackerNation().getSoldiers(), warInfo.getAttackerNation().getTanks(), warInfo.getAttackerNation().getJets(), warInfo.getAttackerNation().getShips(), warInfo.getAttResistance(), warInfo.getDefResistance(), warInfo.getAttmaps(), warInfo.getDefmaps());
                                defensiveWarString += "\n";
                                defensiveWarString += "|| GC: " + warInfo.getAttackerNation().getGroundcontrol() + " AS: " + warInfo.getAttackerNation().getAirsuperiority() + " NB: " + warInfo.getAttackerNation().getNavalblockade();
                            }
                            defensiveWarString += "```";
                            System.out.println(defensiveWarString);
                        }

                        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
                        System.out.println("Last active: " + nation.getLast_active());
                        Duration duration = nation.getActivity();

                        embedBuilder.setDescription("[" + nation.getNation() + ", " + nation.getAlliance() + "](https://politicsandwar.com/nation/id=" + warId + ") | active: " + duration.toDaysPart() + "d " + duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago");
                        if (offensiveWars.size() > 0) {
                            String offWarsStr = "";
                            for (WarInfo warInfo : offensiveWars) {
                                duration = warInfo.getDefenderNation().getActivity();
                                offWarsStr += "[war](https://politicsandwar.com/nation/war/timeline/war=" + warInfo.getId() + ") - [" + warInfo.getDefenderNation().getNation_name() + "](https://politicsandwar.com/nation/id=" + warInfo.getDefenderNation().getId() + ") - (" + warInfo.getDefenderNation().getId() + ") | active: " + duration.toDaysPart() + "d " + duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago";
                                offWarsStr += "\n";
                            }
                            embedBuilder.addField("Offensive Wars", offWarsStr);
                        }
                        if (defensiveWars.size() > 0) {
                            String defWarStr = "";
                            for (WarInfo warInfo : defensiveWars) {
                                duration = warInfo.getDefenderNation().getActivity();
                                defWarStr += "[war](https://politicsandwar.com/nation/war/timeline/war=" + warInfo.getId() + ") - [" + warInfo.getAttackerNation().getNation_name() + "](https://politicsandwar.com/nation/id=" + warInfo.getAttackerNation().getId() + ") - (" + warInfo.getAttackerNation().getId() + ") | active: " + duration.toDaysPart() + "d " + duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago";
                                defWarStr += "\n";
                            }
                            embedBuilder.addField("Defensive Wars", defWarStr);

                        }

                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(nationInfo);
                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage("**OFFENSIVE WARS** (" + offWarCount + "/6)");
                        if (offensiveWarString.length() > 0)
                            messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(offensiveWarString);
                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage("**DEFENSIVE WARS** (" + defWarCount + "/3)");
                        if (defensiveWarString.length() > 0)
                            messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(defensiveWarString);
                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(embedBuilder);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }


            } else if (messageCreateEvent.getMessageContent().equalsIgnoreCase("+help")) {
                String response = "``` \n+wrc [nation_id] to create a war room for the specified nation. \n+wra [@mention] to add someone to the current warrom. \n+wrr [@mention] to remove someone from the current warroom. \n+wrd to delete the current warroom. (requires gov) \n+w [nation_id] to print out war info regarding specified nation. \n```";
                messageCreateEvent.getChannel().sendMessage(response);
            }
        }
    }
}
