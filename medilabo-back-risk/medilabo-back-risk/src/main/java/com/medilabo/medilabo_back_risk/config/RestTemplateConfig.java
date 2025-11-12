package com.medilabo.medilabo_back_risk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class RestTemplateConfig {

        @Bean
        public RestTemplate restTemplate() throws Exception {
                // Trust all certificates (DEV only). For production, configure proper trust store.
                TrustManager[] trustAllCerts = new TrustManager[]{
                                new X509TrustManager() {
                                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                                }
                };

                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

                return new RestTemplate();
        }
}
