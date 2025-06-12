# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This repository contains **two distinct Java projects**:

1. **Main CLI Application** (`src/main/java/`) - Simple CLI with basic data structures
2. **Spring Boot Web Application** (`wolfram_notebook_java_o3plan/`) - Full notebook server with REST API, WebSocket support, and database persistence

## Build Commands

### Spring Boot Web Application (Primary)
```bash
# Database setup (required first time)
cd wolfram_notebook_java_o3plan
./gradlew flywayMigrate    # Initialize database schema

# Development server
./gradlew bootRun          # Start server on localhost:8080

# Build and package
./gradlew bootJar          # Create executable JAR
./gradlew bootBuildImage   # Build Docker image

# Database management
./gradlew flywayInfo       # Show migration status
./gradlew flywayClean      # Reset database
```

### CLI Application (Legacy)
```bash
# Environment setup
magic shell          # Enter project environment
magic install        # Install dependencies

# Build and run
./gradlew build
./gradlew run

# Testing
./gradlew test        # Run all tests
./gradlew test --tests "ClassName"  # Run specific test class
```

## Architecture Overview

This is a **Java-based calculus notebook application** with Wolfram/Mathematica integration via J/Link.

### Spring Boot Web Application (`wolfram_notebook_java_o3plan/`)

**Core Components:**
- `NotebookController`: REST API endpoints for notebook CRUD operations
- `NotebookWebSocketController`: Real-time evaluation updates via STOMP/WebSocket
- `WolframService`: J/Link integration for Mathematica kernel management
- `NotebookRepository`: SQLite database persistence with Flyway migrations

**Domain Models (Java Records):**
- `Notebook`: Container for cells and metadata
- `Cell`: Individual notebook cells with content and evaluation results
- `EvaluationResult`: Wolfram kernel execution results

**REST API Endpoints:**
- `POST /api/notebooks` - Create notebook
- `GET /api/notebooks/{id}` - Get notebook with cells
- `POST /api/notebooks/{id}/cells` - Evaluate Wolfram expression
- `GET /api/notebooks/{id}/history` - Evaluation history
- `DELETE /api/notebooks/{id}` - Delete notebook

**WebSocket Topics:**
- `/topic/notebook/{id}` - Real-time evaluation updates

### Legacy CLI Application (`src/main/java/`)
- Basic data structures and API interaction models
- Foundation classes for the Spring Boot application

## Key Technologies

### Spring Boot Application
- **Java 21** with Spring Boot 3.x
- **J/Link**: Wolfram Mathematica kernel integration
- **SQLite + Flyway**: Database persistence and migrations
- **Spring WebSocket**: Real-time STOMP messaging
- **Jackson**: JSON processing for REST APIs
- **Structured logging**: JSON format with rolling file appender

### Legacy CLI Application
- **Java 24** with Gradle 8.11.1 toolchain
- **Google Guava** for utility libraries
- **SLF4J + Logback** for logging
- **JUnit 5 + Mockito** for testing

### Environment Management
- **Magic shell** and **Pixi.toml**: Python/AI dependencies (Anthropic, OpenAI, MAX framework)
- **MCP server configuration**: Docker-based development proxy

## Development Setup

### First Time Setup
1. **Database initialization**: `cd wolfram_notebook_java_o3plan && ./gradlew flywayMigrate`
2. **Start development server**: `./gradlew bootRun`
3. **Verify setup**: Visit `http://localhost:8080/api/status`

### Wolfram Integration Requirements
- **J/Link JAR**: Requires local Wolfram installation at `/Applications/Wolfram.app/Contents/SystemFiles/Links/JLink/JLink.jar`
- **Kernel lifecycle**: Automatically managed with proper cleanup via `AutoCloseable`
- **Error handling**: MathLinkException handling for connection issues

## Code Patterns

- **Java Records**: Domain models use modern record syntax
- **Constructor DI**: Spring dependency injection without @Autowired annotations
- **Resource management**: Consistent use of try-with-resources and AutoCloseable
- **Defensive programming**: Null checks and state validation throughout
- **Structured logging**: JSON format with MDC context for request tracing

## Development Configuration

The project includes comprehensive Cursor IDE configuration:
- `.cursor/rules/magic.mdc`: Magic shell usage guidelines
- `.cursor/rules/interaction_style.yaml`: Interaction principles focusing on cognitive load management
- `mcp.json`: MCP server configuration for Docker-based development
- `optimization.m`: Mathematica optimization script for Wolfram integration testing