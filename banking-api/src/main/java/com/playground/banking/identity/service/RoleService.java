package com.playground.banking.identity.service;

import com.playground.banking.identity.dto.RoleResponse;
import com.playground.banking.identity.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles(String tenantId) {
        return roleRepository.findByTenantId(tenantId).stream()
                .map(r -> new RoleResponse(r.getId(), r.getTenantId(), r.getName()))
                .collect(Collectors.toList());
    }
}
