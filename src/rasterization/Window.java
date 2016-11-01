/**
 * Starts the physics engine and rasterization components. Defines the
 * constants, the objects in the scene, the key listeners, and initializes the 
 * GUI
 * 
 */

package rasterization;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import rasterization.geometry.Edge;
import rasterization.geometry.Poly;
import rasterization.light.AmbientLight;
import rasterization.light.DirectionalLight;
import rasterization.light.Light;
import rasterization.light.PointLight;
import rasterization.physics.Contact;
import rasterization.tools.Matrix33;
import rasterization.tools.Matrix44;
import rasterization.tools.Utils;
import rasterization.tools.Vector3;
import rasterization.tools.Vector4;

public class Window extends javax.swing.JFrame {
    private Vector3 cameraPos = new Vector3(0, 1, -2);
    //private double[] cameraOrient = new double[]{0, 0, 0};
    private Matrix33 rotMatrix = new Matrix33();
    private Matrix44 projMatrix = new Matrix44();
    private double transSpeed = .1;
    
    private double fov = 50;
    private double near = 1;
    private double far = 20;
    private double scale;
    
    private ArrayList<WorldObject> objects = new ArrayList<>();
    //private ArrayList<Poly> polygons = new ArrayList<>();
    private ArrayList<Light> lights = new ArrayList<>();
    
    private static final Vector3 g = new Vector3(0, -1, 0);
    private double dt = 50.0/1000;

