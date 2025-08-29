package com.joaoac.cwm.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joaoac.cwm.enums.TransactionType;
import com.joaoac.cwm.model.Transaction;
import com.joaoac.cwm.model.Wallet;
import com.joaoac.cwm.repository.WalletRepository;

@Service
public class WalletService {
    
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionService transactionService;

    // Buscar todas as carteiras
    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }

    // Buscar carteira por ID
    public Wallet findById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id " + id));
    }

    // Salvar ou atualizar carteira
    public Wallet save(Wallet wallet) {
        // Definir timestamps
        if (wallet.getId() == null) {
            wallet.setCreatedAt(LocalDateTime.now());
        }
        wallet.setUpdatedAt(LocalDateTime.now());

        // Validações básicas
        validateWallet(wallet);

        return walletRepository.save(wallet);
    }

    // Deletar carteira por ID
    public void deleteById(Long id) {
        validateWalletExists(id);
        
        // Verificar se existem transações associadas
        List<Transaction> transactions = transactionService.findByWalletId(id);
        if (!transactions.isEmpty()) {
            throw new RuntimeException("Não é possível deletar carteira com transações associadas");
        }
        
        walletRepository.deleteById(id);
    }

    // Atualizar carteira existente
    public Wallet update(Long id, Wallet walletDetails) {
        Wallet existing = findById(id);
        
        existing.setName(walletDetails.getName());
        existing.setDescription(walletDetails.getDescription());
        existing.setUpdatedAt(LocalDateTime.now());

        validateWallet(existing);
        return walletRepository.save(existing);
    }

    // Calcular saldo da carteira
    public Map<String, BigDecimal> calculateWalletBalance(Long walletId) {
        validateWalletExists(walletId);
        
        List<Transaction> transactions = transactionService.findByWalletId(walletId);
        Map<String, BigDecimal> balance = new HashMap<>();

        for (Transaction transaction : transactions) {
            String crypto = transaction.getCryptocurrency();
            BigDecimal quantity = transaction.getQuantity();
            
            // Se é compra, adiciona; se é venda, subtrai
            if (transaction.getTransactionType() == TransactionType.BUY) {
                balance.merge(crypto, quantity, BigDecimal::add);
            } else if (transaction.getTransactionType() == TransactionType.SELL) {
                balance.merge(crypto, quantity.negate(), BigDecimal::add);
            }
        }

        // Remover criptomoedas com saldo zero ou negativo
        balance.entrySet().removeIf(entry -> 
            entry.getValue().compareTo(BigDecimal.ZERO) <= 0);

        return balance;
    }

    // Calcular total investido na carteira
    public BigDecimal calculateTotalInvested(Long walletId) {
        validateWalletExists(walletId);
        
        List<Transaction> transactions = transactionService.findByWalletId(walletId);
        BigDecimal totalInvested = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.BUY) {
                totalInvested = totalInvested.add(transaction.getTotalValue());
            }
        }

        return totalInvested;
    }

    // Verificar se a carteira existe
    public void validateWalletExists(Long walletId) {
        if (!walletRepository.existsById(walletId)) {
            throw new RuntimeException("Carteira não encontrada com ID: " + walletId);
        }
    }

    // Verifica se há saldo suficiente para venda
    public boolean hasSufficientBalance(Long walletId, String cryptocurrency, BigDecimal quantity) {
        Map<String, BigDecimal> balance = calculateWalletBalance(walletId);
        BigDecimal currentBalance = balance.getOrDefault(cryptocurrency, BigDecimal.ZERO);
        return currentBalance.compareTo(quantity) >= 0;
    }

    // Validações básicas da carteira
    private void validateWallet(Wallet wallet) {
        if (wallet.getName() == null || wallet.getName().trim().isEmpty()) {
            throw new RuntimeException("Nome da carteira é obrigatório");
        }
        
        if (wallet.getName().length() > 100) {
            throw new RuntimeException("Nome da carteira deve ter no máximo 100 caracteres");
        }
        
        if (wallet.getDescription() != null && wallet.getDescription().length() > 255) {
            throw new RuntimeException("Descrição deve ter no máximo 255 caracteres");
        }
    }
}
