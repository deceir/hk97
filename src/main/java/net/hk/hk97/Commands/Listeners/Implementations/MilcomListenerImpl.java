package net.hk.hk97.Commands.Listeners.Implementations;

import net.hk.hk97.Config;
import net.hk.hk97.Commands.Listeners.MilComListener;
import net.hk.hk97.Models.Nation;
import net.hk.hk97.Models.WarInfo;
import net.hk.hk97.Models.Warroom;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WarroomRepository;
import net.hk.hk97.Services.Util.MilUtil;
import net.hk.hk97.Services.Util.WarUtil;
import org.apache.commons.codec.binary.StringUtils;
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

                        messageCreateEvent.getMessage().reply("War Room created at: <#" + warroomChannel.getIdAsString() + ">");

                        Warroom warroom = new Warroom();
                        warroom.setChannelid(warroomChannel.getIdAsString());
                        warroom.setId(Integer.parseInt(list.get(1)));
                        warRoomDao.save(warroom);


                        //get warinfo list

                        List<List<WarInfo>> nationWars = WarUtil.getNationWars(warId);

                        List<WarInfo> offensiveWars = nationWars.get(0);
                        List<WarInfo> defensiveWars = nationWars.get(1);
                        Nation nation = WarUtil.getNationInfo(warId);


                        String name = "";
                        String acronym = "";
                        String score = "";
                        String soldiers = "";
                        String tanks = "";
                        String air = "";
                        String ships = "";
                        String resist = "";
                        String maps = "";
                        String cities = "";


                        if (nation.getNation().length() > 11) {
                            name = nation.getNation().substring(0, 11);
                        } else if (nation.getNation().length() == 11) {
                            name = nation.getNation();
                        } else if (nation.getNation().length() < 11) {
                            int diff = 11 - nation.getNation().length();
                            name = nation.getNation();
                            for (int i = diff; i > 0; i--) {
                                name += " ";
                            }
                        }

                        // check if acronym has non-alphanumeric character
                        for (int e = acronym.length(); e > 0; e--) {
                            try {
                                char c = acronym.charAt(e);
                                if (!Character.isLetterOrDigit(e)) {
                                    acronym = nation.getAlliance().substring(0, 3);
                                }
                            } catch (Exception exception) {
                                System.out.println(exception);
                                acronym = "---";

                            }
                        }
                        try {
                            if (nation.getAcronym().length() > 3) {
                                acronym = nation.getAcronym().substring(0, 3);
                            } else {
                                acronym = nation.getAcronym();
                            }
                        } catch (Exception e) {
                            acronym = " - ";
                        }
                        if (acronym.equals("")) {
                            acronym = " - ";
                        } else if (acronym.length() == 1) {
                            acronym += "  ";
                        } else if (acronym.length() == 2) {
                            acronym += " ";
                        }

                        cities = nation.getCities() + "";
                        if (cities.length() != 2) {
                            int diff = 2 - cities.length();
                            for (int i = diff; i > 0; i--) {
                                cities += " ";
                            }
                        }


                        score = (int) nation.getScore() + "";
                        int digits = score.length();
                        if (digits != 5) {
                            digits = 5 - digits;
                            for (int i = digits; i > 0; i--) {
                                score += " ";
                            }
                        }
                        System.out.println("soldiers: " + nation.getSoldiers());
                        soldiers = nation.getSoldiers() + "";
                        if (soldiers.equals("")) soldiers = 0 + "";
                        if (soldiers.length() != 9) {
                            digits = soldiers.length();
                            digits = 9 - digits;
                            for (int i = digits; i > 0; i--) {
                                soldiers += " ";
                            }
                        }
                        tanks = nation.getTanks() + "";
                        if (tanks.equals("")) tanks = 0 + "";
                        if (tanks.length() != 6) {
                            digits = tanks.length();
                            digits = 6 - digits;
                            for (int i = digits; i > 0; i--) {
                                tanks += " ";
                            }
                        }
                        air = nation.getAircraft() + "";
                        if (air.equals("")) air = 0 + "";
                        if (air.length() != 5) {
                            digits = air.length();
                            digits = 5 - digits;
                            for (int i = digits; i > 0; i--) {
                                air += " ";
                            }
                        }
                        ships = nation.getShips() + "";
                        if (ships.equals("")) ships = 0 + "";
                        if (ships.length() != 4) {
                            digits = ships.length();
                            digits = 4 - digits;
                            for (int i = digits; i > 0; i--) {
                                tanks += " ";
                            }
                        }
                        String str = "```apache\n" +
                                "     Leader  AA  #  Score  Soldier  Tank  Air Shp |  Resist  MAPS  GC AS NB turns | ID VDS\n" +
                                name + " " + acronym + " " + cities + "  " + score + " " + soldiers + tanks + air + ships +
                                "|  ag de   ag de\n```";


