import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.time.LocalDate;

public class AdminDashboard {

    private BorderPane root;
    private Admin admin;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        root = new BorderPane();
        root.setStyle("-fx-background-color: #080f1e;");
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
        Label l2 = new Label("ADMIN PORTAL");
        l2.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(201,168,76,0.6); -fx-letter-spacing: 3px;");
        logo.getChildren().addAll(l1, l2);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(201,168,76,0.15);");

        VBox userInfo = new VBox(4);
        userInfo.setPadding(new Insets(16, 20, 16, 20));
        Label icon = new Label("🔑");
        icon.setStyle("-fx-font-size: 28px;");
        Label name = new Label(admin.getUsername());
        name.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label role = new Label("Administrator");
        role.setStyle("-fx-text-fill: rgba(201,168,76,0.7); -fx-font-size: 11px;");
        userInfo.getChildren().addAll(icon, name, role);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: rgba(201,168,76,0.1);");

        VBox nav = new VBox(4);
        nav.setPadding(new Insets(12));

        nav.getChildren().addAll(
            makeSideBtn("🏠", "Overview", () -> showHome()),
            makeSideBtn("🏨", "Manage Rooms", () -> showRooms()),
            makeSideBtn("🏷", "Room Types", () -> showRoomTypes()),
            makeSideBtn("✨", "Amenities", () -> showAmenities()),
            makeSideBtn("📋", "All Reservations", () -> showReservations()),
            makeSideBtn("👥", "All Guests", () -> showGuests())
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button logoutBtn = makeSideBtn("🚪", "Logout", () -> SceneManager.showLogin());
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
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(201,168,76,0.1); -fx-text-fill: #c9a84c;" +
            "-fx-font-size: 13px; -fx-padding: 11 16; -fx-background-radius: 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(16, 30, 16, 30));
        bar.setStyle("-fx-background-color: rgba(8,15,30,0.95); -fx-border-color: rgba(201,168,76,0.1); -fx-border-width: 0 0 1 0;");
        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: Georgia;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label badge = new Label("🔑  Administrator");
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
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    // ───────── HOME ─────────
    private void showHome() {
        VBox page = new VBox(24);
        page.setPadding(new Insets(32));

        Label title = new Label("System Overview");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox stats = new HBox(16);
        stats.getChildren().addAll(
            makeStatCard("👥", "Guests", String.valueOf(HotelDatabase.guests.size()), "#00d4ff"),
            makeStatCard("🏨", "Rooms", String.valueOf(HotelDatabase.rooms.size()), "#10b981"),
            makeStatCard("📋", "Reservations", String.valueOf(HotelDatabase.reservations.size()), "#c9a84c"),
            makeStatCard("✨", "Amenities", String.valueOf(HotelDatabase.amenitys.size()), "#a78bfa")
        );
        for (javafx.scene.Node n : stats.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // Recent reservations preview
        Label recTitle = new Label("Recent Reservations");
        recTitle.setStyle("-fx-text-fill: rgba(200,220,255,0.8); -fx-font-size: 15px; -fx-font-weight: bold;");

        VBox resList = new VBox(8);
        int shown = 0;
        for (Reservation res : HotelDatabase.reservations) {
            if (shown >= 5) break;
            HBox row = new HBox(16);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 8;");
            row.setAlignment(Pos.CENTER_LEFT);
            Label rid = new Label("#" + res.getReservationId());
            rid.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-min-width: 100;");
            Label rguest = new Label(res.getGuest().getUsername());
            rguest.setStyle("-fx-text-fill: rgba(180,200,255,0.7); -fx-font-size: 13px; -fx-min-width: 120;");
            Label rroom = new Label("Room " + res.getRoom().getRoomNumber());
            rroom.setStyle("-fx-text-fill: rgba(180,200,255,0.7); -fx-font-size: 13px;");
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            String sc = res.getStatus() == Reservation.ReservationStatus.CONFIRMED ? "#10b981" :
                res.getStatus() == Reservation.ReservationStatus.CANCELLED ? "#ef4444" : "#f59e0b";
            Label rstatus = new Label(res.getStatus().toString());
            rstatus.setStyle("-fx-background-color: " + sc + "22; -fx-text-fill: " + sc +
                "; -fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px;");
            row.getChildren().addAll(rid, rguest, rroom, sp, rstatus);
            resList.getChildren().add(row);
            shown++;
        }
        if (HotelDatabase.reservations.isEmpty()) {
            Label none = new Label("No reservations yet.");
            none.setStyle("-fx-text-fill: rgba(180,200,255,0.4); -fx-font-size: 13px;");
            resList.getChildren().add(none);
        }

        page.getChildren().addAll(title, stats, recTitle, resList);
        setContent(page);
    }

    private VBox makeStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 12;" +
            "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-border-width: 1;");
        Label ic = new Label(icon); ic.setStyle("-fx-font-size: 24px;");
        Label v = new Label(value); v.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label l = new Label(label); l.setStyle("-fx-text-fill: rgba(180,200,255,0.55); -fx-font-size: 12px;");
        card.getChildren().addAll(ic, v, l);
        return card;
    }

