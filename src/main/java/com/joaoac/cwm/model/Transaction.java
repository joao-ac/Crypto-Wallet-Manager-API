package com.joaoac.cwm.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.joaoac.cwm.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cryptocurrency;
    private TransactionType transactionType;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalValue;
    private LocalDateTime transactionDate;
    private String notes;

    // Relationship to Wallet
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
    @Column(name = "wallet_id", insertable = false, updatable = false)
    private Long walletId;

    // Getters
    public Long getId() {
        return id;
    }
    public String getCryptocurrency() {
        return cryptocurrency;
    }
    public TransactionType getTransactionType() {
        return transactionType;
    }
    public BigDecimal getQuantity() {
        return quantity;
    }
    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }
    public BigDecimal getTotalValue() {
        return totalValue;
    }
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    public String getNotes() {
        return notes;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    public void setCryptocurrency(String cryptocurrency) {
        this.cryptocurrency = cryptocurrency;
    }
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }


    // Functions
    public void calculateTotalValue() {
        if (this.quantity != null && this.pricePerUnit != null) {
            this.totalValue = this.pricePerUnit.multiply(this.quantity);
        } else {
            this.totalValue = BigDecimal.ZERO;
        }
    }
}