//                        String nationInfo = "```apache\n";
//                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-12s %-12s", "NAME", "AA", "SCORE", "SOLDIER", "TANK", "AIR", "SHIP", "RESIST", "MAPs");
//                        nationInfo += "\n";
//                        nationInfo += " ";
//                        nationInfo += "\n";
//                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", nation.getNation(), nation.getAcronym(), (int) nation.getScore(), nation.getSoldiers(), nation.getTanks(), nation.getAircraft(), nation.getShips(), "ag", "de", "ag", "de");
//                        nationInfo += "\n";
//                        nationInfo += "```";

                        String loff_string = "";
                        String offensiveWarString = "";
                        int offWarCount = 0;
                        if (offensiveWars.size() > 0) {
                            offWarCount = offensiveWars.size();
                            offensiveWarString = "```apache\n";
                            for (int i = 0; i <= offensiveWars.size() - 1; i++) {
                                WarInfo warInfo = offensiveWars.get(i);

                                if (warInfo.getDefenderNation().getLeader_name().length() > 11) {
                                    name = warInfo.getDefenderNation().getLeader_name().substring(0, 11);
                                } else if (warInfo.getDefenderNation().getLeader_name().length() == 11) {
                                    name = warInfo.getDefenderNation().getLeader_name();
                                } else if (warInfo.getDefenderNation().getLeader_name().length() < 11) {
                                    int diff = 11 - warInfo.getDefenderNation().getLeader_name().length();
                                    name = warInfo.getDefenderNation().getLeader_name();
                                    for (int y = diff; y > 0; y--) {
                                        name += " ";
                                    }
                                }
                                //acronym check
                                for (int e = acronym.length(); e > 0; e--) {
                                    try {
                                        char c = acronym.charAt(e);
                                        if (!Character.isLetterOrDigit(e)) {
                                            acronym = warInfo.getDefenderNation().getAaname().substring(0, 3);
                                        }
                                    } catch (Exception exception) {
                                        System.out.println(exception);
                                        acronym = "---";

                                    }
                                }
                                try {
                                    if (warInfo.getDefenderNation().getAaAcronym().length() > 3) {
                                        acronym = warInfo.getDefenderNation().getAaAcronym().substring(0, 3);
                                    } else {
                                        acronym = warInfo.getDefenderNation().getAaAcronym();
                                    }
                                } catch (Exception e) {
                                    acronym = " - ";
                                }
                                if (acronym.equals("")) {
                                    acronym = " - ";
                                } else if (acronym.length() == 1) {
                                    acronym += "  ";
                                } else if (acronym.length() == 2) {
                                    acronym += " ";
                                }

                                cities = warInfo.getDefenderNation().getCities() + "";
                                if (cities.length() != 2) {
                                    int diff = 2 - cities.length();
                                    for (int y = diff; y > 0; y--) {
                                        cities += " ";
                                    }
                                }


                                score = (int) warInfo.getDefenderNation().getScore() + "";
                                digits = score.length();
                                if (digits != 5) {
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        score += " ";
                                    }
                                }
                                soldiers = warInfo.getDefenderNation().getSoldiers() + "";
                                if (soldiers.equals("")) soldiers = 0 + "";
                                if (soldiers.length() != 9) {
                                    digits = soldiers.length();
                                    digits = 9 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        soldiers += " ";
                                    }
                                }
                                tanks = warInfo.getDefenderNation().getTanks() + "";
                                if (tanks.equals("")) tanks = 0 + "";
                                if (tanks.length() != 6) {
                                    digits = tanks.length();
                                    digits = 6 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }
                                air = warInfo.getDefenderNation().getJets() + "";
                                if (air.equals("")) air = 0 + "";
                                if (air.length() != 5) {
                                    digits = air.length();
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        air += " ";
                                    }
                                }
                                ships = warInfo.getDefenderNation().getShips() + "";
                                if (ships.equals("")) ships = 0 + "";
                                if (ships.length() != 4) {
                                    digits = ships.length();
                                    digits = 4 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }

                                String res = warInfo.getAttResistance() + "  " + warInfo.getDefResistance();
                                if (res.length() != 8) {
                                    int diff = 8 - res.length();
                                    for (int x = diff; x > 0; x--) {
                                        res += " ";
                                    }
                                }

                                String mapSSs = warInfo.getAttmaps() + " " + warInfo.getDefmaps();

                                if (mapSSs.length() != 5) {
                                    int diff = 5 - mapSSs.length();
                                    for (int x = diff; x > 0; x--) {
                                        mapSSs += " ";
                                    }
                                }

                                String gc = "";
                                if (warInfo.getDefenderNation().getGroundcontrol().equals("✓")) {
                                    gc = "de";
                                } else if (warInfo.getAttackerNation().getGroundcontrol().equals("✓")) {
                                    gc = "ag";
                                } else {
                                    gc = "--";
                                }
                                String as = "";
                                if (warInfo.getDefenderNation().getAirsuperiority().equals("✓")) {
                                    as = "de";
                                } else if (warInfo.getAttackerNation().getAirsuperiority().equals("✓")) {
                                    as = "ag";
                                } else {
                                    as = "--";
                                }
                                String nb = "";
                                if (warInfo.getDefenderNation().getNavalblockade().equals("✓")) {
                                    nb = "de";
                                } else if (warInfo.getAttackerNation().getNavalblockade().equals("✓")) {
                                    nb = "ag";
                                } else {
                                    nb = "--";
                                }
                                String irondome = " ";
                                if (warInfo.getDefenderNation().isIronDome()) {
                                    irondome = "💥";
                                }
                                String vds = " ";
                                if (warInfo.getDefenderNation().isVds()) {
                                    vds = "💥";
                                }

                                loff_string += name + " " + acronym + " " + cities + "  " + score + soldiers + tanks + air + ships + " | " + res + "" + mapSSs + "  " + gc + " " + as + " " + nb + "   " + warInfo.getTurnsLeft() + " | " + irondome + " " + vds + "      " + "\n";


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

                                if (warInfo.getAttackerNation().getLeader_name().length() > 11) {
                                    name = warInfo.getAttackerNation().getLeader_name().substring(0, 11);
                                } else if (warInfo.getAttackerNation().getLeader_name().length() == 11) {
                                    name = warInfo.getAttackerNation().getLeader_name();
                                } else if (warInfo.getAttackerNation().getLeader_name().length() < 11) {
                                    int diff = 11 - warInfo.getAttackerNation().getLeader_name().length();
                                    name = warInfo.getAttackerNation().getLeader_name();
                                    for (int y = diff; y > 0; y--) {
                                        name += " ";
                                    }
                                }
                                //acronym check
                                for (int e = acronym.length(); e > 0; e--) {
                                    try {
                                        char c = acronym.charAt(e);
                                        if (!Character.isLetterOrDigit(e)) {
                                            acronym = warInfo.getAttackerNation().getAaname().substring(0, 3);
                                        }
                                    } catch (Exception exception) {
                                        System.out.println(exception);
                                        acronym = "---";
                                    }
                                }
                                try {
                                    if (warInfo.getAttackerNation().getAaAcronym().length() > 3) {
                                        acronym = warInfo.getAttackerNation().getAaAcronym().substring(0, 3);
                                    } else {
                                        acronym = warInfo.getAttackerNation().getAaAcronym();
                                    }
                                } catch (Exception e) {
                                    acronym = " - ";
                                }
                                if (acronym.equals("")) {
                                    acronym = " - ";
                                } else if (acronym.length() == 1) {
                                    acronym += "  ";
                                } else if (acronym.length() == 2) {
                                    acronym += " ";
                                }

                                cities = warInfo.getAttackerNation().getCities() + "";
                                if (cities.length() != 2) {
                                    int diff = 2 - cities.length();
                                    for (int y = diff; y > 0; y--) {
                                        cities += " ";
                                    }
                                }


                                score = (int) warInfo.getAttackerNation().getScore() + "";
                                digits = score.length();
                                if (digits != 5) {
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        score += " ";
                                    }
                                }
                                soldiers = warInfo.getAttackerNation().getSoldiers() + "";
                                if (soldiers.equals("")) soldiers = 0 + "";
                                if (soldiers.length() != 9) {
                                    digits = soldiers.length();
                                    digits = 9 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        soldiers += " ";
                                    }
                                }
                                tanks = warInfo.getAttackerNation().getTanks() + "";
                                if (tanks.equals("")) tanks = 0 + "";
                                if (tanks.length() != 6) {
                                    digits = tanks.length();
                                    digits = 6 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }
                                air = warInfo.getAttackerNation().getJets() + "";
                                if (air.equals("")) air = 0 + "";
                                if (air.length() != 5) {
                                    digits = air.length();
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        air += " ";
                                    }
                                }
                                ships = warInfo.getAttackerNation().getShips() + "";
                                if (ships.equals("")) ships = 0 + "";
                                if (ships.length() != 4) {
                                    digits = ships.length();
                                    digits = 4 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }

                                String res = warInfo.getAttResistance() + "  " + warInfo.getDefResistance();
                                if (res.length() != 8) {
                                    int diff = 8 - res.length();
                                    for (int x = diff; x > 0; x--) {
                                        res += " ";
                                    }
                                }

                                String mapSSs = warInfo.getAttmaps() + " " + warInfo.getDefmaps();

                                if (mapSSs.length() != 5) {
                                    int diff = 5 - mapSSs.length();
                                    for (int x = diff; x > 0; x--) {
                                        mapSSs += " ";
                                    }
                                }

                                String gc = "";
                                if (warInfo.getDefenderNation().getGroundcontrol().equals("✓")) {
                                    gc = "de";
                                } else if (warInfo.getAttackerNation().getGroundcontrol().equals("✓")) {
                                    gc = "ag";
                                } else {
                                    gc = "--";
                                }
                                String as = "";
                                if (warInfo.getDefenderNation().getAirsuperiority().equals("✓")) {
                                    as = "de";
                                } else if (warInfo.getAttackerNation().getAirsuperiority().equals("✓")) {
                                    as = "ag";
                                } else {
                                    as = "--";
                                }
                                String nb = "";
                                if (warInfo.getDefenderNation().getNavalblockade().equals("✓")) {
                                    nb = "de";
                                } else if (warInfo.getAttackerNation().getNavalblockade().equals("✓")) {
                                    nb = "ag";
                                } else {
                                    nb = "--";
                                }
                                String irondome = " ";
                                if (warInfo.getDefenderNation().isIronDome()) {
                                    irondome = "💥";
                                }
                                String vds = " ";
                                if (warInfo.getAttackerNation().isVds()) {
                                    vds = "💥";
                                }

                                defensiveWarString += name + " " + acronym + " " + cities + "  " + score + soldiers + tanks + air + ships + " | " + res + "" + mapSSs + "  " + gc + " " + as + " " + nb + "   " + warInfo.getTurnsLeft() + " | " + irondome + " " + vds + "      " + "\n";


                            }
                            offensiveWarString += "```";
                        }
                        defensiveWarString += "```";
                        System.out.println(defensiveWarString);
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

                        warroomChannel.asServerTextChannel().get().sendMessage(str);
