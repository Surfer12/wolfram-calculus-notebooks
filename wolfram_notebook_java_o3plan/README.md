# Wolfram Notebook Java

A modern, secure web-based Wolfram Language notebook system with enterprise-grade HTTPS security.

## 🔒 Security Features

### HTTPS/TLS Security (Highest Standards)
- **TLS 1.2 & 1.3 only** - Latest protocols
- **Modern cipher suites** - AEAD ciphers with forward secrecy
- **HSTS** - HTTP Strict Transport Security with preload
- **Perfect Forward Secrecy** - ECDHE key exchange
- **OCSP Stapling** - Certificate validation optimization

### Security Headers
- **Content Security Policy (CSP)** - XSS prevention
- **X-Frame-Options** - Clickjacking protection  
- **X-Content-Type-Options** - MIME sniffing prevention
- **Referrer Policy** - Information leakage control
- **Permissions Policy** - Browser feature restrictions
- **Cross-Origin Policies** - COEP, COOP, CORP

### Application Security
- **Spring Security** - Authentication & authorization
- **Secure cookies** - HttpOnly, Secure, SameSite
- **CORS** - Restrictive cross-origin policy
- **Input validation** - SQL injection prevention
- **Session management** - Secure session handling

## 🚀 Quick Start

### Development (HTTP)
```bash
./gradlew bootRun
```
Access: http://localhost:8080

### Production (HTTPS)
```bash
# Generate SSL certificate (development only)
./generate-ssl-cert.sh

# Run with SSL
./gradlew bootRun --args='--spring.profiles.active=ssl'
```
Access: https://localhost:8443

## 📡 API Endpoints

### Notebooks
- `POST /api/notebooks` - Create notebook
- `GET /api/notebooks` - List notebooks  
- `GET /api/notebooks/{id}` - Get notebook
- `DELETE /api/notebooks/{id}` - Delete notebook

### Evaluation
- `POST /api/notebooks/{id}/cells` - Evaluate expression
- `GET /api/notebooks/{id}/history` - Get evaluation history
- `DELETE /api/notebooks/{id}/history` - Clear history

### System
- `GET /api/status` - System status
- `GET /actuator/health` - Health check

### WebSocket
- Connect: `ws://localhost:8080/ws` (or `wss://` for SSL)
- Subscribe: `/topic/notebook/{id}` - Real-time results

## 🔧 Configuration

### SSL Certificate (Production)
Replace the self-signed certificate with a proper CA-issued certificate:

1. Obtain certificate from trusted CA (Let's Encrypt, etc.)
2. Convert to PKCS12 format if needed
3. Update `application.yml`:
   ```yaml
   server:
     ssl:
       key-store: classpath:your-cert.p12
       key-store-password: your-password
   ```

### Environment Variables
```bash
export SSL_KEYSTORE_PASSWORD=your-secure-password
export SPRING_PROFILES_ACTIVE=ssl
```

## 🛡️ Security Best Practices

### For Production:
1. **Use proper SSL certificates** from trusted CA
2. **Enable SSL profile** (`spring.profiles.active=ssl`)
3. **Configure firewall** - Only allow necessary ports
4. **Regular updates** - Keep dependencies current
5. **Monitor logs** - Watch for security events
6. **Backup encryption** - Encrypt database backups

### Network Security:
- Run behind reverse proxy (nginx/Apache)
- Use Web Application Firewall (WAF)
- Implement rate limiting
- Monitor for DDoS attacks

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Web Client    │◄──►│   Spring Boot    │◄──►│ Wolfram Kernel  │
│  (React/HTML)   │    │   (REST + WS)    │    │   (J/Link)      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌──────────────────┐
                       │   SQLite DB      │
                       │  (History/State) │
                       └──────────────────┘
```

## 📋 Requirements

- **Java 21+** - Modern JVM with latest security features
- **Wolfram Engine** - Mathematica or Wolfram Engine installation
- **Gradle 8.5+** - Build system

## 🔍 Monitoring

### Health Checks
- Application: `/actuator/health`
- Database: Automatic via Spring Boot
- Wolfram kernels: Via session manager

### Logging
- **Structured JSON logs** - `logs/app.log`
- **Security events** - Authentication, authorization
- **Performance metrics** - Response times, errors
- **Audit trail** - All evaluations logged

## 🚨 Security Considerations

⚠️ **Code Execution**: This system executes arbitrary Wolfram Language code. In production:
- Run in sandboxed environment
- Implement user authentication
- Add code review/approval workflows
- Monitor resource usage
- Set execution timeouts

## 📄 License

This project demonstrates enterprise security patterns for Wolfram Language web applications.
