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
cd tp-static-analysis

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
- 📊 **Métriques de qualité** : 
  - Nombre moyen de méthodes par classe
  - Nombre moyen de lignes par méthode
  - Nombre moyen d'attributs par classe
- 🏆 **Analyse de complexité** : identification des 10% de classes les plus complexes
- 📦 **Distribution** : analyse par package
- 🎨 **Interface moderne** : visualisation temps réel avec JavaFX

#### Comment l'utiliser ?

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp1.gui.AnalyzerGUI
```

**Étapes** : Parcourir → Sélectionner dossier → Analyser → Visualiser les métriques

#### Métriques calculées

| Catégorie | Métriques |
|-----------|-----------|
| **Volume** | Total classes, total méthodes, total lignes, total attributs |
| **Moyennes** | Méthodes/classe, Lignes/méthode, Attributs/classe |
| **Qualité** | Classes dans le top 10% (complexité) |
| **Distribution** | Répartition par package |

---

### 🌐 Exercice 2 : Graphe d'Appels

**Objectif** : Construire et visualiser les graphes d'invocations de méthodes

#### Fonctionnalités

- 🔍 **Construction complète** : analyse AST pour détecter tous les appels
- 📍 **Points d'entrée** : méthodes jamais appelées (API publique)
- 🍃 **Méthodes feuilles** : méthodes terminales sans appels sortants
- 🖼️ **Visualisation Graphviz** : export PNG haute qualité
- 📄 **Export DOT** : format standard pour traitements ultérieurs
- 🎨 **Viewer intégré** : visualisation dans l'interface JavaFX

#### Comment l'utiliser ?

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp1.callgraph.CallGraphGUI
```

**Étapes** : Parcourir → Analyser & Visualiser → Explorer le graphe

#### Exemple de résultats

```
=== STATISTIQUES DU GRAPHE D'APPELS ===
Nombre de méthodes    : 19 ← Nœuds du graphe
Nombre d'appels       : 13 ← Arêtes (invocations)

Points d'entrée       : 18 ← Méthodes non appelées
Méthodes feuilles     :  6 ← Opérations terminales
```

---

## 🔗 TP2 : Compréhension de Logiciels

### 🔗 Exercice 1 : Analyseur de Couplage (JDT)

**Objectif** : Analyser les relations de couplage entre classes avec Eclipse JDT

#### Fonctionnalités

- 🔗 **Graphe de couplage** : tous types de dépendances détectés
  - Variables de types
  - Paramètres de méthodes
  - Types de retour
  - Relations d'héritage
- 📊 **Métriques normalisées** : calcul de poids de couplage pondérés
- 🎯 **Classement** : top des relations les plus fortes
- 🖼️ **Visualisation** : graphe avec arêtes pondérées
- 📈 **Statistiques** : 38 relations détectées (projet exemple)

#### Comment l'utiliser ?

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.gui.CouplingAnalyzerGUI
```

**Étapes** : Parcourir → Analyser & Visualiser → Explorer le graphe de couplage

#### Exemple de couplages détectés

```
Top Couplages (poids normalisés) :
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
ComplexClass → PrintStream    : 0.3158 ⚠️  Fort
Application  → PrintStream    : 0.1842
Main         → PrintStream    : 0.1316
Main         → ComplexClass   : 0.1053
```

---

### 🌳 Exercice 2 : Identification de Modules (JDT)

**Objectif** : Détecter des modules logiques via clustering hiérarchique

#### Fonctionnalités

- 🌳 **Clustering hiérarchique** : algorithme agglomératif
- 📊 **Dendrogramme** : visualisation de la hiérarchie de clustering
- 🔍 **Seuil CP configurable** : Coupling Percentage ajustable
- 📦 **Modules automatiques** : attribution des classes aux modules
- 🖼️ **Export Graphviz** : dendrogramme au format PNG

#### Comment l'utiliser ?

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.gui.ModuleAnalyzerGUI
```

**Étapes** : Parcourir → Définir seuil CP → Analyser → Visualiser modules + dendrogramme

#### Influence du seuil CP

| Seuil CP | Granularité | Résultat |
|----------|-------------|----------|
| **0.3 - 0.5** | Grossière | 2-3 modules larges |
| **0.5 - 0.7** | Moyenne | 3-5 modules moyens |
| **0.7 - 0.9** | Fine | 5+ petits modules |

**Exemple** :
- CP = 0.5 → **2 modules** détectés (architecture macro)
- CP = 0.7 → **4 modules** détectés (séparation fine)

---

### 🥄 Exercice 3 : Analyse avec Spoon

