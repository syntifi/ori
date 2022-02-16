package com.syntifi.ori.chains.cspr;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syntifi.casper.sdk.identifier.block.BlockIdentifier;
import com.syntifi.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.syntifi.casper.sdk.model.block.JsonBlockData;
import com.syntifi.casper.sdk.model.transfer.TransferData;
import com.syntifi.casper.sdk.service.CasperService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCsprChainTest implements InitializingBean {

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Boolean initialized = false;

    @Autowired
    private CasperService service;

    @Override
    public void afterPropertiesSet() throws Exception {
        synchronized (initialized) {
            if (!initialized) {
                activateMockForFiles();
                initialized = true;
            }
        }
    }

    private void activateMockForFiles() {

        when(service.getBlock(any(BlockIdentifier.class)))
                .thenAnswer(i -> {
                    BlockIdentifier blockIdentifier = i.getArgument(0);
                    if (blockIdentifier instanceof HeightBlockIdentifier) {
                        String blockHeight = String.valueOf(((HeightBlockIdentifier) i.getArgument(0)).getHeight());
                        String filename = String.format("test-data/blocks/block-%s.json", blockHeight);
                        String json = loadJsonFromFile(filename);
                        if (json != null) {
                            JsonBlockData block = OBJECT_MAPPER.readValue(json, JsonBlockData.class);
                            return block;
                        } else {
                            return null;
                        }
                    } else {
                        return i.callRealMethod();
                    }
                });

        when(service.getBlockTransfers(any(BlockIdentifier.class)))
                .thenAnswer(i -> {
                    BlockIdentifier blockIdentifier = i.getArgument(0);
                    if (blockIdentifier instanceof HeightBlockIdentifier) {
                        String blockHeight = String.valueOf(((HeightBlockIdentifier) i.getArgument(0)).getHeight());
                        String filename = String.format("test-data/transfers/transfer-%s.json", blockHeight);
                        String json = loadJsonFromFile(filename);
                        if (json != null) {
                            TransferData transfers = OBJECT_MAPPER.readValue(json, TransferData.class);
                            return transfers;
                        } else {
                            return null;
                        }
                    } else {
                        return i.callRealMethod();
                    }
                });
    }

    /**
     * Reads to a string any file from resources folder
     * 
     * @param filename
     * @return a String with file content
     * @throws IOException
     */
    private String loadJsonFromFile(String filename) throws IOException {
        String fileJson;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                return null;
            }
            // copy stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            fileJson = new String(baos.toByteArray());
        }
        return fileJson;
    }
}
