package com.joaoac.cwm.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joaoac.cwm.enums.TransactionType;
import com.joaoac.cwm.model.Transaction;
import com.joaoac.cwm.model.Wallet;
import com.joaoac.cwm.repository.TransactionRepository;
import com.joaoac.cwm.repository.WalletRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    // Lista de criptomoedas válidas para validação
    private static final List<String> VALID_CRYPTOCURRENCIES = Arrays.asList(
            "BTC", "ETH", "BNB", "ADA", "XRP", "SOL", "DOT", "DOGE", "AVAX", "MATIC",
            "LTC", "BCH", "LINK", "UNI", "ATOM", "XLM", "VET", "FIL", "TRX", "ETC"
    );

    /**
     * Buscar todas as transações
     */
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    /**
     * Buscar transação por ID
     */
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com ID: " + id));
    }

    /**
     * Buscar transações por carteira
     */
    public List<Transaction> findByWalletId(Long walletId) {
        return transactionRepository.findByWalletId(walletId);
    }

    /**
     * Salvar ou atualizar transação
     */
    public Transaction save(Long walletId, Transaction transaction) {
        // Verificar se a carteira existe
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada com ID: " + walletId));

        // Associar transação à carteira
        transaction.setWallet(wallet);
        transaction.setWalletId(walletId);

        // Validar transação
        validateTransaction(transaction);

        // Calcular valor total se não foi informado
        if (transaction.getTotalValue() == null) {
            transaction.calculateTotalValue();
        }

        // Verificar saldo para vendas
        if (transaction.getTransactionType() == TransactionType.SELL) {
            if (!checkSufficientBalance(walletId, transaction.getCryptocurrency(), transaction.getQuantity())) {
                throw new RuntimeException("Saldo insuficiente para venda de " + 
                    transaction.getQuantity() + " " + transaction.getCryptocurrency());
            }
        }

        return transactionRepository.save(transaction);
    }

    /**
     * Atualizar transação existente
     */
    public Transaction update(Long transactionId, Transaction transactionDetails) {
        Transaction existingTransaction = findById(transactionId);
        
        // Manter a carteira original
        transactionDetails.setId(transactionId);
        transactionDetails.setWallet(existingTransaction.getWallet());
        transactionDetails.setWalletId(existingTransaction.getWalletId());

        // Validar e salvar
        validateTransaction(transactionDetails);
        
        if (transactionDetails.getTotalValue() == null) {
            transactionDetails.calculateTotalValue();
        }

        return transactionRepository.save(transactionDetails);
    }

    /**
     * Deletar transação por ID
     */
    public void deleteById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transação não encontrada com ID: " + id);
        }
        transactionRepository.deleteById(id);
    }

    /**
     * Verificar se há saldo suficiente para venda
     */
    public boolean checkSufficientBalance(Long walletId, String cryptocurrency, BigDecimal quantityToSell) {
        List<Transaction> transactions = findByWalletId(walletId);
        BigDecimal currentBalance = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getCryptocurrency().equalsIgnoreCase(cryptocurrency)) {
                if (transaction.getTransactionType() == TransactionType.BUY) {
                    currentBalance = currentBalance.add(transaction.getQuantity());
                } else if (transaction.getTransactionType() == TransactionType.SELL) {
                    currentBalance = currentBalance.subtract(transaction.getQuantity());
                }
            }
        }

        return currentBalance.compareTo(quantityToSell) >= 0;
    }

    /**
     * Buscar transações por criptomoeda
     */
    public List<Transaction> findByWalletIdAndCryptocurrency(Long walletId, String cryptocurrency) {
        return transactionRepository.findByWalletIdAndCryptocurrency(walletId, cryptocurrency);
    }

    /**
     * Buscar transações em um período
     */
    public List<Transaction> findByWalletIdAndDateRange(Long walletId, 
                                                       LocalDateTime startDate, 
                                                       LocalDateTime endDate) {
        return transactionRepository.findByWalletIdAndTransactionDateBetween(walletId, startDate, endDate);
    }

    /**
     * Validar dados da transação
     */
    public void validateTransaction(Transaction transaction) {
        // Validar criptomoeda
        if (transaction.getCryptocurrency() == null || transaction.getCryptocurrency().trim().isEmpty()) {
            throw new RuntimeException("Criptomoeda é obrigatória");
        }

        String crypto = transaction.getCryptocurrency().toUpperCase();
        if (!VALID_CRYPTOCURRENCIES.contains(crypto)) {
            throw new RuntimeException("Criptomoeda não suportada: " + crypto);
        }
        transaction.setCryptocurrency(crypto);

        // Validar tipo de transação
        if (transaction.getTransactionType() == null) {
            throw new RuntimeException("Tipo de transação é obrigatório");
        }

        // Validar quantidade
        if (transaction.getQuantity() == null || transaction.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        // Validar preço por unidade
        if (transaction.getPricePerUnit() == null || transaction.getPricePerUnit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Preço por unidade deve ser maior que zero");
        }

        // Validar data da transação
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }

        // Não permitir transações no futuro
        if (transaction.getTransactionDate().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Data da transação não pode ser no futuro");
        }

        // Validar notas (opcional)
        if (transaction.getNotes() != null && transaction.getNotes().length() > 255) {
            throw new RuntimeException("Notas devem ter no máximo 255 caracteres");
        }
    }
}