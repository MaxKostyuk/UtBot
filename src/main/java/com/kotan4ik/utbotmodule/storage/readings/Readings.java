package com.kotan4ik.utbotmodule.storage.readings;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "readings", schema = "public")
public class Readings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_id")
    private int readingId;
    @Column(name = "tele_id", nullable = false)
    private long userId;
    @Column(nullable = false)
    private int t1;
    @Column(nullable = false)
    private int t2;
    @Column(nullable = false)
    private int t3;
    @Column(nullable = false)
    private int cold;
    @Column(nullable = false)
    private int hot;
}
