package com.medilabo.medilabo_gateway.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtBlackListTest {

    @Test
    void blacklistAndCheck() {
        JwtBlackList bl = new JwtBlackList();
        assertThat(bl.isBlackListed("t")).isFalse();
        bl.blackList("t");
        assertThat(bl.isBlackListed("t")).isTrue();
    }
}
