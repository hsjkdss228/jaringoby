package com.wanted.jaringoby.ledger.applications;

import com.wanted.jaringoby.ledger.dtos.CreateLedgerRequestDto;
import com.wanted.jaringoby.ledger.dtos.CreateLedgerResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateLedgerService {

    @Transactional
    public CreateLedgerResponseDto createLedger(
            String customerId,
            CreateLedgerRequestDto createCustomerRequestDto
    ) {
        return null;
    }
}
