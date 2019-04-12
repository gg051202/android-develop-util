package a26c.com.android_frame_test.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by guilinlin on 2018/10/15 15:52.
 * email 973635949@qq.com
 */

public class BuyRoom {

    /**
     * 公积金利率
     */
    public double 公积金利率;
    /**
     * 商贷上浮
     */
    private float 商贷上浮;
    /**
     * 贷款年限
     */
    public int 贷款年限;
    /**
     * 公积金贷款金额
     */
    public int 公积金贷款金额;
    /**
     * 商贷利率
     */
    public float 商贷利率;
    /**
     * 首付比例
     */
    public float 首付几成;

    public String start(String content, List<Integer> homePrices) {
        公积金利率 = Double.parseDouble(content.substring(content.indexOf("公积金利率") + 5, content.indexOf("%(贷款")));
        商贷上浮 = Float.parseFloat(content.substring(content.indexOf("（上浮") + 3, content.indexOf("%），贷款")));
        商贷利率 = 0.049f * (1 + 商贷上浮 / 100f);
        贷款年限 = Integer.parseInt(content.substring(content.indexOf("贷款年限") + 4, content.length() - 1));
        公积金贷款金额 = Integer.parseInt(content.substring(content.indexOf("(贷款") + 3, content.indexOf("万)，商"))) * 10000;
        首付几成 = Float.parseFloat(content.substring(content.indexOf("首付") + 2, content.indexOf("成，公积金利"))) / 10;

        System.out.println(String.format("首付%s成，公积金利率%s%%(贷款%s万)，商贷利率%s%%（上浮%s%%），贷款年限%s年\n",
                (int) (首付几成 * 10), 公积金利率, 公积金贷款金额, (商贷利率 * 100), (int) 商贷上浮, 贷款年限));

        StringBuilder sb = new StringBuilder();
        //等额本息
        for (int i : homePrices) {
            //先算商贷
            float needLocanBuiness = i * 10000 * (1 - 首付几成) - 公积金贷款金额;
            float monthRateBusiness = 商贷利率 / 12;
            double moneyBusiness = needLocanBuiness * monthRateBusiness * (Math.pow(1 + monthRateBusiness, 贷款年限 * 12)) / (Math.pow(1 + monthRateBusiness, 贷款年限 * 12) - 1);
            //公积金
            double monthRateAccumulation = 公积金利率 / 100 / 12;
            double moneyAccumulation = 公积金贷款金额 * monthRateAccumulation * (Math.pow(1 + monthRateAccumulation, 贷款年限 * 12)) / (Math.pow(1 + monthRateAccumulation, 贷款年限 * 12) - 1);

            double total = moneyBusiness + moneyAccumulation;
            double lixi = total * 贷款年限 * 12 / 10000 - i * 首付几成;
            BigDecimal 首付款 = new BigDecimal(i * 首付几成).setScale(1, RoundingMode.HALF_EVEN);
            sb.append(String.format("房价%s万，首付%s万，月还贷%s，总还款%s万\n", i, 首付款, (int) total, (int) lixi));

        }
        return sb.toString();

    }

}
