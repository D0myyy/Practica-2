package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

import dao.*;
import model.*;
import java.util.List;

public class AppUI extends Application {

    private PasagerDAO pasagerDAO = new PasagerDAO();
    private ZborDAO zborDAO = new ZborDAO();
    private RezervareDAO rezervareDAO = new RezervareDAO();
    private BiletDAO biletDAO = new BiletDAO();
    private PlataDAO plataDAO = new PlataDAO();
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rezervare Bilete Avion");
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0d1117;");

        root.setTop(createTopBar());
        
        HBox mainLayout = new HBox();
        mainLayout.setStyle("-fx-background-color: #0d1117;");
        mainLayout.getChildren().addAll(createSidebar(), createMainContent());
        
        root.setCenter(mainLayout);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createTopBar() {
        HBox topbar = new HBox();
        topbar.setStyle("-fx-background-color: #111827; -fx-border-color: #253047; " + 
                       "-fx-border-width: 0 0 1 0; -fx-padding: 0 20px;");
        topbar.setPrefHeight(52);
        topbar.setSpacing(14);
        topbar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label logo = new Label("Rezervare Avion");
        logo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #f59e0b;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Circle status = new Circle(3.5);
        status.setFill(Color.web("#10b981"));
        Label statusText = new Label("Connected");
        statusText.setStyle("-fx-font-size: 11px; -fx-text-fill: #10b981;");

        topbar.getChildren().addAll(logo, spacer, status, statusText);
        return topbar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setStyle("-fx-background-color: #111827; -fx-border-color: #253047; " +
                        "-fx-border-width: 0 1 0 0; -fx-padding: 12px 0;");
        sidebar.setPrefWidth(220);

        Button[] buttons = {
            createNavButton("Dashboard"),
            createNavButton("Pasageri"),
            createNavButton("Zboruri"),
            createNavButton("Rezervari"),
            createNavButton("Bilete"),
            createNavButton("Plati")
        };

        for (Button b : buttons) {
            sidebar.getChildren().add(b);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        Label dbStatus = new Label("SQL Server: Connected");
        dbStatus.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b; -fx-padding: 10px;");
        sidebar.getChildren().add(dbStatus);

        return sidebar;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-padding: 9px 12px; -fx-font-size: 13px; -fx-text-fill: #94a3b8; " +
                     "-fx-background-color: transparent; -fx-cursor: hand;");
        btn.setPrefWidth(200);
        return btn;
    }

    private BorderPane createMainContent() {
        BorderPane mainContent = new BorderPane();
        mainContent.setStyle("-fx-background-color: #0d1117;");
        HBox.setHgrow(mainContent, Priority.ALWAYS);

        // Dashboard content
        VBox dashboard = new VBox();
        dashboard.setStyle("-fx-background-color: #0d1117; -fx-padding: 20px 24px;");
        dashboard.setSpacing(20);

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");
        dashboard.getChildren().add(title);

        // Stats
        HBox stats = new HBox();
        stats.setSpacing(14);
        
        int pasCount = pasagerDAO.findAll().size();
        int zbCount = zborDAO.findAll().size();
        int rezCount = rezervareDAO.findAll().size();
        int bilCount = biletDAO.findAll().size();

        stats.getChildren().addAll(
            createStatCard("Pasageri", pasCount, "#f59e0b"),
            createStatCard("Zboruri", zbCount, "#3b82f6"),
            createStatCard("Rezervari", rezCount, "#10b981"),
            createStatCard("Bilete", bilCount, "#ef4444")
        );

        dashboard.getChildren().add(stats);

        // Tables
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #161d2a;");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab pasTab = new Tab("Pasageri", createPasageriTable());
        Tab zbTab = new Tab("Zboruri", createZboruriTable());
        Tab rezTab = new Tab("Rezervari", createRezervariTable());
        Tab bilTab = new Tab("Bilete", createBileteTable());
        Tab plTab = new Tab("Plati", createPlatiTable());

        tabPane.getTabs().addAll(pasTab, zbTab, rezTab, bilTab, plTab);

        dashboard.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        mainContent.setCenter(dashboard);
        return mainContent;
    }

    private VBox createStatCard(String label, int value, String color) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: #161d2a; -fx-border-color: #253047; " +
                      "-fx-border-width: 1; -fx-border-radius: 12; -fx-padding: 18px;");
        card.setSpacing(10);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label val = new Label(String.valueOf(value));
        val.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

        card.getChildren().addAll(val, lbl);
        return card;
    }

    private TableView createPasageriTable() {
        TableView table = new TableView();
        table.setStyle("-fx-background-color: #161d2a;");

        TableColumn idCol = new TableColumn("ID");
        TableColumn numeCol = new TableColumn("Nume");
        TableColumn emailCol = new TableColumn("Email");
        TableColumn telCol = new TableColumn("Telefon");

        table.getColumns().addAll(idCol, numeCol, emailCol, telCol);
        table.setItems(FXCollections.observableArrayList(pasagerDAO.findAll()));
        
        return table;
    }

    private TableView createZboruriTable() {
        TableView table = new TableView();
        table.setStyle("-fx-background-color: #161d2a;");
        TableColumn col = new TableColumn("Numar Zbor");
        table.getColumns().add(col);
        table.setItems(FXCollections.observableArrayList(zborDAO.findAll()));
        return table;
    }

    private TableView createRezervariTable() {
        TableView table = new TableView();
        table.setStyle("-fx-background-color: #161d2a;");
        TableColumn col = new TableColumn("ID");
        table.getColumns().add(col);
        table.setItems(FXCollections.observableArrayList(rezervareDAO.findAll()));
        return table;
    }

    private TableView createBileteTable() {
        TableView table = new TableView();
        table.setStyle("-fx-background-color: #161d2a;");
        TableColumn col = new TableColumn("ID");
        table.getColumns().add(col);
        table.setItems(FXCollections.observableArrayList(biletDAO.findAll()));
        return table;
    }

    private TableView createPlatiTable() {
        TableView table = new TableView();
        table.setStyle("-fx-background-color: #161d2a;");
        TableColumn col = new TableColumn("ID");
        table.getColumns().add(col);
        table.setItems(FXCollections.observableArrayList(plataDAO.findAll()));
        return table;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
