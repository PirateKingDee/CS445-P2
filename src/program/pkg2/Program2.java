/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.pkg2;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import java.io.File;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import org.lwjgl.input.Keyboard;

public class Program2 {
    
//    List<Edge> global_edge;
//    List<Edge> active_edge;
//    List<Edge> all_edge;
    
    //method: start
    //purpose: start the program.
    public void start(){
        try {
            //create Window
            createWindow();
             //Initialize GL
            initGL();
            //render
            render();
        } catch (LWJGLException ex) {
            ex.printStackTrace();
        }
       
    }
    
    //method: createWindow
    //purpose: create window for the program.
    public void createWindow() throws LWJGLException{
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640,480));
        Display.setTitle("Program 2");
        
        Display.create();
        
    }
    
    //method: initGL
    //purpose: intitalize properties for GL
    public void initGL(){
        glClearColor(0f, 0f, 0f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        glOrtho(0, 640, 0, 480, 1, -1);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    //method: render
    //purpose: render drawings
    public void render(){
        while(!Display.isCloseRequested()&&!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            try{
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                
                glColor3f(1f, 0f, 0f);
                File file = new File("src/coordinates.txt");
                readPolygon(file);
                
                Display.update();
                Display.sync(60);
            }
            catch(Exception ex){
                
            }     
        }    
        Display.destroy();
    }
    
    public void readPolygon(File file){
        LinkedList<Edge> all_edge;
        try{
            Scanner readFile = new Scanner(file);
            String[] colors = new String[3];
            char state = 'p';
            ArrayList<Vertex> vertexList = new ArrayList<>();
            while(readFile.hasNextLine()){
                String readLine[] = readFile.nextLine().split(" ");
                
                if(state == 'p'){
                    if(readLine[0].equals( "P")){
                        colors = Arrays.copyOfRange(readLine, 1, 4);
                        continue;
                    }
                    else if(readLine[0].equals( "T")){
                        state = 't';
                    }
                    else{
                        vertexList.add(new Vertex(Float.parseFloat(readLine[0]), Float.parseFloat(readLine[1])));
                        continue;
                    }
                }
                
                all_edge = getAllEdgeTable(vertexList);
                //figure transformation
//                if(state == 't'){
//                    if(readLine[0] == "P"){
//                        state = 'p';
//                        //file in figure, then update color
//                    }
//                    else if(readLine[0] == "T"){
//                        continue;
//                    }
//                    
//                }
                //fill polygon
                fillPolygon(all_edge);
                }
        }
        catch(Exception e){
            
        }
 
    }
    
    public void fillPolygon(LinkedList<Edge> allEdges){
        LinkedList<Edge> global_edges;
        LinkedList<Edge> active_edges = new LinkedList<>();
        int parity = 0;
        global_edges = getGlobalEdges(allEdges);
        float scanLine = global_edges.getFirst().getMinYVertex().getY();
        while(global_edges.peekFirst().getMinYVertex().getY() == scanLine){
            active_edges.add(global_edges.removeFirst());
        }
        System.out.println("\nallEdge");
        System.out.println(allEdges);
        System.out.println();
        System.out.println("\nglobal");
        System.out.println(global_edges);
        System.out.println();
        System.out.println("\nactive");
        System.out.println(active_edges);
        
    }
    
    public LinkedList<Edge> getGlobalEdges(LinkedList<Edge> allEdges){
        LinkedList<Edge> globalEdges = new LinkedList<>();
        boolean keepGoing;
        if(allEdges.isEmpty()){
            return null;
        }
        ListIterator<Edge> allEdgesList = allEdges.listIterator();
        globalEdges.add(allEdgesList.next());
        while(allEdgesList.hasNext()){
            keepGoing = true;
            Edge curEdge = allEdgesList.next();
            if(curEdge.getSlope()==0){
                continue;
            }
            ListIterator<Edge> globalEdgesList = globalEdges.listIterator();
            while(globalEdgesList.hasNext()){
                Edge curGlobalEdge = globalEdgesList.next();
                if(curEdge.getMinYVertex().getY() <= curGlobalEdge.getMinYVertex().getY()){
                    keepGoing = false;
                    break;
                }
            }
            if(keepGoing == true){
                globalEdgesList.add(curEdge);
            }
            else{
                //compare X value
                Edge e = globalEdgesList.previous();
                if(curEdge.getMinYVertex().getY() == e.getMinYVertex().getY()){
                    float curEdgeX = curEdge.getMinYVertex().getX();
                    float gloEdgeX = e.getMinYVertex().getX();
                    if(curEdgeX == gloEdgeX){
                        //compare Y-max value
                        float curEdgeYMax = curEdge.getMaxYVertex().getY();
                        float gloEdgeYMax = e.getMaxYVertex().getY();
                        if(curEdgeYMax < gloEdgeYMax){
                           globalEdgesList.add(curEdge); 
                        }
                        else{
                            globalEdgesList.next();
                            globalEdgesList.add(curEdge);
                        }
                    }
                    else if (curEdgeX < gloEdgeX){
                        globalEdgesList.add(curEdge);
                    }
                    else{
                        globalEdgesList.next();
                        globalEdgesList.add(curEdge);
                    }
                    
                }
                else{
                    globalEdgesList.add(curEdge);
                }
            }
            
        }
        return globalEdges;  
    }
    
    public LinkedList<Edge> getAllEdgeTable(ArrayList<Vertex> vertexList){
        LinkedList<Edge> all_edge = new LinkedList<>();
        Vertex first = vertexList.get(0);
        for(int i = 0; i < vertexList.size(); i++){
            if(i == vertexList.size()-1){
                all_edge.add(new Edge(vertexList.get(i), first));
            }
            else {
                all_edge.add(new Edge(vertexList.get(i), vertexList.get(i+1)));
            }
        }
        return all_edge;
    }
    
    public void drawPolygon(ArrayList<Float> x, ArrayList<Float> y){
        for(int i = 0; i < x.size(); i++){
            if(i == x.size()-1){
                drawLine(x.get(i), y.get(i), x.get(0), y.get(0));
            }
            else{
                drawLine(x.get(i), y.get(i), x.get(i+1), y.get(i+1));
            }
        }
    }
    private void drawLine(float x1, float y1, float x2, float y2){
        
        //Declare and initialize dx, dy, d, incrementRight, incrementUpRight, x, and y
        float dx, dy, d, incrementRight, incrementUpRight, x, y, slope;
        
        //dx = x2 - x1
        dx = x2 - x1;
        //dy = y2 - y1
        dy = y2 - y1;
        slope = dy / dx;
        
        //x = x1
        x = x1;
        //y = y1
        y = y1;
        //draw first point
        draw(x,y);
        if(slope > -1 && slope <1 && dx!=0){ 

            //when slope is positive
            if(slope >0){
                //if x1 and y1 are on the left of x2 and y2
                if(dx >0 && dy >0){
                    //d = 2(dy)-dx
                    d = (float) (dy - 0.5 * dx);
                    incrementRight = getIncrementRightCol(dx, dy);
                    incrementUpRight = getIncrementUpRightCol(dx,dy);
                    while(x < x2){
                        if(d > 0){
                            //x+1, y+1
                            x = x+1;
                            y = y+1;
                            //draw new (x,y)
                            draw(x,y);
                              d = d + incrementUpRight;
                        }
                        else{
                            //x+1
                            x = x + 1;
                            //draw new (x,y)
                            draw(x,y);
                              d = d + incrementRight;
                        }
                    }
                }
                //if x1 and y1 are on the right of x2 and y2
                else if(dx < 0 && dy < 0){
                    //d = 0.5dx-dy
                    d = (float) (0.5*dx - dy);
                    dx = -dx;
                    dy = -dy;
                    incrementRight = getIncrementRightCol(dx, dy);
                    incrementUpRight = getIncrementUpRightCol(dx,dy);
                    while(x > x2){
                        if(d > 0){
                            //x+1, y+1
                            x = x-1;
                            y = y-1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = 2(dy-dx)+d(old)
//                            d = d - (dy - dx);
                              d = d + incrementUpRight;
                        }
                        else{
                            //x+1
                            x = x - 1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = d(old)+2dy
//                            d = d - dy;
                            d = d + incrementRight;
                        }
                    }
                }
            }
            //for slope is negative
            else if (slope < 0){
                //if x1 and y1 are on the right of x2 and y2
                if(dx < 0 && dy >0){
                    dy = -dy;
                    //d = 2(dy)-dx
                    d = (float) (0.5*dx - dy);
                    while(x > x2){
                        if(d > 0){
                            //x+1, y+1
                            x = x-1;
                            y = y+1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = 2(dy-dx)+d(old)
                            d = d - (dy - dx);
                        }
                        else{
                            //x+1
                            x = x - 1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = d(old)+2dy
                            d = d - dy;
                        }
                    }
                    
                }
                //if x1 and y1 are on the left of x2 and y2
                else if (dx > 0 && dy < 0){
                    dy = -dy;
                    d = (float) (dy - 0.5 * dx);
                    while(x < x2){
                        if(d > 0){
                            //x+1, y+1
                            x = x+1;
                            y = y-1;
                            //draw new (x,y)
                            draw(x,y);
                              d = d + dy - dx;
                        }
                        else{
                            //x+1
                            x = x + 1;
                            //draw new (x,y)
                            draw(x,y);
                              d = d + dy;
                        }
                    }
                }
                
            } 
            //for slope equals to 0. Horizontal line
            else if(slope == 0){
                if(dx>0){
                    while(x < x2){
                        x++;
                        draw(x,y);
                    }
                }
                else {
                    while(x > x2){
                        x--;
                        draw(x,y);
                    }
                }
            }
        }
        //for slope <= -1 or slope >= 1
        else if((slope <= -1 || slope >= 1)&& dx!=0){
            //d = (0.5 * dy)-dx
            d = (float) ((0.5 * dy) -dx);
            
            
            //for slope is positive and greater than or equal to 1
            if(slope > 0){
                //for x2 and y2 are greater than x1 and y1
                if(dy > 0 && dy > 0){
                    //d = (0.5 * dy)-dx
                    d = (float) ((0.5 * dy) -dx);
                    //incrementRight = 2*dy
                    incrementRight = getIncrementRightRow(dx, dy);
                    //incrementUpRight = 2*(dy-dx)
                    incrementUpRight =getIncrementUpRightRow(dx, dy);
                    while(y < y2){
                        if(d > 0){
                            //x+1, y+1
                            x = x+1;
                            y = y+1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = 2(dy-dx)+d(old)
                            d = incrementUpRight + d;
                        }
                        else{
                            //x+1
                            y = y + 1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = d(old)+2dy
                            d = d + incrementRight;
                        }
                    } 
                }
                //for x2 and y2 are less than x1 and y1
                else if (dy < 0 && dx < 0){
                    dy = -dy;
                    dx = -dx;
                    //incrementRight = 2*dy
                    incrementRight = getIncrementRightRow(dx, dy);
                    //incrementUpRight = 2*(dy-dx)
                    incrementUpRight =getIncrementUpRightRow(dx, dy);
                    while(y > y2){
                        if(d > 0){
                            //x+1, y+1
                            x = x-1;
                            y = y-1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = 2(dy-dx)+d(old)
                            d = incrementUpRight + d;
                        }
                        else{
                            //x+1
                            y = y - 1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = d(old)+2dy
                            d = d + incrementRight;
                        }
                    } 

                }
            }
            //for slope is negative and less than or equal to -1
            else if (slope < 0){
                //for y2 is greater y1 and x2 is less tha x1
                if(dy > 0 && dx < 0){
                    dx = -dx;
                    //incrementRight = 2*dy
                    incrementRight = getIncrementRightRow(dx, dy);
                    //incrementUpRight = 2*(dy-dx)
                    incrementUpRight =getIncrementUpRightRow(dx, dy);
                    while(y < y2){
                        if(d > 0){
                            //x+1, y+1
                            x = x - 1;
                            y = y + 1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = 2(dy-dx)+d(old)
//                            d = 2*(dx-dy) + d;
                            d = d + incrementUpRight;
                        }
                        else{
                            //x+1
                            y = y + 1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = d(old)+2dy
//                            d = d + 2 * dx; 
                            d = d + incrementRight;
                        }
                    } 

                }
                //when x2 is greater than x1 and y2 is less than y1
                else if (dx > 0 && dy < 0){
                    dy = -dy;
                    //incrementRight = 2*dy
                    incrementRight = getIncrementRightRow(dx, dy);
                    //incrementUpRight = 2*(dy-dx)
                    incrementUpRight =getIncrementUpRightRow(dx, dy);
                    while(y > y2){
                        if(d > 0){
                            //x+1, y+1
                            x = x+1;
                            y = y-1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = 2(dy-dx)+d(old)
//                            d = 2*(dx-dy) + d;
                            d = d + incrementUpRight;
                        }
                        else{
                            //x+1
                            y = y - 1;
                            //draw new (x,y)
                            draw(x,y);
                            //d = d(old)+2dy
//                            d = d + 2 * dx;
                            d = d + incrementRight;
                        }
                    } 

                }
            }
            
        }
        
        //if line is vertical
        else{
            if(dy > 0){
                while(y<y2){
                    draw(x,y);
                    y++;
                }
            }
            else{
                while(y>y2){
                    draw(x,y);
                    y--;
                }
            }
        }

    }
    
    //method: draw
    //purpose: draw point at given (x,y) coordinate
    private void draw(float x, float y){
        glBegin(GL_POINTS);
            glVertex2f(x,y);
        glEnd();
    }
    
     //method: getIncrementUpRightCol
    //purpose: get up right increment in a colume
    private float getIncrementUpRightCol(float dx, float dy){
        //incrementUpRight = 2*(dy-dx)
        return 2 * (dy - dx);
    }
    
    //method: getIncrementRightCol
    //purpose: get right increment in a colume
    private float getIncrementRightCol(float dx, float dy){
        //incrementRight = 2*dy
        return 2 * dy;
    }
    
    //method: getIncrementUpRightRow
    //purpose: get up right increment in a row
    private float getIncrementUpRightRow(float dx, float dy){
        //incrementUpRight = 2*(dy-dx)
        return 2 * (dx - dy);
    }
    
    //method: getIncrementUpRightRow
    //purpose: get right increment in a row
    private float getIncrementRightRow(float dx, float dy){
        //incrementRight = 2*dy
        return 2 * dx;
    }
    
   
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Program2 p2 = new Program2();
        p2.start();
//        System.out.println(p2.getAllEdgeTable());
//        
//        System.out.println();
//        System.out.println(p2.getGlobalEdgeTable());
    }

}
