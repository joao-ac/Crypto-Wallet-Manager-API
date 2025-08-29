package com.joaoac.cwm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joaoac.cwm.enums.TransactionType;
import com.joaoac.cwm.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Buscar transacoes por carteira
    List<Transaction> findByWalletId(Long walletId);

    // Buscar transacoes por carteira e criptomoeda
    List<Transaction> findByWalletIdAndCryptocurrency(Long walletId, String cryptocurrency);

    //Buscar transacoes por carteira e tipo
    List<Transaction> findByWalletIdAndTransactionType(Long walletId, TransactionType transactionType);
    
    // Buscar transacoes por carteira em um periodo
    List<Transaction> findByWalletIdAndTransactionDateBetween(Long walletId, 
                                                             LocalDateTime startDate, 
                                                             LocalDateTime endDate);
    
    // buscar transacoes por criptomoeda
    List<Transaction> findByCryptocurrency(String cryptocurrency);

    // Buscar transacoes por tipo
    List<Transaction> findByTransactionType(TransactionType transactionType);

    // Buscar transacoes por data ordenada (mais recente primeiro)
    List<Transaction> findByWalletIdOrderByTransactionDateDesc(Long walletId);

    // Buscar transacoes de compra de uma criptomoeda especifica
    List<Transaction> findByWalletIdAndCryptocurrencyAndTransactionType(Long walletId, 
                                                                       String cryptocurrency, 
                                                                       TransactionType transactionType);

    // Contar transacoes por carteira
    long countByWalletId(Long walletId);


    // Deletar todas as transacoes de uma carteira
    void deleteByWalletId(Long walletId);
}
