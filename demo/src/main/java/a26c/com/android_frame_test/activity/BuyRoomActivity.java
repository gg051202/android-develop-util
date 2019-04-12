package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.a26c.android.frame.adapter.TextWatcherAdapter;
import com.a26c.android.frame.base.CommonActivity;

import java.util.ArrayList;
import java.util.List;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.util.BuyRoom;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BuyRoomActivity extends CommonActivity {


    public List<Integer> homePrices = new ArrayList<>();

    @BindView(R.id.priceEditText)
    EditText mPriceEditText;
    @BindView(R.id.descEditText)
    EditText mDescEditText;
    @BindView(R.id.submitTextView)
    Button mSubmitTextView;
    @BindView(R.id.resultTextView)
    TextView mResultTextView;
    private Unbinder mBind;


    @Override
    public int getContainLayout() {
        return R.layout.activity_buy_room;

    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBind = ButterKnife.bind(this);
        mPriceEditText.setText("150,160,170,180,190,200,210,220,230,240,250,260,270");
        mPriceEditText.setSelection(mPriceEditText.getText().toString().length());
        mDescEditText.setText("住宅：首付3成，公积金利率3.25%(贷款65万)，商贷利率4.9%（上浮5%），贷款年限30年");
        count();
    }

    @Override
    protected void setEvent() {
        mDescEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                String trim = mPriceEditText.getText().toString().trim();
                count();
            }
        });
        mDescEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                count();
            }
        });

    }

    @OnClick(R.id.submitTextView)
    public void onClick() {
        count();

    }

    private void count() {
        try {
            homePrices.clear();
            for (String s : mPriceEditText.getText().toString().split(",")) {
                homePrices.add(Integer.parseInt(s.trim()));
            }
            String start = new BuyRoom().start(mDescEditText.getText().toString(), homePrices);
            mResultTextView.setText(start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }
}
