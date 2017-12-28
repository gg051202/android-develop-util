package a26c.com.android_frame_test.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by guilinlin on 2017/6/21 10:28.
 * email 973635949@qq.com
 */

public class UrlLinkTextView extends android.support.v7.widget.AppCompatTextView {
    private static final Pattern urlPattern = Pattern.compile(
            "((https?|ftp|gopher|telnet|file):((//)|(\\\\\\\\))+[\\\\w\\\\d:#@%/;$()~_?\\\\+-=\\\\\\\\\\\\.&]*)",
            Pattern.CASE_INSENSITIVE  );

    private Context context;

    public UrlLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明
    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Matcher matcher = urlPattern.matcher(text);
        SpannableString ss = new SpannableString(text);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end();
            ss.setSpan(new UrlLinkSpan(context, text.substring(start, end)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(ss);
    }

    private static class UrlLinkSpan extends ClickableSpan {

        private Context context;
        private String url;

        public UrlLinkSpan(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public void updateDrawState(TextPaint text) {
            super.updateDrawState(text);
            text.setColor(0xffff0000);
            text.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            if (url.startsWith("www")) {
                url = "http://" + url;
            }
            Toast.makeText(context, url, Toast.LENGTH_LONG).show();
        }
    }

}
