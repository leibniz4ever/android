package com.overfitters;
 
import com.theoverfitters.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
 
public class SaveAs extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_as);
    }
    
    public void ok(View view) {
    	Intent i = new Intent();
    	i.putExtra("filename", ((EditText)findViewById(R.id.saveAsText)).getText().toString());
    	this.setResult(Activity.RESULT_OK, i);
    	finish();
    }
    public void cancel(View view) {
    	this.setResult(RESULT_CANCELED);
    	finish();
    }
}