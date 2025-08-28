# Crypto Wallet Manager
Projeto de API Java RESTful para gerenciar carteiras de crypto, feito para praticar noçÕes publicação de API em nuvem.

## Diagrama de classes

```mermaid
classDiagram
    class Wallet {
        -Long id
        -String name
        -String description
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        -List~Transaction~ transactions
        +getId() Long
        +getName() String
        +getDescription() String
        +getCreatedAt() LocalDateTime
        +getUpdatedAt() LocalDateTime
        +getTransactions() List~Transaction~
        +addTransaction(Transaction) void
        +removeTransaction(Transaction) void
        +calculateBalance() Map~String, WalletBalance~
        +getTotalInvested() BigDecimal
    }
    
    class Transaction {
        -Long id
        -Long walletId
        -String cryptocurrency
        -TransactionType transactionType
        -BigDecimal quantity
        -BigDecimal pricePerUnit
        -BigDecimal totalValue
        -LocalDateTime transactionDate
        -String notes
        +getId() Long
        +getWalletId() Long
        +getCryptocurrency() String
        +getTransactionType() TransactionType
        +getQuantity() BigDecimal
        +getPricePerUnit() BigDecimal
        +getTotalValue() BigDecimal
        +getTransactionDate() LocalDateTime
        +getNotes() String
        +calculateTotalValue() void
    }
    
    class TransactionType {
        <<enumeration>>
        BUY
        SELL
    }
    
    class WalletBalance {
        -String cryptocurrency
        -BigDecimal totalQuantity
        -BigDecimal averagePrice
        -BigDecimal totalInvested
        -BigDecimal totalSold
        -BigDecimal realizedProfit
        +getCryptocurrency() String
        +getTotalQuantity() BigDecimal
        +getAveragePrice() BigDecimal
        +getTotalInvested() BigDecimal
        +getTotalSold() BigDecimal
        +getRealizedProfit() BigDecimal
        +calculateAveragePrice(List~Transaction~) void
    }
    
    class WalletController {
        -WalletService walletService
        +getAllWallets() ResponseEntity~List~Wallet~~
        +getWalletById(Long) ResponseEntity~Wallet~
        +createWallet(WalletDTO) ResponseEntity~Wallet~
        +updateWallet(Long, WalletDTO) ResponseEntity~Wallet~
        +deleteWallet(Long) ResponseEntity~Void~
        +getWalletBalance(Long) ResponseEntity~Map~String, WalletBalance~~
        +getWalletPerformance(Long) ResponseEntity~WalletPerformanceDTO~
    }
    
    class TransactionController {
        -TransactionService transactionService
        +getTransactionsByWalletId(Long) ResponseEntity~List~Transaction~~
        +getTransactionById(Long) ResponseEntity~Transaction~
        +createTransaction(Long, TransactionDTO) ResponseEntity~Transaction~
        +updateTransaction(Long, TransactionDTO) ResponseEntity~Transaction~
        +deleteTransaction(Long) ResponseEntity~Void~
        +getTransactionsSummary(Long) ResponseEntity~TransactionSummaryDTO~
    }
    
    class WalletService {
        -WalletRepository walletRepository
        -TransactionService transactionService
        +findAll() List~Wallet~
        +findById(Long) Wallet
        +save(Wallet) Wallet
        +deleteById(Long) void
        +calculateWalletBalance(Long) Map~String, WalletBalance~
        +calculatePerformance(Long) WalletPerformanceDTO
        +validateWalletExists(Long) void
    }
    
    class TransactionService {
        -TransactionRepository transactionRepository
        -WalletService walletService
        +findByWalletId(Long) List~Transaction~
        +findById(Long) Transaction
        +save(Transaction) Transaction
        +deleteById(Long) void
        +validateTransaction(Transaction) void
        +calculateTransactionSummary(Long) TransactionSummaryDTO
        +checkSufficientBalance(Long, String, BigDecimal) boolean
    }
    
    class WalletRepository {
        <<interface>>
        +findAll() List~Wallet~
        +findById(Long) Optional~Wallet~
        +save(Wallet) Wallet
        +deleteById(Long) void
        +existsById(Long) boolean
    }
    
    class TransactionRepository {
        <<interface>>
        +findByWalletId(Long) List~Transaction~
        +findById(Long) Optional~Transaction~
        +save(Transaction) Transaction
        +deleteById(Long) void
        +findByWalletIdAndCryptocurrency(Long, String) List~Transaction~
        +findByWalletIdAndTransactionDateBetween(Long, LocalDateTime, LocalDateTime) List~Transaction~
        +sumQuantityByWalletIdAndCryptocurrencyAndTransactionType(Long, String, TransactionType) BigDecimal
    }
    
    class WalletDTO {
        -String name
        -String description
        +getName() String
        +getDescription() String
        +setName(String) void
        +setDescription(String) void
    }
    
    class TransactionDTO {
        -String cryptocurrency
        -TransactionType transactionType
        -BigDecimal quantity
        -BigDecimal pricePerUnit
        -LocalDateTime transactionDate
        -String notes
        +getCryptocurrency() String
        +getTransactionType() TransactionType
        +getQuantity() BigDecimal
        +getPricePerUnit() BigDecimal
        +getTransactionDate() LocalDateTime
        +getNotes() String
    }
    
    class WalletPerformanceDTO {
        -Long walletId
        -BigDecimal totalInvested
        -BigDecimal totalCurrentValue
        -BigDecimal totalRealizedProfit
        -BigDecimal totalUnrealizedProfit
        -BigDecimal totalReturn
        -Map~String, WalletBalance~ balanceByToken
    }
    
    class TransactionSummaryDTO {
        -Long walletId
        -Map~String, BigDecimal~ totalBoughtByToken
        -Map~String, BigDecimal~ totalSoldByToken
        -Map~String, BigDecimal~ currentBalanceByToken
        -BigDecimal totalInvested
        -BigDecimal totalWithdrawn
    }

    %% Relationships
    Wallet ||--o{ Transaction : contains
    Transaction }o--|| TransactionType : has
    Wallet ||--o{ WalletBalance : calculates
    
    WalletController --> WalletService : uses
    TransactionController --> TransactionService : uses
    
    WalletService --> WalletRepository : uses
    WalletService --> TransactionService : uses
    TransactionService --> TransactionRepository : uses
    TransactionService --> WalletService : uses
    
    WalletController --> WalletDTO : receives
    TransactionController --> TransactionDTO : receives
    WalletController --> WalletPerformanceDTO : returns
    TransactionController --> TransactionSummaryDTO : returns
    
    WalletRepository --|> Wallet : manages
    TransactionRepository --|> Transaction : manages
```