    public Window() {
        initComponents();
        scale = 1/Math.tan(fov*0.5*Math.PI/180.0);
        projMatrix = getProj();
        
        setupKeyListeners();
        
        /*for (int i = 0; i < 10; i++) {
            double x = Math.random()*8-4;
            double y = Math.random()+0.5;
            double z = Math.random()*4-8;
            double xVel = Math.random()*2-1;
            double yVel = Math.random()*2-1;
            double zVel = Math.random()*2-1;
            Ball b = new Ball(new Vector3(x, y, z), new Vector3(xVel, yVel, zVel), 0.5, 2, 7, 255, 0, 0, 0.1);
            objects.add(b);
        }*/
        
        Ball b1 = new Ball(new Vector3(0.6, 2, -7), new Vector3(0, 0, 0), 0.5, 2, 7, 255, 0, 0, 0.1);
        Ball b2 = new Ball(new Vector3(0, 1, -7), new Vector3(0, 0, 0), 0.5, 2, 7, 255, 0, 0, 0.1);
        Plane p1 = new Plane(new Vector3(-5, -1, -2),
                new Vector3(5, -1, -2),
                new Vector3(5, -1, -12),
                new Vector3(-5, -1, -12), 4, 3, 0, 255, 0, 0);
        Plane p2 = new Plane(new Vector3(-5, -1, -2),
                new Vector3(-5, -1, -12),
                new Vector3(-5, 3, -12),
                new Vector3(-5, 3, -2), 4, 3, 0, 255, 0, 0);
        Plane p3 = new Plane(new Vector3(5, -1, -2),
                new Vector3(5, 3, -2),
                new Vector3(5, 3, -12),
                new Vector3(5, -1, -12), 4, 3, 0, 255, 0, 0);
        Plane p4 = new Plane(new Vector3(-5, -1, -12),
                new Vector3(5, -1, -12),
                new Vector3(5, 3, -12),
                new Vector3(-5, 3, -12), 4, 3, 0, 255, 0, 0);
        Plane p5 = new Plane(new Vector3(-5, -1, -2),
                new Vector3(-5, 3, -2),
                new Vector3(5, 3, -2),
                new Vector3(5, -1, -2), 4, 3, 0, 255, 0, 0);
        objects.add(b1);
        objects.add(b2);
        objects.add(p1);
        objects.add(p2);
        objects.add(p3);
        objects.add(p4);
        objects.add(p5);
        
        /*Plane p = new Plane(new Vector3(-5, -1, -2),
                new Vector3(5, -1, -2),
                new Vector3(5, -1, -12),
                new Vector3(-5, -1, -12), 0, 3, 0, 255, 0);
        objects.add(p);*/
        
        //makeSphere(-3, -.5, -7, 0.5, 3);
        //makeSphere(3, -1, -7, 1, 3);
        
        
        /*Poly ground = new Poly(new Vector3[]{
                new Vector3(-5, -.5-Math.sqrt(0.5), -near),
                new Vector3(5, -.5-Math.sqrt(0.5), -near),
                new Vector3(5, -.5-Math.sqrt(0.5), -far),
                new Vector3(-5, -.5-Math.sqrt(0.5), -far)},
                0, 255, 0);*/
        
        /*Poly t1 = new Poly(new Vector3[]{
            new Vector3(-.5, 0, -2),
            new Vector3(0, -.5, -2),
            new Vector3(0, .5, -2)
        }, 0, 255, 0);*/
        
        //polygons.add(t1);
        //polygons.add(ground);
        
        AmbientLight al = new AmbientLight(0.12);
        DirectionalLight dl = new DirectionalLight(
                new Vector3(1, 1, 1), new Vector3(0.5, 0.5, 0.5), new Vector3(-1, 1, 1));
        PointLight pl = new PointLight(
                new Vector3(1, 1, 1), new Vector3(0.3, 0.3, 0.3), new Vector3(0, 2, -5));
        PointLight pl2 = new PointLight(
                new Vector3(1, 1, 1), new Vector3(0.3, 0.3, 0.3), new Vector3(1, 3, -10));
        
        lights.add(al);
        //lights.add(dl);
        lights.add(pl);
        lights.add(pl2);
        
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                step();
            }
        }, 0, (int)(1000*dt));
    }
    
    private void step() {
        // Gravity
        for (WorldObject wo : objects) {
            if (wo.invMass > 0) {
                wo.vel = wo.vel.plus(g.times(dt));
            }
        }
        
        // Contacts
        //ArrayList<Contact> contacts = new ArrayList();
        for (int i = 0; i < objects.size()-1; i++) {
            WorldObject rbi = objects.get(i);
            for (int j = i+1; j < objects.size(); j++) {
                WorldObject rbj = objects.get(j);
                
                if (rbi.invMass == 0 && rbj.invMass == 0) {
                    continue;
                }
                
                Contact c = rbi.generateContact(rbj);
                if (c != null) {
                    c.solve(dt);
                    //contacts.add(c);
                }
            }
        }
        
        // Integrate
        for (WorldObject wo : objects) {
            wo.integrate(dt);
        }
        
        mainPanel.repaint();
    }
    
    private void redoRotMatrix() {
        double tx = Math.toRadians(xRotSlider.getValue());
        double ty = Math.toRadians(yRotSlider.getValue());
        double tz = Math.toRadians(zRotSlider.getValue());
        
        Matrix33 rotx = new Matrix33(new double[][]{
            new double[]{1, 0, 0},
            new double[]{0, Math.cos(tx), -Math.sin(tx)},
            new double[]{0, Math.sin(tx), Math.cos(tx)}
        });
        
        Matrix33 roty = new Matrix33(new double[][]{
            new double[]{Math.cos(ty), 0, Math.sin(ty)},
            new double[]{0, 1, 0},
            new double[]{-Math.sin(ty), 0, Math.cos(ty)}
        });
        
        Matrix33 rotz = new Matrix33(new double[][]{
            new double[]{Math.cos(tz), -Math.sin(tz), 0},
            new double[]{Math.sin(tz), Math.cos(tz), 0},
            new double[]{0, 0, 1}
        });
        
        rotMatrix = rotx.times(roty).times(rotz);
        //mainPanel.repaint();
    }
    
    /**
     * Gets the projection matrix for the camera
     */
    private Matrix44 getProj() {
        return new Matrix44(new double[][]{
            new double[]{scale*mainPanel.getHeight()/mainPanel.getWidth(), 0, 0, 0},
            new double[]{0, scale, 0, 0},
            new double[]{0, 0, -far/(far-near), -far*near/(far-near)},
            new double[]{0, 0, -1, 0}
        });
    }
    
    /**
     * @return The forward direction
     */
    private Vector3 getForward() {
        return rotMatrix.transpose().times(new Vector3(0, 0, -1));
    }
    
    /**
     * @return The right direction
     */
    private Vector3 getRight() {
        return rotMatrix.transpose().times(new Vector3(1, 0, 0));
    }
    
    /**
     * @return The up direction
     */
    private Vector3 getUp() {
        return rotMatrix.transpose().times(new Vector3(0, 1, 0));
    }
    
    // <editor-fold defaultstate="collapsed" desc="setupKeyListeners">
    private void setupKeyListeners() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('w'), "wPressed");
        getRootPane().getActionMap().put("wPressed", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraPos = cameraPos.plus(getForward().times(transSpeed));
                //mainPanel.repaint();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('s'), "sPressed");
        getRootPane().getActionMap().put("sPressed", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraPos = cameraPos.plus(getForward().times(-transSpeed));
                //mainPanel.repaint();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('a'), "aPressed");
        getRootPane().getActionMap().put("aPressed", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraPos = cameraPos.plus(getRight().times(-transSpeed));
                //mainPanel.repaint();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('d'), "dPressed");
        getRootPane().getActionMap().put("dPressed", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraPos = cameraPos.plus(getRight().times(transSpeed));
                //mainPanel.repaint();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('q'), "qPressed");
        getRootPane().getActionMap().put("qPressed", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraPos = cameraPos.plus(getUp().times(-transSpeed));
                //mainPanel.repaint();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('e'), "ePressed");
        getRootPane().getActionMap().put("ePressed", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraPos = cameraPos.plus(getUp().times(transSpeed));
                //mainPanel.repaint();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('r'), "rPressed");
        getRootPane().getActionMap().put("rPressed", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraPos = new Vector3(0, 0, 0);
                rotMatrix = new Matrix33();
                //mainPanel.repaint();
            }
        });
    }// </editor-fold>
    
    /**
     * Clip the polygons that do not need to be rendered
     * @param verticies All vertices
     * @param w Width of the screen
     * @param h Height of the screen
     * @param side 0=right, 1=top, 2=left, 3=bottom
     */
    private void clipPoly(ArrayList<Vector3> verticies, int w, int h, int side) {
        if (verticies.isEmpty()) return;
        
        if (side < 0 || side > 3) {
            System.err.println("Error, side is not between 0 and 3, inclusive, in clipPoly");
            System.exit(1);
            return;
        }
        
        //System.out.println("Starting clipping");
        ArrayList<Vector3> inputList = new ArrayList<>();
        inputList.addAll(verticies);
        verticies.clear();

        Vector3 last = inputList.get(inputList.size()-1);
        for (Vector3 v : inputList) {
            //System.out.print("current v = ");
            //v.printVector();
            if (inside(v, w, h, side)) {
                //System.out.print("v on inside, ");
                if (!inside(last, w, h, side)) {
                    //System.out.println("last on outside");
                    verticies.add(getIntersect(v, last, w, h, side));
                } else {
                    //System.out.println("last on inside");
                }
                verticies.add(v);
            } else if (inside(last, w, h, side)) {
                //System.out.println("v on outside, last on inside");
                verticies.add(getIntersect(last, v, w, h, side));
            } else {
                //System.out.println("v on outside, last on outside");
            }
            last = v;
        }
    }
    
    /**
     * Tells whether or not a point is on the screen
     * @param v The point
     * @param w Screen width
     * @param h Screen height
     * @param side 0=right, 1=top, 2=left, 3=bottom
     */
    private boolean inside(Vector3 v, int w, int h, int side) {
        if (side == 0) return v.array[0] < w;
        else if (side == 1) return v.array[1] >= 0;
        else if (side == 2) return v.array[0] >= 0;
        else return v.array[1] < h;
    }
    
    /**
     * Gets the intersect between two points
     * @param v1 Point 1
     * @param v2 Point 2
     * @param w Width of screen
     * @param h Height of screen
     * @param side 0=right, 1=top, 2=left, 3=bottom
     * @return 
     */
    private Vector3 getIntersect(Vector3 v1, Vector3 v2, int w, int h, int side) {
        double t;
        if (side == 0) {
            t = (w - v1.array[0])/(v2.array[0] - v1.array[0]);
        } else if (side == 1) {
            t = (0 - v1.array[1])/(v2.array[1] - v1.array[1]);
        } else if (side == 2) {
            t = (0 - v1.array[0])/(v2.array[0] - v1.array[0]);
        } else {
            t = (h - v1.array[1])/(v2.array[1] - v1.array[1]);
        }
        return v1.plus(v2.minus(v1).times(t));
    }
    
    /**
     * Clip the vertices that are too far away or too close
     * @param verticies All vertices considered
     * @param side 0 = near, 1 = far
     */
    private void preProjClip(ArrayList<Vector3> verticies, int side) {
        if (verticies.isEmpty()) return;
        
        if (side != 0 && side != 1) {
            System.err.println("Error, side is not 0 or 1 in preProjClip");
            System.exit(1);
            return;
        }
        
        //System.out.println("Starting clipping");
        ArrayList<Vector3> inputList = new ArrayList<>();
        inputList.addAll(verticies);
        verticies.clear();

        Vector3 last = inputList.get(inputList.size()-1);
        for (Vector3 v : inputList) {
            //System.out.print("current v = ");
            //v.printVector();
            if (insidePreProj(v, side)) {
                //System.out.print("v on inside, ");
                if (!insidePreProj(last, side)) {
                    //System.out.println("last on outside");
                    verticies.add(getIntersectPreProj(v, last, side));
                } else {
                    //System.out.println("last on inside");
                }
                verticies.add(v);
            } else if (insidePreProj(last, side)) {
                //System.out.println("v on outside, last on inside");
                verticies.add(getIntersectPreProj(last, v, side));
            } else {
                //System.out.println("v on outside, last on outside");
            }
            last = v;
        }
    }
    
    public boolean insidePreProj(Vector3 vert, int side) {
        if (side == 0) return vert.array[2] < -near; // all negative
        else return vert.array[2] > -far;
    }
    
    /**
     * Get side intersects 
     * @param v1 Point 1
     * @param v2 Point 2
     * @param side 0 = near, 1 = far
     * @return The position of the intersect
     */
    public Vector3 getIntersectPreProj(Vector3 v1, Vector3 v2, int side) {
        double t;
        if (side == 0) {
            t = (-near - v1.array[2])/(v2.array[2] - v1.array[2]);
        } else {
            t = (-far - v1.array[2])/(v2.array[2] - v1.array[2]);
        }
        return v1.plus(v2.minus(v1).times(t));
    }
    
    /**
     * The panel on which the scene is drawn
     */
    public class MainPanel extends JPanel {
        /**
         * Paint the panel
         * @param g Grapahics object
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            long t = System.currentTimeMillis();
            
            int w = getWidth();
            int h = getHeight();
            
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            double[][] zbuffer = new double[h][w];
            for (int i = 0; i < h; i++) {
                Arrays.fill(zbuffer[i], 1.0);
            }
            
            ArrayList<Poly> polygons = new ArrayList<>();
            
            // Fill polygons with polygons from all the objects in camera space
            for (WorldObject wo : objects) {
                polygons.addAll(wo.getPolys(cameraPos, rotMatrix));
            }
            
            // Project the polygon onto the camera's viewing plane
            for (Poly p : polygons) {
                //ArrayList<Vector3> camXYZ = new ArrayList<>(Arrays.asList(p.v));
                ArrayList<Vector3> camXYZ = new ArrayList<>();
                //System.out.println("Camera Coordinates: ");
                for (Vector3 vec : p.v) {
                    //Vector3 vec3Cam = vec.augment().times(trans).times(rotx).times(roty).times(rotz).decrement();
                    Vector3 vec3Cam = rotMatrix.times(vec.minus(cameraPos));
                    camXYZ.add(vec3Cam);
                }
                
                // Near clipping, so we don't get weirdness
                preProjClip(camXYZ, 0);
                // Far clipping, just for optimization
                preProjClip(camXYZ, 1);
                
                ArrayList<Vector3> projVerticies = new ArrayList<>();
                for (Vector3 vec : camXYZ) {
                    Vector4 vec4 = projMatrix.times(vec.augment()).makeHomogenous();
                    double x = (vec4.array[0]+1)*w/2.0;
                    double y = (-vec4.array[1]+1)*h/2.0;
                    
                   projVerticies.add(new Vector3(x, y, vec4.array[2]));
                }
                
                // Clipping
                clipPoly(projVerticies, w, h, 0);
                clipPoly(projVerticies, w, h, 1);
                clipPoly(projVerticies, w, h, 2);
                clipPoly(projVerticies, w, h, 3);
                if (projVerticies.isEmpty()) continue; // So we don't have issues with new Poly creation
                
                ArrayList<Vector3> finalVerticies = new ArrayList<>();
                for (Vector3 v : projVerticies) {
                    int x = (int)Math.round(v.array[0]);
                    int y = (int)Math.round(v.array[1]);
                    finalVerticies.add(new Vector3(x, y, v.array[2]));
                }
                
                /*for (Vector3 vert : finalVerticies) {
                    for (int i = -2; i <= 2; i++) {
                        for (int j = -2; j <= 2; j++) {
                            int x = (int)vert.array[0]+i;
                            int y = (int)vert.array[1]+j;
                            if (x >= 0 && x < w && y >= 0 && y < h) {
                                img.setRGB(x, y, 0xFF69b4);
                                zbuffer[y][x] = 0;
                            }
                        }
                    }
                   // vert.printVector();
                }*/
                //System.out.print("Cam pos: ");
                //new Vector3(cameraPos).printVector();
                
                Poly newP = new Poly(finalVerticies.toArray(new Vector3[0]), p.getR(), p.getG(), p.getB(), 0, p.normal);

                ArrayList<Edge> globalEdgeTable = new ArrayList<>();
                ArrayList<Edge> activeEdgeTable = new ArrayList<>();
                
                for (Edge e : newP.edges) {
                    if (e.ymax == e.ymin) continue;
                    globalEdgeTable.add(e);
                }
                if (globalEdgeTable.isEmpty()) continue; // So we don't have issues with only horizontal lines
                
                Collections.sort(globalEdgeTable, Edge.GET);
                int scanline = (int)Math.round(globalEdgeTable.get(0).ymin);
                
                for (Edge e : globalEdgeTable) {
                    if ((int)Math.round(e.ymin) == scanline) activeEdgeTable.add(e);
                    else break;
                }
                
                Vector3 color01 = new Vector3(0, 0, 0);
                for (Light l : lights) {
                    color01 = color01.plus(l.getColors(p, cameraPos));
                }
                int redFinal = (int)(Utils.clamp01(color01.array[0])*255.0);
                int greenFinal = (int)(Utils.clamp01(color01.array[1])*255.0);
                int blueFinal = (int)(Utils.clamp01(color01.array[2])*255.0);
                int color = redFinal << 16 | greenFinal << 8 | blueFinal;
                
                //System.out.println(color);
                
                /*System.out.println("GET: ");
                for (Edge e : globalEdgeTable) {
                    System.out.println(e);
                }*/
                
                // Actually do the rasterization!
                while (!activeEdgeTable.isEmpty() && scanline < h) {
                    //System.out.println("Drawing line at y="+
                    //        scanline+"GET: "+globalEdgeTable.size()+", AET: "+activeEdgeTable.size());
                    if (activeEdgeTable.size() % 2 != 0) {
                        System.err.println("This shouldn't happen, odd number of edges in AET.");
                        //THINK ABOUT THIS. MAY WAY TO DO Z CLIPPING AND SEE IF THAT FIXES THINGS
                        //System.exit(1);
                    }
                    
                    if (scanline >= 0) {
                        for (int n = 0; n < activeEdgeTable.size()-1; n += 2) {
                            //System.out.println("Edge pair starting at "+n);
                            Edge start = activeEdgeTable.get(n);
                            Edge end = activeEdgeTable.get(n+1);

                            double z1 = start.zvalMin +
                                    (start.zvalMax-start.zvalMin)*(scanline-start.ymin)/(start.ymax-start.ymin);
                            double z2 = end.zvalMin +
                                    (end.zvalMax-end.zvalMin)*(scanline-end.ymin)/(end.ymax-end.ymin);

                            int startx = (int)Math.round(start.xval);
                            int endx = (int)Math.round(end.xval);
                            //System.out.println("x from "+startx+", to "+endx);
                            for (int x = startx < 0 ? 0 : startx; x < endx && x < w; x++) {
                                double z = z1 + (z2-z1)*(x-start.xval)/(end.xval-start.xval);

                                if (z < zbuffer[scanline][x] && z > 0) {
                                    img.setRGB(x, scanline, color);
                                    zbuffer[scanline][x] = z;
                                }
                            }
                        }
                    }
                        
                    scanline++;

                    for (int i = 0; i < activeEdgeTable.size(); i++) {
                        Edge e = activeEdgeTable.get(i);
                        if ((int)Math.round(e.ymax) == scanline) {
                            activeEdgeTable.remove(e);
                            i--;
                            continue;
                        }
                        e.xval += e.invm;
                    }
                    
                    for (Edge e : globalEdgeTable) {
                        if ((int)Math.round(e.ymin) == scanline) {
                            activeEdgeTable.add(e);
                        }
                    }
                    Collections.sort(activeEdgeTable, Edge.AET);
                }
            }
            
            g.drawImage(img, 0, 0, null);
            //System.out.println(System.currentTimeMillis()-t);
        }
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new MainPanel();
        javax.swing.JPanel mainPanel = this.mainPanel;
        zLabel = new javax.swing.JLabel();
        yLabel = new javax.swing.JLabel();
        xLabel = new javax.swing.JLabel();
        zRotSlider = new javax.swing.JSlider();
        xRotSlider = new javax.swing.JSlider();
        yRotSlider = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                mainPanelAncestorResized(evt);
            }
        });

        zLabel.setText("Z:");

        yLabel.setText("Y:");

        xLabel.setText("X:");

        zRotSlider.setBackground(new java.awt.Color(255, 255, 255));
        zRotSlider.setMaximum(180);
        zRotSlider.setMinimum(-180);
        zRotSlider.setValue(0);
        zRotSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zRotSliderStateChanged(evt);
            }
        });

        xRotSlider.setBackground(new java.awt.Color(255, 255, 255));
        xRotSlider.setMaximum(180);
        xRotSlider.setMinimum(-180);
        xRotSlider.setValue(0);
        xRotSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xRotSliderStateChanged(evt);
            }
        });

        yRotSlider.setBackground(new java.awt.Color(255, 255, 255));
        yRotSlider.setMaximum(180);
        yRotSlider.setMinimum(-180);
        yRotSlider.setValue(0);
        yRotSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                yRotSliderStateChanged(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(326, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(xLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xRotSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(yLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yRotSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(zLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zRotSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(408, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(xRotSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(xLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yRotSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(yLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(zRotSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zLabel))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void xRotSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xRotSliderStateChanged
        redoRotMatrix();
    }//GEN-LAST:event_xRotSliderStateChanged

    private void yRotSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_yRotSliderStateChanged
        redoRotMatrix();
    }//GEN-LAST:event_yRotSliderStateChanged

    private void zRotSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zRotSliderStateChanged
        redoRotMatrix();
    }//GEN-LAST:event_zRotSliderStateChanged

    private void mainPanelAncestorResized(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_mainPanelAncestorResized
        projMatrix = getProj();
    }//GEN-LAST:event_mainPanelAncestorResized

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Window().setVisible(true);
            }
        });
    }
    
    private MainPanel mainPanel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel xLabel;
    private javax.swing.JSlider xRotSlider;
    private javax.swing.JLabel yLabel;
    private javax.swing.JSlider yRotSlider;
    private javax.swing.JLabel zLabel;
    private javax.swing.JSlider zRotSlider;
    // End of variables declaration//GEN-END:variables
}
