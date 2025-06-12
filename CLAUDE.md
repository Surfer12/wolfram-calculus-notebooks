# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

This project uses **Gradle** as the primary build system with **Magic shell** for environment management:

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

Alternative Maven build is also available:
```bash
mvn compile
mvn test
mvn exec:java
```

## Architecture Overview

This is a **Java-based calculus notebook application** designed for Wolfram/Mathematica integration. The project is in early development, focusing on foundational data structures and API integration models.

**Core Package Structure:**
- `com.example.wolframnotebooks.App`: Main application entry point with SLF4J logging
- `com.example.wolframnotebooks.Interaction`: Models API interactions with metadata, timestamps, and payloads
- Custom data structures (`AbstractGenericHashMap`, `LinkedGenericHashMap`, `MinHeap`) for performance-critical operations

**Planned Integration Points:**
- Wolfram Engine integration via J/Link
- REST API endpoints for notebook operations
- Web-based notebook interface with collaborative features
- Support for both native `.nb` Wolfram files and standalone web notebooks

## Key Technologies

- **Java 24** with Gradle 8.11.1 toolchain
- **Google Guava** for utility libraries
- **SLF4J + Logback** for logging
- **JUnit 5 + Mockito** for testing
- **Magic shell** for environment and dependency management
- **Pixi.toml** configuration includes Python/AI dependencies (Anthropic, OpenAI, MAX framework)

## Development Configuration

The project includes comprehensive Cursor IDE configuration:
- `.cursor/rules/magic.mdc`: Magic shell usage guidelines
- `.cursor/rules/interaction_style.yaml`: Interaction principles focusing on cognitive load management
- `mcp.json`: MCP server configuration for Docker-based development
- `optimization.m`: Mathematica optimization script for Wolfram integration testing