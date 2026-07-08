package com.playground.enterprise.document.service;

import com.playground.enterprise.audit.service.EnterpriseAuditService;
import com.playground.enterprise.document.dto.DocumentResponse;
import com.playground.enterprise.document.dto.RegisterDocumentRequest;
import com.playground.enterprise.document.entity.DocumentEntity;
import com.playground.enterprise.document.repository.DocumentRepository;
import com.playground.enterprise.domain.DocumentStatus;
import com.playground.enterprise.event.service.EventOutboxService;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EnterpriseAuditService auditService;
    private final EventOutboxService eventOutboxService;

    public DocumentService(
            DocumentRepository documentRepository,
            EnterpriseAuditService auditService,
            EventOutboxService eventOutboxService) {
        this.documentRepository = documentRepository;
        this.auditService = auditService;
        this.eventOutboxService = eventOutboxService;
    }

    @Transactional
    public DocumentResponse register(String tenantId, String actorId, RegisterDocumentRequest request) {
        Instant now = Instant.now();
        String documentId = UUID.randomUUID().toString();
        String storageKey = tenantId + "/" + request.partyId() + "/" + documentId + "/" + request.fileName();

        DocumentEntity document = new DocumentEntity();
        document.setId(documentId);
        document.setTenantId(tenantId);
        document.setPartyId(request.partyId());
        document.setDocumentType(request.documentType());
        document.setFileName(request.fileName());
        document.setContentType(request.contentType());
        document.setStorageKey(storageKey);
        document.setStatus(DocumentStatus.UPLOADED);
        document.setCreatedAt(now);
        documentRepository.save(document);

        auditService.record(tenantId, "DOCUMENT", documentId, "REGISTERED", actorId,
                "documentType=" + request.documentType());
        eventOutboxService.publish(tenantId, "DOCUMENT", documentId, "DOCUMENT_REGISTERED",
                "partyId=" + request.partyId() + ",documentType=" + request.documentType());

        return toResponse(document);
    }

    @Transactional(readOnly = true)
    public DocumentResponse get(String tenantId, String documentId) {
        DocumentEntity document = documentRepository.findByIdAndTenantId(documentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        return toResponse(document);
    }

    @Transactional(readOnly = true)
    public PageResponse<DocumentResponse> listByParty(String tenantId, String partyId, int page, int size) {
        Page<DocumentEntity> result = documentRepository.findByTenantIdAndPartyId(
                tenantId, partyId, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(DocumentService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    static DocumentResponse toResponse(DocumentEntity document) {
        return new DocumentResponse(
                document.getId(), document.getPartyId(), document.getDocumentType(),
                document.getFileName(), document.getContentType(), document.getStorageKey(),
                document.getStatus(), document.getCreatedAt());
    }
}
