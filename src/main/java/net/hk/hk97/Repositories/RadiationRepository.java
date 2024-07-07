package net.hk.hk97.Repositories;

import net.hk.hk97.Models.Radiation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface RadiationRepository extends JpaRepository<Radiation, String> {

    Radiation findRadiationById(String ID);
}
