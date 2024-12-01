package bartosan.main;

import bartosan.constant.ViewParameters;

import static bartosan.constant.ViewParameters.WINDOW_HEIGHT;
import static bartosan.constant.ViewParameters.WINDOW_WIDTH;
import static java.lang.Math.min;


/**
 *
 * @author bartosan
 */
public class Main {




  /*

    private static PixelPerformanceCounter paintSet(GraphicsContext ctx, DrawAreaRect2D drawArea, int windowWidth, int windowHeight)
    {
        double stepX = drawArea.getWidth() / windowWidth;
        double stepY = drawArea.getHeight() / windowHeight;

        int convergenceSteps = (int) min(50.0 + 0.000001 * windowWidth / stepX, MAX_CONVERGENCE_DEPTH);
        final PixelWriter pixelWriter = ctx != null ? ctx.getPixelWriter() : null;

        long pixelCount = 0;
        long drawTime = System.currentTimeMillis();

        if (currentImplementation instanceof CalculatorFullFrame)
        {
            ((CalculatorFullFrame) currentImplementation).calculateFrame(drawArea,
                windowWidth,
                windowHeight,
                convergenceSteps,
                copyBuffer);
            pixelCount += windowWidth * windowHeight;
        }
        else
        {
            int yR = 0;
            int adr = 0;
            for (double ci = drawArea.getMinY(); yR < windowHeight; ci = ci + stepY, yR++)
            {
                int xR = 0;
                for (double c = drawArea.getMinX(); xR < windowWidth; c = c + stepX, xR++, adr++)
                {

                    int convergenceValue = currentImplementation.checkConvergence(ci, c, convergenceSteps);
                    copyBuffer[adr] = convergenceValue;
                    pixelCount++;
                }
            }
        }

        drawTime = System.currentTimeMillis() - drawTime;
        drawBufferOnScreen(pixelWriter, windowWidth, windowHeight, copyBuffer, convergenceSteps);

        return new PixelPerformanceCounter(pixelCount, drawTime);
    }


*/
    public static void main(String[] args)
    {
        ViewParameters.calculateColorMap();
        MainFrame mainPanel = new MainFrame("Mandelbrot demo", WINDOW_WIDTH, WINDOW_HEIGHT);

    }
/*
    public static void runTestWithImplementation(final Calculator calculatorImpl)
    {
        Calculator previousImplementation = currentImplementation;
        currentImplementation = calculatorImpl;
        System.out.println("Running test for " + calculatorImpl.getClass().getName());
        PixelPerformanceCounter globalCounter = new PixelPerformanceCounter(0, 0);
        for (int testFrame = 0; testFrame < 2 * ANIMATION_HALF_LENGTH; testFrame++)
        {
            PixelPerformanceCounter perfCounter = paintSet(null, animateDrawArea(testFrame), WINDOW_WIDTH, WINDOW_HEIGHT);
            globalCounter = globalCounter.add(perfCounter);
        }
        System.out.println("Global statistics: " + globalCounter.toString());
        System.out.println();
        currentImplementation = previousImplementation;
    }

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(eventClose -> System.exit(0));

        Pane controllsPane = new HBox();
        controllsPane.setPrefHeight(MARGIN);
        controllsPane.setMinHeight(MARGIN);
        Button playButton = new Button("Play");
        playButton.setPrefSize(100, 20);
        playButton.setDefaultButton(true);
        label = new Label("Pixels per second");
        controllsPane.getChildren().add(playButton);
        ComboBox implementationCombo = new ComboBox();
        fillImplementationCombo(implementationCombo);
        calculateColorMap();

        controllsPane.getChildren().addAll(implementationCombo);
        controllsPane.getChildren().add(label);

        Pane fractalRootPane = new Pane();
        VBox vBox = new VBox(controllsPane, fractalRootPane);

        Pane rootPane = new Pane(vBox);
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        graphicsContext2D = canvas.getGraphicsContext2D();

        fractalRootPane.getChildren().add(canvas);

        scene = new Scene(rootPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setFill(Color.BLACK);

        primaryStage.setTitle("Mandelbrot Set");
        primaryStage.setScene(scene);
        primaryStage.show();
        playButton.setOnAction(event -> doTest());
        redraw();
    }

    private void fillImplementationCombo(final ComboBox implementationCombo)
    {
        final ObservableList<String> implementationNames = FXCollections.observableArrayList("Choose Implementation...");
        Class interfaceClass = Calculator.class;
        Class[] implementations = new Class[0];
        try
        {
            implementations = ReflectionHelper.getClasses("bartosan.algo.impl", interfaceClass);
            for (Class impl : implementations)
            {
                implementationNames.add(impl.getName());
            }
        }
        catch (ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
        }

        implementationCombo.setItems(implementationNames);
        if (currentImplementation != null)
        {
            SingleSelectionModel ssm = implementationCombo.getSelectionModel();
            ssm.select(currentImplementation.getClass().getName());
        }
        implementationCombo.setOnAction(event -> {
            String selection = (String) implementationCombo.getSelectionModel().getSelectedItem();
            changeImplementation(selection);
        });

    }

    private void changeImplementation(final String className)
    {
        System.out.println("change implementation to: " + className);
        try
        {
            Class newImplementation = ClassLoader.getSystemClassLoader().loadClass(className);
            {
                currentImplementation = (Calculator) newImplementation.getDeclaredConstructor().newInstance();
            }
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        redraw();
    }

    private void doTest()
    {
        frame = 0;
        new AnimationTimer()
        {
            @Override
            public void handle(final long now)
            {
                frame++;
                redraw();
                if (frame >= 2 * ANIMATION_HALF_LENGTH)
                {
                    this.stop();
                }
            }
        }.start();

    }

    private void redraw()
    {
        PixelPerformanceCounter lastPixelPerformanceCounter = paintSet(graphicsContext2D, animateDrawArea(frame), (int) scene.getWidth(),
            (int) scene.getHeight());
        label.setText(lastPixelPerformanceCounter.toString());
    }

 */
}
