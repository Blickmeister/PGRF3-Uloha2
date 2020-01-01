package uloha2;

import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL40.*;

/**
 * @author Bc. Ondřej Schneider && FIM UHK
 * @version 1.0
 * @since 2019-11-20
 * Renderovací třída
 */
public class Renderer extends AbstractRenderer {

    // proměnné pro ovládání myší
    double ox, oy;
    boolean mouseButton1, mouseButton2 = false;

    OGLBuffers buffers, buffersAxis, buffersBolzano1N, buffersBolzano2N,
            buffersBolzano3N, buffersBolzano4N, buffersBolzano5N;

    int shaderProgramTriangles, shaderProgramLines;

    // parametry pro ovládání - manipulaci s fcemi
    int width, height, n;
    float b;
    float zoom = 1;
    double dx = 0;
    double dy = 0;
    double px = 0;
    double py = 0;

    // uniform lokátory
    int locMathModelView, locMathViewView, locMathProjView,
            locMathModelAxis, locMathViewAxis, locMathProjAxis, locTime, locN, locB,
            locObjectType, locView, locZoom, locZoomLines, locPx, locPy, locPxLines,
            locPyLines;

    // proměnné pro přepínání módů
    boolean wireframe = false;
    boolean view3D = false;
    boolean startAnim = true;
    boolean resetAnim = false;
    int funcType = 1;
    boolean resetZoom = false;

    boolean line1 = false;
    boolean line2 = false;
    boolean line3 = false;
    boolean line4 = false;
    boolean line5 = false;

    // proměnné pro shadery
    float innerLevel = 0;

    // proměnné pro TextRenderer
    String viewString = "2D";
    String wireframeString = "Fill";
    String startAnimString = "Zapnuta";
    String funcTypeString = "Weierstrassova funkce";

    // vytvoření kamery a definice projekce
    Camera cam = new Camera();
    Mat4 proj = new Mat4PerspRH(Math.PI / 4, 1, 0.01, 100.0);

