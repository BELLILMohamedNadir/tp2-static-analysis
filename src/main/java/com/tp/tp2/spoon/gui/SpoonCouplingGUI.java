package com.tp.tp2.spoon.gui;

import com.tp.tp2.spoon.analyzer.SpoonCouplingAnalyzer;
import com.tp.tp2.graph.CouplingGraphExporter;
import com.tp.tp2.model.ClassCoupling;
import com.tp.tp2.model.CouplingGraph;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class SpoonCouplingGUI extends Application {
    private TextField projectPathField;
    private Button browseButton, analyzeButton, clearButton;
    private TextArea resultsArea;
    private ImageView imageView;
    private Label statusLabel;
    private CouplingGraph currentGraph;
    private ScrollPane imageScrollPane;
    private StackPane placeholderPane;
    private VBox graphBox;

    public void start(Stage stage) {
        stage.setTitle("Analyseur Spoon - Graphe de Couplage - Exercise 1");
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
        
        Label title = new Label("Analyseur Spoon - Graphe de Couplage");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333333;");
        
        Label subtitle = new Label("Analyse avec Spoon - Graphe de couplage class coupling graphs from Java source code");
        subtitle.setFont(Font.font("System", 13));
        subtitle.setStyle("-fx-text-fill: #666666;");
        
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        
        Label pathLabel = new Label("Project Path:");
        pathLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        projectPathField = new TextField();
        projectPathField.setPrefWidth(600);
        
        browseButton = new Button("Browse");
        browseButton.setStyle("-fx-background-color: #17A2B8; -fx-text-fill: white; -fx-font-weight: bold;");
        browseButton.setOnAction(e -> browse(stage));
        
        analyzeButton = new Button("Analyze & Visualize");
        analyzeButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold;");
        analyzeButton.setDisable(true);
        analyzeButton.setOnAction(e -> analyze());
        
        clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #6C757D; -fx-text-fill: white; -fx-font-weight: bold;");
        clearButton.setOnAction(e -> clear());
        
        controls.getChildren().addAll(pathLabel, projectPathField, browseButton, analyzeButton, clearButton);
        header.getChildren().addAll(title, subtitle, controls);
        return header;
    }

    private VBox createContent() {
        VBox content = new VBox();
        
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.25);
        
        // LEFT: Statistics (COMME TP1 EXO2)
        VBox statsBox = new VBox(10);
        Label statsLabel = new Label("📊 Statistics");
        statsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        resultsArea.setText("=== STATISTICS ===\n\nWaiting...");
        VBox.setVgrow(resultsArea, Priority.ALWAYS);
        
        statsBox.getChildren().addAll(statsLabel, resultsArea);
        
        // RIGHT: Graph (COMME TP1 EXO2)
        graphBox = new VBox(10);
        Label graphLabel = new Label("🔗 Coupling Graph Visualization");
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
        
        Label placeholderLabel = new Label("No graph to display\n\nAnalyze a project first");
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
        statusLabel = new Label("Ready");
        bar.getChildren().add(statusLabel);
        return bar;
    }

    private void browse(Stage stage) {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(stage);
        if (dir != null) {
            projectPathField.setText(dir.getAbsolutePath());
            analyzeButton.setDisable(false);
            statusLabel.setText("Selected: " + dir.getName());
        }
    }

    private void analyze() {
        String path = projectPathField.getText();
        if (path.isEmpty()) return;
        
        analyzeButton.setDisable(true);
        statusLabel.setText("Analyzing...");
        
        Task<Void> task = new Task<Void>() {
            protected Void call() throws Exception {
                SpoonCouplingAnalyzer analyzer = new SpoonCouplingAnalyzer();
                currentGraph = analyzer.analyzeProject(path);
                currentGraph.computeCouplingMetrics();
                CouplingGraphExporter exporter = new CouplingGraphExporter();
                new File("docs").mkdirs();
                exporter.exportToDot(currentGraph, "docs/coupling-spoon.dot");
                exporter.generateImage("docs/coupling-spoon.dot", "docs/coupling-spoon.png");
                return null;
            }
        };
        
        task.setOnSucceeded(e -> {
            display();
            load();
            statusLabel.setText("Complete!");
            analyzeButton.setDisable(false);
        });
        
        new Thread(task).start();
    }

    private void display() {
        StringBuilder sb = new StringBuilder("=== STATISTICS ===\n\n");
        sb.append(String.format("Classes  : %d\n", currentGraph.getClassCount()));
        sb.append(String.format("Relations: %d\n\n", currentGraph.getTotalRelations()));
        sb.append("--- Top Couplings ---\n\n");
        List<ClassCoupling> list = currentGraph.getAllCouplings();
        list.sort((a, b) -> Double.compare(b.getCouplingMetric(), a.getCouplingMetric()));
        int c = 0;
        for (ClassCoupling cp : list) {
            if (c++ >= 10) break;
            sb.append(String.format("• %s -> %s (%.4f)\n", cp.getClassA(), cp.getClassB(), cp.getCouplingMetric()));
        }
        resultsArea.setText(sb.toString());
    }

    private void load() {
        try {
            File f = new File("docs/coupling-spoon.png");
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
        resultsArea.setText("=== STATISTICS ===\n\nWaiting...");
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
