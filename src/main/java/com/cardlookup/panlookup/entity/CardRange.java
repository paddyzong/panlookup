package com.cardlookup.panlookup.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "card_ranges",
        indexes = @Index(name = "idx_range", columnList = "start_bin,end_bin"))
public class CardRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_bin", nullable = false)
    private int startBin;

    @Column(name = "end_bin", nullable = false)
    private int endBin;

    @Column(nullable = false, length = 512)
    private String threeDSMethodUrl;

    /* getters / setters / constructor(s) */
}
