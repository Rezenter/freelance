/**
 * Created by Rezenter on 11/9/15.
 */
public class Vector2f
        extends java.lang.Object
        implements java.io.Serializable{

        private float x;
        private float y;

        public Vector2f() {
                set(0, 0);
        }

        public Vector2f(float x, float y) {
                set(x, y);
        }

        public Vector2f(float[] c) {
                if (c.length == 2) {
                        set(c[0],c[1]);
                }
        }

        public Vector2f(Vector2f t){
                set(t.get());
        }

        public final void absolute() {
                absolute(this);
        }

        public final void absolute(Vector2f t) {
                set(Math.abs(t.get()[0]), Math.abs(t.get()[0]));
        }

        public final void add(Vector2f t) {
                add(this, t);
        }

        public final void add(Vector2f a, Vector2f b) {
                set(a.get()[0] + b.get()[0], a.get()[1] + b.get()[1]);
        }

        public final void sub(Vector2f a, Vector2f b){
                Vector2f tmp = b;
                tmp.negate();
                add(a, tmp);
        }

        public final void clamp(float min, float max) {
                clampMin(min);
                clampMax(max);
        }

        public final void clamp(float min, float max, Vector2f t) {
                Vector2f tmp = t;
                tmp.clamp(min, max);
                set(tmp);
        }

        public final void clampMin(float min) {
                set(Math.min(x, min), Math.min(y, min));
        }

        public final void clampMin(float min, Vector2f t) {
                clamp(min, Math.max(t.get()[0], t.get()[1]), t);
        }

        public final void clmpMax(float max, Vector2f t) {
                clamp(Math.min(t.get()[0], t.get()[1]), max, t);
        }

        public final void clampMax(float max) {
                set(Math.max(x, max), Math.max(y, max));
        }

        public final float[] get() {
                float[] res = new float[2];
                res[0] = x;
                res[1] = y;
                return res;
        }

        public final boolean epsilonEquals(Vector2f t, float epsilon) {
                if (Math.max(Math.abs(t.get()[0] - x), Math.abs(t.get()[1] - y)) <= epsilon) {
                        return true;
                } else {
                        return false;
                }
        }

        public final boolean equals(Vector2f t){
                if(x == t.get()[0] && y == t.get()[1]){
                        return true;
                }else{
                        return false;
                }
        }

        public final String toString(){
                return "(" + x + "," + y + ")";
        }

        public final void negate(){
                negate(this);
        }

        public final void negate(Vector2f t){
                set(-t.get()[0], -t.get()[1]);
        }

        public final void scale(float s, Vector2f t){
                set(t.get()[0]*s, t.get()[1]*s);
        }

        public final void scale(float s){
                scale(s, this);
        }

        public final void set(float x, float y){
                this.x = x;
                this.y = y;
        }

        public final void set(float[] t){
                if(t.length == 2){
                        set(t[0], t[1]);
                }
        }

        public final void set(Vector2f t){
                set(t.get()[0], t.get()[1]);
        }

        public final void interpolate(Vector2f tF, Vector2f tS, float alpha) {
                set((1-alpha)*tF.get()[0] + alpha*tS.get()[0], (1-alpha)*tF.get()[1] + alpha*tS.get()[1]);
        }

        public void interpolate(Vector2f t, float alpha){
                interpolate(this, t, alpha);
        }

        public final float dot(Vector2f t){
                return get()[0]*t.get()[0] + get()[1]*t.get()[1];
        }

        public final float angle(Vector2f t){
                this.normalize();
                Vector2f v = t;
                v.normalize();
                return (float) Math.acos(dot(v));
        }

        public final float length(){
                return lengthSquared()*lengthSquared();
        }

        public final float lengthSquared(){
                return (float) Math.sqrt(Math.pow(this.get()[0], 2) + Math.pow(this.get()[1], 2));
        }

        public final void normalize(){
                normalize(this);
        }

        public final void normalize(Vector2f t){
                scale(1/length(), t);
        }
}