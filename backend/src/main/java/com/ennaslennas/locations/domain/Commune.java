package com.ennaslennas.locations.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "communes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commune {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wilaya_id", nullable = false)
    private Wilaya wilaya;

    @Column(name = "name_fr", nullable = false, length = 100)
    private String nameFr;

    @Column(name = "name_ar", nullable = false, length = 100)
    private String nameAr;

    @Column(name = "postal_code", length = 10)
    private String postalCode;
}
