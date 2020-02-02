package bartosan.algo;

public class PixelPerformanceCounter
{
    private final long drawnPixelCount;
    private final long drawTimeMillis;

    public PixelPerformanceCounter(final long drawnPixelCount, final long drawTimeMillis)
    {
        this.drawnPixelCount = drawnPixelCount;
        this.drawTimeMillis = drawTimeMillis;
    }

    @Override
    public String toString()
    {
        return String.format("%.3f", (1.0 / 1000.0 * drawnPixelCount / drawTimeMillis)) + " MPix/s" + " drawn in " + String.format("%.3f",
            1.0 * drawTimeMillis) + " msec       ";
    }

    public PixelPerformanceCounter add(final PixelPerformanceCounter other)
    {
        return new PixelPerformanceCounter(this.drawnPixelCount + other.drawnPixelCount, this.drawTimeMillis + other.drawTimeMillis);
    }
}

