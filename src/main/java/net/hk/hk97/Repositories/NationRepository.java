package net.hk.hk97.Repositories;

import net.hk.hk97.Models.Nation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NationRepository extends JpaRepository<Nation, Long> {


    List<Nation> getNationsByColor(String color);

    List<Nation> getNationsByColorAndContinentLike(String color, String continent);


}
