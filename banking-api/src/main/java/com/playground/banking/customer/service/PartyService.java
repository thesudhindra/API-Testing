package com.playground.banking.customer.service;

import com.playground.banking.customer.dto.CreatePartyRequest;
import com.playground.banking.customer.dto.PartyResponse;
import com.playground.banking.customer.dto.UpdatePartyRequest;
import com.playground.banking.customer.entity.PartyEntity;
import com.playground.banking.customer.repository.PartyRepository;
import com.playground.banking.domain.PartyStatus;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ForbiddenException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PartyService {

    private final PartyRepository partyRepository;

    public PartyService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<PartyResponse> listParties(String tenantId, PartyStatus status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<PartyEntity> result = status == null
                ? partyRepository.findByTenantId(tenantId, pageable)
                : partyRepository.findByTenantIdAndStatus(tenantId, status, pageable);
        return toPageResponse(result);
    }

    @Transactional(readOnly = true)
    public PartyResponse getParty(String tenantId, String partyId) {
        return toResponse(requireParty(tenantId, partyId));
    }

    @Transactional
    public PartyResponse createParty(String tenantId, CreatePartyRequest request) {
        validateIndividualNames(request.partyType(), request.firstName(), request.lastName());

        Instant now = Instant.now();
        PartyEntity party = new PartyEntity();
        party.setId("party-" + UUID.randomUUID());
        party.setTenantId(tenantId);
        party.setPartyType(request.partyType());
        party.setStatus(PartyStatus.ACTIVE);
        party.setFirstName(request.firstName());
        party.setLastName(request.lastName());
        party.setEmail(request.email());
        party.setCreatedAt(now);
        party.setUpdatedAt(now);

        return toResponse(partyRepository.save(party));
    }

    @Transactional
    public PartyResponse updateParty(String tenantId, String partyId, UpdatePartyRequest request, boolean privileged) {
        if (!privileged && request.status() != null) {
            throw new ForbiddenException("Customers cannot change party status");
        }

        PartyEntity party = requireParty(tenantId, partyId);

        if (request.partyType() != null) {
            party.setPartyType(request.partyType());
        }
        if (request.status() != null) {
            party.setStatus(request.status());
        }
        if (request.firstName() != null) {
            party.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            party.setLastName(request.lastName());
        }
        if (request.email() != null) {
            party.setEmail(request.email());
        }
        party.setUpdatedAt(Instant.now());

        return toResponse(partyRepository.save(party));
    }

    @Transactional(readOnly = true)
    public void requirePartyInTenant(String tenantId, String partyId) {
        requireParty(tenantId, partyId);
    }

    private PartyEntity requireParty(String tenantId, String partyId) {
        return partyRepository.findByIdAndTenantId(partyId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Party not found"));
    }

    private static void validateIndividualNames(
            com.playground.banking.domain.PartyType partyType, String firstName, String lastName) {
        if (partyType == com.playground.banking.domain.PartyType.INDIVIDUAL
                && (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank())) {
            throw new BadRequestException("firstName and lastName are required for INDIVIDUAL parties");
        }
    }

    static PartyResponse toResponse(PartyEntity party) {
        return new PartyResponse(
                party.getId(),
                party.getTenantId(),
                party.getPartyType(),
                party.getStatus(),
                party.getFirstName(),
                party.getLastName(),
                party.getEmail(),
                party.getVersion(),
                party.getCreatedAt(),
                party.getUpdatedAt());
    }

    private PageResponse<PartyResponse> toPageResponse(Page<PartyEntity> page) {
        return new PageResponse<>(
                page.getContent().stream().map(PartyService::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                null);
    }
}
