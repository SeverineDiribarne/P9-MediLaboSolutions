package com.medilabo.medilabo_gateway.config;

import java.io.Serializable;

public record User(String username, String password) implements Serializable {
}
