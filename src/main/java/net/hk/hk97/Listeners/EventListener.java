package net.hk.hk97.Listeners;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hk.hk97.Hk97Application;
import net.hk.hk97.Models.User;
import net.hk.hk97.Repositories.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EventListener extends ListenerAdapter {

//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
//        if (event.getName().equalsIgnoreCase("appraise")) {
//
//            List<User> users = userRepository.findAll();
//            for (User user : users) {
//                System.out.println(user.getName());
//            }
//
//        }
//    }
}
