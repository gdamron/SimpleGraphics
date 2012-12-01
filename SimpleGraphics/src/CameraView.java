/* class CameraView
 * The OpenGL drawing component of the interface
 * for the "camera" viewport
 *
 * CS 428   Doug DeCarlo
 */

import java.awt.*;
import javax.media.opengl.*;

public class CameraView extends SimpleGLCanvas
{
    Cube cube;

    // Constructor
    public CameraView(Window parent, boolean debug)
    {
        super(parent);
        debugging = debug;

        cube = new Cube();
    }
    
    // ------------------------------------------------------------

    // Method for initializing OpenGL (called once at the beginning)
    public void init(GL gl)
    {
        // Set background color to black
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Turn on visibility test
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    // Method for handling window resizing
    public void projection(GL gl)
    {
        // Set drawing area
        gl.glViewport(0, 0, width, height);

        // Bounds of window coordinates: (left, right, bottom, top)
        float l, r, b, t;

        // Set the camera projection transformation based on the
        // aspect ratio of the window and the projection type
        // (orthographic or perspective)

        // set aspect ratio
        float aspect = (float) width / (float) height;
        l = -aspect;
        r = aspect;
        b = -1;
        t = 1;
        
        // Record camera viewport coordinates (these values are used
        // when drawing the view volume in the world window)
        Camera.setCurrentView(l, r, b, t);

        // Set the camera projection transformation
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        if (Camera.isPerspective())
        	gl.glFrustum(l, r, b, t, Camera.near(), Camera.far());
        else
        	gl.glOrtho(l, r, b, t, Camera.near(), Camera.far());
        //

        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    // Method for drawing the contents of the window
    public void draw(GL gl)
    {
        // Clear the window
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Note: we typically will only call projection() when the
        //       window is resized; in this case, the near/far planes
        //       of this window can be changed by the user interface so
        //       we always call projection in case they were altered
        projection(gl);

        // Draw the scene (the cube)
        
        gl.glLoadIdentity();
        transform(gl);
        cube.transform(gl);
        cube.draw(gl, true);
        
    }

    // Apply the (camera) viewing transformation (V)
    void transform(GL gl)
    {
    	//gl.glTranslated(-Camera.tx(), -Camera.ty(), -Camera.tz());
    	gl.glRotated(Camera.roll(), 0, 0, 1);
        gl.glRotated(Camera.yaw(), 0, 1, 0);
        gl.glRotated(Camera.pitch(), 1, 0, 0);
        gl.glTranslated(-Camera.tx(), -Camera.ty(), Camera.tz());
        
    }
}
