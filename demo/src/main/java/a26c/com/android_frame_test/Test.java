package a26c.com.android_frame_test;

/**
 * Created by guilinlin on 2016/12/14 08:54.
 * email 973635949@qq.com
 */
public class Test {

    public static void main(String[] args) {
        float p = 1.141666667f;

        for (int i = 0; i < 301; i++) {
            int i1 = i * 1;
            System.out.println(i1 + "->" + (int) (i1 / p));
            System.out.println("");
        }

    }

}
