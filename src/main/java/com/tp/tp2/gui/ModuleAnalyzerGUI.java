package com.tp.tp2.gui;
import com.tp.tp2.analyzer.CouplingAnalyzer;
import com.tp.tp2.clustering.*;
import com.tp.tp2.model.CouplingGraph;
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
import java.util.List;

public class ModuleAnalyzerGUI extends Application {
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
        stage.setTitle("Analyseur de Modules - Exercice 2");
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
        Label title = new Label("Analyseur de Modules");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333333;");
        Label subtitle = new Label("Clustering hiérarchique et identification de modules");
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
        Label statsLabel = new Label("📊 Modules");
        statsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        resultsArea.setText("En attente...");
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
        Label placeholderLabel = new Label("Aucun dendrogramme\n\nAnalysez un projet d'abord");
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
        statusLabel = new Label("Prêt");
        bar.getChildren().add(statusLabel);
        return bar;
    }

    private void browse(Stage stage) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Sélectionner un projet");
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
        statusLabel.setText("Analyse en cours...");
        Task<Void> task = new Task<Void>() {
            protected Void call() throws Exception {
                CouplingAnalyzer analyzer = new CouplingAnalyzer();
                currentGraph = analyzer.analyzeProject(path);
                currentGraph.computeCouplingMetrics();
                HierarchicalClustering clustering = new HierarchicalClustering();
                dendrogram = clustering.buildDendrogram(currentGraph);
                ModuleIdentifier identifier = new ModuleIdentifier();
                List<com.tp.tp2.modules.Module> modules = identifier.identifyModules(dendrogram, threshold);
                DendrogramExporter exporter = new DendrogramExporter();
                new File("docs").mkdirs();
                exporter.exportToDot(dendrogram, "docs/dendrogram.dot");
                exporter.generateImage("docs/dendrogram.dot", "docs/dendrogram.png");
                StringBuilder sb = new StringBuilder("=== MODULES IDENTIFIÉS ===\n\n");
                sb.append("Seuil      : ").append(threshold).append("\n");
                sb.append("Nb modules : ").append(modules.size()).append("\n\n");
                for (com.tp.tp2.modules.Module m : modules) {
                    sb.append(m.toString()).append("\n");
                }
                resultsArea.setText(sb.toString());
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            load();
            statusLabel.setText("Analyse terminée !");
            analyzeButton.setDisable(false);
        });
        task.setOnFailed(e -> {
            statusLabel.setText("Erreur lors de l'analyse");
            analyzeButton.setDisable(false);
        });
        new Thread(task).start();
    }

    private void load() {
        try {
            File f = new File("docs/dendrogram.png");
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
        statusLabel.setText("Prêt");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
