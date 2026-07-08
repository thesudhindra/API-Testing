package com.playground.banking.api;

import com.playground.banking.card.dto.AuthorizeCardRequest;
import com.playground.banking.card.dto.CardAuthorizationResponse;
import com.playground.banking.card.dto.CardResponse;
import com.playground.banking.card.dto.CreateCardRequest;
import com.playground.banking.card.service.CardService;
import com.playground.banking.idempotency.service.IdempotencyService;
import com.playground.common.security.TenantAccess;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/cards")
@Tag(name = "Cards", description = "Debit card lifecycle and authorizations")
@SecurityRequirement(name = "bearerAuth")
public class CardController {

    private final CardService cardService;
    private final IdempotencyService idempotencyService;

    public CardController(CardService cardService, IdempotencyService idempotencyService) {
        this.cardService = cardService;
        this.idempotencyService = idempotencyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Issue card")
    public CardResponse createCard(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody CreateCardRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, request.partyId());
        return cardService.createCard(tenantId, jwt.getSubject(), request);
    }

    @GetMapping
    @Operation(summary = "List cards for a party")
    public List<CardResponse> listCards(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String partyId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, partyId);
        return cardService.listCards(tenantId, partyId);
    }

    @GetMapping("/{cardId}")
    @Operation(summary = "Get card by id")
    public CardResponse getCard(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String cardId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        CardResponse card = cardService.getCard(tenantId, cardId);
        TenantAccess.requirePartyAccess(jwt, card.partyId());
        return card;
    }

    @PostMapping("/{cardId}/authorizations")
    @Operation(summary = "Authorize card purchase")
    public ResponseEntity<CardAuthorizationResponse> authorize(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestHeader(ApiHeaders.IDEMPOTENCY_KEY) String idempotencyKey,
            @PathVariable String cardId,
            @Valid @RequestBody AuthorizeCardRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        CardResponse card = cardService.getCard(tenantId, cardId);
        TenantAccess.requirePartyAccess(jwt, card.partyId());

        IdempotencyService.IdempotencyOutcome<CardAuthorizationResponse> outcome = idempotencyService.execute(
                tenantId, idempotencyKey, "CARD_AUTHORIZATION", request,
                () -> cardService.authorize(tenantId, jwt.getSubject(), cardId, idempotencyKey, request),
                CardAuthorizationResponse.class, HttpStatus.CREATED);

        return ResponseEntity.status(outcome.statusCode()).body(outcome.body());
    }
}
