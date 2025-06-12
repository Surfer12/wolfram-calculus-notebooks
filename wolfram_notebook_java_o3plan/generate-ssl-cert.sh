#!/bin/bash

# Generate self-signed certificate for development/testing
# For production, use certificates from a trusted CA

echo "Generating self-signed SSL certificate for development..."

# Create keystore directory
mkdir -p src/main/resources

# Generate private key and certificate
keytool -genkeypair \
    -alias wolfram-notebook \
    -keyalg RSA \
    -keysize 4096 \
    -sigalg SHA256withRSA \
    -validity 365 \
    -keystore src/main/resources/keystore.p12 \
    -storetype PKCS12 \
    -storepass changeit \
    -keypass changeit \
    -dname "CN=localhost, OU=Development, O=Wolfram Notebook, L=City, ST=State, C=US" \
    -ext SAN=dns:localhost,ip:127.0.0.1

echo "SSL certificate generated successfully!"
echo "Keystore location: src/main/resources/keystore.p12"
echo "Password: changeit"
echo ""
echo "To run with SSL, use: ./gradlew bootRun --args='--spring.profiles.active=ssl'"
echo ""
echo "⚠️  This is a self-signed certificate for development only!"
echo "   For production, obtain certificates from a trusted Certificate Authority."
