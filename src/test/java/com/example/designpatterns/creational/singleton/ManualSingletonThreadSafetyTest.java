package com.example.designpatterns.creational.singleton;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManualSingletonThreadSafetyTest {

    @Test
    void getInstanceReturnsExactlyOneInstanceUnderConcurrentAccess() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        Set<DoubleCheckedLockingSingleton> instances = new CopyOnWriteArraySet<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    instances.add(DoubleCheckedLockingSingleton.getInstance());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS), "threads did not finish in time");
        executor.shutdown();

        assertEquals(1, instances.size(), "expected exactly one Singleton instance across all threads");
    }
}
