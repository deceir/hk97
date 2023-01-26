package net.hk.hk97.Repositories;


import net.hk.hk97.Models.Bank.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankRepository extends JpaRepository<Bank, String> {


    Bank findByDiscordid(String id);

    List<Bank> findBanksByDiscordid(String id);

    Bank findBankByNationid(long id);

    Bank findBankByDepositcode(String code);
}
