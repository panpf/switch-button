package me.xiaopan.android.switchbutton.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import me.xiaopan.android.switchbutton.R;

public class MainActivity extends Activity implements View.OnClickListener{
    private CompoundButton compoundButton1;
    private CompoundButton compoundButton2;
    private CompoundButton compoundButton3;
    private CompoundButton compoundButton4;
    private CompoundButton compoundButton5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final LinearLayout layout1 = (LinearLayout) findViewById(R.id.layout_main_1);
        final LinearLayout layout2 = (LinearLayout) findViewById(R.id.layout_main_2);
        final LinearLayout layout3 = (LinearLayout) findViewById(R.id.layout_main_3);
        final LinearLayout layout4 = (LinearLayout) findViewById(R.id.layout_main_4);
        final LinearLayout layout5 = (LinearLayout) findViewById(R.id.layout_main_5);

        compoundButton1 = (CompoundButton) findViewById(R.id.switch_main_1);
        compoundButton2 = (CompoundButton) findViewById(R.id.switch_main_2);
        compoundButton3 = (CompoundButton) findViewById(R.id.switch_main_3);
        compoundButton4 = (CompoundButton) findViewById(R.id.switch_main_4);
        compoundButton5 = (CompoundButton) findViewById(R.id.switch_main_5);

        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        layout4.setOnClickListener(this);
        layout5.setOnClickListener(this);

        compoundButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                compoundButton2.setEnabled(isChecked);
                compoundButton3.setEnabled(isChecked);
                compoundButton4.setEnabled(isChecked);
                compoundButton5.setEnabled(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.layout_main_1 : if(compoundButton1.isEnabled()) compoundButton1.setChecked(!compoundButton1.isChecked()); break;
            case R.id.layout_main_2 : if(compoundButton2.isEnabled()) compoundButton2.setChecked(!compoundButton2.isChecked()); break;
            case R.id.layout_main_3 : if(compoundButton3.isEnabled()) compoundButton3.setChecked(!compoundButton3.isChecked()); break;
            case R.id.layout_main_4 : if(compoundButton4.isEnabled()) compoundButton4.setChecked(!compoundButton4.isChecked()); break;
            case R.id.layout_main_5 : if(compoundButton5.isEnabled()) compoundButton5.setChecked(!compoundButton5.isChecked()); break;
        }
    }
}
