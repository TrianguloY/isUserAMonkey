package com.trianguloy.isUserAMonkey;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.trianguloy.isUserAMonkey.tools.Animations;
import com.trianguloy.isUserAMonkey.tools.ClickableLinks;

import java.util.HashMap;

/**
 * The main, and only, activity
 */
public class MainActivity extends Activity implements ClickableLinks.OnUrlListener {

    // ------------------- data -------------------

    /**
     * tags to urls map
     */
    private static final HashMap<String, String> links = new HashMap<>();

    static {
        links.put("TrianguloY", "https://github.com/TrianguloY");
        links.put("GitHub", "https://github.com/TrianguloY/isUserAMonkey");

        links.put("isUserAMonkey", "https://developer.android.com/reference/android/app/ActivityManager.html#isUserAMonkey()");
        links.put("monkey", "https://developer.android.com/studio/test/monkey.html");

        links.put("isUserAGoat", "https://developer.android.com/reference/android/os/UserManager.html#isUserAGoat()");
        links.put("Goat Simulator", "https://play.google.com/store/apps/details?id=com.coffeestainstudios.goatsimulator");

        links.put("DISALLOW_FUN", "https://developer.android.com/reference/android/os/UserManager.html#DISALLOW_FUN");

        links.put("strange-function-in-activitymanager-isuseramonkey-what-does-this-mean-what-is", "https://stackoverflow.com/a/7792165");
        links.put("proper-use-cases-for-android-usermanager-isuseragoat", "https://stackoverflow.com/a/13375461");
        links.put("Android-Documentation-Easter-Eggs", "https://github.com/vitorOta/Android-Documentation-Easter-Eggs");
        links.put("Noto Emoji", "https://github.com/googlefonts/noto-emoji/blob/f2a4f72/svg/emoji_u1f412.svg");
    }

    // ------------------- initialization -------------------

    /**
     * App initialization
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewGroup parent = findViewById(R.id.parent);

        // init animations
        Animations.enable(parent);

        // init links
        ClickableLinks.linkifyAll(parent, this);
    }

    // ------------------- listeners -------------------

    /**
     * A button was clicked
     */
    public void onButtonClick(View view) {
        switch (view.getId()) {
            // run the isUserAMonkey function
            case R.id.m_run -> runMonkey();
            // run the isUserAGoat function
            case R.id.g_run -> runGoat();
            // run the DISALLOW_FUN function
            case R.id.f_run -> runDisallowFun();

            // show details for the isUserAMonkey function
            case R.id.m_expand -> swap(view, R.id.m_details);
            // show details for the isUserAGoat function
            case R.id.g_expand -> swap(view, R.id.g_details);
            // show details for the DISALLOW FUN function
            case R.id.f_expand -> swap(view, R.id.f_details);
        }
    }

    /**
     * A link (in a textview) was clicked
     */
    @Override
    public void onLinkClick(String tag) {
        if (!links.containsKey(tag)) {
            // link not present, error
            String msg = "The entry '" + tag + "' wasn't present in the links hashmap";
            Log.d("NotFoundException", msg);
            if (BuildConfig.DEBUG) // crash in development
                throw new Resources.NotFoundException(msg);
            return; // ignore in production
        }

        try {
            // open
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(links.get(tag))));
        } catch (Exception e) {
            // can't open
            Toast.makeText(this, R.string.no_browser, Toast.LENGTH_LONG).show();
        }
    }

    // ------------------- functions -------------------

    /**
     * Just a (hopefully) inlined function to hide one view and show another
     *
     * @param hide view to hide
     * @param show identifier of view to show
     */
    private void swap(View hide, int show) {
        hide.setVisibility(View.GONE);
        findViewById(show).setVisibility(View.VISIBLE);
    }

    /**
     * Run the isUserAMonkey function, and update the ui accordingly
     */
    private void runMonkey() {

        // views
        TextView result = this.findViewById(R.id.m_result);
        TextView comment = this.findViewById(R.id.m_comment);

        try {

            // run
            boolean isUserAMonkey = ActivityManager.isUserAMonkey(); // <-- This!

            // result
            result.setText(Boolean.toString(isUserAMonkey));

            // comment
            if (isUserAMonkey) {
                comment.setText(R.string.m_true);
            } else {
                comment.setText(R.string.m_false);
            }

        } catch (Throwable e) {

            // unavailable (unexpected)
            result.setText(R.string.unavailable);
            comment.setText(R.string.m_crash);
            comment.append("\n\n");
            comment.append(e.toString());
        }

        // update
        ClickableLinks.linkify(result, this);
        ClickableLinks.linkify(comment, this);
    }

    /**
     * Run the isUserAGoat function, and update the ui accordingly
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) // to suppress the wanted unavailable error
    private void runGoat() {

        // views
        TextView result = this.findViewById(R.id.g_result);
        TextView comment = this.findViewById(R.id.g_comment);

        try {

            // run
            boolean isUserAGoat = ((UserManager) getSystemService(Context.USER_SERVICE)).isUserAGoat(); // <-- This!

            // result
            result.setText(Boolean.toString(isUserAGoat));

            // comment
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // pre lollipop, always return false
                if (isUserAGoat) {
                    comment.setText(R.string.g_lollipop_true);
                } else {
                    comment.setText(R.string.g_lollipop_false);
                }
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                // post lollipop and pre R, checks for goat simulator
                if (isUserAGoat) {
                    comment.setText(R.string.g_true);
                } else {
                    comment.setText(R.string.g_false);
                }
            } else {
                // post R, always return false
                if (isUserAGoat) {
                    comment.setText(R.string.g_r_true);
                } else {
                    comment.setText(R.string.g_r_false);
                }
            }

        } catch (Throwable e) {
            // unavailable

            // result
            result.setText(R.string.unavailable);

            // comment
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // pre jelly bean, expected crash, function unavailable
                comment.setText(R.string.g_unavailable);
            } else {
                // unexpected crash
                comment.setText(R.string.g_crash);
            }
            comment.append("\n\n");
            comment.append(e.toString());
        }

        // update
        ClickableLinks.linkify(result, this);
        ClickableLinks.linkify(comment, this);

    }

    @TargetApi(Build.VERSION_CODES.M) // to suppress the wanted unavailable error
    private void runDisallowFun() {

        // views
        TextView result = this.findViewById(R.id.f_result);
        TextView comment = this.findViewById(R.id.f_comment);

        try {

            // run
            var disallowFun = ((UserManager) getSystemService(Context.USER_SERVICE)).hasUserRestriction(UserManager.DISALLOW_FUN); // <-- This!

            // result
            result.setText(Boolean.toString(disallowFun));

            // comment
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // constant should not be available
                if (disallowFun) {
                    // disallowed
                    comment.setText(getString(R.string.f_m, getString(R.string.f_true)));
                } else {
                    // allowed
                    comment.setText(getString(R.string.f_m, getString(R.string.f_false)));
                }
            } else {
                // available
                if (disallowFun) {
                    // disallowed
                    comment.setText(R.string.f_true);
                } else {
                    // allowed
                    comment.setText(R.string.f_false);
                }
            }


        } catch (Throwable e) {
            // unavailable

            // result
            result.setText(R.string.unavailable);

            // comment
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // pre Marshmallow, expected crash, function unavailable
                comment.setText(R.string.f_unavailable);
            } else {
                // unexpected crash
                comment.setText(R.string.f_crash);
            }
            comment.append("\n\n");
            comment.append(e.toString());
        }

        // update
        ClickableLinks.linkify(result, this);
        ClickableLinks.linkify(comment, this);
    }

}