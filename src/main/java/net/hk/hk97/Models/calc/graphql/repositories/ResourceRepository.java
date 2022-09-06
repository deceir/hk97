package net.hk.hk97.Models.calc.graphql.repositories;

import net.hk.hk97.Models.calc.graphql.models.Resources;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resources, Long> {

    Resources findResourcesByName(String name);

}