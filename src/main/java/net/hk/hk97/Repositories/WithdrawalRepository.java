package net.hk.hk97.Repositories;

import net.hk.hk97.Models.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, String> {

    Withdrawal findWithdrawalByDepositcode(String code);
}