//                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(nationInfo);
                        warroomChannel.asServerTextChannel().get().sendMessage("**OFFENSIVE WARS** (" + offWarCount + "/6)");
//                        if (offensiveWarString.length() > 0)
//                            messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(offensiveWarString);
                        String sending = "```apache\n" + loff_string + "\n```";
                        if (loff_string.length() > 0)
                            warroomChannel.asServerTextChannel().get().sendMessage(sending);
                        warroomChannel.asServerTextChannel().get().sendMessage("**DEFENSIVE WARS** (" + defWarCount + "/3)");
                        if (defensiveWarString.length() > 3)
                            warroomChannel.asServerTextChannel().get().sendMessage(defensiveWarString);
                        warroomChannel.asServerTextChannel().get().sendMessage(embedBuilder);


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                } else {
                    messageCreateEvent.getChannel().sendMessage("Error creating war room. You did not enter correct arguments.\nProper syntax is:\n`+wrc [nationid]");
                }
            } else if (messageCreateEvent.getMessageContent().startsWith("+wra")) {


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

                                List<net.hk.hk97.Models.User> member = userDao.getUsersByLeadernameContainingIgnoreCase(list.get(1));
                                for (net.hk.hk97.Models.User user : member) {
                                    System.out.println("id: " + user.getDiscordid());
                                }
                                User user = api.getUserById(member.get(0).getDiscordid()).get();

                                boolean isMember = false;

                                if (userDao.findById(user.getIdAsString()).isPresent()) {
                                    System.out.println("is a member");
                                    isMember = true;
                                }


                                if (isMember) {
                                    Channel channel = messageCreateEvent.getChannel();
                                    RegularServerChannelUpdater scu = channel.asServerTextChannel().get().createUpdater();

                                    Permissions permissions = new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build();

                                    scu.addPermissionOverwrite(user, permissions);
                                    scu.update();
//                            new ServerChannelUpdater(channel.asServerTextChannel().get()).addPermissionOverwrite(messageCreateEvent.getMessageAuthor().asUser().get(), permissions).update();
                                    channel.asServerTextChannel().get().sendMessage(user.getDiscriminatedName() + " has been added to this room.");
                                } else {
                                    Channel channel = messageCreateEvent.getChannel();
                                    channel.asServerTextChannel().get().sendMessage(user.getDiscriminatedName() + " is not a member.");
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
                            try {

                                List<net.hk.hk97.Models.User> member = userDao.getUsersByLeadernameContainingIgnoreCase(list.get(1));
                                for (net.hk.hk97.Models.User user : member) {
                                    System.out.println("id: " + user.getDiscordid());
                                }
                                User user = api.getUserById(member.get(0).getDiscordid()).get();

                                boolean isMember = false;

                                if (userDao.findById(user.getIdAsString()).isPresent()) {
                                    System.out.println("is a member");
                                    isMember = true;
                                }


                                if (isMember) {
                                    Channel channel = messageCreateEvent.getChannel();
                                    RegularServerChannelUpdater scu = channel.asServerTextChannel().get().createUpdater();

                                    Permissions permissions = new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL).build();

                                    scu.addPermissionOverwrite(user, permissions);
                                    scu.update();
//                            new ServerChannelUpdater(channel.asServerTextChannel().get()).addPermissionOverwrite(messageCreateEvent.getMessageAuthor().asUser().get(), permissions).update();
                                    channel.asServerTextChannel().get().sendMessage(user.getDiscriminatedName() + " has been removed from this room.");
                                } else {
                                    Channel channel = messageCreateEvent.getChannel();
                                    channel.asServerTextChannel().get().sendMessage(user.getDiscriminatedName() + " is not a member.");
                                }
                            } catch (Exception exception) {
                                messageCreateEvent.getChannel().sendMessage("Could not find user.");
                            }
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

                        String name = "";
                        String acronym = "";
                        String score = "";
                        String soldiers = "";
                        String tanks = "";
                        String air = "";
                        String ships = "";
                        String resist = "";
                        String maps = "";
                        String cities = "";


                        if (nation.getNation().length() > 11) {
                            name = nation.getNation().substring(0, 11);
                        } else if (nation.getNation().length() == 11) {
                            name = nation.getNation();
                        } else if (nation.getNation().length() < 11) {
                            int diff = 11 - nation.getNation().length();
                            name = nation.getNation();
                            for (int i = diff; i > 0; i--) {
                                name += " ";
                            }
                        }

                        // check if acronym has non-alphanumeric character
                        for (int e = acronym.length(); e > 0; e--) {
                            try {
                                char c = acronym.charAt(e);
                                if (!Character.isLetterOrDigit(e)) {
                                    acronym = nation.getAlliance().substring(0, 3);
                                }
                            } catch (Exception exception) {
                                System.out.println(exception);
                                acronym = "---";

                            }
                        }
                        try {
                            if (nation.getAcronym().length() > 3) {
                                acronym = nation.getAcronym().substring(0, 3);
                            } else {
                                acronym = nation.getAcronym();
                            }
                        } catch (Exception e) {
                            acronym = " - ";
                        }
                        if (acronym.equals("")) {
                            acronym = " - ";
                        } else if (acronym.length() == 1) {
                            acronym += "  ";
                        } else if (acronym.length() == 2) {
                            acronym += " ";
                        }

                        cities = nation.getCities() + "";
                        if (cities.length() != 2) {
                            int diff = 2 - cities.length();
                            for (int i = diff; i > 0; i--) {
                                cities += " ";
                            }
                        }


                        score = (int) nation.getScore() + "";
                        int digits = score.length();
                        if (digits != 5) {
                            digits = 5 - digits;
                            for (int i = digits; i > 0; i--) {
                                score += " ";
                            }
                        }
                        System.out.println("soldiers: " + nation.getSoldiers());
                        soldiers = nation.getSoldiers() + "";
                        if (soldiers.equals("")) soldiers = 0 + "";
                        if (soldiers.length() != 9) {
                            digits = soldiers.length();
                            digits = 9 - digits;
                            for (int i = digits; i > 0; i--) {
                                soldiers += " ";
                            }
                        }
                        tanks = nation.getTanks() + "";
                        if (tanks.equals("")) tanks = 0 + "";
                        if (tanks.length() != 6) {
                            digits = tanks.length();
                            digits = 6 - digits;
                            for (int i = digits; i > 0; i--) {
                                tanks += " ";
                            }
                        }
                        air = nation.getAircraft() + "";
                        if (air.equals("")) air = 0 + "";
                        if (air.length() != 5) {
                            digits = air.length();
                            digits = 5 - digits;
                            for (int i = digits; i > 0; i--) {
                                air += " ";
                            }
                        }
                        ships = nation.getShips() + "";
                        if (ships.equals("")) ships = 0 + "";
                        if (ships.length() != 4) {
                            digits = ships.length();
                            digits = 4 - digits;
                            for (int i = digits; i > 0; i--) {
                                tanks += " ";
                            }
                        }
                        String str = "```apache\n" +
                                "     Leader  AA  #  Score  Soldier  Tank  Air Shp |  Resist  MAPS  GC AS NB turns | ID VDS\n" +
                                name + " " + acronym + " " + cities + "  " + score + " " + soldiers + tanks + air + ships +
                                "|  ag de   ag de\n```";


//                        String nationInfo = "```apache\n";
//                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-12s %-12s", "NAME", "AA", "SCORE", "SOLDIER", "TANK", "AIR", "SHIP", "RESIST", "MAPs");
//                        nationInfo += "\n";
//                        nationInfo += " ";
//                        nationInfo += "\n";
//                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", nation.getNation(), nation.getAcronym(), (int) nation.getScore(), nation.getSoldiers(), nation.getTanks(), nation.getAircraft(), nation.getShips(), "ag", "de", "ag", "de");
//                        nationInfo += "\n";
//                        nationInfo += "```";

                        String loff_string = "";
                        String offensiveWarString = "";
                        int offWarCount = 0;
                        if (offensiveWars.size() > 0) {
                            offWarCount = offensiveWars.size();
                            offensiveWarString = "```apache\n";
                            for (int i = 0; i <= offensiveWars.size() - 1; i++) {
                                WarInfo warInfo = offensiveWars.get(i);

                                if (warInfo.getDefenderNation().getLeader_name().length() > 11) {
                                    name = warInfo.getDefenderNation().getLeader_name().substring(0, 11);
                                } else if (warInfo.getDefenderNation().getLeader_name().length() == 11) {
                                    name = warInfo.getDefenderNation().getLeader_name();
                                } else if (warInfo.getDefenderNation().getLeader_name().length() < 11) {
                                    int diff = 11 - warInfo.getDefenderNation().getLeader_name().length();
                                    name = warInfo.getDefenderNation().getLeader_name();
                                    for (int y = diff; y > 0; y--) {
                                        name += " ";
                                    }
                                }
                                //acronym check
                                for (int e = acronym.length(); e > 0; e--) {
                                    try {
                                        char c = acronym.charAt(e);
                                        if (!Character.isLetterOrDigit(e)) {
                                            acronym = warInfo.getDefenderNation().getAaname().substring(0, 3);
                                        }
                                    } catch (Exception exception) {
                                        System.out.println(exception);
                                        acronym = "---";

                                    }
                                }
                                try {
                                    if (warInfo.getDefenderNation().getAaAcronym().length() > 3) {
                                        acronym = warInfo.getDefenderNation().getAaAcronym().substring(0, 3);
                                    } else {
                                        acronym = warInfo.getDefenderNation().getAaAcronym();
                                    }
                                } catch (Exception e) {
                                    acronym = " - ";
                                }
                                if (acronym.equals("")) {
                                    acronym = " - ";
                                } else if (acronym.length() == 1) {
                                    acronym += "  ";
                                } else if (acronym.length() == 2) {
                                    acronym += " ";
                                }

                                cities = warInfo.getDefenderNation().getCities() + "";
                                if (cities.length() != 2) {
                                    int diff = 2 - cities.length();
                                    for (int y = diff; y > 0; y--) {
                                        cities += " ";
                                    }
                                }


                                score = (int) warInfo.getDefenderNation().getScore() + "";
                                digits = score.length();
                                if (digits != 5) {
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        score += " ";
                                    }
                                }
                                soldiers = warInfo.getDefenderNation().getSoldiers() + "";
                                if (soldiers.equals("")) soldiers = 0 + "";
                                if (soldiers.length() != 9) {
                                    digits = soldiers.length();
                                    digits = 9 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        soldiers += " ";
                                    }
                                }
                                tanks = warInfo.getDefenderNation().getTanks() + "";
                                if (tanks.equals("")) tanks = 0 + "";
                                if (tanks.length() != 6) {
                                    digits = tanks.length();
                                    digits = 6 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }
                                air = warInfo.getDefenderNation().getJets() + "";
                                if (air.equals("")) air = 0 + "";
                                if (air.length() != 5) {
                                    digits = air.length();
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        air += " ";
                                    }
                                }
                                ships = warInfo.getDefenderNation().getShips() + "";
                                if (ships.equals("")) ships = 0 + "";
                                if (ships.length() != 4) {
                                    digits = ships.length();
                                    digits = 4 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }

                                String res = warInfo.getAttResistance() + "  " + warInfo.getDefResistance();
                                if (res.length() != 8) {
                                    int diff = 8 - res.length();
                                    for (int x = diff; x > 0; x--) {
                                        res += " ";
                                    }
                                }

                                String mapSSs = warInfo.getAttmaps() + " " + warInfo.getDefmaps();

                                if (mapSSs.length() != 5) {
                                    int diff = 5 - mapSSs.length();
                                    for (int x = diff; x > 0; x--) {
                                        mapSSs += " ";
                                    }
                                }

                                String gc = "";
                                if (warInfo.getDefenderNation().getGroundcontrol().equals("✓")) {
                                    gc = "de";
                                } else if (warInfo.getAttackerNation().getGroundcontrol().equals("✓")) {
                                    gc = "ag";
                                } else {
                                    gc = "--";
                                }
                                String as = "";
                                if (warInfo.getDefenderNation().getAirsuperiority().equals("✓")) {
                                    as = "de";
                                } else if (warInfo.getAttackerNation().getAirsuperiority().equals("✓")) {
                                    as = "ag";
                                } else {
                                    as = "--";
                                }
                                String nb = "";
                                if (warInfo.getDefenderNation().getNavalblockade().equals("✓")) {
                                    nb = "de";
                                } else if (warInfo.getAttackerNation().getNavalblockade().equals("✓")) {
                                    nb = "ag";
                                } else {
                                    nb = "--";
                                }
                                String irondome = " ";
                                if (warInfo.getDefenderNation().isIronDome()) {
                                    irondome = "💥";
                                }
                                String vds = " ";
                                if (warInfo.getDefenderNation().isVds()) {
                                    vds = "💥";
                                }

                                loff_string += name + " " + acronym + " " + cities + "  " + score + soldiers + tanks + air + ships + " | " + res + "" + mapSSs + "  " + gc + " " + as + " " + nb + "   " + warInfo.getTurnsLeft() + " | " + irondome + " " + vds + "      " + "\n";


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

                                if (warInfo.getAttackerNation().getLeader_name().length() > 11) {
                                    name = warInfo.getAttackerNation().getLeader_name().substring(0, 11);
                                } else if (warInfo.getAttackerNation().getLeader_name().length() == 11) {
                                    name = warInfo.getAttackerNation().getLeader_name();
                                } else if (warInfo.getAttackerNation().getLeader_name().length() < 11) {
                                    int diff = 11 - warInfo.getAttackerNation().getLeader_name().length();
                                    name = warInfo.getAttackerNation().getLeader_name();
                                    for (int y = diff; y > 0; y--) {
                                        name += " ";
                                    }
                                }
                                //acronym check
                                for (int e = acronym.length(); e > 0; e--) {
                                    try {
                                        char c = acronym.charAt(e);
                                        if (!Character.isLetterOrDigit(e)) {
                                            acronym = warInfo.getAttackerNation().getAaname().substring(0, 3);
                                        }
                                    } catch (Exception exception) {
                                        System.out.println(exception);
                                        acronym = "---";
                                    }
                                }
                                try {
                                    if (warInfo.getAttackerNation().getAaAcronym().length() > 3) {
                                        acronym = warInfo.getAttackerNation().getAaAcronym().substring(0, 3);
                                    } else {
                                        acronym = warInfo.getAttackerNation().getAaAcronym();
                                    }
                                } catch (Exception e) {
                                    acronym = " - ";
                                }
                                if (acronym.equals("")) {
                                    acronym = " - ";
                                } else if (acronym.length() == 1) {
                                    acronym += "  ";
                                } else if (acronym.length() == 2) {
                                    acronym += " ";
                                }

                                cities = warInfo.getAttackerNation().getCities() + "";
                                if (cities.length() != 2) {
                                    int diff = 2 - cities.length();
                                    for (int y = diff; y > 0; y--) {
                                        cities += " ";
                                    }
                                }


                                score = (int) warInfo.getAttackerNation().getScore() + "";
                                digits = score.length();
                                if (digits != 5) {
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        score += " ";
                                    }
                                }
                                soldiers = warInfo.getAttackerNation().getSoldiers() + "";
                                if (soldiers.equals("")) soldiers = 0 + "";
                                if (soldiers.length() != 9) {
                                    digits = soldiers.length();
                                    digits = 9 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        soldiers += " ";
                                    }
                                }
                                tanks = warInfo.getAttackerNation().getTanks() + "";
                                if (tanks.equals("")) tanks = 0 + "";
                                if (tanks.length() != 6) {
                                    digits = tanks.length();
                                    digits = 6 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }
                                air = warInfo.getAttackerNation().getJets() + "";
                                if (air.equals("")) air = 0 + "";
                                if (air.length() != 5) {
                                    digits = air.length();
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        air += " ";
                                    }
                                }
                                ships = warInfo.getAttackerNation().getShips() + "";
                                if (ships.equals("")) ships = 0 + "";
                                if (ships.length() != 4) {
                                    digits = ships.length();
                                    digits = 4 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }

                                String res = warInfo.getAttResistance() + "  " + warInfo.getDefResistance();
                                if (res.length() != 8) {
                                    int diff = 8 - res.length();
                                    for (int x = diff; x > 0; x--) {
                                        res += " ";
                                    }
                                }

                                String mapSSs = warInfo.getAttmaps() + " " + warInfo.getDefmaps();

                                if (mapSSs.length() != 5) {
                                    int diff = 5 - mapSSs.length();
                                    for (int x = diff; x > 0; x--) {
                                        mapSSs += " ";
                                    }
                                }

                                String gc = "";
                                if (warInfo.getDefenderNation().getGroundcontrol().equals("✓")) {
                                    gc = "de";
                                } else if (warInfo.getAttackerNation().getGroundcontrol().equals("✓")) {
                                    gc = "ag";
                                } else {
                                    gc = "--";
                                }
                                String as = "";
                                if (warInfo.getDefenderNation().getAirsuperiority().equals("✓")) {
                                    as = "de";
                                } else if (warInfo.getAttackerNation().getAirsuperiority().equals("✓")) {
                                    as = "ag";
                                } else {
                                    as = "--";
                                }
                                String nb = "";
                                if (warInfo.getDefenderNation().getNavalblockade().equals("✓")) {
                                    nb = "de";
                                } else if (warInfo.getAttackerNation().getNavalblockade().equals("✓")) {
                                    nb = "ag";
                                } else {
                                    nb = "--";
                                }
                                String irondome = " ";
                                if (warInfo.getDefenderNation().isIronDome()) {
                                    irondome = "💥";
                                }
                                String vds = " ";
                                if (warInfo.getAttackerNation().isVds()) {
                                    vds = "💥";
                                }

                                defensiveWarString += name + " " + acronym + " " + cities + "  " + score + soldiers + tanks + air + ships + " | " + res + "" + mapSSs + "  " + gc + " " + as + " " + nb + "   " + warInfo.getTurnsLeft() + " | " + irondome + " " + vds + "      " + "\n";


                            }
                            offensiveWarString += "```";
                        }
                        defensiveWarString += "```";
                        System.out.println(defensiveWarString);

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

                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(str);
