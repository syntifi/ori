package com.syntifi.ori.model;

import java.util.Date;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Block {
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ")
    public Date timeStamp;

    @NotNull
    public String hash;

    @NotNull
    public Long height;

    @NotNull
    public Long era;

    @NotNull
    public String parent;

    @NotNull
    public String root;

    @NotNull
    public String validator;

    public Block () {};

    public Block(Date timeStamp, String hash, Long height, Long era, 
                    String parent, String root, String validator) {
        this.timeStamp = timeStamp;
        this.hash = hash;
        this.height = height;
        this.era = era;
        this.parent = parent;
        this.root = root;
        this.validator = validator;
    }
}