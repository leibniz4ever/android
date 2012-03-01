package com.overfitters;
 
import com.theoverfitters.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
 
public class SaveAs extends Activity {
	TextView tb;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_as);
        
        tb = (TextView) findViewById(R.id.saveAsWarning);
    }
    
    public void ok(View view) {
    	String filename = ((EditText)findViewById(R.id.saveAsText)).getText().toString();
    	if(ContentManager.getContentManager().saveTo(filename))
    		finish();
    	else
    		tb.setText("Invalid filename!");
    }
    
    public void cancel(View view) {
    	finish();
    }
}