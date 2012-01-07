package de.dsp.android.hsc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import de.dsp.android.hsc.common.HscApplicationPreferences;
import de.dsp.android.hsc.task.HttpStatusCheckTask;

public class HttpStatusCheckerActivity extends Activity {
	
	private static final Map<Integer, ViewGroup> LAYOUT_GROUPS = new HashMap<Integer, ViewGroup>();
			
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LAYOUT_GROUPS.clear();
        
        List<CheckObject> checkObjects = HscApplicationPreferences.getInstance(this).getCheckObjects();

        ViewGroup mainLayout = (ViewGroup) findViewById(R.id.mainLayout);
        
        for (int i = 0; i < checkObjects.size(); i++) {
        	CheckObject checkObject = checkObjects.get(i);
        	
            LinearLayout subLayout = new LinearLayout(this);
            subLayout.setOrientation(LinearLayout.HORIZONTAL);
            subLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            //wir merken uns die Gruppe, damit wir später wieder die Elemente herausholen können
            LAYOUT_GROUPS.put(checkObject.getId(), subLayout);
            mainLayout.addView(subLayout, i);

            ImageView status = new ImageView(this);
            LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER_VERTICAL;
            status.setLayoutParams(p);
            status.setVisibility(View.GONE);
            subLayout.addView(status);

            ProgressBar progressBar = new ProgressBar(this);
            p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(p);
            progressBar.setVisibility(View.GONE);
            subLayout.addView(progressBar);
            
            CheckBox checkBox = new CheckBox(this);
            p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            checkBox.setLayoutParams(p);
            checkBox.setText(checkObject.getName());
            //TODO Checkstatus setzen
            subLayout.addView(checkBox);
        }
        
    }
    
    public void buttonCheckOnClick(View view){
    	
    	switch(view.getId()){
    		case R.id.buttonCheck:
    			
    	        List<CheckObject> checkObjects = HscApplicationPreferences.getInstance(this).getCheckObjects();

    	        for (int i = 0; i < checkObjects.size(); i++) {
    	        	CheckObject checkObject = checkObjects.get(i);
    	        	ViewGroup layoutGroup = LAYOUT_GROUPS.get(checkObject.getId());
    	        	
        			ImageView imageViewStatus = (ImageView) layoutGroup.getChildAt(0);
        			ProgressBar progressBar = (ProgressBar) layoutGroup.getChildAt(1);
        			CheckBox checkBox = (CheckBox) layoutGroup.getChildAt(2);

        			checkHttpStatus(checkObject.getUrl(), checkBox, progressBar, imageViewStatus);
    	        }
    	}
    	
    }

    private void checkHttpStatus(String url, CheckBox checkBox, ProgressBar progressBar, ImageView imageViewStatus){
    	if (checkBox!=null && checkBox.isChecked()){
        	HttpStatusCheckTask task = new HttpStatusCheckTask(progressBar, imageViewStatus);
        	task.execute(url);
    	}
    }
    
}