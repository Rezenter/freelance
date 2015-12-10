import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Rezenter on 12/1/15.
 */
public class Reader {
    final static String PATH = "/objects/";
    static String name;
    static ArrayList<Vector3f> res;
    static ArrayList<Vector3f> cons;
    static String colour;
    static String file;
    static ArrayList<Obj> objects;
    static int count;
    static int num;
    static int currNum;

    public Reader(String nnn) throws IOException {
        res = new ArrayList<Vector3f>();
        cons = new ArrayList<Vector3f>();
        colour = "";
        file = "";
        count = -1;
        num = 0;
        currNum = 0;
        objects = new ArrayList<>();
        name = nnn;
        BufferedReader in =
            new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(PATH + name +".obj")));
        while (in.ready()) {
            String curr = in.readLine();
            if (curr.length() > 1) {
                if (curr.charAt(0) == 'v' && curr.charAt(1) == ' ') {
                    String[] split = curr.split(" ");
                    float[] tmp = new float[3];
                    for (int i = 1; i < 4; i++) {
                        tmp[i - 1] = Float.parseFloat(split[i + 1]);
                    }
                    currNum++;
                    res.add(new Vector3f(tmp));
                } else {
                    if (curr.charAt(0) == 'f') {
                        String[] split = curr.split(" ");

                        float[] tmp = new float[3];
                        for (int i = 1; i < 4; i++) {
                            tmp[i - 1] = Float.parseFloat(split[i]) - num;
                        }
                        cons.add(new Vector3f(tmp));
                    } else {
                        if (curr.charAt(0) == 'm') {
                            String[] split = curr.split(" ");
                            file = split[1];
                        } else {
                            if (curr.charAt(0) == '#' && curr.charAt(1) == ' ' && curr.charAt(2) == 'o') {
                                if (count != -1) {
                                    objects.get(count).set(res, cons, colour);
                                }
                                count++;
                                cons.clear();
                                res.clear();
                                num = currNum;
                                objects.add(new Obj());
                            }
                            if (curr.charAt(0) == 'u') {
                                String[] split = curr.split(" ");
                                colour = split[1];
                            }
                        }
                    }
                }
            }
        }
        objects.get(count).set(res, cons, colour);
        in.close();
        in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(PATH + file)));
        String a = "";
        while (in.ready()) {
            String curr = in.readLine();
            if (curr.length() > 1) {
                if (curr.charAt(0) == 'n' && curr.charAt(1) == 'e') {
                        String[] split = curr.split(" ");
                        a = split[1];
                } else {
                    if (curr.charAt(0) == '\t' && curr.charAt(1) == 'K' && curr.charAt(2) == 'd') {
                        String[] cols = curr.split(" ");
                        for (Obj t : objects) {
                            if (t.getCol().equals(a)) {
                                t.setCol(new Vector3f(Float.parseFloat(cols[1]), Float.parseFloat(cols[2]),
                                        Float.parseFloat(cols[3])));
                            }
                        }
                    }
                }
            }
        }
        in.close();
    }

    public static ArrayList<Obj> pointReader(){
        return objects;
    }
}
