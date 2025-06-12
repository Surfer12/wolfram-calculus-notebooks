## Recommended Immediate Next Steps

1. **Confirm UI Strategy**  
   • Do we rely on native `.nb` files (+ J/Link hooks), or do we build a standalone web notebook?  
   • Clarify because it affects the amount of Java code vs. front-end work.

use both 
   native `.nb` files (+ J/Link hooks)
   and create a standalone web notebook for user convenience 

2. **Bootstrap Java Project**  
   ```bash
   mkdir wolfram-notebook-java && cd $_
   gradle init --type java-application
   ```
   Add J/Link dependency (local or via Maven Central mirror):
   ```groovy
   dependencies {
       implementation files('/Applications/Mathematica.app/SystemFiles/Links/JLink/JLink.jar')
   }
   ```

3. **Prototype Kernel Bridge**  
   ```java
   KernelLink ml = MathLinkFactory.createKernelLink("-linkmode launch -linkname 'wolframscript -wstp'");
   ml.discardAnswer();                // First packet is version info
   Expr result = ml.evaluateToExpression("2+2", 0);
   System.out.println(result.asString()); // Expect "4"
   ml.close();
   ```
   ✓ Verify local Wolfram Engine is reachable.

4. **Define Domain Models** (`src/main/java/com/example/notebook/model`)
   ```java
   record Notebook(UUID id, String title, Instant created, Instant updated) {}
   record Cell(UUID id, UUID notebookId, String input, String output,
               Instant submittedAt, Duration evalTime) {}
   ```

5. **Pick Persistence & Logging Stack**  
   • Add SQLite via `org.xerial:sqlite-jdbc`.  
   • Configure `logback.xml` with JSON encoder.

6. **Spike a “Evaluate & Store” Flow**  
   1. Read `stdin` line.  
   2. Send to kernel.  
   3. Persist cell + result.  
   4. Print output.

7. **Unit & Integration Tests**  
   • JUnit 5 + Testcontainers (for DB).  
   • Mock kernel with TestDouble for offline tests.

8. **Road-map Next Features**  
   | Milestone | Deliverable |
   |-----------|-------------|
   | M1 | CLI prototype: evaluate & store history |
   | M2 | REST API (Spring Boot or Micronaut) |
   | M3 | Basic web notebook (React/Vite) |
   | M4 | Version timeline UI, diff viewer |
   | M5 | Multi-user auth, sharing, export to .nb |

---

## What I Can Do Immediately

1. Scaffold the Gradle project in your current directory.  
2. Drop in sample kernel-bridge code.  
3. Set up basic SQLite schema migration via Flyway.  

Let me know:

• Which UI path (native Wolfram vs. custom web) do you prefer for now?  
• Shall I proceed to create the Gradle skeleton on disk?  

