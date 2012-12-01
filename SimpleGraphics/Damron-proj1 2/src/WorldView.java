/* class WorldView
 * The OpenGL drawing component of the interface
 * for the "world" viewport
 *
 * CS 428   Doug DeCarlo
 */

import java.awt.*;
import javax.media.opengl.*;
//import javax.media.opengl.glu.*;

public class WorldView extends SimpleGLCanvas
{
    ViewVolume viewVol;
    Cube cube;

    public WorldView(Window parent, boolean debug)
    {
        super(parent);
        debugging = debug;

        viewVol = new ViewVolume();
        cube = new Cube();
    }

    // Method for initializing OpenGL (called once at the beginning)
    public void init(GL gl)
    {
        // Set background color to black
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Turn on visibility test
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    // ------------------------------------------------------------

    // Method for handling window resizing
    public void projection(GL gl)
    {
    	float l,r,t,b, near, far, aspect;
    	aspect = (float) width / (float) height;
    	l = -aspect;
    	r = aspect;
    	b = -1;
    	t = 1;
    	near = 2;
    	far = 100;
    	
        // Set drawing area
        gl.glViewport(0, 0, width, height);
        
        // Set the world projection transformation based on the
        // aspect ratio of the window
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(l, r, b, t, near, far);
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    // Method for drawing the contents of the window
    public void draw(GL gl)
    {
        // Clear the window
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        projection(gl);
        
        // Draw the cube (perhaps clipped) and the view volume
        drawViewVolume(gl);
        
        if (World.isClipping())
        	enableClipPlanes(gl, true);
        
        drawClipPlanes(gl);
        drawCube(gl, true, false);
        
        if (World.isClipping()) {
        	enableClipPlanes(gl, false);
        	drawCube(gl, false, true);
        }
    }

    // Apply the (world) viewing transformation (W)
    void transformation(GL gl)
    {
    	// using right hand rule. these settings make rotation *feel* right
        gl.glRotated(-World.roll(), 0, 0, 1);
        gl.glRotated(-World.yaw(), 0, 1, 0);
        gl.glRotated(-World.pitch(), 1, 0, 0);
        gl.glTranslated(-World.tx(), -World.ty(), World.tz());
    }
    
    void drawCube(GL gl, boolean mode, boolean scale) {
    	gl.glPushMatrix();
        gl.glLoadIdentity();
        transformation(gl);
        cube.transform(gl);
        if (scale)
        	gl.glScaled(0.99, 0.99, 0.99);
        cube.draw(gl, mode);
        gl.glPopMatrix();
    }
    
    void drawViewVolume(GL gl) {
    	gl.glLoadIdentity();
        transformation(gl);
        viewVol.transform(gl);
        viewVol.draw(gl);
    }
    
    void drawClipPlanes(GL gl) {
    	gl.glPushMatrix();
        gl.glLoadIdentity();
        transformation(gl);
        viewVol.transform(gl);
        viewVol.placeClipPlanes(gl);
        gl.glPopMatrix();
    }
    
    void enableClipPlanes(GL gl, boolean b) {
    	if (b) {
    		gl.glEnable(GL.GL_CLIP_PLANE0);
    		gl.glEnable(GL.GL_CLIP_PLANE1);
    		gl.glEnable(GL.GL_CLIP_PLANE2);
    		gl.glEnable(GL.GL_CLIP_PLANE3);
    		gl.glEnable(GL.GL_CLIP_PLANE4);
    		gl.glEnable(GL.GL_CLIP_PLANE5);
    	} else {
    		gl.glDisable(GL.GL_CLIP_PLANE0);
    		gl.glDisable(GL.GL_CLIP_PLANE1);
    		gl.glDisable(GL.GL_CLIP_PLANE2);
    		gl.glDisable(GL.GL_CLIP_PLANE3);
    		gl.glDisable(GL.GL_CLIP_PLANE4);
    		gl.glDisable(GL.GL_CLIP_PLANE5);
    	}
    }
    
}
