package com.joaoac.cwm.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.joaoac.cwm.model.Wallet;
import com.joaoac.cwm.service.WalletService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/wallets")
@CrossOrigin(origins = "*")
public class WalletController {
    @Autowired
    private WalletService walletService;

    // Listar todas as carteiras
    @GetMapping
    public ResponseEntity<List<Wallet>> getAllWallets() {
        List<Wallet> wallets = walletService.findAll();
        return ResponseEntity.ok(wallets);
    }

    // Buscar carteira por ID
    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable Long id) {
        try {
            Wallet wallet = walletService.findById(id);
            return ResponseEntity.ok(wallet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Criar nova carteira
    @PostMapping
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody Wallet wallet) {
        try {
            Wallet savedWallet = walletService.save(wallet);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedWallet);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Atualizar carteira existente
    @PutMapping("/{id}")
    public ResponseEntity<Wallet> updateWallet(@PathVariable Long id, @Valid @RequestBody Wallet wallet) {
        try {
            Wallet updatedWallet = walletService.update(id, wallet);
            return ResponseEntity.ok(updatedWallet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Deletar carteira
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        try {
            walletService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Calcular saldo da carteira
    @GetMapping("/{id}/balance")
    public ResponseEntity<Map<String, BigDecimal>> getWalletBalance(@PathVariable Long id) {
        try {
            Map<String, BigDecimal> balance = walletService.calculateWalletBalance(id);
            return ResponseEntity.ok(balance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obter total investido na carteira
    @GetMapping("/{id}/total-invested")
    public ResponseEntity<BigDecimal> getTotalInvested(@PathVariable Long id) {
        try {
            Wallet wallet = walletService.findById(id);
            BigDecimal totalInvested = wallet.getTotalInvested();
            return ResponseEntity.ok(totalInvested);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
