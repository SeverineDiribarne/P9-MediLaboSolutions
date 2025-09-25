package com.medilabo.medilabo_gateway.models;

import java.io.Serializable;

public record User(String username, String password) implements Serializable {
}
