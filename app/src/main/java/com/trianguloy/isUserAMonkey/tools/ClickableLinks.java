package com.trianguloy.isUserAMonkey.tools;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container for {@link #linkify(TextView, OnUrlListener)}
 */
public class ClickableLinks {

    /**
     * Listener for when a link is clicked. The link's tag specify which link was clicked.
     */
    public interface OnUrlListener {
        void onLinkClick(String tag);
    }

    /**
     * Converts a TextView with custom tags into clickable links to use in a custom listener.<br>
     * Pattern: "[[tag|display text]]" or "[[tag and display text]]" (similar to wikipedia linking) <br>
     * <br>
     * For example, a TextView with text <code>"Press [[main|here]] to open or [[reset]]."</code> <br>
     * will be converted into <code>"Press <u>here</u> to open or <u>reset</u>."</code> <br>
     * so that when clicked in the 'here' word: <i>listener.onClick("main")</i> will be called, <br>
     * and when clicked in the 'reset' word: <i>listener.onClick("reset")</i> will be called. <br>
     * (note: all spaces are kept in both tag and display text)
     *
     * @param textView TextView to convert
     * @param listener Listener for when a link is clicked. The link's tag specify which link was clicked.
     */
    public static void linkify(TextView textView, final OnUrlListener listener) {
        ArrayList<Match> matches = new ArrayList<>(); // keeps matches, reverse order
        SpannableStringBuilder text = new SpannableStringBuilder(textView.getText()); // final text

        // search all matches
        final Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            matches.add(0, new Match(matcher.start(), matcher.end(), matcher.group(1), matcher.group(2)));
        }

        // replace matches (in reverse order)
        for (final Match match : matches) {
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(View view) {
                    listener.onLinkClick(match.tag);
                }
            };
            text.setSpan(clickable, match.start, match.end, 0); // first set span
            text.replace(match.start, match.end, match.text); // then replace (span is automatically adapted)
        }

        // finish conversion
        textView.setText(text);
        textView.setLinksClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Runs the {@link #linkify(TextView, OnUrlListener)} on all textviews of the hierarchy recursively
     *
     * @param container parent where to search
     * @param listener  listener to apply to all textviews
     */
    public static void linkifyAll(ViewGroup container, OnUrlListener listener) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);

            if (view instanceof TextView) linkify((TextView) view, listener);
            else if (view instanceof ViewGroup) linkifyAll((ViewGroup) view, listener);
        }
    }

    // ------------------- private -------------------

    /**
     * Pattern: "[[tag|display text]]" or "[[tag and display text]]" (similar to wikipedia linking)
     */
    private static final Pattern PATTERN = Pattern.compile("\\[\\[([^|\\]]*)(?:\\|([^]]*))?]]");

    /**
     * Just a container
     */
    private static class Match {
        final int start;
        final int end;
        final String tag;
        final String text;

        Match(int start, int end, String tag, String text) {
            this.start = start;
            this.end = end;
            this.tag = tag;
            this.text = text == null ? tag : text;
        }
    }
}
