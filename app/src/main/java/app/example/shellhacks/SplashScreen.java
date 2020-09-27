package app.example.shellhacks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);
        logo = findViewById(R.id.splashImage);

        fadeInLogo(logo);
       /* new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                //Create an Intent that will start the Menu-Activity.
                Intent mainIntent = new Intent(SplashScreen.this, SignIn.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);*/

    }

    // Create a method called fadeOutAndHideImage that takes in an ImageView
    private void fadeInLogo(final ImageView img)
    {
        // Fade Animation code
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(1500); // How long it takes for the animation to complete in milliseconds

        fadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            // Once the animation is done, set the visibility of the logo to GONE and navigate to the MainActivity after the set amount of time.
            public void onAnimationEnd(Animation animation)
            {
                /* New Handler to start the Menu-Activity
                 * and close this Splash-Screen after some seconds.*/
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        /* Create an Intent that will start the Menu-Activity. */
                        startActivity(new Intent(SplashScreen.this, SignIn.class));
                        finish();

                    }
                }, SPLASH_DISPLAY_LENGTH);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeIn);
    }
}
