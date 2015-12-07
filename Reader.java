import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Rezenter on 12/1/15.
 */
public class Reader {
    //final static String PATH = "./";
    final static String PATH = "./objects/";

    public static ArrayList<Obj> pointReader(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new File(PATH + filename + ".obj"));
        ArrayList<Vector3f> res = new ArrayList<Vector3f>();
        ArrayList<Vector3f> cons = new ArrayList<Vector3f>();
        String colour = "";
        String file = "";
        ArrayList<Obj> objects = new ArrayList<>();
        int count = -1;
        int num = 0;
        int currNum = 0;
        while (in.hasNextLine()) {
            String curr = in.nextLine();
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
        in = new Scanner(new File(PATH + file));
        String a = "";
        while (in.hasNextLine()) {
            String curr = in.nextLine();
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
        return objects;
    }
}