//                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(nationInfo);
                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage("**OFFENSIVE WARS** (" + offWarCount + "/6)");
//                        if (offensiveWarString.length() > 0)
//                            messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(offensiveWarString);
                        String sending = "```apache\n" + loff_string + "\n```";
                        if (loff_string.length() > 0)
                            messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(sending);
                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage("**DEFENSIVE WARS** (" + defWarCount + "/3)");
                        if (defensiveWarString.length() > 0)
                            messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(defensiveWarString);
                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(embedBuilder);

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

                        String name = "";
                        String acronym = "";
                        String score = "";
                        String soldiers = "";
                        String tanks = "";
                        String air = "";
                        String ships = "";
                        String resist = "";
                        String maps = "";
                        String cities = "";


                        if (nation.getNation().length() > 11) {
                            name = nation.getNation().substring(0, 11);
                        } else if (nation.getNation().length() == 11) {
                            name = nation.getNation();
                        } else if (nation.getNation().length() < 11) {
                            int diff = 11 - nation.getNation().length();
                            name = nation.getNation();
                            for (int i = diff; i > 0; i--) {
                                name += " ";
                            }
                        }

                        // check if acronym has non-alphanumeric character
                        for (int e = acronym.length(); e > 0; e--) {
                            try {
                                char c = acronym.charAt(e);
                                if (!Character.isLetterOrDigit(e)) {
                                    acronym = nation.getAlliance().substring(0, 3);
                                }
                            } catch (Exception exception) {
                                System.out.println(exception);
                                acronym = "---";

                            }
                        }
                        try {
                            if (nation.getAcronym().length() > 3) {
                                acronym = nation.getAcronym().substring(0, 3);
                            } else {
                                acronym = nation.getAcronym();
                            }
                        } catch (Exception e) {
                            acronym = " - ";
                        }
                        if (acronym.equals("")) {
                            acronym = " - ";
                        } else if (acronym.length() == 1) {
                            acronym += "  ";
                        } else if (acronym.length() == 2) {
                            acronym += " ";
                        }

                        cities = nation.getCities() + "";
                        if (cities.length() != 2) {
                            int diff = 2 - cities.length();
                            for (int i = diff; i > 0; i--) {
                                cities += " ";
                            }
                        }


                        score = (int) nation.getScore() + "";
                        int digits = score.length();
                        if (digits != 5) {
                            digits = 5 - digits;
                            for (int i = digits; i > 0; i--) {
                                score += " ";
                            }
                        }
                        System.out.println("soldiers: " + nation.getSoldiers());
                        soldiers = nation.getSoldiers() + "";
                        if (soldiers.equals("")) soldiers = 0 + "";
                        if (soldiers.length() != 9) {
                            digits = soldiers.length();
                            digits = 9 - digits;
                            for (int i = digits; i > 0; i--) {
                                soldiers += " ";
                            }
                        }
                        tanks = nation.getTanks() + "";
                        if (tanks.equals("")) tanks = 0 + "";
                        if (tanks.length() != 6) {
                            digits = tanks.length();
                            digits = 6 - digits;
                            for (int i = digits; i > 0; i--) {
                                tanks += " ";
                            }
                        }
                        air = nation.getAircraft() + "";
                        if (air.equals("")) air = 0 + "";
                        if (air.length() != 5) {
                            digits = air.length();
                            digits = 5 - digits;
                            for (int i = digits; i > 0; i--) {
                                air += " ";
                            }
                        }
                        ships = nation.getShips() + "";
                        if (ships.equals("")) ships = 0 + "";
                        if (ships.length() != 4) {
                            digits = ships.length();
                            digits = 4 - digits;
                            for (int i = digits; i > 0; i--) {
                                tanks += " ";
                            }
                        }
                        String str = "```apache\n" +
                                "     Leader  AA  #  Score  Soldier  Tank  Air Shp |  Resist  MAPS  GC AS NB turns | ID VDS\n" +
                                name + " " + acronym + " " + cities + "  " + score + " " + soldiers + tanks + air + ships +
                                "|  ag de   ag de\n```";


