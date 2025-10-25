# 📊 TP1 & TP2 - Analyse Statique de Code Java

> Suite complète d'outils d'analyse statique avec Eclipse JDT et Spoon + Interfaces JavaFX modernes

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![JDT](https://img.shields.io/badge/Eclipse_JDT-3.32-purple.svg)](https://www.eclipse.org/jdt/)
[![Spoon](https://img.shields.io/badge/Spoon-10.4-green.svg)](https://spoon.gforge.inria.fr/)
[![Tests](https://img.shields.io/badge/tests-30%20réussis-brightgreen.svg)](https://junit.org/junit5/)

**Suite d'analyse statique** combinant calcul de métriques, graphes d'appels, détection de couplage, clustering hiérarchique et modularisation.

📚 **Développé pour** : HAI913I - Compréhension et Restructuration de Logiciels  
🎓 **Formation** : Master 2 Génie Logiciel - Université de Montpellier

---

## 🚀 Démarrage Rapide

### 1️⃣ Installation

```bash
# Cloner le dépôt
git clone https://github.com/BELLILMohamedNadir/tp2-static-analysis.git
cd tp2-static-analysis

# Compiler le projet
mvn clean install
```

### 2️⃣ Vérifier les prérequis

```bash
java -version  # Java 11+ requis
mvn -version   # Maven 3.6+ requis
dot -V         # Graphviz (pour visualisation)
```

### 3️⃣ Lancer les outils

**TP1 - Analyse Statique**

```bash
# Analyseur de métriques
mvn exec:java -Dexec.mainClass=com.tp.tp1.gui.AnalyzerGUI

# Graphe d'appels
mvn exec:java -Dexec.mainClass=com.tp.tp1.callgraph.CallGraphGUI
```

**TP2 - Compréhension (JDT)**

```bash
# Graphe de couplage
mvn exec:java -Dexec.mainClass=com.tp.tp2.gui.CouplingAnalyzerGUI

# Détection de modules
mvn exec:java -Dexec.mainClass=com.tp.tp2.gui.ModuleAnalyzerGUI
```

**TP2 - Compréhension (Spoon)**

```bash
# Couplage Spoon
mvn exec:java -Dexec.mainClass=com.tp.tp2.spoon.gui.SpoonCouplingGUI

# Modules Spoon
mvn exec:java -Dexec.mainClass=com.tp.tp2.spoon.gui.SpoonAnalyzerGUI
```

---

## 📝 TP1 : Analyse Statique de Code

### 📊 Exercice 1 : Analyseur de Métriques

**Objectif** : Calculer 13 métriques logicielles à partir du code source Java

#### Fonctionnalités

- 📈 **Statistiques globales** : nombre de classes, méthodes, lignes de code, attributs
- 📊 **Métriques de qualité** : moyennes par classe/méthode
- 🏆 **Analyse de complexité** : identification des 10% de classes les plus complexes
- 📦 **Distribution** : analyse par package
- 🎨 **Interface moderne** : visualisation temps réel avec JavaFX

#### Utilisation

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp1.gui.AnalyzerGUI
```

---

### 🌐 Exercice 2 : Graphe d'Appels

**Objectif** : Construire et visualiser les graphes d'invocations de méthodes

#### Fonctionnalités

- 🔍 **Construction complète** : analyse AST pour détecter tous les appels
- 📍 **Points d'entrée** : méthodes jamais appelées
- 🍃 **Méthodes feuilles** : méthodes terminales
- 🖼️ **Visualisation Graphviz** : export PNG haute qualité

#### Utilisation

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp1.callgraph.CallGraphGUI
```

---

## 🔗 TP2 : Compréhension de Logiciels

### Exercice 1 : Analyseur de Couplage (JDT)

**Objectif** : Analyser les relations de couplage entre classes avec Eclipse JDT

#### Fonctionnalités

- 🔗 **Graphe de couplage** : toutes dépendances (variables, params, retours, héritage)
- 📊 **Métriques normalisées** : poids de couplage pondérés
- 🎯 **Classement** : top des relations les plus fortes
- 📈 **38 relations** détectées sur projet exemple

#### Utilisation

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.gui.CouplingAnalyzerGUI
```

---

### Exercice 2 : Identification de Modules (JDT)

**Objectif** : Détecter des modules logiques via clustering hiérarchique

#### Fonctionnalités

- 🌳 **Clustering hiérarchique** : algorithme agglomératif
- 📊 **Dendrogramme** : visualisation de la hiérarchie
- 🔍 **Seuil CP configurable** : Coupling Percentage ajustable
- 📦 **Modules automatiques** : attribution aux modules

#### Utilisation

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.gui.ModuleAnalyzerGUI
```

---

### 🥄 Exercice 3 : Analyse avec Spoon

**Objectif** : Analyse alternative utilisant le métamodèle Spoon

#### 3.1 - Analyseur de Couplage Spoon

**Analyse comportementale basée sur les invocations de méthodes**

##### Fonctionnalités

- 🔗 Détection des appels de méthodes uniquement
- 📊 Graphe de couplage Spoon
- 📈 **35 relations** détectées (vs 38 pour JDT)
- 📁 Export `docs/coupling-spoon.png`

##### Utilisation

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.spoon.gui.SpoonCouplingGUI
```

---

#### 3.2 - Analyseur de Modules Spoon

**Identification de modules via clustering avec données Spoon**

##### Fonctionnalités

- 🌳 Clustering basé sur invocations Spoon
- 📊 Dendrogramme Spoon
- 🔍 Seuil CP ajustable
- 📁 Export `docs/spoon-dendrogram.png`

##### Utilisation

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.spoon.gui.SpoonAnalyzerGUI
```

---

#### 🆚 Comparaison JDT vs Spoon

| Critère | Eclipse JDT | Spoon |
|---------|-------------|-------|
| **Relations détectées** | 38 | 35 |
| **Méthode** | Toutes dépendances | Invocations uniquement |
| **Type détection** | Structurelle | Comportementale |
| **Avantages** | Vue exhaustive | Focus sur exécution réelle |

**Différences clés** :

✅ **JDT** : analyse **structurelle** (toutes références de types)  
✅ **Spoon** : analyse **comportementale** (appels effectifs de méthodes)  
✅ Les deux approches sont **complémentaires** et valides

---

## 🛠️ Installation

### Prérequis

- Java 11+
- Maven 3.6+
- Graphviz (pour visualisation)

### Installer Graphviz

**Ubuntu/Debian**

```bash
sudo apt install graphviz
```

**macOS**

```bash
brew install graphviz
```

**Windows**

```bash
choco install graphviz
```

### Compiler

```bash
mvn clean install
```

---

## 🧪 Tests

```bash
# Tous les tests
mvn test

# Tests spécifiques
mvn test -Dtest=CouplingAnalyzerTest
mvn test -Dtest=SpoonCouplingAnalyzerTest
```

**Résultats** : ✅ 30 tests réussis

---

## 🏗️ Structure du Projet

```
tp-static-analysis/
├── src/main/java/com/tp/
│   ├── tp1/                    ← TP1 : Analyse Statique
│   │   ├── analyzer/           ← Métriques
│   │   ├── callgraph/          ← Graphe d'appels
│   │   └── gui/                ← Interfaces JavaFX
│   │
│   └── tp2/                    ← TP2 : Compréhension
│       ├── analyzer/           ← Couplage JDT
│       ├── clustering/         ← Clustering hiérarchique
│       ├── model/              ← Modèles de données
│       ├── modules/            ← Détection modules
│       ├── graph/              ← Export DOT
│       ├── visualization/      ← Dendrogrammes
│       ├── gui/                ← GUI JDT
│       └── spoon/              ← Analyse Spoon
│           ├── analyzer/
│           └── gui/
│
├── src/test/java/              ← Tests JUnit 5
├── docs/                       ← Graphes générés
└── README.md
```

---

## 🛠️ Technologies

| Technologie | Version | Usage |
|-------------|---------|-------|
| **Java** | 11+ | Langage principal |
| **JavaFX** | 17.0.2 | Framework GUI |
| **Eclipse JDT** | 3.32.0 | Parsing AST |
| **Spoon** | 10.4.0 | Métamodèle Java |
| **JUnit 5** | 5.10.0 | Tests unitaires |
| **Graphviz** | 2.43.0 | Visualisation |
| **Maven** | 3.6+ | Build |

---

## 👨‍💻 Auteur

**Mohamed Nadir BELLIL**  
🎓 Master 2 Génie Logiciel  
🏛️ Université de Montpellier  
📧 [GitHub](https://github.com/BELLILMohamedNadir)

---

## 📜 Licence

Projet académique - HAI913I - Compréhension et Restructuration de Logiciels

---

<p align="center">
  <strong>Développé avec ☕ pour HAI913I</strong><br>
  <sub>Université de Montpellier - 2025</sub>
</p>
