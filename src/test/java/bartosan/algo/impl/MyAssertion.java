package bartosan.algo.impl;

@FunctionalInterface
interface FailingRunnable
{
    void run() throws Exception;
}

public class MyAssertion
{
    public static void assertDoesNotThrow(FailingRunnable action)
    {
        try
        {
            assert (action != null);
            action.run();
        }
        catch (Exception ex)
        {
            throw new Error("expected action not to throw, but it did!", ex);
        }
    }
}
