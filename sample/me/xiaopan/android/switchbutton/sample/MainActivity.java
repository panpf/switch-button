package me.xiaopan.android.switchbutton.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import me.xiaopan.android.switchbutton.R;

public class MainActivity extends Activity implements View.OnClickListener{
    private CompoundButton compoundButton1;
    private CompoundButton compoundButton2;
    private CompoundButton compoundButton3;
    private CompoundButton compoundButton4;
    private CompoundButton compoundButton5;
    private CompoundButton compoundButton6;
    private CompoundButton compoundButton7;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        compoundButton1 = (CompoundButton) findViewById(R.id.switch_main_1);
        compoundButton2 = (CompoundButton) findViewById(R.id.switch_main_2);
        compoundButton3 = (CompoundButton) findViewById(R.id.switch_main_3);
        compoundButton4 = (CompoundButton) findViewById(R.id.switch_main_4);
        compoundButton5 = (CompoundButton) findViewById(R.id.switch_main_5);
        compoundButton6 = (CompoundButton) findViewById(R.id.switch_main_6);
        compoundButton7 = (CompoundButton) findViewById(R.id.switch_main_7);

        compoundButton1.setOnClickListener(this);
        compoundButton2.setOnClickListener(this);
        compoundButton3.setOnClickListener(this);
        compoundButton4.setOnClickListener(this);
        compoundButton5.setOnClickListener(this);
        compoundButton6.setOnClickListener(this);
        compoundButton7.setOnClickListener(this);

        compoundButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                compoundButton2.setEnabled(isChecked);
                compoundButton3.setEnabled(isChecked);
                compoundButton4.setEnabled(isChecked);
                compoundButton5.setEnabled(isChecked);
                compoundButton6.setEnabled(isChecked);
                compoundButton7.setEnabled(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getBaseContext(), "点击", Toast.LENGTH_SHORT).show();
        if(v.isEnabled()){
            CompoundButton compoundButton = (CompoundButton) v;
            compoundButton.setChecked(!compoundButton.isChecked());
        }
    }
}
