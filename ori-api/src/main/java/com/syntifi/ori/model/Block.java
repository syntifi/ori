package com.syntifi.ori.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
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
public class Block extends PanacheEntityBase {

    @Id
    @NotNull
    @Column(unique = true)
    private String hash;

    @JsonIgnore
    @NotNull
    @ManyToOne
    @JoinColumn(name = "token_id", nullable = false)
    private Token token;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ")
    @Column(name = "time_stamp")
    private Date timeStamp;

    @NotNull
    @Min(-1)
    private Long height;

    @NotNull
    @Min(-1)
    private Long era;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_block_id", nullable = true)
    private Block parent;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "parent")
    private Block child;

    @NotNull
    private String root;

    @NotNull
    private String validator;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "block")
    private Set<Transaction> transactions;

    @JsonGetter("parent")
    public String getJsonParent() {
        return parent == null ? null : parent.getHash();
    }

    @JsonGetter("token")
    public String getJsonToken() {
        return token.getSymbol();
    }
}