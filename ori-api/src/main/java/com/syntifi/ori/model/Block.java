package com.syntifi.ori.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Block model, only a couple of the fields available in the chain are needed
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(BlockId.class)
public class Block extends PanacheEntityBase {
    
    @Id
    private String hash;

    @Id
    @MapsId("symbol")
    @ManyToOne(fetch = FetchType.LAZY)
    private Token token;

    @NotNull
    @Column(name = "time_stamp")
    private Date timeStamp;

    @NotNull
    @Min(-1)
    private Long height;

    @NotNull
    @Min(-1)
    private Long era;

    @OneToOne(fetch = FetchType.LAZY)
    private Block parent;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "parent")
    private Block child;

    @NotNull
    private String root;

    @NotNull
    private String validator;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "block")
    private Set<Transaction> transactions;

}