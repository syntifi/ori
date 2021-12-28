package com.syntifi.ori.chains.cspr.listeners;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public class CustomChunkListener implements ChunkListener {

	@Override
	public void afterChunk(ChunkContext context) {
		System.out.println("Called afterChunk().");
	}

	@Override
	public void beforeChunk(ChunkContext context) {
		System.out.println("Called beforeChunk().");
	}

       @Override
	public void afterChunkError(ChunkContext context) {
		System.out.println("Called afterChunkError().");
	}
}