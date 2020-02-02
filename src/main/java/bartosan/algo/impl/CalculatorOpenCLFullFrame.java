package bartosan.algo.impl;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_MEM_WRITE_ONLY;
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
import static org.jocl.CL.clSetKernelArg;

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

import bartosan.algo.CalculatorFullFrame;
import bartosan.algo.DrawAreaRect2D;


public class CalculatorOpenCLFullFrame implements CalculatorFullFrame
{
    private static String programSource =
        "__kernel void fullFrameMandelbrot(__global const float *inputData,\n"
            + "                         __global int *result)\n"
            + "{\n"
            + "    int width = (int)inputData[4]; \n"
            + "    int height = (int)inputData[5];\n"
            + "    int convergenceCount = (int)inputData[6];\n"
            + "    unsigned int ix = get_global_id(0);\n"
            + "    unsigned int iy = get_global_id(1);\n"

            + "    int addr = ix + iy * width;\n"
            + "    { \n"
            + "        float ciFloat = (inputData[3] - inputData[1]) * iy / height + inputData[1];\n"
            + "        float cFloat = (inputData[2] - inputData[0]) * ix / width + inputData[0] ;\n"
            + "        {\n"
            + "            float z = 0;\n"
            + "            float zi = 0;\n"
            + "            \n"
            + "            int   stepsCount = (int)inputData[6];\n"
            + "            int i = 0;\n"
            + "            for (i = 0; i < stepsCount; i++) \n"
            + "            {\n"
            + "                float ziT = 2 * (z * zi);\n"
            + "                float zT = z * z - (zi * zi);\n"
            + "                z = zT + cFloat;\n"
            + "                zi = ziT + ciFloat;\n"
            + "                if (z * z + zi * zi >= 4.0) \n"
            + "                {\n"
            + "                    break;    \n"
            + "                }\n"
            + "            }\n"
            + "        \n"
            + "        result[addr] = i;\n"
            + "        }\n"
            + "    }\n"
            + "}";

    private final float[] inputData = new float[7];
    private final cl_kernel kernel;
    private final Pointer inputDataPointer;
    private final cl_mem srcMemInputData;
    private final cl_command_queue commandQueue;
    private final cl_context context;
    private cl_mem dstMemResult;

    public CalculatorOpenCLFullFrame()
    {
        inputDataPointer = Pointer.to(inputData);

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
            System.out.println(getString(platforms[i], CL_PLATFORM_VENDOR)
                    + " " + getString(platforms[i], CL_PLATFORM_VERSION)
                    + " " + getString(platforms[i], CL_PLATFORM_NAME)
                // + " " + getString(platforms[i], CL_PLATFORM_EXTENSIONS)
            );
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
        context = clCreateContext(
            contextProperties, 1, new cl_device_id[] {device},
            null, null, null);

        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();
        commandQueue = clCreateCommandQueueWithProperties(
            context, device, properties, null);

        // Allocate the memory objects for the input- and output data
        srcMemInputData = clCreateBuffer(context,
            CL_MEM_READ_WRITE,
            Sizeof.cl_float * inputData.length, inputDataPointer, null);

        cl_program program = clCreateProgramWithSource(context,
            1, new String[] {programSource}, null, null);
        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        kernel = clCreateKernel(program, "fullFrameMandelbrot", null);
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

    //@Override
    public void calculateFrame(final DrawAreaRect2D drawArea, final int width, final int height, final int convergenceSteps, int[] result)
    {
        inputData[0] = (float) drawArea.getMinX();
        inputData[1] = (float) drawArea.getMinY();
        inputData[2] = (float) drawArea.getMaxX();
        inputData[3] = (float) drawArea.getMaxY();
        inputData[4] = width;
        inputData[5] = height;
        inputData[6] = convergenceSteps;
        clEnqueueWriteBuffer(commandQueue, srcMemInputData, true, 0,
            inputData.length * Sizeof.cl_float, Pointer.to(inputData), 0, null, null);

        Pointer resultPointer = Pointer.to(result);

        if (this.dstMemResult == null)
        {
            initReultBuffer(width, height, resultPointer);
        }

        clEnqueueWriteBuffer(commandQueue, srcMemInputData, true, 0, 7 * Sizeof.cl_float, Pointer.to(inputData), 0, null, null);
        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(this.srcMemInputData));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(dstMemResult));

        // Set the work-item dimensions
        long global_work_size[] = new long[2];
        global_work_size[0] = width;
        global_work_size[1] = height;

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 2, null,
            global_work_size, null, 0, null, null);
        // Read the output data
        clEnqueueReadBuffer(commandQueue, dstMemResult, CL_TRUE, 0,
            result.length * Sizeof.cl_int,
            Pointer.to(result), 0, null, null);
    }

    private void initReultBuffer(final int width, final int height, Pointer hostPointer)
    {
        dstMemResult = clCreateBuffer(context,
            CL_MEM_WRITE_ONLY,
            Sizeof.cl_int * width * height, hostPointer, null);

    }

    //@Override
    public int checkConvergence(final double ci, final double c, final int convergenceSteps)
    {
        return 0;
    }
}