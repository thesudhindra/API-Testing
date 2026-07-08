package com.playground.playground.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "playground.lab")
public class PlaygroundProperties {

    private String bankingUrl = "http://localhost:8081";
    private String enterpriseUrl = "http://localhost:8082";
    private String platformUrl = "http://localhost:8080";
    private String contractsPath = "contracts";
    private int faultTtlMinutes = 60;

    public String getBankingUrl() {
        return bankingUrl;
    }

    public void setBankingUrl(String bankingUrl) {
        this.bankingUrl = bankingUrl;
    }

    public String getEnterpriseUrl() {
        return enterpriseUrl;
    }

    public void setEnterpriseUrl(String enterpriseUrl) {
        this.enterpriseUrl = enterpriseUrl;
    }

    public String getPlatformUrl() {
        return platformUrl;
    }

    public void setPlatformUrl(String platformUrl) {
        this.platformUrl = platformUrl;
    }

    public String getContractsPath() {
        return contractsPath;
    }

    public void setContractsPath(String contractsPath) {
        this.contractsPath = contractsPath;
    }

    public int getFaultTtlMinutes() {
        return faultTtlMinutes;
    }

    public void setFaultTtlMinutes(int faultTtlMinutes) {
        this.faultTtlMinutes = faultTtlMinutes;
    }
}
