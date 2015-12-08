/**
 * Created by Rezenter on 11/11/15.
 */
public class Vector3f
        extends java.lang.Object
        implements java.io.Serializable{

        private float x;
        private float y;
        private float z;

        public Vector3f(){
                this.set(0, 0, 0);
        }

        public Vector3f(float x, float y, float z){
                this.set(x,y,z);
        }

        public  Vector3f(float[] t){
                if(t.length == 3){
                        set(t);
                }
        }

        public Vector3f(double x, double y, double z){
                this.set((float) x, (float) y, (float) z);
        }

        public Vector3f(Vector3f t){
                set(t.get());
        }

        public final String toString(){
                return "(" + x + "," + y + "," + z + ")";
        }

        public final void set(float x, float y, float z){
                this.x = x;
                this.y = y;
                this.z = z;
        }

        public final void set(float[] f){
                if(f.length == 3){
                        set(f[0], f[1], f[2]);
                }
        }

        public final void set(Vector3f t){
                set(t.get());
        }

        public final void get(float[] t){
                if(t.length == 3){
                        t[0] = x;
                        t[1] = y;
                        t[2] = z;
                }
        }

        public final float[] get(){
                float[] res = new float[3];
                get(res);
                return res;
        }

        public final void get(Vector3f t){
                t.set(get());
        }

        public final void add(Vector3f a, Vector3f b){
                set(a.get()[0] + b.get()[0], a.get()[1] + b.get()[1], a.get()[2] + b.get()[2]);
        }

        public final void add(Vector3f t){
                add(this, t);
        }

        public final void sub(Vector3f a, Vector3f b){
                Vector3f tmp = b;
                tmp.negate();
                add(a, tmp);
        }

        public final void sub(Vector3f t){
                sub(this, t);
        }

        public final void negate(){
                negate(this);
        }

        public final void negate(Vector3f t){
                set(-t.get()[0], -t.get()[1], -t.get()[2]);
        }

        public final void scale(float s, Vector3f t){
                set(t.get()[0]*s, t.get()[1]*s, t.get()[2]*s);
        }

        public final void scale(float s){
                scale(s, this);
        }

        public final boolean equals(Vector3f t){
                if(t.get().equals(this.get())){
                        return true;
                }else{
                        return false;
                }
        }

        public  final boolean epsilonEquals(Vector3f t, float epsilon){
                if (Math.max(Math.abs(t.get()[0] - get()[0]),
                        Math.max(Math.abs(t.get()[1] - get()[1]), Math.abs(t.get()[2] - get()[2])))
                        <= epsilon) {
                        return true;
                } else {
                        return false;
                }
        }

        public final void clamp(float min, float max, Vector3f t){
                Vector3f tmp = t;
                tmp.clamp(min, max);
                set(tmp);
        }

        public final void clamp(float min, float max){
                clampMin(min);
                clampMax(max);
        }

        public final void clampMin(float min){
                clampMin(min, this);
        }

        public final void clampMax(float max){
                clampMax(max, this);
        }

        public final void clampMin(float min, Vector3f t){
                float[] tmp = t.get();
                for(int i = 0; i < 3; i++){
                        if(tmp[i] < min){
                                tmp[i] = min;
                        }
                }
                set(tmp);
        }

        public final void clampMax(float max, Vector3f t){
                float[] tmp = t.get();
                for(int i = 0; i < 3; i++){
                        if(tmp[i] > max){
                                tmp[i] = max;
                        }
                }
                set(tmp);
        }


        public final void absolute(Vector3f t){
                float[] tmp = t.get();
                for(int i = 0; i < 3; i++){
                        tmp[i] = Math.abs(tmp[i]);
                }
                set(tmp);
        }

        public final void absolute(){
                absolute(this);
        }

        public final void interpolate(Vector3f a, Vector3f b, float alpha){
                Vector3f tmp = b;
                this.set(a);
                this.scale(1 - alpha);
                scale(alpha, tmp);
                add(this, tmp);
        }

        public final void interpolate(Vector3f t, float alpha){
                interpolate(this, t, alpha);
        }

        public final float dot(Vector3f t){
                return get()[0]*t.get()[0] + get()[1]*t.get()[1] + get()[2]*t.get()[2];
        }

        public final float angle(Vector3f t){
                this.normalize();
                Vector3f v = t;
                v.normalize();
                return (float) Math.acos(dot(v));
        }

        public final float length(){
                return lengthSquared()*lengthSquared();
        }

        public final float lengthSquared(){
                return (float) Math.sqrt(Math.pow(this.get()[0], 2) + Math.pow(this.get()[1], 2)
                        + Math.pow(this.get()[2], 2));
        }

        public final void normalize(){
                normalize(this);
        }

        public final void normalize(Vector3f t){
                scale(1/length(), t);
        }

        public final void mult(Vector3f a, Vector3f b, Vector3f c){
                float[] tmp = new float[3];
                for(int i = 0; i < 3; i++){
                        tmp[i] = (a.get()[i]*get()[0] + b.get()[i]*get()[1] + c.get()[i]*get()[2]);
                }
                set(tmp);
        }
}