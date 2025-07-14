package de.robinfrederik.drunkracing;

// here we import the core project classes
import de.robinfrederik.drunkracing.physics.ackermann.AckermannModel;
import de.robinfrederik.drunkracing.car.CarVisual;
import de.robinfrederik.drunkracing.mvp.CarVisualTest;
import de.robinfrederik.drunkracing.physics.PhysicsLoop;
import de.robinfrederik.drunkracing.physics.ackermann.CarGoKartSportModel;

// now we import all the necessary JavaFX libaries, we'll shortly comment their usage for our project in the following
import javafx.animation.AnimationTimer; // For creating game loop with high frequency (we aim at ca 60 fps)
import javafx.application.Application; // Base class for any JavaFX application
import javafx.geometry.Pos; // For alignment (e.g. in VBox/StackPane)
import javafx.scene.Scene; // Main container for UI content
import javafx.scene.Group; // A container for visual nodes
import javafx.scene.canvas.Canvas; // Used to draw maps with tiles
import javafx.scene.canvas.GraphicsContext; // Graphics context for drawing on Canvas
import javafx.scene.control.Label; // For UI labels
import javafx.scene.effect.DropShadow; // Drop shadow visual effect
import javafx.scene.image.Image; // Class to load images
import javafx.scene.image.ImageView; // UI component to show an image
import javafx.scene.input.KeyCode; // Represents keys on keyboard (arrow-keys are unfortunately not working)
import javafx.scene.paint.Color; // Used to define colors
import javafx.scene.text.Font; // Used to define fonts
import javafx.scene.text.FontWeight; // Used to define font weight (bold, etc.)
import javafx.scene.layout.Pane; // Basic layout container
import javafx.scene.layout.StackPane; // Layout that stacks nodes on top of each other
import javafx.scene.layout.VBox; // Vertical layout manager
import javafx.scene.control.Button; // Button element
import javafx.stage.Stage; // The main application window
import javafx.scene.effect.ColorAdjust; // Visual effect to adjust color (used for brightness)
import javafx.scene.control.Slider; // Slider component for settings

// here are some more imports for other important JavaFX + Java utility
import java.util.Map; // Interface for key-value data structures
import java.util.HashMap; // Implementation of Map
import javafx.scene.image.PixelReader; // Allows reading individual pixels from an image
import javafx.application.Platform; // Allows running tasks on JavaFX application thread
import javafx.animation.KeyFrame; // Represents a single keyframe in a Timeline
import javafx.animation.Timeline; // Used to create time-based animations or countdowns (starting countdown)
import javafx.util.Duration; // Used to specify time durations

// here is our Main JavaFX game application class
public class Main extends Application {

    // in the following we define many things (variables, physics, keybinds, etc.)

    // we start with the stage and scenes
    private Stage primaryStage;
    private Scene mainMenuScene, difficultyScene, mapSelectionScene, gameScene;

    // here we have the main containers and components
    private Pane root; // Root container for the game scene
    private Group world; // Group that contains all game elements (car, map, etc.)
    private Canvas mapCanvas; // Canvas to draw test map with tiles (the test map or testground isn't visible anymroe in the main game -> only for test purposes)
    private Label hudLabel; // Label to show HUD text in game

    // physics
    private PhysicsLoop physicsLoop = new PhysicsLoop(new CarGoKartSportModel(), 0.001); // Physics engine with specific car model and time step
    private CarVisual car = new CarVisual(); // Visual and logical representation of the player's car (our car pic)
    private boolean up, down, left, right; // Input flags for driving directions

    // map settings
    private final double tileSize = 50; // Size of one tile on the test map
    private final double mapWidth = 5000; // Total map width in pixels
    private final double mapHeight = 5000; // Total map height in pixels

    private String currentDifficulty = "SOBER"; // Default difficulty mode
    private String selectedMap = "Test-Ground"; // test-map (not visible in the game)

    private double wavePhase = 0; // Phase variable used for visual drunk effects

    // our display settings (brightness)
    private double brightnessValue = 0; // Brightness slider value (0 = normal)

    // timer and game state
    private long startTime; // Time when race started (in ns = nanoseconds)
    private boolean raceStarted = false; // True once countdown is over
    private boolean finished = false; // True if player finished race
    private double startX, startY; // Start position of the car

    // UI (Interface-design)
    private Label countdownLabel; // Label used to show countdown numbers (3,2,1,GO)
    private Label timeLabel; // Label used to show race time
    private boolean inputBlocked = false; // Used to block inputs before race begins

