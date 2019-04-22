package marketing;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() throws Exception {
        float p = 1.141666667f;

        for (int i = 0; i < 301; i++) {
            int i1 = i * 1;
            System.out.println(i1 + "->" + (int) (i1 / p));
            System.out.println("");
        }

    }
}