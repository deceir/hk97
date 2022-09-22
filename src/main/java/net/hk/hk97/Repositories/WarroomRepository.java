package net.hk.hk97.Repositories;

import net.hk.hk97.Models.Warroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarroomRepository extends JpaRepository<Warroom, Integer> {

    Warroom findWarroomByChannelid(String id);


}
