package com.trianguloy.isUserAMonkey;


import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static java.util.Map.entry;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trianguloy.isUserAMonkey.tools.Animations;
import com.trianguloy.isUserAMonkey.tools.ClickableLinks;

import java.util.List;
import java.util.Map;

/**
 * The main, and only, activity
 */
public class MainActivity extends Activity implements ClickableLinks.OnUrlListener {

    // ------------------- data -------------------

    /**
     * tags to urls map
     */
    private static final Map<String, String> links = Map.ofEntries(
            entry("TrianguloY", "https://github.com/TrianguloY"),
            entry("GitHub", "https://github.com/TrianguloY/isUserAMonkey"),

            entry("isUserAMonkey", "https://developer.android.com/reference/android/app/ActivityManager.html#isUserAMonkey()"),
            entry("monkey", "https://developer.android.com/studio/test/monkey.html"),

            entry("isUserAGoat", "https://developer.android.com/reference/android/os/UserManager.html#isUserAGoat()"),
            entry("Goat Simulator", "https://play.google.com/store/apps/details?id=com.coffeestainstudios.goatsimulator"),

            entry("DISALLOW_FUN", "https://developer.android.com/reference/android/os/UserManager.html#DISALLOW_FUN"),

            entry("isTheFinalCountdown", "https://developer.android.com/reference/android/widget/Chronometer#isTheFinalCountDown()"),
            entry("The final countdown", "https://youtu.be/9jK-NcRmVcw"),
            entry("FLAG_ACTIVITY_LAUNCH_ADJACENT", "https://developer.android.com/reference/android/content/Intent#FLAG_ACTIVITY_LAUNCH_ADJACENT"),

            entry("FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND", "https://developer.android.com/reference/android/content/pm/PackageManager.html#FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND"),

            entry("wtf", "https://developer.android.com/reference/android/util/Log.html#wtf(java.lang.String,%20java.lang.String,%20java.lang.Throwable)"),

            entry("fyiWillBeAdvancedByHostKThx", "https://developer.android.com/reference/android/widget/AdapterViewFlipper.html#fyiWillBeAdvancedByHostKThx()"),

            entry("strange-function-in-activitymanager-isuseramonkey-what-does-this-mean-what-is", "https://stackoverflow.com/a/7792165"),
            entry("proper-use-cases-for-android-usermanager-isuseragoat", "https://stackoverflow.com/a/13375461"),
            entry("jokes-and-humour-in-the-public-android-api", "https://voxelmanip.se/2025/06/14/jokes-and-humour-in-the-public-android-api/"),
            entry("Android-Documentation-Easter-Eggs", "https://github.com/vitorOta/Android-Documentation-Easter-Eggs"),

            entry("Noto Emoji", "https://github.com/googlefonts/noto-emoji/blob/f2a4f72/svg/emoji_u1f412.svg")
    );

    /**
     * List of eggs
     */
    private static final List<Egg> eggs = List.of(
            new Egg("🐒", R.string.m_summary, R.string.m_explanation, R.string.m_function, MainActivity::runMonkey),
            new Egg("🐐", R.string.g_summary, R.string.g_explanation, R.string.g_function, MainActivity::runGoat),
            new Egg("🎉", R.string.f_summary, R.string.f_explanation, R.string.f_function, MainActivity::runDisallowFun),
            new Egg("⏱", R.string.c_summary, R.string.c_explanation, R.string.c_function, MainActivity::runChronoFinalCountdown),
            new Egg("🎺", R.string.j_summary, R.string.j_explanation, R.string.j_function, MainActivity::runJazzHands),
            new Egg("⁉", R.string.wtf_summary, R.string.wtf_explanation, R.string.wtf_function, MainActivity::runWTF),
            new Egg("ℹ", R.string.fyi_summary, R.string.fyi_explanation, R.string.fyi_function, MainActivity::runFYI)
    );

    record Egg(
            String btnText,
            int summaryRes,
            int explanationRes,
            int functionRes,
            Run runAction
    ) {
        interface Run {
            void run(MainActivity mainActivity);
        }
    }

