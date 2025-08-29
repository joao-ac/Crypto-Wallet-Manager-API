package com.joaoac.cwm.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joaoac.cwm.enums.TransactionType;
import com.joaoac.cwm.model.Transaction;
import com.joaoac.cwm.service.TransactionService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Listar todas as transações
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.findAll();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Buscar transação por ID
     */
    @GetMapping("/transactions/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        try {
            Transaction transaction = transactionService.findById(id);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Listar transações de uma carteira
     */
    @GetMapping("/wallets/{walletId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsByWalletId(@PathVariable Long walletId) {
        try {
            List<Transaction> transactions = transactionService.findByWalletId(walletId);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Criar nova transação para uma carteira
     */
    @PostMapping("/wallets/{walletId}/transactions")
    public ResponseEntity<Transaction> createTransaction(@PathVariable Long walletId,
                                                       @RequestBody Transaction transaction) {
        try {
            Transaction savedTransaction = transactionService.save(walletId, transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Atualizar transação existente
     */
    @PutMapping("/transactions/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id,
                                                       @RequestBody Transaction transactionDetails) {
        try {
            Transaction updatedTransaction = transactionService.update(id, transactionDetails);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletar transação
     */
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Buscar transações por carteira e criptomoeda
     */
    @GetMapping("/wallets/{walletId}/transactions/cryptocurrency/{crypto}")
    public ResponseEntity<List<Transaction>> getTransactionsByCrypto(@PathVariable Long walletId,
                                                                   @PathVariable String crypto) {
        try {
            List<Transaction> transactions = transactionService.findByWalletIdAndCryptocurrency(walletId, crypto);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Buscar transações por período
     */
    @GetMapping("/wallets/{walletId}/transactions/period")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @PathVariable Long walletId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            List<Transaction> transactions = transactionService.findByWalletIdAndDateRange(walletId, start, end);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Verificar saldo disponível para venda
     */
    @GetMapping("/wallets/{walletId}/balance-check")
    public ResponseEntity<Map<String, Object>> checkBalance(@PathVariable Long walletId,
                                                           @RequestParam String cryptocurrency,
                                                           @RequestParam BigDecimal quantity) {
        try {
            boolean sufficientBalance = transactionService.checkSufficientBalance(walletId, cryptocurrency, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("sufficientBalance", sufficientBalance);
            response.put("cryptocurrency", cryptocurrency);
            response.put("requestedQuantity", quantity);
            response.put("message", sufficientBalance ? 
                "Saldo suficiente para a operação" : 
                "Saldo insuficiente para a operação");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obter tipos de transação disponíveis
     */
    @GetMapping("/transaction-types")
    public ResponseEntity<Map<String, Object>> getTransactionTypes() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, String> types = new HashMap<>();
        for (TransactionType type : TransactionType.values()) {
            types.put(type.name(), type.getDisplayName());
        }
        
        response.put("types", types);
        response.put("descriptions", Map.of(
            "BUY", TransactionType.BUY.getDescription(),
            "SELL", TransactionType.SELL.getDescription()
        ));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Estatísticas rápidas de uma carteira
     */
    @GetMapping("/wallets/{walletId}/transaction-stats")
    public ResponseEntity<Map<String, Object>> getTransactionStats(@PathVariable Long walletId) {
        try {
            List<Transaction> transactions = transactionService.findByWalletId(walletId);
            
            long totalTransactions = transactions.size();
            long buyTransactions = transactions.stream()
                .mapToLong(t -> t.getTransactionType() == TransactionType.BUY ? 1 : 0)
                .sum();
            long sellTransactions = totalTransactions - buyTransactions;
            
            BigDecimal totalInvested = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.BUY)
                .map(Transaction::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            BigDecimal totalWithdrawn = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.SELL)
                .map(Transaction::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalTransactions", totalTransactions);
            stats.put("buyTransactions", buyTransactions);
            stats.put("sellTransactions", sellTransactions);
            stats.put("totalInvested", totalInvested);
            stats.put("totalWithdrawn", totalWithdrawn);
            stats.put("netInvestment", totalInvested.subtract(totalWithdrawn));
            
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obter última transação de uma carteira
     */
    @GetMapping("/wallets/{walletId}/transactions/latest")
    public ResponseEntity<Transaction> getLatestTransaction(@PathVariable Long walletId) {
        try {
            List<Transaction> transactions = transactionService.findByWalletId(walletId);
            if (transactions.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Transaction latest = transactions.stream()
                .max((t1, t2) -> t1.getTransactionDate().compareTo(t2.getTransactionDate()))
                .orElse(null);
                
            return ResponseEntity.ok(latest);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
