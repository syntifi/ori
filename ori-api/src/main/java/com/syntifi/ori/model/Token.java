package com.syntifi.ori.model;

import java.math.BigInteger;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ori Entity for Token data
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token extends PanacheEntityBase {

    @Id
    @NotNull
    private String symbol;

    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    private Chain chain;

    @NotNull
    private Double quantization;



}
