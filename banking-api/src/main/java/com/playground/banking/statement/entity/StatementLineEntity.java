package com.playground.banking.statement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(schema = "banking", name = "statement_lines")
public class StatementLineEntity {

    @Id
    private String id;

    @Column(name = "statement_id", nullable = false)
    private String statementId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "posted_at", nullable = false)
    private Instant postedAt;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false)
    private BigDecimal balanceAfter;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStatementId() { return statementId; }
    public void setStatementId(String statementId) { this.statementId = statementId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public Instant getPostedAt() { return postedAt; }
    public void setPostedAt(Instant postedAt) { this.postedAt = postedAt; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