    // ───────── ROOMS ─────────
    private void showRooms() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Manage Rooms");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button addBtn = new Button("+ Create Room");
        addBtn.setStyle("-fx-background-color: linear-gradient(to right, #c9a84c, #e8c97a); -fx-text-fill: #0a0e17;" +
            "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showCreateRoomDialog(() -> showRooms()));
        header.getChildren().addAll(title, sp, addBtn);

        VBox list = new VBox(10);
        for (Room r : HotelDatabase.rooms) {
            list.getChildren().add(makeRoomManageCard(r));
        }
        if (HotelDatabase.rooms.isEmpty()) {
            Label none = new Label("No rooms created yet. Click '+ Create Room' to begin.");
            none.setStyle("-fx-text-fill: rgba(180,200,255,0.5); -fx-font-size: 14px;");
            list.getChildren().add(none);
        }

        page.getChildren().addAll(header, list);
        setContent(page);
    }

    private VBox makeRoomManageCard(Room r) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10;" +
            "-fx-border-color: rgba(255,255,255,0.07); -fx-border-radius: 10; -fx-border-width: 1;");

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label rn = new Label("Room " + r.getRoomNumber());
        rn.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        Label rt = new Label(r.getRoomType().getTypeName() + " · " + r.getViewPreference());
        rt.setStyle("-fx-text-fill: rgba(180,200,255,0.6); -fx-font-size: 12px;");
        Label ra = new Label("Amenities: " + (r.getRoomAmenities().isEmpty() ? "None" : r.getRoomAmenities()));
        ra.setStyle("-fx-text-fill: rgba(180,200,255,0.4); -fx-font-size: 11px;");
        info.getChildren().addAll(rn, rt, ra);

        Label price = new Label("$" + String.format("%.0f", r.getPrice()));
        price.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 18px; -fx-font-weight: bold;");

        String avColor = r.isAvailable() ? "#10b981" : "#f59e0b";
        Label avail = new Label(r.isAvailable() ? "✓ Available" : "⏳ Occupied");
        avail.setStyle("-fx-text-fill: " + avColor + "; -fx-font-size: 12px;");

        row.getChildren().addAll(info, avail, price);

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button editPriceBtn = makeSmallBtn("Edit Price", "#1e3a5f", "#00d4ff");
        editPriceBtn.setOnAction(e -> {
            javafx.scene.control.Dialog<Void> pd = new javafx.scene.control.Dialog<>();
            pd.setTitle("Update Price"); pd.setHeaderText(null);

            VBox pcontent = new VBox(12);
            pcontent.setPadding(new Insets(20));
            pcontent.setStyle("-fx-background-color: #0d1525;");
            pcontent.setPrefWidth(300);
            Label plabel = new Label("New price for Room " + r.getRoomNumber() + ":");
            plabel.setStyle("-fx-text-fill: rgba(200,220,255,0.7); -fx-font-size: 13px;");
            TextField priceInputField = new TextField(String.valueOf((int) r.getPrice()));
            styleInput(priceInputField);
            Label perrorLbl = new Label("");
            perrorLbl.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
            perrorLbl.setWrapText(true);
            pcontent.getChildren().addAll(plabel, priceInputField, perrorLbl);

            pd.getDialogPane().setContent(pcontent);
            pd.getDialogPane().setStyle("-fx-background-color: #0d1525;");

            ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            pd.getDialogPane().getButtonTypes().addAll(saveType, cancelType);

            javafx.scene.Node saveBtn = pd.getDialogPane().lookupButton(saveType);
            saveBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                perrorLbl.setText("");
                String val = priceInputField.getText().trim();
                double p;
                try { p = Double.parseDouble(val); }
                catch (NumberFormatException ex) { perrorLbl.setText("Invalid price."); event.consume(); return; }
                if (p < 0) { perrorLbl.setText("Price cannot be negative."); event.consume(); return; }
                admin.updateRoomPrice(r.getRoomNumber(), p);
                HotelDatabase.persistRoom(r);
                showRooms();
            });
            pd.setResultConverter(btn -> null);
            pd.showAndWait();
        });

        Button addAmenityBtn = makeSmallBtn("Add Amenity", "#1a2a1a", "#10b981");
        addAmenityBtn.setOnAction(e -> {
            if (HotelDatabase.amenitys.isEmpty()) { UIHelper.showError("Error", "No amenities exist. Create one first."); return; }
            ChoiceDialog<String> d = new ChoiceDialog<>();
            for (Amenity a : HotelDatabase.amenitys) d.getItems().add(a.getName());
            d.setSelectedItem(HotelDatabase.amenitys.get(0).getName());
            d.setTitle("Add Amenity"); d.setHeaderText(null);
            d.setContentText("Select amenity to add:");
            styleDialog(d);
            d.showAndWait().ifPresent(chosen -> {
                for (Amenity a : HotelDatabase.amenitys) {
                    if (a.getName().equals(chosen)) {
                        if (r.getRoomAmenities().contains(a)) UIHelper.showError("Error", "Room already has this amenity.");
                        else { HotelDatabase.addRoomAmenity(r, a); UIHelper.showAlert("Success", "Amenity added!"); showRooms(); }
                        break;
                    }
                }
            });
        });

        Button deleteBtn = makeSmallBtn("Delete", "#2a1010", "#ef4444");
        deleteBtn.setOnAction(e -> {
            if (UIHelper.showConfirm("Delete Room", "Delete Room " + r.getRoomNumber() + "? This cannot be undone.")) {
                if (!HotelDatabase.deleteRoom(r.getRoomNumber())) {
                    UIHelper.showError("Cannot Delete", "Room " + r.getRoomNumber() +
                        " has active reservations and cannot be deleted.");
                } else {
                    showRooms();
                }
            }
        });

        actions.getChildren().addAll(editPriceBtn, addAmenityBtn, deleteBtn);
        card.getChildren().addAll(row, actions);
        return card;
    }

    private void showCreateRoomDialog(Runnable onSuccess) {
        if (HotelDatabase.roomTypes.isEmpty()) {
            UIHelper.showError("No Room Types", "Create a room type first before adding rooms.");
            return;
        }
        javafx.scene.control.Dialog<Void> d = new javafx.scene.control.Dialog<>();
        d.setTitle("Create Room"); d.setHeaderText(null);

        VBox content = new VBox(14);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #0d1525;");
        content.setPrefWidth(360);

        Label title = new Label("Create New Room");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        TextField numField = new TextField(); numField.setPromptText("e.g. 101"); styleInput(numField);
        TextField priceField = new TextField(); priceField.setPromptText("e.g. 250"); styleInput(priceField);
        TextField viewField = new TextField(); viewField.setPromptText("e.g. Sea View"); styleInput(viewField);

        ComboBox<String> typeBox = new ComboBox<>();
        for (RoomType rt : HotelDatabase.roomTypes) typeBox.getItems().add(rt.getTypeName());
        if (!typeBox.getItems().isEmpty()) typeBox.setValue(typeBox.getItems().get(0));
        typeBox.setMaxWidth(Double.MAX_VALUE);

        Label status = new Label(""); status.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");

        content.getChildren().addAll(title,
            makeLabeledNode("Room Number", numField),
            makeLabeledNode("Price per Night ($)", priceField),
            makeLabeledNode("View Preference", viewField),
            makeLabeledNode("Room Type", typeBox),
            status);

        d.getDialogPane().setContent(content);
        d.getDialogPane().setStyle("-fx-background-color: #0d1525;");

        ButtonType create = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        d.getDialogPane().getButtonTypes().addAll(create, cancel);

        javafx.scene.Node createBtn = d.getDialogPane().lookupButton(create);

        createBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            status.setText("");
            String num = numField.getText().trim();
            String view = viewField.getText().trim();
            String type = typeBox.getValue();
            if (num.isEmpty() || view.isEmpty() || type == null) { status.setText("Fill all fields."); event.consume(); return; }
            for (Room r : HotelDatabase.rooms) {
                if (r.getRoomNumber().equalsIgnoreCase(num)) { status.setText("Room already exists."); event.consume(); return; }
            }
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                RoomType selectedType = null;
                for (RoomType rt : HotelDatabase.roomTypes)
                    if (rt.getTypeName().equals(type)) { selectedType = rt; break; }
                Room newRoom = new Room(num, selectedType, price, view);
                HotelDatabase.addRoom(newRoom);
                UIHelper.showAlert("Success", "Room " + num + " created!");
                onSuccess.run();
            } catch (NumberFormatException ex) { status.setText("Invalid price."); event.consume(); }
            catch (IllegalArgumentException ex) { status.setText("Price cannot be negative."); event.consume(); }
        });
        d.setResultConverter(btn -> null);
        d.showAndWait();
    }

    // ───────── ROOM TYPES ─────────
    private void showRoomTypes() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Room Types");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button addBtn = new Button("+ Add Type");
        addBtn.setStyle("-fx-background-color: linear-gradient(to right, #c9a84c, #e8c97a); -fx-text-fill: #0a0e17;" +
            "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        addBtn.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog();
            d.setTitle("New Room Type"); d.setHeaderText(null);
            d.setContentText("Enter room type name (e.g. Suite, Double):");
            styleDialog(d);
            d.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    HotelDatabase.addRoomType(admin.createRoomType(name.trim()));
                    showRoomTypes();
                }
            });
        });
        header.getChildren().addAll(title, sp, addBtn);

        VBox list = new VBox(10);
        for (RoomType rt : HotelDatabase.roomTypes) {
            HBox card = new HBox(16);
            card.setPadding(new Insets(16, 20, 16, 20));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10;");
            Label name = new Label("🏷  " + rt.getTypeName());
            name.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
            HBox.setHgrow(name, Priority.ALWAYS);
            Button editBtn = makeSmallBtn("Rename", "#1e3a5f", "#00d4ff");
            editBtn.setOnAction(e -> {
                TextInputDialog d = new TextInputDialog(rt.getTypeName());
                d.setTitle("Rename"); d.setHeaderText(null); d.setContentText("New name:");
                styleDialog(d);
                d.showAndWait().ifPresent(n -> {
                    if (!n.trim().isEmpty()) {
                        String oldName = rt.getTypeName();
                        rt.setTypeName(n.trim());
                        HotelDatabase.renameRoomType(oldName, n.trim());
                        showRoomTypes();
                    }
                });
            });
            card.getChildren().addAll(name, editBtn);
            list.getChildren().add(card);
        }
        if (HotelDatabase.roomTypes.isEmpty()) {
            Label none = new Label("No room types yet.");
            none.setStyle("-fx-text-fill: rgba(180,200,255,0.5); -fx-font-size: 14px;");
            list.getChildren().add(none);
        }

        page.getChildren().addAll(header, list);
        setContent(page);
    }

    // ───────── AMENITIES ─────────
    private void showAmenities() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Manage Amenities");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button addBtn = new Button("+ Add Amenity");
        addBtn.setStyle("-fx-background-color: linear-gradient(to right, #c9a84c, #e8c97a); -fx-text-fill: #0a0e17;" +
            "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        addBtn.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog();
            d.setTitle("New Amenity"); d.setHeaderText(null);
            d.setContentText("Amenity name (e.g. WiFi, Pool):");
            styleDialog(d);
            d.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    HotelDatabase.addAmenity(new Amenity(name.trim()));
                    showAmenities();
                }
            });
        });
        header.getChildren().addAll(title, sp, addBtn);

        VBox list = new VBox(10);
        for (int i = 0; i < HotelDatabase.amenitys.size(); i++) {
            final Amenity a = HotelDatabase.amenitys.get(i);
            HBox card = new HBox(16);
            card.setPadding(new Insets(14, 20, 14, 20));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10;");
            Label name = new Label("✨  " + a.getName());
            name.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            HBox.setHgrow(name, Priority.ALWAYS);

            Button editBtn = makeSmallBtn("Rename", "#1e3a5f", "#00d4ff");
            editBtn.setOnAction(e -> {
                TextInputDialog d = new TextInputDialog(a.getName());
                d.setTitle("Rename Amenity"); d.setHeaderText(null); d.setContentText("New name:");
                styleDialog(d);
                d.showAndWait().ifPresent(n -> {
                    if (!n.trim().isEmpty()) {
                        String oldName = a.getName();
                        a.setName(n.trim());
                        HotelDatabase.renameAmenity(oldName, n.trim());
                        showAmenities();
                    }
                });
            });

            Button delBtn = makeSmallBtn("Delete", "#2a1010", "#ef4444");
            delBtn.setOnAction(e -> {
                if (UIHelper.showConfirm("Delete", "Delete amenity '" + a.getName() + "'?")) {
                    HotelDatabase.deleteAmenity(a);
                    showAmenities();
                }
            });
            card.getChildren().addAll(name, editBtn, delBtn);
            list.getChildren().add(card);
        }
        if (HotelDatabase.amenitys.isEmpty()) {
            Label none = new Label("No amenities yet.");
            none.setStyle("-fx-text-fill: rgba(180,200,255,0.5); -fx-font-size: 14px;");
            list.getChildren().add(none);
        }

        page.getChildren().addAll(header, list);
        setContent(page);
    }

    // ───────── RESERVATIONS ─────────
    private void showReservations() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        Label title = new Label("All Reservations");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox list = new VBox(10);
        for (Reservation res : HotelDatabase.reservations) {
            VBox card = new VBox(8);
            card.setPadding(new Insets(16));
            card.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10;");

            HBox row = new HBox(16);
            row.setAlignment(Pos.CENTER_LEFT);
            VBox info = new VBox(3);
            HBox.setHgrow(info, Priority.ALWAYS);
            Label rid = new Label("#" + res.getReservationId());
            rid.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
            Label rg = new Label("Guest: " + res.getGuest().getUsername() + " · Room: " + res.getRoom().getRoomNumber());
            rg.setStyle("-fx-text-fill: rgba(180,200,255,0.65); -fx-font-size: 13px;");
            info.getChildren().addAll(rid, rg);

            String sc = res.getStatus() == Reservation.ReservationStatus.CONFIRMED ? "#10b981" :
                res.getStatus() == Reservation.ReservationStatus.CANCELLED ? "#ef4444" : "#f59e0b";
            Label status = new Label(res.getStatus().toString());
            status.setStyle("-fx-background-color: " + sc + "22; -fx-text-fill: " + sc +
                "; -fx-background-radius: 20; -fx-padding: 4 12; -fx-font-size: 12px;");

            row.getChildren().addAll(info, status);
            card.getChildren().add(row);
            list.getChildren().add(card);
        }
        if (HotelDatabase.reservations.isEmpty()) {
            Label none = new Label("No reservations found.");
            none.setStyle("-fx-text-fill: rgba(180,200,255,0.5); -fx-font-size: 14px;");
            list.getChildren().add(none);
        }

        page.getChildren().addAll(title, list);
        setContent(page);
    }

    // ───────── GUESTS ─────────
    private void showGuests() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(32));

        Label title = new Label("All Guests");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox list = new VBox(10);
        for (Guest g : HotelDatabase.guests) {
            HBox card = new HBox(16);
            card.setPadding(new Insets(16));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10;");
            VBox info = new VBox(3);
            HBox.setHgrow(info, Priority.ALWAYS);
            Label gn = new Label("👤  " + g.getUsername());
            gn.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
            Label gd = new Label("Address: " + g.getAddress() + " · Gender: " + g.getGender());
            gd.setStyle("-fx-text-fill: rgba(180,200,255,0.6); -fx-font-size: 12px;");
            info.getChildren().addAll(gn, gd);
            Label bal = new Label("$" + String.format("%.2f", g.getBalance()));
            bal.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 16px; -fx-font-weight: bold;");
            card.getChildren().addAll(info, bal);
            list.getChildren().add(card);
        }
        if (HotelDatabase.guests.isEmpty()) {
            Label none = new Label("No guests registered.");
            none.setStyle("-fx-text-fill: rgba(180,200,255,0.5); -fx-font-size: 14px;");
            list.getChildren().add(none);
        }

        page.getChildren().addAll(title, list);
        setContent(page);
    }

    // ───────── HELPERS ─────────
    private Button makeSmallBtn(String text, String bg, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + color +
            "; -fx-font-size: 12px; -fx-background-radius: 6; -fx-padding: 7 14; -fx-cursor: hand;" +
            "-fx-border-color: " + color + "44; -fx-border-radius: 6; -fx-border-width: 1;");
        return btn;
    }

    private void styleInput(TextField f) {
        f.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(180,200,255,0.4); -fx-background-radius: 8;" +
            "-fx-border-color: rgba(201,168,76,0.15); -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 9 12;");
    }

    private VBox makeLabeledNode(String label, javafx.scene.Node node) {
        VBox b = new VBox(6);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: rgba(200,220,255,0.65); -fx-font-size: 12px;");
        b.getChildren().addAll(l, node);
        return b;
    }

    private void styleDialog(javafx.scene.control.Dialog<?> d) {
        try {
            d.getDialogPane().setStyle("-fx-background-color: #0d1525;");
            d.getDialogPane().lookup(".content.label") /*.setStyle("-fx-text-fill: white;")*/;
        } catch (Exception ignored) {}
    }

    public BorderPane getRoot() { return root; }
}
