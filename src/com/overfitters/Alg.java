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
 
    public void toAlg1(View view) {
         Intent i = new Intent(this, Alg1.class);
         this.startActivity(i);
         this.finish();
    }
 
    public void toAlg2(View view) {
         Intent i = new Intent(this, Alg2.class);
         this.startActivity(i);
         this.finish();
    }
}