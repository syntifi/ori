package com.syntifi.ori.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OriBlockPost {

    private String timeStamp;

    private String hash;

    private Long height;

    private Long era;

    private String root;

    private String validator;
}