    // ovládání tlačítky
    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_W:
                        cam = cam.forward(1);
                        break;
                    case GLFW_KEY_D:
                        cam = cam.right(1);
                        break;
                    case GLFW_KEY_S:
                        cam = cam.backward(1);
                        break;
                    case GLFW_KEY_A:
                        cam = cam.left(1);
                        break;
                    case GLFW_KEY_SPACE:
                        cam = cam.withFirstPerson(!cam.getFirstPerson());
                        break;
                    case GLFW_KEY_E:
                        innerLevel += 1;
                        break;
                    case GLFW_KEY_R:
                        innerLevel -= 1;
                        break;
                    // 2D/3D zobrazení
                    case GLFW_KEY_P:
                        if (view3D) {
                            viewString = "2D";
                            view3D = false;
                        } else {
                            viewString = "3D";
                            view3D = true;
                        }
                        break;
                    case GLFW_KEY_F:
                        // vyplenePlochy/dratovyModel
                        if (wireframe) {
                            wireframeString = "Fill";
                            wireframe = false;
                        } else {
                            wireframeString = "Wireframe";
                            wireframe = true;
                        }
                        break;
                    case GLFW_KEY_KP_ADD:
                        // inkrementace proměnné ve vybrané funkci
                        if (n < 50) {
                            n++;
                        } else {
                            n = 50;
                        }
                        break;
                    case GLFW_KEY_KP_SUBTRACT:
                        // dekrementace proměnné ve vybrané funkci
                        if (n > 1) {
                            n--;
                        } else {
                            n = 1;
                        }
                        break;
                    case GLFW_KEY_ENTER:
                        // zapnutí/vypnutí animace
                        if (startAnim) {
                            startAnimString = "Vypnuta";
                            startAnim = false;
                        } else {
                            startAnimString = "Zapnuta";
                            startAnim = true;
                        }
                        break;
                    case GLFW_KEY_BACKSPACE:
                        // reset animace
                        if (!resetAnim) {
                            resetAnim = true;
                        }
                        break;
                    case GLFW_KEY_1:
                        // Weierstrassova fce
                        funcType = 1;
                        funcTypeString = "Weierstrassova funkce";
                        break;
                    case GLFW_KEY_2:
                        // Bolzanova fce
                        funcType = 2;
                        funcTypeString = "Bolzanova funkce";
                        break;
                    case GLFW_KEY_KP_1:
                        line1 = !line1;
                        break;
                    case GLFW_KEY_KP_2:
                        line2 = !line2;
                        break;
                    case GLFW_KEY_KP_3:
                        line3 = !line3;
                        break;
                    case GLFW_KEY_KP_4:
                        line4 = !line4;
                        break;
                    case GLFW_KEY_KP_5:
                        line5 = !line5;
                        break;
                    case GLFW_KEY_UP:
                        py += 0.1;
                        break;
                    case GLFW_KEY_DOWN:
                        py -= 0.1;
                        break;
                    case GLFW_KEY_RIGHT:
                        px += 0.1;
                        break;
                    case GLFW_KEY_LEFT:
                        px -= 0.1;
                        break;
                    case GLFW_KEY_Y:
                        resetZoom = true;
                        zoom = 1;
                        break;
                }
            }
        }
    };

    // resize okna
    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0 &&
                    (w != width || h != height)) {
                width = w;
                height = h;
                proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 100.0);
                if (textRenderer != null)
                    textRenderer.resize(width, height);
            }
        }
    };

    // ovládání myší
    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
            mouseButton2 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2) == GLFW_PRESS;

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                cam = cam.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                cam = cam.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            if (dy < 0)
                zoom += 0.5;
            else if (zoom > 0.5) zoom -= 0.5;
        }
    };

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWWindowSizeCallback getWsCallback() {
        return wsCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }


    // vytvoření gridu - VB a IB
    void createBuffers(int m) {

        BufferGenerator bufTriangle = new BufferGenerator();
        BufferGenerator bufLines = new BufferGenerator();

        bufTriangle.createVertexBuffer(m, m);
        bufTriangle.createIndexBuffer(m, m);

        // vytvoření IBs
        bufLines.createVertexBufferForBolzano(1);
        bufLines.createIndexBufferForBolzano1N();
        bufLines.createIndexBufferForBolzano2N();
        bufLines.createIndexBufferForBolzano3N();
        bufLines.createIndexBufferForBolzano4N();
        bufLines.createIndexBufferForBolzano5N();

        // naplnění datových struktur pro VBs a IBs
        float[] vertexBufferData = bufTriangle.getVertexBufferData();
        int[] indexBufferData = bufTriangle.getIndexBufferData();

        float[] vertexBufferAxis = new float[]{-4, 0, 0, 4, 0, 0, 0, -4, 0, 0, 4, 0, 0, 0, -4, 0, 0, 4};
        int[] indexBufferAxis = new int[]{0, 1, 2, 3, 4, 5};

        float[] vertexBufferBolzano = bufLines.getVertexBufferData();
        int[] indexBufferBolzano1N = bufLines.getIndexBufferDataBolzano1N();
        int[] indexBufferBolzano2N = bufLines.getIndexBufferDataBolzano2N();
        int[] indexBufferBolzano3N = bufLines.getIndexBufferDataBolzano3N();
        int[] indexBufferBolzano4N = bufLines.getIndexBufferDataBolzano4N();
        int[] indexBufferBolzano5N = bufLines.getIndexBufferDataBolzano5N();

        // nabindování a vlastnosti VBs
        OGLBuffers.Attrib[] attributesAxis = {
                new OGLBuffers.Attrib("inPositionAxis", 3), // 3 floats
        };
        buffersAxis = new OGLBuffers(vertexBufferAxis, attributesAxis, indexBufferAxis);

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2), // 2 floats
        };
        buffers = new OGLBuffers(vertexBufferData, attributes, indexBufferData);

        OGLBuffers.Attrib[] attributesBolzano1N = {
                new OGLBuffers.Attrib("inPositionBol1N", 2), // 2 floats
        };
        buffersBolzano1N = new OGLBuffers(vertexBufferBolzano, attributesBolzano1N, indexBufferBolzano1N);

        OGLBuffers.Attrib[] attributesBolzano2N = {
                new OGLBuffers.Attrib("inPositionBol2N", 2), // 2 floats
        };
        buffersBolzano2N = new OGLBuffers(vertexBufferBolzano, attributesBolzano2N, indexBufferBolzano2N);

        OGLBuffers.Attrib[] attributesBolzano3N = {
                new OGLBuffers.Attrib("inPositionBol3N", 2), // 2 floats
        };
        buffersBolzano3N = new OGLBuffers(vertexBufferBolzano, attributesBolzano3N, indexBufferBolzano3N);

        OGLBuffers.Attrib[] attributesBolzano4N = {
                new OGLBuffers.Attrib("inPositionBol4N", 2), // 2 floats
        };
        buffersBolzano4N = new OGLBuffers(vertexBufferBolzano, attributesBolzano4N, indexBufferBolzano4N);

        OGLBuffers.Attrib[] attributesBolzano5N = {
                new OGLBuffers.Attrib("inPositionBol5N", 2), // 2 floats
        };
        buffersBolzano5N = new OGLBuffers(vertexBufferBolzano, attributesBolzano5N, indexBufferBolzano5N);
    }

    // inicializace scény
    @Override
    public void init() {
        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();

        // Set the clear color
        glClearColor(0.8f, 0.8f, 0.8f, 1.0f);

        createBuffers(200); // 200x200 grid

        // init shaderu
        shaderProgramTriangles = ShaderUtils.loadProgram("/uloha2/triangles");
        shaderProgramLines = ShaderUtils.loadProgram("/uloha2/lines");

        // nastavení aktuálního shaderu
        glUseProgram(shaderProgramTriangles);

        // init parametrů Weierstrassovy fce
        n = 1;
        b = 1;

        // init lokátorů
        locMathModelView = glGetUniformLocation(shaderProgramTriangles, "model");
        locMathViewView = glGetUniformLocation(shaderProgramTriangles, "view");
        locMathProjView = glGetUniformLocation(shaderProgramTriangles, "proj");
        locTime = glGetUniformLocation(shaderProgramTriangles, "time");
        locView = glGetUniformLocation(shaderProgramTriangles, "view3D");
        locN = glGetUniformLocation(shaderProgramTriangles, "N");
        locB = glGetUniformLocation(shaderProgramTriangles, "b");
        locZoom = glGetUniformLocation(shaderProgramTriangles, "zoom");
        locPx = glGetUniformLocation(shaderProgramTriangles, "px");
        locPy = glGetUniformLocation(shaderProgramTriangles, "py");

        locMathModelAxis = glGetUniformLocation(shaderProgramLines, "model");
        locMathViewAxis = glGetUniformLocation(shaderProgramLines, "view");
        locMathProjAxis = glGetUniformLocation(shaderProgramLines, "proj");
        locObjectType = glGetUniformLocation(shaderProgramLines, "objType");
        locZoomLines = glGetUniformLocation(shaderProgramLines, "zoom");
        locPxLines = glGetUniformLocation(shaderProgramLines, "px");
        locPyLines = glGetUniformLocation(shaderProgramLines, "py");

        textRenderer = new OGLTextRenderer(width, height);

        // definice kamery
        cam = cam.withPosition(new Vec3D(0, 10, 0))
                .withAzimuth(Math.PI * 1.50)
                .withZenith(Math.PI * -0.020);
    }

    // metoda pro renderování
    public void render() {
        glClearColor(0.1f, 0.1f, 0.1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glUseProgram(shaderProgramTriangles);

        // přepínání mezi drátovým modelem a vyplněnými plochami
        if (view3D) {
            glUniform1i(locView, 1);
            if (wireframe) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            else glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        } else {
            glUniform1i(locView, 0);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        // spuštění a reset animace
        if (funcType == 1) {
            if (startAnim) {
                if (b < 5) b += 0.01;
            }
            if (resetAnim) {
                b = 1;
                resetAnim = false;
            }
        }
        if (resetZoom) resetZoom = false;

        // uniform proměnné pro renderování
        glUniform1i(locN, n);
        glUniform1f(locB, b);
        glUniform1f(locPx, (float) px);
        glUniform1f(locPy, (float) -py);
        glUniform1f(locTime, innerLevel);
        glUniform1f(locZoom, zoom);
        glUniformMatrix4fv(locMathViewView, false,
                cam.getViewMatrix().floatArray());
        glUniformMatrix4fv(locMathProjView, false,
                proj.floatArray());
        glUniform1i(locN, n);

        if (funcType == 1) {
            // Weierstrassova funkce
            glUniformMatrix4fv(locMathModelView, false,
                    new Mat4Scale(2).floatArray());
            glPatchParameteri(GL_PATCH_VERTICES, 3);
            buffers.draw(GL_PATCHES, shaderProgramTriangles);
        }

        glUseProgram(shaderProgramLines);
        glUniform1f(locZoomLines, zoom);
        glUniform1f(locPxLines, (float) px);
        glUniform1f(locPyLines, (float) -py);

        if (funcType == 2) {
            // Bolzanova funkce
            if (line1) { // N = 1
                glUniform1i(locObjectType, 1);
                glUniformMatrix4fv(locMathModelAxis, false,
                        new Mat4Scale(2).floatArray());
                buffersBolzano1N.draw(GL_LINES, shaderProgramLines);
            }
            if (line2) { // N = 2
                glUniform1i(locObjectType, 2);
                glUniformMatrix4fv(locMathModelAxis, false,
                        new Mat4Scale(2).floatArray());
                buffersBolzano2N.draw(GL_LINES, shaderProgramLines);
            }
            if (line3) { // N = 3
                glUniform1i(locObjectType, 3);
                glUniformMatrix4fv(locMathModelAxis, false,
                        new Mat4Scale(2).floatArray());
                buffersBolzano3N.draw(GL_LINES, shaderProgramLines);
            }
            if (line4) { // N = 4
                glUniform1i(locObjectType, 4);
                glUniformMatrix4fv(locMathModelAxis, false,
                        new Mat4Scale(2).floatArray());
                buffersBolzano4N.draw(GL_LINES, shaderProgramLines);
            }
            if (line5) { // N = 5
                glUniform1i(locObjectType, 5);
                glUniformMatrix4fv(locMathModelAxis, false,
                        new Mat4Scale(2).floatArray());
                buffersBolzano5N.draw(GL_LINES, shaderProgramLines);
            }
        }

        // osy
        glUniformMatrix4fv(locMathViewAxis, false,
                cam.getViewMatrix().floatArray());
        glUniformMatrix4fv(locMathProjAxis, false,
                proj.floatArray());
        glUniform1i(locObjectType, 0);
        glUseProgram(shaderProgramLines);
        buffersAxis.draw(GL_LINES, shaderProgramLines);

        // popis ovládání
        String textFuncType = new String("1,2 -> Vybraná funkce: " + funcTypeString);
        String textAnimation = new String("Enter -> Animace inkrementace podle B: " + startAnimString);
        String textresetAnimation = new String("Reset animace -> Backspace");
        String textCamera = new String("[LMB] a WSAD -> Camera; SPACE -> First person");
        String textView = new String("P -> Zobrazení: " + viewString);
        String textWireframe = new String("F -> Fill/Line: " + wireframeString);
        String textinnerLevel = new String("E -> + innerLevel; R -> - innerLevel");
        String textinnerLevel2 = new String("innerLevel: " + innerLevel);
        textRenderer.clear();
        textRenderer.addStr2D(5, 30, textFuncType);
        if (funcType == 1) {
            textRenderer.addStr2D(5, 60, new String("parametry funkce:"));
            textRenderer.addStr2D(5, 120, new String("a: 0.8 B: " + b + " N: " + n));
            textRenderer.addStr2D(5, 180, "+/- -> inkrementace/dekrementace N");
            textRenderer.addStr2D(5, 210, textAnimation);
            textRenderer.addStr2D(5, 240, textresetAnimation);
            textRenderer.addStr2D(5, 450, textView);
            if (view3D) {
                textRenderer.addStr2D(5, 480, textWireframe);
                textRenderer.addStr2D(5, 540, "Nastavení vnitřní úrovně teselace u 3D");
                textRenderer.addStr2D(5, 570, textinnerLevel);
                textRenderer.addStr2D(5, 600, textinnerLevel2);
            }
        } else {
            textRenderer.addStr2D(5, 60, new String("Varianty funkce podle počtu opakování N:"));
            if (line1) textRenderer.addStr2D(5, 120, new String("num1 -> N = 1: Zobrazeno"));
            else textRenderer.addStr2D(5, 120, new String("num1 -> N = 1: Nezobrazeno"));
            if (line2) textRenderer.addStr2D(5, 150, new String("num2 -> N = 2: Zobrazeno"));
            else textRenderer.addStr2D(5, 150, new String("num2 -> N = 2: Nezobrazeno"));
            if (line3) textRenderer.addStr2D(5, 180, new String("num3 -> N = 3: Zobrazeno"));
            else textRenderer.addStr2D(5, 180, new String("num3 -> N = 3: Nezobrazeno"));
            if (line4) textRenderer.addStr2D(5, 210, new String("num4 -> N = 4: Zobrazeno"));
            else textRenderer.addStr2D(5, 210, new String("num4 -> N = 4: Nezobrazeno"));
            if (line5) textRenderer.addStr2D(5, 240, new String("num5 -> N = 5: Zobrazeno"));
            else textRenderer.addStr2D(5, 240, new String("num5 -> N = 5: Nezobrazeno"));
        }
        textRenderer.addStr2D(5, 300, "mouseWheel -> zoom: " + zoom + "x");
        textRenderer.addStr2D(5, 330, "Z -> reset zoom:");
        DecimalFormat numberFormat = new DecimalFormat("0.00");
        textRenderer.addStr2D(5, 360, "→← -> posun po ose x: " + numberFormat.format(px) + " ↑↓ -> posun po ose y: " + numberFormat.format(py));
        textRenderer.addStr2D(5, 420, textCamera);
        textRenderer.addStr2D(width - 520, height - 10, "Autor: Bc. Ondřej Schneider (c) PGRF3 UHK");
        textRenderer.draw();
    }

    // vykreslení scény
    @Override
    public void display() {
        glEnable(GL_DEPTH_TEST); // zapnutí z-testu
        glLineWidth(5); // šířka čar

        //renderování z pohledu kamery do obrazovky
        render();
    }
}
