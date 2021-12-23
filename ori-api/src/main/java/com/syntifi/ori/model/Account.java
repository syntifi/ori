package com.syntifi.ori.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Indexed
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account extends PanacheEntityBase {
    @JsonIgnore
    @NotNull
    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false)
    private Token token;

    @JsonGetter("token")
    public String getJsonToken() {
        return token.getSymbol();
    }

    @Id
    @NotNull
    @Column(nullable = false)
    @FullTextField(analyzer = "standard")
    private String hash;

    @Column(name = "public_key")
    @FullTextField(analyzer = "standard")
    private String publicKey;

    @FullTextField(analyzer = "standard")
    private String label;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "fromAccount")
    @IndexedEmbedded
    private Set<Transaction> out;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "toAccount")
    @IndexedEmbedded
    private Set<Transaction> in;
    
}
