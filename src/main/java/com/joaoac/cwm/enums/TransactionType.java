package com.joaoac.cwm.enums;

public enum TransactionType {

    BUY("Compra", "Transação de compra de criptomoeda"),
    SELL("Venda", "Transação de venda de criptomoeda");
    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }
    public String getDescription() {
        return description;
    }
    public boolean isBuy() {
        return this == BUY;
    }
    public boolean isSell() {
        return this == SELL;
    }

    // Verifica o tipo da transação
    public static TransactionType fromString(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de transação não pode ser nulo ou vazio");
        }
        
        try {
            return TransactionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de transação inválido: " + type + 
                ". Tipos válidos: BUY, SELL");
        }
    }
}
