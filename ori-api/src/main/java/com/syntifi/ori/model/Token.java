package com.syntifi.ori.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token extends PanacheEntity{
    @NotNull
    @Column(unique = true)
    private String symbol; 

    @NotNull
    private String name; 

    @NotNull
    private String protocol; 

    @JsonIgnore
    @OneToMany(mappedBy = "token")
    private Set<Block> blocks;

    @JsonIgnore
    @OneToMany(mappedBy = "token")
    private Set<Account> accounts;
}
