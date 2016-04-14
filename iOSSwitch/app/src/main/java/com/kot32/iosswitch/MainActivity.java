package com.kot32.iosswitch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.kot32.library.widgets.IOSSwitch;

public class MainActivity extends AppCompatActivity {

    private Button toggle;

    private IOSSwitch mIOSSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggle =(Button)findViewById(R.id.open);

        mIOSSwitch=(IOSSwitch)findViewById(R.id.swi);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIOSSwitch.toggle();
            }
        });

        mIOSSwitch.setOnCheckedChangeListener(new IOSSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( boolean isChecked) {
                if(isChecked){
                    toggle.setText("关闭");
                }else{
                    toggle.setText("打开");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
