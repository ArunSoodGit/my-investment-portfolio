package com.sood.portfolio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "portfolios")
public class PortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String foundName; // np. "Główny portfel", "Inwestycje długoterminowe"
    private String description;

    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioItemEntity> items = new ArrayList<>();

    public void addItem(PortfolioItemEntity item) {
        item.setPortfolio(this); // ustawienie właściciela relacji
        this.items.add(item);
    }
}