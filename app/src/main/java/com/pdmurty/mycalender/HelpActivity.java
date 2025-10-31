package com.pdmurty.mycalender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ApplyWindowInsets();


    }

    void ApplyWindowInsets(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setStatusBarContrastEnforced(true);
        }
        if (Build.VERSION.SDK_INT >= 36) {
            // Additional configuration for Android 16 (API level 36)
            View decorView = getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        TextView tv = findViewById(R.id.locTxt);
        ViewCompat.setOnApplyWindowInsetsListener(
                tv, (v, windowInsets) -> {
                    Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                    // Apply the insets as a margin to the view. This solution sets only the
                    // bottom, left, and right dimensions, but you can apply whichever insets are
                    // appropriate to your layout. You can also update the view padding if that's
                    // more appropriate.
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    //mlp.leftMargin = insets.left;
                    //mlp.bottomMargin = insets.bottom;
                    mlp.topMargin = insets.top;
                    v.setLayoutParams(mlp);

                    // Return CONSUMED if you don't want the window insets to keep passing
                    // down to descendant views.
                    return WindowInsetsCompat.CONSUMED;
                }


        );

    }
}