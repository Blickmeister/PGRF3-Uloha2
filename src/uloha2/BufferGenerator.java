package uloha2;

/**
 * @author Bc. Ondřej Schneider && FIM UHK
 * @version 1.0
 * @since 2019-11-16
 * Třída pro generování VB a IB
 */
public class BufferGenerator {

    float[] vertexBufferData = {};
    int[] indexBufferData = {};
    int[] indexBufferDataBolzano1N = {};
    int[] indexBufferDataBolzano2N = {};
    int[] indexBufferDataBolzano3N = {};
    int[] indexBufferDataBolzano4N = {};
    int[] indexBufferDataBolzano5N = {};
    int indexBolzano = 0; // aktuální index v IB pro spojení bodů Bolzanovy fce
    int intervalCoef = 30; // koeficient pro určení intervalu jednotlivých IB Bolzanovy fce
    int IBsize = 8; // interval jednotlivých IB Bolzanovy fce

    public float[] getVertexBufferData() {
        return vertexBufferData;
    }

    public int[] getIndexBufferData() {
        return indexBufferData;
    }

    public int[] getIndexBufferDataBolzano1N() {
        return indexBufferDataBolzano1N;
    }

    public int[] getIndexBufferDataBolzano2N() {
        return indexBufferDataBolzano2N;
    }

    public int[] getIndexBufferDataBolzano3N() {
        return indexBufferDataBolzano3N;
    }

    public int[] getIndexBufferDataBolzano4N() {
        return indexBufferDataBolzano4N;
    }

    public int[] getIndexBufferDataBolzano5N() {
        return indexBufferDataBolzano5N;
    }

    public BufferGenerator() {
    }

    // Bolzanova fce - vygenerování všech bodů dle zvoleného N opakování
    public void createVertexBufferForBolzano(float interval) {

        float a = 0;
        float A = 0;
        float b = a + interval;
        float B = A + interval;
        int i = 0;
        int aktualniIndexA = 0;
        int aktualniIndexB = 2;
        final int N = 83;

        vertexBufferData = new float[N * 40 + 40];

        for (int n = 0; n <= N; n++) {
            for (int j = 0; j < 4; j++) {
                createBolzanoPoints(a, A, b, B, i);
                i += 10;
                a = vertexBufferData[aktualniIndexA];
                A = vertexBufferData[++aktualniIndexA];
                aktualniIndexA++;
                b = vertexBufferData[aktualniIndexB];
                B = vertexBufferData[++aktualniIndexB];
                aktualniIndexB++;
            }
            aktualniIndexA += 2;
            aktualniIndexB += 2;
        }
    }

    // Definice 5 bodů Bolzanovy fce
    private void createBolzanoPoints(float a, float A, float b, float B, int i) {
        vertexBufferData[i++] = a;
        vertexBufferData[i++] = A;
        vertexBufferData[i++] = a + (float) 3 / 8 * (b - a);
        vertexBufferData[i++] = A + (float) 5 / 8 * (B - A);
        vertexBufferData[i++] = (float) 1 / 2 * (a + b);
        vertexBufferData[i++] = (float) 1 / 2 * (A + B);
        vertexBufferData[i++] = a + (float) 7 / 8 * (b - a);
        vertexBufferData[i++] = A + (float) 9 / 8 * (B - A);
        vertexBufferData[i++] = b;
        vertexBufferData[i++] = B;
    }

    // zobrazení Bolzanovy fce pro N = 1
    public void createIndexBufferForBolzano1N() {
        indexBufferDataBolzano1N = new int[IBsize];

        int i = 0;
        indexBufferDataBolzano1N[i++] = indexBolzano;
        for(int j = 0; j < (IBsize-2)/2; j++) {
            indexBufferDataBolzano1N[i++] = ++indexBolzano;
            indexBufferDataBolzano1N[i++] = indexBolzano;
        }
        indexBufferDataBolzano1N[IBsize-1] = ++indexBolzano;

        IBsize += intervalCoef;
    }

    // zobrazení Bolzanovy fce pro N = 2
    public void createIndexBufferForBolzano2N() {
        indexBufferDataBolzano2N = new int[IBsize];

        int i = 0;
        indexBufferDataBolzano2N[i++] = ++indexBolzano;
        for(int j = 0; j < (IBsize-2)/2; j++) {
            indexBufferDataBolzano2N[i++] = ++indexBolzano;
            indexBufferDataBolzano2N[i++] = indexBolzano;
        }
        indexBufferDataBolzano2N[IBsize-1] = ++indexBolzano;

        intervalCoef *= 4;
        IBsize += intervalCoef;
    }

