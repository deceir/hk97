package net.hk.hk97.Repositories;

import net.hk.hk97.Models.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Configuration

public interface UserRepository extends JpaRepository<User, String> {

    User findUserByNationid(long id);

    List<User> getUsersByLeadernameContainingIgnoreCase(String name);

    User getUserByDiscordid(String id);

    List<User> getUsersByNameLikeIgnoreCase(String name);

    List<User> getUsersByNameContainingIgnoreCase(String name);




}
