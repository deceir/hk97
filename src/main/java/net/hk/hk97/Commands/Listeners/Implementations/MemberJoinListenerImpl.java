package net.hk.hk97.Commands.Listeners.Implementations;

import net.hk.hk97.Commands.Listeners.MemberJoinListener;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.springframework.stereotype.Component;

@Component
public class MemberJoinListenerImpl implements MemberJoinListener {
    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent serverMemberJoinEvent) {
        if (serverMemberJoinEvent.getServer().getIdAsString().equals("1016240494948397066")) {
            try {
                Role role = serverMemberJoinEvent.getApi().getRoleById("1016476498628202538").get();
                serverMemberJoinEvent.getUser().addRole(role);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