(If yes, I’ll run tBelow is a concrete, end-to-end starter blueprint that supports BOTH goals:

1. Keep using native Wolfram Notebook files (*.nb) with J/Link hooks.  
2. Grow a full web notebook (REST + WebSocket backend, React front-end).

Everything is laid out so you can either (a) copy/paste the snippets yourself, or (b) tell me “Yes, scaffold it” and I’ll run the shell commands to create the files in your current repo.

---

## 1. Project Layout

```
wolfram-notebook-java/
├─ build.gradle.kts
├─ settings.gradle.kts
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/example/notebook/
│  │  │     ├─ kernel/           <-- J/Link wrappers
│  │  │     ├─ model/            <-- POJOs / JPA entities
│  │  │     ├─ persistence/      <-- Repositories (JDBC or JPA)
│  │  │     ├─ service/          <-- SessionManager, HistoryService
│  │  │     └─ web/              <-- REST + WebSocket controllers
│  │  └─ resources/
│  │     ├─ logback.xml
│  │     └─ db/migration/        <-- Flyway SQL migrations
│  └─ test/
└─ ui-web/                       <-- React (Vite) or Compose Desktop
    └─ ...
```

---

## 2. Gradle (+J/Link) Configuration

`build.gradle.kts` (Kotlin DSL, works the same in Groovy):

```kotlin
plugins {
    id("java")
    id("application")
    id("org.flywaydb.flyway") version "10.9.0"
}

java.sourceCompatibility = JavaVersion.VERSION_17       // or 21

repositories { mavenCentral() }

dependencies {
    // -- Wolfram J/Link (shipped with Mathematica) --
    implementation(files("/Applications/Mathematica.app/SystemFiles/Links/JLink/JLink.jar"))

    // -- Web Layer (Spring Boot minimal) --
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.0")
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.3.0")

    // -- Persistence --
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")        // switch to postgres later
    implementation("org.flywaydb:flyway-core")               // migrations
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

application {
    mainClass.set("com.example.notebook.Application")
}

tasks.test { useJUnitPlatform() }
```

---

## 3. Core Java Classes (abridged)

### 3.1 Kernel Layer (J/Link wrapper)

```java
// src/main/java/com/example/notebook/kernel/WolframKernel.java
package com.example.notebook.kernel;

import com.wolfram.jlink.*;

public class WolframKernel implements AutoCloseable {
    private final KernelLink link;

    public WolframKernel() throws MathLinkException {
        link = MathLinkFactory.createKernelLink("-linkmode launch -linkname 'wolframscript -wstp'");
        link.discardAnswer(); // discard initial packet
    }

    public String evaluate(String input) throws MathLinkException {
        return link.evaluateToOutputForm(input, 0);
    }

    @Override public void close() { link.close(); }
}
```

### 3.2 Domain Model

```java
// Notebook, Cell, EvaluationResult as immutable records
public record Notebook(UUID id, String title, Instant created, Instant updated) {}
public record Cell(UUID id, UUID notebookId, String input, Instant submittedAt) {}
public record EvaluationResult(UUID cellId, String output, boolean success,
                               String errorMessage, Duration evalTime) {}
```

### 3.3 Session Manager

```java
public class SessionManager {
    private final Map<UUID, WolframKernel> activeKernels = new ConcurrentHashMap<>();

    public WolframKernel getKernel(UUID notebookId) {
        return activeKernels.computeIfAbsent(notebookId, id -> {
            try { return new WolframKernel(); }
            catch (MathLinkException e) { throw new RuntimeException(e); }
        });
    }

    public void shutdown(UUID notebookId) {
        Optional.ofNullable(activeKernels.remove(notebookId)).ifPresent(AutoCloseable::close);
    }
}
```

### 3.4 History Service

```java
public class HistoryService {
    private final CellRepository cellRepo;
    private final ResultRepository resultRepo;
    private final SessionManager sessions;

    public EvaluationResult evaluate(UUID notebookId, String input) {
        Cell cell = cellRepo.insert(new Cell(UUID.randomUUID(), notebookId, input, Instant.now()));
        Instant start = Instant.now();
        try (WolframKernel kernel = sessions.getKernel(notebookId)) {
            String output = kernel.evaluate(input);
            EvaluationResult res = new EvaluationResult(
                    cell.id(), output, true, null, Duration.between(start, Instant.now()));
            resultRepo.insert(res);
            return res;
        } catch (Exception ex) {
            EvaluationResult res = new EvaluationResult(
                    cell.id(), null, false, ex.getMessage(), Duration.between(start, Instant.now()));
            resultRepo.insert(res);
            throw ex;
        }
    }
}
```

---

## 4. Persistence (Flyway + SQLite)

`src/main/resources/db/migration/V1__init.sql`

```sql
CREATE TABLE notebook (
  id         TEXT PRIMARY KEY,
  title      TEXT NOT NULL,
  created    TEXT,
  updated    TEXT
);
CREATE TABLE cell (
  id           TEXT PRIMARY KEY,
  notebook_id  TEXT NOT NULL,
  input        TEXT,
  submitted_at TEXT,
  FOREIGN KEY(notebook_id) REFERENCES notebook(id)
);
CREATE TABLE evaluation_result (
  cell_id      TEXT PRIMARY KEY,
  output       TEXT,
  success      INTEGER,
  error_msg    TEXT,
  eval_time_ms INTEGER,
  FOREIGN KEY(cell_id) REFERENCES cell(id)
);
```

---

## 5. Web Layer

### 5.1 REST (evaluate & get history)

```java
@RestController
@RequestMapping("/api")
public class NotebookController {
    private final HistoryService history;

    @PostMapping("/notebooks/{id}/cells")
    public EvaluationResult eval(@PathVariable UUID id, @RequestBody Map<String,String> body) {
        return history.evaluate(id, body.get("input"));
    }

    @GetMapping("/notebooks/{id}/history")
    public List<EvaluationResult> history(@PathVariable UUID id) {
        return history.getHistory(id);
    }
}
```

### 5.2 WebSocket (live updates)

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry reg) {
        reg.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry reg) {
        reg.enableSimpleBroker("/topic");
        reg.setApplicationDestinationPrefixes("/app");
    }
}
```

Publish results in `HistoryService.evaluate(...)`:

```java
simpMessagingTemplate.convertAndSend("/topic/notebook/" + notebookId, res);
```

---

## 6. Front-End Skeleton (React + Vite)

```bash
# run only if you want the web UI scaffold
npm create vite@latest ui-web -- --template react-ts
cd ui-web && npm install
```

Minimal cell component (`src/Cell.tsx`):

```tsx
export default function Cell({cell, sendEval}) {
  const [input, setInput] = useState("");
  return (
    <div className="cell">
      <textarea value={input} onChange={e=>setInput(e.target.value)} />
      <button onClick={()=>sendEval(input)}>Run</button>
      {cell.output && <pre>{cell.output}</pre>}
    </div>
  );
}
```

Open a WebSocket to `ws://localhost:8080/ws`, subscribe to `/topic/notebook/{id}`, and append new results.

