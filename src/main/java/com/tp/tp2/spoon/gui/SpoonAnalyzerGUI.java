package com.tp.tp2.spoon.gui;

import com.tp.tp2.spoon.analyzer.SpoonCouplingAnalyzer;
import com.tp.tp2.model.CouplingGraph;
import com.tp.tp2.clustering.*;
import com.tp.tp2.modules.ModuleIdentifier;
import com.tp.tp2.visualization.DendrogramExporter;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import java.io.*;
import java.util.*;

public class SpoonAnalyzerGUI extends Application {
    private TextField projectPathField, thresholdField;
    private Button browseButton, analyzeButton, clearButton;
    private TextArea resultsArea;
    private ImageView imageView;
    private Label statusLabel;
    private CouplingGraph currentGraph;
    private DendrogramNode dendrogram;
    private ScrollPane imageScrollPane;
    private StackPane placeholderPane;
    private VBox graphBox;

    public void start(Stage stage) {
        stage.setTitle("Analyseur Spoon - TP2 Exercice 3");
        VBox root = new VBox();
        root.setStyle("-fx-background-color: white;");
        VBox header = createHeader(stage);
        VBox content = createContent();
        HBox statusBar = createStatusBar();
        VBox.setVgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(header, content, statusBar);
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
    }

    private VBox createHeader(Stage stage) {
        VBox header = new VBox(15);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #E8E8E8;");
        Label title = new Label("🥄 Analyseur Spoon - Exo 1 + 2");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333333;");
        Label subtitle = new Label("TP2 Exercice 3 - Couplage + Clustering avec Spoon");
        subtitle.setFont(Font.font("System", 13));
        subtitle.setStyle("-fx-text-fill: #666666;");
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        Label pathLabel = new Label("Projet :");
        pathLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        projectPathField = new TextField();
        projectPathField.setPrefWidth(400);
        browseButton = new Button("Parcourir");
        browseButton.setStyle("-fx-background-color: #17A2B8; -fx-text-fill: white; -fx-font-weight: bold;");
        browseButton.setOnAction(e -> browse(stage));
        Label threshLabel = new Label("Seuil :");
        threshLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        thresholdField = new TextField("0.5");
        thresholdField.setPrefWidth(80);
        analyzeButton = new Button("Analyser");
        analyzeButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold;");
        analyzeButton.setDisable(true);
        analyzeButton.setOnAction(e -> analyze());
        clearButton = new Button("Effacer");
        clearButton.setStyle("-fx-background-color: #6C757D; -fx-text-fill: white; -fx-font-weight: bold;");
        clearButton.setOnAction(e -> clear());
        controls.getChildren().addAll(pathLabel, projectPathField, browseButton, threshLabel, thresholdField, analyzeButton, clearButton);
        header.getChildren().addAll(title, subtitle, controls);
        return header;
    }

