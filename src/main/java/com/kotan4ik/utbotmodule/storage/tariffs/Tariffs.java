package com.kotan4ik.utbotmodule.storage.tariffs;

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
@Table(name = "tariffs", schema = "public")
public class Tariffs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tariff_id")
    private int tariffId;
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
