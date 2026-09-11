package com.ennaslennas.locations.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wilayas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wilaya {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "name_fr", nullable = false, length = 100)
    private String nameFr;

    @Column(name = "name_ar", nullable = false, length = 100)
    private String nameAr;

    @OneToMany(mappedBy = "wilaya", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Commune> communes = new ArrayList<>();
}
