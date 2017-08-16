package a26c.com.android_frame_test;

/**
 * Created by guilinlin on 2017/8/15 11:17.
 * email 973635949@qq.com
 */

public class DomainPriceData {

    private float price;
    private String time;

    public DomainPriceData(float price, String time) {
        this.price = price;
        this.time = time;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
