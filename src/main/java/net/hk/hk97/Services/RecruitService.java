package net.hk.hk97.Services;

import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.domains.subdomains.SNationContainer;
import net.hk.hk97.Config;
import net.hk.hk97.Models.Message.Messenger;
import net.hk.hk97.Models.Recruit;
import net.hk.hk97.Repositories.RecruitRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Configuration
@EnableScheduling
public class RecruitService {

    PoliticsAndWar pnw = PoliticsAndWar.getDefaultInstance(Config.adamApiKey);

    @Autowired
    RecruitRepository recruitDao;

    private String adviceMessageStr = "Hello!\n\nI see you’re new, and so I want to try and help you out with some basic tips for you:\n\nYou should raid inactive players! As a new player, raiding is by far the best way to make money, and it can quickly boost you in the game and allow you to catch up with long-term players. To do this properly, you most likely want to join an alliance with technology that helps you raid (my alliance, [url=https://politicsandwar.com/alliance/id=4829]The Enterprise[/url], has some of the best tech, but any alliance with the tech will do). I cannot say this enough: raiding is extremely useful, and will make the game much easier!\n\nTo help you raid, you should change your war policy to [b]Pirate[/b], which will massively increase your profits. You can do this by going to the nation edit tab on the side and changing your war policy to pirate! While you’re here, change the domestic policy to imperialism, as it’ll make you the most money! If you think you might want to change it later, don’t worry as the cooldown for changing policies is only 5 days.\n\nOutside of raiding, economy-wise, you should [b]always[/b] buy infrastructure in multiples of 100 and land in multiples of 500. This is due to a quirk of the game: Buying in these multiples is the cheapest way to buy! For example, buying 50 infrastructure twice costs more than buying 100 infrastructure once.\n\nYour infrastructure and land per city should generally go no higher than 1000 at the start of the game; at the start, cities are so cheap, so it’s better to focus on them! More cities lets you make more money with more buildings and such, and also lets you find better raid targets at the start. You should stop this at 3 cities, since that’s where the best raiding targets are.\n\n    If you do raid, which as I’ve described above, you should, there’s one thing to note: raiding will remove your status as a beige nation. As long as you’re in a good alliance, this is fine! Nobody dares to attack good alliances, since they’ll be countered and lose the war. But if you aren’t in a good alliance, you will likely be attacked, and that’s not good. As such, in order to raid properly, you should join an alliance! I think that my alliance is quite good ([url=https://discord.gg/M2qhH3G2Fg]discord[/url]), but this isn’t a recruitment message, so I won’t try to shill you too hard.\n\n    If you have any other questions, [b]feel free to reply[/b] to this message! I’m always happy to help, and can try to help with any problems you have.";

    private String initialMessage = "[img]https://cdn.discordapp.com/attachments/442900043683332096/480504024609849354/es_banner_snek_test.png[/img]\nHello! I’d like to invite you to interview with us at the [b][url=https://politicsandwar.com/alliance/id=4829]Enterprise Corporation[/url].[/b] We are a wholly-owned subsidiary of the [b][url=https://politicsandwar.com/alliance/id=831]Syndicate[/url][/b] Corporation that exists to recruit and train members to gain membership to the Syndicate. The Syndicate is one of the oldest and strongest alliances in the game and has some of the most skilled players in Politics and War.\n\nThe Enterprise has many ways to train you to become skilled, including:\n • [b]Expert guides[/b] covering all aspects of the game to ensure you become a master.\n • Bots to help you find the best targets to raid.\n • A mentor from The Syndicate to teach you the game and answer your questions.\n\nIn addition, The Enterprise also offers you direct bonuses to help you become stronger, such as:\n • [b]First 20 cities fully paid for[/b], with more upon promotion to The Syndicate.\n • Innovative technology to help you easily make [b]$70M+ a week[/b] as soon as you join.\n • Grants for 4 projects.\n • Complete protection from raids, using The Syndicate anti-raid team.\n\nThe Enterprise also provides plenty of leisure activities for its junior associates, such as:\n • [b]Weekly multiplayer games[/b], with a different game every week.\n • Plenty of members willing to join you in games at any time.\n • A great, active community.\n\nAs long as you’re active and willing to learn, we’d love to have you in the enterprise. If you’d like to join, just apply in-game [url=https://politicsandwar.com/alliance/join/id=4829]here[/url], and apply in the discord [url=https://discord.gg/jYd4RPeGAA]here[/url] by going into #applications and following the instructions in the channel description.\n[img]https://media.discordapp.net/attachments/829899033522339911/848674671553282068/Comp_1_11.gif[/img]";

