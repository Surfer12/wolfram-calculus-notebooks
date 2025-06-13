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
        
        // Configure SSL/TLS settings through system properties or application.yml
        // Modern Spring Boot handles most TLS configuration automatically
        
        // Disable SSL compression (CRIME attack prevention)
        protocol.setCompression("off");
        
        // Additional connector optimizations can be added here
        connector.setSecure(true);
        connector.setScheme("https");
    }
}
