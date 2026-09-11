package com.ennaslennas.cases.domain;

import com.ennaslennas.categories.domain.Category;
import com.ennaslennas.locations.domain.Commune;
import com.ennaslennas.locations.domain.Wilaya;
import com.ennaslennas.requesters.domain.Requester;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private Requester requester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wilaya_id", nullable = false)
    private Wilaya wilaya;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commune_id", nullable = false)
    private Commune commune;

    // Données brutes (demandeur)
    @Column(name = "raw_title", nullable = false)
    private String rawTitle;

    @Column(name = "raw_description", nullable = false, columnDefinition = "TEXT")
    private String rawDescription;

    // Données publiques (après modération)
    @Column(name = "public_title")
    private String publicTitle;

    @Column(name = "public_description", columnDefinition = "TEXT")
    private String publicDescription;

    @Column(name = "public_image_url", length = 500)
    private String publicImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UrgencyLevel urgency = UrgencyLevel.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private CaseStatus status = CaseStatus.SUBMITTED;

    @Column(name = "amount_needed", precision = 12, scale = 2)
    private BigDecimal amountNeeded;

    @Column(name = "amount_collected", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal amountCollected = BigDecimal.ZERO;

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CaseDocument> documents = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    public void addDocument(CaseDocument document) {
        documents.add(document);
        document.setCaseEntity(this);
    }
}
