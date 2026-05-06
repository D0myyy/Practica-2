import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class Main extends Application {

	private static final String DEFAULT_SERVER = "L204_PC14";
	private static final String DEFAULT_DATABASE = "Rezervare_bilete_avion";
	private static final String DEFAULT_PORT = "1433";

	private final Label statusLabel = new Label("Not connected.");
	private final TextArea outputArea = new TextArea();

	@Override
	public void start(Stage stage) {
		Label title = new Label("SQL Server Connection");
		title.setFont(Font.font("System", FontWeight.BOLD, 26));
		title.setTextFill(Color.WHITE);

		Label subtitle = new Label("Connect to the Rezervare_bilete_avion database using Windows Authentication.");
		subtitle.setTextFill(Color.web("#e6eefc"));

		GridPane form = new GridPane();
		form.setHgap(12);
		form.setVgap(12);

		TextField serverField = new TextField(DEFAULT_SERVER);
		TextField portField = new TextField(DEFAULT_PORT);
		TextField databaseField = new TextField(DEFAULT_DATABASE);

		ComboBox<String> engineField = new ComboBox<>();
		engineField.getItems().add("SQL Server");
		engineField.getSelectionModel().selectFirst();
		engineField.setDisable(true);

		ComboBox<String> authField = new ComboBox<>();
		authField.getItems().add("Windows Authentication");
		authField.getSelectionModel().selectFirst();
		authField.setDisable(true);

		TextField userField = new TextField();
		userField.setPromptText("Disabled for Windows Authentication");
		userField.setDisable(true);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Disabled for Windows Authentication");
		passwordField.setDisable(true);

		CheckBox trustServerCertificate = new CheckBox("Trust server certificate");
		trustServerCertificate.setSelected(true);

		form.addRow(0, createFieldLabel("Server name"), serverField);
		form.addRow(1, createFieldLabel("Port"), portField);
		form.addRow(2, createFieldLabel("Database name"), databaseField);
		form.addRow(3, createFieldLabel("Database engine"), engineField);
		form.addRow(4, createFieldLabel("Authentication"), authField);
		form.addRow(5, createFieldLabel("User"), userField);
		form.addRow(6, createFieldLabel("Password"), passwordField);
		form.addRow(7, new Label(), trustServerCertificate);

		Button connectButton = new Button("Connect");
		connectButton.setDefaultButton(true);
		connectButton.setOnAction(event -> connect(
				serverField.getText().trim(),
				portField.getText().trim(),
				databaseField.getText().trim(),
				trustServerCertificate.isSelected(),
				connectButton));

		statusLabel.setTextFill(Color.web("#dbeafe"));

		outputArea.setEditable(false);
		outputArea.setWrapText(true);
		outputArea.setPrefRowCount(12);

		HBox buttonRow = new HBox(12, connectButton, statusLabel);
		buttonRow.setAlignment(Pos.CENTER_LEFT);

		VBox content = new VBox(16, title, subtitle, form, buttonRow, outputArea);
		content.setPadding(new Insets(24));
		content.setFillWidth(true);

		BorderPane root = new BorderPane();
		root.setCenter(new ScrollPane(content));
		root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f172a, #1e293b, #111827);");

		Scene scene = new Scene(root, 760, 620);
		stage.setTitle("SQL Server Login");
		stage.setScene(scene);
		stage.show();

		outputArea.setText("Ready to connect. The SQL script in BazaDeDate.sql must already be executed in SQL Server.");
	}

	private Label createFieldLabel(String text) {
		Label label = new Label(text);
		label.setTextFill(Color.WHITE);
		return label;
	}

	private void connect(String server, String port, String database, boolean trustServerCertificate, Button connectButton) {
		if (server.isBlank() || port.isBlank() || database.isBlank()) {
			statusLabel.setText("Server, port, and database are required.");
			outputArea.appendText("\n" + now() + " Missing required connection values.");
			return;
		}

		connectButton.setDisable(true);
		statusLabel.setText("Connecting...");
		outputArea.appendText("\n" + now() + " Connecting to " + server + ":" + port + " / " + database + "...");

		Task<String> task = new Task<>() {
			@Override
			protected String call() throws Exception {
				try {
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				} catch (ClassNotFoundException missingDriver) {
					throw new IllegalStateException("Microsoft SQL Server JDBC driver not found. Place the driver jar in lib and add it to the runtime classpath.", missingDriver);
				}

				String jdbcUrl = "jdbc:sqlserver://" + server + ":" + port
						+ ";databaseName=" + database
						+ ";integratedSecurity=true"
						+ ";authenticationScheme=NTLM"
						+ ";encrypt=true"
						+ ";trustServerCertificate=" + trustServerCertificate;

				try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
					DatabaseMetaData metaData = connection.getMetaData();
					StringBuilder result = new StringBuilder();
					result.append("Connected successfully to ")
							.append(metaData.getDatabaseProductName())
							.append(' ')
							.append(metaData.getDatabaseProductVersion())
							.append('\n');
					result.append("User: ").append(metaData.getUserName()).append('\n');
					result.append("Catalog: ").append(connection.getCatalog()).append('\n');
					result.append("Tables found:\n");

					try (ResultSet tables = metaData.getTables(database, null, "%", new String[]{"TABLE"})) {
						boolean foundAny = false;
						while (tables.next()) {
							foundAny = true;
							result.append(" - ")
									.append(tables.getString("TABLE_NAME"))
									.append('\n');
						}

						if (!foundAny) {
							result.append(" - No tables were found.\n");
						}
					}

					return result.toString();
				}
			}
		};

		task.setOnSucceeded(event -> {
			statusLabel.setText("Connected.");
			outputArea.appendText("\n" + now() + " Connection successful.");
			outputArea.appendText("\n\n" + task.getValue());
			connectButton.setDisable(false);
		});

		task.setOnFailed(event -> {
			Throwable error = task.getException();
			statusLabel.setText("Connection failed.");
			String message = error.getMessage();
			if (error.getCause() != null && error.getCause().getMessage() != null) {
				message = error.getCause().getMessage();
			}
			outputArea.appendText("\n" + now() + " Connection failed: " + message);
			connectButton.setDisable(false);
		});

		Thread thread = new Thread(task, "sql-connection-task");
		thread.setDaemon(true);
		thread.start();
	}

	private String now() {
		return "[" + LocalDateTime.now().withNano(0) + "]";
	}

	public static void main(String[] args) {
		launch(args);
	}
}
