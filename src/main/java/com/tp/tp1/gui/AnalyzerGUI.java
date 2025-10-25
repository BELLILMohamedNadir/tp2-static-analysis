package com.tp.tp1.gui;

import com.tp.tp1.analyzer.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import java.io.File;

public class AnalyzerGUI extends Application {
    private CodeAnalyzer analyzer;
    private TextField pathField;
    private Button browseButton, analyzeButton;
    private TextArea resultsArea;
    private Label statusLabel;

    public void start(Stage stage) {
        stage.setTitle("Analyseur de Métriques - TP1 Exercice 2");
        
        analyzer = new CodeAnalyzer();
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");
        
        VBox header = new VBox(10);
        Label title = new Label("Analyseur de Métriques");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        
        Label subtitle = new Label("Analyse statique de code Java - Calcul des métriques");
        subtitle.setFont(Font.font("System", 13));
        subtitle.setStyle("-fx-text-fill: #666;");
        header.getChildren().addAll(title, subtitle);
        
        HBox controls = new HBox(10);
        controls.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label pathLabel = new Label("Projet :");
        pathLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        pathField = new TextField();
        pathField.setPrefWidth(400);
        pathField.setPromptText("Sélectionner un dossier...");
        
        browseButton = new Button("Parcourir");
        browseButton.setStyle("-fx-background-color: #17A2B8; -fx-text-fill: white;");
        browseButton.setOnAction(e -> browse(stage));
        
        analyzeButton = new Button("Analyser");
        analyzeButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white;");
        analyzeButton.setDisable(true);
        analyzeButton.setOnAction(e -> analyze());
        
        Button clearButton = new Button("Effacer");
        clearButton.setStyle("-fx-background-color: #6C757D; -fx-text-fill: white;");
        clearButton.setOnAction(e -> clear());
        
        controls.getChildren().addAll(pathLabel, pathField, browseButton, analyzeButton, clearButton);
        
        VBox resultsBox = new VBox(10);
        Label resultsLabel = new Label("📊 Résultats");
        resultsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        resultsArea.setText("En attente...");
        VBox.setVgrow(resultsArea, Priority.ALWAYS);
        resultsBox.getChildren().addAll(resultsLabel, resultsArea);
        VBox.setVgrow(resultsBox, Priority.ALWAYS);
        
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #f0f0f0;");
        statusLabel = new Label("Prêt");
        statusBar.getChildren().add(statusLabel);
        
        root.getChildren().addAll(header, controls, resultsBox, statusBar);
        stage.setScene(new Scene(root, 1000, 700));
        stage.show();
    }
    
    private void browse(Stage stage) {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(stage);
        if (dir != null) {
            pathField.setText(dir.getAbsolutePath());
            analyzeButton.setDisable(false);
            statusLabel.setText("Sélectionné : " + dir.getName());
        }
    }
    
    private void analyze() {
        String path = pathField.getText();
        if (path.isEmpty()) return;
        
        analyzeButton.setDisable(true);
        statusLabel.setText("Analyse en cours...");
        
        new Thread(() -> {
            try {
                analyzer.analyzeDirectory(path);
                
                javafx.application.Platform.runLater(() -> {
                    analyzer.printStatistics(10);
                    resultsArea.setText("Analyse terminée avec succès !");
                    statusLabel.setText("Terminé");
                    analyzeButton.setDisable(false);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    resultsArea.setText("Erreur : " + e.getMessage());
                    statusLabel.setText("Erreur");
                    analyzeButton.setDisable(false);
                });
            }
        }).start();
    }
    
    private void clear() {
        pathField.clear();
        resultsArea.setText("En attente...");
        analyzeButton.setDisable(true);
        statusLabel.setText("Prêt");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
