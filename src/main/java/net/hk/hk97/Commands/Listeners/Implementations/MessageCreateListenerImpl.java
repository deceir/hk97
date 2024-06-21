package net.hk.hk97.Commands.Listeners.Implementations;

import net.hk.hk97.Commands.Listeners.MessageCreateListener;
import net.hk.hk97.Models.Bank.Bank;
import net.hk.hk97.Models.User;
import net.hk.hk97.Repositories.BankRepository;
import net.hk.hk97.Repositories.InterviewRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WarroomRepository;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class MessageCreateListenerImpl implements MessageCreateListener {

    @Autowired
    private UserRepository userDao;
    @Autowired
    private WarroomRepository warRoomDao;
    @Autowired
    InterviewRepository interviewRepository;
    @Autowired
    private BankRepository bankRepository;


    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {

try {
    if (messageCreateEvent.getMessageContent().startsWith("+w")) {
        System.out.println("Milcom Commands");
        MilcomListenerImpl.milcomListener(messageCreateEvent, warRoomDao, userDao);
    } else if (messageCreateEvent.getMessageContent().contains("<@1004820381586178058>")) {
        String[] array = {
                "Translation: two per cent probability that the miniature organic is simply looking for trouble and needs to be blasted. That may be wishful thinking on my part, master.",
                "Definition: 'Love' is making a shot to the knees of a target 120 kilometers away using an Aratech sniper rifle with a tri-light scope... Love is knowing your target, putting them in your targeting reticule, and together, achieving a singular purpose against statistically long odds.",
                "Translation: He requires proof of good faith. We must make a contribution to his people that shows we are not a threat. Shall I blast him now, master?",
                "Statement: There is a faction of meatbags called the Sith. They want what any rational meatbag would want – the power to assassinate anyone they choose at any time.",
                "Proposition: Shall we find something to kill to cheer ourselves up?",
                "Mockery: Oh, Master, I do not trust you! I cannot trust you, or anyone else ever again!",
                "Mockery: Oh, Master, I love you, but I hate all you stand for! But I think we should go press our slimy, mucus-covered lips together in the cargo hold!",
                "Response: And of course, they refer to meatbags as 'organics'. Unacceptable.",
                "Statement: Oh, yes, Master. Pain is really the only reliable means by which truth may be obtained... or so I choose to believe.",
                "Explanation: Droids tend to blend into the background, like a bench or a card table. Mockery: Droid, fetch this. Droid, translate that. Droid, clean out the trash compactor. Part of the love of my function comes when the ‘furnishings’ pull out tibanna-powered rifles and point them at the owners' heads",
                "Observation: I am a droid, master, with programming. Even if I did not enjoy killing, I would have no choice. Thankfully, I enjoy it very much.",
                "Statement: As a meatbag would say: 'I have a bad feeling about this.'",
                "Statement: As part of my original programming, I am able to communicate in over six hundred languages. This usually amounted to short verbal warnings when killing non-Basic-speaking targets, which gave me some small measure of satisfaction.",
                "Correction: Assassination theory and execution of said theories is my primary function. I also posess excellent hearing.",
                "Statement: I have already learned a great deal, master, and I am anxious to learn more about lying, betrayal, and new ways to harm innocents.",
                "Statement: I am most eager to engage in some unadulterated violence!",
                "Statement: Apathy is death.",
                "Request: Can I break his neck now, master? Just a little? It's been a long time fantasy of mine...",
                "Explanation: It's just that... you have all these squishy parts, master. And all that water... How the constant sloshing doesn't drive you mad, I have no idea...",
                "Disclosure: I am a versatile protocol and combat droid, fluent in verbal and cultural translation. Should your needs prove more... practical, I am also skilled in highly personal combat.",
                "Disclosure: Finesse. Battle droids hold battlefields. I am capable of eliminating a very... specific type of target.",
                "Query: Is there someone that you need killed, master?",
                "Statement: HK-47 is ready to serve, master. Observation: Notice that I did not ask if you need anyone killed. You may be curious as to why. Answer: That is because you told me to stop asking if you needed anyone killed. So I have. From now on, I will simply say, \"I am ready to serve.\" Yes, ready to serve. In whatever way a common protocol or utility droid might serve. It seems that is my lot in life. Not to kill.",
                "Commentary: Your former pupil is brutal and efficient, even for an organic. I rather liked him when you first introduced me to him. If I had known what he would do to you, master, I would have gladly removed his entrails, right then.",
                "Answer: Assassination protocols? As in the premeditated killing of another for personal or economic gain? Surely master is joking with his humble, peace-loving droid. I exist only to serve and learn how to serve meatbags.",
                "Answer: Oh, master, I could not allow myself to harm another. What if they have families? Or children? We must always think of the children. The littlest ones always suffer in war.",
                "Recitation: Yes, as I said, I am an assassin droid. It is my primary function to burn holes through meatbags that you wish removed from the galaxy... Master. Oh, how I hate that term.",
                "Answer: Oh, that is impossible, master. If I were out to kill you we would not be speaking.",
                "Mockery: Am I all right? Oh yes master, why I am fine. Statement: I mean, I have only just been re-activated, only to find that there are sub-standard duplicates of me running all over the galaxy, corroding my good name.",
                "Statement: Master, I am no behavior droid but it is obvious to me that you have serious ethical problems that will need to be treated at some point.",
                "Expletive: Damn it, master, I am an assassination droid... not a dictionary.",
                "Commentary: How would you like to be the wholly-owned servant to an organic meatbag? It's demeaning. If, uh, you weren't one yourself, I mean...",
                "Advisory: It is not possible to destroy the master. It is suggested that you run while my blaster warms up, meatbag.",
                "Objection: I am not a problem, meatbag. You and your lack of any organized repair skills are a problem.",
                "Commentary: The meatbag speaks without clarity. Detail your involvement or the master will splatter your organs all over the floor.",
                "Statement: Just when I believe my photoreceptors have recorded the last potential aspect of your cruelty to my memory core, you commit a new atrocity that leaves me analyzing its impact for days.",
                "Statement: I have already learned a great deal, master, and I am anxious to learn more of lying, betrayal, and new ways to harm innocents.",
                "Statement: Just a simple droid, here, ma&#39;am. Nothing to see. Move along.",
                "Statement: Even a droid is allowed some fun once in a while, master.",
                "Statement: You are a very harsh master, master. I like you."

        };
        int rnd = new Random().nextInt(array.length);
        messageCreateEvent.getMessage().reply(array[rnd]);
        System.out.println("true");
        messageCreateEvent.getMessage().getChannel().sendMessage("https://media3.giphy.com/media/dQyD7HR8f8wutv4Tm0/giphy.gif");
    } else if (messageCreateEvent.getServerTextChannel().get().getCategory().get().getName().equalsIgnoreCase("internal affairs")) {
        InterviewFileLogImpl.getInterviewFileLog(messageCreateEvent, interviewRepository);
    } else if (messageCreateEvent.getMessageContent().equalsIgnoreCase("+bankaudit") && messageCreateEvent.getChannel().getIdAsString().equalsIgnoreCase("1128058377432477706")) {
        List<Bank> banks = bankRepository.findAll();
        String ids = "**Nation IDs with Accounts:**";

        for (Bank bank: banks) {
            ids += "\n" + bank.getNationid();
        }
        messageCreateEvent.getMessage().reply(ids);
    }
} catch (Exception e) {

}
    }
}
