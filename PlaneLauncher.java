import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by Rezenter on 12/7/15.
 * commit?
 */
public class PlaneLauncher extends JFrame {

    public static void main(String[] args) throws IOException {
        new PlaneLauncher();
    }

    public static final String FRAME_TITLE = "launcher";

    public PlaneLauncher() throws IOException {
        super(FRAME_TITLE);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());


        JButton start = new JButton();
        start.setText("Start");
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new Plane().run();
                    dispose();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(start);

        JButton exit = new JButton();
        exit.setText("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel.add(exit);

        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 100);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int)d.getWidth()/2 - 150, (int)d.getHeight()/2 - 50);
        setVisible(true);
    }

    public static class Plane {

        final String TITLE = "game";
        int count = 0;
        int nCount = 0;
        int fCount = 0;
        int collision = 0;
        private GLFWErrorCallback errorCallback;
        private GLFWKeyCallback keyCallback;
        private long window;
        private Vector3f movement = new Vector3f();
        private int move = 0;
        private int pos = 0;

        public Plane() throws FileNotFoundException {
        }

        public void run() throws FileNotFoundException {
            try {
                try {
                    init();
                    loop();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                glfwDestroyWindow(window);
                keyCallback.release();
            } finally {
                glfwTerminate();
                errorCallback.release();
            }
        }

        private Vector3f nearCol = new Vector3f(0.1, 0.1, 0.1);
        private Vector3f farCol = new Vector3f(0.2, 0.2, 0.2);
        private int up = move - pos;

        private void init() throws IOException {
            for (int c = 0; c < 3; c++) {
                near[c] = new Mountain((float) (c - 0.75));
                far[c] = new Mountain((float) (c - 0.5));
                nearTriang.add(near[c].triangulate(nearCol, (float) 0.99));
                farTriang.add(far[c].triangulate(farCol, 1));
            }

            glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
            if (glfwInit() != GL11.GL_TRUE)
                throw new IllegalStateException("Unable to initialize GLFW");

            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
            glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
            ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            int WIDTH = GLFWvidmode.width(vidmode);
            int HEIGHT = GLFWvidmode.height(vidmode);

            window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, glfwGetPrimaryMonitor(), NULL);
            if (window == NULL)
                throw new RuntimeException("Failed to create the GLFW window");

            glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
                @Override
                public void invoke(long window, int key, int scancode, int action, int mods) {
                    if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                        glfwSetWindowShouldClose(window, GL_TRUE);
                    } else {
                        if ((key == GLFW_KEY_W || key == GLFW_KEY_UP) && action == GLFW_PRESS) {
                            if (move != 1 && up == 0) {
                                move += 1;
                            }
                        } else {
                            if ((key == GLFW_KEY_S || key == GLFW_KEY_DOWN) && action == GLFW_PRESS) {
                                if (move != -1 && up == 0) {
                                    move -= 1;
                                }
                            }
                        }
                    }
                }

            });
            plane = planeGenerator();
            cloudTriang = cloudGenerator();
            glfwMakeContextCurrent(window);
            glfwSwapInterval(1);
            glfwShowWindow(window);
        }

        private Mountain[] near = new Mountain[3];
        private Mountain[] far = new Mountain[3];
        private ArrayList<ArrayList> nearTriang = new ArrayList<>();
        private ArrayList<ArrayList> farTriang = new ArrayList<>();
        private double nearCount = 0.75;
        private double farCount = 2;
        private ArrayList<Triangle> plane = new ArrayList<>();
        private int phase = 0;

        private void loop() throws IOException {
            GLContext.createFromCurrent();
            glClearColor(0, 0, (float) 0.1, 0);
            int nc = 0;
            int fc = 0;
            boolean[] cf = line(1);
            boolean[] cs = line(0);
            int lineCount = 0;

            while (glfwWindowShouldClose(window) == GL_FALSE) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                count++;
                nCount++;
                fCount++;

                glClear(GL_COLOR_BUFFER_BIT);
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                glOrtho(-1, 1, -1.f, 1.f, 1.f, -1.f);
                glMatrixMode(GL_MODELVIEW);
                glLoadIdentity();
                glEnable(GL_DEPTH_TEST);
                glMatrixMode(GL_MODELVIEW);

                glPushMatrix();
                glTranslated(-0.002 * nCount, -1, 0);
                for (ArrayList<Triangle> t : nearTriang) {
                    t.forEach(Plane::drawTriangle);
                }
                nearCount -= 0.002;
                if (nearCount <= 0) {
                    nearCount = 1;
                    near[nc] = new Mountain((float) (2.25));
                    nearTriang.remove(nc);
                    nearTriang.add(nc, near[nc].triangulate(nearCol, (float) 0.99));
                    if (nc == 2) {
                        nc = -1;
                    }
                    nc++;
                    nCount -= 500;
                    for (ArrayList<Triangle> al : nearTriang) {
                        for (Triangle t : al) {
                            t.move(new Vector3f(-1, 0, 0));
                        }
                    }
                }
                glPopMatrix();

                glPushMatrix();
                glTranslated(-0.001 * fCount, -1, 0);
                farCount -= 0.001;
                for (ArrayList<Triangle> t : farTriang) {
                    t.forEach(Plane::drawTriangle);
                }
                farCount -= 0.001;
                if (farCount <= 0) {
                    farCount = 2;
                    far[fc] = new Mountain((float) (2.5));
                    farTriang.remove(fc);
                    farTriang.add(fc, far[fc].triangulate(farCol, (float) 1));
                    if (fc == 2) {
                        fc = -1;
                    }
                    fc++;
                    fCount -= 1000;
                    for (ArrayList<Triangle> al : farTriang) {
                        for (Triangle t : al) {
                            t.move(new Vector3f(-1, 0, 0));
                        }
                    }
                }
                glPopMatrix();

                glMatrixMode(GL_MODELVIEW);

                glPushMatrix();
                glTranslated(1.252 - count * 0.004 + lineCount * 1.252, 0.2, -0.8);
                drawCloudLine(cf);
                glPushMatrix();
                glTranslated(-1.252, 0, 0);
                drawCloudLine(cs);
                glPopMatrix();
                if (count % 313 == 0 && count != 0) {
                    lineCount += 1;
                    cs = cf;
                    cf = line(1);
                }
                glPopMatrix();

                glPushMatrix();
                glTranslated(-0.6, 0.2, -0.5);
                glTranslated(movement.get()[0], movement.get()[1], movement.get()[2]);
                if (collision > 24 && cs[pos + 1] && collision < 278 && phase == 0) {
                    new PlaneLauncher();
                    glfwSetWindowShouldClose(window, GL_TRUE);
                } else {
                    collision++;
                }
                collision = collision % 313;
                if (move != pos) {
                    up = move - pos;
                    manoeuvre(up);
                    up = move - pos;
                }
                glRotated((4 * Math.sin(count * 0.02) - 30 ), 1, 0, 0);
                glRotated(4 * Math.sin((count + 19) * 0.02), 0, 1, 0);
                glRotated(4 * Math.sin(((count) * 0.02) + Math.PI / 2), 0, 0, 1);
                glTranslated(0, 0.07 * Math.sin((count) * 0.02), 0);
                plane.forEach(Plane::drawTriangle);
                glPopMatrix();

                drawSky();

                sync(60);

                glfwSwapBuffers(window);
                glfwPollEvents();
            }
        }

        private void manoeuvre(int up) {
            if (phase == 90) {
                pos = move;
                phase = 0;
            } else {
                movement.add(new Vector3f(0, 0.5 * up / 90, 0));
                if (phase < 45) {
                    glRotated(phase * up, 0, 0, 1);
                } else {
                    glRotated((-phase + 90) * up, 0, 0, 1);
                }
                glRotated(phase * 4, up, 0, 0);
                phase++;
            }
        }

        private static ArrayList<Triangle> cloudTriang;

        private static ArrayList<Triangle> planeGenerator() throws IOException {
            ArrayList<Triangle> res = new ArrayList<>();
            Reader pl = new Reader("plane");
            for (Obj t : pl.pointReader()) {
                res.addAll(t.triangulate());
            }
            return res;
        }

        public static void drawCloudLine(boolean[] pos) {
            for (int i = 0; i < 3; i++) {
                if (pos[i]) {
                    glMatrixMode(GL_MODELVIEW);
                    glPushMatrix();
                    glTranslated(0, (i - 1) * 0.5, 0);
                    cloudTriang.forEach(Plane::drawTriangle);
                    glPopMatrix();
                }
            }
        }

        private static ArrayList<Triangle> cloudGenerator() throws IOException {
            ArrayList<Triangle> res = new ArrayList<>();
            Reader clf = new Reader("cloud");
            for (Obj t : clf.pointReader()) {
                res.addAll(t.triangulate());
            }
            return res;
        }

        private boolean[] line(int a) {
            boolean[] res = new boolean[3];
            if (a == 1) {
                Random random = new Random();
                int count = Math.abs(random.nextInt()) % 3;
                if (count == 1) {
                    res[Math.abs(random.nextInt()) % 3] = true;
                } else {
                    if (count == 2) {
                        count = Math.abs(random.nextInt()) % 3;
                        for (int i = 0; i < 3; i++) {
                            if (i != count) {
                                res[i] = true;
                            } else {
                                res[i] = false;
                            }
                        }
                    }else{
                        if(random.nextBoolean()){
                            res = line(1);
                        }
                    }
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    res[i] = false;
                }
            }
            return res;
        }

        private void drawSky() {
            glPushMatrix();
            glBegin(GL_QUADS);
            glColor3f(0, 0, (float) 0.1);
            glVertex3f(-1, (float) 0.7, (float) 1);
            glVertex3f(1, (float) 0.7, (float) 1);
            glColor3f((float) 0.4, (float) 0, (float) 1);
            glVertex3f(1, (float) -0.7, (float) 1);
            glVertex3f(-1, (float) -0.7, (float) 1);
            glEnd();
            glBegin(GL_QUADS);
            glColor3d(0.4, 0, 1);
            glVertex3d(-1, -0.7, 1);
            glVertex3d(-0.5, -0.7, 1);
            glColor3d(0.5, 0, 0.5);
            glVertex3d(-0.5, -1, 1);
            glVertex3d(-1, -1, 1);
            glEnd();
            glBegin(GL_QUADS);
            glColor3d(0.4, 0, 1);
            glVertex3d(1, -0.7, 1);
            glVertex3d(0.5, -0.7, 1);
            glColor3d(0.5, 0, 0.5);
            glVertex3d(0.5, -1, 1);
            glVertex3d(1, -1, 1);
            glEnd();
            glBegin(GL_QUADS);
            glColor3d(0.4, 0, 1);
            glVertex3d(-0.5, -0.7, 1);
            glVertex3d(0, -0.7, 1);
            glColor3d(0.9, 0.1, 0.1);
            glVertex3d(0, -1, 1);
            glColor3d(0.5, 0, 0.5);
            glVertex3d(-0.5, -1, 1);
            glEnd();
            glBegin(GL_QUADS);
            glColor3d(0.4, 0, 1);
            glVertex3d(0.5, -0.7, 1);
            glVertex3d(0, -0.7, 1);
            glColor3d(0.9, 0.1, 0.1);
            glVertex3d(0, -1, 1);
            glColor3d(0.5, 0, 0.5);
            glVertex3d(0.5, -1, 1);
            glEnd();
            glPopMatrix();
        }


        public static void drawTriangle(Triangle t) {
            glBegin(GL_TRIANGLES);
            glColor3f(t.get()[3].get()[0], t.get()[3].get()[1], t.get()[3].get()[2]);
            glVertex3f(t.get()[0].get()[0], t.get()[0].get()[1], t.get()[0].get()[2]);
            glVertex3f(t.get()[1].get()[0], t.get()[1].get()[1], t.get()[1].get()[2]);
            glVertex3f(t.get()[2].get()[0], t.get()[2].get()[1], t.get()[2].get()[2]);
            glEnd();
        }

        private long variableYieldTime, lastTime;

        private void sync(int fps) {
            if (fps <= 0) return;
            long sleepTime = 1000000000 / fps;
            long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000 * 1000));
            long overSleep = 0;
            try {
                while (true) {
                    long t = System.nanoTime() - lastTime;

                    if (t < sleepTime - yieldTime) {
                        Thread.sleep(1);
                    } else if (t < sleepTime) {
                        Thread.yield();
                    } else {
                        overSleep = t - sleepTime;
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);
                if (overSleep > variableYieldTime) {
                    variableYieldTime = Math.min(variableYieldTime + 200 * 1000, sleepTime);
                } else if (overSleep < variableYieldTime - 200 * 1000) {
                    variableYieldTime = Math.max(variableYieldTime - 2 * 1000, 0);
                }
            }
        }


    }
}