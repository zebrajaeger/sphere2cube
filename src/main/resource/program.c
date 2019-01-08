__kernel void sampleKernel( __global const int *face, __global const int *sourceEdge,
                            __global const double *a, __global const double *b,
                            __global double *uf, __global double *vf) {

    int gid = get_global_id(0);
    const double PI = 3.14159265358979323846264338328;

    double x, y, z;
    switch (face[gid]) {
        case 0:
            x = -1.0;
            y = 1.0 - a[gid];
            z = 1.0 - b[gid];
            break;
        case 1:
            x = a[gid] - 1.0;
            y = -1.0;
            z = 1.0 - b[gid];
            break;
        case 2:
            x = 1.0;
            y = a[gid] - 1.0;
            z = 1.0 - b[gid];
            break;
        case 3:
            x = 1.0 - a[gid];
            y = 1.0;
            z = 1.0 - b[gid];
            break;
        case 4:
            x = 1.0 - b[gid];
            y = a[gid] - 1.0;
            z = 1.0;
            break;
        case 5:
            x = 1.0 - b[gid];
            y = a[gid] - 1.0;
            z = -1.0;
            break;
    }

    double theta = atan2(y, x);
    double r = hypot(x, y);
    double phi = atan2(z, r);

    uf[gid] = (2.0 * sourceEdge[gid] * (theta + PI) / PI);
    vf[gid] = (2.0 * sourceEdge[gid] * (PI / 2.0 - phi) / PI);
}
