package game;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import engine.InputHandler;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Main {
	private InputHandler input;
    private boolean done = false;
    private boolean fullscreen = false;
    private final String windowTitle = "Game";
    private boolean f1 = false;
    private DisplayMode displayMode;
    private int filter;                 // Which Filter To Use
    private Texture texture;
    private float walkbias = 0;
    private Sector sector1;
    private float lookupdown = 0.0f;
    
    
    public static void main(String args[]) throws Exception {
        boolean fullscreen = false;
        if(args.length>0) {
            if(args[0].equalsIgnoreCase("fullscreen")) {
                fullscreen = true;
            }
        }

        Main app = new Main();

        app.init();
        app.run(fullscreen);
    }
    
    public void run(boolean fullscreen) {
		InputHandler input = new InputHandler();
		
        this.fullscreen = fullscreen;
        try {
            while (!input.exit()) {
            	input.event();
                render(input);
                Display.update();
            }
            cleanup();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
		
	}

    private boolean render(InputHandler input) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);  // Clear The Screen And The Depth Buffer
        GL11.glLoadIdentity();                                  // Reset The View

        float x_m, y_m, z_m, u_m, v_m;
        float xtrans = -input.getX();
        float ztrans = -input.getZ();
        float ytrans = -input.getWalkBias()-0.25f;

        int numTriangles;

        GL11.glRotatef(-input.getYAngle(),1.0f,0,0);
        GL11.glRotatef(-input.getXAngle(),0,1.0f,0);

        GL11.glTranslatef(xtrans, ytrans, ztrans);
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture[filter]);

        numTriangles = sector1.numTriangles;

        // Process Each Triangle
        for (int loop_m = 0; loop_m < numTriangles; loop_m++)
        {
            GL11.glBegin(GL11.GL_TRIANGLES);
            texture.bind();
            	GL11.glColor3f(0,80,30);
                GL11.glNormal3f( 0.0f, 0.0f, 1.0f);
                x_m = sector1.triangle[loop_m].vertex[0].x;
                y_m = sector1.triangle[loop_m].vertex[0].y;
                z_m = sector1.triangle[loop_m].vertex[0].z;
                u_m = sector1.triangle[loop_m].vertex[0].u;
                v_m = sector1.triangle[loop_m].vertex[0].v;
                GL11.glTexCoord2f(u_m,v_m); GL11.glVertex3f(x_m,y_m,z_m);

                GL11.glColor3f(0.0f,1.0f,0.0f);
                
                x_m = sector1.triangle[loop_m].vertex[1].x;
                y_m = sector1.triangle[loop_m].vertex[1].y;
                z_m = sector1.triangle[loop_m].vertex[1].z;
                u_m = sector1.triangle[loop_m].vertex[1].u;
                v_m = sector1.triangle[loop_m].vertex[1].v;
                GL11.glTexCoord2f(u_m,v_m); GL11.glVertex3f(x_m,y_m,z_m);

                GL11.glColor3f(0,0,0);
                x_m = sector1.triangle[loop_m].vertex[2].x;
                y_m = sector1.triangle[loop_m].vertex[2].y;
                z_m = sector1.triangle[loop_m].vertex[2].z;
                u_m = sector1.triangle[loop_m].vertex[2].u;
                v_m = sector1.triangle[loop_m].vertex[2].v;
                GL11.glTexCoord2f(u_m,v_m); GL11.glVertex3f(x_m,y_m,z_m);
            GL11.glEnd();
        }
        return true;
    }
    private void createWindow() throws Exception {
        Display.setFullscreen(fullscreen);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640
                && d[i].getHeight() == 480
                && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        //Display.setDisplayMode(displayMode);
        Display.setTitle(windowTitle);
        Display.create();
    }
    private void init() throws Exception {

        createWindow();
        setupWorld();
        initGL();
        
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("data/wall.png"));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    

    private void initGL() {
        GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
        GL11.glClearDepth(1.0f); // Depth Buffer Setup
        // Really Nice Perspective Calculations
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glLoadIdentity(); // Reset The Projection Matrix

        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(45.0f,
                (float) 800 / (float) 600,
                0.1f,100.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix
    }
    private static void cleanup() {
        Display.destroy();
    }

    private final void setupWorld() {
        float x, y, z, u, v;
        int numtriangles;

        try {
            String line;
            BufferedReader dis = new BufferedReader(new FileReader("data/world.txt"));

            while ((line = dis.readLine()) != null) {
                if (line.trim().length() == 0 || line.trim().startsWith("//"))
                    continue;

                if (line.startsWith("NUMPOLLIES")) {
                    int numTriangles;

                    numTriangles = Integer.parseInt(line.substring(line.indexOf("NUMPOLLIES") + "NUMPOLLIES".length() + 1));
                    sector1 = new Sector(numTriangles);

                    break;
                }
            }

            for (int i = 0; i < sector1.numTriangles; i++) {
                for (int vert = 0; vert < 3; vert++) {

                    while ((line = dis.readLine()) != null) {
                        if (line.trim().length() == 0 || line.trim().startsWith("//"))
                            continue;

                        break;
                    }

                    if (line != null) {
                        StringTokenizer st = new StringTokenizer(line, " ");

                        sector1.triangle[i].vertex[vert].x = Float.valueOf(st.nextToken()).floatValue();
                        sector1.triangle[i].vertex[vert].y = Float.valueOf(st.nextToken()).floatValue();
                        sector1.triangle[i].vertex[vert].z = Float.valueOf(st.nextToken()).floatValue();
                        sector1.triangle[i].vertex[vert].u = Float.valueOf(st.nextToken()).floatValue();
                        sector1.triangle[i].vertex[vert].v = Float.valueOf(st.nextToken()).floatValue();
                    }
                }
            }

            dis.close();

        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
class Vertex {
    public float x, y, z;
    public float u, v;
}

class Triangle {
    public Vertex vertex[];

    public Triangle() {
        vertex=new Vertex[3];
        for(int i=0;i<3;i++) {
            vertex[i]=new Vertex();
        }
    }
}

class Sector {
    public int numTriangles;
    Triangle triangle[]; //holds class Triangle objects
    public Sector(int num) {
        numTriangles=num;
        triangle=new Triangle[numTriangles];
        for(int i=0;i<numTriangles;i++) {
            triangle[i]=new Triangle();
        }
    }
}
