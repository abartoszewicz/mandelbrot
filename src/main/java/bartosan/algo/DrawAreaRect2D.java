package bartosan.algo;

public class DrawAreaRect2D
{
    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;

    public DrawAreaRect2D(final double minX, final double minY, final double maxX, final double maxY)
    {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public double getMinX()
    {
        return minX;
    }

    public double getMinY()
    {
        return minY;
    }

    public double getMaxX()
    {
        return maxX;
    }

    public double getMaxY()
    {
        return maxY;
    }

    @Override
    public String toString()
    {
        return "DrawAreaRect2D{" +
            "minX=" + minX +
            ", minY=" + minY +
            ", maxX=" + maxX +
            ", maxY=" + maxY +
            '}';
    }

    public DrawAreaRect2D grow(final double value)
    {
        double centerX = getCenterX();
        double centerY = getCenterY();
        double horizontalGrow = getWidth() * value * 0.5;
        double verticalGrow = getHeight() * value * 0.5;
        return new DrawAreaRect2D(centerX - horizontalGrow, centerY - verticalGrow,
            centerX + horizontalGrow, centerY + verticalGrow);
    }

    public double getHeight()
    {
        return (maxY - minY);
    }

    public double getWidth()
    {
        return (maxX - minX);
    }

    private double getCenterY()
    {
        return (minY + maxY) * 0.5;
    }

    private double getCenterX()
    {
        return (minX + maxX) * 0.5;
    }
}