    private VBox createContent() {
        VBox content = new VBox();
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.3);
        VBox statsBox = new VBox(10);
        Label statsLabel = new Label("📊 Résultats (Spoon)");
        statsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");
        resultsArea.setText("En attente d'analyse...\n\nFramework: Spoon\n\nExo 1: Graphe de couplage ✓\nExo 2: Clustering hiérarchique ✓");
        VBox.setVgrow(resultsArea, Priority.ALWAYS);
        statsBox.getChildren().addAll(statsLabel, resultsArea);
        graphBox = new VBox(10);
        Label graphLabel = new Label("🌳 Dendrogramme");
        graphLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageScrollPane = new ScrollPane();
        imageScrollPane.setContent(imageView);
        imageScrollPane.setFitToWidth(true);
        imageScrollPane.setFitToHeight(true);
        imageScrollPane.setStyle("-fx-background-color: white;");
        VBox.setVgrow(imageScrollPane, Priority.ALWAYS);
        Label placeholderLabel = new Label("Aucun dendrogramme\n\nAnalysez un projet");
        placeholderLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #95a5a6;");
        placeholderPane = new StackPane(placeholderLabel);
        placeholderPane.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1;");
        VBox.setVgrow(placeholderPane, Priority.ALWAYS);
        graphBox.getChildren().addAll(graphLabel, placeholderPane);
        splitPane.getItems().addAll(statsBox, graphBox);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        content.getChildren().add(splitPane);
        return content;
    }

    private HBox createStatusBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(5, 10, 5, 10));
        bar.setStyle("-fx-background-color: #F0F0F0; -fx-border-color: #CCC; -fx-border-width: 1 0 0 0;");
        statusLabel = new Label("Prêt - Framework Spoon");
        bar.getChildren().add(statusLabel);
        return bar;
    }

    private void browse(Stage stage) {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(stage);
        if (dir != null) {
            projectPathField.setText(dir.getAbsolutePath());
            analyzeButton.setDisable(false);
            statusLabel.setText("Sélectionné : " + dir.getName());
        }
    }

    private void analyze() {
        String path = projectPathField.getText();
        if (path.isEmpty()) return;
        double threshold = Double.parseDouble(thresholdField.getText());
        analyzeButton.setDisable(true);
        statusLabel.setText("Analyse Spoon en cours...");
        Task<Void> task = new Task<Void>() {
            protected Void call() throws Exception {
                SpoonCouplingAnalyzer analyzer = new SpoonCouplingAnalyzer();
                currentGraph = analyzer.analyzeProject(path);
                currentGraph.computeCouplingMetrics();
                
                HierarchicalClustering clustering = new HierarchicalClustering();
                dendrogram = clustering.buildDendrogram(currentGraph);
                ModuleIdentifier identifier = new ModuleIdentifier();
                List<com.tp.tp2.modules.Module> modules = identifier.identifyModules(dendrogram, threshold);
                
                DendrogramExporter exporter = new DendrogramExporter();
                new File("docs").mkdirs();
                exporter.exportToDot(dendrogram, "docs/spoon-dendrogram.dot");
                exporter.generateImage("docs/spoon-dendrogram.dot", "docs/spoon-dendrogram.png");
                
                // Afficher les résultats
                StringBuilder sb = new StringBuilder();
                sb.append("=== ANALYSE SPOON (EXO 1 + 2) ===\n\n");
                sb.append("Framework : Spoon 10.4.2\n");
                sb.append("Projet    : ").append(new File(path).getName()).append("\n\n");
                sb.append("--- Résultats ---\n");
                sb.append("Seuil     : ").append(threshold).append("\n");
                sb.append("Nb modules: ").append(modules.size()).append("\n\n");
                
                for (int i = 0; i < modules.size(); i++) {
                    com.tp.tp2.modules.Module m = modules.get(i);
                    sb.append("Module").append(i+1).append(":\n");
                    for (String className : m.getClasses()) {
                        sb.append("  • ").append(className).append("\n");
                    }
                    sb.append("\n");
                }
                
                resultsArea.setText(sb.toString());
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            load();
            statusLabel.setText("Analyse Spoon terminée !");
            analyzeButton.setDisable(false);
        });
        new Thread(task).start();
    }

    private void load() {
        try {
            File f = new File("docs/spoon-dendrogram.png");
            if (f.exists()) {
                imageView.setImage(new Image(new FileInputStream(f)));
                graphBox.getChildren().remove(placeholderPane);
                if (!graphBox.getChildren().contains(imageScrollPane)) {
                    graphBox.getChildren().add(imageScrollPane);
                }
            }
        } catch (Exception e) {}
    }

    private void clear() {
        projectPathField.clear();
        thresholdField.setText("0.5");
        resultsArea.setText("En attente...");
        imageView.setImage(null);
        analyzeButton.setDisable(true);
        graphBox.getChildren().remove(imageScrollPane);
        if (!graphBox.getChildren().contains(placeholderPane)) {
            graphBox.getChildren().add(placeholderPane);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
