/*
 * Copyright 2018 Mirko Sertic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mirkosertic.bytecoder.backend.opencl;

import de.mirkosertic.bytecoder.api.opencl.Context;
import de.mirkosertic.bytecoder.api.opencl.Kernel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class CPUContext implements Context {

    private final ExecutorService executorService;

    public CPUContext() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {

            int counter = 0;

            @Override
            public Thread newThread(Runnable aRunnable) {
                return new Thread(aRunnable, "OpenCL-CPU#" + (counter ++));
            }
        });
    }

    @Override
    public void compute(int aNumberOfStreams, Kernel aKernel) {
        CountDownLatch theLatch = new CountDownLatch(aNumberOfStreams);
        for (int i=0;i<aNumberOfStreams;i++) {
            final int theWorkItemId = i;
            executorService.submit(() -> {
                try {
                    aKernel.set_global_id(0, theWorkItemId);
                    aKernel.processWorkItem();
                } finally {
                    theLatch.countDown();
                }
            });
        }
        try {
            theLatch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Something went wrong", e);
        }
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }
}
