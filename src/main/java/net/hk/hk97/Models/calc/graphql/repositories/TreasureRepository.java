package net.hk.hk97.Models.calc.graphql.repositories;


import net.hk.hk97.Models.calc.graphql.models.Treasure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreasureRepository extends JpaRepository<Treasure, String> {
}
