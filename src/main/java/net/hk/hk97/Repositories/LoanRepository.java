package net.hk.hk97.Repositories;

import net.hk.hk97.Models.Bank.Loan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Configuration
public interface LoanRepository extends JpaRepository <Loan, Long> {

    List<Loan> getLoansByActive(Boolean active);

    List<Loan> getLoansByDiscordid(Long discordid);

    Loan getLoanByDepositcode(String depositcode);

}
