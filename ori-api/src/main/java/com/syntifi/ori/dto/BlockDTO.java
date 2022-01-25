package com.syntifi.ori.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.BlockId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlockDTO {
    private static final BlockDTO DEFAULT_DTO_VALUE = null;

    private String hash;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ")
    private Date timeStamp;

    private Long height;

    private Long era;

    private String root;

    private String validator;

    private String parent;

    private String token;

    public static BlockDTO fromModel(Block model) {
        return model != null
                ? new BlockDTO(model.getHash(), model.getTimeStamp(), model.getHeight(), model.getEra(),
                        model.getRoot(),
                        model.getValidator(), model.getParent() != null ? model.getParent().getHash() : null,
                        model.getToken().getSymbol())
                : DEFAULT_DTO_VALUE;
    }
    
    //TODO static
    public Block toModel() {
        Block block = new Block();
        block.setHash(hash);
        block.setTimeStamp(timeStamp);
        block.setHeight(height);
        block.setEra(era);
        block.setRoot(root);
        block.setValidator(validator);
        block.setBlockId(new BlockId(hash, token));
        return block;
    }
}