package bartosan.main;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import static bartosan.constant.ViewParameters.CONVERGENCE_TO_RGB_MAP;
import static bartosan.constant.ViewParameters.EMPTY_COLOR;

public class MainPanel extends JPanel {
    private int[] copyBuffer;
    BufferedImage bufferedImage;
    private int height, width;
    public MainPanel(int windowWidth, int windowHeight) {
        copyBuffer = new int[windowHeight * windowWidth];
        this.width = windowWidth;
        this.height = windowHeight;
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void drawBufferOnImage(int convergenceSteps) {
        int address = 0;
        if (copyBuffer.length < width * height) {
            System.out.println("copyBuffer was resized");
            return;
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, address++) {
                int value = copyBuffer[address];
                int argbColor = CONVERGENCE_TO_RGB_MAP[value];
                if (value == convergenceSteps) {
                    argbColor = EMPTY_COLOR;
                }
                bufferedImage.setRGB(x, y, argbColor & 0x00ffffff);
            }
        }
    }

    public int[] getInternalCopyBuffer() {
        return copyBuffer;
    }

    @Override
    public void paintComponent(Graphics g) {
      //  System.out.println("paint on panel");
        g.drawImage(bufferedImage, 0, 0, (img, infoflags, x, y, width, height) -> false);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setNewSize(int newWidth, int newHeight) {
        copyBuffer = new int[newWidth * newHeight];
        this.width = newWidth;
        this.height = newHeight;
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public long getPixelCount() {
        return (long) width * height;
    }
}
