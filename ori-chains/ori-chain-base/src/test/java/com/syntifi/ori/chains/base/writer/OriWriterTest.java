package com.syntifi.ori.chains.base.writer;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.ori.chains.base.MockChainConfig;
import com.syntifi.ori.chains.base.MockChainCrawlerJob;
import com.syntifi.ori.chains.base.exception.OriItemWriterException;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.chains.base.service.MockTestChainService;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBatchTest
@ContextConfiguration(classes = { MockChainConfig.class, MockChainCrawlerJob.class })
@TestPropertySource("classpath:application.properties")
public class OriWriterTest {

    @Autowired
    private OriWriter writer;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @BeforeEach
    void beforeEach(@Autowired MockTestChainService service) {
        service.reset();
    }

    @Test
    public void testWriter_emptyOrNullList_shouldThrowOriWriterException() throws IOException, InterruptedException {
        assertThrows(OriItemWriterException.class, () -> writer.write(null));

        List<OriData> oriData = new LinkedList<>();
        assertThrows(OriItemWriterException.class, () -> writer.write(oriData));
    }

    @Test
    public void testWriter_savingExisting_shouldThrowOriWriterException() throws IOException, InterruptedException {
        List<OriData> oriData = new LinkedList<>();

        oriData.add(getBlock(0, 0, "parentHash"));

        assertDoesNotThrow(() -> writer.write(oriData));

        assertDoesNotThrow(() -> writer.write(oriData));
    }

    private OriData getBlock(long height, int tranferCount, String parentHash) {
        List<TransactionDTO> transfers = new LinkedList<>();

        return OriData.builder()
                .block(BlockDTO.builder()
                        .hash("hash-" + height)
                        .height(height)
                        .era(0L)
                        .parent(parentHash)
                        .root("root-" + height)
                        .build())
                .transfers(transfers).build();
    }
}
