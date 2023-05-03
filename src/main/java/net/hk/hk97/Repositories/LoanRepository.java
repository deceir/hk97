//package net.hk.hk97.Repositories;
//
//import net.hk.hk97.Models.Bank.Loan;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface LoanRepository extends JpaRepository <Loan, Long> {
//    List<Loan> getLoansByActive(Boolean active);
//
//    List<Loan> getLoansByDiscordidAAndActive(long id, boolean active);
//
//    Loan getLoanByDepositcode(String code);
//}
