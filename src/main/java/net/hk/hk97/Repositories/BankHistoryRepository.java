package net.hk.hk97.Repositories;

import net.hk.hk97.Models.Bank.AllianceBankHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDate;

public interface BankHistoryRepository extends JpaRepository<AllianceBankHistory, LocalDate> {



}
