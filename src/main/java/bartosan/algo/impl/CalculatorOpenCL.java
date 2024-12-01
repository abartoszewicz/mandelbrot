package bartosan.algo.impl;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_PLATFORM_EXTENSIONS;
import static org.jocl.CL.CL_PLATFORM_NAME;
import static org.jocl.CL.CL_PLATFORM_VENDOR;
import static org.jocl.CL.CL_PLATFORM_VERSION;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueueWithProperties;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clEnqueueWriteBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clGetPlatformInfo;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;

import java.util.Arrays;

import bartosan.algo.Calculator;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;
import org.jocl.cl_queue_properties;


public class CalculatorOpenCL implements Calculator
{
    private static String programSourceTest =
        "__kernel void " +
            "checkConvergence(__global const float *coords," +
            "             __global const int *steps," +
            "             __global int *result)" +
            "{" +
            "result[0] = (int)(coords[0] * coords[1]) + steps[0];" +
            "}";

    private static String programSource =
        "__kernel void " +
            "checkConvergence(__global const float *coords," +
            "             __global const int *steps," +
            "             __global int *result)" +
            "{"
            + "    float z = 0;\n"
            + "    float zi = 0;\n"
            + "    float ciFloat = coords[0];\n"
            + "    float cFloat = coords[1];\n"
            + "    int   stepsCount = steps[0];\n"
            + "    for (int i = 0; i < stepsCount; i++) {\n"
            + "        float ziT = 2 * (z * zi);\n"
            + "        float zT = z * z - (zi * zi);\n"
            + "        z = zT + cFloat;\n"
            + "        zi = ziT + ciFloat;\n"
            + "        if (z * z + zi * zi >= 4.0) "
            + "        {"
            + "            *result = i;\n"
            + "            return;\n"
            + "        }\n"
            + "    }\n"
            + "    *result = stepsCount;\n  "
            + "}";
    private static String programSample =
        "__kernel void " +
            "sampleKernel(__global const float *a," +
            "             __global const float *b," +
            "             __global float *c)" +
            "{" +
            "    int gid = get_global_id(0);" +
            "    c[gid] = a[gid] * b[gid];" +
            "}";
    private final float[] coordinates = new float[2];
    private final int[] steps = new int[1];
    private final int[] result = new int[1];
    private final cl_kernel kernel;
    private final Pointer coordinatesPointer;
    private final Pointer stepsPointer;
    private final Pointer resultPointer;
    private final cl_mem srcMemCoordinates;
    private final cl_mem srcMemSteps;
    private final cl_mem dstMemResult;
    private final cl_command_queue commandQueue;

    public CalculatorOpenCL()
    {
        coordinatesPointer = Pointer.to(coordinates);
        stepsPointer = Pointer.to(steps);
        resultPointer = Pointer.to(result);

        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];
        System.out.println("Found OpenCL platforms:");
        for (int i = 0; i < numPlatforms; i++)
        {
            long size[] = new long[1];
            clGetPlatformInfo(platforms[i], CL_PLATFORM_VENDOR, 0, null, size);
            byte buffer[] = new byte[(int) size[0]];
            clGetPlatformInfo(platforms[i], CL_PLATFORM_VENDOR, buffer.length, Pointer.to(buffer), null);
            System.out.println(getString(platforms[i], CL_PLATFORM_VENDOR) + " " +
                getString(platforms[i], CL_PLATFORM_VERSION) + " " +
                getString(platforms[i], CL_PLATFORM_NAME) + " " +
                getString(platforms[i], CL_PLATFORM_EXTENSIONS));
        }
        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        cl_context context = clCreateContext(
            contextProperties, 1, new cl_device_id[] {device},
            null, null, null);

        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();
        commandQueue = clCreateCommandQueueWithProperties(
            context, device, properties, null);

        // Allocate the memory objects for the input- and output data
        srcMemCoordinates = clCreateBuffer(context,
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * coordinates.length, coordinatesPointer, null);
        srcMemSteps = clCreateBuffer(context,
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_int * steps.length, stepsPointer, null);
        dstMemResult = clCreateBuffer(context,
            CL_MEM_READ_WRITE,
            Sizeof.cl_int * result.length, resultPointer, null);

