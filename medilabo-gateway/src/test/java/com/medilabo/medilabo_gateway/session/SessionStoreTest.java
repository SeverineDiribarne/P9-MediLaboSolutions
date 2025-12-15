package com.medilabo.medilabo_gateway.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionStoreTest {

    @Test
    void putAndRetrieveAndRemoveSession() {
        SessionStore store = new SessionStore();
        store.put("sess-1","user@example.com");
        assertThat(store.getSessionNumber("user@example.com")).isEqualTo("sess-1");
        assertThat(store.isValid("sess-1")).isTrue();
        store.remove("sess-1");
        assertThat(store.isValid("sess-1")).isFalse();
        assertThat(store.getSessionNumber("user@example.com")).isNull();
    }
}
