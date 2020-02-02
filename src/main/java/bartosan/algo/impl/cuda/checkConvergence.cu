
extern "C"
__global__ void checkConvergence(float *coordArray, int *result)
{
    float z = 0;
    float zi = 0;
    float ciFloat = coordArray[0];
    float cFloat = coordArray[1];
    int   steps = result[0];

    for (int i = 0; i < steps; i++) {
        float ziT = 2 * (z * zi);
        float zT = z * z - (zi * zi);
        z = zT + cFloat;
        zi = ziT + ciFloat;

        if (z * z + zi * zi >= 4.0) {
                *result = i;
		return;
        }
    }
    *result = steps;
}