    // ------------------- common -------------------
    private TextView txt_result;
    private TextView txt_comment;
    private Egg.Run runAction;

    // ------------------- initialization -------------------

    /**
     * App initialization
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get common views
        txt_result = this.findViewById(R.id.result);
        txt_comment = this.findViewById(R.id.comment);

        // get specific views
        var txt_explanation = this.<TextView>findViewById(R.id.explanation);
        var ll_buttons = this.<LinearLayout>findViewById(R.id.buttons);
        var txt_summary = this.<TextView>findViewById(R.id.summary);
        var txt_function = this.<TextView>findViewById(R.id.function);
        var txt_expand = this.<TextView>findViewById(R.id.expand);
        txt_expand.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            txt_explanation.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.run).setOnClickListener(v -> {
            // run
            runAction.run(this);
            // update
            ClickableLinks.linkify(txt_result, this);
            ClickableLinks.linkify(txt_comment, this);
        });

        // prepare about dialog
        findViewById(R.id.about).setOnClickListener(v -> {
            var about = getLayoutInflater().inflate(R.layout.about, null);
            ClickableLinks.linkify(about.findViewById(R.id.about), this);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setView(about)
                    .setNegativeButton(android.R.string.ok, null)
                    .show();
        });

        // configure eggs
        for (var egg : eggs) {
            var btn = new Button(this);
            btn.setText(egg.btnText);
            btn.setOnClickListener(v -> {
                // set egg specifics
                txt_summary.setText(egg.summaryRes());
                ClickableLinks.linkify(txt_summary, this);
                txt_explanation.setText(egg.explanationRes());
                ClickableLinks.linkify(txt_explanation, this);
                txt_function.setText(egg.functionRes());
                ClickableLinks.linkify(txt_function, this);
                txt_result.setText("");
                txt_comment.setText(R.string.press);
                runAction = egg.runAction;

                // set bigger button
                for (var i = 0; i < ll_buttons.getChildCount(); i++) {
                    ((Button) ll_buttons.getChildAt(i)).setTextSize(COMPLEX_UNIT_DIP, 15);
                }
                btn.setTextSize(COMPLEX_UNIT_DIP, 30);

                // reset state
                txt_explanation.setVisibility(View.GONE);
                txt_expand.setVisibility(View.VISIBLE);
            });
            ll_buttons.addView(btn);
        }

        // start with the first easter egg already loaded
        ll_buttons.getChildAt(0).performClick();

        // and an animation to indicate there are more elements
        var scr_parent = (HorizontalScrollView) ll_buttons.getParent();
        scr_parent.post(() -> {
            scr_parent.scrollTo(scr_parent.getWidth(), 0);
            scr_parent.postDelayed(() -> scr_parent.smoothScrollTo(0, 0), 500);
        });

        // init rest
        Animations.enable(findViewById(R.id.parent));
    }

    // ------------------- listeners -------------------

    /**
     * A link (in a textview) was clicked
     */
    @Override
    public void onLinkClick(String tag) {
        if (!links.containsKey(tag)) {
            // link not present, error
            var msg = "The entry '" + tag + "' wasn't present in the links hashmap";
            Log.d("NotFoundException", msg);
            // crash in development
            if (BuildConfig.DEBUG) throw new Resources.NotFoundException(msg);
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

    private void runMonkey() {
        try {
            var isUserAMonkey = ActivityManager.isUserAMonkey(); // <-- this!
            txt_result.setText(Boolean.toString(isUserAMonkey));
            txt_comment.setText(isUserAMonkey ? R.string.m_true : R.string.m_false);

        } catch (Throwable e) {
            txt_result.setText(R.string.crash);
            txt_comment.setText(R.string.m_crash);
            txt_comment.append("\n\n");
            txt_comment.append(e.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) // to suppress the wanted unavailable error
    private void runGoat() {
        try {
            var result = ((UserManager) getSystemService(Context.USER_SERVICE)).isUserAGoat(); // <-- this!
            txt_result.setText(Boolean.toString(result));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // should have crashed
                txt_comment.setText(R.string.g_jb);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // pre lollipop, always return false
                txt_comment.setText(result ? R.string.g_lollipop_true : R.string.g_lollipop_false);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                // post lollipop and pre R, checks for goat simulator
                txt_comment.setText(result ? R.string.g_true : R.string.g_false);
            } else {
                // post R, always return false
                txt_comment.setText(result ? R.string.g_r_true : R.string.g_r_false);
            }

        } catch (Throwable e) {
            txt_result.setText(R.string.crash);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // expected crash, function unavailable
                txt_comment.setText(R.string.g_unavailable);
            } else {
                // unexpected crash
                txt_comment.setText(R.string.g_crash);
            }
            txt_comment.append("\n\n");
            txt_comment.append(e.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.M) // to suppress the wanted unavailable error
    private void runDisallowFun() {
        try {
            var result = ((UserManager) getSystemService(Context.USER_SERVICE)).hasUserRestriction(UserManager.DISALLOW_FUN); // <-- this!
            txt_result.setText(Boolean.toString(result));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // constant should not be available
                txt_comment.setText(getString(R.string.f_m, getString(result ? R.string.f_true : R.string.f_false)));
            } else {
                // available
                txt_comment.setText(result ? R.string.f_true : R.string.f_false);
            }


        } catch (Throwable e) {
            txt_result.setText(R.string.crash);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // expected crash, function unavailable
                txt_comment.setText(R.string.f_unavailable);
            } else {
                // unexpected crash
                txt_comment.setText(R.string.f_crash);
            }
            txt_comment.append("\n\n");
            txt_comment.append(e.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    void runChronoFinalCountdown() {
        try {
            var result = new Chronometer(this).isTheFinalCountDown(); // <-- this!
            txt_result.setText(Boolean.toString(result));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                // constant should not be available
                txt_comment.setText(getString(R.string.c_available, getString(result ? R.string.f_true : R.string.f_false)));
            } else {
                // available
                txt_comment.setText(result ? R.string.c_true : R.string.c_false);
            }

        } catch (Throwable e) {
            txt_result.setText(R.string.crash);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                // expected crash, function unavailable
                txt_comment.setText(R.string.c_unavailable);
            } else {
                // unexpected crash
                txt_comment.setText(R.string.c_crash);
            }
            txt_comment.append("\n\n");
            txt_comment.append(e.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    void runJazzHands() {
        try {

            var result = getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND); // <-- this!
            txt_result.setText(Boolean.toString(result));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                // constant should not be available
                txt_comment.setText(getString(R.string.j_g, getString(result ? R.string.j_true : R.string.j_false)));
            } else {
                // available
                txt_comment.setText(result ? R.string.j_true : R.string.j_false);
            }

        } catch (Throwable e) {
            txt_result.setText(R.string.crash);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                // expected crash, function unavailable
                txt_comment.setText(R.string.j_unavailable);
            } else {
                // unexpected crash
                txt_comment.setText(R.string.j_crash);
            }

            txt_comment.append("\n\n");
            txt_comment.append(e.toString());
        }
    }

    void runWTF() {
        try {
            var result = Log.wtf("TAG", "msg"); // <-- this!

            txt_result.setText(Integer.toString(result));
            txt_comment.setText(result > 0 ? R.string.wtf_positive : R.string.wtf_nonpositive);

        } catch (Throwable e) {
            txt_comment.setText(R.string.wtf_crash);

            txt_comment.append("\n\n");
            txt_comment.append(e.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void runFYI() {
        try {

            new AdapterViewFlipper(this).fyiWillBeAdvancedByHostKThx(); // <-- this!
            txt_result.setText(R.string.no_value);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                // constant should not be available
                txt_comment.setText(getString(R.string.fyi_unexpected, getString(R.string.fyi_run)));
            } else {
                // available
                txt_comment.setText(R.string.fyi_run);
            }

        } catch (Throwable e) {
            txt_result.setText(R.string.crash);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                // expected crash, function unavailable
                txt_comment.setText(R.string.fyi_unavailable);
            } else {
                // unexpected crash
                txt_comment.setText(R.string.fyi_crash);
            }

            txt_comment.append("\n\n");
            txt_comment.append(e.toString());
        }
    }

}