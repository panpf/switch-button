# ![Logo](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-mdpi/ic_launcher.png) Android-SwitchButton

Android-SwitchButton是用在Android上的开关按钮，最低兼容Android2.2

![sample](https://github.com/xiaopansky/Android-SwitchButton/raw/master/docs/sample.jpg)

##Features

>* 支持滑动切换

>* 支持标题

>* 支持Left、Top、Right、Bottom Drawable

>* 支持使用遮罩实现圆角按钮

##Usage
### 使用自定义属性设置按钮图片
####1. 下载**[SwitchButton.java](https://github.com/xiaopansky/Android-SwitchButton/raw/master/src/me.xiaopan.android.switchbutton.SwitchButton.java)**并放到任意源码目录下

####2. 下载属性文件**[attrs.xml](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/values/attrs.xml)**并放到values目录下。源码如下，也可复制粘贴到项目中
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="SwitchButton">
        <attr name="frameDrawable" format="reference|color"/>
        <attr name="stateDrawable" format="reference|color"/>
        <attr name="stateMaskDrawable" format="reference|color"/>
        <attr name="sliderDrawable" format="reference|color"/>
        <attr name="sliderMaskDrawable" format="reference|color"/>
	    <attr name="withTextInterval" format="dimension"/>
    </declare-styleable>
</resources>
```

####3. 在布局中添加自定义属性并引用SwitchButton
```xml
<?xml version="1.0" encoding="utf-8"?>
<me.xiaopan.android.switchbutton.SwitchButton
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res/me.xiaopan.android.switchbutton"
    android:id="@+id/switch"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:minHeight="50dp"
    android:paddingLeft="30dp"
    android:paddingRight="30dp"
    android:text="接受推送"
    app:withTextInterval="16dp"
    app:frameDrawable="@drawable/switch_frame"
    app:stateDrawable="@drawable/selector_switch_state"
    app:stateMaskDrawable="@drawable/switch_state_mask"
    app:sliderDrawable="@drawable/selector_switch_slider"
    app:sliderMaskDrawable="@drawable/switch_slider_mask"/>
```

### 使用相关方法设置按钮图片

##Downloads
**[android-switch-button-1.0.0.jar](https://github.com/xiaopansky/Android-SwitchButton/raw/master/releases/android-switch-button-1.0.0.jar)**

**[android-switch-button-1.0.0-with-src.jar](https://github.com/xiaopansky/Android-SwitchButton/raw/master/releases/android-switch-button-1.0.0-with-src.jar)**

**[attrs.xml](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/values/attrs.xml)**

**[SwitchButton.java](https://github.com/xiaopansky/Android-SwitchButton/raw/master/src/me/xiaopan/android/switchbutton/SwitchButton.java)**

![switch_frame.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_frame.png) **[switch_frame.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_frame.png)**

##License
```java
/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```
