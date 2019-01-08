package de.zebrajaeger.sphere2cube.jocl;

import de.zebrajaeger.sphere2cube.converter.Face;
import org.apache.commons.io.IOUtils;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueueWithProperties;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class JoclCalculator {

    private final long deviceType = CL_DEVICE_TYPE_ALL;

    private final int platformIndex;
    private final int deviceIndex;
    private final int maxN;

    private int[] srcArrayFace;
    private int[] srcArrayEdge;
    private double[] srcArrayA;
    private double[] srcArrayB;

    private cl_command_queue commandQueue;
    private cl_kernel kernel;
    private long[] local_work_size;
    private cl_program program;
    private cl_context context;

    public JoclCalculator(int platformIndex, int deviceIndex, int maxN) {
        this.platformIndex = platformIndex;
        this.deviceIndex = deviceIndex;
        this.maxN = maxN;
    }

    public void init() throws IOException {
        srcArrayFace = new int[maxN];
        srcArrayEdge = new int[maxN];
        srcArrayA = new double[maxN];
        srcArrayB = new double[maxN];

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int[] numPlatformsArray = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int[] numDevicesArray = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id[] devices = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        context = clCreateContext(contextProperties, 1, new cl_device_id[]{device}, null, null, null);

        // Create a command-queue for the selected device
        commandQueue = clCreateCommandQueueWithProperties(context, device, null, null);

        // Create the program from the source code
        InputStream r = this.getClass().getClassLoader().getResourceAsStream("program.c");
        String programStr = IOUtils.toString(r, StandardCharsets.UTF_8);

        program = clCreateProgramWithSource(context, 1, new String[]{programStr}, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        kernel = clCreateKernel(program, "sampleKernel", null);

        // Set the work-item dimensions
        local_work_size = new long[]{1};
    }

    public void setFace(Face face) {
        Arrays.fill(srcArrayFace, face.getNr());
    }

    public synchronized JoclCalculationJob calc(JoclCalculationJob job) {
        int n = job.getGlobalWorkUnit();
        long[] global_work_size = new long[]{n};

        Arrays.fill(srcArrayFace, job.getFace().getNr(), 0, n);
        job.fillSource(srcArrayA, srcArrayB);

        double[] dstArrayUf = new double[maxN];
        double[] dstArrayVf = new double[maxN];

        // Execute the kernel
        Pointer srcFace = Pointer.to(srcArrayFace);
        Pointer srcEdge = Pointer.to(srcArrayEdge);
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer srcB = Pointer.to(srcArrayB);

        // Allocate the memory objects for the input- and output data
        cl_mem[] memObjects= new cl_mem[6];
        memObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * n, srcFace, null);
        memObjects[1] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * n, srcEdge, null);
        memObjects[2] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_double * n, srcA, null);
        memObjects[3] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_double * n, srcB, null);

        memObjects[4] = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_double * n, null, null);
        memObjects[5] = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_double * n, null, null);

        for (int i = 0; i < memObjects.length; ++i) {
            clSetKernelArg(kernel, i, Sizeof.cl_mem, Pointer.to(memObjects[i]));
        }

        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size, local_work_size, 0, null, null);

        Pointer dstUf = Pointer.to(dstArrayUf);
        Pointer dstVf = Pointer.to(dstArrayVf);
        clEnqueueReadBuffer(commandQueue, memObjects[4], CL_TRUE, 0, n * Sizeof.cl_double, dstUf, 0, null, null);
        clEnqueueReadBuffer(commandQueue, memObjects[5], CL_TRUE, 0, n * Sizeof.cl_double, dstVf, 0, null, null);

        for (cl_mem memObject : memObjects) {
            clReleaseMemObject(memObject);
        }

        job.setResult(dstArrayUf, dstArrayVf);

        return job;
    }

    public void shutdown() {
        // Release kernel, program, and memory objects
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
    }
}
