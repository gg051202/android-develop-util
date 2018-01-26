package a26c.com.android_frame_test.model;

/**
 * Created by guilinlin on 2018/1/26 10:48.
 * email 973635949@qq.com
 */

public class UserData {

    private String firstName;
    private int age;
    private int sex;
    private boolean isBoy = false;
    private String aihao="123aaa";

    public UserData(String firstName, int age, int sex) {
        this.firstName = firstName;
        this.age = age;
        this.sex = sex;
    }

    public UserData(String firstName) {
        this.firstName = firstName;
    }

    public String getAihao() {
        return aihao;
    }

    public void setAihao(String aihao) {
        this.aihao = aihao;
    }

    public boolean isBoy() {
        return isBoy;
    }

    public void setBoy(boolean boy) {
        isBoy = boy;
    }

    public int getSex() {
        return sex;
    }


    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
