package com.playground.playground.contract.service;

import com.playground.common.exception.ResourceNotFoundException;
import com.playground.playground.config.PlaygroundProperties;
import com.playground.playground.contract.dto.ContractContent;
import com.playground.playground.contract.dto.ContractSummary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ContractRegistryService {

    private final PlaygroundProperties properties;

    public ContractRegistryService(PlaygroundProperties properties) {
        this.properties = properties;
    }

    public List<ContractSummary> listContracts() {
        Path contractsRoot = resolveContractsRoot();
        if (!Files.isDirectory(contractsRoot)) {
            return List.of();
        }
        List<ContractSummary> summaries = new ArrayList<>();
        try (Stream<Path> serviceDirs = Files.list(contractsRoot)) {
            serviceDirs.filter(Files::isDirectory).forEach(serviceDir -> {
                String service = serviceDir.getFileName().toString();
                try (Stream<Path> files = Files.list(serviceDir)) {
                    files.filter(path -> {
                        String name = path.getFileName().toString().toLowerCase();
                        return name.endsWith(".yaml") || name.endsWith(".yml");
                    }).forEach(file -> summaries.add(new ContractSummary(
                            service,
                            file.getFileName().toString(),
                            file.toString()
                    )));
                } catch (IOException ex) {
                    throw new IllegalStateException("Failed to list contracts for service: " + service, ex);
                }
            });
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to scan contracts directory: " + contractsRoot, ex);
        }
        summaries.sort(Comparator.comparing(ContractSummary::service).thenComparing(ContractSummary::filename));
        return summaries;
    }

    public ContractContent getContract(String service, String filename) {
        Path file = resolveContractsRoot().resolve(service).resolve(filename);
        if (!Files.isRegularFile(file)) {
            throw new ResourceNotFoundException("Contract not found: " + service + "/" + filename);
        }
        try {
            return new ContractContent(service, filename, Files.readString(file));
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read contract: " + file, ex);
        }
    }

    private Path resolveContractsRoot() {
        Path configured = Path.of(properties.getContractsPath());
        if (configured.isAbsolute()) {
            return configured;
        }
        Path workspaceRoot = Path.of(System.getProperty("user.dir"));
        if ("playground-api".equals(workspaceRoot.getFileName().toString())) {
            workspaceRoot = workspaceRoot.getParent();
        }
        return workspaceRoot.resolve(configured);
    }
}
