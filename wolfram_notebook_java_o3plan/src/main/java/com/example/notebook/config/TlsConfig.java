package com.example.notebook.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TLS/SSL configuration with modern cipher suites and protocols.
 * Activated with 'ssl' profile for production use.
 */
@Configuration
@Profile("ssl")
public class TlsConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        return factory -> {
            factory.addConnectorCustomizers(this::customizeConnector);
        };
    }

    private void customizeConnector(Connector connector) {
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        
        // Enable only TLS 1.2 and 1.3 (highest security)
        protocol.setSSLEnabledProtocols("TLSv1.2,TLSv1.3");
        
        // Modern cipher suites - prioritize AEAD ciphers and forward secrecy
        protocol.setCiphers(String.join(",", 
            // TLS 1.3 cipher suites (AEAD only)
            "TLS_AES_256_GCM_SHA384",
            "TLS_CHACHA20_POLY1305_SHA256",
            "TLS_AES_128_GCM_SHA256",
            
            // TLS 1.2 cipher suites with forward secrecy
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256",
            "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256"
        ));
        
        // Prefer server cipher order
        protocol.setUseServerCipherSuitesOrder(true);
        
        // Disable SSL compression (CRIME attack prevention)
        protocol.setCompression("off");
        
        // Enable OCSP stapling for certificate validation
        protocol.setUseServerCipherSuitesOrder(true);
        
        // Session settings
        protocol.setSessionCacheSize(20480);
        protocol.setSessionTimeout(300);
        
        // Disable client-initiated renegotiation
        protocol.setAllowUnsafeLegacyRenegotiation(false);
    }
}
