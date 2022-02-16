package com.syntifi.ori.chains.eth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;

public abstract class AbstractEthChainTest implements InitializingBean {

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Boolean initialized = false;

    @Autowired
    private Web3j service;

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
        when(service.ethGetBlockByNumber(any(DefaultBlockParameter.class), eq(true)))
                .thenAnswer(i -> {
                    @SuppressWarnings("unchecked")
                    Request<?, EthBlock> request = Mockito.spy((Request<?, EthBlock>) i.callRealMethod());
                    String blockNumber = ((DefaultBlockParameter) i.getArgument(0)).getValue().toUpperCase();
                    doAnswer(new Answer<EthBlock>() {
                        @Override
                        public EthBlock answer(InvocationOnMock invocation) throws Throwable {
                            String filename = String.format("test-data/block-%s.json", blockNumber);
                            String json = loadJsonFromFile(filename);
                            if (json != null) {
                                EthBlock block = OBJECT_MAPPER.readValue(json, EthBlock.class);
                                return block;
                            } else {
                                return null;
                            }
                        }
                    }).when(request).send();

                    return request;
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
