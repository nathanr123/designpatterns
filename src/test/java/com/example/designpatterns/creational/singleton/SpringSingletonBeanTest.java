package com.example.designpatterns.creational.singleton;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
class SpringSingletonBeanTest {

    @Autowired
    private ConfigurationManager first;

    @Autowired
    private ConfigurationManager second;

    @Test
    void springInjectsTheSameSingletonScopedBeanEverywhere() {
        assertSame(first, second, "Spring should hand out the same bean instance for both injection points");
        assertSame(first.getInstanceId(), second.getInstanceId(), "instanceId should be identical for the same bean");
    }
}
