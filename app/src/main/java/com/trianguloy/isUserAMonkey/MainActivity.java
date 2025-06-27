package com.trianguloy.isUserAMonkey;


import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static java.util.Map.entry;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
            entry("strange-function-in-activitymanager-isuseramonkey-what-does-this-mean-what-is", "https://stackoverflow.com/a/7792165"),
            entry("proper-use-cases-for-android-usermanager-isuseragoat", "https://stackoverflow.com/a/13375461"),
            entry("Android-Documentation-Easter-Eggs", "https://github.com/vitorOta/Android-Documentation-Easter-Eggs"),
            entry("Noto Emoji", "https://github.com/googlefonts/noto-emoji/blob/f2a4f72/svg/emoji_u1f412.svg")
    );

    /**
     * List of eggs
     */
    private static final List<Egg> eggs = List.of(
            new Egg("🐒", R.string.m_summary, R.string.m_explanation, R.string.m_function, MainActivity::runMonkey),
            new Egg("🐐", R.string.g_summary, R.string.g_explanation, R.string.g_function, MainActivity::runGoat),
            new Egg("🎉", R.string.f_summary, R.string.f_explanation, R.string.f_function, MainActivity::runDisallowFun)
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

    private TextView txt_explanation;
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
        txt_explanation = this.findViewById(R.id.explanation);
        txt_result = this.findViewById(R.id.result);
        txt_comment = this.findViewById(R.id.comment);

        // get specific views
        var ll_buttons = this.<LinearLayout>findViewById(R.id.buttons);
        var txt_summary = this.<TextView>findViewById(R.id.summary);
        var txt_function = this.<TextView>findViewById(R.id.function);
        var txt_expand = this.<TextView>findViewById(R.id.expand);
        txt_expand.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            txt_explanation.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.run).setOnClickListener(v -> runAction.run(this));

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

            // run
            var isUserAMonkey = ActivityManager.isUserAMonkey(); // <-- This!

            // result
            txt_result.setText(Boolean.toString(isUserAMonkey));

            // comment
            if (isUserAMonkey) {
                txt_comment.setText(R.string.m_true);
            } else {
                txt_comment.setText(R.string.m_false);
            }

        } catch (Throwable e) {

            // unavailable (unexpected)
            txt_result.setText(R.string.unavailable);
            txt_comment.setText(R.string.m_crash);
            txt_comment.append("\n\n");
            txt_comment.append(e.toString());
        }

        // update
        ClickableLinks.linkify(txt_result, this);
        ClickableLinks.linkify(txt_comment, this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) // to suppress the wanted unavailable error
    private void runGoat() {
        try {

            // run
            boolean isUserAGoat = ((UserManager) getSystemService(Context.USER_SERVICE)).isUserAGoat(); // <-- This!

            // result
            txt_result.setText(Boolean.toString(isUserAGoat));

            // comment
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // should have crashed
                txt_comment.setText(R.string.g_jb);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // pre lollipop, always return false
                if (isUserAGoat) {
                    txt_comment.setText(R.string.g_lollipop_true);
                } else {
                    txt_comment.setText(R.string.g_lollipop_false);
                }
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                // post lollipop and pre R, checks for goat simulator
                if (isUserAGoat) {
                    txt_comment.setText(R.string.g_true);
                } else {
                    txt_comment.setText(R.string.g_false);
                }
            } else {
                // post R, always return false
                if (isUserAGoat) {
                    txt_comment.setText(R.string.g_r_true);
                } else {
                    txt_comment.setText(R.string.g_r_false);
                }
            }

        } catch (Throwable e) {
            // unavailable

            // result
            txt_result.setText(R.string.unavailable);

            // comment
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // pre jelly bean, expected crash, function unavailable
                txt_comment.setText(R.string.g_unavailable);
            } else {
                // unexpected crash
                txt_comment.setText(R.string.g_crash);
            }
            txt_comment.append("\n\n");
            txt_comment.append(e.toString());
        }

        // update
        ClickableLinks.linkify(txt_result, this);
        ClickableLinks.linkify(txt_comment, this);
    }

    @TargetApi(Build.VERSION_CODES.M) // to suppress the wanted unavailable error
    private void runDisallowFun() {
        try {

            // run
            var disallowFun = ((UserManager) getSystemService(Context.USER_SERVICE)).hasUserRestriction(UserManager.DISALLOW_FUN); // <-- This!

            // result
            txt_result.setText(Boolean.toString(disallowFun));

            // comment
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // constant should not be available
                if (disallowFun) {
                    // disallowed
                    txt_comment.setText(getString(R.string.f_m, getString(R.string.f_true)));
                } else {
                    // allowed
                    txt_comment.setText(getString(R.string.f_m, getString(R.string.f_false)));
                }
            } else {
                // available
                if (disallowFun) {
                    // disallowed
                    txt_comment.setText(R.string.f_true);
                } else {
                    // allowed
                    txt_comment.setText(R.string.f_false);
                }
            }


        } catch (Throwable e) {
            // unavailable

            // result
            txt_result.setText(R.string.unavailable);

            // comment
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // pre Marshmallow, expected crash, function unavailable
                txt_comment.setText(R.string.f_unavailable);
            } else {
                // unexpected crash
                txt_comment.setText(R.string.f_crash);
            }
            txt_comment.append("\n\n");
            txt_comment.append(e.toString());
        }

        // update
        ClickableLinks.linkify(txt_result, this);
        ClickableLinks.linkify(txt_comment, this);
    }

}