package me.xiaopan.android.switchbutton.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import me.xiaopan.android.switchbutton.R;

public class MainActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ((CompoundButton) findViewById(R.id.switch_main_1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.switch_main_2).setEnabled(isChecked);
                findViewById(R.id.switch_main_3).setEnabled(isChecked);
                findViewById(R.id.switch_main_4).setEnabled(isChecked);
                findViewById(R.id.switch_main_5).setEnabled(isChecked);
                findViewById(R.id.switch_main_6).setEnabled(isChecked);
                findViewById(R.id.switch_main_7).setEnabled(isChecked);
                findViewById(R.id.switch_main_8).setEnabled(isChecked);
            }
        });
    }
}
