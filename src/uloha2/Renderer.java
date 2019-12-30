package uloha2;

import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

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

    OGLBuffers buffers, buffersAxis;

    int shaderProgramView, shaderProgramAxis;

    int width, height, n;
    float b;

    // uniform lokátory
    int locMathModelView, locMathViewView, locMathProjView,
            locMathModelAxis, locMathViewAxis, locMathProjAxis, locTime, locN, locB,
            locObjectType, locView;

    // proměnné pro přepínání módů
    boolean wireframe = false;
    boolean view3D = false;
    boolean startAnim = true;
    boolean resetAnim = false;

    // proměnné pro shadery
    float time = 0;

    // proměnné pro TextRenderer
    String viewString = "2D";
    String wireframeString = "Fill";
    String startAnimString = "Zapnuta";

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
                    case GLFW_KEY_DOWN:
                        cam = cam.down(1);
                        break;
                    case GLFW_KEY_UP:
                        cam = cam.up(1);
                        break;
                    case GLFW_KEY_SPACE:
                        cam = cam.withFirstPerson(!cam.getFirstPerson());
                        break;
                    case GLFW_KEY_E:
                        time += 1;
                        break;
                    case GLFW_KEY_R:
                        time -= 1;
                        break;
                    // 2D/3D zobrazení
                    case GLFW_KEY_P:
                        if(view3D) {
                            viewString = "2D";
                            view3D = false;
                        } else {
                            viewString = "3D";
                            view3D = true;
                        }
                        break;
                    case GLFW_KEY_F:
                        // vyplenePlochy/dratovyModel
                        if(wireframe) {
                            wireframeString = "Fill";
                            wireframe = false;
                        } else {
                            wireframeString = "Wireframe";
                            wireframe = true;
                        }
                        break;
                    case GLFW_KEY_KP_ADD:
                        // inkrementace proměnné ve vybrané funkci
                        if(n < 50) {
                            n++;
                        } else {
                            n = 50;
                        }
                        break;
                    case GLFW_KEY_KP_SUBTRACT:
                        // dekrementace proměnné ve vybrané funkci
                        if(n > 1) {
                            n--;
                        } else {
                            n = 1;
                        }
                        break;
                    case GLFW_KEY_ENTER:
                        // zapnutí/vypnutí animace
                        if(startAnim) {
                            startAnimString = "Vypnuta";
                            startAnim = false;
                        } else {
                            startAnimString = "Zapnuta";
                            startAnim = true;
                        }
                        break;
                    case GLFW_KEY_BACKSPACE:
                        // reset animace
                        if(!resetAnim) {
                            resetAnim = true;
                        }
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
                cam = cam.mulRadius(0.9f);
            else
                cam = cam.mulRadius(1.1f);

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

        BufferGenerator buf = new BufferGenerator();

        buf.createVertexBuffer(m, m);
        buf.createIndexBuffer(m , m);
        //buf.createIndexBufferTriangleStrips(m, m);

        float[] vertexBufferData = buf.getVertexBufferData();
        int[] indexBufferData = buf.getIndexBufferData();

        /*for (int i = 0; i < vertexBufferData.length; i++) {
            System.out.print(vertexBufferData[i] + "  ");
        }

        for (int i = 0; i < indexBufferData.length; i++) {
            System.out.print(indexBufferData[i] + "  ");
        }*/

        float[] vertexBufferAxis = new float[] {-4,0,0,4,0,0,
        0,-4,0,0,4,0,
                0,0,-4,0,0,4};
        int[] indexBufferAxis = new int[] {0,1,2,3,4,5};

        // nabindování a vlastnosti VB
        OGLBuffers.Attrib[] attributesAxis = {
                new OGLBuffers.Attrib("inPositionAxis", 3), // 3 floats
        };
        buffersAxis = new OGLBuffers(vertexBufferAxis, attributesAxis,
                indexBufferAxis);

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2), // 2 floats
        };
        buffers = new OGLBuffers(vertexBufferData, attributes,
                indexBufferData);
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
        shaderProgramView = ShaderUtils.loadProgram("/uloha2/view");
        shaderProgramAxis = ShaderUtils.loadProgram("/uloha2/axis");

        // nastavení aktuálního shaderu
        glUseProgram(shaderProgramView);

        // načtení textury
        /*try {
            texture = new OGLTexture2D("textures/globeNormal.png");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        n = 10;
        b = 1;

        // init lokátorů
        locMathModelView = glGetUniformLocation(shaderProgramView, "model");
        locMathViewView = glGetUniformLocation(shaderProgramView, "view");
        locMathProjView = glGetUniformLocation(shaderProgramView, "proj");
        locTime = glGetUniformLocation(shaderProgramView, "time");
        locObjectType = glGetUniformLocation(shaderProgramView, "objType");
        locView = glGetUniformLocation(shaderProgramView, "view3D");
        locN = glGetUniformLocation(shaderProgramView, "N");
        locB = glGetUniformLocation(shaderProgramView, "b");

        locMathModelAxis = glGetUniformLocation(shaderProgramAxis, "model");
        locMathViewAxis = glGetUniformLocation(shaderProgramAxis, "view");
        locMathProjAxis = glGetUniformLocation(shaderProgramAxis, "proj");

        textRenderer = new OGLTextRenderer(width, height);

        // definice kamery
        cam = cam.withPosition(new Vec3D(0, 10, 0))
                .withAzimuth(Math.PI * 1.50)
                .withZenith(Math.PI * -0.020);
    }

    // metoda pro renderování z pohledu kamery do obrazovky
    public void renderFromView() {
        glClearColor(0.1f, 0.1f, 0.1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glUseProgram(shaderProgramView);

        // přepínání mezi drátovým modelem a vyplněnými plochami
        if(view3D) {
            glUniform1i(locView, 1);
            if (wireframe) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            else glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        } else {
            glUniform1i(locView, 0);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        if(startAnim) {
            if(b<5) b+=0.01;
        }

        if(resetAnim) {
            b = 1;
            resetAnim = false;
        }

        // uniform proměnné pro renderování
        glUniform1i(locN, n);
        glUniform1f(locB, b);
        glUniform1f(locTime, time);
        glUniformMatrix4fv(locMathViewView, false,
                cam.getViewMatrix().floatArray());
        glUniformMatrix4fv(locMathProjView, false,
                proj.floatArray());
        glUniform1i(locN, n);

        // Weierstrassova funkce
        //glUseProgram(shaderProgramView);
        //glUniform1i(locObjectType, 0);
        glUniformMatrix4fv(locMathModelView, false,
                new Mat4Scale(2).floatArray());
        glPatchParameteri(GL_PATCH_VERTICES, 3);
        buffers.draw(GL_PATCHES, shaderProgramView);

        // osy
        glUseProgram(shaderProgramAxis);
        //glUniform1i(locObjectType, 0);
        glUniformMatrix4fv(locMathViewAxis, false,
                cam.getViewMatrix().floatArray());
        glUniformMatrix4fv(locMathProjAxis, false,
                proj.floatArray());
        buffersAxis.draw(GL_LINES, shaderProgramAxis);

        // popis ovládání
        String textCamera = new String(this.getClass().getName() + ": [LMB] a WSAD↑↓ -> Camera; SPACE -> First person");
        String textView = new String("P -> Zobrazení: " + viewString);
        String textWireframe = new String("F -> Fill/Line: " + wireframeString);
        String textAnimation = new String("Enter -> Animace inkrementace podle N: " + startAnimString);
        String textresetAnimation = new String("Reset animace -> Backspace");
        String innerLevel = new String("E -> + innerLevel; R -> - innerLevel");
        String innerLevel2 = new String("innerLevel: " + time);
        textRenderer.clear();
        textRenderer.addStr2D(5, 30, textCamera);
        textRenderer.addStr2D(5, 60, textView);
        if(view3D) textRenderer.addStr2D(5, 90, textWireframe);
        textRenderer.addStr2D(5, 120, textAnimation);
        textRenderer.addStr2D(5, 150, textresetAnimation);
        textRenderer.addStr2D(5, 180, new String("parametry funkce: B: " + b + " N: " + n));
        textRenderer.addStr2D(5, 210, innerLevel);
        textRenderer.addStr2D(5, 240, innerLevel2);
        textRenderer.addStr2D(width - 520, height - 10, "Autor: Bc. Ondřej Schneider (c) PGRF3 UHK");
        textRenderer.draw();
    }

    // vykreslení scény
    @Override
    public void display() {
        glEnable(GL_DEPTH_TEST); // zapnutí z-testu
        //glEnable(GL_BLEND); // zapnutí blendingu
        glLineWidth(5); // šířka čar

        // zastavení pohybu světla a modelu
        /*if (!mouseButton1)
            rot1 += 0.01;
        if (!mouseButton2)
            rot2 += 0.01;*/

        // perspektivní/ortogonální projekce
        //if (persp) proj = new Mat4PerspRH(Math.PI / 4, 0.5, 0.01, 100.0);
        //else proj = new Mat4OrthoRH(40, 20, 0.01, 100.0);

        //renderování z pohledu kamery do obrazovky
        renderFromView();

    }
}
