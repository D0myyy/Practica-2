package ui;

import dao.*;
import database.DatabaseConnection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import model.enums.MetodaPlata;
import model.enums.StatusBilet;
import model.enums.StatusPlata;

public class AppUI extends Application {
    private static final String BG = "#0b1017";
    private static final String SIDEBAR = "#111827";
    private static final String PANEL = "#161d2a";
    private static final String CARD = "#1c2535";
    private static final String BORDER = "#253047";
    private static final String TEXT = "#f8fafc";
    private static final String MUTED = "#89a0c0";
    private static final String ACCENT = "#f59e0b";
    private static final String BLUE = "#3b82f6";
    private static final String GREEN = "#10b981";
    private static final String RED = "#ef4444";
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]+(?:[ -][A-Za-z]+)*$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\-\\s]{6,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final PasagerDAO pasagerDAO = new PasagerDAO();
    private final ZborDAO zborDAO = new ZborDAO();
    private final RezervareDAO rezervareDAO = new RezervareDAO();
    private final BiletDAO biletDAO = new BiletDAO();
    private final PlataDAO plataDAO = new PlataDAO();
    private final AeroportDAO aeroportDAO = new AeroportDAO();
    private final AvionDAO avionDAO = new AvionDAO();
    private final LocDAO locDAO = new LocDAO();

    private final ObservableList<Pasager> pasageri = FXCollections.observableArrayList();
    private final ObservableList<Zbor> zboruri = FXCollections.observableArrayList();
    private final ObservableList<Rezervare> rezervari = FXCollections.observableArrayList();
    private final ObservableList<Bilet> bilete = FXCollections.observableArrayList();
    private final ObservableList<Plata> plati = FXCollections.observableArrayList();
    private final ObservableList<Aeroport> aeroporturi = FXCollections.observableArrayList();
    private final ObservableList<Avion> avioane = FXCollections.observableArrayList();
    private final ObservableList<Loc> locuri = FXCollections.observableArrayList();
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "airbook-db-worker");
        thread.setDaemon(true);
        return thread;
    });

    private final List<Button> navButtons = new ArrayList<>();
    private final Map<String, String> navIcons = new LinkedHashMap<>();
    private BorderPane root;
    private BorderPane content;
    private Label dbStatus;
    private Stage stage;
    private String activeScreen = "Dashboard";
    private String lastReportTitle = "Raport";
    private String lastReportText = "";

    @Override
    public void start(Stage primaryStage) {
        Locale.setDefault(Locale.US);
        stage = primaryStage;
        stage.setTitle("AirBook - Rezervare Bilete Avion");
        stage.setMinWidth(1280);
        stage.setMinHeight(780);
        stage.getIcons().add(new Image(new File("ui/Logo.png").toURI().toString()));

        navIcons.put("Dashboard", "");
        navIcons.put("Zboruri", "");
        navIcons.put("Pasageri", "");
        navIcons.put("Rezervari", "");
        navIcons.put("Bilete", "");
        navIcons.put("Plati", "");
        navIcons.put("Aeroporturi", "");
        navIcons.put("Avioane", "");
        navIcons.put("Rapoarte", "");
        navIcons.put("Export", "");

        root = new BorderPane();
        root.setTop(topBar());
        root.setLeft(sidebar());
        content = new BorderPane();
        content.getStyleClass().add("app-bg");
        root.setCenter(content);

        Scene scene = new Scene(root, 1450, 860);
        scene.getStylesheets().add(new File("ui/css/style.css").toURI().toString());
        stage.setScene(scene);
        stage.show();

        showDashboard();
        loadAllAsync(true);
    }

    @Override
    public void stop() {
        dbExecutor.shutdownNow();
    }

    private Node topBar() {
        HBox bar = new HBox(16);
        bar.getStyleClass().add("topbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Label logo = new Label("AirBook");
        logo.getStyleClass().add("topbar-logo");
        Label version = new Label("Rezervare Bilete Avion");
        version.getStyleClass().add("topbar-subtitle");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Circle dot = new Circle(4, Color.web(GREEN));
        dbStatus = new Label("Rezervare_bilete_avion - SQL Server");
        dbStatus.getStyleClass().add("db-online");
        bar.getChildren().addAll(logo, divider(), version, spacer, dot, dbStatus);
        return bar;
    }

    private Node sidebar() {
        VBox side = new VBox(6);
        side.getStyleClass().add("sidebar");
        side.getChildren().addAll(section("Principal"), nav("Dashboard", this::showDashboard));
        side.getChildren().addAll(section("Management"), nav("Zboruri", this::showZboruri), nav("Pasageri", this::showPasageri),
                nav("Rezervari", this::showRezervari), nav("Bilete", this::showBilete), nav("Plati", this::showPlati));
        side.getChildren().addAll(section("Date"), nav("Aeroporturi", this::showAeroporturi), nav("Avioane", this::showAvioane));
        side.getChildren().addAll(section("Analitica"), nav("Rapoarte", this::showRapoarte), nav("Export", this::showExport));
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox status = new VBox(2);
        status.getStyleClass().add("db-card");
        status.getChildren().addAll(label("SQL Server", "db-title"), label("localhost:1433 - online", "db-sub"));
        side.getChildren().addAll(spacer, status);
        return side;
    }

    private Label section(String text) {
        Label label = new Label(text.toUpperCase());
        label.getStyleClass().add("nav-section");
        return label;
    }

    private Button nav(String text, Runnable action) {
        Button b = new Button(text + badgeText(text));
        b.getStyleClass().add("nav-button");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setOnAction(e -> {
            try {
                activeScreen = text;
                action.run();
            } catch (RuntimeException ex) {
                showError("Eroare aplicatie", rootMessage(ex));
            }
        });
        navButtons.add(b);
        return b;
    }

    private String badgeText(String text) {
        if ("Zboruri".equals(text) || "Pasageri".equals(text) || "Rezervari".equals(text) || "Bilete".equals(text)) {
            return "        " + countFor(text);
        }
        return "";
    }

    private int countFor(String text) {
        if ("Zboruri".equals(text)) return zboruri.size();
        if ("Pasageri".equals(text)) return pasageri.size();
        if ("Rezervari".equals(text)) return rezervari.size();
        if ("Bilete".equals(text)) return bilete.size();
        return 0;
    }

    private void markActive() {
        for (Button b : navButtons) {
            b.getStyleClass().remove("active-nav");
            if (b.getText().contains(activeScreen)) {
                b.getStyleClass().add("active-nav");
            }
        }
    }

    private DataSnapshot loadAll() {
        try {
            return new DataSnapshot(
                    aeroportDAO.findAll(),
                    avionDAO.findAll(),
                    locDAO.findAll(),
                    pasagerDAO.findAll(),
                    zborDAO.findAll(),
                    rezervareDAO.findAll(),
                    biletDAO.findAll(),
                    plataDAO.findAll());
        } catch (RuntimeException ex) {
            throw ex;
        }
    }

    private void loadAllAsync(boolean renderAfter) {
        setDbStatus("Se incarca datele...", "db-loading");
        Task<DataSnapshot> task = new Task<>() {
            @Override
            protected DataSnapshot call() {
                return loadAll();
            }
        };
        task.setOnSucceeded(e -> {
            applyData(task.getValue());
            setDbStatus("Rezervare_bilete_avion - SQL Server", "db-online");
            updateNavLabels();
            if (renderAfter) renderActiveScreen();
        });
        task.setOnFailed(e -> {
            setDbStatus("Eroare conectare SQL Server", "db-error");
            showError("Conectare BD", rootMessage(task.getException()));
        });
        dbExecutor.submit(task);
    }

    private void applyData(DataSnapshot data) {
        aeroporturi.setAll(data.aeroporturi);
        avioane.setAll(data.avioane);
        locuri.setAll(data.locuri);
        pasageri.setAll(data.pasageri);
        zboruri.setAll(data.zboruri);
        rezervari.setAll(data.rezervari);
        bilete.setAll(data.bilete);
        plati.setAll(data.plati);
    }

    private void setDbStatus(String text, String styleClass) {
        if (dbStatus == null) return;
        dbStatus.setText(text);
        dbStatus.getStyleClass().setAll(styleClass);
    }

    private void updateNavLabels() {
        for (Button button : navButtons) {
            for (String name : navIcons.keySet()) {
                if (button.getText().contains(name)) {
                    button.setText(name + badgeText(name));
                    break;
                }
            }
        }
    }

    private void refreshCurrent() {
        loadAllAsync(true);
    }

    private void renderActiveScreen() {
        if ("Zboruri".equals(activeScreen)) showZboruri();
        else if ("Pasageri".equals(activeScreen)) showPasageri();
        else if ("Rezervari".equals(activeScreen)) showRezervari();
        else if ("Bilete".equals(activeScreen)) showBilete();
        else if ("Plati".equals(activeScreen)) showPlati();
        else if ("Aeroporturi".equals(activeScreen)) showAeroporturi();
        else if ("Avioane".equals(activeScreen)) showAvioane();
        else if ("Rapoarte".equals(activeScreen)) showRapoarte();
        else if ("Export".equals(activeScreen)) showExport();
        else showDashboard();
    }

    private void setScreen(String icon, String title, String subtitle, Node toolbar, Node body, Node footer) {
        markActive();
        BorderPane screen = new BorderPane();
        screen.getStyleClass().add("screen");
        screen.setTop(screenTop(icon, title, subtitle, toolbar));
        screen.setCenter(body);
        if (footer != null) screen.setBottom(footer);
        content.setCenter(screen);
    }

    private Node screenTop(String icon, String title, String subtitle, Node toolbar) {
        VBox top = new VBox();
        VBox header = new VBox(3);
        header.getStyleClass().add("screen-header");
        Label t = new Label(icon == null || icon.isBlank() ? title : icon + " " + title);
        t.getStyleClass().add("screen-title");
        Label sub = new Label(subtitle);
        sub.getStyleClass().add("screen-subtitle");
        header.getChildren().addAll(t, sub);
        top.getChildren().add(header);
        if (toolbar != null) top.getChildren().add(toolbar);
        return top;
    }

    private HBox toolbar() {
        HBox box = new HBox(8);
        box.getStyleClass().add("toolbar");
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private VBox body() {
        VBox body = new VBox(14);
        body.getStyleClass().add("body");
        VBox.setVgrow(body, Priority.ALWAYS);
        return body;
    }

    private HBox footer(String text) {
        HBox footer = new HBox(8);
        footer.getStyleClass().add("footer");
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.getChildren().addAll(pageButton("1", true), pageButton("2", false), pageButton("3", false), pageButton("Next", false));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        footer.getChildren().addAll(spacer, label(text, "footer-text"));
        return footer;
    }

    private Button pageButton(String text, boolean active) {
        Button button = new Button(text);
        button.getStyleClass().add(active ? "page-button-active" : "page-button");
        return button;
    }

    private void showDashboard() {
        activeScreen = "Dashboard";
        VBox main = body();
        HBox stats = new HBox(12);
        stats.getChildren().addAll(statCard("Zboruri", String.valueOf(zboruri.size()), "Zboruri active", "+3 astazi", ACCENT),
                statCard("Pasageri", String.valueOf(pasageri.size()), "Pasageri inregistrati", "+2 astazi", BLUE),
                statCard("Bilete", String.valueOf(bilete.size()), "Bilete vandute", "+8 saptamana asta", GREEN),
                statCard("Venit", money(totalVenit()), "Venit total (MDL)", "+15.2%", RED));

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(58);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(42);
        grid.getColumnConstraints().addAll(c1, c2);
        grid.add(panel("Zboruri Recente", recentFlights()), 0, 0);
        grid.add(panel("Statistici", statisticsPanel()), 1, 0);
        VBox.setVgrow(grid, Priority.ALWAYS);
        main.getChildren().addAll(stats, grid);
        setScreen("", "Dashboard", "Vedere generala a sistemului de rezervare", null, main, null);
    }

    private VBox statCard(String icon, String value, String label, String change, String color) {
        VBox card = new VBox(7);
        card.getStyleClass().add("stat-card");
        card.setStyle("-fx-border-color: " + color + " " + BORDER + " " + BORDER + " " + BORDER + "; -fx-border-width: 2 1 1 1;");
        HBox.setHgrow(card, Priority.ALWAYS);
        Label v = label(value, "stat-value");
        Label l = label(label, "stat-label");
        Label c = label(change, "stat-change");
        if (icon != null && !icon.isBlank()) {
            card.getChildren().add(label(icon, "stat-icon"));
        }
        card.getChildren().addAll(v, l, c);
        return card;
    }

    private Node recentFlights() {
        VBox list = new VBox(0);
        for (Zbor zbor : zboruri.stream().limit(7).collect(Collectors.toList())) {
            HBox row = new HBox(14);
            row.getStyleClass().add("flight-row");
            row.setAlignment(Pos.CENTER_LEFT);
            Label numar = label(zbor.getNumarZbor(), "code");
            Label plane = label("-", "muted");
            VBox route = new VBox(2);
            route.getChildren().addAll(label(zbor.getPlecare().getCodIata() + " -> " + zbor.getSosire().getCodIata(), "route"),
                    label(date(zbor.getDataPlecare()) + " - " + time(zbor.getDataPlecare()) + " - " + time(zbor.getDataSosire()), "mono-muted"));
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            row.getChildren().addAll(numar, plane, route, spacer, badge(statusForFlight(zbor), statusColor(statusForFlight(zbor))));
            list.getChildren().add(row);
        }
        return list;
    }

    private Node statisticsPanel() {
        VBox box = new VBox(14);
        box.getChildren().add(barChart());
        box.getChildren().addAll(miniStat("Rata confirmare", confirmationRate() + "%", GREEN),
                miniStat("Aeroporturi active", String.valueOf(aeroporturi.size()), TEXT),
                miniStat("Avioane disponibile", String.valueOf(avioane.size()), TEXT),
                miniStat("Plati card", paymentCardRate() + "%", BLUE));
        return box;
    }

    private Node barChart() {
        HBox bars = new HBox(10);
        bars.setAlignment(Pos.BOTTOM_CENTER);
        bars.getStyleClass().add("bar-chart");
        int[] values = {6, 9, 5, 11, 8, 12, 7};
        String[] labels = {"L", "Ma", "Mi", "J", "V", "S", "D"};
        for (int i = 0; i < values.length; i++) {
            VBox col = new VBox(4);
            col.setAlignment(Pos.BOTTOM_CENTER);
            Label val = label(String.valueOf(values[i]), "bar-val");
            Region bar = new Region();
            bar.getStyleClass().add(i == 5 ? "bar-highlight" : "bar");
            bar.setPrefSize(66, values[i] * 7);
            Label lab = label(labels[i], "mono-muted");
            col.getChildren().addAll(val, bar, lab);
            HBox.setHgrow(col, Priority.ALWAYS);
            bars.getChildren().add(col);
        }
        return bars;
    }

    private HBox miniStat(String label, String value, String color) {
        HBox row = new HBox();
        row.getStyleClass().add("mini-stat");
        Label l = label(label, "muted");
        Label v = label(value, "mini-value");
        v.setStyle("-fx-text-fill: " + color + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(l, spacer, v);
        return row;
    }

    private void showZboruri() {
        activeScreen = "Zboruri";
        TableView<Zbor> table = table();
        table.getColumns().addAll(col("#", z -> String.valueOf(z.getIdZbor()), 55),
                col("NR. ZBOR", Zbor::getNumarZbor, 110), col("PLECARE", z -> z.getPlecare().getCodIata() + " - " + z.getPlecare().getOras(), 210),
                col("SOSIRE", z -> z.getSosire().getCodIata() + " - " + z.getSosire().getOras(), 210),
                col("AVION", z -> z.getAvion().getModel(), 180), col("DATA PLECARE", z -> fmt(z.getDataPlecare()), 170),
                col("DATA SOSIRE", z -> fmt(z.getDataSosire()), 170), col("STATUS", z -> statusForFlight(z), 120));
        FilteredList<Zbor> filtered = new FilteredList<>(zboruri, z -> true);
        table.setItems(filtered);

        TextField search = search("Cauta numar zbor, aeroport...");
        ComboBox<String> status = filter("Toate zborurile", "Active", "Programate", "Boarding");
        ComboBox<String> destinatie = filter("Toate destinatiile");
        aeroporturi.stream().map(Aeroport::getOras).distinct().sorted().forEach(destinatie.getItems()::add);
        Runnable apply = () -> filtered.setPredicate(z -> match(search, z.getNumarZbor(), z.getPlecare().toString(), z.getSosire().toString(), z.getAvion().toString())
                && ("Toate zborurile".equals(status.getValue()) || statusForFlight(z).equals(status.getValue()))
                && ("Toate destinatiile".equals(destinatie.getValue()) || z.getSosire().getOras().equals(destinatie.getValue())));
        wireFilters(apply, search, status, destinatie);

        HBox tb = toolbar();
        tb.getChildren().addAll(button("Zbor nou", ACCENT, () -> zborDialog(null)), button("Actualizare", CARD, this::refreshCurrent),
            button("Editeaza", BLUE, () -> zborDialog(table.getSelectionModel().getSelectedItem())),
            button("Sterge", RED, () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "zbor", z -> zborDAO.delete(z.getIdZbor()))),
            spacer(), search, destinatie, status);
        setScreen("", "Zboruri", "Gestionarea tuturor zborurilor din sistem", tb, wrapTable(table), footer(zboruri.size() + " inregistrari - Pagina 1 din 7"));
    }

    private void showPasageri() {
        activeScreen = "Pasageri";
        TableView<Pasager> table = table();
        table.getColumns().addAll(col("#", p -> String.valueOf(p.getIdPasager()), 70), col("NUME", Pasager::getNume, 190),
                col("PRENUME", Pasager::getPrenume, 190), col("EMAIL", Pasager::getEmail, 330), col("TELEFON", Pasager::getTelefon, 190),
                col("REZERVARI", p -> String.valueOf(countTicketsForPassenger(p.getIdPasager())), 120));
        FilteredList<Pasager> filtered = new FilteredList<>(pasageri, p -> true);
        table.setItems(filtered);
        TextField search = search("Cauta dupa nume, email, tel...");
        ComboBox<String> filter = filter("Toti pasagerii", "Cu rezervari", "Fara rezervari");
        Runnable apply = () -> filtered.setPredicate(p -> match(search, p.getNume(), p.getPrenume(), p.getEmail(), p.getTelefon())
                && ("Toti pasagerii".equals(filter.getValue())
                || ("Cu rezervari".equals(filter.getValue()) && countTicketsForPassenger(p.getIdPasager()) > 0)
                || ("Fara rezervari".equals(filter.getValue()) && countTicketsForPassenger(p.getIdPasager()) == 0)));
        wireFilters(apply, search, filter);
        HBox tb = toolbar();
        tb.getChildren().addAll(button("Pasager nou", ACCENT, () -> pasagerDialog(null)), button("Actualizare", CARD, this::refreshCurrent),
                button("Editeaza", BLUE, () -> pasagerDialog(table.getSelectionModel().getSelectedItem())),
                button("Sterge", RED, () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "pasager", p -> pasagerDAO.delete(p.getIdPasager()))),
                spacer(), search, filter);
        setScreen("", "Pasageri", "Baza de date a pasagerilor inregistrati", tb, wrapTable(table), footer(pasageri.size() + " inregistrari - Pagina 1 din 7"));
    }

    private void showRezervari() {
        activeScreen = "Rezervari";
        TableView<Rezervare> table = table();
        table.getColumns().addAll(col("#", r -> String.valueOf(r.getIdRezervare()), 70), col("COD REZERVARE", Rezervare::getCodRezervare, 170),
                col("DATA REZERVARE", r -> date(r.getDataRezervare()), 170), col("PASAGER", this::ticketPassenger, 220),
                col("ZBOR", this::ticketFlight, 120), col("LOC", this::ticketSeat, 90),
                col("PRET (MDL)", r -> money(ticketPrice(r)), 140), col("STATUS BILET", this::ticketStatus, 150),
                col("PLATA", r -> paymentStatus(r.getIdRezervare()), 120));
        FilteredList<Rezervare> filtered = new FilteredList<>(rezervari, r -> true);
        table.setItems(filtered);
        TextField search = search("Cauta cod rezervare...");
        ComboBox<String> filter = filter("Toate", "Confirmate", "Anulate", "Platite", "Refuzate");
        Runnable apply = () -> filtered.setPredicate(r -> match(search, r.getCodRezervare(), ticketPassenger(r), ticketFlight(r))
                && ("Toate".equals(filter.getValue())
                || ("Confirmate".equals(filter.getValue()) && "CONFIRMAT".equals(ticketStatus(r)))
                || ("Anulate".equals(filter.getValue()) && "ANULAT".equals(ticketStatus(r)))
                || ("Platite".equals(filter.getValue()) && paymentStatus(r.getIdRezervare()).contains("PLATIT"))
                || ("Refuzate".equals(filter.getValue()) && paymentStatus(r.getIdRezervare()).contains("REFUZAT"))));
        wireFilters(apply, search, filter);
        HBox tb = toolbar();
        tb.getChildren().addAll(button("Rezervare noua", ACCENT, () -> rezervareDialog(null)), button("Actualizare", CARD, this::refreshCurrent),
                button("Editeaza", BLUE, () -> rezervareDialog(table.getSelectionModel().getSelectedItem())),
                button("Sterge", RED, () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "rezervare", r -> rezervareDAO.delete(r.getIdRezervare()))),
                spacer(), search, filter);
        setScreen("", "Rezervari", "Managementul rezervarilor de bilete", tb, wrapTable(table), footer(rezervari.size() + " inregistrari - Pagina 1 din 7"));
    }

    private void showBilete() {
        activeScreen = "Bilete";
        TableView<Bilet> table = table();
        table.getColumns().addAll(col("#", b -> String.valueOf(b.getIdBilet()), 70), col("REZERVARE", b -> b.getRezervare().getCodRezervare(), 150),
                col("PASAGER", b -> b.getPasager().getNumeComplet(), 250), col("ZBOR", b -> b.getZbor().getNumarZbor(), 130),
                col("LOC", b -> b.getLoc().getNumarLoc(), 100), col("CLASA", b -> b.getLoc().getClasaLoc().name(), 130),
                col("PRET", b -> money(b.getPret()), 130), col("STATUS", b -> b.getStatus().name(), 150));
        FilteredList<Bilet> filtered = new FilteredList<>(bilete, b -> true);
        table.setItems(filtered);
        TextField search = search("Cauta bilet, pasager, zbor...");
        ComboBox<String> clasa = filter("Toate clasele", "ECONOMY", "BUSINESS");
        ComboBox<String> status = filter("Toate statusurile", "CONFIRMAT", "ANULAT", "IN_ASTEPTARE");
        Runnable apply = () -> filtered.setPredicate(b -> match(search, b.getRezervare().getCodRezervare(), b.getPasager().getNumeComplet(), b.getZbor().getNumarZbor())
                && ("Toate clasele".equals(clasa.getValue()) || b.getLoc().getClasaLoc().name().equals(clasa.getValue()))
                && ("Toate statusurile".equals(status.getValue()) || b.getStatus().name().equals(status.getValue())));
        wireFilters(apply, search, clasa, status);
        HBox tb = toolbar();
        tb.getChildren().addAll(button("Bilet nou", ACCENT, () -> biletDialog(null)), button("Actualizare", CARD, this::refreshCurrent),
                button("Export CSV", BLUE, () -> exportLines("bilete.csv", "Id,Rezervare,Pasager,Zbor,Loc,Pret,Status\n", bilete.stream().map(Bilet::toCSV).collect(Collectors.toList()))),
                button("Editeaza", BLUE, () -> biletDialog(table.getSelectionModel().getSelectedItem())),
                button("Sterge", RED, () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "bilet", b -> biletDAO.delete(b.getIdBilet()))),
                spacer(), search, clasa, status);
        setScreen("", "Bilete", "Vizualizare si gestionare bilete de avion", tb, wrapTable(table), footer(bilete.size() + " inregistrari - Pagina 1 din 7"));
    }

    private void showPlati() {
        activeScreen = "Plati";
        TableView<Plata> table = table();
        table.getColumns().addAll(col("#", p -> String.valueOf(p.getIdPlata()), 80), col("REZERVARE", p -> p.getRezervare().getCodRezervare(), 220),
                col("SUMA (MDL)", p -> money(p.getSuma()), 200), col("METODA", p -> p.getMetoda().name(), 180),
                col("STATUS", p -> p.getStatus().name(), 180), col("DATA PLATA", p -> fmt(p.getDataPlata()), 220));
        FilteredList<Plata> filtered = new FilteredList<>(plati, p -> true);
        table.setItems(filtered);
        TextField search = search("Cauta rezervare...");
        ComboBox<String> metoda = filter("Toate metodele", "CARD", "CASH", "TRANSFER");
        ComboBox<String> status = filter("Toate statusurile", "PLATIT", "REFUZAT", "IN_ASTEPTARE");
        Runnable apply = () -> filtered.setPredicate(p -> match(search, p.getRezervare().getCodRezervare(), p.getMetoda().name(), p.getStatus().name())
                && ("Toate metodele".equals(metoda.getValue()) || p.getMetoda().name().equals(metoda.getValue()))
                && ("Toate statusurile".equals(status.getValue()) || p.getStatus().name().equals(status.getValue())));
        wireFilters(apply, search, metoda, status);
        HBox tb = toolbar();
        tb.getChildren().addAll(button("Plata noua", ACCENT, () -> plataDialog(null)), button("Actualizare", CARD, this::refreshCurrent),
                button("Editeaza", BLUE, () -> plataDialog(table.getSelectionModel().getSelectedItem())),
                button("Sterge", RED, () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "plata", p -> plataDAO.delete(p.getIdPlata()))),
                spacer(), search, metoda, status);
        setScreen("", "Plati", "Evidenta platilor si tranzactiilor", tb, wrapTable(table), footer(plati.size() + " inregistrari - Pagina 1 din 7"));
    }

    private void showAeroporturi() {
        activeScreen = "Aeroporturi";
        TableView<Aeroport> table = table();
        table.getColumns().addAll(col("#", a -> String.valueOf(a.getIdAeroport()), 70), col("COD IATA", Aeroport::getCodIata, 140),
                col("NUME AEROPORT", Aeroport::getNume, 430), col("ORAS", Aeroport::getOras, 220), col("TARA", Aeroport::getTara, 220),
                col("ZBORURI", a -> String.valueOf(countFlightsForAirport(a.getIdAeroport())), 120));
        FilteredList<Aeroport> filtered = new FilteredList<>(aeroporturi, a -> true);
        table.setItems(filtered);
        TextField search = search("Cauta dupa nume, cod IATA...");
        ComboBox<String> tara = filter("Toate tarile");
        aeroporturi.stream().map(Aeroport::getTara).distinct().sorted().forEach(tara.getItems()::add);
        Runnable apply = () -> filtered.setPredicate(a -> match(search, a.getNume(), a.getCodIata(), a.getOras(), a.getTara())
                && ("Toate tarile".equals(tara.getValue()) || a.getTara().equals(tara.getValue())));
        wireFilters(apply, search, tara);
        HBox tb = toolbar();
        tb.getChildren().addAll(button("Aeroport nou", ACCENT, () -> aeroportDialog(null)), button("Actualizare", CARD, this::refreshCurrent),
                button("Editeaza", BLUE, () -> aeroportDialog(table.getSelectionModel().getSelectedItem())),
                button("Sterge", RED, () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "aeroport", a -> aeroportDAO.delete(a.getIdAeroport()))),
                spacer(), search, tara);
        setScreen("", "Aeroporturi", "Lista tuturor aeroporturilor din sistem", tb, wrapTable(table), footer(aeroporturi.size() + " inregistrari"));
    }

    private void showAvioane() {
        activeScreen = "Avioane";
        TableView<Avion> table = table();
        table.getColumns().addAll(col("#", a -> String.valueOf(a.getIdAvion()), 70), col("MODEL", Avion::getModel, 320),
                col("CAPACITATE", a -> a.getCapacitate() + " pasageri", 220), col("LOCURI DISPONIBILE", a -> freeSeatsText(a), 260),
                col("ZBORURI", a -> String.valueOf(countFlightsForPlane(a.getIdAvion())), 120));
        FilteredList<Avion> filtered = new FilteredList<>(avioane, a -> true);
        table.setItems(filtered);
        TextField search = search("Cauta model avion...");
        ComboBox<String> cap = filter("Toate", "Sub 100 locuri", "100-200 locuri", "Peste 200 locuri");
        Runnable apply = () -> filtered.setPredicate(a -> match(search, a.getModel())
                && ("Toate".equals(cap.getValue())
                || ("Sub 100 locuri".equals(cap.getValue()) && a.getCapacitate() < 100)
                || ("100-200 locuri".equals(cap.getValue()) && a.getCapacitate() >= 100 && a.getCapacitate() <= 200)
                || ("Peste 200 locuri".equals(cap.getValue()) && a.getCapacitate() > 200)));
        wireFilters(apply, search, cap);
        HBox tb = toolbar();
        tb.getChildren().addAll(button("Avion nou", ACCENT, () -> avionDialog(null)), button("Actualizare", CARD, this::refreshCurrent),
                button("Editeaza", BLUE, () -> avionDialog(table.getSelectionModel().getSelectedItem())),
                button("Sterge", RED, () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "avion", a -> avionDAO.delete(a.getIdAvion()))),
                spacer(), search, cap);
        setScreen("", "Avioane", "Flota de avioane inregistrata in sistem", tb, wrapTable(table), footer(avioane.size() + " inregistrari"));
    }

    private void showRapoarte() {
        activeScreen = "Rapoarte";
        VBox main = body();
        GridPane cards = new GridPane();
        cards.setHgap(14);
        cards.setVgap(14);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(33.33);
            cards.getColumnConstraints().add(c);
        }
        cards.add(reportCard("", "Raport Zboruri", "Lista zborurilor cu bilete vandute, locuri libere si venituri generate.", "zbor  pasageri  venit", () -> runReport("Raport Zboruri", reportFlightsSql())), 0, 0);
        cards.add(reportCard("", "Raport Pasageri", "Statistici despre pasageri: rezervari, total cheltuit si frecventa zboruri.", "pasager  rezervare  total", () -> runReport("Raport Pasageri", reportPassengersSql())), 1, 0);
        cards.add(reportCard("", "Raport Financiar", "Analiza veniturilor pe metoda de plata si statusul platilor.", "venituri  plati  metoda", () -> runReport("Raport Financiar", reportFinanceSql())), 2, 0);
        cards.add(reportCard("", "Raport Destinatii", "Cele mai populare destinatii si numarul de zboruri pe oras.", "destinatie  ruta  popularitate", () -> runReport("Raport Destinatii", reportDestinationsSql())), 0, 1);
        cards.add(reportCard("", "Raport Rezervari Lunare", "Evolutia rezervarilor grupata pe luna calendaristica.", "luna  trend  comparatie", () -> runReport("Raport Rezervari Lunare", reportMonthlySql())), 1, 1);
        cards.add(reportCard("", "Raport Flota", "Utilizarea avioanelor, numar zboruri si venituri per avion.", "avion  utilizare  eficienta", () -> runReport("Raport Flota", reportFleetSql())), 2, 1);
        VBox result = new VBox(10);
        result.getStyleClass().add("report-result");
        Label title = label("Selecteaza un raport pentru afisare", "report-title");
        TableView<ReportRow> table = reportTable(new ReportData(new ArrayList<>(), new ArrayList<>()));
        result.getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        main.getChildren().addAll(cards, result);
        setScreen("", "Rapoarte", "Generare rapoarte statistice si analitice", null, main, null);
    }

    private Node reportCard(String icon, String title, String desc, String tag, Runnable action) {
        VBox card = new VBox(10);
        card.getStyleClass().add("report-card");
        card.setOnMouseClicked(e -> {
            try {
                action.run();
            } catch (RuntimeException ex) {
                showError("Eroare raport", rootMessage(ex));
            }
        });
        if (icon != null && !icon.isBlank()) {
            card.getChildren().add(label(icon, "report-icon"));
        }
        card.getChildren().addAll(label(title, "report-card-title"), label(desc, "report-desc"), label(tag, "report-tag"));
        return card;
    }

    private void runReport(String title, String sql) {
        setDbStatus("Se genereaza raportul...", "db-loading");
        Task<ReportData> task = new Task<>() {
            @Override
            protected ReportData call() {
                return queryReportData(sql);
            }
        };
        task.setOnSucceeded(e -> {
            setDbStatus("Rezervare_bilete_avion - SQL Server", "db-online");
            renderReport(title, task.getValue());
        });
        task.setOnFailed(e -> {
            setDbStatus("Eroare SQL Server", "db-error");
            showError("Eroare raport", rootMessage(task.getException()));
        });
        dbExecutor.submit(task);
    }

    private void renderReport(String title, ReportData data) {
        lastReportTitle = title;
        lastReportText = reportToText(data);
        VBox main = body();
        GridPane cards = new GridPane();
        cards.setHgap(14);
        cards.setVgap(14);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(33.33);
            cards.getColumnConstraints().add(c);
        }
        cards.add(reportCard("", "Raport Zboruri", "Lista zborurilor cu bilete vandute, locuri libere si venituri generate.", "zbor  pasageri  venit", () -> runReport("Raport Zboruri", reportFlightsSql())), 0, 0);
        cards.add(reportCard("", "Raport Pasageri", "Statistici despre pasageri: rezervari, total cheltuit si frecventa zboruri.", "pasager  rezervare  total", () -> runReport("Raport Pasageri", reportPassengersSql())), 1, 0);
        cards.add(reportCard("", "Raport Financiar", "Analiza veniturilor pe metoda de plata si statusul platilor.", "venituri  plati  metoda", () -> runReport("Raport Financiar", reportFinanceSql())), 2, 0);
        cards.add(reportCard("", "Raport Destinatii", "Cele mai populare destinatii si numarul de zboruri pe oras.", "destinatie  ruta  popularitate", () -> runReport("Raport Destinatii", reportDestinationsSql())), 0, 1);
        cards.add(reportCard("", "Raport Rezervari Lunare", "Evolutia rezervarilor grupata pe luna calendaristica.", "luna  trend  comparatie", () -> runReport("Raport Rezervari Lunare", reportMonthlySql())), 1, 1);
        cards.add(reportCard("", "Raport Flota", "Utilizarea avioanelor, numar zboruri si venituri per avion.", "avion  utilizare  eficienta", () -> runReport("Raport Flota", reportFleetSql())), 2, 1);
        HBox resultHeader = new HBox(8);
        resultHeader.setAlignment(Pos.CENTER_LEFT);
        resultHeader.getChildren().addAll(label(title + " - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), "report-title"),
                spacer(), button("Export CSV", BLUE, () -> exportText(slug(title) + ".csv", lastReportText)),
                button("Export TXT", CARD, () -> exportText(slug(title) + ".txt", lastReportText)));
        VBox result = new VBox(8);
        result.getStyleClass().add("report-result");
        TableView<ReportRow> table = reportTable(data);
        VBox.setVgrow(table, Priority.ALWAYS);
        result.getChildren().addAll(resultHeader, table);
        main.getChildren().addAll(cards, result);
        setScreen("", "Rapoarte", "Generare rapoarte statistice si analitice", null, main, null);
    }

    private TableView<ReportRow> reportTable(ReportData data) {
        TableView<ReportRow> table = table();
        int index = 0;
        for (String columnName : data.columns) {
            final int columnIndex = index;
            table.getColumns().add(col(columnName.toUpperCase(), row -> row.value(columnIndex), 180));
            index++;
        }
        table.setItems(FXCollections.observableArrayList(data.rows));
        table.setMinHeight(240);
        return table;
    }

    private void showExport() {
        activeScreen = "Export";
        VBox main = body();
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        VBox config = new VBox(12);
        config.getStyleClass().add("export-card");
        ComboBox<String> table = filter("Bilete (cu detalii zbor si pasager)", "Pasageri", "Zboruri", "Rezervari", "Plati", "Aeroporturi", "Avioane");
        ComboBox<String> format = filter("CSV", "TXT");
        ComboBox<String> separator = filter("Virgula (,)", "Punct si virgula (;)", "Tab");
        DatePicker from = new DatePicker(LocalDate.now().minusDays(7));
        DatePicker to = new DatePicker(LocalDate.now());
        from.getStyleClass().add("input");
        to.getStyleClass().add("input");
        Button export = button("Exporta Acum", ACCENT, () -> exportSelected(table.getValue(), format.getValue()));
        config.getChildren().addAll(label("Configurare Export", "report-title"), formLabel("TABEL DE EXPORTAT"), table,
                formLabel("FORMAT FISIER"), format, formLabel("DE LA DATA"), from, formLabel("PANA LA DATA"), to,
                formLabel("SEPARATOR (CSV)"), separator, export);
        VBox recent = new VBox(12);
        recent.getStyleClass().add("export-card");
        recent.getChildren().addAll(label("Exporturi Recente", "report-title"), exportRecent("", "bilete_all_07052026.csv", "50 randuri - 4.2 KB - 07.05.2026"),
                exportRecent("", "raport_zboruri_05052026.txt", "50 randuri - 2.8 KB - 05.05.2026"),
                exportRecent("", "pasageri_03052026.csv", "50 randuri - 3.1 KB - 03.05.2026"));
        VBox preview = new VBox(10);
        preview.getStyleClass().add("export-card");
        TextArea area = new TextArea("IdBilet,CodRezervare,Pasager,Zbor,Loc,Pret,Status\n1,RES1001,Achirus Constantin,MD101,5A,189.00,Confirmat\n2,RES1002,Batitchi Nicolae,MD103,7B,245.00,Confirmat\n3,RES1003,Besliu Dan,MD105,12C,110.00,Anulat");
        area.getStyleClass().add("report");
        preview.getChildren().addAll(label("Previzualizare CSV", "report-title"), area);
        grid.add(config, 0, 0, 1, 2);
        grid.add(recent, 1, 0);
        grid.add(preview, 1, 1);
        main.getChildren().add(grid);
        setScreen("", "Export Date", "Exportati datele sistemului in format CSV sau TXT", null, main, null);
    }

    private Node exportRecent(String icon, String file, String sub) {
        HBox row = new HBox(10);
        row.getStyleClass().add("export-row");
        row.setAlignment(Pos.CENTER_LEFT);
        VBox text = new VBox(2);
        text.getChildren().addAll(label(file, "route"), label(sub, "mono-muted"));
        if (icon != null && !icon.isBlank()) {
            row.getChildren().add(label(icon, "report-icon"));
        }
        row.getChildren().addAll(text, spacer(), button("Descarca", CARD, () -> {}));
        return row;
    }

    private void exportSelected(String selectedTable, String format) {
        String name = selectedTable.toLowerCase().replace(" ", "_").replace("(", "").replace(")", "");
        boolean csv = "CSV".equals(format);
        if (selectedTable.startsWith("Bilete")) exportLines("bilete." + (csv ? "csv" : "txt"), csv ? "Id,Rezervare,Pasager,Zbor,Loc,Pret,Status\n" : "", bilete.stream().map(csv ? Bilet::toCSV : Bilet::toTXT).collect(Collectors.toList()));
        else if ("Pasageri".equals(selectedTable)) exportLines("pasageri." + (csv ? "csv" : "txt"), csv ? "Id,Nume,Prenume,Email,Telefon\n" : "", pasageri.stream().map(csv ? Pasager::toCSV : Pasager::toTXT).collect(Collectors.toList()));
        else if ("Rezervari".equals(selectedTable)) exportLines("rezervari." + (csv ? "csv" : "txt"), csv ? "Id,Cod,Data,Total,Bilete\n" : "", rezervari.stream().map(csv ? Rezervare::toCSV : Rezervare::toTXT).collect(Collectors.toList()));
        else if ("Plati".equals(selectedTable)) exportLines("plati." + (csv ? "csv" : "txt"), csv ? "Id,Rezervare,Suma,Metoda,Status,Data\n" : "", plati.stream().map(csv ? Plata::toCSV : Plata::toTXT).collect(Collectors.toList()));
        else exportText(name + "." + (csv ? "csv" : "txt"), selectedTable + System.lineSeparator() + "Export generat: " + LocalDateTime.now());
    }

    private void pasagerDialog(Pasager selected) {
        Dialog<Pasager> dialog = baseDialog(selected == null ? "Adaugare Pasager" : "Editare Pasager");
        GridPane form = form();
        TextField nume = input(selected == null ? "" : selected.getNume());
        TextField prenume = input(selected == null ? "" : selected.getPrenume());
        TextField email = input(selected == null ? "" : selected.getEmail());
        TextField telefon = input(selected == null ? "" : selected.getTelefon());
        addRow(form, 0, "Nume *", nume, "Prenume *", prenume);
        addRow(form, 1, "Email *", email, "Telefon *", telefon);
        dialog.getDialogPane().setContent(form);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, e -> {
            List<String> errors = new ArrayList<>();
            try {
                requireName(nume, "Numele");
            } catch (IllegalArgumentException ex) {
                errors.add(ex.getMessage());
            }
            try {
                requireName(prenume, "Prenumele");
            } catch (IllegalArgumentException ex) {
                errors.add(ex.getMessage());
            }
            try {
                requireEmail(email);
            } catch (IllegalArgumentException ex) {
                errors.add(ex.getMessage());
            }
            try {
                requirePhone(telefon, "Telefonul");
            } catch (IllegalArgumentException ex) {
                errors.add(ex.getMessage());
            }
            if (!errors.isEmpty()) {
                showError("Date invalide", String.join(System.lineSeparator(), errors));
                e.consume();
            }
        });
        dialog.setResultConverter(btn -> btn == ButtonType.OK
            ? new Pasager(selected == null ? 0 : selected.getIdPasager(),
            requireName(nume, "Numele"),
            requireName(prenume, "Prenumele"),
            requireEmail(email),
            requirePhone(telefon, "Telefonul"))
            : null);
        saveDialog(dialog, p -> { if (selected == null) pasagerDAO.create(p); else pasagerDAO.update(p); });
    }

    private void zborDialog(Zbor selected) {
        Dialog<Zbor> dialog = baseDialog(selected == null ? "Adaugare Zbor Nou" : "Editare Zbor");
        GridPane form = form();
        TextField numar = input(selected == null ? "" : selected.getNumarZbor());
        ComboBox<Avion> avion = combo(avioane);
        ComboBox<Aeroport> plecare = combo(aeroporturi);
        ComboBox<Aeroport> sosire = combo(aeroporturi);
        DatePicker dataP = datePicker(selected == null ? LocalDate.now().plusDays(1) : selected.getDataPlecare().toLocalDate());
        DatePicker dataS = datePicker(selected == null ? LocalDate.now().plusDays(1) : selected.getDataSosire().toLocalDate());
        TextField oraP = input(selected == null ? "08:00" : time(selected.getDataPlecare()));
        TextField oraS = input(selected == null ? "10:00" : time(selected.getDataSosire()));
        if (selected == null) {
            avion.getSelectionModel().selectFirst();
            plecare.getSelectionModel().selectFirst();
            if (aeroporturi.size() > 1) sosire.getSelectionModel().select(1);
        } else {
            selectById(avion, selected.getAvion().getIdAvion(), Avion::getIdAvion);
            selectById(plecare, selected.getPlecare().getIdAeroport(), Aeroport::getIdAeroport);
            selectById(sosire, selected.getSosire().getIdAeroport(), Aeroport::getIdAeroport);
        }
        addRow(form, 0, "Numar Zbor *", numar, "Avion *", avion);
        addRow(form, 1, "Aeroport Plecare *", plecare, "Aeroport Sosire *", sosire);
        addRow(form, 2, "Data Plecarii *", dataP, "Ora Plecarii *", oraP);
        addRow(form, 3, "Data Sosirii *", dataS, "Ora Sosirii *", oraS);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            return new Zbor(selected == null ? 0 : selected.getIdZbor(), numar.getText(), plecare.getValue(), sosire.getValue(), avion.getValue(),
                    readDateTime(dataP, oraP, "Data/Ora plecarii"),
                    readDateTime(dataS, oraS, "Data/Ora sosirii"));
        });
        saveDialog(dialog, z -> { if (selected == null) zborDAO.create(z); else zborDAO.update(z); });
    }

    private void rezervareDialog(Rezervare selected) {
        Dialog<Rezervare> dialog = baseDialog(selected == null ? "Rezervare Noua" : "Editare Rezervare");
        GridPane form = form();
        TextField cod = input(selected == null ? "RES" + System.currentTimeMillis() % 100000 : selected.getCodRezervare());
        DatePicker data = datePicker(selected == null ? LocalDate.now() : selected.getDataRezervare().toLocalDate());
        TextField ora = input(selected == null ? "12:00" : time(selected.getDataRezervare()));
        addRow(form, 0, "Cod Rezervare *", cod, "Data *", data);
        addRow(form, 1, "Ora *", ora, "", label("", "muted"));
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? new Rezervare(selected == null ? 0 : selected.getIdRezervare(), cod.getText(), readDateTime(data, ora, "Data/Ora rezervarii")) : null);
        saveDialog(dialog, r -> { if (selected == null) rezervareDAO.create(r); else rezervareDAO.update(r); });
    }

    private void biletDialog(Bilet selected) {
        Dialog<Bilet> dialog = baseDialog(selected == null ? "Bilet Nou" : "Editare Bilet");
        GridPane form = form();
        ComboBox<Rezervare> rezervare = combo(rezervari);
        ComboBox<Pasager> pasager = combo(pasageri);
        ComboBox<Zbor> zbor = combo(zboruri);
        ComboBox<Loc> loc = combo(FXCollections.observableArrayList());
        TextField pret = input(selected == null ? "" : String.format(Locale.US, "%.2f", selected.getPret()));
        ComboBox<StatusBilet> status = combo(FXCollections.observableArrayList(StatusBilet.values()));
        zbor.setOnAction(e -> {
            try {
                loc.getItems().clear();
                if (zbor.getValue() != null) loc.getItems().setAll(locuri.stream()
                        .filter(item -> item.getIdAvion() == zbor.getValue().getAvion().getIdAvion())
                        .collect(Collectors.toList()));
                loc.getSelectionModel().selectFirst();
            } catch (RuntimeException ex) {
                showError("Eroare incarcare locuri", rootMessage(ex));
            }
        });
        if (selected == null) {
            rezervare.getSelectionModel().selectFirst();
            pasager.getSelectionModel().selectFirst();
            zbor.getSelectionModel().selectFirst();
            status.getSelectionModel().select(StatusBilet.CONFIRMAT);
        } else {
            selectById(rezervare, selected.getRezervare().getIdRezervare(), Rezervare::getIdRezervare);
            selectById(pasager, selected.getPasager().getIdPasager(), Pasager::getIdPasager);
            selectById(zbor, selected.getZbor().getIdZbor(), Zbor::getIdZbor);
            loc.getItems().setAll(locuri.stream()
                    .filter(item -> item.getIdAvion() == selected.getZbor().getAvion().getIdAvion())
                    .collect(Collectors.toList()));
            selectById(loc, selected.getLoc().getIdLoc(), Loc::getIdLoc);
            status.getSelectionModel().select(selected.getStatus());
        }
        addRow(form, 0, "Rezervare *", rezervare, "Pasager *", pasager);
        addRow(form, 1, "Zbor *", zbor, "Loc *", loc);
        addRow(form, 2, "Pret (MDL) *", pret, "Status *", status);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? new Bilet(selected == null ? 0 : selected.getIdBilet(), requiredCombo(rezervare, "Rezervarea"), requiredCombo(pasager, "Pasagerul"), requiredCombo(zbor, "Zborul"), requiredCombo(loc, "Locul"), parseNonNegativeDouble(pret, "Pretul"), requiredCombo(status, "Statusul")) : null);
        saveDialog(dialog, b -> { if (selected == null) biletDAO.create(b); else biletDAO.update(b); });
    }

    private void plataDialog(Plata selected) {
        Dialog<Plata> dialog = baseDialog(selected == null ? "Plata Noua" : "Editare Plata");
        GridPane form = form();
        ComboBox<Rezervare> rezervare = combo(rezervari);
        TextField suma = input(selected == null ? "" : String.format(Locale.US, "%.2f", selected.getSuma()));
        ComboBox<MetodaPlata> metoda = combo(FXCollections.observableArrayList(MetodaPlata.values()));
        ComboBox<StatusPlata> status = combo(FXCollections.observableArrayList(StatusPlata.values()));
        if (selected == null) {
            rezervare.getSelectionModel().selectFirst();
            metoda.getSelectionModel().select(MetodaPlata.CARD);
            status.getSelectionModel().select(StatusPlata.PLATIT);
        } else {
            selectById(rezervare, selected.getRezervare().getIdRezervare(), Rezervare::getIdRezervare);
            metoda.getSelectionModel().select(selected.getMetoda());
            status.getSelectionModel().select(selected.getStatus());
        }
        addRow(form, 0, "Rezervare *", rezervare, "Suma *", suma);
        addRow(form, 1, "Metoda *", metoda, "Status *", status);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? new Plata(selected == null ? 0 : selected.getIdPlata(), requiredCombo(rezervare, "Rezervarea"), parseNonNegativeDouble(suma, "Suma"), requiredCombo(metoda, "Metoda de plata"), requiredCombo(status, "Statusul platii"), LocalDateTime.now()) : null);
        saveDialog(dialog, p -> { if (selected == null) plataDAO.create(p); else plataDAO.update(p); });
    }

    private void aeroportDialog(Aeroport selected) {
        Dialog<Aeroport> dialog = baseDialog(selected == null ? "Aeroport Nou" : "Editare Aeroport");
        GridPane form = form();
        TextField nume = input(selected == null ? "" : selected.getNume());
        TextField cod = input(selected == null ? "" : selected.getCodIata());
        TextField oras = input(selected == null ? "" : selected.getOras());
        TextField tara = input(selected == null ? "" : selected.getTara());
        addRow(form, 0, "Nume Aeroport *", nume, "Cod IATA *", cod);
        addRow(form, 1, "Oras *", oras, "Tara *", tara);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? new Aeroport(selected == null ? 0 : selected.getIdAeroport(), nume.getText(), oras.getText(), tara.getText(), cod.getText()) : null);
        saveDialog(dialog, a -> { if (selected == null) aeroportDAO.create(a); else aeroportDAO.update(a); });
    }

    private void avionDialog(Avion selected) {
        Dialog<Avion> dialog = baseDialog(selected == null ? "Avion Nou" : "Editare Avion");
        GridPane form = form();
        TextField model = input(selected == null ? "" : selected.getModel());
        TextField capacitate = input(selected == null ? "" : String.valueOf(selected.getCapacitate()));
        DatePicker data = datePicker(LocalDate.now());
        addRow(form, 0, "Model avion *", model, "Capacitate *", capacitate);
        addRow(form, 1, "Data inregistrarii", data, "", label("Informativa pentru formular", "muted"));
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? new Avion(selected == null ? 0 : selected.getIdAvion(), model.getText(), parsePositiveInt(capacitate, "Capacitatea")) : null);
        saveDialog(dialog, a -> { if (selected == null) avionDAO.create(a); else avionDAO.update(a); });
    }

    private <T> TableView<T> table() {
        TableView<T> table = new TableView<>();
        table.getStyleClass().add("air-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private Node wrapTable(TableView<?> table) {
        VBox box = body();
        box.getChildren().add(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }

    private <T> TableColumn<T, String> col(String title, Value<T> value, int width) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setCellValueFactory(data -> new SimpleStringProperty(value.get(data.getValue())));
        c.setPrefWidth(width);
        return c;
    }

    private TextField search(String prompt) {
        TextField field = new TextField();
        field.getStyleClass().add("input");
        field.setPromptText(prompt);
        field.setMinWidth(230);
        return field;
    }

    private ComboBox<String> filter(String first, String... values) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getStyleClass().add("input");
        combo.getItems().add(first);
        combo.getItems().addAll(values);
        combo.getSelectionModel().selectFirst();
        combo.setMinWidth(145);
        return combo;
    }

    private void wireFilters(Runnable apply, TextField search, ComboBox<?>... combos) {
        search.textProperty().addListener((obs, old, value) -> apply.run());
        for (ComboBox<?> combo : combos) combo.setOnAction(e -> apply.run());
    }

    private boolean match(TextField field, String... values) {
        String query = field.getText() == null ? "" : field.getText().trim().toLowerCase();
        if (query.isEmpty()) return true;
        for (String value : values) {
            if (value != null && value.toLowerCase().contains(query)) return true;
        }
        return false;
    }

    private Button button(String text, String color, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().add("action-button");
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: " + (ACCENT.equals(color) ? "#111827" : TEXT) + ";");
        b.setOnAction(e -> {
            try {
                action.run();
            } catch (RuntimeException ex) {
                showError("Eroare aplicatie", rootMessage(ex));
            }
        });
        return b;
    }

    private Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private Node divider() {
        Region r = new Region();
        r.getStyleClass().add("divider");
        return r;
    }

    private Node panel(String title, Node content) {
        VBox box = new VBox(10);
        box.getStyleClass().add("panel");
        Label label = label(title, "panel-title");
        box.getChildren().addAll(label, content);
        VBox.setVgrow(content, Priority.ALWAYS);
        return box;
    }

    private Badge badge(String text, String color) {
        return new Badge(text, color);
    }

    private String statusColor(String status) {
        if ("Active".equals(status)) return GREEN;
        if ("Boarding".equals(status)) return ACCENT;
        if ("Anulat".equals(status)) return RED;
        return BLUE;
    }

    private String statusForFlight(Zbor zbor) {
        if (zbor.getDataPlecare().isBefore(LocalDateTime.now())) return "Active";
        if (zbor.getDataPlecare().toLocalDate().equals(LocalDate.now().plusDays(1))) return "Boarding";
        return "Programate";
    }

    private DatePicker datePicker(LocalDate date) {
        DatePicker picker = new DatePicker(date);
        picker.getStyleClass().add("input");
        return picker;
    }

    private LocalDateTime readDateTime(DatePicker datePicker, TextField timeField, String fieldName) {
        if (datePicker.getValue() == null) {
            throw new IllegalArgumentException(fieldName + " este obligatorie.");
        }
        try {
            return LocalDateTime.of(datePicker.getValue(), LocalTime.parse(requiredText(timeField, fieldName)));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " trebuie sa aiba ora in format HH:mm.");
        }
    }

    private int parsePositiveInt(TextField field, String fieldName) {
        try {
            int value = Integer.parseInt(requiredText(field, fieldName));
            if (value <= 0) throw new IllegalArgumentException(fieldName + " trebuie sa fie un numar pozitiv.");
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " trebuie sa fie un numar intreg valid.");
        }
    }

    private double parseNonNegativeDouble(TextField field, String fieldName) {
        try {
            double value = Double.parseDouble(requiredText(field, fieldName).replace(',', '.'));
            if (value < 0) throw new IllegalArgumentException(fieldName + " nu poate fi negativ.");
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " trebuie sa fie un numar valid.");
        }
    }

    private String requiredText(TextField field, String fieldName) {
        String value = field.getText();
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " este obligatoriu.");
        }
        return value.trim();
    }


    private String requireName(TextField field, String fieldName) {
        String value = requiredText(field, fieldName);
        if (!NAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(fieldName + " trebuie sa contina doar litere, spatii sau cratime.");
        }
        return value;
    }

    private String requireEmail(TextField field) {
        String value = requiredText(field, "Emailul");
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Emailul trebuie sa contina caracterul @.");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Format email invalid.");
        }
        return value;
    }

    private String requirePhone(TextField field, String fieldName) {
        String value = requiredText(field, fieldName);
        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(fieldName + " trebuie sa contina doar cifre si simboluri uzuale (+, -, spatiu).");
        }
        int digits = value.replaceAll("\\D", "").length();
        if (digits < 6) {
            throw new IllegalArgumentException(fieldName + " trebuie sa aiba cel putin 6 cifre.");
        }
        return value;
    }

    private <T> T requiredCombo(ComboBox<T> combo, String fieldName) {
        T value = combo.getValue();
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " este obligatoriu.");
        }
        return value;
    }

    private Label formLabel(String text) {
        return label(text, "form-label");
    }

    private GridPane form() {
        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(10);
        form.getStyleClass().add("form-grid");
        return form;
    }

    private void addRow(GridPane form, int row, String l1, Node n1, String l2, Node n2) {
        form.add(formLabel(l1), 0, row * 2);
        form.add(n1, 0, row * 2 + 1);
        form.add(formLabel(l2), 1, row * 2);
        form.add(n2, 1, row * 2 + 1);
    }

    private TextField input(String value) {
        TextField input = new TextField(value);
        input.getStyleClass().add("input");
        return input;
    }

    private <T> ComboBox<T> combo(ObservableList<T> values) {
        ComboBox<T> combo = new ComboBox<>(values);
        combo.getStyleClass().add("input");
        combo.setMinWidth(260);
        return combo;
    }

    private <T> void selectById(ComboBox<T> combo, int id, IdGetter<T> getter) {
        combo.getItems().stream().filter(item -> getter.getId(item) == id).findFirst().ifPresent(combo.getSelectionModel()::select);
    }

    private <T> Dialog<T> baseDialog(String text) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(text);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);
        return dialog;
    }

    private void styleDialog(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(new File("ui/css/style.css").toURI().toString());
        pane.getStyleClass().add("dialog-dark");
    }

    private <T> void saveDialog(Dialog<T> dialog, Saver<T> saver) {
        try {
            dialog.showAndWait().ifPresent(value -> {
                runDbOperation("Se salveaza datele...", () -> {
                    saver.save(value);
                }, "Operatie reusita", "Datele au fost salvate.", "Eroare salvare");
            });
        } catch (RuntimeException ex) {
            showError("Date invalide", rootMessage(ex));
        }
    }

    private <T> void deleteSelected(T selected, String name, Deleter<T> deleter) {
        if (selected == null) {
            showError("Selectie lipsa", "Selectati o inregistrare pentru stergere.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Sigur doriti sa stergeti acest " + name + "?", ButtonType.YES, ButtonType.NO);
        styleDialog(confirm);
        confirm.showAndWait().filter(ButtonType.YES::equals).ifPresent(btn -> {
            runDbOperation("Se sterge inregistrarea...", () -> {
                deleter.delete(selected);
            }, "Sters", "Inregistrarea a fost stearsa.", "Eroare stergere");
        });
    }

    private void runDbOperation(String workingStatus, Runnable operation, String successHeader, String successMessage, String errorHeader) {
        setDbStatus(workingStatus, "db-loading");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                operation.run();
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showInfo(successHeader, successMessage);
            loadAllAsync(true);
        });
        task.setOnFailed(e -> {
            setDbStatus("Eroare SQL Server", "db-error");
            showError(errorHeader, rootMessage(task.getException()));
        });
        dbExecutor.submit(task);
    }

    private void showInfo(String header, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        a.setHeaderText(header);
        styleDialog(a);
        a.showAndWait();
    }

    private void showError(String header, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        a.setHeaderText(header);
        styleDialog(a);
        a.showAndWait();
    }

    private void exportLines(String defaultName, String header, List<String> rows) {
        exportText(defaultName, header + String.join(System.lineSeparator(), rows));
    }

    private void exportText(String defaultName, String text) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(defaultName);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text/CSV", "*.txt", "*.csv"));
        File file = chooser.showSaveDialog(stage);
        if (file == null) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(text);
            showInfo("Export reusit", "Fisier salvat: " + file.getAbsolutePath());
        } catch (IOException ex) {
            showError("Eroare export", ex.getMessage());
        } catch (RuntimeException ex) {
            showError("Eroare export", rootMessage(ex));
        }
    }

    private ReportData queryReportData(String sql) {
        List<String> columns = new ArrayList<>();
        List<ReportRow> rows = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement st = con.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) columns.add(meta.getColumnLabel(i));
            while (rs.next()) {
                List<String> values = new ArrayList<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) values.add(rs.getString(i));
                rows.add(new ReportRow(values));
            }
        } catch (SQLException | RuntimeException ex) {
            columns.add("Eroare");
            rows.add(new ReportRow(FXCollections.observableArrayList(rootMessage(ex))));
        }
        return new ReportData(columns, rows);
    }

    private String reportToText(ReportData data) {
        StringBuilder builder = new StringBuilder(lastReportTitle).append(System.lineSeparator());
        builder.append(String.join(",", data.columns)).append(System.lineSeparator());
        for (ReportRow row : data.rows) builder.append(String.join(",", row.values)).append(System.lineSeparator());
        return builder.toString();
    }

    private Bilet firstTicket(Rezervare rezervare) {
        return bilete.stream().filter(b -> b.getRezervare().getIdRezervare() == rezervare.getIdRezervare()).findFirst().orElse(null);
    }

    private String ticketPassenger(Rezervare rezervare) {
        Bilet ticket = firstTicket(rezervare);
        return ticket == null || ticket.getPasager() == null ? "Fara bilet" : ticket.getPasager().getNumeComplet();
    }

    private String ticketFlight(Rezervare rezervare) {
        Bilet ticket = firstTicket(rezervare);
        return ticket == null || ticket.getZbor() == null ? "-" : ticket.getZbor().getNumarZbor();
    }

    private String ticketSeat(Rezervare rezervare) {
        Bilet ticket = firstTicket(rezervare);
        return ticket == null || ticket.getLoc() == null ? "-" : ticket.getLoc().getNumarLoc();
    }

    private double ticketPrice(Rezervare rezervare) {
        Bilet ticket = firstTicket(rezervare);
        return ticket == null ? 0 : ticket.getPret();
    }

    private String ticketStatus(Rezervare rezervare) {
        Bilet ticket = firstTicket(rezervare);
        return ticket == null || ticket.getStatus() == null ? "FARA_BILET" : ticket.getStatus().name();
    }

    private String paymentStatus(int idRezervare) {
        return plati.stream().filter(p -> p.getRezervare().getIdRezervare() == idRezervare).map(p -> p.getStatus().name()).findFirst().orElse("NEPLATIT");
    }

    private int countTicketsForPassenger(int idPasager) {
        return (int) bilete.stream().filter(b -> b.getPasager().getIdPasager() == idPasager).count();
    }

    private int countFlightsForAirport(int idAeroport) {
        return (int) zboruri.stream().filter(z -> z.getPlecare().getIdAeroport() == idAeroport || z.getSosire().getIdAeroport() == idAeroport).count();
    }

    private int countFlightsForPlane(int idAvion) {
        return (int) zboruri.stream().filter(z -> z.getAvion().getIdAvion() == idAvion).count();
    }

    private String freeSeatsText(Avion avion) {
        int sold = (int) bilete.stream().filter(b -> b.getZbor().getAvion().getIdAvion() == avion.getIdAvion() && b.getStatus() == StatusBilet.CONFIRMAT).count();
        return Math.max(0, avion.getCapacitate() - sold) + " libere";
    }

    private double totalVenit() {
        return bilete.stream().filter(b -> b.getStatus() == StatusBilet.CONFIRMAT).mapToDouble(Bilet::getPret).sum();
    }

    private int confirmationRate() {
        if (bilete.isEmpty()) return 0;
        return (int) Math.round(bilete.stream().filter(b -> b.getStatus() == StatusBilet.CONFIRMAT).count() * 100.0 / bilete.size());
    }

    private int paymentCardRate() {
        if (plati.isEmpty()) return 0;
        return (int) Math.round(plati.stream().filter(p -> p.getMetoda() == MetodaPlata.CARD).count() * 100.0 / plati.size());
    }

    private String fmt(LocalDateTime value) {
        return value == null ? "" : value.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    private String date(LocalDateTime value) {
        return value == null ? "" : value.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private String time(LocalDateTime value) {
        return value == null ? "" : value.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String money(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private String rootMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null) current = current.getCause();
        return current.getMessage() == null ? ex.getMessage() : current.getMessage();
    }

    private String slug(String value) {
        return value.toLowerCase().replace(" ", "_").replace("", "a").replace("", "a").replace("", "i");
    }

    private String reportFlightsSql() {
        return "SELECT z.NumarZbor AS Zbor, ap.CodIATA + ' -> ' + asr.CodIATA AS Ruta, CONVERT(varchar(10), z.DataPlecare, 104) AS Data, COUNT(b.IdBilet) AS Bilete, SUM(b.Pret) AS Venit FROM Zboruri z JOIN Aeroporturi ap ON z.IdAeroportPlecare = ap.IdAeroport JOIN Aeroporturi asr ON z.IdAeroportSosire = asr.IdAeroport LEFT JOIN Bilete b ON z.IdZbor = b.IdZbor GROUP BY z.NumarZbor, ap.CodIATA, asr.CodIATA, z.DataPlecare ORDER BY z.DataPlecare";
    }

    private String reportPassengersSql() {
        return "SELECT TOP 20 p.Nume + ' ' + p.Prenume AS Pasager, COUNT(b.IdBilet) AS Bilete, ISNULL(SUM(b.Pret),0) AS Total, MAX(r.DataRezervare) AS UltimaRezervare FROM Pasageri p LEFT JOIN Bilete b ON p.IdPasager = b.IdPasager LEFT JOIN Rezervari r ON b.IdRezervare = r.IdRezervare GROUP BY p.Nume, p.Prenume ORDER BY Total DESC";
    }

    private String reportFinanceSql() {
        return "SELECT Metoda, Status, COUNT(*) AS Tranzactii, SUM(Suma) AS Total FROM Plati GROUP BY Metoda, Status ORDER BY Total DESC";
    }

    private String reportDestinationsSql() {
        return "SELECT TOP 20 a.Oras AS Destinatie, a.Tara, COUNT(z.IdZbor) AS Zboruri, COUNT(b.IdBilet) AS Bilete FROM Aeroporturi a JOIN Zboruri z ON a.IdAeroport = z.IdAeroportSosire LEFT JOIN Bilete b ON z.IdZbor = b.IdZbor GROUP BY a.Oras, a.Tara ORDER BY Zboruri DESC";
    }

    private String reportMonthlySql() {
        return "SELECT FORMAT(DataRezervare, 'yyyy-MM') AS Luna, COUNT(*) AS Rezervari FROM Rezervari GROUP BY FORMAT(DataRezervare, 'yyyy-MM') ORDER BY Luna";
    }

    private String reportFleetSql() {
        return "SELECT av.Model, av.Capacitate, COUNT(DISTINCT z.IdZbor) AS Zboruri, COUNT(b.IdBilet) AS Bilete, ISNULL(SUM(b.Pret),0) AS Venit FROM Avioane av LEFT JOIN Zboruri z ON av.IdAvion = z.IdAvion LEFT JOIN Bilete b ON z.IdZbor = b.IdZbor GROUP BY av.Model, av.Capacitate ORDER BY Venit DESC";
    }

    @FunctionalInterface private interface Value<T> { String get(T item); }
    @FunctionalInterface private interface Saver<T> { void save(T item); }
    @FunctionalInterface private interface Deleter<T> { void delete(T item); }
    @FunctionalInterface private interface IdGetter<T> { int getId(T item); }

    private static class DataSnapshot {
        final List<Aeroport> aeroporturi;
        final List<Avion> avioane;
        final List<Loc> locuri;
        final List<Pasager> pasageri;
        final List<Zbor> zboruri;
        final List<Rezervare> rezervari;
        final List<Bilet> bilete;
        final List<Plata> plati;

        DataSnapshot(List<Aeroport> aeroporturi, List<Avion> avioane, List<Loc> locuri, List<Pasager> pasageri,
                     List<Zbor> zboruri, List<Rezervare> rezervari, List<Bilet> bilete, List<Plata> plati) {
            this.aeroporturi = aeroporturi;
            this.avioane = avioane;
            this.locuri = locuri;
            this.pasageri = pasageri;
            this.zboruri = zboruri;
            this.rezervari = rezervari;
            this.bilete = bilete;
            this.plati = plati;
        }
    }

    private static class ReportData {
        final List<String> columns;
        final List<ReportRow> rows;
        ReportData(List<String> columns, List<ReportRow> rows) {
            this.columns = columns;
            this.rows = rows;
        }
    }

    private static class ReportRow {
        final List<String> values;
        ReportRow(List<String> values) {
            this.values = values;
        }
        String value(int index) {
            return index < values.size() ? values.get(index) : "";
        }
    }

    private static class Badge extends Label {
        Badge(String text, String color) {
            super(text);
            getStyleClass().add("badge");
            setStyle("-fx-text-fill: " + color + "; -fx-background-color: rgba(16,185,129,0.13);");
        }
    }
}

