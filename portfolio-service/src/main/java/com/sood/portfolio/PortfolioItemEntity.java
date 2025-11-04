package com.sood.portfolio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "portfolio_items")
public class PortfolioItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String symbol;
    private String name;
    private double quantity;
    private double averagePurchasePrice;
    private double currentPrice;
    private double totalValue;
    private double profitLoss;
    private String currency;
    private LocalDate lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private PortfolioEntity portfolio;

    @OneToMany(mappedBy = "portfolioItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioItemTransactionEntity> transactions = new ArrayList<>();

    public void addTransaction(final PortfolioItemTransactionEntity transaction) {
        transaction.setPortfolioItem(this); // utrzymanie relacji dwukierunkowej
        this.transactions.add(transaction);
    }

    public void removeTransaction(final PortfolioItemTransactionEntity transaction) {
        transaction.setPortfolioItem(null);
        this.transactions.remove(transaction);
    }
}