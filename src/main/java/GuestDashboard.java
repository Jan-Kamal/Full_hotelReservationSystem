import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.util.Duration;
import java.time.LocalDate;
import java.util.ArrayList;

public class GuestDashboard {

    private BorderPane root;
    private Guest guest;
    private Label balanceLabel;
    private ChatClient chatClient;

    public GuestDashboard(Guest guest) {
        this.guest = guest;
        root = new BorderPane();
        root.setStyle("-fx-background-color: #080f1e;");

        // Connect to chat server
        chatClient = new ChatClient(guest.getUsername(), msg -> {/* handled per-panel */});

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
        sidebar.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #050c1a, #080f20);" +
            "-fx-border-color: rgba(201,168,76,0.15); -fx-border-width: 0 1 0 0;"
        );

        VBox logo = new VBox(4);
        logo.setPadding(new Insets(28, 20, 24, 20));
        logo.setAlignment(Pos.CENTER_LEFT);
        Label logoName = new Label("AIN SHAMS");
        logoName.setStyle("-fx-font-family: Georgia; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #c9a84c;");
        Label logoSub = new Label("GRAND HOTEL");
        logoSub.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(201,168,76,0.6); -fx-letter-spacing: 3px;");
        logo.getChildren().addAll(logoName, logoSub);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(201,168,76,0.15);");

        VBox guestInfo = new VBox(4);
        guestInfo.setPadding(new Insets(16, 20, 16, 20));
        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 28px;");
        Label guestName = new Label(guest.getUsername());
        guestName.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        balanceLabel = new Label("$" + String.format("%.2f", guest.getBalance()));
        balanceLabel.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 13px;");
        Label balLbl = new Label("Available Balance");
        balLbl.setStyle("-fx-text-fill: rgba(180,200,255,0.5); -fx-font-size: 11px;");
        guestInfo.getChildren().addAll(avatar, guestName, balanceLabel, balLbl);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: rgba(201,168,76,0.1);");

        VBox nav = new VBox(4);
        nav.setPadding(new Insets(12, 12, 12, 12));

        Button[] navBtns = {
            makeSidebarBtn("🏠", "Dashboard",       () -> showHome()),
            makeSidebarBtn("🛏", "Browse & Reserve", () -> showRooms()),
            makeSidebarBtn("📋", "My Reservations",  () -> showMyReservations()),
            makeSidebarBtn("➕", "Add Balance",       () -> showTopUp()),
            makeSidebarBtn("✨", "Extra Amenities",   () -> showAmenities()),
            makeSidebarBtn("💬", "Live Chat",         () -> showChat()),
        };
        for (Button b : navBtns) nav.getChildren().add(b);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = makeSidebarBtn("🚪", "Logout", () -> {
            if (chatClient != null) chatClient.disconnect();
            SceneManager.showLogin();
        });
        logoutBtn.setStyle(logoutBtn.getStyle() + "-fx-text-fill: #ef4444;");

