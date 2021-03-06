package app.refactoring.com.refactoringretrofit.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import app.refactoring.com.refactoringretrofit.activity.SplashActivity;

public class ViewUtil {
    @SuppressLint("PrivateApi")
    public static void initSystemBar(Activity activity, int res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            setTranslucentStatus(activity, true);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            if (activity instanceof SplashActivity) {
                //去除半透明状态栏
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //一般配合fitsSystemWindows()使用, 或者在根部局加上属性android:fitsSystemWindows="true", 使根部局全屏显示
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                activity.getWindow().setStatusBarColor(res);
            }
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                //改为透明
                field.setInt(activity.getWindow().getDecorView(), Color.TRANSPARENT);
            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        //设置系统栏设置颜色
        tintManager.setTintColor(res);
        //给导航栏设置资源
        tintManager.setNavigationBarTintResource(res);
        tintManager.setStatusBarTintResource(res);


    }

    public static void setBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = activity.getWindow();
            View decorView = win.getDecorView();
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//沉浸式状态栏(4.4-5.0透明，5.0以上半透明)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//android5.0以上设置透明效果
                win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//清除flag，为了android5.0以上也全透明效果
                //让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | option);
                win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                win.setStatusBarColor(color);//设置状态栏背景色
            }
        }
    }

    /**
     * 设置屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public static void setBackgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();

        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 调整Picker布局
     *
     * @param frameLayout
     */
    public static void resizePicker(FrameLayout frameLayout) {
        List<NumberPicker> numberPickers = findNumberPicker(frameLayout);
        for (NumberPicker numberPicker : numberPickers) {
            resizeNumberPicker(numberPicker);
        }
    }

    /**
     * 获取ViewGroup中的NumberPicker组件
     *
     * @param viewGroup
     * @return
     */
    private static List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> numberPickers = new ArrayList<>();
        View child;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    numberPickers.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return numberPickers;
    }

    /**
     * 调整NumberPicker大小
     *
     * @param numberPicker
     */
    private static void resizeNumberPicker(NumberPicker numberPicker) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(15, 0, 15, 0);
        numberPicker.setLayoutParams(params);
    }

}