        cl_program program = clCreateProgramWithSource(context,
            1, new String[] {programSource}, null, null);
        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        kernel = clCreateKernel(program, "checkConvergence", null);
        doTest();
    }

    private static String getString(cl_platform_id platform, int paramName)
    {
        long size[] = new long[1];
        clGetPlatformInfo(platform, paramName, 0, null, size);
        byte buffer[] = new byte[(int) size[0]];
        clGetPlatformInfo(platform, paramName,
            buffer.length, Pointer.to(buffer), null);
        return new String(buffer, 0, buffer.length - 1);
    }

    private void sample()
    {
        // Create input- and output data
        int n = 10;
        float srcArrayA[] = new float[n];
        float srcArrayB[] = new float[n];
        float dstArray[] = new float[n];
        for (int i = 0; i < n; i++)
        {
            srcArrayA[i] = i;
            srcArrayB[i] = i;
        }
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer srcB = Pointer.to(srcArrayB);
        Pointer dst = Pointer.to(dstArray);

        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        cl_context context = clCreateContext(
            contextProperties, 1, new cl_device_id[] {device},
            null, null, null);

        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();
        cl_command_queue commandQueue = clCreateCommandQueueWithProperties(
            context, device, properties, null);

        // Allocate the memory objects for the input- and output data
        cl_mem srcMemA = clCreateBuffer(context,
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n, srcA, null);
        cl_mem srcMemB = clCreateBuffer(context,
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n, srcB, null);
        cl_mem dstMem = clCreateBuffer(context,
            CL_MEM_READ_WRITE,
            Sizeof.cl_float * n, null, null);

        // Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
            1, new String[] {programSample}, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "sampleKernel", null);

        // Set the arguments for the kernel
        int a = 0;
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(srcMemA));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(srcMemB));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(dstMem));

        // Set the work-item dimensions
        long global_work_size[] = new long[] {n};

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
            global_work_size, null, 0, null, null);

        // Read the output data
        clEnqueueReadBuffer(commandQueue, dstMem, CL_TRUE, 0,
            n * Sizeof.cl_float, dst, 0, null, null);

        // Release kernel, program, and memory objects
        clReleaseMemObject(srcMemA);
        clReleaseMemObject(srcMemB);
        clReleaseMemObject(dstMem);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);

        // Verify the result
        boolean passed = true;
        final float epsilon = 1e-7f;
        for (int i = 0; i < n; i++)
        {
            float x = dstArray[i];
            float y = srcArrayA[i] * srcArrayB[i];
            boolean epsilonEqual = Math.abs(x - y) <= epsilon * Math.abs(x);
            if (!epsilonEqual)
            {
                passed = false;
                break;
            }
        }
        System.out.println("Test " + (passed ? "PASSED" : "FAILED"));
        if (n <= 10)
        {
            System.out.println("Result: " + Arrays.toString(dstArray));
        }

    }

    private void doTest()
    {
        int result2 = checkConvergence(1.0, 1.0, 1);
        int result8 = checkConvergence(2.0, 3.0, 2);
        int result1500 = checkConvergence(40.0, 30.0, 300);

    }

    //@Override
    public int checkConvergence(final double ci, final double c, final int convergenceSteps)
    {
        coordinates[0] = (float) ci;
        coordinates[1] = (float) c;
        clEnqueueWriteBuffer(commandQueue, srcMemCoordinates, true, 0, 2 * Sizeof.cl_float, Pointer.to(coordinates), 0, null, null);
        steps[0] = convergenceSteps;
        clEnqueueWriteBuffer(commandQueue, srcMemSteps, true, 0, 1 * Sizeof.cl_int, Pointer.to(steps), 0, null, null);

        //3 parameters:
        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(this.srcMemCoordinates));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(this.srcMemSteps));
        clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(this.dstMemResult));

        // Set the work-item dimensions
        long global_work_size[] = new long[] {1};

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
            global_work_size, null, 0, null, null);

        // Read the output data
        clEnqueueReadBuffer(commandQueue, dstMemResult, CL_TRUE, 0,
            result.length * Sizeof.cl_int, Pointer.to(result), 0, null, null);

        return result[0];
    }
}