**Objectif** : Analyse alternative utilisant le métamodèle Spoon

#### Analyseur de Couplage Spoon

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.spoon.gui.SpoonCouplingGUI
```

#### Analyseur de Modules Spoon

```bash
mvn exec:java -Dexec.mainClass=com.tp.tp2.spoon.gui.SpoonAnalyzerGUI
```

#### 🆚 Comparaison JDT vs Spoon

| Critère | Eclipse JDT | Spoon |
|---------|-------------|-------|
| **Relations détectées** | 38 | 35 |
| **Méthode** | Toutes dépendances | Invocations uniquement |
| **Type détection** | Structurelle | Comportementale |
| **Granularité** | Variables, params, retours, héritage | Appels de méthodes |
| **Avantages** | Vue complète des dépendances | Focus sur interactions réelles |

**Différences clés** :

✅ **JDT** détecte les **dépendances structurelles** (références de types)  
✅ **Spoon** détecte les **dépendances comportementales** (appels effectifs)  
✅ Les deux approches sont **complémentaires** et produisent des graphes valides

**Conclusion** : Les différences sont **normales et attendues** - elles reflètent deux perspectives d'analyse complémentaires !

---

## 🛠️ Installation et Configuration

### Prérequis

```bash
java -version  # Java 11+ requis
mvn -version   # Maven 3.6+ requis
dot -V         # Graphviz (pour visualisation)
```

### Installer Graphviz

**Ubuntu/Debian**
```bash
sudo apt install graphviz
```

**macOS**
```bash
brew install graphviz
```

**Windows (avec Chocolatey)**
```bash
choco install graphviz
```

**Vérification**
```bash
dot -V
# Sortie attendue : dot - graphviz version 2.43.0 (ou supérieur)
```

### Compiler le Projet

```bash
mvn clean install
```

**Résultat attendu** :
```
[INFO] BUILD SUCCESS
[INFO] Total time: 15.234 s
✅ Compilation réussie
✅ 30 tests exécutés avec succès
```

---

## 🧪 Tests

### Lancer tous les tests

```bash
mvn test
```

### Suites de tests individuelles

```bash
# Tests TP1
mvn test -Dtest=CallGraphBuilderTest

# Tests TP2 (JDT)
mvn test -Dtest=CouplingAnalyzerTest
mvn test -Dtest=ModuleIdentifierTest
mvn test -Dtest=HierarchicalClusteringTest

