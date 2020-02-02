package bartosan.main;

import static bartosan.constant.ViewParameters.ANIMATION_HALF_LENGTH;
import static bartosan.constant.ViewParameters.CONVERGENCE_TO_RGB_MAP;
import static bartosan.constant.ViewParameters.EMPTY_COLOR;
import static bartosan.constant.ViewParameters.FRAME_GROWTH;
import static bartosan.constant.ViewParameters.MARGIN;
import static bartosan.constant.ViewParameters.MAX_AREA_2D;
import static bartosan.constant.ViewParameters.MAX_CONVERGENCE_DEPTH;
import static bartosan.constant.ViewParameters.MIN_AREA_2D;
import static bartosan.constant.ViewParameters.WINDOW_HEIGHT;
import static bartosan.constant.ViewParameters.WINDOW_WIDTH;
import static java.lang.Math.min;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import bartosan.algo.Calculator;
import bartosan.algo.CalculatorFullFrame;
import bartosan.algo.DrawAreaRect2D;
import bartosan.algo.PixelPerformanceCounter;
import bartosan.algo.impl.CalculatorFloat;
import bartosan.util.ReflectionHelper;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * Mandelbrot set with JavaFX.
 * @author bartosan
 */
public class Main extends Application
{

    private static final int[] copyBuffer = new int[(WINDOW_WIDTH * WINDOW_HEIGHT)];
    private static Calculator currentImplementation = new CalculatorFloat();
    private long frame = 0;
    private GraphicsContext graphicsContext2D;
    private Scene scene;
    private Label label;

    private static void calculateColorMap()
    {

        for (int i = 0; i < MAX_CONVERGENCE_DEPTH + 1; i++)
        {
            double hueAngle = 30.0 * Math.PI * i / MAX_CONVERGENCE_DEPTH;

            //HSV to RGB conversion, with max saturation and value

            int red = (int) ((Math.max(-0.5, Math.min(0.5, Math.sin(hueAngle + Math.PI * 3.0 / 6.0))) + 0.5) * 255);
            int green = (int) ((Math.max(-0.5, Math.min(0.5, Math.sin(hueAngle + Math.PI * 11.0 / 6.0))) + 0.5) * 255);
            int blue = (int) ((Math.max(-0.5, Math.min(0.5, Math.sin(hueAngle + Math.PI * 7.0 / 6.0))) + 0.5) * 255);
            int alpha = 0xff;

            int argbColor = (alpha << 24) | (red << 16) | (green << 8) | (blue);
            CONVERGENCE_TO_RGB_MAP[i] = argbColor;
        }
        CONVERGENCE_TO_RGB_MAP[0] = 0xff000000; //black for 0;

    }

    private static DrawAreaRect2D animateDrawArea(final long l)
    {
        DrawAreaRect2D result;
        if (l < ANIMATION_HALF_LENGTH)
        {
            result = MAX_AREA_2D;
            for (int i = 0; i < l; i++)
            {
                result = result.grow(1.0 / FRAME_GROWTH);
            }
        }
        else
        {
            result = MIN_AREA_2D;
            for (int i = 100; i < l; i++)
            {
                result = result.grow(FRAME_GROWTH);
            }
        }
        return result;
    }

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

    private static void drawBufferOnScreen(final PixelWriter pixelWriter, final int windowWidth, final int windowHeight, final int[] copyBuffer,
                                           final int convergenceSteps)
    {
        if (pixelWriter == null)
        {
            return;
        }
        int address = 0;
        for (int y = 0; y < windowHeight; y++)
        {
            for (int x = 0; x < windowWidth; x++, address++)
            {
                int value = copyBuffer[address];
                int argbColor = CONVERGENCE_TO_RGB_MAP[value];
                if (value == convergenceSteps)
                {
                    argbColor = EMPTY_COLOR;
                }
                pixelWriter.setArgb(x, y, argbColor);
            }
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }

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
                implementationNames.add(impl.getSimpleName());
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
}
