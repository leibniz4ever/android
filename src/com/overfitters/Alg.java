package com.overfitters;
 
import com.theoverfitters.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
 
public class Alg extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alg);
    }
 
    public void toBrCon(View view) {
         Intent i = new Intent(this, BrightnessContrast.class);
         this.startActivity(i);
         this.finish();
    }
 
    public void toInvert(View view) {
         Intent i = new Intent(this, Invert.class);
         this.startActivity(i);
         this.finish();
    }
}