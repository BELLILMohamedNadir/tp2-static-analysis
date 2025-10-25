# 📊 TP1 & TP2 - Advanced Static Analysis & Software Comprehension

> Comprehensive Java static analysis toolkit with Eclipse JDT & Spoon frameworks + Modern JavaFX interfaces

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![JDT](https://img.shields.io/badge/Eclipse_JDT-3.32-purple.svg)](https://www.eclipse.org/jdt/)
[![Spoon](https://img.shields.io/badge/Spoon-10.4-green.svg)](https://spoon.gforge.inria.fr/)
[![Tests](https://img.shields.io/badge/tests-30%20passed-brightgreen.svg)](https://junit.org/junit5/)

Complete static analysis suite combining **metrics calculation**, **call graph analysis**, **coupling detection**, **hierarchical clustering**, and **modularization** — developed for **HAI913I - Software Comprehension and Restructuring** course.

---

## 🚀 Quick Start

```bash
# Clone & Build
git clone https://github.com/BELLILMohamedNadir/tp-static-analysis.git
cd tp-static-analysis
mvn clean install

# Run TP1 Tools
mvn exec:java -Dexec.mainClass=com.tp.tp1.gui.AnalyzerGUI          # Code Metrics
mvn exec:java -Dexec.mainClass=com.tp.tp1.callgraph.CallGraphGUI   # Call Graph

# Run TP2 Tools (JDT)
mvn exec:java -Dexec.mainClass=com.tp.tp2.gui.CouplingAnalyzerGUI  # Coupling Graph
mvn exec:java -Dexec.mainClass=com.tp.tp2.gui.ModuleAnalyzerGUI    # Module Detection

# Run TP2 Tools (Spoon)
mvn exec:java -Dexec.mainClass=com.tp.tp2.spoon.gui.SpoonCouplingGUI  # Spoon Coupling
mvn exec:java -Dexec.mainClass=com.tp.tp2.spoon.gui.SpoonAnalyzerGUI  # Spoon Modules
```

---

## 📝 TP1: Static Code Analysis

### Exercise 1: Code Metrics Analyzer
**Computes 13 comprehensive software metrics from Java source code**

#### Features
- 📊 Project statistics: classes, methods, lines, attributes
- 📈 Quality metrics: methods/class, lines/method, attributes/class
- 🏆 Top 10% complex classes detection
- 📦 Package-level distribution analysis
- 🎨 Modern JavaFX interface with real-time visualization

#### Usage
```bash
mvn exec:java -Dexec.mainClass=com.tp.tp1.gui.AnalyzerGUI
# Browse → Select folder → Analyze → View metrics
```

#### Metrics Computed
| Category | Metrics |
|----------|---------|
| **Size** | Total classes, methods, LOC, attributes |
| **Averages** | Methods/class, LOC/method, Attributes/class |
| **Complexity** | Top 10% complex classes by method count |

---

### Exercise 2: Call Graph Builder
**Builds and visualizes method invocation graphs**

#### Features
- 🌐 Complete call graph construction (AST-based)
- 📊 Entry points detection (never called methods)
- 🍃 Leaf methods identification (terminal operations)
- 🖼️ Graphviz visualization with PNG export
- 📄 DOT format export for further analysis
- 🎨 Integrated JavaFX viewer

#### Usage
```bash
mvn exec:java -Dexec.mainClass=com.tp.tp1.callgraph.CallGraphGUI
# Browse → Analyze & Visualize → View graph in UI
```

#### Graph Statistics Example
```
=== CALL GRAPH STATISTICS ===
Total Methods  : 19    ← Nodes in graph
Total Calls    : 13    ← Edges (method invocations)

Entry Points   : 18    ← External API/unused methods
Leaf Methods   : 6     ← Terminal operations
```

---

## 🔗 TP2: Software Comprehension

### Exercise 1: Coupling Graph Analyzer (JDT)
**Analyzes class coupling relationships with Eclipse JDT**

#### Features
- 🔗 Coupling graph construction (all dependency types)
- 📊 Coupling metrics calculation (normalized weights)
- 🎯 Top coupling relationships ranking
- 🖼️ Visual graph with weighted edges
- 📈 38 relations detected from sample project

#### Usage
```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.gui.CouplingAnalyzerGUI
# Browse → Analyze & Visualize → View coupling graph
```

#### Coupling Metrics
```
ComplexClass → PrintStream  (0.3158)  ← Strong coupling
Application  → PrintStream  (0.1842)  
Main         → PrintStream  (0.1316)
Main         → ComplexClass (0.1053)
```

---

### Exercise 2: Module Identification (JDT)
**Hierarchical clustering for module detection**

#### Features
- 🌳 Hierarchical clustering (agglomerative)
- 📊 Dendrogram generation and visualization
- 🔍 Module extraction at configurable coupling percentage (CP)
- 📦 Automatic module membership assignment
- 🖼️ Graphviz dendrogram rendering

#### Usage
```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.gui.ModuleAnalyzerGUI
# Browse → Set CP threshold → Analyze → View modules + dendrogram
```

#### Module Detection
- **CP = 0.5** → 2 modules (coarse-grained)
- **CP = 0.7** → 4 modules (fine-grained)
- **Dendrogram** shows hierarchical structure

---

### Exercise 3: Spoon-Based Analysis
**Alternative analysis using Spoon metamodel**

#### Spoon Coupling Analyzer
```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.spoon.gui.SpoonCouplingGUI
# Browse → Analyze & Visualize → Compare with JDT results
```

#### Spoon Module Analyzer
```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.spoon.gui.SpoonAnalyzerGUI
# Browse → Set CP → Analyze → View modules
```

#### JDT vs Spoon Comparison
| Framework | Relations | Detection Method | Metrics |
|-----------|-----------|------------------|---------|
| **Eclipse JDT** | 38 | All dependencies (variables, params, returns, inheritance) | ✅ Full |
| **Spoon** | 35 | Method invocations only | ✅ Focused |

**Key Differences:**
- JDT detects **structural dependencies** (type references)
- Spoon detects **behavioral dependencies** (method calls)
- Both produce valid coupling graphs with different granularity ✅

---

## 🛠️ Installation

### Prerequisites
```bash
java -version    # Java 11+ required
mvn -version     # Maven 3.6+ required
dot -V           # Graphviz (for graph visualization)
```

### Install Graphviz
```bash
# Ubuntu/Debian
sudo apt install graphviz

# macOS
brew install graphviz

# Windows
choco install graphviz
```

### Build Project
```bash
mvn clean install
# ✅ Compiles all modules + runs tests
```

---

## 🧪 Testing

```bash
# Run all tests
mvn test

# Individual test suites
mvn test -Dtest=CallGraphBuilderTest
mvn test -Dtest=CouplingAnalyzerTest
mvn test -Dtest=SpoonCouplingAnalyzerTest
```

**Test Results:**
```
✅ CallGraphBuilderTest         : 5 tests PASSED
✅ CouplingAnalyzerTest          : 3 tests PASSED
✅ ModuleIdentifierTest          : 1 test PASSED
✅ CallRelationTest              : 3 tests PASSED
✅ ClassCouplingTest             : 4 tests PASSED
✅ CouplingGraphTest             : 7 tests PASSED
✅ HierarchicalClusteringTest    : 1 test PASSED
✅ CouplingGraphExporterTest     : 3 tests PASSED
✅ SpoonCouplingAnalyzerTest     : 3 tests PASSED
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🎉 ALL 30 TESTS PASSED!
```

---

## 🏗️ Project Structure

```
tp-static-analysis/
├── src/main/java/com/tp/
│   ├── tp1/                        # TP1: Static Analysis
│   │   ├── analyzer/               # Code metrics engine
│   │   │   ├── CodeAnalyzer.java
│   │   │   ├── ClassInfo.java
│   │   │   └── MethodInfo.java
│   │   ├── callgraph/              # Call graph builder
│   │   │   ├── CallGraphBuilder.java
│   │   │   ├── CallGraphNode.java
│   │   │   └── CallGraphGUI.java
│   │   └── gui/                    # JavaFX interfaces
│   │       └── AnalyzerGUI.java
│   │
│   └── tp2/                        # TP2: Software Comprehension
│       ├── analyzer/               # JDT coupling analyzer
│       │   └── CouplingAnalyzer.java
│       ├── clustering/             # Hierarchical clustering
│       │   ├── HierarchicalClustering.java
│       │   └── DendrogramNode.java
│       ├── model/                  # Data model
│       │   ├── CouplingGraph.java
│       │   ├── ClassCoupling.java
│       │   └── CallRelation.java
│       ├── modules/                # Module detection
│       │   ├── ModuleIdentifier.java
│       │   └── Module.java
│       ├── graph/                  # Graph export (DOT)
│       │   └── CouplingGraphExporter.java
│       ├── visualization/          # Dendrogram export
│       │   └── DendrogramExporter.java
│       ├── gui/                    # JDT GUI tools
│       │   ├── CouplingAnalyzerGUI.java
│       │   └── ModuleAnalyzerGUI.java
│       └── spoon/                  # Spoon-based analysis
│           ├── analyzer/
│           │   └── SpoonCouplingAnalyzer.java
│           └── gui/
│               ├── SpoonCouplingGUI.java
│               └── SpoonAnalyzerGUI.java
│
├── src/test/java/com/tp/           # JUnit 5 test suites
├── docs/                           # Generated graphs
│   ├── coupling-graph.dot
│   └── coupling-graph.png
├── pom.xml                         # Maven configuration
└── README.md                       # This file
```

---

## 🛠️ Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 11+ | Core language |
| **JavaFX** | 17.0.2 | Modern GUI framework |
| **Eclipse JDT** | 3.32.0 | AST parsing & analysis |
| **Spoon** | 10.4.0 | Metamodel-based analysis |
| **JUnit 5** | 5.10.0 | Unit testing |
| **Graphviz** | 2.43.0 | Graph visualization |
| **Maven** | 3.6+ | Build automation |

---

## 📚 Key Concepts

### Coupling Metrics
**Normalized coupling weight** between classes A and B:
```
Coupling(A,B) = Relations(A,B) / TotalRelations
```

### Hierarchical Clustering
Agglomerative clustering process:
1. Start: Each class = 1 cluster
2. Merge closest clusters iteratively
3. Stop at desired CP (Coupling Percentage) threshold

### Module Identification
Extract modules at **CP threshold**:
- **Low CP (0.3-0.5)** → Few large modules
- **High CP (0.7-0.9)** → Many small modules

---

## 📊 Sample Analysis Results

### Project: `tp1_part2.samples` (6 classes)

#### JDT Analysis
```
Classes: 6  |  Relations: 38
Top Coupling: ComplexClass ↔ PrintStream (0.316)
Modules (CP=0.5): 2 detected
```

#### Spoon Analysis
```
Classes: 6  |  Relations: 35
Top Coupling: ComplexClass ↔ PrintStream (0.343)
Modules (CP=0.5): 2 detected
```

**Differences are EXPECTED** — demonstrates framework comparison! ✅

---

## 🎨 GUI Features

All tools feature modern JavaFX interfaces with:
- 📁 Project browser
- 📊 Real-time statistics
- 🖼️ Embedded graph visualization
- 💾 DOT/PNG export capabilities

---

## 🚀 Advanced Usage

### Manual Graph Generation
```bash
# Generate coupling graph DOT file
mvn exec:java -Dexec.mainClass=com.tp.tp2.Main \
  -Dexec.args="/path/to/project"

# Render with Graphviz
dot -Tpng docs/coupling-graph.dot -o docs/coupling-graph.png
xdg-open docs/coupling-graph.png
```

### Customize Clustering Threshold
Modify `CP` parameter in GUI or programmatically:
```java
ModuleIdentifier identifier = new ModuleIdentifier();
List<Module> modules = identifier.identifyModules(graph, 0.7); // CP=70%
```

---

## 👨‍💻 Author

**Mohamed Nadir BELLIL**  
🎓 Master 2 Software Engineering  
🏛️ University of Montpellier  
📧 [GitHub](https://github.com/BELLILMohamedNadir)

---

## 🤝 Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a Pull Request

---

## 📜 License

Academic project for HAI913I course.

---

**⭐ Star this repo if you find it useful!**

<p align="center">
  <strong>Made with ☕ for HAI913I - Software Comprehension & Restructuring</strong>
</p>

---

## 🔧 Troubleshooting

### JavaFX not found
```bash
# Add JavaFX to classpath
export PATH_TO_FX=/path/to/javafx-sdk/lib
mvn javafx:run
```

### Graphviz not installed
```bash
# Check installation
dot -V

# If missing, install (see Installation section above)
```

### Tests failing
```bash
# Clean rebuild
mvn clean install
```

---

## 📖 Additional Resources

- [Eclipse JDT Documentation](https://www.eclipse.org/jdt/)
- [Spoon Framework](https://spoon.gforge.inria.fr/)
- [JavaFX Documentation](https://openjfx.io/)
- [Graphviz DOT Language](https://graphviz.org/doc/info/lang.html)

---

**End of README** 🎯
