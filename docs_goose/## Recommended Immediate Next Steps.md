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

(If yes, I’ll run the necessary shell commands and push starter files into your repo.)