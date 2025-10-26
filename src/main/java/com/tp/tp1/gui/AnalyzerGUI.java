package com.tp.tp1.gui;

import com.tp.tp1.analyzer.CodeAnalyzer;
import com.tp.tp1.analyzer.ClassInfo;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;
import java.util.Map;

public class AnalyzerGUI extends Application {
    
    private CodeAnalyzer analyzer;
    private TextArea resultsArea;
    private TextField pathField;
    private Spinner<Integer> thresholdSpinner;
    private Button analyzeBtn;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Analyseur Java - TP1 Exercice 1");
        analyzer = new CodeAnalyzer();
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f1f5f9;");
        
        root.setTop(createHeader());
        root.setCenter(createMainContent(primaryStage));
        root.setBottom(createFooter());
        
        Scene scene = new Scene(root, 1100, 680);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createHeader() {
        VBox header = new VBox(5);
        header.setPadding(new Insets(18, 25, 18, 25));
        header.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-width: 0 0 1px 0;");
        
        Label title = new Label("Analyseur Statique Java");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 600; -fx-text-fill: #000000;");
        
        Label subtitle = new Label("HAI913I - TP1 Exercice 1");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    private HBox createMainContent(Stage stage) {
        HBox mainContent = new HBox(18);
        mainContent.setPadding(new Insets(18));
        
        VBox leftPanel = createControlPanel(stage);
        leftPanel.setPrefWidth(320);
        leftPanel.setMinWidth(300);
        
        VBox rightPanel = createResultsPanel();
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        
        mainContent.getChildren().addAll(leftPanel, rightPanel);
        return mainContent;
    }
    
    private VBox createControlPanel(Stage stage) {
        VBox controlPanel = new VBox(12);
        
        // Configuration Card
        VBox configCard = new VBox(10);
        configCard.setPadding(new Insets(14));
        configCard.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 1);");
        
        Label configTitle = new Label("Configuration");
        configTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");
        
        Label pathLabel = new Label("Chemin du Code Source");
        pathLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: #0f172a;");
        
        pathField = new TextField();
        pathField.setPromptText("Sélectionner un répertoire...");
        pathField.setStyle("-fx-padding: 8px 12px; -fx-font-size: 12px; -fx-background-color: #f1f5f9; -fx-background-radius: 6px; -fx-border-color: #cbd5e1; -fx-border-radius: 6px; -fx-border-width: 1px;");
        
        Button browseBtn = new Button("Parcourir");
        browseBtn.setMaxWidth(Double.MAX_VALUE);
        styleSecondaryButton(browseBtn);
        browseBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Sélectionner le Répertoire");
            File dir = chooser.showDialog(stage);
            if (dir != null) {
                pathField.setText(dir.getAbsolutePath());
            }
        });
        
        configCard.getChildren().addAll(configTitle, new Separator(), pathLabel, pathField, browseBtn, new Separator());
        
        // Seuil
        Label thresholdLabel = new Label("Seuil de Méthodes (Question 11)");
        thresholdLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: #0f172a;");
        
        thresholdSpinner = new Spinner<>(1, 50, 3);
        thresholdSpinner.setEditable(true);
        thresholdSpinner.setMaxWidth(Double.MAX_VALUE);
        thresholdSpinner.setStyle("-fx-font-size: 12px;");
        
        configCard.getChildren().addAll(thresholdLabel, thresholdSpinner);
        
        // Actions Card
        VBox actionsCard = new VBox(8);
        actionsCard.setPadding(new Insets(14));
        actionsCard.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 1);");
        
        Label actionsTitle = new Label("Actions");
        actionsTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");
        
        analyzeBtn = new Button("▶ Analyser le Code");
        analyzeBtn.setMaxWidth(Double.MAX_VALUE);
        analyzeBtn.setPrefHeight(40);
        stylePrimaryButton(analyzeBtn);
        analyzeBtn.setOnAction(e -> analyzeCode());
        
        actionsCard.getChildren().addAll(actionsTitle, new Separator(), analyzeBtn);
        
        controlPanel.getChildren().addAll(configCard, actionsCard);
        return controlPanel;
    }
    
    private VBox createResultsPanel() {
        VBox resultsPanel = new VBox(12);
        
        Label resultsTitle = new Label("Résultats d'Analyse");
        resultsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");
        
        resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setWrapText(false);
        resultsArea.setStyle("-fx-font-family: 'Monospaced', 'Courier New', monospace; -fx-font-size: 14px; -fx-background-color: #fefefe; -fx-control-inner-background: #fefefe; -fx-text-fill: #0f172a; -fx-padding: 20px;");
        resultsArea.setText("Sélectionnez un répertoire et cliquez sur 'Analyser'");
        
        VBox resultsCard = new VBox();
        resultsCard.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        resultsCard.getChildren().add(resultsArea);
        VBox.setVgrow(resultsCard, Priority.ALWAYS);
        VBox.setVgrow(resultsArea, Priority.ALWAYS);
        
        resultsPanel.getChildren().addAll(resultsTitle, resultsCard);
        VBox.setVgrow(resultsPanel, Priority.ALWAYS);
        return resultsPanel;
    }
    
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(12, 25, 12, 25));
        footer.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-width: 1px 0 0 0;");
        
        Label statusLabel = new Label("Prêt - Sélectionnez un répertoire pour commencer");
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        
        footer.getChildren().add(statusLabel);
        return footer;
    }
    
    @SuppressWarnings("unchecked")
    private void analyzeCode() {
        String path = pathField.getText().trim();
        
        if (path.isEmpty()) {
            showAlert("Attention", "Veuillez sélectionner un répertoire.", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            analyzer = new CodeAnalyzer();
            analyzer.analyzeDirectory(path);
            
            int threshold = thresholdSpinner.getValue();
            Map<String, Object> stats = analyzer.getStatistics(threshold);
            
            // Formatter les 13 métriques
            StringBuilder sb = new StringBuilder();
            sb.append("RÉSULTATS DE L'ANALYSE\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            
            // 1-4: MÉTRIQUES GLOBALES
            sb.append("MÉTRIQUES GLOBALES\n\n");
            sb.append(String.format(" %-45s %s\n", "Classes", stats.get("totalClasses")));
            sb.append(String.format(" %-45s %s\n", "Lignes de code", stats.get("totalLines")));
            sb.append(String.format(" %-45s %s\n", "Méthodes", stats.get("totalMethods")));
            sb.append(String.format(" %-45s %s\n", "Packages", stats.get("totalPackages")));
            sb.append("\n");
            
            // 5-7: MOYENNES
            sb.append("MOYENNES\n\n");
            sb.append(String.format(" %-45s %.2f\n", "Méthodes/classe", stats.get("avgMethodsPerClass")));
            sb.append(String.format(" %-45s %.2f\n", "Lignes/méthode", stats.get("avgLinesPerMethod")));
            sb.append(String.format(" %-45s %.2f\n", "Attributs/classe", stats.get("avgAttributesPerClass")));
            sb.append("\n");
            
            // 8: TOP 10% MÉTHODES
            sb.append("TOP 10% - CLASSES AVEC PLUS DE MÉTHODES\n\n");
            List<ClassInfo> topMethodClasses = (List<ClassInfo>) stats.get("topMethodClasses");
            if (topMethodClasses != null && !topMethodClasses.isEmpty()) {
                for (ClassInfo cls : topMethodClasses) {
                    sb.append(String.format(" %s (%d méthodes)\n", cls.name, cls.getMethodCount()));
                }
            }
            sb.append("\n");
            
            // 9: TOP 10% ATTRIBUTS
            sb.append("TOP 10% - CLASSES AVEC PLUS D'ATTRIBUTS\n\n");
            List<ClassInfo> topAttributeClasses = (List<ClassInfo>) stats.get("topAttributeClasses");
            if (topAttributeClasses != null && !topAttributeClasses.isEmpty()) {
                for (ClassInfo cls : topAttributeClasses) {
                    sb.append(String.format(" %s (%d attributs)\n", cls.name, cls.getAttributeCount()));
                }
            }
            sb.append("\n");
            
            // 10: DANS LES DEUX
            sb.append("CLASSES DANS LES DEUX CATÉGORIES (Top 10%)\n\n");
            List<ClassInfo> inBothCategories = (List<ClassInfo>) stats.get("inBothCategories");
            if (inBothCategories != null && !inBothCategories.isEmpty()) {
                for (ClassInfo cls : inBothCategories) {
                    sb.append(String.format(" %s\n", cls.name));
                }
            } else {
                sb.append(" Aucune classe\n");
            }
            sb.append("\n");
            
            // 11: SEUIL
            sb.append(String.format("CLASSES AVEC PLUS DE %d MÉTHODES\n\n", threshold));
            List<ClassInfo> classesAboveThreshold = (List<ClassInfo>) stats.get("classesAboveThreshold");
            if (classesAboveThreshold != null && !classesAboveThreshold.isEmpty()) {
                for (ClassInfo cls : classesAboveThreshold) {
                    sb.append(String.format(" %s (%d méthodes)\n", cls.name, cls.getMethodCount()));
                }
            } else {
                sb.append(" Aucune classe\n");
            }
            sb.append("\n");
            
            // 12: TOP MÉTHODES PAR CLASSE
            sb.append("TOP 10% - MÉTHODES AVEC PLUS DE LIGNES (par classe)\n\n");
            Map<String, List<String>> topMethodsPerClass = (Map<String, List<String>>) stats.get("topMethodsPerClass");
            if (topMethodsPerClass != null && !topMethodsPerClass.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : topMethodsPerClass.entrySet()) {
                    sb.append(" Classe " + entry.getKey() + ":\n");
                    for (String method : entry.getValue()) {
                        sb.append("   - " + method + "\n");
                    }
                }
            } else {
                sb.append(" Aucune méthode\n");
            }
            sb.append("\n");
            
            // 13: MAX PARAMÈTRES
            int maxParams = (int) stats.get("maxParameters");
            sb.append(String.format("MAXIMUM DE PARAMÈTRES: %d\n\n", maxParams));
            sb.append(" Méthodes avec " + maxParams + " paramètres:\n");
            List<String> maxParamMethods = (List<String>) stats.get("maxParameterMethods");
            if (maxParamMethods != null && !maxParamMethods.isEmpty()) {
                for (String method : maxParamMethods) {
                    sb.append("   - " + method + "\n");
                }
            } else {
                sb.append("   Aucune\n");
            }
            
            resultsArea.setText(sb.toString());
            
        } catch (Exception e) {
            showAlert("Erreur", "Erreur : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void stylePrimaryButton(Button btn) {
        btn.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 10px; -fx-background-radius: 6px; -fx-cursor: hand;");
    }
    
    private void styleSecondaryButton(Button btn) {
        btn.setStyle("-fx-background-color: white; -fx-text-fill: #000000; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 8px; -fx-background-radius: 6px; -fx-border-color: #000000; -fx-border-radius: 6px; -fx-border-width: 1.5px; -fx-cursor: hand;");
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
