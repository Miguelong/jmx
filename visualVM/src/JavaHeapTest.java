/**
 * Created by miguel on 09/04/2018.
 */
public class JavaHeapTest {
    public final static int OUTOFMEMORY = 300;

    private String oom;

    private int length;

    StringBuffer tempOOM = new StringBuffer();

    public JavaHeapTest(int leng) {
        this.length = leng;

        int i = 0;
        while (i < leng) {
            i++;

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                tempOOM.append("a");
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                break;
            }
        }
        this.oom = tempOOM.toString();

    }


    public JavaHeapTest() {
        while(true){
            try {
                Thread.sleep(1);
                new String("hello");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public String getOom() {
        return oom;
    }

    public int getLength() {
        return length;
    }

    public static void main(String[] args) {
        //JavaHeapTest javaHeapTest = new JavaHeapTest(OUTOFMEMORY);
        JavaHeapTest javaHeapTest = new JavaHeapTest();
        //System.out.println(javaHeapTest.getOom().length());
    }
}
