package com.tp.tp1.callgraph;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.*;
import java.util.Map;

/**
 * JavaFX GUI for Call Graph Analyzer
 */
public class CallGraphGUI extends Application {
    
    private TextField pathField;
    private TextArea resultsArea;
    private TextArea statsArea;
    private CallGraphBuilder builder;
    private Label statusLabel;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Call Graph Analyzer - Exercise 2");
        
        builder = new CallGraphBuilder();
        
        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Top: Header
        root.setTop(createHeader());
        
        // Center: Content
        root.setCenter(createContent());
        
        // Bottom: Status
        root.setBottom(createFooter());
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 15, 0));
        
        Label title = new Label("📞 Call Graph Analyzer");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label subtitle = new Label("Build method call graphs from Java source code");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    private VBox createContent() {
        VBox content = new VBox(15);
        
        // Control panel
        content.getChildren().add(createControlPanel());
        
        // Split view: Stats + Graph
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.3);
        
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
        
        // Right: Call Graph
        VBox graphBox = new VBox(10);
        Label graphLabel = new Label("🔗 Call Graph");
        graphLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        resultsArea.setText("=== CALL GRAPH ===\n\nNo results to display.");
        VBox.setVgrow(resultsArea, Priority.ALWAYS);
        
        graphBox.getChildren().addAll(graphLabel, resultsArea);
        
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
        
        Button browseBtn = new Button("📁 Browse");
        browseBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        browseBtn.setOnAction(e -> browseFolder());
        
        Button analyzeBtn = new Button("🔍 Analyze");
        analyzeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        analyzeBtn.setOnAction(e -> analyzeProject());
        
        Button exportBtn = new Button("💾 Export DOT");
        exportBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        exportBtn.setOnAction(e -> exportDot());
        
        Button clearBtn = new Button("🗑️ Clear");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> clear());
        
        controlPanel.getChildren().addAll(pathLabel, pathField, browseBtn, analyzeBtn, exportBtn, clearBtn);
        
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
    
    private void analyzeProject() {
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
            builder = new CallGraphBuilder();
            builder.analyzeProject(projectPath);
            
            displayResults();
            updateStatus("✅ Analysis complete!");
            
        } catch (Exception e) {
            showAlert("Analysis Error", "Error during analysis: " + e.getMessage());
            updateStatus("❌ Analysis failed");
        }
    }
    
    private void displayResults() {
        // Display statistics
        Map<String, Object> stats = builder.getStatistics();
        StringBuilder statsSb = new StringBuilder();
        statsSb.append("═══ STATISTICS ═══\n\n");
        statsSb.append("Total Methods  : ").append(stats.get("totalNodes")).append("\n");
        statsSb.append("Total Calls    : ").append(stats.get("totalEdges")).append("\n\n");
        
        statsSb.append("─── Entry Points ───\n");
        java.util.List<?> entryPoints = (java.util.List<?>) stats.get("entryPoints");
        if (entryPoints.isEmpty()) {
            statsSb.append("(none)\n");
        } else {
            entryPoints.stream().limit(10).forEach(ep -> statsSb.append("• ").append(ep).append("\n"));
            if (entryPoints.size() > 10) {
                statsSb.append("... +").append(entryPoints.size() - 10).append(" more\n");
            }
        }
        
        statsSb.append("\n─── Leaf Methods ───\n");
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
        
        // Display call graph
        resultsArea.setText(builder.toGraphFormat());
    }
    
    private void exportDot() {
        if (builder.getNodes().isEmpty()) {
            showAlert("No Data", "Please analyze a project first.");
            return;
        }
        
        try {
            String dotContent = builder.toDotFormat();
            Path dotFile = Paths.get("callgraph.dot");
            Files.writeString(dotFile, dotContent);
            
            showInfo("Export Successful", 
                    "DOT file saved: " + dotFile.toAbsolutePath() + "\n\n" +
                    "To visualize:\n" +
                    "dot -Tpng callgraph.dot -o callgraph.png");
            
            updateStatus("✅ Exported to callgraph.dot");
            
        } catch (Exception e) {
            showAlert("Export Error", "Failed to export: " + e.getMessage());
        }
    }
    
    private void clear() {
        pathField.clear();
        resultsArea.setText("=== CALL GRAPH ===\n\nNo results to display.");
        statsArea.setText("No analysis yet...\n\nSelect a project folder and click Analyze.");
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
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