    // images for maps (2 and Racetrack / map3)
    private ImageView map2View; // image-based map for Map 2
    private ImageView map3View; // image-based map for Map 3, which is now called Racetrack (but it was called map 3 since it was our third map xD but we changed it in the code later in the process to "Racetrack" for the main game)

    // game state flags
    private boolean gameOverActive = false; // True if game over is triggered

    // JavaFX application entry point
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage; // Save reference to the main stage

        setupMainMenu();  // Setup title screen
        setupDifficultyScene(); // Setup difficulty selection screen
        setupMapSelectionScene(); // Setup map selection screen
        setupGameScene(); // Setup the actual game view and logic

        primaryStage.setScene(mainMenuScene); // Start with main menu
        primaryStage.setTitle("Drunk Racing"); // Set window title
        primaryStage.show(); // Show the application window

        primaryStage.setOnCloseRequest(event -> {
            if (physicsLoop != null) {
                physicsLoop.stopLoop(); // Stop physics updates
            }
            Platform.exit();            // Exit JavaFX application
            System.exit(0);      // Ensure JVM process shuts down
        });
    }

    // Sets up the main menu screen with title and navigation buttons
    private void setupMainMenu() {
        Image backgroundImage = new Image(getClass().getResource("/images/titlescreen.png").toExternalForm()); // Loads our crazy good title screen image
        ImageView backgroundView = new ImageView(backgroundImage); // Create an image view to display background
        backgroundView.setFitWidth(800); // Set background width
        backgroundView.setFitHeight(600); // Set background height
        backgroundView.setPreserveRatio(false); // Do not preserve image ratio to stretch

        Label title = new Label("Drunk Racing"); // Game title label
        title.setFont(Font.font("MV Boli", FontWeight.BOLD, 60)); // Set title font and size
        title.setTextFill(Color.RED); // Set title text color to red

        DropShadow outline = new DropShadow(); // Create drop shadow for title
        outline.setColor(Color.WHITE); // Shadow color is white
        outline.setOffsetX(0); // No horizontal offset
        outline.setOffsetY(0); // No vertical offset
        outline.setRadius(2); // Radius of the shadow
        outline.setSpread(1); // Shadow spread
        title.setEffect(outline); // Apply the effect to the title

        Button playButton = new Button("Play"); // Button to start the game
        Button rulesButton = new Button("Rules"); // Button to show rules
        Button settingsButton = new Button("Settings"); // Button to open settings
        Button quitButton = new Button("Quit"); // Button to quit the game

        playButton.setOnAction(e -> primaryStage.setScene(difficultyScene)); // Go to difficulty selection scene
        rulesButton.setOnAction(e -> showRulesScene()); // Open rules scene
        settingsButton.setOnAction(e -> showSettingsScene()); // Open settings scene
        quitButton.setOnAction(e -> primaryStage.close()); // Exit the application

        VBox menuLayout = new VBox(30, title, playButton, rulesButton, settingsButton, quitButton); // Arrange items vertically
        menuLayout.setAlignment(Pos.CENTER); // Center align the menu items

        StackPane stackPane = new StackPane(backgroundView, menuLayout); // Stack background and menu items
        mainMenuScene = new Scene(stackPane, 800, 600); // Create the main menu scene with specified size
    }

    // now we come to the difficulty selection
    private void setupDifficultyScene() {

        // as before we make the background etc. (won't comment this again)
        Image bg = new Image(getClass().getResource("/images/background.png").toExternalForm());
        ImageView bgView = new ImageView(bg);
        bgView.setFitWidth(800);
        bgView.setFitHeight(600);

        // here we create the title of the interface
        Label title = new Label("Choose Difficulty");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        // difficulty buttons are made (to choose drunk effect)
        Button soberButton = new Button("SOBER");
        Button tipsyButton = new Button("TIPSY");
        Button wastedButton = new Button("WASTED");
        Button blackoutButton = new Button("BLACKOUT");
        Button backButton = new Button("Back to Menu");

        // here we declare what happens when each button is pressed (e.g. sober button -> sets sober and got to map selector)
        soberButton.setOnAction(e -> selectDifficultyAndProceed("SOBER"));
        tipsyButton.setOnAction(e -> selectDifficultyAndProceed("TIPSY"));
        wastedButton.setOnAction(e -> selectDifficultyAndProceed("WASTED"));
        blackoutButton.setOnAction(e -> selectDifficultyAndProceed("BLACKOUT"));
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));

        VBox buttonsLayout = new VBox(15, soberButton, tipsyButton, wastedButton, blackoutButton); // Arrange difficulty buttons
        buttonsLayout.setAlignment(Pos.CENTER); // Center align buttons

        VBox layout = new VBox(30, title, buttonsLayout, backButton); // Combine title and buttons
        layout.setAlignment(Pos.CENTER); // Center align everything

        StackPane stackPane = new StackPane(bgView, layout); // Stack background and layout
        difficultyScene = new Scene(stackPane, 800, 600); // Create difficulty selection scene
    }

    // method name pretty much explains the function
    private void selectDifficultyAndProceed(String difficulty) {
        currentDifficulty = difficulty; // Save the chosen difficulty
        primaryStage.setScene(mapSelectionScene); // Show the map selection screen
    }

    // here is the map selection (in our game is due to missing tiome only 1 functional map)
    private void setupMapSelectionScene() {

        // this part isn't new, just a new interface with buttons and our background pic (won't explain this again either)
        Image bg = new Image(getClass().getResource("/images/background.png").toExternalForm());
        ImageView bgView = new ImageView(bg);
        bgView.setFitWidth(800);
        bgView.setFitHeight(600);

        Label title = new Label("Choose Map");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Button testGroundButton = new Button("Test-Ground"); got deleted from the interface, becasue it was only for test purposes
        // Button map2Button = new Button("Map 2"); got deleted from the interface, becasue it was only for test purposes
        Button RacetrackButton = new Button("Racetrack");
        RacetrackButton.setOnAction(e -> startGameWithMap("Racetrack"));
        Button backButton = new Button("Back to Difficulty");

        // testGroundButton.setOnAction(e -> startGameWithMap("Test-Ground")); got deleted from the interface, becasue it was only for test purposes
        // map2Button.setOnAction(e -> startGameWithMap("Map 2")); got deleted from the interface, becasue it was only for test purposes
        backButton.setOnAction(e -> primaryStage.setScene(difficultyScene));

        VBox layout = new VBox(20, title, RacetrackButton, backButton);  // testground and map2 were also here deleted
        layout.setAlignment(Pos.CENTER);

        StackPane stackPane = new StackPane(bgView, layout);
        mapSelectionScene = new Scene(stackPane, 800, 600);
    }

    // Starts the game with the chosen map (self explaining isn't it? xD)
    private void startGameWithMap(String map) {
        selectedMap = map; // Save selected map globally
        resetGame(); // Reset car position and game state
        applyDifficultyEffect(); // Apply blur or screen effects for selected difficulty
        primaryStage.setScene(gameScene); // Show the game scene
        root.requestFocus(); // Request keyboard focus so controls work immediately
    }

    // now the game scene
    private void setupGameScene() {
        mapCanvas = new Canvas(mapWidth, mapHeight); // Create a blank canvas for manual map drawing (Test-Ground)

        // load map 2 (not important fpr the game)
        Image map2Image = new Image(getClass().getResource("/images/mapTest1.png").toExternalForm());
        map2View = new ImageView(map2Image);
        map2View.setPreserveRatio(false);
        map2View.setFitWidth(mapWidth);
        map2View.setFitHeight(mapHeight);

        // load map 3 (Racetrack)
        Image map3Image = new Image(getClass().getResource("/images/map3.png").toExternalForm()); // Load Map 3 (Racetrack) image
        map3View = new ImageView(map3Image); // ImageView for Map 3
        map3View.setPreserveRatio(false); // Do not preserve aspect ratio
        map3View.setFitWidth(mapWidth); // Scale to canvas width
        map3View.setFitHeight(mapHeight); // Scale to canvas height

        world = new Group(); // Create a container for all world elements (map, car, etc.)
        hudLabel = new Label(); // HUD label to show info like lap, speed, etc.
        hudLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold;"); // Style HUD
        hudLabel.setTranslateX(10); // Position X
        hudLabel.setTranslateY(10); // Position Y

        Pane layeredWorld = new Pane(world, hudLabel); // Overlay HUD on top of world

        countdownLabel = new Label(); // Label to display countdown (3, 2, 1, GO!)
        countdownLabel.setFont(Font.font("Arial", FontWeight.BOLD, 100)); // Set font and size
        countdownLabel.setTextFill(Color.WHITE); // White text
        countdownLabel.setAlignment(Pos.CENTER); // Center text
        countdownLabel.setTranslateY(-200); // Move label up
        StackPane.setAlignment(countdownLabel, Pos.CENTER); // Center it in StackPane

        timeLabel = new Label("0.000 s"); // Label to display race time
        timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30)); // Font and size for time
        timeLabel.setTextFill(Color.WHITE); // White color
        timeLabel.setVisible(false); // Initially hidden
        StackPane.setAlignment(timeLabel, Pos.TOP_CENTER); // Position at top center

        root = new StackPane(layeredWorld, countdownLabel, timeLabel); // Combine all into root stack

        gameScene = new Scene(root, 800, 600); // Create the scene for gameplay

        gameScene.setOnKeyPressed(e -> {
            // Allow ESC anytime in the game
            if (e.getCode() == KeyCode.ESCAPE) { // If ESC pressed
                primaryStage.setScene(mainMenuScene); // Return to main menu
                physicsLoop.stopLoop(); // Stop physics loop
                return;
            }

            if (!raceStarted || inputBlocked || gameOverActive || finished) return; // Ignore keys if game not running

            if (e.getCode() == keyBindings.get("up")) up = true; // Set up flag
            if (e.getCode() == keyBindings.get("down")) down = true; // Set down flag
            if (e.getCode() == keyBindings.get("left")) left = true; // Set left flag
            if (e.getCode() == keyBindings.get("right")) right = true; // Set right flag
        });


        gameScene.setOnKeyReleased(e -> {
            if (!raceStarted || inputBlocked || gameOverActive || finished) return; // Ignore if game not running

            if (e.getCode() == keyBindings.get("up")) up = false; // Clear up flag
            if (e.getCode() == keyBindings.get("down")) down = false; // Clear down flag
            if (e.getCode() == keyBindings.get("left")) left = false; // Clear left flag
            if (e.getCode() == keyBindings.get("right")) right = false; // Clear right flag
        });

        AckermannModel model = new CarGoKartSportModel(); // Create car physics model (Ackermann steering)

        // our main game loop and related methods come now in the animation timer (main purpose = animating the scene in 60 fps)
        new AnimationTimer() {
            long lastUpdate = System.nanoTime(); // Store time of last update

            @Override
            public void handle(long now) {

                if (!raceStarted || gameOverActive || finished) return; // Skip if game isn't running

                // Update timer display
                long currentTime = System.nanoTime(); // Get current system time
                double elapsedSeconds = (currentTime - startTime) / 1e9; // Calculate elapsed time in seconds
                timeLabel.setText(String.format("%.3f s", elapsedSeconds)); // Display it with 3 decimal places

                if (gameOverActive) return; // Skip further "Animation-updates" if game is over

                double deltaTime = (now - lastUpdate) / 1e9; // Compute time since last frame
                lastUpdate = now; // Update last timestamp

                // Check if player has finished the race -> crossed finish line -> victory detection
                double dx = car.getX() - startX; // X difference from start
                double dy = car.getY() - startY; // Y difference from start
                double distance = Math.sqrt(dx * dx + dy * dy); // Distance from start (not used directly)

                if (Math.abs(car.getX() - startX) < 600 && Math.abs(car.getY() - startY) < 20 && elapsedSeconds > 8) { // here we create a rectangle across the starting / finish line which will be active after 8s into the game start, den if the car touches the rectangle, the victory-screen with your time will appear
                    finished = true; // Flag game as finished
                    showVictoryScreen(elapsedSeconds); // Show win screen with time
                    return;
                }

                car.update(up, down, left, right); // update car inputs

                // Check for collision on Map 2 (irrelevant) using red pixel detection and image coordinates ===
                if (!gameOverActive && (selectedMap.equals("Map 2") || selectedMap.equals("Racetrack"))) {
                    // Get current image depending on selected map
                    Image currentMapImage = selectedMap.equals("Map 2")
                            ? map2View.getImage()
                            : map3View.getImage();

                    PixelReader reader = currentMapImage.getPixelReader(); // Create PixelReader to get color data

                    // Calculate scale factor from image to world coordinates
                    double scaleX = selectedMap.equals("Map 2")
                            ? map2View.getFitWidth() / currentMapImage.getWidth()
                            : map3View.getFitWidth() / currentMapImage.getWidth();

                    double scaleY = selectedMap.equals("Map 2")
                            ? map2View.getFitHeight() / currentMapImage.getHeight()
                            : map3View.getFitHeight() / currentMapImage.getHeight();

                    // Convert world coordinates to image pixel coordinates
                    int baseX = (int) (car.getX() / scaleX);
                    int baseY = (int) (car.getY() / scaleY);

                    int[][] offsets = {
                            {0, 0}, {-10, 0}, {10, 0}, {0, -10}, {0, 10} // Pixel offsets for surrounding area, so basically the hitbox of the car
                    };

                    for (int[] offset : offsets) {
                        int px = baseX + offset[0]; // Offset x
                        int py = baseY + offset[1]; // Offset y

                        // Only check pixels that are within the map bounds
                        if (px >= 0 && py >= 0 &&
                                px < currentMapImage.getWidth() &&
                                py < currentMapImage.getHeight()) {

                            Color color = reader.getColor(px, py); // Read color at pixel

                            if (selectedMap.equals("Map 2")) {
                                // red zone detection for Map 2 (irrelevant for the current game)
                                boolean isDark = color.getBrightness() < 0.2;
                                boolean isClearlyRed = color.getRed() - color.getGreen() > 0.4 &&
                                        color.getRed() - color.getBlue() > 0.4 &&
                                        color.getRed() > 0.5; // Red dominance

                                if (isClearlyRed && !isDark) {
                                    gameOverActive = true;
                                    triggerGameOver(); // Trigger Game Over
                                    return;
                                }

                            } else if (selectedMap.equals("Racetrack")) {
                                // green zone detection for Map 3 / Racetrack -Y meaning if the car touches the color green the game over screens appears
                                boolean isClearlyGreen = color.getGreen() - color.getRed() > 0.2 &&
                                        color.getGreen() - color.getBlue() > 0.1 &&
                                        color.getGreen() > 0.4; // Green dominance

                                if (isClearlyGreen) {
                                    gameOverActive = true; // Trigger Game Over
                                    triggerGameOver();
                                    return;
                                }
                            }
                        }
                    }
                }

                //  Update car
                car.getCar().updateState(new CarGoKartSportModel(), car.getSteeringInput(), car.getAccelInput(), deltaTime); // Update car's physics state with model and inputs

                //  Update visuals
                car.setTranslateX(car.getX()); // Update visual x-position
                car.setTranslateY(car.getY()); // Update visual y-position
                car.updateVisuals(); // Update visual representation of the car (sprite, rotation, etc.)
                // car.updateForceArrows(); this was only needed to test physics etc. and is not important in the actual game

                // Camera follow -> basically we move the map in the opposite direction of the moving car, this way the car is always centered
                double offsetX = -car.getTranslateX() + gameScene.getWidth() / 2; // Calculate horizontal camera offset to center car
                double offsetY = -car.getTranslateY() + gameScene.getHeight() / 2; // Calculate vertical camera offset to center car
                world.setTranslateX(offsetX); // Apply horizontal offset to world
                world.setTranslateY(offsetY); // Apply vertical offset to world

                // here we define the Drunk motion effects
                if (currentDifficulty.equals("BLACKOUT")) {
                    wavePhase += 0.2; // Update wave phase for motion

                    // Combine chaotic shake and smooth wave
                    double shakeX = (Math.random() - 0.5) * 30; // Random horizontal shake
                    double shakeY = (Math.random() - 0.5) * 30; // Random vertical shake

                    double waveX = Math.sin(wavePhase) * 40; // here we make sinus like waves (horizontal wave)
                    double waveY = Math.cos(wavePhase * 0.5) * 20; // here we make sinus like waves (vertical wave)

                    root.setTranslateX(shakeX + waveX); // Apply combined horizontal distortion
                    root.setTranslateY(shakeY + waveY); // Apply combined vertical distortion
                }
                else if (currentDifficulty.equals("WASTED")) {
                    wavePhase += 0.2; // Update wave phase
                    double waveX = Math.sin(wavePhase) * 40; // Only horizontal wave motion
                    root.setTranslateX(waveX); // Apply horizontal wave
                    root.setTranslateY(0); // No vertical movement
                } else if (currentDifficulty.equals("TIPSY")) {
                    wavePhase += 0.1; // Slower wave motion
                    double swayX = Math.sin(wavePhase) * 20; // Mild horizontal wave
                    root.setTranslateX(swayX); // Apply sway
                    root.setTranslateY(0);  // Again no vertical movement
                } else {
                    root.setTranslateX(0); // Reset horizontal camera shake
                    root.setTranslateY(0); // Reset vertical camera shake
                }
            }
        }.start(); // Now start the animation timer (game loop)
    }

    //  Apply drunk effects for the different maps, but in the game will only Racetrack (map3) visible
    private void applyDifficultyEffect() {
        world.getChildren().clear(); // Clear existing world elements

        if (selectedMap.equals("Test-Ground")) {
            drawMap();
            world.getChildren().addAll(mapCanvas, car);
            root.setStyle("-fx-background-color: black;");
        } else if (selectedMap.equals("Map 2")) {
            world.getChildren().addAll(map2View, car);
            root.setStyle("-fx-background-color: black;");
        }
        else if (selectedMap.equals("Racetrack")) {
            world.getChildren().addAll(map3View, car); // Add Racetrack image and car
            root.setStyle("-fx-background-color: black;");
        }


        world.setEffect(null); // Clear any previous visual effects
        switch (currentDifficulty) {
            case "SOBER": break; // No effect
            case "TIPSY": world.setEffect(new javafx.scene.effect.GaussianBlur(12)); break; // Light blur
            case "WASTED": world.setEffect(new javafx.scene.effect.GaussianBlur(20)); break; // Stronger blur
            case "BLACKOUT":
                javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(28); // Even stronger blur
                javafx.scene.effect.ColorAdjust color = new javafx.scene.effect.ColorAdjust(); // Color distortion
                color.setHue(0.7); // Purple tint
                color.setSaturation(0.2); // Slight color change
                color.setInput(blur); // Apply blur as input
                world.setEffect(color); // Apply combined effect
                break;
        }
    }

    // In "drawMap" we made our test-ground (kitchen like floor) to test the physics (speed, acceleration, etc.) -> but it's irrelevant for the actual game
    private void drawMap() {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        int tilesX = (int) (mapWidth / tileSize);
        int tilesY = (int) (mapHeight / tileSize);

        for (int y = 0; y < tilesY; y++) {
            for (int x = 0; x < tilesX; x++) {
                gc.setFill((x + y) % 2 == 0 ? Color.BLACK : Color.WHITE);
                gc.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }
    }


    private Timeline countdownTimeline; // Timeline for countdown before race start

    private void resetGame() {
        // Remove any Victory/GameOver overlays
        root.getChildren().removeIf(node -> node instanceof VBox);

        physicsLoop.stopLoop(); // Stop any running physics loop

        if (countdownTimeline != null) {
            countdownTimeline.stop(); // Stop countdown if already running
        }

        // Remove previous car if it exists
        if (this.car != null) {
            world.getChildren().remove(this.car);
        }

        // Create new car instance and add to world
        this.car = new CarVisual();
        world.getChildren().add(this.car);

        //  Set start position depending on map (only Racetrack is relevant)
        double startX;
        double startY;
        if (selectedMap.equals("Test-Ground")) {
            startX = mapWidth / 2;
            startY = mapHeight / 2;
        } else if (selectedMap.equals("Map 2")) {
            startX = mapWidth / 2 + 1050;
            startY = mapHeight / 2 + 550;
        } else if (selectedMap.equals("Racetrack")) {
            startX = mapWidth / 2 + 120;
            startY = mapHeight / 2 + 500;
        } else {
            startX = mapWidth / 2 + 120;
            startY = mapHeight / 2 + 500;
        }

        // Set position and orientation of the car
        car.setTranslateX(startX);
        car.setX(startX);
        car.setTranslateY(startY);
        car.setY(startY);
        car.getCar().getState().setYaw(Math.toRadians(-90)); // Face upwards
        car.updateVisuals();

        // Save start coordinates for victory detection
        this.startX = startX;
        this.startY = startY;

        // Center camera on car
        world.setTranslateX(-car.getTranslateX() + gameScene.getWidth() / 2);
        world.setTranslateY(-car.getTranslateY() + gameScene.getHeight() / 2);

        // Reset game state
        raceStarted = false;
        finished = false;
        inputBlocked = true;
        up = down = left = right = false;

        // Setup countdown display
        countdownLabel.setText("3");
        countdownLabel.setVisible(true);
        timeLabel.setVisible(false);

        countdownTimeline = new Timeline();
        countdownTimeline.setCycleCount(1);

        // Add countdown keyframes (3 → 2 → 1 → GO → hide)
        countdownTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(1), e -> countdownLabel.setText("2")),
                new KeyFrame(Duration.seconds(2), e -> countdownLabel.setText("1")),
                new KeyFrame(Duration.seconds(3), e -> {
                    countdownLabel.setText("GO!");

                    startTime = System.nanoTime(); // Record start time
                    raceStarted = true;
                    inputBlocked = false;
                    physicsLoop.startLoop(this.car); // Start game loop
                }),

                new KeyFrame(Duration.seconds(4), e -> {
                    countdownLabel.setVisible(false); // Hide countdown
                    timeLabel.setVisible(true); // Show timer
                })
        );

        countdownTimeline.playFromStart(); // Begin countdown
    }

    private void showRulesScene() {
        // Load and scale background
        Image bg = new Image(getClass().getResource("/images/background.png").toExternalForm());
        ImageView bgView = new ImageView(bg);
        bgView.setFitWidth(800);
        bgView.setFitHeight(600);

        // Create black glow effect for text
        DropShadow glow = new DropShadow();
        glow.setColor(Color.BLACK);
        glow.setRadius(3);

        // Title label
        Label title = new Label("GAME RULES");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");
        title.setEffect(glow);

        // Rule labels
        Label rule1 = new Label("• Use W A S D to drive your car.");
        Label rule2 = new Label("• Press ESC anytime to return to the main menu.");
        Label rule3 = new Label("• Choose how drunk you want to be:");
        Label rule4 = new Label("     - SOBER: clear vision, normal driving.");
        Label rule5 = new Label("     - TIPSY: light blur, mild swaying.");
        Label rule6 = new Label("     - WASTED: heavy blur, strong wave motion.");
        Label rule7 = new Label("     - BLACKOUT: extreme blur, purple tint, chaotic shaking. (A real nightmare!)");
        Label rule8 = new Label("• Goal: Complete the track as fast as possible!");

        // Apply style to all rules
        Label[] rules = {rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8};
        for (Label l : rules) {
            l.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
            l.setEffect(glow);
        }

        // Back button to return to main menu
        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));

        // Assemble all into a layout
        VBox rulesLayout = new VBox(15, title, rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8, backButton);
        rulesLayout.setAlignment(Pos.CENTER);

        // create "Rules" window
        StackPane stackPane = new StackPane(bgView, rulesLayout);
        Scene rulesScene = new Scene(stackPane, 800, 600);
        primaryStage.setScene(rulesScene);
    }

    // in settings to change your key-binds (but arrow-keys aren't possible to bind)
    private void setRebindHandler(Button button, String direction) {
        // Set button to listen for next key press and update binding
        button.setOnAction(e -> {
            button.setText(direction.substring(0, 1).toUpperCase() + direction.substring(1) + ": [Press Key]");
            Scene scene = button.getScene();
            scene.setOnKeyPressed(keyEvent -> {
                keyBindings.put(direction, keyEvent.getCode()); // Update mapping
                button.setText(direction.substring(0, 1).toUpperCase() + direction.substring(1) + ": " + keyEvent.getCode());
                scene.setOnKeyPressed(null); // Stop listening after assignment
            });
        });
    }

    // Map storing current key bindings for movement (up, down, left, right)
    private final Map<String, KeyCode> keyBindings = new HashMap<>() {{
        put("up", KeyCode.W); // Default key for moving up is W
        put("down", KeyCode.S); // Default key for moving down is S
        put("left", KeyCode.A); // Default key for turning left is A
        put("right", KeyCode.D); // Default key for turning right is D
    }};

    // Displays the settings scene for brightness and key remapping
    private void showSettingsScene() {
        Image bg = new Image(getClass().getResource("/images/background.png").toExternalForm()); // Load background image
        ImageView bgView = new ImageView(bg); // Create image view for background
        bgView.setFitWidth(800); // Set width to 800 pixels (as we did before like 10 times)
        bgView.setFitHeight(600); // Set height to 600 pixels (as we did before like 10 times)

        VBox settingsLayout = new VBox(20); // Create vertical layout with 20px spacing
        settingsLayout.setAlignment(Pos.CENTER); // Center-align the contents

        // Brightness Slider
        Label brightnessTitle = new Label("Display Settings"); // Title for brightness section
        brightnessTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;"); // Style the label

        Label brightnessLabel = new Label("Brightness:"); // Label for the slider
        brightnessLabel.setStyle("-fx-text-fill: white;"); // Set text color to white

        Slider brightnessSlider = new Slider(-1, 1, brightnessValue); // Slider from -1 to 1 with current brightness value
        brightnessSlider.setPrefWidth(300); // Set preferred width

        // Add listener to update brightness live
        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            brightnessValue = newVal.doubleValue(); // Store new brightness
            ColorAdjust adjust = new ColorAdjust(); // Create color effect
            adjust.setBrightness(brightnessValue); // Set brightness level
            root.setEffect(adjust); // Apply effect to root pane
        });

        VBox brightnessBox = new VBox(5, brightnessTitle, brightnessLabel, brightnessSlider); // Combine brightness controls
        brightnessBox.setAlignment(Pos.CENTER); // Center align

        // Key Mapping
        Label controlsLabel = new Label("Set Controls:"); // Section title
        controlsLabel.setStyle("-fx-text-fill: white;"); // White text color

        Button upButton = new Button("Up: " + keyBindings.get("up")); // Display current key for up
        Button downButton = new Button("Down: " + keyBindings.get("down")); // Display current key for down
        Button leftButton = new Button("Left: " + keyBindings.get("left")); // Display current key for left
        Button rightButton = new Button("Right: " + keyBindings.get("right")); // Display current key for right

        // Assign rebind handler to each button
        setRebindHandler(upButton, "up");
        setRebindHandler(downButton, "down");
        setRebindHandler(leftButton, "left");
        setRebindHandler(rightButton, "right");

        VBox controlsBox = new VBox(10, controlsLabel, upButton, downButton, leftButton, rightButton); // Layout for controls
        controlsBox.setAlignment(Pos.CENTER); // Center align

        Button backButton = new Button("Back to Menu"); // Button to return to main menu
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene)); // Switch scene on click

        settingsLayout.getChildren().addAll(brightnessBox, controlsBox, backButton); // Add all settings to main layout

        StackPane stackPane = new StackPane(); // Create stack pane to layer background and layout
        stackPane.getChildren().addAll(bgView, settingsLayout); // Add background and settings layout
        bgView.toBack(); // Send background to back

        Scene settingsScene = new Scene(stackPane, 800, 600); // Create scene with dimensions
        primaryStage.setScene(settingsScene); // Set the scene to primary stage
    }

    // Triggers game over sequence and restarts after short delay
    private void triggerGameOver() {
        physicsLoop.stopLoop(); // Stop the physics loop
        gameOverActive = true; // Flag game as over
        up = down = left = right = false; // Disable player input
        root.setTranslateX(0); // Reset camera X offset
        root.setTranslateY(0); // Reset camera Y offset

        Label gameOverLabel = new Label("GAME OVER"); // Create game over text
        gameOverLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 64)); // Set font and size
        gameOverLabel.setTextFill(Color.RED); // Set text color to red
        gameOverLabel.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 20px;"); // Style background and padding
        gameOverLabel.setAlignment(Pos.CENTER); // Center-align text
        gameOverLabel.setPrefSize(800, 600); // Fill screen size

        StackPane overlay = new StackPane(gameOverLabel); // Add label to overlay pane
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"); // Semi-transparent black overlay
        overlay.setPrefSize(800, 600); // Fill screen

        root.getChildren().add(overlay); // Add overlay to root

        // Start a background thread to wait 400ms
        new Thread(() -> {
            try {
                Thread.sleep(400); // Wait 400 milliseconds
            } catch (InterruptedException ignored) {}

            // Restart game on UI thread
            Platform.runLater(() -> {
                root.getChildren().remove(overlay); // Remove overlay
                startGameWithMap(selectedMap); // Restart game
                gameOverActive = false; // Reset game over flag
            });
        }).start();
    }

    // Displays the Victory screen with time and buttons (retry and back to menu)
    private void showVictoryScreen(double time) {
        physicsLoop.stopLoop(); // Stop the physics loop

        // Create victory message with final time
        Label victoryLabel = new Label("You made it in:\n" + String.format("%.3f s", time));
        victoryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 60)); // Set font
        victoryLabel.setTextFill(Color.LIME); // Set text color to lime
        victoryLabel.setAlignment(Pos.CENTER); // Center align text

        Button retryBtn = new Button("Retry"); // Button to retry the race
        retryBtn.setOnAction(e -> {
            root.getChildren().removeIf(node -> node instanceof VBox); // Remove victory screen
            startGameWithMap(selectedMap); // Restart game
        });

        Button backBtn = new Button("Back to Menu"); // Button to return to menu
        backBtn.setOnAction(e -> {
            root.getChildren().clear(); // Clear all UI nodes
            setupMainMenu(); // Set up main menu again
            primaryStage.setScene(mainMenuScene); // Show main menu scene
        });

        VBox box = new VBox(40, victoryLabel, retryBtn, backBtn); // Layout for victory screen
        box.setAlignment(Pos.CENTER); // Center all elements
        box.setPrefSize(800, 600); // Fill screen
        box.setStyle("-fx-background-color: rgba(0,0,0,0.8);"); // Dark overlay background

        root.getChildren().add(box); // Add victory screen to root
    }

    // and finally: Launches the JavaFX application
    public static void main(String[] args) {
        launch(); // right here we launch it
    }
}
