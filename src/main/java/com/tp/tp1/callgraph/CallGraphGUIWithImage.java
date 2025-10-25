package com.tp.tp1.callgraph;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class CallGraphGUIWithImage extends Application {
    
    private TextField pathField;
    private TextArea statsArea;
    private ImageView graphImageView;
    private CallGraphBuilder builder;
    private Label statusLabel;
    private ScrollPane imageScrollPane;
    private VBox graphBox;
    private StackPane placeholderPane;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Call Graph Analyzer - Exercise 2");
        
        builder = new CallGraphBuilder();
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        root.setTop(createHeader());
        root.setCenter(createContent());
        root.setBottom(createFooter());
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 15, 0));
        
        Label title = new Label("📞 Call Graph Analyzer");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label subtitle = new Label("Build and visualize method call graphs from Java source code");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    private VBox createContent() {
        VBox content = new VBox(15);
        content.getChildren().add(createControlPanel());
        
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.25);
        
        // Left: Statistics
        VBox statsBox = new VBox(10);
        Label statsLabel = new Label("📊 Statistics");
        statsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        statsArea = new TextArea();
        statsArea.setEditable(false);
        statsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        statsArea.setText("No analysis yet...\n\nSelect a project folder and click Analyze.");
        VBox.setVgrow(statsArea, Priority.ALWAYS);
        
        statsBox.getChildren().addAll(statsLabel, statsArea);
        
        // Right: Graph Image
        graphBox = new VBox(10);
        Label graphLabel = new Label("🖼️ Call Graph Visualization");
        graphLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        graphImageView = new ImageView();
        graphImageView.setPreserveRatio(true);
        graphImageView.setSmooth(true);
        
        imageScrollPane = new ScrollPane();
        imageScrollPane.setContent(graphImageView);
        imageScrollPane.setFitToWidth(true);
        imageScrollPane.setFitToHeight(true);
        imageScrollPane.setStyle("-fx-background-color: white;");
        VBox.setVgrow(imageScrollPane, Priority.ALWAYS);
        
        // Placeholder
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
    
    private HBox createControlPanel() {
        HBox controlPanel = new HBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        controlPanel.setAlignment(Pos.CENTER_LEFT);
        
        Label pathLabel = new Label("Project Path:");
        pathLabel.setStyle("-fx-font-weight: bold;");
        
        pathField = new TextField();
        pathField.setPromptText("Select a Java project folder...");
        pathField.setPrefWidth(500);
        HBox.setHgrow(pathField, Priority.ALWAYS);
        
        Button browseBtn = new Button("Browse");
        browseBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        browseBtn.setOnAction(e -> browseFolder());
        
        Button analyzeBtn = new Button("Analyze & Visualize");
        analyzeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        analyzeBtn.setOnAction(e -> analyzeAndVisualize());
        
        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> clear());
        
        controlPanel.getChildren().addAll(pathLabel, pathField, browseBtn, analyzeBtn, clearBtn);
        return controlPanel;
    }
    
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(10, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_LEFT);
        
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        footer.getChildren().add(statusLabel);
        return footer;
    }
    
    private void browseFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Java Project Folder");
        
        File selectedDir = chooser.showDialog(pathField.getScene().getWindow());
        if (selectedDir != null) {
            pathField.setText(selectedDir.getAbsolutePath());
            updateStatus("Folder selected: " + selectedDir.getName());
        }
    }
    
    private void analyzeAndVisualize() {
        String projectPath = pathField.getText();
        
        if (projectPath.isEmpty()) {
            showAlert("No Path Selected", "Please select a project folder first.");
            return;
        }
        
        if (!Files.exists(Paths.get(projectPath))) {
            showAlert("Invalid Path", "The selected path does not exist.");
            return;
        }
        
        updateStatus("Analyzing...");
        
        try {
            // Build call graph
            builder = new CallGraphBuilder();
            builder.analyzeProject(projectPath);
            
            // Display statistics
            displayStatistics();
            
            // Generate and display image
            updateStatus("Generating visualization...");
            generateAndDisplayImage();
            
            updateStatus("✅ Analysis complete!");
            
        } catch (Exception e) {
            showAlert("Error", "Error during analysis:\n" + e.getClass().getName() + ": " + e.getMessage());
            updateStatus("❌ Analysis failed");
            e.printStackTrace();
        }
    }
    
    private void displayStatistics() {
        Map<String, Object> stats = builder.getStatistics();
        StringBuilder statsSb = new StringBuilder();
        statsSb.append("=== STATISTICS ===\n\n");
        statsSb.append("Total Methods  : ").append(stats.get("totalNodes")).append("\n");
        statsSb.append("Total Calls    : ").append(stats.get("totalEdges")).append("\n\n");
        
        statsSb.append("--- Entry Points ---\n");
        java.util.List<?> entryPoints = (java.util.List<?>) stats.get("entryPoints");
        if (entryPoints.isEmpty()) {
            statsSb.append("(none)\n");
        } else {
            entryPoints.stream().limit(10).forEach(ep -> statsSb.append("• ").append(ep).append("\n"));
            if (entryPoints.size() > 10) {
                statsSb.append("... +").append(entryPoints.size() - 10).append(" more\n");
            }
        }
        
        statsSb.append("\n--- Leaf Methods ---\n");
        java.util.List<?> leafMethods = (java.util.List<?>) stats.get("leafMethods");
        if (leafMethods.isEmpty()) {
            statsSb.append("(none)\n");
        } else {
            leafMethods.stream().limit(10).forEach(lm -> statsSb.append("• ").append(lm).append("\n"));
            if (leafMethods.size() > 10) {
                statsSb.append("... +").append(leafMethods.size() - 10).append(" more\n");
            }
        }
        
        statsArea.setText(statsSb.toString());
    }
    
    private void generateAndDisplayImage() {
        try {
            // Export DOT file
            String dotContent = builder.toDotFormat();
            Path dotFile = Paths.get("callgraph.dot");
            Files.writeString(dotFile, dotContent);
            
            // Generate PNG with Graphviz
            Path pngFile = Paths.get("callgraph.png");
            
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFile.toString(), "-o", pngFile.toString());
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                showAlert("Graphviz Error", "Failed to generate image.\n\nMake sure Graphviz is installed:\nsudo apt install graphviz");
                return;
            }
            
            // Load and display image
            if (Files.exists(pngFile)) {
                Image image = new Image(pngFile.toUri().toString());
                graphImageView.setImage(image);
                
                // Replace placeholder with image
                if (graphBox.getChildren().contains(placeholderPane)) {
                    graphBox.getChildren().remove(placeholderPane);
                    graphBox.getChildren().add(imageScrollPane);
                }
                
                updateStatus("✅ Visualization generated: " + pngFile.toAbsolutePath());
            }
            
        } catch (Exception e) {
            showAlert("Visualization Error", "Failed to generate visualization:\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void clear() {
        pathField.clear();
        statsArea.setText("No analysis yet...\n\nSelect a project folder and click Analyze.");
        graphImageView.setImage(null);
        
        // Restore placeholder
        if (!graphBox.getChildren().contains(placeholderPane)) {
            graphBox.getChildren().remove(imageScrollPane);
            graphBox.getChildren().add(placeholderPane);
        }
        
        builder = new CallGraphBuilder();
        updateStatus("Cleared");
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
