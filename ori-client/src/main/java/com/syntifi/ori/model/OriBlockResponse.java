package com.syntifi.ori.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OriBlockResponse {
    private Date timeStamp;

    private String hash;

    private Long height;

    private Long era;

    private String parent;

    private String root;

    private String validator;

    private String child;

    private String token;


}