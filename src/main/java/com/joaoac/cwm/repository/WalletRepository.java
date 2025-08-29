package com.joaoac.cwm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joaoac.cwm.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Buscar carteira por nome
    List<Wallet> findByNameContainingIgnoreCase(String name);

    // Verificar se existe por nome
    boolean existsByName(String name);

    
    
}
