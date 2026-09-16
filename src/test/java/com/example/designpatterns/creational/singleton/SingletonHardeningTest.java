package com.example.designpatterns.creational.singleton;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies the two hardening measures the classic and Bill Pugh singletons
 * add on top of their basic lazy-initialization: a reflective call to the
 * private constructor must fail once an instance already exists, and
 * deserializing a captured byte stream must yield the existing instance
 * rather than a new one.
 */
class SingletonHardeningTest {

    @Test
    void doubleCheckedLockingRejectsSecondReflectiveConstruction() throws Exception {
        DoubleCheckedLockingSingleton.getInstance();

        Constructor<DoubleCheckedLockingSingleton> constructor =
                DoubleCheckedLockingSingleton.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertThrows(IllegalStateException.class, () -> {
            throw thrown.getCause();
        });
    }

    @Test
    void doubleCheckedLockingDeserializationReturnsExistingInstance() throws Exception {
        DoubleCheckedLockingSingleton original = DoubleCheckedLockingSingleton.getInstance();

        DoubleCheckedLockingSingleton deserialized = serializeThenDeserialize(original);

        assertSame(original, deserialized, "deserialization must not create a second instance");
    }

    @Test
    void billPughRejectsSecondReflectiveConstruction() throws Exception {
        BillPughSingleton.getInstance();

        Constructor<BillPughSingleton> constructor = BillPughSingleton.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertThrows(IllegalStateException.class, () -> {
            throw thrown.getCause();
        });
    }

    @Test
    void billPughDeserializationReturnsExistingInstance() throws Exception {
        BillPughSingleton original = BillPughSingleton.getInstance();

        BillPughSingleton deserialized = serializeThenDeserialize(original);

        assertSame(original, deserialized, "deserialization must not create a second instance");
    }

    @SuppressWarnings("unchecked")
    private static <T> T serializeThenDeserialize(T instance) throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(byteStream)) {
            out.writeObject(instance);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteStream.toByteArray()))) {
            return (T) in.readObject();
        }
    }
}
