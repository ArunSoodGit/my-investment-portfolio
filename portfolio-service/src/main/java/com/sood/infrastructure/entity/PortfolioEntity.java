package com.sood.infrastructure.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "portfolios")
public class PortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String portfolioName;
    private String description;
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PortfolioItemEntity> items = new HashSet<>();

    public Optional<PortfolioItemEntity> findItem(final String symbol) {
        return items.stream()
                .filter(item -> item.getSymbol().equals(symbol))
                .findFirst();
    }

    public void addItem(final PortfolioItemEntity item) {
        item.setPortfolio(this);
        this.items.add(item);
    }

    public void removeItem(final PortfolioItemEntity item) {
        this.items.remove(item);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String foundName) {
        this.portfolioName = foundName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Set<PortfolioItemEntity> getItems() {
        return items;
    }

    public void setItems(Set<PortfolioItemEntity> items) {
        this.items = items;
    }
}