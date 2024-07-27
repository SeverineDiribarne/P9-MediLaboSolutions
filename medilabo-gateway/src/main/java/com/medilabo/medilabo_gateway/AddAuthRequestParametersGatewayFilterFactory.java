package com.medilabo.medilabo_gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class AddAuthRequestParametersGatewayFilterFactory extends AbstractGatewayFilterFactory<AddAuthRequestParametersGatewayFilterFactory.Config> {

    @Getter
    @Setter
    public static class Config {
        private String defaultUsername;
        private String defaultPassword;
    }

    public AddAuthRequestParametersGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        System.out.println("je passe par la methode apply de AddAuthRequestParametersGatewayFilterFactory");

        return (exchange, chain) -> DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("Request body: " + body);

                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> bodyMap;
                    try {
                        bodyMap = mapper.readValue(body, new TypeReference<>() {
                        });
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }

                    String username = bodyMap.getOrDefault("username", config.getDefaultUsername());
                    String password = bodyMap.getOrDefault("password", config.getDefaultPassword());

                    System.out.println("Username: " + username);
                    System.out.println("Password: " + password);

                    URI uri = exchange.getRequest().getURI();
                    URI modifiedUri = UriComponentsBuilder.fromUri(uri)
                            .replaceQueryParam("username", username)
                            .replaceQueryParam("password", password)
                            .build(true).toUri();

                    System.out.println("Modified URI: " + modifiedUri);

                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().uri(modifiedUri).build();
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                });
    }
}