    // zobrazení Bolzanovy fce pro N = 3
    public void createIndexBufferForBolzano3N() {
        indexBufferDataBolzano3N = new int[IBsize];

        int i = 0;
        indexBufferDataBolzano3N[i++] = ++indexBolzano;
        for(int j = 0; j < (IBsize-2)/2; j++) {
            indexBufferDataBolzano3N[i++] = ++indexBolzano;
            indexBufferDataBolzano3N[i++] = indexBolzano;
        }
        indexBufferDataBolzano3N[IBsize-1] = ++indexBolzano;

        intervalCoef *= 4;
        IBsize += intervalCoef;
    }

    // zobrazení Bolzanovy fce pro N = 4
    public void createIndexBufferForBolzano4N() {
        indexBufferDataBolzano4N = new int[IBsize];

        int i = 0;
        indexBufferDataBolzano4N[i++] = ++indexBolzano;
        for(int j = 0; j < (IBsize-2)/2; j++) {
            indexBufferDataBolzano4N[i++] = ++indexBolzano;
            indexBufferDataBolzano4N[i++] = indexBolzano;
        }
        indexBufferDataBolzano4N[IBsize-1] = ++indexBolzano;

        intervalCoef *= 4;
        IBsize += intervalCoef;
    }

    // zobrazení Bolzanovy fce pro N = 5
    public void createIndexBufferForBolzano5N() {
        IBsize -= 54;
        indexBufferDataBolzano5N = new int[IBsize];

        int i = 0;
        indexBufferDataBolzano5N[i++] = ++indexBolzano;
        for(int j = 0; j < (IBsize-2)/2; j++) {
            indexBufferDataBolzano5N[i++] = ++indexBolzano;
            indexBufferDataBolzano5N[i++] = indexBolzano;
        }
        indexBufferDataBolzano5N[IBsize-1] = ++indexBolzano;
    }

    // metoda pro generování vertex bufferu pro trojuhelníkový grid
    public void createVertexBuffer(int m, int n) {

        vertexBufferData = new float[m * n * 2];
        //vertexBufferData[0] = 0;

        float interval = (float) 5 / (m - 1);

        float x = 0;
        float y = 0;

        for (int i = 0; i < m * n * 2; i += m * 2) {
            for (int j = i; j < n * 2 + i - 1; j += 2) {
                vertexBufferData[j] = x;
                vertexBufferData[j + 1] = y;
                x += interval;
            }
            y += interval;
            x = 0;
        }
    }

    // metoda pro vytvoření index bufferu pro vykreslení pomocí GL_TRIANGLES
    public void createIndexBuffer(int m, int n) {
        // 0,5,6,0,6,1,1,6,7,1,7,2,2,7,8,2,8,3,3,8,9,3,9,4....

        indexBufferData = new int[6 * (m - 1) * (n - 1)];
        int i = 0;
        int k;
        for (int y = 0; y < n - 1; y++) {
            for (int x = 0; x < m - 1; x++) {
                k = x + y * m;
                indexBufferData[i++] = k;
                indexBufferData[i++] = k + 1;
                indexBufferData[i++] = k + (m + 1);
                indexBufferData[i++] = k;
                indexBufferData[i++] = k + (m + 1);
                indexBufferData[i++] = k + m;
            }
        }
    }

    // metoda pro vytvoření index bufferu pro vykreslení pomocí GL_TRIANGLE_STRIP (použití degenerovaných trojuhelníků)
    public void createIndexBufferTriangleStrips(int m, int n) {
        // výpočet velikosti pole
        final int numOfTriangleStrips = n - 1;
        final int numOfDegenTriangles = 2 * (numOfTriangleStrips - 1);
        final int verticesPerStrip = 2 * m;
        indexBufferData = new int[(verticesPerStrip * numOfTriangleStrips)
                + numOfDegenTriangles];

        // naplnění index bufferu
        int i = 0;
        for (int y = 0; y < n - 1; y++) {
            if (y > 0) {
                indexBufferData[i++] = y * n;
            }
            for (int x = 0; x < m; x++) {
                indexBufferData[i++] = (y * n) + x;
                indexBufferData[i++] = ((y + 1) * n) + x;
            }

            if (y < n - 2) {
                indexBufferData[i++] = ((y + 1) * n) + (m - 1);
            }
        }
    }
}