    private String tghMessage = "In the annals of history, there are few forces as awe-inspiring and unstoppable as the Mongol Hordes. Just as Genghis Khan once united the nomadic tribes of the steppes into a formidable empire, our alliance, the Golden Horde, stands united under a common banner to conquer the world of Politics and War.\nLike the swift riders of the Mongol Empire, our members are known for their unparalleled speed and precision in both diplomacy and warfare. We believe in the principles of strength through unity and collaboration through camaraderie. Just as the Mongols adapted to diverse terrains and cultures, we adapt to the ever-changing landscape of global politics, forging alliances, and expanding our reach.\nThe spirit of the Golden Horde is characterized by determination, fearlessness, and a relentless pursuit of victory. We are not bound by borders or limitations; instead, we transcend boundaries to establish a legacy of dominance. Our diplomatic acumen and military prowess make us a force to be reckoned with on the world stage.\nAs a member of the Golden Horde, you will find a community that values your contribution and supports your growth. Whether you are a seasoned strategist or a newcomer eager to learn, our alliance provides opportunities for development and advancement. Together, we ride into the realm, conquering challenges, securing resources, and leaving our mark on the world of Politics and War.\n\nJoin the Golden Horde today, and become part of a legacy that echoes the indomitable spirit of the ancient Mongol warriors. Together, we ride towards glory and domination, forging alliances, and leaving our rivals in awe of our might. The world of Politics and War will tremble before the horde!\n[url=https://politicsandwar.com/alliance/id=4567]Alliance[/url] \n[url=https://discord.gg/PpzcNDA]Discord Server[/url]\nJoin the Golden Horde and Ride to Victory!";

    private String newTghMessage = "Hold your horses! Don’t delete me yet! Still here? Great! \n" +
            "\n" +
            "Here at the Golden Horde, we believe that through sharing our experiences with each other, everyone gets better. We believe that hard work leads to both individual and collective success. We believe that a close community is the cornerstone for an effective alliance. \n" +
            "\n" +
            "If you want that too, then The Golden Horde is the place for you. \n" +
            "\n" +
            "We have decades of combined experience between just a handful of members who are willing to teach you how to run a company, become a ruthless raider, wealthy trader, or a member of our government! \n" +
            "\n" +
            "We reject the idea common among larger alliances that new members are a waste of time and that experienced members should only talk to other experienced members. We bring together people of all experience levels to create a tight knit community where you are always welcome to bounce ideas off of or ask questions.\n" +
            "\n" +
            "We view every new member as the individual that we know you are. You are not just a number when you join us. We cap how many trainees we let in to guarantee individualized support and bring in some of the most experienced members of the game to train you!\n" +
            "\n" +
            "Pack up your bags, come see our [url=https://discord.gg/PpzcNDA]Discord community[/url], and see if this is the place for you! You can also [url=https://politicsandwar.com/alliance/id=4567]find us in-game[/url] as well! \n" +
            "\n" +
            "We look forward to seeing where you go with the Horde!\n";


            @Scheduled(cron = "0 */10 * * * *")
    public void recruitService() throws IOException, JSONException {

        List<SNationContainer> listOfNations = pnw.getNations(false,0,1500,0).getNationsContainer();

        for (SNationContainer nation : listOfNations) {
            boolean firstMessage = false;

            Recruit recruit = null;

            if ( recruitDao.existsById((long) nation.getNationId()) ) {
                recruit = recruitDao.findById(nation.getNationId());
                if (recruit.initial_message) {
                    firstMessage = true;
                }
            } else {
                recruit = new Recruit();
                recruit.setId(nation.getNationId());
            }




//            Channel channel = api.getChannelById("949114516652822629").get();

            if (nation.getMinutessinceactive() < 60 && !firstMessage && nation.getScore() < 1500) {

                Messenger.sendMessagePnw(nation.getNationId(), "ACTION REQUIRED: Find Experienced Support and Tight Knit Community Today!", newTghMessage, Config.itachiPnwKey);

                recruit.setInitial_message(true);
                recruitDao.save(recruit);
//                channel.asTextChannel().get().sendMessage("I would send an initial message to this nation: https://politicsandwar.com/nation/id=" + nation.getNationId());


            }

        }


    }

}