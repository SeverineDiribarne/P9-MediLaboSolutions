package com.medilabo.medilabo.session;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionStoreTest {

    @Test
    @DisplayName("put/get/isValid/remove fonctionnent")
    void basics() {
        SessionStore store = new SessionStore();
        assertFalse(store.isValid("s1"));
        assertNull(store.getUsername("s1"));

        store.put("s1", "john@doe");
        assertTrue(store.isValid("s1"));
        assertEquals("john@doe", store.getUsername("s1"));

        store.remove("s1");
        assertFalse(store.isValid("s1"));
        assertNull(store.getUsername("s1"));
    }
}