---

## 7. Logging (JSON)

`src/main/resources/logback.xml`

```xml
<configuration>
  <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/app.log</file>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
  </appender>
  <root level="INFO">
    <appender-ref ref="FILE"/>
  </root>
</configuration>
```

---

## 8. Development Workflow

1. `./gradlew flywayMigrate` – initializes SQLite schema.  
2. `./gradlew bootRun` – starts Spring server (REST + WS).  
3. Native path: open any `.nb` file in Wolfram Desktop, but evaluations triggered via our Java service (we can inject results back using J/Link if you wish).  
4. Web path: `cd ui-web && npm run dev` – opens React notebook in <http://localhost:5173>.  
5. Observe JSON logs in `logs/app.log`.

---

## 9. What I Can Do For You Now

• Generate all the Gradle files, Java packages, and stub classes shown above.  
• Run Flyway migration so the DB is ready.  
• (Optionally) create the React scaffold under `ui-web/`.

**Command-ready summary**

1. Create project folders & `build.gradle.kts`.  
2. Add `settings.gradle.kts`, `Application.java`, sample classes.  
3. Run `gradle wrapper`, `flywayMigrate`, `bootRun`.  
4. Scaffold React UI.

---

### Let me know:

1. Shall I run the shell commands to scaffold the Java project in your repo now?  
2. Do you also want the React UI scaffold, or prefer to add it later?

(Just tell me “Yes, scaffold Java only”, “Yes, scaffold both”, or “Hold on”.)

## 9. What I Can Do For You Now

• Generate all the Gradle files, Java packages, and stub classes shown above.  
• Run Flyway migration so the DB is ready.  
• (Optionally) create the React scaffold under `ui-web/`.

**Command-ready summary**

1. Create project folders & `build.gradle.kts`.  
2. Add `settings.gradle.kts`, `Application.java`, sample classes.  
3. Run `gradle wrapper`, `flywayMigrate`, `bootRun`.  
4. Scaffold React UI.

---