//                        String nationInfo = "```apache\n";
//                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-12s %-12s", "NAME", "AA", "SCORE", "SOLDIER", "TANK", "AIR", "SHIP", "RESIST", "MAPs");
//                        nationInfo += "\n";
//                        nationInfo += " ";
//                        nationInfo += "\n";
//                        nationInfo += String.format("%-34s %-8s %-6s %-7s %-6s %-5s %-4s | %-6s %-6s %-6s %-6s", nation.getNation(), nation.getAcronym(), (int) nation.getScore(), nation.getSoldiers(), nation.getTanks(), nation.getAircraft(), nation.getShips(), "ag", "de", "ag", "de");
//                        nationInfo += "\n";
//                        nationInfo += "```";

                        String loff_string = "";
                        String offensiveWarString = "";
                        int offWarCount = 0;
                        if (offensiveWars.size() > 0) {
                            offWarCount = offensiveWars.size();
                            offensiveWarString = "```apache\n";
                            for (int i = 0; i <= offensiveWars.size() - 1; i++) {
                                WarInfo warInfo = offensiveWars.get(i);

                                if (warInfo.getDefenderNation().getLeader_name().length() > 11) {
                                    name = warInfo.getDefenderNation().getLeader_name().substring(0, 11);
                                } else if (warInfo.getDefenderNation().getLeader_name().length() == 11) {
                                    name = warInfo.getDefenderNation().getLeader_name();
                                } else if (warInfo.getDefenderNation().getLeader_name().length() < 11) {
                                    int diff = 11 - warInfo.getDefenderNation().getLeader_name().length();
                                    name = warInfo.getDefenderNation().getLeader_name();
                                    for (int y = diff; y > 0; y--) {
                                        name += " ";
                                    }
                                }
                                //acronym check
                                for (int e = acronym.length(); e > 0; e--) {
                                    try {
                                        char c = acronym.charAt(e);
                                        if (!Character.isLetterOrDigit(e)) {
                                            acronym = warInfo.getDefenderNation().getAaname().substring(0, 3);
                                        }
                                    } catch (Exception exception) {
                                        System.out.println(exception);
                                        acronym = "---";

                                    }
                                }
                                try {
                                    if (warInfo.getDefenderNation().getAaAcronym().length() > 3) {
                                        acronym = warInfo.getDefenderNation().getAaAcronym().substring(0, 3);
                                    } else {
                                        acronym = warInfo.getDefenderNation().getAaAcronym();
                                    }
                                } catch (Exception e) {
                                    acronym = " - ";
                                }
                                if (acronym.equals("")) {
                                    acronym = " - ";
                                } else if (acronym.length() == 1) {
                                    acronym += "  ";
                                } else if (acronym.length() == 2) {
                                    acronym += " ";
                                }

                                cities = warInfo.getDefenderNation().getCities() + "";
                                if (cities.length() != 2) {
                                    int diff = 2 - cities.length();
                                    for (int y = diff; y > 0; y--) {
                                        cities += " ";
                                    }
                                }


                                score = (int) warInfo.getDefenderNation().getScore() + "";
                                digits = score.length();
                                if (digits != 5) {
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        score += " ";
                                    }
                                }
                                soldiers = warInfo.getDefenderNation().getSoldiers() + "";
                                if (soldiers.equals("")) soldiers = 0 + "";
                                if (soldiers.length() != 9) {
                                    digits = soldiers.length();
                                    digits = 9 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        soldiers += " ";
                                    }
                                }
                                tanks = warInfo.getDefenderNation().getTanks() + "";
                                if (tanks.equals("")) tanks = 0 + "";
                                if (tanks.length() != 6) {
                                    digits = tanks.length();
                                    digits = 6 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }
                                air = warInfo.getDefenderNation().getJets() + "";
                                if (air.equals("")) air = 0 + "";
                                if (air.length() != 5) {
                                    digits = air.length();
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        air += " ";
                                    }
                                }
                                ships = warInfo.getDefenderNation().getShips() + "";
                                if (ships.equals("")) ships = 0 + "";
                                if (ships.length() != 4) {
                                    digits = ships.length();
                                    digits = 4 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }

                                String res = warInfo.getAttResistance() + "  " + warInfo.getDefResistance();
                                if (res.length() != 8) {
                                    int diff = 8 - res.length();
                                    for (int x = diff; x > 0; x--) {
                                        res += " ";
                                    }
                                }

                                String mapSSs = warInfo.getAttmaps() + " " + warInfo.getDefmaps();

                                if (mapSSs.length() != 5) {
                                    int diff = 5 - mapSSs.length();
                                    for (int x = diff; x > 0; x--) {
                                        mapSSs += " ";
                                    }
                                }

                                String gc = "";
                                if (warInfo.getDefenderNation().getGroundcontrol().equals("✓")) {
                                    gc = "de";
                                } else if (warInfo.getAttackerNation().getGroundcontrol().equals("✓")) {
                                    gc = "ag";
                                } else {
                                    gc = "--";
                                }
                                String as = "";
                                if (warInfo.getDefenderNation().getAirsuperiority().equals("✓")) {
                                    as = "de";
                                } else if (warInfo.getAttackerNation().getAirsuperiority().equals("✓")) {
                                    as = "ag";
                                } else {
                                    as = "--";
                                }
                                String nb = "";
                                if (warInfo.getDefenderNation().getNavalblockade().equals("✓")) {
                                    nb = "de";
                                } else if (warInfo.getAttackerNation().getNavalblockade().equals("✓")) {
                                    nb = "ag";
                                } else {
                                    nb = "--";
                                }
                                String irondome = " ";
                                if (warInfo.getDefenderNation().isIronDome()) {
                                    irondome = "💥";
                                }
                                String vds = " ";
                                if (warInfo.getDefenderNation().isVds()) {
                                    vds = "💥";
                                }

                                loff_string += name + " " + acronym + " " + cities + "  " + score + soldiers + tanks + air + ships + " | " + res + "" + mapSSs + "  " + gc + " " + as + " " + nb + "   " + warInfo.getTurnsLeft() + " | " + irondome + " " + vds + "      " + "\n";


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

                                if (warInfo.getAttackerNation().getLeader_name().length() > 11) {
                                    name = warInfo.getAttackerNation().getLeader_name().substring(0, 11);
                                } else if (warInfo.getAttackerNation().getLeader_name().length() == 11) {
                                    name = warInfo.getAttackerNation().getLeader_name();
                                } else if (warInfo.getAttackerNation().getLeader_name().length() < 11) {
                                    int diff = 11 - warInfo.getAttackerNation().getLeader_name().length();
                                    name = warInfo.getAttackerNation().getLeader_name();
                                    for (int y = diff; y > 0; y--) {
                                        name += " ";
                                    }
                                }
                                //acronym check
                                for (int e = acronym.length(); e > 0; e--) {
                                    try {
                                        char c = acronym.charAt(e);
                                        if (!Character.isLetterOrDigit(e)) {
                                            acronym = warInfo.getAttackerNation().getAaname().substring(0, 3);
                                        }
                                    } catch (Exception exception) {
                                        System.out.println(exception);
                                        acronym = "---";
                                    }
                                }
                                try {
                                    if (warInfo.getAttackerNation().getAaAcronym().length() > 3) {
                                        acronym = warInfo.getAttackerNation().getAaAcronym().substring(0, 3);
                                    } else {
                                        acronym = warInfo.getAttackerNation().getAaAcronym();
                                    }
                                } catch (Exception e) {
                                    acronym = " - ";
                                }
                                if (acronym.equals("")) {
                                    acronym = " - ";
                                } else if (acronym.length() == 1) {
                                    acronym += "  ";
                                } else if (acronym.length() == 2) {
                                    acronym += " ";
                                }

                                cities = warInfo.getAttackerNation().getCities() + "";
                                if (cities.length() != 2) {
                                    int diff = 2 - cities.length();
                                    for (int y = diff; y > 0; y--) {
                                        cities += " ";
                                    }
                                }


                                score = (int) warInfo.getAttackerNation().getScore() + "";
                                digits = score.length();
                                if (digits != 5) {
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        score += " ";
                                    }
                                }
                                soldiers = warInfo.getAttackerNation().getSoldiers() + "";
                                if (soldiers.equals("")) soldiers = 0 + "";
                                if (soldiers.length() != 9) {
                                    digits = soldiers.length();
                                    digits = 9 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        soldiers += " ";
                                    }
                                }
                                tanks = warInfo.getAttackerNation().getTanks() + "";
                                if (tanks.equals("")) tanks = 0 + "";
                                if (tanks.length() != 6) {
                                    digits = tanks.length();
                                    digits = 6 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }
                                air = warInfo.getAttackerNation().getJets() + "";
                                if (air.equals("")) air = 0 + "";
                                if (air.length() != 5) {
                                    digits = air.length();
                                    digits = 5 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        air += " ";
                                    }
                                }
                                ships = warInfo.getAttackerNation().getShips() + "";
                                if (ships.equals("")) ships = 0 + "";
                                if (ships.length() != 4) {
                                    digits = ships.length();
                                    digits = 4 - digits;
                                    for (int y = digits; y > 0; y--) {
                                        tanks += " ";
                                    }
                                }

                                String res = warInfo.getAttResistance() + "  " + warInfo.getDefResistance();
                                if (res.length() != 8) {
                                    int diff = 8 - res.length();
                                    for (int x = diff; x > 0; x--) {
                                        res += " ";
                                    }
                                }

                                String mapSSs = warInfo.getAttmaps() + " " + warInfo.getDefmaps();

                                if (mapSSs.length() != 5) {
                                    int diff = 5 - mapSSs.length();
                                    for (int x = diff; x > 0; x--) {
                                        mapSSs += " ";
                                    }
                                }

                                String gc = "";
                                if (warInfo.getDefenderNation().getGroundcontrol().equals("✓")) {
                                    gc = "de";
                                } else if (warInfo.getAttackerNation().getGroundcontrol().equals("✓")) {
                                    gc = "ag";
                                } else {
                                    gc = "--";
                                }
                                String as = "";
                                if (warInfo.getDefenderNation().getAirsuperiority().equals("✓")) {
                                    as = "de";
                                } else if (warInfo.getAttackerNation().getAirsuperiority().equals("✓")) {
                                    as = "ag";
                                } else {
                                    as = "--";
                                }
                                String nb = "";
                                if (warInfo.getDefenderNation().getNavalblockade().equals("✓")) {
                                    nb = "de";
                                } else if (warInfo.getAttackerNation().getNavalblockade().equals("✓")) {
                                    nb = "ag";
                                } else {
                                    nb = "--";
                                }
                                String irondome = " ";
                                if (warInfo.getDefenderNation().isIronDome()) {
                                    irondome = "💥";
                                }
                                String vds = " ";
                                if (warInfo.getAttackerNation().isVds()) {
                                    vds = "💥";
                                }

                                defensiveWarString += name + " " + acronym + " " + cities + "  " + score + soldiers + tanks + air + ships + " | " + res + "" + mapSSs + "  " + gc + " " + as + " " + nb + "   " + warInfo.getTurnsLeft() + " | " + irondome + " " + vds + "      " + "\n";


                            }
                            offensiveWarString += "```";
                        }
                        defensiveWarString += "```";
                        System.out.println(defensiveWarString);


                        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
                        System.out.println("Last active: " + nation.getLast_active());
                        Duration duration = nation.getActivity();

                        embedBuilder.setDescription("[" + nation.getNation() + ", " + nation.getAlliance() + "](https://politicsandwar.com/nation/id=" + warId + ") | active: " + duration.toDaysPart() + "d " + duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago");
                        if (offensiveWars.size() > 0) {
                            String offWarsStr = "";
                            for (WarInfo warInfo : offensiveWars) {
                                duration = warInfo.getDefenderNation().getActivity();
                                offWarsStr += "[link](https://politicsandwar.com/nation/war/timeline/war=" + warInfo.getId() + ") - [" + warInfo.getDefenderNation().getNation_name() + "](https://politicsandwar.com/nation/id=" + warInfo.getDefenderNation().getId() + ") - (" + warInfo.getDefenderNation().getId() + ") | active: " + duration.toDaysPart() + "d " + duration.toHoursPart() + "h " + duration.toMinutesPart() + "m ago";
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

                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(str);
//                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(nationInfo);
                        messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage("**OFFENSIVE WARS** (" + offWarCount + "/6)");
//                        if (offensiveWarString.length() > 0)
//                            messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(offensiveWarString);
                        String sending = "```apache\n" + loff_string + "\n```";
                        if (loff_string.length() > 0)
                            messageCreateEvent.getChannel().asServerTextChannel().get().sendMessage(sending);
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
