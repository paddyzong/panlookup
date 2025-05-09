package com.cardlookup.panlookup.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "card_ranges",
        indexes = @Index(name = "idx_range", columnList = "start_bin,end_bin"))
public class CardRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_bin", nullable = false)
    private long startBin;

    @Column(name = "end_bin", nullable = false)
    private long endBin;

    @Column(nullable = true, length = 512)
    private String threeDSMethodUrl;

    protected CardRange() {}

    public CardRange(Long id, long startBin, long endBin, String threeDSMethodUrl) {
        this.id = id;
        this.startBin = startBin;
        this.endBin = endBin;
        this.threeDSMethodUrl = threeDSMethodUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getStartBin() {
        return startBin;
    }

    public void setStartBin(long startBin) {
        this.startBin = startBin;
    }

    public long getEndBin() {
        return endBin;
    }

    public void setEndBin(long endBin) {
        this.endBin = endBin;
    }

    public String getThreeDSMethodUrl() {
        return threeDSMethodUrl;
    }

    public void setThreeDSMethodUrl(String threeDSMethodUrl) {
        this.threeDSMethodUrl = threeDSMethodUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardRange)) return false;
        CardRange that = (CardRange) o;
        return startBin == that.startBin &&
                endBin == that.endBin &&
                Objects.equals(threeDSMethodUrl, that.threeDSMethodUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startBin, endBin, threeDSMethodUrl);
    }
}
