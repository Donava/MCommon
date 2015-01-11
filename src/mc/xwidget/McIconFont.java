package mc.xwidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * IconView use iconfont
 */
public class McIconFont extends TextView {
  private static Typeface iconFont;

  public McIconFont(Context context) {
    super(context);
    init(context);
  }

  public McIconFont(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public McIconFont(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public McIconFont(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context);
  }

  private void init(Context context) {
    setIncludeFontPadding(false);

    if (isInEditMode()) {
      return;
    }

    if (iconFont != null) {
      setTypeface(iconFont);
      return;
    }

    iconFont = Typeface.createFromAsset(context.getAssets(), "fonts/mcIconFont.ttf");
  }
}
