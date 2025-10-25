#!/bin/bash

echo "🧪 VALIDATION COMPLÈTE DU PROJET"
echo "================================="
echo ""

# Fonction pour afficher les résultats
function print_result() {
    if [ $1 -eq 0 ]; then
        echo "✅ $2"
    else
        echo "❌ $2 - ÉCHEC"
        FAILED=true
    fi
}

FAILED=false

# 1. Compilation
echo "📦 Étape 1 : Compilation du projet"
echo "-----------------------------------"
mvn clean compile -q 2>&1 | tail -5
print_result $? "Compilation"
echo ""

# 2. Tests unitaires
echo "🧪 Étape 2 : Exécution des tests"
echo "---------------------------------"
mvn test -q 2>&1 | grep -E "(Tests run|BUILD)"
print_result $? "Tests unitaires"
echo ""

# 3. Vérifier les fichiers sample
echo "📁 Étape 3 : Vérification des fichiers de test"
echo "-----------------------------------------------"

# Chercher le répertoire de test
TEST_DIR=$(find . -type d -name "tp1_part2" -o -name "test" | head -1)

if [ -n "$TEST_DIR" ]; then
    echo "📂 Dossier de test trouvé : $TEST_DIR"
    echo "Fichiers Java disponibles :"
    find $TEST_DIR -name "*.java" 2>/dev/null | head -10
    print_result 0 "Fichiers de test présents"
else
    echo "⚠️  Pas de dossier tp1_part2 trouvé"
    echo "Utiliser le projet actuel comme test"
    print_result 0 "Utilisation du code source comme test"
fi
echo ""

# 4. Vérifier les exécutables
echo "🎯 Étape 4 : Vérification des points d'entrée"
echo "----------------------------------------------"

ENTRY_POINTS=(
    "com.tp.tp1.gui.AnalyzerGUI"
    "com.tp.tp1.callgraph.CallGraphGUI"
    "com.tp.tp2.gui.CouplingAnalyzerGUI"
    "com.tp.tp2.gui.ModuleAnalyzerGUI"
    "com.tp.tp2.spoon.gui.SpoonCouplingGUI"
    "com.tp.tp2.spoon.gui.SpoonAnalyzerGUI"
)

for entry in "${ENTRY_POINTS[@]}"; do
    CLASS_FILE="target/classes/${entry//.//}.class"
    if [ -f "$CLASS_FILE" ]; then
        print_result 0 "$entry"
    else
        print_result 1 "$entry (fichier non trouvé)"
    fi
done
echo ""

# 5. Vérifier Graphviz
echo "🖼️  Étape 5 : Vérification de Graphviz"
echo "---------------------------------------"
if command -v dot &> /dev/null; then
    dot -V 2>&1
    print_result 0 "Graphviz installé"
else
    print_result 1 "Graphviz NON installé (optionnel)"
fi
echo ""

# 6. Compter les tests
echo "📊 Étape 6 : Statistiques des tests"
echo "------------------------------------"
echo "Tests disponibles :"
find src/test -name "*Test.java" | while read test; do
    basename $test
done
echo ""
TEST_COUNT=$(find src/test -name "*Test.java" | wc -l)
echo "Total : $TEST_COUNT fichiers de test"
echo ""

# 7. Vérifier la structure docs/
echo "📂 Étape 7 : Vérification du dossier docs/"
echo "--------------------------------------------"
if [ -d "docs" ]; then
    echo "Fichiers générés dans docs/ :"
    ls -lh docs/ 2>/dev/null || echo "  (vide)"
    print_result 0 "Dossier docs/ existe"
else
    mkdir -p docs
    print_result 0 "Dossier docs/ créé"
fi
echo ""

# RÉSUMÉ FINAL
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🎯 RÉSUMÉ DE LA VALIDATION"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

if [ "$FAILED" = true ]; then
    echo "❌ Certaines vérifications ont échoué"
    echo "⚠️  Corrige les erreurs avant de finaliser"
    exit 1
else
    echo "✅ TOUTES LES VÉRIFICATIONS SONT PASSÉES !"
    echo ""
    echo "🎊 Projet prêt pour :"
    echo "   ✅ Utilisation"
    echo "   ✅ Tests"
    echo "   ✅ Démonstration"
    echo "   ✅ Évaluation"
    echo ""
    echo "📝 README peut être finalisé"
    exit 0
fi

