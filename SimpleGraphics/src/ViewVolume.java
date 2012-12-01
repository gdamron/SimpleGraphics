/* class ViewVolume
 * OpenGL methods for drawing a view volume (given Camera specification)
 * using OpenGL clipping planes
 *
 * CS 428   Doug DeCarlo
 */

import javax.media.opengl.*;
import javax.vecmath.*;

class ViewVolume
{
    // Constructor
    public ViewVolume()
    {
    }

    //-----------------------------------------------------------------------

    // Draw view volume (given camera specification in Camera) in
    // its canonical coordinate system
    public void draw(GL gl)
    {
    	
    	double l = Camera.left();
    	double r = Camera.right();
    	double b = Camera.bottom();
    	double t = Camera.top();
    	double n = Camera.near();
    	double f = Camera.far();
    	
    	// back clipping pane vertices
    	double bl = adjustedBackAttribute(l);
    	double br = adjustedBackAttribute(r);
    	double bb = adjustedBackAttribute(b);
    	double bt = adjustedBackAttribute(t);
    	
        // Cube vertices
        double[] v0 = {l, b, -n };
        double[] v1 = {bl, bb,  -f };
        double[] v2 = {bl,  bt,  -f };
        double[] v3 = {l,  t, -n };
        double[] v4 = { r, b, -n };
        double[] v5 = { br, bb,  -f };
        double[] v6 = { br,  bt,  -f };
        double[] v7 = { r,  t, -n };
        
        // draw
        gl.glColor3d(0.0, 0.0, 1.0);
        addSide(gl, v0, v3, v7, v4);
        gl.glColor3d(0.5, 0.5, 0.5);
        addSide(gl, v1, v5, v6, v2);
        addSide(gl, v5, v6, v7, v4);
        addSide(gl, v1, v2, v3, v0);
    }
    
    public void transform(GL gl) {
    	// apply transformations before drawing
    	//gl.glTranslated(-Camera.tx(), -Camera.ty(), -Camera.tz());
    	gl.glRotated(-Camera.roll(), 0, 0, 1);
        gl.glRotated(-Camera.yaw(), 0, 1, 0);
        gl.glRotated(-Camera.pitch(), 1, 0, 0);
        gl.glTranslated(Camera.tx(), Camera.ty(), -Camera.tz());	
    }

    // Specify positions of all clipping planes
    //  - this is called from WorldView.draw()
    //  - it calls placeClipPlane() below 6 times -- once for each
    //    side of the view volume
    public void placeClipPlanes(GL gl)
    {
    	//gl.glTranslated(0, 0, 0.0002);
    	//gl.glScaled(1.01, 1.01, 1.0001);
    	//double offset = 0.01;
        Point3d p10,p20,p30,p11,p21,p31,p12,p22,p32,p13,p23,p33,p14,p24,p34,p15,p25,p35;
        // near plane
        p10 = new Point3d(Camera.left(), Camera.bottom(), Camera.near());
        p20 = new Point3d(Camera.left(), Camera.top(), Camera.near());
        p30 = new Point3d(Camera.right(), Camera.bottom(), Camera.near());
        placeClipPlane(gl, GL.GL_CLIP_PLANE0, p10, p20, p30);
        // left plane
        p11 = new Point3d(Camera.left(), Camera.bottom(), Camera.near());
        p21 = new Point3d(adjustedBackAttribute(Camera.left()), adjustedBackAttribute(Camera.bottom()), Camera.far());
        p31 = new Point3d(Camera.left(), Camera.top(), Camera.near());
        placeClipPlane(gl, GL.GL_CLIP_PLANE1, p11, p21, p31);
        // far plane
        p12 = new Point3d(adjustedBackAttribute(Camera.right()), adjustedBackAttribute(Camera.bottom()), Camera.far());
        p22 = new Point3d(adjustedBackAttribute(Camera.right()), adjustedBackAttribute(Camera.top()), Camera.far());
        p32 = new Point3d(adjustedBackAttribute(Camera.left()), adjustedBackAttribute(Camera.bottom()), Camera.far());
        placeClipPlane(gl, GL.GL_CLIP_PLANE2, p12, p22, p32);
        // right plane
        p13 = new Point3d(Camera.right(), Camera.bottom(), Camera.near());
        p23 = new Point3d(Camera.right(), Camera.top(), Camera.near());
        p33 = new Point3d(adjustedBackAttribute(Camera.right()), adjustedBackAttribute(Camera.bottom()), Camera.far());
        placeClipPlane(gl, GL.GL_CLIP_PLANE3, p13, p23, p33);
        // bottom plane
        p14 = new Point3d(Camera.left(), Camera.bottom(), Camera.near());
        p24 = new Point3d(Camera.right(), Camera.bottom(), Camera.near());
        p34 = new Point3d(adjustedBackAttribute(Camera.right()), adjustedBackAttribute(Camera.bottom()), Camera.far());
        placeClipPlane(gl, GL.GL_CLIP_PLANE4, p14, p24, p34);
        // top plane
        p15 = new Point3d(Camera.right(), Camera.top(), Camera.near());
        p25 = new Point3d(Camera.left(), Camera.top(), Camera.near());
        p35 = new Point3d(adjustedBackAttribute(Camera.right()), adjustedBackAttribute(Camera.top()), Camera.far());
        placeClipPlane(gl, GL.GL_CLIP_PLANE5, p15, p25, p35);
           
    }

    // Specify position of one particular clipping plane given 3
    // points on the plane ordered counter-clockwise
    void placeClipPlane(GL gl, int plane, Point3d p1, Point3d p2, Point3d p3)
    {
        

        // Compute the plane equation from the 3 points -- fill in eqn[]
        Point3d p1Copy = new Point3d(p1);
        Point3d p2Copy = new Point3d(p2);
        Point3d p3Copy = new Point3d(p3);
        p2Copy.sub(p1Copy);
        p3Copy.sub(p1Copy);
        Vector3d p1Vec = new Vector3d(p1Copy);
        Vector3d p2Vec = new Vector3d(p2Copy);
        Vector3d p3Vec = new Vector3d(p3Copy);
        Vector3d nVec = new Vector3d();
        nVec.cross(p2Vec, p3Vec);
        double d = nVec.dot(p1Vec);
        
        // Plane equation Ax + By + Cz + D stored as [A,B,C,D]
        double[] eqn = { nVec.x, nVec.y, nVec.z, d };

        // Specify the clipping plane
        gl.glClipPlane(plane, eqn, 0);
    }
    
    double adjustedBackAttribute(double att) {
    	double adjustedAtt = 0.0;
    	if (Camera.isPerspective()) {
    		adjustedAtt = (att*Camera.far())/Camera.near();
    	} else {
    		adjustedAtt = att;
    	}
    	return adjustedAtt;
    }
    
    void addSide(GL gl, double[] p0, double[] p1, double[] p2, double[] p3) {
    	int mode = GL.GL_LINE_LOOP;
    	gl.glBegin(mode);
        gl.glVertex3dv(p0, 0);
        gl.glVertex3dv(p1, 0);
        gl.glVertex3dv(p2, 0);
        gl.glVertex3dv(p3, 0);
        gl.glEnd();
    }
}
