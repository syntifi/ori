package com.syntifi.ori.model;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ori Entity for Transfer data
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(HashTokenId.class)
public class Transfer extends PanacheEntityBase {

    @Id
    @NotNull
    private String hash;

    @Id
    @NotNull
    @MapsId("symbol")
    @ManyToOne
    private Token token;

    @ManyToOne
    private Block block;

    @NotNull
    @Column(name = "time_stamp")
    private OffsetDateTime timeStamp;

    @ManyToOne
    private Account fromAccount;

    @ManyToOne
    private Account toAccount;

    @NotNull
    @Min(0)
    private Double amount;
}
