package com.syntifi.ori.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Transaction model, only a couple of the fields available in the chain are
 * needed
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Entity
@Indexed
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends PanacheEntityBase {
    @Id
    @NotNull
    @FullTextField(analyzer = "standard")
    public String hash;

    @NotNull
    @Column(name = "time_stamp")
    public Date timeStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id", nullable = false)
    public Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id", nullable = false)
    public Account toAccount;

    @NotNull
    @GenericField
    @Min(0)
    public Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    public Block block;
}
