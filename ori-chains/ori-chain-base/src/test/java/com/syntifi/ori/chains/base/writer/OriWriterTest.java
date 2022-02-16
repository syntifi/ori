package com.syntifi.ori.chains.base.writer;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.syntifi.ori.chains.base.MockChainConfig;
import com.syntifi.ori.chains.base.MockChainCrawlerJob;
import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.exception.OriItemWriterException;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.chains.base.service.MockTestChainService;
import com.syntifi.ori.client.MockOriRestClient;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBatchTest
@ContextConfiguration(classes = { MockChainConfig.class, MockChainCrawlerJob.class })
@TestPropertySource("classpath:application.properties")
public class OriWriterTest {

    @Autowired
    private MockOriRestClient oriClient;

    @Autowired
    private MockTestChainService mockTestChainService;

    @Autowired
    private OriChainConfigProperties oriChainConfigProperties;

    @Autowired
    private OriWriter writer;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @BeforeEach
    void beforeEach(@Autowired MockTestChainService service) {
        service.reset();
        oriClient.reset();
    }

    @Test
    public void testWriter_emptyOrNullList_shouldThrowOriWriterException() throws IOException, InterruptedException {
        assertThrows(OriItemWriterException.class, () -> writer.write(null));

        List<OriData> oriData = new LinkedList<>();
        assertThrows(OriItemWriterException.class, () -> writer.write(oriData));
    }

    @Test
    public void testWriter_savingExisting_shouldIgnoreNotThrowOriWriterException()
            throws IOException, InterruptedException {
        List<OriData> oriData = new LinkedList<>();

        oriData.add(getBlock());

        assertDoesNotThrow(() -> writer.write(oriData));

        assertDoesNotThrow(() -> writer.write(oriData));
    }

    @Test
    public void testWriter_badRequest_onWriteBlock_shouldThrowItemWriterException()
            throws IOException, InterruptedException {
        oriClient.generateErrorOnRequest(HttpStatus.BAD_REQUEST, "postBlock");

        List<OriData> oriData = new LinkedList<>();

        oriData.add(getBlock());

        assertThrows(OriItemWriterException.class, () -> writer.write(oriData));
    }

    @Test
    public void testWriter_badRequest_onWriteBlocks_shouldThrowItemWriterException()
            throws IOException, InterruptedException {
        oriClient.generateErrorOnRequest(HttpStatus.BAD_REQUEST, "postBlocks");

        List<OriData> oriData = new LinkedList<>();

        oriData.add(getBlock());
        oriData.add(getBlock());

        assertThrows(OriItemWriterException.class, () -> writer.write(oriData));
    }

    @Test
    public void testWriter_badRequest_onWriteTransactions_shouldThrowItemWriterException()
            throws IOException, InterruptedException {
        oriClient.generateErrorOnRequest(HttpStatus.BAD_REQUEST, "postTransfer");

        List<OriData> oriData = new LinkedList<>();

        oriData.add(getBlock());
        oriData.add(getBlock());

        assertThrows(OriItemWriterException.class, () -> writer.write(oriData));
    }

    @Test
    public void testWriter_badRequest_onWriteAccount_shouldThrowItemWriterException()
            throws IOException, InterruptedException {
        oriClient.generateErrorOnRequest(HttpStatus.BAD_REQUEST, "postAccount");

        List<OriData> oriData = new LinkedList<>();

        oriData.add(getBlock());
        oriData.add(getBlock());

        assertThrows(OriItemWriterException.class, () -> writer.write(oriData));
    }

    private OriData getBlock() {
        BlockDTO block = mockTestChainService.getNextBlock().toDTO();
        block.setTokenSymbol(oriChainConfigProperties.getChainTokenSymbol());

        List<TransactionDTO> transfers = mockTestChainService.getTransfers(block.getHash())
                .stream()
                .map(t -> t.toDTO())
                .collect(Collectors.toList());
        transfers.forEach(a -> a.setTokenSymbol(oriChainConfigProperties.getChainTokenSymbol()));
        return OriData.builder()
                .block(block)
                .transfers(transfers).build();
    }
}
