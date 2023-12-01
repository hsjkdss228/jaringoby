package com.wanted.jaringoby.domains.ledger.repositories;

import com.wanted.jaringoby.domains.ledger.models.ledger.Ledger;
import com.wanted.jaringoby.domains.ledger.models.ledger.LedgerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<Ledger, LedgerId>,
        LedgerQueryDslRepository {

}
