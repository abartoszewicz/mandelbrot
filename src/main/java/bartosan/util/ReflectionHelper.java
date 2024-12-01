package bartosan.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class ReflectionHelper
{
    private ReflectionHelper()
    {
    }

    public static Class[] getClasses(String packageName)
        throws ClassNotFoundException, IOException
    {
        return getClasses(packageName, null);
    }

    public static Class[] getClasses(String packageName, Class implementingInterface)
        throws ClassNotFoundException, IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements())
        {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs)
        {
            classes.addAll(findClasses(directory, packageName, implementingInterface));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    public static <T> List<T> getInstancesOf(String packageName, Class<T> implementingInterface) {
        try {
            Class[] rawClasses = getClasses(packageName, implementingInterface);
            List<T> instances = new ArrayList<>();
            for (int i = 0; i < rawClasses.length; i++) {
                try {
                    T newInstance = (T) rawClasses[i].newInstance();
                    instances.add(newInstance);
                } catch (Exception e) {
                    System.out.println("Could not create an instance of " + rawClasses[i].getSimpleName());
                    System.out.println(e.getMessage());
                }
            }

            return instances;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName, Class implementingInterface) throws ClassNotFoundException
    {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists())
        {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName(), implementingInterface));
            }
            else if (file.getName().endsWith(".class"))
            {
                Class classFromFile = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                if (implementingInterface == null || implementingInterface.isAssignableFrom(classFromFile))
                {
                    classes.add(classFromFile);
                }
            }
        }
        return classes;
    }

}