# Tests TP2 (Spoon)
mvn test -Dtest=SpoonCouplingAnalyzerTest
```

### Résultats des Tests

```
✅ CallGraphBuilderTest           : 5 tests RÉUSSIS
✅ CouplingAnalyzerTest            : 3 tests RÉUSSIS
✅ ModuleIdentifierTest            : 1 test RÉUSSI
✅ CallRelationTest                : 3 tests RÉUSSIS
✅ ClassCouplingTest               : 4 tests RÉUSSIS
✅ CouplingGraphTest               : 7 tests RÉUSSIS
✅ HierarchicalClusteringTest      : 1 test RÉUSSI
✅ CouplingGraphExporterTest       : 3 tests RÉUSSIS
✅ SpoonCouplingAnalyzerTest       : 3 tests RÉUSSIS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🎉 TOTAL : 30 TESTS RÉUSSIS !
```

---

## 🏗️ Structure du Projet

```
tp-static-analysis/
│
├── src/main/java/com/tp/
│   │
│   ├── tp1/                          ← TP1 : Analyse Statique
│   │   ├── analyzer/                 ← Moteur de calcul de métriques
│   │   │   ├── CodeAnalyzer.java
│   │   │   ├── ClassInfo.java
│   │   │   └── MethodInfo.java
│   │   │
│   │   ├── callgraph/                ← Constructeur de graphe d'appels
│   │   │   ├── CallGraphBuilder.java
│   │   │   ├── CallGraphNode.java
│   │   │   └── CallGraphGUI.java
│   │   │
│   │   └── gui/                      ← Interfaces JavaFX
│   │       └── AnalyzerGUI.java
│   │
│   └── tp2/                          ← TP2 : Compréhension de Logiciels
│       │
│       ├── analyzer/                 ← Analyseur de couplage JDT
│       │   └── CouplingAnalyzer.java
│       │
│       ├── clustering/               ← Clustering hiérarchique
│       │   ├── HierarchicalClustering.java
│       │   └── DendrogramNode.java
│       │
│       ├── model/                    ← Modèle de données
│       │   ├── CouplingGraph.java
│       │   ├── ClassCoupling.java
│       │   └── CallRelation.java
│       │
│       ├── modules/                  ← Détection de modules
│       │   ├── ModuleIdentifier.java
│       │   └── Module.java
│       │
│       ├── graph/                    ← Export graphe (DOT)
│       │   └── CouplingGraphExporter.java
│       │
│       ├── visualization/            ← Export dendrogramme
│       │   └── DendrogramExporter.java
│       │
│       ├── gui/                      ← Outils GUI JDT
│       │   ├── CouplingAnalyzerGUI.java
│       │   └── ModuleAnalyzerGUI.java
│       │
│       └── spoon/                    ← Analyse basée Spoon
│           ├── analyzer/
│           │   └── SpoonCouplingAnalyzer.java
│           └── gui/
│               ├── SpoonCouplingGUI.java
│               └── SpoonAnalyzerGUI.java
│
├── src/test/java/com/tp/             ← Suites de tests JUnit 5
│
├── docs/                             ← Graphes générés
├── pom.xml                           ← Configuration Maven
└── README.md                         ← Ce fichier
```

---

## 🛠️ Technologies Utilisées

| Technologie | Version | Usage |
|-------------|---------|-------|
| **Java** | 11+ | Langage de programmation principal |
| **JavaFX** | 17.0.2 | Framework GUI moderne et réactif |
| **Eclipse JDT** | 3.32.0 | Parsing AST et analyse de code |
| **Spoon** | 10.4.0 | Analyse par métamodèle |
| **JUnit 5** | 5.10.0 | Framework de tests unitaires |
| **Graphviz** | 2.43.0 | Visualisation et export de graphes |
| **Maven** | 3.6+ | Gestion de build et dépendances |

---

## 📚 Concepts Clés

### 🔢 Métriques de Couplage

**Formule du poids de couplage normalisé** entre classes A et B :

```
Couplage(A,B) = Relations(A,B) / RelationsTotales
```

**Exemple** :
- ComplexClass → PrintStream : 12 relations / 38 total = **0.3158**
- Application → PrintStream : 7 relations / 38 total = **0.1842**

### 🌳 Clustering Hiérarchique

**Algorithme agglomératif** :

1. **Initialisation** : Chaque classe = 1 cluster distinct
2. **Itération** : Fusion des 2 clusters les plus proches (couplage maximal)
3. **Arrêt** : Atteinte du seuil CP (Coupling Percentage) désiré

**Distance entre clusters** : Inverse du couplage moyen

### 📦 Identification de Modules

**Extraction selon le seuil CP** :

- **CP bas (0.3 - 0.5)** :
  - Peu de modules
  - Modules larges et généraux
  - Vue architecturale macro
  
- **CP moyen (0.5 - 0.7)** :
  - Nombre équilibré de modules
  - Granularité intermédiaire
  
- **CP élevé (0.7 - 0.9)** :
  - Nombreux petits modules
  - Granularité fine
  - Séparation maximale des responsabilités

---

## 🎨 Interfaces Graphiques

Tous les outils disposent d'**interfaces JavaFX modernes** avec :

- 📁 **Navigateur de projets** : sélection intuitive de dossiers
- 📊 **Statistiques temps réel** : affichage dynamique des métriques
- 🖼️ **Visualisation intégrée** : graphes directement dans l'interface
- 💾 **Export multi-format** : DOT, PNG, CSV
- 🎨 **Design moderne** : interface claire et ergonomique
- ⚡ **Performances** : traitement rapide des projets Java

---

## 👨‍💻 Auteur

**Mohamed Nadir BELLIL**  
🎓 Master 2 Génie Logiciel  
🏛️ Université de Montpellier  
📧 [GitHub](https://github.com/BELLILMohamedNadir)

---

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/amelioration`)
3. Commit les changements (`git commit -m 'Ajout fonctionnalité X'`)
4. Push vers la branche (`git push origin feature/amelioration`)
5. Ouvrir une Pull Request

---

## 📜 Licence

Projet académique développé dans le cadre du cours **HAI913I - Compréhension et Restructuration de Logiciels**.

---

## 📖 Ressources Complémentaires

- [Documentation Eclipse JDT](https://www.eclipse.org/jdt/)
- [Documentation Spoon](https://spoon.gforge.inria.fr/)
- [Guide JavaFX](https://openjfx.io/)
- [Langage DOT Graphviz](https://graphviz.org/doc/info/lang.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

---


<p align="center">
  <strong>Développé avec ☕ pour HAI913I - Compréhension et Restructuration de Logiciels</strong><br>
  <sub>Université de Montpellier - 2024</sub>
</p>
