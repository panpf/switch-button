# ![Logo](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-mdpi/ic_launcher.png) Android-SwitchButton

Android-SwitchButton是用在Android上的开关按钮，最低兼容Android2.2

![sample](https://github.com/xiaopansky/Android-SwitchButton/raw/master/docs/sample.png)

##Features

>* 支持滑动切换

>* 支持标题

>* 支持Left、Top、Right、Bottom Drawable

>* 支持使用遮罩实现圆角按钮

##Usage
###1.图片准备

![switch_frame.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_frame.png) **[switch_frame.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_frame.png)**

![switch_state_normal.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_state_normal.png) **[switch_state_normal.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_state_normal.png)**

![switch_state_disable.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_state_disable.png) **[switch_state_disable.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_state_disable.png)**

![switch_state_mask.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_state_mask.png) **[switch_state_mask.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_state_mask.png)**

![switch_slider_normal.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_slider_normal.png) **[switch_slider_normal.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_slider_normal.png)**

![switch_slider_disable.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_slider_disable.png) **[switch_slider_disable.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_slider_disable.png)**

![switch_slider_mask.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_slider_mask.png) **[switch_slider_mask.png](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/drawable-xhdpi/switch_slider_mask.png)**

selector_switch_state.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android" >
    <item android:state_enabled="false" android:drawable="@drawable/switch_slider_disable"/>
    <item android:drawable="@drawable/switch_slider_normal"/>
</selector>
```

selector_switch_slider.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android" >
    <item android:state_enabled="false" android:drawable="@drawable/switch_state_disable"/>
    <item android:drawable="@drawable/switch_state_normal"/>
</selector>
```
###2.下载属性文件**[attrs.xml](https://github.com/xiaopansky/Android-SwitchButton/raw/master/res/values/attrs.xml)**并放到values目录下。源码如下，也可复制粘贴到项目中
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

###3.下载**[SwitchButton.java](https://github.com/xiaopansky/Android-SwitchButton/raw/master/src/me/xiaopan/android/switchbutton/SwitchButton.java)**并放到任意源码目录下，注意放到你项目中要修改R文件的引用才能编译通过

###4.在布局中添加自定义属性并引用SwitchButton
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

###5.属性解释
>* withTextInterval：标题文字和按钮之间的距离
>* frameDrawable：框架图片，定义按钮的大小以及显示区域
>* stateDrawable：状态图片，显示开启或关闭
>* stateMaskDrawable：状态图片遮罩层
>* sliderDrawable：滑块图片
>* sliderMaskDrawable：滑块图片遮罩层

###6.其它方法：
>* setDrawables(Drawable frameBitmap, Drawable stateDrawable, Drawable stateMaskDrawable, Drawable sliderDrawable, Drawable sliderMaskDrawable)：设置图片
>* setDrawableResIds(int frameDrawableResId, int stateDrawableResId, int stateMaskDrawableResId, int sliderDrawableResId, int sliderMaskDrawableResId)：设置图片ID
>* setWithTextInterval(int withTextInterval)：设置标题和按钮的间距，默认为16
>* setDuration(int duration)：设置动画持续时间，单位毫秒，默认为200
>* setMinChangeDistanceScale(float minChangeDistanceScale)：设置滑动有效距离比例，默认为0.2。例如按钮宽度为100，比例为0.2，那么只有当滑动距离大于等于(100*0.2)才会切换状态，否则就回滚

##示例图片源码
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView
	xmlns:android="http://schemas.android.com/apk/res/android"
	xmlns:app="http://schemas.android.com/apk/res/me.xiaopan.android.switchbutton"
	android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/white">
	<LinearLayout
	    android:orientation="vertical"
	    android:layout_width="fill_parent"
	    android:layout_height="fill_parent"
	    android:paddingTop="16dp"
	    android:paddingBottom="16dp">

        <me.xiaopan.android.switchbutton.SwitchButton
	        style="@style/item"
            android:id="@+id/switch_main_1"
            android:text="接受推送"
            android:checked="true"
            android:background="@drawable/selector_preference_complete"
            app:withTextInterval="16dp"
            app:frameDrawable="@drawable/switch_frame"
            app:stateDrawable="@drawable/selector_switch_state"
            app:stateMaskDrawable="@drawable/switch_state_mask"
            app:sliderDrawable="@drawable/selector_switch_slider"
            app:sliderMaskDrawable="@drawable/switch_slider_mask"/>

        <me.xiaopan.android.switchbutton.SwitchButton
	        style="@style/item"
            android:id="@+id/switch_main_2"
            android:text="每日头条"
	        android:background="@drawable/selector_preference_header"
	        android:layout_marginTop="16dp"
            app:withTextInterval="16dp"
            app:frameDrawable="@drawable/switch_frame"
            app:stateDrawable="@drawable/selector_switch_state"
            app:stateMaskDrawable="@drawable/switch_state_mask"
            app:sliderDrawable="@drawable/selector_switch_slider"
            app:sliderMaskDrawable="@drawable/switch_slider_mask"/>

        <me.xiaopan.android.switchbutton.SwitchButton
            style="@style/item"
            android:id="@+id/switch_main_3"
            android:text="每日热卖"
            android:background="@drawable/selector_preference_center"
            app:withTextInterval="16dp"
            app:frameDrawable="@drawable/switch_frame"
            app:stateDrawable="@drawable/selector_switch_state"
            app:stateMaskDrawable="@drawable/switch_state_mask"
            app:sliderDrawable="@drawable/selector_switch_slider"
            app:sliderMaskDrawable="@drawable/switch_slider_mask"/>

        <me.xiaopan.android.switchbutton.SwitchButton
            style="@style/item"
            android:id="@+id/switch_main_4"
            android:text="优惠促销"
            android:background="@drawable/selector_preference_center"
            app:withTextInterval="16dp"
            app:frameDrawable="@drawable/switch_frame"
            app:stateDrawable="@drawable/selector_switch_state"
            app:stateMaskDrawable="@drawable/switch_state_mask"
            app:sliderDrawable="@drawable/selector_switch_slider"
            app:sliderMaskDrawable="@drawable/switch_slider_mask"/>

        <me.xiaopan.android.switchbutton.SwitchButton
	        style="@style/item"
            android:id="@+id/switch_main_5"
            android:text="偶像动态"
	        android:background="@drawable/selector_preference_center"
	        app:withTextInterval="16dp"
            app:frameDrawable="@drawable/switch_frame"
            app:stateDrawable="@drawable/selector_switch_state"
            app:stateMaskDrawable="@drawable/switch_state_mask"
            app:sliderDrawable="@drawable/selector_switch_slider"
            app:sliderMaskDrawable="@drawable/switch_slider_mask"/>

		<me.xiaopan.android.switchbutton.SwitchButton
			style="@style/item"
			android:id="@+id/switch_main_6"
			android:text="娱乐播报"
			android:background="@drawable/selector_preference_footer"
			app:withTextInterval="16dp"
			app:frameDrawable="@drawable/switch_frame"
			app:stateDrawable="@drawable/selector_switch_state"
			app:stateMaskDrawable="@drawable/switch_state_mask"
			app:sliderDrawable="@drawable/selector_switch_slider"
			app:sliderMaskDrawable="@drawable/switch_slider_mask"/>

        <me.xiaopan.android.switchbutton.SwitchButton
            style="@style/item"
            android:id="@+id/switch_main_7"
            android:text="天气预报"
            android:background="@drawable/selector_preference_header"
            android:layout_marginTop="16dp"
            app:withTextInterval="16dp"
            app:frameDrawable="@drawable/switch_frame"
            app:stateDrawable="@drawable/selector_switch_state"
            app:stateMaskDrawable="@drawable/switch_state_mask"
            app:sliderDrawable="@drawable/selector_switch_slider"
            app:sliderMaskDrawable="@drawable/switch_slider_mask"/>

        <me.xiaopan.android.switchbutton.SwitchButton
            style="@style/item"
            android:id="@+id/switch_main_8"
            android:text="系统消息"
            android:background="@drawable/selector_preference_footer"
            app:withTextInterval="16dp"
            app:frameDrawable="@drawable/switch_frame"
            app:stateDrawable="@drawable/selector_switch_state"
            app:stateMaskDrawable="@drawable/switch_state_mask"
            app:sliderDrawable="@drawable/selector_switch_slider"
            app:sliderMaskDrawable="@drawable/switch_slider_mask"/>
	</LinearLayout>
</ScrollView>

```

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
