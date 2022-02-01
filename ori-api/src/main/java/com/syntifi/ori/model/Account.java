package com.syntifi.ori.model;

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

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(HashTokenId.class)
public class Account extends PanacheEntityBase {

    @Id
    private String hash;

    @Id
    @MapsId("symbol")
    @ManyToOne
    private Token token;

    @Column(name = "public_key")
    private String publicKey;

    private String label;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "fromAccount")
    private Set<Transaction> out;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "toAccount")
    private Set<Transaction> in;
}
