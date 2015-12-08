import java.util.ArrayList;

/**
 * Created by Rezenter on 11/11/15.
 */
public class Obj {
    private ArrayList<Vector3f> points;
    private ArrayList<Vector3f> connections;
    private String col;
    private Vector3f colour;



    public void set(ArrayList<Vector3f> p, ArrayList<Vector3f> c, String colour){
        points = (ArrayList<Vector3f>)p.clone();
        connections = (ArrayList<Vector3f>) c.clone();
        col = colour;
    }

    public void setCol(Vector3f c){
        colour = c;
    }

    public Obj(){
        points = new ArrayList<Vector3f>();
        connections = new ArrayList<Vector3f>();
    }

    public ArrayList<Triangle> triangulate(){
        ArrayList<Triangle> res = new ArrayList<Triangle>();
        for(Vector3f face: connections){
            int[] tmp = new int[3];
            for(int i = 0; i < 3; i ++){
                tmp[i] = (int)face.get()[i] - 1;
            }
            res.add(new Triangle(points.get(tmp[0]), points.get(tmp[1]), points.get(tmp[2]), colour));
        }
        return res;
    }

    public ArrayList<Vector3f> getPoints(){
        return points;
    }

    public String getCol(){
        return col;
    }
}
