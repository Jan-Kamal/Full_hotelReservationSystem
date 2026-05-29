import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.util.Duration;

public class LoginScreen {

    private StackPane root;

    public LoginScreen() {
        root = new StackPane();
        root.setStyle("-fx-background-color: #0a0e17;");
        buildUI();
    }

    private void buildUI() {

        Pane animBg = buildAnimatedBackground();


        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.rgb(5, 10, 25, 0.72));
        overlay.widthProperty().bind(root.widthProperty());
        overlay.heightProperty().bind(root.heightProperty());


        VBox panel = buildLoginPanel();


        VBox branding = buildBranding();

        HBox centerLayout = new HBox();
        centerLayout.setAlignment(Pos.CENTER);
        HBox.setHgrow(branding, Priority.ALWAYS);
        HBox.setHgrow(panel, Priority.SOMETIMES);
        centerLayout.getChildren().addAll(branding, panel);
        centerLayout.setPadding(new Insets(60, 80, 60, 80));
        centerLayout.setSpacing(80);
        centerLayout.setMaxWidth(1000);

        root.getChildren().addAll(animBg, overlay, centerLayout);
    }

    private Pane buildAnimatedBackground() {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: linear-gradient(to bottom, #050d1a, #091828, #0a1020);");
        pane.prefWidthProperty().bind(root.widthProperty());
        pane.prefHeightProperty().bind(root.heightProperty());


        for (int i = 0; i < 6; i++) {
            javafx.scene.shape.Circle orb = new javafx.scene.shape.Circle(
                30 + Math.random() * 120,
                Color.rgb(0, 150, 255, 0.04 + Math.random() * 0.06)
            );
            orb.setLayoutX(Math.random() * 1100);
            orb.setLayoutY(Math.random() * 720);

            TranslateTransition tt = new TranslateTransition(
                Duration.seconds(8 + Math.random() * 8), orb
            );
            tt.setByX((Math.random() - 0.5) * 200);
            tt.setByY((Math.random() - 0.5) * 200);
            tt.setAutoReverse(true);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.play();

            FadeTransition ft = new FadeTransition(Duration.seconds(4 + Math.random() * 4), orb);
            ft.setFromValue(0.3);
            ft.setToValue(1.0);
            ft.setAutoReverse(true);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.play();

            pane.getChildren().add(orb);
        }

        return pane;
    }

    private VBox buildBranding() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(420);

        // Gold star decoration
        Label star = new Label("★ ★ ★ ★ ★");
        star.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 18px; -fx-letter-spacing: 4px;");

        Label hotelName = new Label("AIN SHAMS");
        hotelName.setStyle(
            "-fx-font-family: 'Georgia'; -fx-font-size: 52px; -fx-font-weight: bold;" +
            "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,180,255,0.4), 20, 0, 0, 0);"
        );

        Label hotelSub = new Label("GRAND HOTEL");
        hotelSub.setStyle(
            "-fx-font-family: 'Georgia'; -fx-font-size: 28px;" +
            "-fx-text-fill: #c9a84c; -fx-letter-spacing: 8px;"
        );

        Label divider = new Label("─────────────────");
        divider.setStyle("-fx-text-fill: rgba(201,168,76,0.4); -fx-font-size: 14px;");

        Label tagline = new Label("Where every stay becomes\na timeless memory.");
        tagline.setStyle(
            "-fx-font-family: 'Georgia'; -fx-font-size: 17px; -fx-font-style: italic;" +
            "-fx-text-fill: rgba(200,220,255,0.75); -fx-line-spacing: 6;"
        );


        HBox stats = new HBox(40);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.getChildren().addAll(
            makeMiniStat("200+", "Rooms"),
            makeMiniStat("24/7", "Service"),
            makeMiniStat("5★", "Rated")
        );
        stats.setPadding(new Insets(20, 0, 0, 0));

        box.getChildren().addAll(star, hotelName, hotelSub, divider, tagline, stats);

        // Fade in animation
        FadeTransition ft = new FadeTransition(Duration.seconds(1.2), box);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.2), box);
        tt.setFromX(-40); tt.setToX(0); tt.play();

        return box;
    }

    private VBox makeMiniStat(String value, String label) {
        VBox b = new VBox(2);
        b.setAlignment(Pos.CENTER);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: rgba(200,220,255,0.6); -fx-font-size: 12px;");
        b.getChildren().addAll(v, l);
        return b;
    }

    private VBox buildLoginPanel() {
        VBox panel = new VBox(0);
        panel.setAlignment(Pos.CENTER);
        panel.setMinWidth(380);
        panel.setMaxWidth(400);
        panel.setStyle(
            "-fx-background-color: rgba(10,18,35,0.88);" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: rgba(201,168,76,0.25);" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 30, 0, 0, 8);"
        );


        HBox tabs = new HBox(0);
        tabs.setAlignment(Pos.CENTER);
        tabs.setStyle(
            "-fx-background-color: rgba(0,0,0,0.3);" +
            "-fx-background-radius: 18 18 0 0;"
        );

        ToggleGroup tg = new ToggleGroup();
        ToggleButton guestTab = makeTab("Guest", tg, true);
        ToggleButton adminTab = makeTab("Admin", tg, false);
        ToggleButton recepTab = makeTab("Reception", tg, false);
        tabs.getChildren().addAll(guestTab, adminTab, recepTab);
        HBox.setHgrow(guestTab, Priority.ALWAYS);
        HBox.setHgrow(adminTab, Priority.ALWAYS);
        HBox.setHgrow(recepTab, Priority.ALWAYS);


        VBox formArea = new VBox(16);
        formArea.setPadding(new Insets(30, 32, 30, 32));

        Label formTitle = new Label("Welcome Back");
        formTitle.setStyle(
            "-fx-font-family: 'Georgia'; -fx-font-size: 22px; -fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        Label formSub = new Label("Sign in to continue");
        formSub.setStyle("-fx-text-fill: rgba(180,200,255,0.6); -fx-font-size: 13px;");

        VBox.setMargin(formSub, new Insets(0, 0, 8, 0));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleInput(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleInput(passwordField);

        Label statusLabel = new Label("");
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");

        Button loginBtn = new Button("SIGN IN");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #c9a84c, #e8c97a);" +
            "-fx-text-fill: #0a0e17; -fx-font-weight: bold; -fx-font-size: 14px;" +
            "-fx-background-radius: 8; -fx-padding: 14 0;"
        );
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #e8c97a, #c9a84c);" +
            "-fx-text-fill: #0a0e17; -fx-font-weight: bold; -fx-font-size: 14px;" +
            "-fx-background-radius: 8; -fx-padding: 14 0; -fx-cursor: hand;"
        ));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #c9a84c, #e8c97a);" +
            "-fx-text-fill: #0a0e17; -fx-font-weight: bold; -fx-font-size: 14px;" +
            "-fx-background-radius: 8; -fx-padding: 14 0;"
        ));

        // Register link (only for guest tab)
        Button registerBtn = new Button("New guest? Register here");
        registerBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #c9a84c;" +
            "-fx-font-size: 12px; -fx-cursor: hand; -fx-underline: true;"
        );

        // Login action
        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();
            if (user.isEmpty() || pass.isEmpty()) {
                statusLabel.setText("Please enter both username and password.");
                return;
            }
            if (guestTab.isSelected()) handleGuestLogin(user, pass, statusLabel);
            else if (adminTab.isSelected()) handleAdminLogin(user, pass, statusLabel);
            else handleReceptionistLogin(user, pass, statusLabel);
        });

        // Enter key login
        passwordField.setOnAction(e -> loginBtn.fire());

        registerBtn.setOnAction(e -> showRegisterDialog());

        // Show/hide register button based on tab
        guestTab.setOnAction(e -> {
            formTitle.setText("Welcome Back");
            registerBtn.setVisible(true);
        });
        adminTab.setOnAction(e -> {
            formTitle.setText("Admin Portal");
            registerBtn.setVisible(false);
        });
        recepTab.setOnAction(e -> {
            formTitle.setText("Reception Desk");
            registerBtn.setVisible(false);
        });

        formArea.getChildren().addAll(
            formTitle, formSub,
            makeInputBlock("Username", usernameField),
            makeInputBlock("Password", passwordField),
            statusLabel, loginBtn, registerBtn
        );

        panel.getChildren().addAll(tabs, formArea);

        // Animate panel in
        FadeTransition ft = new FadeTransition(Duration.seconds(1.0), panel);
        ft.setFromValue(0); ft.setToValue(1); ft.setDelay(Duration.seconds(0.3)); ft.play();
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.0), panel);
        tt.setFromY(30); tt.setToY(0); tt.setDelay(Duration.seconds(0.3)); tt.play();

        return panel;
    }

    private VBox makeInputBlock(String label, javafx.scene.control.Control field) {
        VBox block = new VBox(6);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: rgba(200,220,255,0.7); -fx-font-size: 12px; -fx-font-weight: bold;");
        block.getChildren().addAll(lbl, field);
        return block;
    }

    private void styleInput(TextInputControl field) {
        field.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-text-fill: white; -fx-prompt-text-fill: rgba(180,200,255,0.4);" +
            "-fx-background-radius: 8; -fx-border-color: rgba(201,168,76,0.2);" +
            "-fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 14;"
        );
        field.focusedProperty().addListener((obs, old, focused) -> {
            if (focused) {
                field.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.1);" +
                    "-fx-text-fill: white; -fx-prompt-text-fill: rgba(180,200,255,0.4);" +
                    "-fx-background-radius: 8; -fx-border-color: #c9a84c;" +
                    "-fx-border-radius: 8; -fx-border-width: 1.5; -fx-padding: 12 14;"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.07);" +
                    "-fx-text-fill: white; -fx-prompt-text-fill: rgba(180,200,255,0.4);" +
                    "-fx-background-radius: 8; -fx-border-color: rgba(201,168,76,0.2);" +
                    "-fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 14;"
                );
            }
        });
    }

    private ToggleButton makeTab(String text, ToggleGroup tg, boolean selected) {
        ToggleButton tb = new ToggleButton(text);
        tb.setToggleGroup(tg);
        tb.setSelected(selected);
        tb.setMaxWidth(Double.MAX_VALUE);
        tb.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: rgba(200,220,255,0.5);" +
            "-fx-font-size: 13px; -fx-padding: 14 0; -fx-background-radius: 0; -fx-border-width: 0;"
        );
        tb.selectedProperty().addListener((obs, old, sel) -> {
            if (sel) {
                tb.setStyle(
                    "-fx-background-color: rgba(201,168,76,0.12);" +
                    "-fx-text-fill: #c9a84c; -fx-font-weight: bold;" +
                    "-fx-font-size: 13px; -fx-padding: 14 0; -fx-background-radius: 0;" +
                    "-fx-border-color: transparent transparent #c9a84c transparent; -fx-border-width: 0 0 2 0;"
                );
            } else {
                tb.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: rgba(200,220,255,0.5);" +
                    "-fx-font-size: 13px; -fx-padding: 14 0; -fx-background-radius: 0; -fx-border-width: 0;"
                );
            }
        });
        return tb;
    }

    private void handleGuestLogin(String user, String pass, Label status) {
        for (Guest g : HotelDatabase.guests) {
            if (g.login(user, pass)) {
                SceneManager.showGuestDashboard(g);
                return;
            }
        }
        status.setText("Invalid username or password.");
    }

    private void handleAdminLogin(String user, String pass, Label status) {
        for (Admin a : HotelDatabase.admins) {
            if (a.login(user, pass)) {
                SceneManager.showAdminDashboard(a);
                return;
            }
        }
        status.setText("Invalid admin credentials.");
    }

    private void handleReceptionistLogin(String user, String pass, Label status) {
        for (Receptionist r : HotelDatabase.receptionists) {
            if (r.login(user, pass)) {
                SceneManager.showReceptionistDashboard(r);
                return;
            }
        }
        status.setText("Invalid receptionist credentials.");
    }

    private void showRegisterDialog() {
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Guest Registration");
        dialog.setHeaderText(null);

        VBox content = new VBox(14);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #0d1525;");
        content.setPrefWidth(360);

        Label title = new Label("Create Your Account");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: Georgia;");

        TextField nameField = new TextField(); nameField.setPromptText("Full Name"); styleInput(nameField);
        PasswordField passField = new PasswordField(); passField.setPromptText("Password (min 6 chars)"); styleInput(passField);
        TextField balField = new TextField(); balField.setPromptText("Initial Balance ($)"); styleInput(balField);
        TextField addrField = new TextField(); addrField.setPromptText("Address"); styleInput(addrField);

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("MALE", "FEMALE");
        genderBox.setValue("MALE");
        genderBox.setMaxWidth(Double.MAX_VALUE);
        genderBox.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-text-fill: white;");

        Label statusLbl = new Label("");
        statusLbl.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        statusLbl.setWrapText(true);

        content.getChildren().addAll(
            title,
            makeLabeledField("Full Name", nameField),
            makeLabeledField("Password", passField),
            makeLabeledField("Balance ($)", balField),
            makeLabeledField("Address", addrField),
            makeLabeledField("Gender", genderBox),
            statusLbl
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle("-fx-background-color: #0d1525; -fx-padding: 0;");

        ButtonType registerType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(registerType, cancelType);

        javafx.scene.Node regBtn = dialog.getDialogPane().lookupButton(registerType);
        regBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #c9a84c, #e8c97a);" +
            "-fx-text-fill: #0a0e17; -fx-font-weight: bold; -fx-background-radius: 6;"
        );

        regBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            statusLbl.setText("");
            String name = nameField.getText().trim();
            String pass = passField.getText().trim();
            String balStr = balField.getText().trim();
            String addr = addrField.getText().trim();
            String gen = genderBox.getValue();

            if (name.isEmpty() || pass.isEmpty() || balStr.isEmpty() || addr.isEmpty()) {
                statusLbl.setText("Please fill all fields.");
                event.consume();
                return;
            }
            double bal;
            try { bal = Double.parseDouble(balStr); }
            catch (NumberFormatException ex) {
                statusLbl.setText("Balance must be a number.");
                event.consume();
                return;
            }

            Guest.Gender gender = gen.equals("MALE") ? Guest.Gender.MALE : Guest.Gender.FEMALE;
            try {
                Guest newGuest = new Guest(name, pass, java.time.LocalDate.now(), bal, addr, gender);
                HotelDatabase.addGuest(newGuest);
                UIHelper.showAlert("Success", "Account created! Welcome, " + name + "!\nYou can now log in.");
            } catch (IllegalArgumentException ex) {
                statusLbl.setText(ex.getMessage());
                event.consume();
            } catch (NegativeBalanceException ex) {
                statusLbl.setText(ex.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(btn -> null);
        dialog.showAndWait();
    }

    private VBox makeLabeledField(String label, javafx.scene.Node field) {
        VBox b = new VBox(5);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: rgba(200,220,255,0.65); -fx-font-size: 12px;");
        b.getChildren().addAll(l, field);
        return b;
    }

    public StackPane getRoot() { return root; }
}
