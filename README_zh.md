# IOSSwitch
一个iOS7 风格的 Switch Button 。很多Android App中都使用了iOS风格的Switch Button，暂不论行为的好坏，但是这些控件都是有个样子而已，动画僵硬刻板（QQ就是说的你）。

###屏幕截图:

![Screen shot](http://image17-c.poco.cn/mypoco/myphoto/20160414/14/17425403720160414142738071.gif?327x484_110)

图片链接:http://image17-c.poco.cn/mypoco/myphoto/20160414/14/17425403720160414142738071.gif?327x484_110

###怎样使用

导入这个 module:library

###一些个性化设置

####布局文件中:

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

####代码中:

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
