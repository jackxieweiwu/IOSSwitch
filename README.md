# IOSSwitch
A Switch Button with smooth animation like iOS7~iOS9

###Screen shot:

![Screen shot](http://image17-c.poco.cn/mypoco/myphoto/20160414/14/17425403720160414142738071.gif?327x484_110)

Picture link:http://image17-c.poco.cn/mypoco/myphoto/20160414/14/17425403720160414142738071.gif?327x484_110

###How to use
import the module:library

###Some custom setting

####In layout xml:

```

  <com.kot32.library.widgets.IOSSwitch
        android:layout_width="50dp"
        android:layout_height="25dp"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="80dp"
        app:ballColor="#4675FF"
        app:bgColor="#d84315"
        app:checked="true"
        />
        
```

####In Java code:

```

        mIOSSwitch.toggle();

        mIOSSwitch.setOnCheckedChangeListener(new IOSSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( boolean isChecked) {
                if(isChecked){
                    toggleButton.setText("关闭");
                }else{
                    toggleButton.setText("打开");
                }
            }
        });

```
