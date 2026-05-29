import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class ReceptionistDashboard {

    private BorderPane root;
    private Receptionist receptionist;
    private ChatClient chatClient;

    public ReceptionistDashboard(Receptionist receptionist) {
        this.receptionist = receptionist;
        root = new BorderPane();
        root.setStyle("-fx-background-color: #080f1e;");

        chatClient = new ChatClient("Reception-" + receptionist.getUsername(), msg -> {/* handled per-panel */});
        buildUI();
    }

    private void buildUI() {
        root.setLeft(buildSidebar());
        root.setTop(buildTopBar());
        showHome();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #050c1a, #080f20);" +
            "-fx-border-color: rgba(201,168,76,0.15); -fx-border-width: 0 1 0 0;");

        VBox logo = new VBox(4);
        logo.setPadding(new Insets(28, 20, 24, 20));
        Label l1 = new Label("AIN SHAMS");
        l1.setStyle("-fx-font-family: Georgia; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #c9a84c;");
        Label l2 = new Label("RECEPTION DESK");
        l2.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(201,168,76,0.6); -fx-letter-spacing: 2px;");
        logo.getChildren().addAll(l1, l2);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(201,168,76,0.15);");

        VBox userInfo = new VBox(4);
        userInfo.setPadding(new Insets(16, 20, 16, 20));
        Label icon = new Label("🛎");
        icon.setStyle("-fx-font-size: 28px;");
        Label name = new Label(receptionist.getUsername());
        name.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label role = new Label("Receptionist");
        role.setStyle("-fx-text-fill: rgba(201,168,76,0.7); -fx-font-size: 11px;");
        userInfo.getChildren().addAll(icon, name, role);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: rgba(201,168,76,0.1);");

        VBox nav = new VBox(4);
        nav.setPadding(new Insets(12));
        nav.getChildren().addAll(
            makeSideBtn("🏠", "Overview",         () -> showHome()),
            makeSideBtn("✅", "Check-In Guest",    () -> showCheckIn()),
            makeSideBtn("🚪", "Check-Out Guest",   () -> showCheckOut()),
            makeSideBtn("📋", "All Reservations",  () -> showAllReservations()),
            makeSideBtn("💬", "Live Chat",          () -> showChat())
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = makeSideBtn("↩", "Logout", () -> {
            if (chatClient != null) chatClient.disconnect();
            SceneManager.showLogin();
        });
        logoutBtn.setStyle(logoutBtn.getStyle() + "-fx-text-fill: #ef4444;");

        sidebar.getChildren().addAll(logo, sep, userInfo, sep2, nav, spacer, logoutBtn);
        return sidebar;
    }

    private Button makeSideBtn(String icon, String text, Runnable action) {
        Button btn = new Button(icon + "  " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        String base = "-fx-background-color: transparent; -fx-text-fill: rgba(200,220,255,0.75);" +
            "-fx-font-size: 13px; -fx-padding: 11 16; -fx-background-radius: 8; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(201,168,76,0.1); -fx-text-fill: #c9a84c;" +
            "-fx-font-size: 13px; -fx-padding: 11 16; -fx-background-radius: 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(16, 30, 16, 30));
        bar.setStyle("-fx-background-color: rgba(8,15,30,0.95);" +
            "-fx-border-color: rgba(201,168,76,0.1); -fx-border-width: 0 0 1 0;");
        Label title = new Label("Reception Desk");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: Georgia;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label badge = new Label("🛎  Receptionist");
        badge.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 12px;");
        bar.getChildren().addAll(title, sp, badge);
        return bar;
    }

    private void setContent(javafx.scene.Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        root.setCenter(scroll);
        FadeTransition ft = new FadeTransition(Duration.millis(180), content);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    // ───────────────────────── HOME ─────────────────────────
    private void showHome() {
        VBox page = new VBox(24);
        page.setPadding(new Insets(32));

        Label welcome = new Label("Welcome, " + receptionist.getUsername() + " 🛎");
        welcome.setStyle("-fx-font-family: Georgia; -fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label sub = new Label("Manage guest check-ins and check-outs from here.");
        sub.setStyle("-fx-text-fill: rgba(180,200,255,0.6); -fx-font-size: 14px;");

        // Stats row
        long pending = HotelDatabase.reservations.stream()
            .filter(r -> r.getStatus() == Reservation.ReservationStatus.PENDING).count();
        long confirmed = HotelDatabase.reservations.stream()
            .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED).count();
        long available = HotelDatabase.rooms.stream()
            .filter(Room::isAvailable).count();

        HBox stats = new HBox(16);
        stats.getChildren().addAll(
            makeStatCard("⏳", "Pending Check-Ins",  String.valueOf(pending),   "#f59e0b"),
            makeStatCard("✅", "Confirmed Stays",     String.valueOf(confirmed), "#10b981"),
            makeStatCard("🛏", "Available Rooms",     String.valueOf(available), "#00d4ff"),
            makeStatCard("📋", "Total Reservations",  String.valueOf(HotelDatabase.reservations.size()), "#a78bfa")
        );
        for (javafx.scene.Node n : stats.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // Quick action buttons
        Label qaTitle = new Label("Quick Actions");
        qaTitle.setStyle("-fx-text-fill: rgba(200,220,255,0.8); -fx-font-size: 15px; -fx-font-weight: bold;");

        HBox quickActions = new HBox(12);
        Button ciBtn = makeActionBtn("✅  Check-In a Guest",   "#0f2a1a", "#10b981");
        Button coBtn = makeActionBtn("🚪  Check-Out a Guest",  "#1e1a0a", "#f59e0b");
        Button vrBtn = makeActionBtn("📋  View All Reservations", "#0d1a2a", "#00d4ff");
        ciBtn.setOnAction(e -> showCheckIn());
        coBtn.setOnAction(e -> showCheckOut());
        vrBtn.setOnAction(e -> showAllReservations());
        quickActions.getChildren().addAll(ciBtn, coBtn, vrBtn);
        HBox.setHgrow(ciBtn, Priority.ALWAYS);
        HBox.setHgrow(coBtn, Priority.ALWAYS);
        HBox.setHgrow(vrBtn, Priority.ALWAYS);

        // Pending reservations list
        Label pendingTitle = new Label("Pending Check-Ins");
        pendingTitle.setStyle("-fx-text-fill: rgba(200,220,255,0.8); -fx-font-size: 15px; -fx-font-weight: bold;");

        VBox pendingList = new VBox(8);
        boolean anyPending = false;
        for (Reservation res : HotelDatabase.reservations) {
            if (res.getStatus() == Reservation.ReservationStatus.PENDING) {
                pendingList.getChildren().add(makeReservationRow(res, true));
                anyPending = true;
            }
        }
        if (!anyPending) {
            Label none = new Label("No pending check-ins at this time.");
            none.setStyle("-fx-text-fill: rgba(180,200,255,0.4); -fx-font-size: 13px;");
            pendingList.getChildren().add(none);
        }

        page.getChildren().addAll(welcome, sub, stats, qaTitle, quickActions, pendingTitle, pendingList);
        setContent(page);
    }

    private VBox makeStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 12;" +
            "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-border-width: 1;");
        Label ic = new Label(icon);
        ic.setStyle("-fx-font-size: 24px;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: rgba(180,200,255,0.55); -fx-font-size: 12px;");
        card.getChildren().addAll(ic, v, l);
        return card;
    }

    private Button makeActionBtn(String text, String bg, String color) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(16));
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + color +
            "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10;" +
            "-fx-border-color: " + color + "33; -fx-border-radius: 10; -fx-border-width: 1; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }


    private void showCheckIn() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        Label title = new Label("Check-In Guest");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label sub = new Label("Confirm a PENDING reservation to check the guest in.");
        sub.setStyle("-fx-text-fill: rgba(180,200,255,0.55); -fx-font-size: 13px;");


        HBox searchBar = new HBox(12);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        TextField idField = new TextField();
        idField.setPromptText("Enter Reservation ID (e.g. A3F9B21C)");
        styleInput(idField);
        idField.setPrefWidth(320);
        Button searchBtn = new Button("Check In");
        searchBtn.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #34d399);" +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;" +
            "-fx-padding: 10 22; -fx-cursor: hand;");

        Label resultLabel = new Label("");
        resultLabel.setWrapText(true);
        resultLabel.setStyle("-fx-font-size: 13px;");

        searchBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                resultLabel.setStyle("-fx-text-fill: #ef4444;");
                resultLabel.setText("Please enter a reservation ID.");
                return;
            }
            boolean found = false;
            for (Reservation res : HotelDatabase.reservations) {
                if (res.getReservationId().equalsIgnoreCase(id)) {
                    found = true;
                    if (res.getStatus() == Reservation.ReservationStatus.PENDING) {
                        receptionist.checkIn(res);
                        HotelDatabase.persistReservation(res);
                        resultLabel.setStyle("-fx-text-fill: #10b981;");
                        resultLabel.setText("✓ Guest checked in successfully!\n" +
                            "Reservation #" + res.getReservationId() +
                            " is now CONFIRMED.\nGuest: " + res.getGuest().getUsername() +
                            " · Room: " + res.getRoom().getRoomNumber());
                        idField.clear();
                        showCheckIn();
                    } else {
                        resultLabel.setStyle("-fx-text-fill: #f59e0b;");
                        resultLabel.setText("Cannot check in. Status is: " + res.getStatus() +
                            "\nOnly PENDING reservations can be checked in.");
                    }
                    break;
                }
            }
            if (!found) {
                resultLabel.setStyle("-fx-text-fill: #ef4444;");
                resultLabel.setText("No reservation found with ID: " + id);
            }
        });
        idField.setOnAction(e -> searchBtn.fire());

        searchBar.getChildren().addAll(idField, searchBtn);

        // All PENDING reservations list
        Label listTitle = new Label("All Pending Reservations");
        listTitle.setStyle("-fx-text-fill: rgba(200,220,255,0.8); -fx-font-size: 15px; -fx-font-weight: bold;");

        VBox pendingList = new VBox(10);
        boolean any = false;
        for (Reservation res : HotelDatabase.reservations) {
            if (res.getStatus() == Reservation.ReservationStatus.PENDING) {
                VBox card = makeReservationCard(res);

                Button checkInBtn = new Button("✅  Check In Now");
                checkInBtn.setStyle("-fx-background-color: rgba(16,185,129,0.15); -fx-text-fill: #10b981;" +
                    "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 9 18; -fx-cursor: hand;" +
                    "-fx-border-color: rgba(16,185,129,0.3); -fx-border-radius: 8; -fx-border-width: 1;");
                checkInBtn.setOnAction(e -> {
                    receptionist.checkIn(res);
                    HotelDatabase.persistReservation(res);
                    UIHelper.showAlert("Checked In!",
                        "Guest " + res.getGuest().getUsername() + " has been checked in.\n" +
                        "Room " + res.getRoom().getRoomNumber() + " is now occupied.");
                    showCheckIn();
                });

                HBox actionRow = new HBox();
                actionRow.setAlignment(Pos.CENTER_RIGHT);
                actionRow.getChildren().add(checkInBtn);
                card.getChildren().add(actionRow);
                pendingList.getChildren().add(card);
                any = true;
            }
        }
        if (!any) {
            VBox empty = buildEmptyState("✅", "No Pending Check-Ins",
                "All reservations have been processed.");
            pendingList.getChildren().add(empty);
        }

        page.getChildren().addAll(title, sub, searchBar, resultLabel, listTitle, pendingList);
        setContent(page);
    }

    // ───────────────────────── CHECK-OUT ─────────────────────────
    private void showCheckOut() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        Label title = new Label("Check-Out Guest");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label sub = new Label("Process a departure for a CONFIRMED reservation.");
        sub.setStyle("-fx-text-fill: rgba(180,200,255,0.55); -fx-font-size: 13px;");

        // Search box
        HBox searchBar = new HBox(12);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        TextField idField = new TextField();
        idField.setPromptText("Enter Reservation ID to check out");
        styleInput(idField);
        idField.setPrefWidth(320);
        Button searchBtn = new Button("Check Out");
        searchBtn.setStyle("-fx-background-color: linear-gradient(to right, #f59e0b, #fbbf24);" +
            "-fx-text-fill: #0a0e17; -fx-font-weight: bold; -fx-background-radius: 8;" +
            "-fx-padding: 10 22; -fx-cursor: hand;");

        Label resultLabel = new Label("");
        resultLabel.setWrapText(true);
        resultLabel.setStyle("-fx-font-size: 13px;");

        searchBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                resultLabel.setStyle("-fx-text-fill: #ef4444;");
                resultLabel.setText("Please enter a reservation ID.");
                return;
            }
            Reservation found = null;
            for (Reservation res : HotelDatabase.reservations) {
                if (res.getReservationId().equalsIgnoreCase(id)) {
                    found = res;
                    break;
                }
            }
            if (found == null) {
                resultLabel.setStyle("-fx-text-fill: #ef4444;");
                resultLabel.setText("No reservation found with ID: " + id);
                return;
            }
            if (found.getStatus() != Reservation.ReservationStatus.CONFIRMED) {
                resultLabel.setStyle("-fx-text-fill: #f59e0b;");
                resultLabel.setText("Cannot check out. Status is: " + found.getStatus() +
                    "\nOnly CONFIRMED reservations can be checked out.");
                return;
            }
            receptionist.checkOut(found);
            HotelDatabase.checkOutReservation(found);
            resultLabel.setStyle("-fx-text-fill: #10b981;");
            resultLabel.setText("✓ Guest " + found.getGuest().getUsername() +
                " has successfully checked out.\nRoom " + found.getRoom().getRoomNumber() +
                " is now available.");
            idField.clear();
            showCheckOut();
        });
        idField.setOnAction(e -> searchBtn.fire());
        searchBar.getChildren().addAll(idField, searchBtn);

        // All CONFIRMED reservations list
        Label listTitle = new Label("Currently Checked-In Guests");
        listTitle.setStyle("-fx-text-fill: rgba(200,220,255,0.8); -fx-font-size: 15px; -fx-font-weight: bold;");

        VBox confirmedList = new VBox(10);
        boolean any = false;
        for (Reservation res : HotelDatabase.reservations) {
            if (res.getStatus() == Reservation.ReservationStatus.CONFIRMED) {
                VBox card = makeReservationCard(res);

                Button checkOutBtn = new Button("🚪  Check Out Now");
                checkOutBtn.setStyle("-fx-background-color: rgba(245,158,11,0.15); -fx-text-fill: #f59e0b;" +
                    "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 9 18; -fx-cursor: hand;" +
                    "-fx-border-color: rgba(245,158,11,0.3); -fx-border-radius: 8; -fx-border-width: 1;");

                // Keep reference before lambda
                final Reservation finalRes = res;
                checkOutBtn.setOnAction(e -> {
                    if (UIHelper.showConfirm("Confirm Check-Out",
                        "Check out " + finalRes.getGuest().getUsername() +
                        " from Room " + finalRes.getRoom().getRoomNumber() + "?")) {
                        receptionist.checkOut(finalRes);
                        HotelDatabase.checkOutReservation(finalRes);
                        UIHelper.showAlert("Checked Out!",
                            "Guest " + finalRes.getGuest().getUsername() + " has departed.\n" +
                            "Room " + finalRes.getRoom().getRoomNumber() + " is now available.");
                        showCheckOut();
                    }
                });

                HBox actionRow = new HBox();
                actionRow.setAlignment(Pos.CENTER_RIGHT);
                actionRow.getChildren().add(checkOutBtn);
                card.getChildren().add(actionRow);
                confirmedList.getChildren().add(card);
                any = true;
            }
        }
        if (!any) {
            VBox empty = buildEmptyState("🚪", "No Checked-In Guests",
                "No guests are currently checked in.");
            confirmedList.getChildren().add(empty);
        }

        page.getChildren().addAll(title, sub, searchBar, resultLabel, listTitle, confirmedList);
        setContent(page);
    }

    // ───────────────────────── ALL RESERVATIONS ─────────────────────────
    private void showAllReservations() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        Label title = new Label("All Reservations");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Filter tabs
        HBox filterRow = new HBox(10);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        ToggleGroup tg = new ToggleGroup();
        ToggleButton allTab     = makeFilterTab("All",       tg, true);
        ToggleButton pendTab    = makeFilterTab("Pending",   tg, false);
        ToggleButton confTab    = makeFilterTab("Confirmed", tg, false);
        ToggleButton cancTab    = makeFilterTab("Cancelled", tg, false);
        filterRow.getChildren().addAll(allTab, pendTab, confTab, cancTab);

        VBox listContainer = new VBox(10);

        Runnable refresh = () -> {
            listContainer.getChildren().clear();
            boolean any = false;
            for (Reservation res : HotelDatabase.reservations) {
                boolean show = allTab.isSelected()
                    || (pendTab.isSelected() && res.getStatus() == Reservation.ReservationStatus.PENDING)
                    || (confTab.isSelected() && res.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    || (cancTab.isSelected() && res.getStatus() == Reservation.ReservationStatus.CANCELLED);
                if (show) {
                    listContainer.getChildren().add(makeReservationRow(res, false));
                    any = true;
                }
            }
            if (!any) {
                Label none = new Label("No reservations found.");
                none.setStyle("-fx-text-fill: rgba(180,200,255,0.4); -fx-font-size: 13px;");
                listContainer.getChildren().add(none);
            }
        };

        allTab.setOnAction(e  -> refresh.run());
        pendTab.setOnAction(e -> refresh.run());
        confTab.setOnAction(e -> refresh.run());
        cancTab.setOnAction(e -> refresh.run());
        refresh.run();

        page.getChildren().addAll(title, filterRow, listContainer);
        setContent(page);
    }

    // ───────────────────────── SHARED CARD BUILDERS ─────────────────────────

    /** Detailed card used inside check-in/check-out panels */
    private VBox makeReservationCard(Reservation res) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        String statusColor = statusColor(res.getStatus());
        card.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 12;" +
            "-fx-border-color: " + statusColor + "33; -fx-border-radius: 12; -fx-border-width: 1;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label rid = new Label("Reservation #" + res.getReservationId());
        rid.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        Label guest = new Label("Guest: " + res.getGuest().getUsername());
        guest.setStyle("-fx-text-fill: rgba(180,200,255,0.75); -fx-font-size: 13px;");
        Label room = new Label("Room: " + res.getRoom().getRoomNumber() +
            " — " + res.getRoom().getRoomType().getTypeName() +
            " | " + res.getRoom().getViewPreference());
        room.setStyle("-fx-text-fill: rgba(180,200,255,0.6); -fx-font-size: 13px;");
        Label price = new Label("Charge: $" + String.format("%.2f", res.getRoom().getPrice()));
        price.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 13px;");
        info.getChildren().addAll(rid, guest, room, price);

        Label statusBadge = new Label(res.getStatus().toString());
        statusBadge.setStyle("-fx-background-color: " + statusColor + "22; -fx-text-fill: " + statusColor +
            "; -fx-background-radius: 20; -fx-padding: 4 14; -fx-font-size: 12px; -fx-font-weight: bold;");

        topRow.getChildren().addAll(info, statusBadge);
        card.getChildren().add(topRow);
        return card;
    }

    /** Compact row used in overview and all-reservations list */
    private HBox makeReservationRow(Reservation res, boolean showCheckInBtn) {
        HBox row = new HBox(16);
        row.setPadding(new Insets(14, 18, 14, 18));
        row.setAlignment(Pos.CENTER_LEFT);
        String statusColor = statusColor(res.getStatus());
        row.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 10;" +
            "-fx-border-color: " + statusColor + "22; -fx-border-radius: 10; -fx-border-width: 1;");

        Label rid = new Label("#" + res.getReservationId());
        rid.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-min-width: 100;");
        Label guestLbl = new Label(res.getGuest().getUsername());
        guestLbl.setStyle("-fx-text-fill: rgba(180,200,255,0.7); -fx-font-size: 13px; -fx-min-width: 120;");
        Label roomLbl = new Label("Room " + res.getRoom().getRoomNumber());
        roomLbl.setStyle("-fx-text-fill: rgba(180,200,255,0.6); -fx-font-size: 13px;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label statusBadge = new Label(res.getStatus().toString());
        statusBadge.setStyle("-fx-background-color: " + statusColor + "22; -fx-text-fill: " + statusColor +
            "; -fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;");

        row.getChildren().addAll(rid, guestLbl, roomLbl, sp, statusBadge);

        if (showCheckInBtn && res.getStatus() == Reservation.ReservationStatus.PENDING) {
            Button quickCI = new Button("Check In");
            quickCI.setStyle("-fx-background-color: rgba(16,185,129,0.15); -fx-text-fill: #10b981;" +
                "-fx-font-size: 12px; -fx-background-radius: 6; -fx-padding: 5 12; -fx-cursor: hand;" +
                "-fx-border-color: rgba(16,185,129,0.3); -fx-border-radius: 6; -fx-border-width: 1;");
            quickCI.setOnAction(e -> {
                receptionist.checkIn(res);
                HotelDatabase.persistReservation(res);
                UIHelper.showAlert("Checked In!", "Reservation #" + res.getReservationId() + " confirmed.");
                showHome();
            });
            row.getChildren().add(quickCI);
        }

        return row;
    }

    // ───────────────────────── HELPERS ─────────────────────────

    private String statusColor(Reservation.ReservationStatus status) {
        switch (status) {
            case CONFIRMED:  return "#10b981";
            case CANCELLED:  return "#ef4444";
            default:         return "#f59e0b"; // PENDING
        }
    }

    private VBox buildEmptyState(String icon, String heading, String body) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(50));
        Label ic = new Label(icon);
        ic.setStyle("-fx-font-size: 42px;");
        Label h = new Label(heading);
        h.setStyle("-fx-text-fill: rgba(180,200,255,0.6); -fx-font-size: 16px; -fx-font-weight: bold;");
        Label b = new Label(body);
        b.setStyle("-fx-text-fill: rgba(180,200,255,0.35); -fx-font-size: 13px;");
        box.getChildren().addAll(ic, h, b);
        return box;
    }

    private ToggleButton makeFilterTab(String text, ToggleGroup tg, boolean selected) {
        ToggleButton tb = new ToggleButton(text);
        tb.setToggleGroup(tg);
        tb.setSelected(selected);
        String base = "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: rgba(200,220,255,0.6);" +
            "-fx-font-size: 13px; -fx-padding: 8 18; -fx-background-radius: 20; -fx-cursor: hand;";
        String active = "-fx-background-color: rgba(201,168,76,0.2); -fx-text-fill: #c9a84c;" +
            "-fx-font-size: 13px; -fx-padding: 8 18; -fx-background-radius: 20; -fx-cursor: hand;" +
            "-fx-border-color: rgba(201,168,76,0.35); -fx-border-radius: 20; -fx-border-width: 1;";
        tb.setStyle(selected ? active : base);
        tb.selectedProperty().addListener((obs, old, sel) -> tb.setStyle(sel ? active : base));
        return tb;
    }

    private void styleInput(TextField f) {
        f.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(180,200,255,0.4); -fx-background-radius: 8;" +
            "-fx-border-color: rgba(201,168,76,0.15); -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 9 12;");
        f.focusedProperty().addListener((obs, old, focused) -> {
            if (focused) f.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white;" +
                "-fx-prompt-text-fill: rgba(180,200,255,0.4); -fx-background-radius: 8;" +
                "-fx-border-color: #c9a84c; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-padding: 9 12;");
            else f.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-text-fill: white;" +
                "-fx-prompt-text-fill: rgba(180,200,255,0.4); -fx-background-radius: 8;" +
                "-fx-border-color: rgba(201,168,76,0.15); -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 9 12;");
        });
    }


    // ───────────────────────── LIVE CHAT ─────────────────────────
    private void showChat() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(32));

        Label title = new Label("💬  Live Chat with Guests");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label sub = new Label("Messages from all connected guests appear here in real-time.");
        sub.setStyle("-fx-text-fill: rgba(180,200,255,0.55); -fx-font-size: 13px;");

        VBox messagesBox = new VBox(6);
        messagesBox.setPadding(new Insets(14));

        ScrollPane chatScroll = new ScrollPane(messagesBox);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(400);
        chatScroll.setStyle(
            "-fx-background-color: rgba(10,16,30,0.8); -fx-background: rgba(10,16,30,0.8);" +
            "-fx-border-color: rgba(201,168,76,0.15); -fx-border-radius: 12; -fx-border-width: 1;" +
            "-fx-background-radius: 12;"
        );
        messagesBox.heightProperty().addListener((obs, old, newH) ->
            chatScroll.setVvalue(1.0));

        Label connStatus = new Label("● Connecting...");
        connStatus.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 11px;");

        String recepName = "Reception-" + receptionist.getUsername();
        ChatClient panelClient = new ChatClient(recepName, msg -> {
            Label lbl = makeChatBubble(msg, recepName);
            messagesBox.getChildren().add(lbl);
        });
        panelClient.connect();

        javafx.animation.PauseTransition pause =
            new javafx.animation.PauseTransition(javafx.util.Duration.millis(600));
        pause.setOnFinished(ev -> {
            if (panelClient.isConnected()) {
                connStatus.setText("● Connected");
                connStatus.setStyle("-fx-text-fill: #10b981; -fx-font-size: 11px;");
            } else {
                connStatus.setText("● Offline");
                connStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px;");
            }
        });
        pause.play();

        TextField inputField = new TextField();
        inputField.setPromptText("Reply to guests...");
        styleInput(inputField);
        inputField.setPrefWidth(9999);

        Button sendBtn = new Button("Reply ➤");
        sendBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #10b981, #34d399);" +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;" +
            "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;"
        );

        Runnable doSend = () -> {
            String text = inputField.getText().trim();
            if (text.isEmpty()) return;
            panelClient.sendMessage(text);
            inputField.clear();
        };
        sendBtn.setOnAction(e -> doSend.run());
        inputField.setOnAction(e -> doSend.run());

        HBox inputRow = new HBox(10);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputRow.getChildren().addAll(inputField, sendBtn);

        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_RIGHT);
        statusRow.getChildren().add(connStatus);

        page.getChildren().addAll(title, sub, statusRow, chatScroll, inputRow);
        setContent(page);
    }

    private Label makeChatBubble(String message, String myName) {
        Label lbl = new Label(message);
        lbl.setWrapText(true);
        lbl.setMaxWidth(560);

        boolean isSystem = message.startsWith("[SYSTEM]");
        boolean isMine   = message.startsWith(myName + ":");

        if (isSystem) {
            lbl.setStyle(
                "-fx-text-fill: rgba(180,200,255,0.45); -fx-font-size: 11px;" +
                "-fx-font-style: italic; -fx-padding: 2 8;"
            );
        } else if (isMine) {
            lbl.setStyle(
                "-fx-background-color: rgba(16,185,129,0.18);" +
                "-fx-text-fill: #34d399; -fx-font-size: 13px;" +
                "-fx-background-radius: 10; -fx-padding: 8 14;"
            );
        } else {
            lbl.setStyle(
                "-fx-background-color: rgba(255,255,255,0.07);" +
                "-fx-text-fill: rgba(200,220,255,0.9); -fx-font-size: 13px;" +
                "-fx-background-radius: 10; -fx-padding: 8 14;"
            );
        }
        return lbl;
    }

    public BorderPane getRoot() { return root; }
}
