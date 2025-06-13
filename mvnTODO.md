
# 📝 Comprehensive Maven README – `wolfram-calculus-notebooks`

> A reference README template for a pure-Maven project.  
> Replace bracketed text `[like this]` with project-specific details as needed.

---

## 1 Project Overview
`wolfram-calculus-notebooks` is a Java/JVM library & CLI that … [1–2 sentences summarising purpose].  
It ships as a self-contained **JAR** (packaging =`jar`) and can be consumed as a dependency or executed directly.

| Group ID | Artifact ID | Version | Packaging |
|----------|-------------|---------|-----------|
| `com.example.wolframnotebooks` | `wolfram-calculus-notebooks` | `1.0-SNAPSHOT` | `jar` |

---

## 2 Repository Layout

```
.
├── src/
│   ├── main/java/…     – production code
│   └── test/java/…     – unit / integration tests
├── pom.xml             – Maven build definition
├── docs/ or docs_goose – supplementary docs
├── app/ / cli/         – optional CLI entry points
└── README.md           – this file
```

> Tip: Use `mvn -X` for verbose build logs.

---

## 3 Prerequisites

| Tool | Minimum Version | Notes |
|------|-----------------|-------|
| Java | 17 (LTS recommended) | Set `JAVA_HOME` |
| Maven| 3.9.x           | The project bundles a Maven Wrapper (`mvnw`) for convenience |
| Git  | latest stable  | Source control |

---

## 4 Quick-start

```bash
# Clone & enter
git clone https://github.com/[your-org]/wolfram-calculus-notebooks.git
cd wolfram-calculus-notebooks

# Build everything, run unit tests, and create target/…jar
./mvnw clean verify

# Execute the CLI (if present)
java -jar target/wolfram-calculus-notebooks-1.0-SNAPSHOT.jar --help
```

Common lifecycle phases:

| Task                               | Command                              |
|------------------------------------|--------------------------------------|
| **Clean** compiled artifacts       | `mvn clean`                          |
| Compile sources                    | `mvn compile`                        |
| Run unit tests                     | `mvn test`                           |
| Package JAR                        | `mvn package`                        |
| Install JAR to local repo          | `mvn install`                        |
| Deploy to remote repo (CI/CD)      | `mvn deploy`                         |
| Generate project site & javadoc    | `mvn site`                           |

> ⚠️ `mvn cleab` (typo) will trigger `LifecyclePhaseNotFoundException`. Use `mvn clean`.

---

## 5 Key Maven Coordinates

```xml
<dependency>
  <groupId>com.example.wolframnotebooks</groupId>
  <artifactId>wolfram-calculus-notebooks</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

To consume *snapshot* builds, add the snapshot repository to your own `pom.xml` or `settings.xml`.

---

## 6 Profiles & Build Options

| Profile ID | Purpose                               | Activation |
|------------|---------------------------------------|------------|
| `release`  | GPG signing, attach sources/javadoc   | `-Prelease`|
| `ci`       | Skip UI tests, enable jacoco/coverage | CI runner  |

Enable with `mvn -P[profile] …`.

---

## 7 Testing

* **Unit tests**: JUnit 5 in `src/test/java`.
* **Coverage**: Jacoco (`mvn verify` writes `target/site/jacoco`).
* **Integration tests**: put in `src/it/` and run with `mvn verify -Pit`.

---

## 8 Code Quality

* Static analysis: SpotBugs & Checkstyle bound to `verify`.
* Formatting: `mvn fmt:format` (google-java-format).
* Lint: `mvn checkstyle:check`.

---

## 9 IDE Import

1. IntelliJ IDEA → *Open* → select root `pom.xml`.
2. Eclipse / STS → *Import → Existing Maven Project*.
3. VS Code → install *Java Extension Pack* → `File > Open Folder`.

The Maven wrapper (`./mvnw`) lets the IDE fetch the exact Maven version.

---

## 10 Continuous Integration / Deployment

| Stage            | Command                                 |
|------------------|-----------------------------------------|
| Build & test     | `./mvnw --batch-mode clean verify`      |
| Package & sign   | `./mvnw --batch-mode -Prelease deploy`  |

Example GitHub Actions snippet:

```yaml
- uses: actions/setup-java@v4
  with:
    distribution: 'temurin'
    java-version: '17'
    cache: 'maven'

- run: ./mvnw --batch-mode clean verify
```

---

## 11 Releasing

1. Update version in `pom.xml` (`mvn versions:set`).
2. `git tag -s vX.Y.Z -m "Release X.Y.Z"`
3. `mvn clean deploy -Prelease`
4. Push tags & artifacts.

---

## 12 Troubleshooting

| Symptom | Fix |
|---------|-----|
| `Unknown lifecycle phase "cleab"` | Typo; use `clean`. |
| SSL repo errors | Ensure Java CA-certs are current or add `-Dmaven.wagon.http.ssl.insecure=true` (not for prod). |
| Out-of-memory during tests | `MAVEN_OPTS="-Xmx2G" mvn test` |

---

## 13 Contributing

1. Fork & create feature branch.
2. Follow code style (`mvn fmt:format`).
3. Ensure `mvn verify` passes.
4. Open PR; describe motivation & usage.

---

## 14 License

Distributed under the **[MIT]** license. See `LICENSE` for full text.

---

## 15 References

* Maven documentation: <https://maven.apache.org/guides/>
* FAQ on lifecycles/phases: <https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html>

---

