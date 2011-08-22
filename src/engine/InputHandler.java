package engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;


public class InputHandler {
	private boolean done = false;
	private boolean blend;              // Blending ON/OFF
	
	private final float piover180 = 0.0174532925f;
    private float heading;
    private float xpos;
    private float zpos;
    private float yrot;                 // Y Rotation
    private float walkbias = 0;
    private float walkbiasangle = 0;
    private float lookupdown = 0.0f;
    private float z=0.0f;               // Depth Into The Screen
    
    private float xCurrent = 0.0f;
    private float xPrevious = 0.0f;
    private float xDiff = 0.0f;
    private float yCurrent = 0.0f;
    private float yPrevious = 0.0f;
    private float yDiff = 0.0f;
    
    private static final float MOUSE_SENSITIVITY = 0.2f;
    // Angle coordinates of the camera
    private float xAngle = -33.5f;
    private float yAngle = 53.0f;
    
	public void event() { 
		handleMouse();
		
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {       // Exit if Escape is pressed
            done = true;
        }
        if(Display.isCloseRequested()) {                     // Exit if window is closed
            done = true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {

            xpos += (float) Math.sin(heading * piover180) * 0.05f;
            zpos += (float) Math.cos(heading * piover180) * 0.05f;
            if (walkbiasangle >= 359.0f) {
                walkbiasangle = 0.0f;
            }
            else {
                walkbiasangle += 10;
            }
            walkbias = (float) Math.sin(walkbiasangle * piover180) / 20.0f;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
            xpos -= (float) Math.sin(heading * piover180) * 0.05f;
            zpos -= (float) Math.cos(heading * piover180) * 0.05f;
            if (walkbiasangle <= 1.0f) {
                walkbiasangle = 359.0f;
            }
            else {
                walkbiasangle -= 10;
            }
            walkbias = (float) Math.sin(walkbiasangle * piover180) / 20.0f;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
            heading -= 1.0f;
            xAngle = 360.0f - heading;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
            heading += 1.0f;
            xAngle = 360.0f - heading;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR)) {
            lookupdown -= 1.0f;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_NEXT)) {
            lookupdown += 1.0f;
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
        	walkbias = (float) Math.sin(walkbiasangle * piover180) / 10.0f;
        }
	}
	
	public void handleMouse() { 
        xCurrent = Mouse.getX();
        yCurrent = Mouse.getY();

        xDiff = xCurrent - xPrevious;
        yDiff = yCurrent - yPrevious;

        xPrevious = xCurrent;
        yPrevious = yCurrent;

        xAngle = xAngle + xDiff*MOUSE_SENSITIVITY;

        if (xAngle > 360.0f)
            xAngle = xAngle - 360.0f;
        else if (xAngle < -360.0f)
            xAngle = xAngle + 360.0f;

        // Vertical movement of the mouse
        yAngle = yAngle + yDiff*MOUSE_SENSITIVITY;
        heading = xAngle;
	}
	
	public float getX() {
		return xpos;
	}
	
	public float getY() {
		return yrot;
	}
	
	public float getZ() {
		return zpos;
	}
	
	public float getLookUpDown() { 
		return lookupdown;
	}
	
	public float getWalkBias() { 
		return walkbias;
	}
	
	public boolean exit() { 
		return done;
	}
	
	public float getXAngle() { 
		return xAngle;
	}
	
	
	public float getYAngle() { 
		return yAngle;
	}
}
