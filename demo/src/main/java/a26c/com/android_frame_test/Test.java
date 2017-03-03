package a26c.com.android_frame_test;

import com.a26c.android.frame.util.CommonUtils;

/**
 * Created by guilinlin on 2016/12/14 08:54.
 * email 973635949@qq.com
 */
public class Test {

    public static void main(String[] args){

        System.out.println(CommonUtils.getHhMmSs(1481676652));
        System.out.println(CommonUtils.getYyyyMmDd(1481676652));
        System.out.println(CommonUtils.getYyyyMmDdHhMm(1481676652));
        System.out.println(CommonUtils.getYyyyMmDdHhMmSs(1481676652));
        System.out.println(CommonUtils.getOnePointNumber(12.23131));
        System.out.println(CommonUtils.getTwoPointNumber(12f));
        System.out.println();
    }

}
