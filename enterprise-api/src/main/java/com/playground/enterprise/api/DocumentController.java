package com.playground.enterprise.api;

import com.playground.enterprise.document.dto.DocumentResponse;
import com.playground.enterprise.document.dto.RegisterDocumentRequest;
import com.playground.enterprise.document.service.DocumentService;
import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/v1/documents")
@Tag(name = "Documents", description = "Document metadata registration")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register document metadata")
    public DocumentResponse register(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody RegisterDocumentRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, request.partyId());
        return documentService.register(tenantId, jwt.getSubject(), request);
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "Get document by id")
    public DocumentResponse get(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String documentId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        DocumentResponse document = documentService.get(tenantId, documentId);
        TenantAccess.requirePartyAccess(jwt, document.partyId());
        return document;
    }

    @GetMapping
    @Operation(summary = "List documents for a party")
    public PageResponse<DocumentResponse> listByParty(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String partyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, partyId);
        return documentService.listByParty(tenantId, partyId, page, size);
    }
}