        sidebar.getChildren().addAll(logo, sep, guestInfo, sep2, nav, spacer, logoutBtn);
        return sidebar;
    }

    private Button makeSidebarBtn(String icon, String text, Runnable action) {
        Button btn = new Button(icon + "  " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        String baseStyle =
            "-fx-background-color: transparent; -fx-text-fill: rgba(200,220,255,0.75);" +
            "-fx-font-size: 13px; -fx-padding: 11 16; -fx-background-radius: 8; -fx-cursor: hand;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(201,168,76,0.1); -fx-text-fill: #c9a84c;" +
            "-fx-font-size: 13px; -fx-padding: 11 16; -fx-background-radius: 8; -fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(16, 30, 16, 30));
        bar.setStyle(
            "-fx-background-color: rgba(8,15,30,0.95);" +
            "-fx-border-color: rgba(201,168,76,0.1); -fx-border-width: 0 0 1 0;"
        );
        Label title = new Label("Guest Portal");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: Georgia;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label stars = new Label("★ ★ ★ ★ ★");
        stars.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 12px;");
        bar.getChildren().addAll(title, spacer, stars);
        return bar;
    }

    private void setContent(javafx.scene.Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setPadding(new Insets(0));
        root.setCenter(scroll);

        FadeTransition ft = new FadeTransition(Duration.millis(200), content);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    // ───────── HOME ─────────
    private void showHome() {
        VBox page = new VBox(24);
        page.setPadding(new Insets(32));

        Label welcome = new Label("Welcome back, " + guest.getUsername() + " 👋");
        welcome.setStyle("-fx-font-family: Georgia; -fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label sub = new Label("Here's your account overview");
        sub.setStyle("-fx-text-fill: rgba(180,200,255,0.6); -fx-font-size: 14px;");

        // Stats cards — no "Member Status" card
        HBox stats = new HBox(16);
        stats.getChildren().addAll(
            makeStatCard("💰", "Balance",          "$" + String.format("%.2f", guest.getBalance()), "#c9a84c"),
            makeStatCard("📋", "Reservations",     countMyReservations() + " Active",                "#00d4ff"),
            makeStatCard("🏨", "Available Rooms",  countAvailableRooms() + " Rooms",                 "#10b981")
        );
        for (javafx.scene.Node n : stats.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // Quick actions
        Label qaTitle = new Label("Quick Actions");
        qaTitle.setStyle("-fx-text-fill: rgba(200,220,255,0.8); -fx-font-size: 15px; -fx-font-weight: bold;");

        HBox quickActions = new HBox(12);
        Button reserveBtn = makeActionBtn("🛏  Browse & Reserve a Room", "#1e3a5f", "#00d4ff");
        Button viewBtn    = makeActionBtn("📋  View My Reservations",    "#1a2a1a", "#10b981");
        Button topupBtn   = makeActionBtn("💳  Add Balance",              "#2a1e0a", "#f59e0b");
        Button chatBtn    = makeActionBtn("💬  Open Live Chat",           "#0f1a2e", "#a78bfa");
        reserveBtn.setOnAction(e -> showRooms());
        viewBtn.setOnAction(e -> showMyReservations());
        topupBtn.setOnAction(e -> showTopUp());
        chatBtn.setOnAction(e -> showChat());
        quickActions.getChildren().addAll(reserveBtn, viewBtn, topupBtn, chatBtn);
        for (javafx.scene.Node n : quickActions.getChildren()) HBox.setHgrow((javafx.scene.Node)n, Priority.ALWAYS);

        page.getChildren().addAll(welcome, sub, stats, qaTitle, quickActions);
        setContent(page);
    }

    private VBox makeStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.04);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-border-width: 1;"
        );
        card.setAlignment(Pos.CENTER_LEFT);
        Label ic = new Label(icon);
        ic.setStyle("-fx-font-size: 24px;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: rgba(180,200,255,0.55); -fx-font-size: 12px;");
        card.getChildren().addAll(ic, v, l);
        return card;
    }

    private Button makeActionBtn(String text, String bgColor, String borderColor) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(16));
        String base = "-fx-background-color: " + bgColor + "; -fx-text-fill: " + borderColor +
            "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10;" +
            "-fx-border-color: " + borderColor + "33; -fx-border-radius: 10; -fx-border-width: 1; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    // ───────── ROOMS ─────────
    private void showRooms() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        Label title = new Label("Browse & Reserve Rooms");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox filters = new HBox(12);
        filters.setAlignment(Pos.CENTER_LEFT);
        TextField typeFilter = new TextField();
        typeFilter.setPromptText("Filter by type (e.g. Suite)");
        styleCompact(typeFilter);
        TextField viewFilter = new TextField();
        viewFilter.setPromptText("Filter by view (e.g. Sea View)");
        styleCompact(viewFilter);
        Button filterBtn = new Button("🔍  Search");
        filterBtn.setStyle(
            "-fx-background-color: rgba(201,168,76,0.2); -fx-text-fill: #c9a84c;" +
            "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 9 18; -fx-cursor: hand;" +
            "-fx-border-color: rgba(201,168,76,0.3); -fx-border-radius: 8; -fx-border-width: 1;"
        );
        filters.getChildren().addAll(typeFilter, viewFilter, filterBtn);

        VBox roomsList = new VBox(12);

        Runnable refresh = () -> {
            roomsList.getChildren().clear();
            String tf = typeFilter.getText().trim().toLowerCase();
            String vf = viewFilter.getText().trim().toLowerCase();
            boolean anyShown = false;
            for (Room r : HotelDatabase.rooms) {
                if (!r.isAvailable()) continue;
                if (!tf.isEmpty() && !r.getRoomType().getTypeName().toLowerCase().contains(tf)) continue;
                if (!vf.isEmpty() && !r.getViewPreference().toLowerCase().contains(vf)) continue;
                roomsList.getChildren().add(makeRoomCard(r));
                anyShown = true;
            }
            if (!anyShown) {
                Label none = new Label("No available rooms match your search.");
                none.setStyle("-fx-text-fill: rgba(180,200,255,0.5); -fx-font-size: 14px;");
                roomsList.getChildren().add(none);
            }
        };

        filterBtn.setOnAction(e -> refresh.run());
        refresh.run();

        page.getChildren().addAll(title, filters, roomsList);
        setContent(page);
    }

    private VBox makeRoomCard(Room r) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.04);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-border-width: 1;"
        );

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(5);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label rNum = new Label("Room " + r.getRoomNumber());
        rNum.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label rType = new Label(r.getRoomType().getTypeName() + "  ·  " + r.getViewPreference());
        rType.setStyle("-fx-text-fill: rgba(180,200,255,0.65); -fx-font-size: 13px;");
        Label rAmen = new Label("Amenities: " + (r.getRoomAmenities().isEmpty() ? "Standard" : r.getRoomAmenities().toString()));
        rAmen.setStyle("-fx-text-fill: rgba(180,200,255,0.45); -fx-font-size: 12px;");
        info.getChildren().addAll(rNum, rType, rAmen);

        VBox priceBox = new VBox(4);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        Label price = new Label("$" + String.format("%.0f", r.getPrice()));
        price.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label perNight = new Label("per night");
        perNight.setStyle("-fx-text-fill: rgba(180,200,255,0.4); -fx-font-size: 11px;");
        priceBox.getChildren().addAll(price, perNight);

        topRow.getChildren().addAll(info, priceBox);

        Label avail = new Label("✓ Available");
        avail.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;");

        Button bookBtn = new Button("Reserve This Room →");
        bookBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #c9a84c, #e8c97a);" +
            "-fx-text-fill: #0a0e17; -fx-font-weight: bold; -fx-font-size: 13px;" +
            "-fx-background-radius: 8; -fx-padding: 10 24; -fx-cursor: hand;"
        );
        bookBtn.setOnAction(e -> showReservationForm(r));

        HBox bottomRow = new HBox(12);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        bottomRow.getChildren().addAll(avail, sp, bookBtn);

        card.getChildren().addAll(topRow, new Separator(), bottomRow);
        return card;
    }

    private void showReservationForm(Room r) {
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Reserve Room " + r.getRoomNumber());
        dialog.setHeaderText(null);

        VBox content = new VBox(14);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: #0d1525;");
        content.setPrefWidth(400);

        Label title = new Label("Confirm Reservation");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox summary = new VBox(6);
        summary.setPadding(new Insets(14));
        summary.setStyle("-fx-background-color: rgba(201,168,76,0.08); -fx-background-radius: 10;" +
            "-fx-border-color: rgba(201,168,76,0.2); -fx-border-radius: 10; -fx-border-width: 1;");
        summary.getChildren().addAll(
            makeDialogRow("Room:",        "Room " + r.getRoomNumber()),
            makeDialogRow("Type:",        r.getRoomType().getTypeName()),
            makeDialogRow("View:",        r.getViewPreference()),
            makeDialogRow("Price:",       "$" + String.format("%.2f", r.getPrice())),
            makeDialogRow("Your Balance:", "$" + String.format("%.2f", guest.getBalance()))
        );

        DatePicker checkInPicker  = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker checkOutPicker = new DatePicker(LocalDate.now().plusDays(2));

        ComboBox<String> paymentBox = new ComboBox<>();
        paymentBox.getItems().addAll("CASH", "CREDIT_CARD", "ONLINE");
        paymentBox.setValue("CASH");
        paymentBox.setMaxWidth(Double.MAX_VALUE);

        Label statusLbl = new Label("");
        statusLbl.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        statusLbl.setWrapText(true);

        content.getChildren().addAll(
            title, summary,
            makeLabeledNode("Check-In Date",  checkInPicker),
            makeLabeledNode("Check-Out Date", checkOutPicker),
            makeLabeledNode("Payment Method", paymentBox),
            statusLbl
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle("-fx-background-color: #0d1525;");

        ButtonType confirmType = new ButtonType("Confirm Reservation", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType  = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmType, cancelType);

        javafx.scene.Node confirmBtn = dialog.getDialogPane().lookupButton(confirmType);

        confirmBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            statusLbl.setText("");
            LocalDate ci = checkInPicker.getValue();
            LocalDate co = checkOutPicker.getValue();
            if (ci == null || co == null) { statusLbl.setText("Select both dates."); event.consume(); return; }
            if (!co.isAfter(ci)) { statusLbl.setText("Check-out must be after check-in."); event.consume(); return; }
            if (!guest.canAfford(r.getPrice())) { statusLbl.setText("Insufficient balance."); event.consume(); return; }

            String pm = paymentBox.getValue();
            Invoice.PaymentMethod method = pm.equals("CASH") ? Invoice.PaymentMethod.CASH :
                pm.equals("CREDIT_CARD") ? Invoice.PaymentMethod.CREDIT_CARD : Invoice.PaymentMethod.ONLINE;

            guest.deductBalance(r.getPrice());
            Invoice inv = new Invoice(r.getPrice(), method, ci);
            Reservation res = new Reservation(guest, r, ci, co);
            HotelDatabase.addReservation(res, inv);
            HotelDatabase.persistGuest(guest);
            balanceLabel.setText("$" + String.format("%.2f", guest.getBalance()));

            UIHelper.showAlert("Reservation Confirmed! 🎉",
                "Reservation ID: " + res.getReservationId() +
                "\nRoom: " + r.getRoomNumber() +
                "\nCheck-in: " + ci +
                "\nTotal Charged: $" + String.format("%.2f", r.getPrice()));
        });

        dialog.setResultConverter(btn -> null);
        dialog.showAndWait();
        showRooms();
    }

    // ───────── MY RESERVATIONS ─────────
    private void showMyReservations() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        Label title = new Label("My Reservations");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox list = new VBox(12);
        boolean any = false;
        for (Reservation res : HotelDatabase.reservations) {
            if (res.getGuest().equals(guest)) {
                list.getChildren().add(makeReservationCard(res, list));
                any = true;
            }
        }
        if (!any) {
            VBox empty = new VBox(12);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(60));
            Label el = new Label("🛏"); el.setStyle("-fx-font-size: 48px;");
            Label et = new Label("No reservations yet"); et.setStyle("-fx-text-fill: rgba(180,200,255,0.5); -fx-font-size: 16px;");
            Label es = new Label("Browse rooms to make your first reservation"); es.setStyle("-fx-text-fill: rgba(180,200,255,0.35); -fx-font-size: 13px;");
            Button go = new Button("Browse Rooms →");
            go.setStyle("-fx-background-color: rgba(201,168,76,0.2); -fx-text-fill: #c9a84c; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
            go.setOnAction(e -> showRooms());
            empty.getChildren().addAll(el, et, es, go);
            list.getChildren().add(empty);
        }

        page.getChildren().addAll(title, list);
        setContent(page);
    }

    private VBox makeReservationCard(Reservation res, VBox parentList) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        String statusColor = res.getStatus() == Reservation.ReservationStatus.CONFIRMED ? "#10b981" :
            res.getStatus() == Reservation.ReservationStatus.CANCELLED ? "#ef4444" : "#f59e0b";
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.04);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + statusColor + "33; -fx-border-radius: 12; -fx-border-width: 1;"
        );

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label rid = new Label("Reservation #" + res.getReservationId());
        rid.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        Label rroom = new Label("Room " + res.getRoom().getRoomNumber() + " — " +
            res.getRoom().getRoomType().getTypeName() + " | " + res.getRoom().getViewPreference());
        rroom.setStyle("-fx-text-fill: rgba(180,200,255,0.65); -fx-font-size: 13px;");
        Label rprice = new Label("$" + String.format("%.2f", res.getRoom().getPrice()));
        rprice.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 13px;");
        info.getChildren().addAll(rid, rroom, rprice);

        Label statusBadge = new Label(res.getStatus().toString());
        statusBadge.setStyle(
            "-fx-background-color: " + statusColor + "22; -fx-text-fill: " + statusColor +
            "; -fx-background-radius: 20; -fx-padding: 4 12; -fx-font-size: 12px; -fx-font-weight: bold;"
        );
        topRow.getChildren().addAll(info, statusBadge);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        if (res.getStatus() != Reservation.ReservationStatus.CANCELLED) {
            Button cancelBtn = new Button("Cancel Reservation");
            cancelBtn.setStyle(
                "-fx-background-color: rgba(239,68,68,0.15); -fx-text-fill: #ef4444;" +
                "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;" +
                "-fx-border-color: rgba(239,68,68,0.3); -fx-border-radius: 8; -fx-border-width: 1;"
            );
            cancelBtn.setOnAction(e -> {
                if (UIHelper.showConfirm("Cancel Reservation",
                    "Cancel reservation #" + res.getReservationId() + "?\nYou will receive a full refund of $" +
                    String.format("%.2f", res.getRoom().getPrice()))) {
                    res.cancel();
                    HotelDatabase.cancelReservation(res);
                    balanceLabel.setText("$" + String.format("%.2f", guest.getBalance()));
                    showMyReservations();
                }
            });
            actions.getChildren().add(cancelBtn);
        }

        card.getChildren().addAll(topRow, actions);
        return card;
    }

    // ───────── TOP UP ─────────
    private void showTopUp() {
        VBox page = new VBox(24);
        page.setPadding(new Insets(32));
        page.setMaxWidth(500);

        Label title = new Label("Add Balance");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox card = new VBox(18);
        card.setPadding(new Insets(28));
        card.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 14;" +
            "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14; -fx-border-width: 1;");

        Label curBal = new Label("Current Balance: $" + String.format("%.2f", guest.getBalance()));
        curBal.setStyle("-fx-text-fill: rgba(180,200,255,0.7); -fx-font-size: 14px;");

        Label quickLabel = new Label("Quick Add");
        quickLabel.setStyle("-fx-text-fill: rgba(180,200,255,0.6); -fx-font-size: 12px;");
        HBox quickBtns = new HBox(10);
        TextField amountField = new TextField();
        amountField.setPromptText("Enter custom amount");
        styleCompact(amountField);

        for (String amt : new String[]{"$100", "$250", "$500", "$1000"}) {
            Button qb = new Button(amt);
            qb.setStyle("-fx-background-color: rgba(201,168,76,0.1); -fx-text-fill: #c9a84c;" +
                "-fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand;" +
                "-fx-border-color: rgba(201,168,76,0.25); -fx-border-radius: 8; -fx-border-width: 1;");
            qb.setOnAction(e -> amountField.setText(amt.replace("$", "")));
            quickBtns.getChildren().add(qb);
        }

        Button addBtn = new Button("Add Balance →");
        addBtn.setStyle("-fx-background-color: linear-gradient(to right, #c9a84c, #e8c97a);" +
            "-fx-text-fill: #0a0e17; -fx-font-weight: bold; -fx-font-size: 14px;" +
            "-fx-background-radius: 10; -fx-padding: 14 0; -fx-cursor: hand;");
        addBtn.setMaxWidth(Double.MAX_VALUE);

        Label statusLbl = new Label("");
        statusLbl.setStyle("-fx-text-fill: #10b981; -fx-font-size: 13px;");

        addBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) { statusLbl.setStyle("-fx-text-fill: #ef4444;"); statusLbl.setText("Amount must be positive."); return; }
                guest.topUpBalance(amount);
                HotelDatabase.persistGuest(guest);
                balanceLabel.setText("$" + String.format("%.2f", guest.getBalance()));
                curBal.setText("Current Balance: $" + String.format("%.2f", guest.getBalance()));
                statusLbl.setStyle("-fx-text-fill: #10b981;");
                statusLbl.setText("✓ Successfully added $" + String.format("%.2f", amount));
                amountField.clear();
            } catch (NumberFormatException ex) {
                statusLbl.setStyle("-fx-text-fill: #ef4444;");
                statusLbl.setText("Please enter a valid number.");
            }
        });

        card.getChildren().addAll(curBal, quickLabel, quickBtns,
            makeLabeledNode("Custom Amount ($)", amountField), addBtn, statusLbl);
        page.getChildren().addAll(title, card);
        setContent(page);
    }

    // ───────── EXTRA AMENITIES ─────────
    private void showAmenities() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        Label title = new Label("Request Extra Amenities");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        ArrayList<Reservation> active = new ArrayList<>();
        for (Reservation res : HotelDatabase.reservations) {
            if (res.getGuest().equals(guest) && res.getStatus() != Reservation.ReservationStatus.CANCELLED) {
                active.add(res);
            }
        }

        if (active.isEmpty()) {
            Label none = new Label("You have no active reservations to add amenities to.");
            none.setStyle("-fx-text-fill: rgba(180,200,255,0.5); -fx-font-size: 14px;");
            page.getChildren().addAll(title, none);
        } else {
            for (Reservation res : active) {
                VBox card = new VBox(14);
                card.setPadding(new Insets(20));
                card.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 12;" +
                    "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-border-width: 1;");

                Label resInfo = new Label("Room " + res.getRoom().getRoomNumber() + " — #" + res.getReservationId());
                resInfo.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

                boolean isSuite = res.getRoom().getRoomType().getTypeName().toLowerCase().contains("suite");

                HBox amenityBtns = new HBox(12);
                Button bedBtn = new Button("🛏  Extra Bed — $" + (isSuite ? "50" : "100"));
                styleLuxuryBtn(bedBtn);
                bedBtn.setOnAction(e -> {
                    double price = isSuite ? 50.0 : 100.0;
                    if (guest.canAfford(price)) {
                        guest.deductBalance(price);
                        Amenity bedAmenity = new Amenity("Extra Bed");
                        res.addExtraAmenity(bedAmenity);
                        HotelDatabase.persistGuest(guest);
                        HotelDatabase.persistExtraAmenity(res, bedAmenity);
                        balanceLabel.setText("$" + String.format("%.2f", guest.getBalance()));
                        UIHelper.showAlert("Added!", "Extra Bed added to Room " + res.getRoom().getRoomNumber());
                    } else UIHelper.showError("Insufficient Funds", "Not enough balance.");
                });
                amenityBtns.getChildren().add(bedBtn);

                if (!isSuite) {
                    Button wifiBtn = new Button("📶  Unlimited WiFi — $50");
                    styleLuxuryBtn(wifiBtn);
                    wifiBtn.setOnAction(e -> {
                        if (guest.canAfford(50.0)) {
                            guest.deductBalance(50.0);
                            Amenity wifiAmenity = new Amenity("Unlimited WiFi");
                            res.addExtraAmenity(wifiAmenity);
                            HotelDatabase.persistGuest(guest);
                            HotelDatabase.persistExtraAmenity(res, wifiAmenity);
                            balanceLabel.setText("$" + String.format("%.2f", guest.getBalance()));
                            UIHelper.showAlert("Added!", "Unlimited WiFi added.");
                        } else UIHelper.showError("Insufficient Funds", "Not enough balance.");
                    });
                    amenityBtns.getChildren().add(wifiBtn);
                }

                card.getChildren().addAll(resInfo, amenityBtns);
                page.getChildren().add(card);
            }
        }

        setContent(page);
    }

    // ───────── LIVE CHAT ─────────
    private void showChat() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(32));

        Label title = new Label("💬  Live Chat with Reception");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label sub = new Label("Messages are delivered in real-time to the reception desk.");
        sub.setStyle("-fx-text-fill: rgba(180,200,255,0.55); -fx-font-size: 13px;");

        // Chat message log
        VBox messagesBox = new VBox(6);
        messagesBox.setPadding(new Insets(14));

        ScrollPane chatScroll = new ScrollPane(messagesBox);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(380);
        chatScroll.setStyle(
            "-fx-background-color: rgba(10,16,30,0.8); -fx-background: rgba(10,16,30,0.8);" +
            "-fx-border-color: rgba(201,168,76,0.15); -fx-border-radius: 12; -fx-border-width: 1;" +
            "-fx-background-radius: 12;"
        );

        // Auto-scroll to bottom on new messages
        messagesBox.heightProperty().addListener((obs, old, newH) ->
            chatScroll.setVvalue(1.0));

        // Status indicator
        Label connStatus = new Label("● Connecting...");
        connStatus.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 11px;");

        // Create a fresh chat client for this panel
        ChatClient panelClient = new ChatClient(guest.getUsername(), msg -> {
            Label msgLabel = makeChatBubble(msg, guest.getUsername());
            messagesBox.getChildren().add(msgLabel);
        });
        panelClient.connect();

        // Short delay to check connection
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(600));
        pause.setOnFinished(e -> {
            if (panelClient.isConnected()) {
                connStatus.setText("● Connected");
                connStatus.setStyle("-fx-text-fill: #10b981; -fx-font-size: 11px;");
            } else {
                connStatus.setText("● Offline");
                connStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px;");
            }
        });
        pause.play();

        // Input row
        TextField inputField = new TextField();
        inputField.setPromptText("Type your message...");
        styleCompact(inputField);
        inputField.setPrefWidth(9999);

        Button sendBtn = new Button("Send ➤");
        sendBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #c9a84c, #e8c97a);" +
            "-fx-text-fill: #0a0e17; -fx-font-weight: bold; -fx-font-size: 13px;" +
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

    private Label makeChatBubble(String message, String myUsername) {
        Label lbl = new Label(message);
        lbl.setWrapText(true);
        lbl.setMaxWidth(560);

        boolean isSystem = message.startsWith("[SYSTEM]");
        boolean isMine   = message.startsWith(myUsername + ":");

        if (isSystem) {
            lbl.setStyle(
                "-fx-text-fill: rgba(180,200,255,0.45); -fx-font-size: 11px;" +
                "-fx-font-style: italic; -fx-padding: 2 8;"
            );
        } else if (isMine) {
            lbl.setStyle(
                "-fx-background-color: rgba(201,168,76,0.18);" +
                "-fx-text-fill: #e8c97a; -fx-font-size: 13px;" +
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

    // ───────── HELPERS ─────────
    private void styleLuxuryBtn(Button btn) {
        btn.setStyle("-fx-background-color: rgba(201,168,76,0.12); -fx-text-fill: #c9a84c;" +
            "-fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 12 20; -fx-cursor: hand;" +
            "-fx-border-color: rgba(201,168,76,0.3); -fx-border-radius: 8; -fx-border-width: 1;");
    }

    private int countMyReservations() {
        int count = 0;
        for (Reservation res : HotelDatabase.reservations)
            if (res.getGuest().equals(guest) && res.getStatus() != Reservation.ReservationStatus.CANCELLED) count++;
        return count;
    }

    private int countAvailableRooms() {
        int count = 0;
        for (Room r : HotelDatabase.rooms) if (r.isAvailable()) count++;
        return count;
    }

    private void styleCompact(TextInputControl f) {
        f.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(180,200,255,0.4); -fx-background-radius: 8;" +
            "-fx-border-color: rgba(201,168,76,0.15); -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 9 12;");
        f.setPrefWidth(200);
    }

    private HBox makeDialogRow(String label, String value) {
        HBox row = new HBox();
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: rgba(180,200,255,0.6); -fx-font-size: 13px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        row.getChildren().addAll(l, sp, v);
        return row;
    }

    private VBox makeLabeledNode(String label, javafx.scene.Node node) {
        VBox b = new VBox(6);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: rgba(200,220,255,0.65); -fx-font-size: 12px;");
        b.getChildren().addAll(l, node);
        return b;
    }

    public BorderPane getRoot() { return root; }
}
