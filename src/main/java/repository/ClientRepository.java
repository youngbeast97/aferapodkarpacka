package repository;


import model.client.Client;
import model.enums.PoliticalParty;
import model.enums.SocialStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByActiveTrue();

    List<Client> findByActiveFalse();

    List<Client> findByInfectedUntilBefore(LocalDateTime dateTime);

    List<Client> findByActiveFalseAndInfectedUntilBefore(
            LocalDateTime dateTime
    );

    List<Client> findByDebtGreaterThan(BigDecimal debt);

    List<Client> findBySocialStatus(SocialStatus socialStatus);

    List<Client> findByParty(PoliticalParty party);

    List<Client> findByPartyAndDebtGreaterThan(
            PoliticalParty party,
            BigDecimal debt
    );

    List<Client> findBySocialStatusInAndDebtGreaterThan(
            List<SocialStatus> statuses,
            BigDecimal debt
    );

}
