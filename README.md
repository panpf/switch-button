# ![Logo](docs/logo.png) SwitchButton

【Deprecated】【Stop maintenance】停止维护了，推荐使用：https://github.com/zcweng/SwitchButton

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SwitchButton-green.svg?style=true)](https://android-arsenal.com/details/1/4175)
[![Release Version](https://img.shields.io/github/release/panpf/switch-button.svg)](https://github.com/panpf/switch-button/releases)

SwitchButton 是 Android 上的一个开关按钮控件

![sample](docs/sample.gif)

### 示例 APP

![SampleApp](docs/sample-qrcode.png)

[扫描二维码或点我下载](https://github.com/panpf/switch-button/raw/master/docs/switch-button-sample.apk)

### 特性

* 支持滑动切换
* 支持标题
* 支持 Left、Top、Right、Bottom Drawable
* 支持使用遮罩实现圆角按钮

### 使用指南（Usage Guide）

#### 从 JCenter 导入

```groovy
dependencies {
	compile 'me.panpf:switch-button:$lastVersionName'
}
```

`$lastVersionName`：[![Release Version](https://img.shields.io/github/release/panpf/switch-button.svg)](https://github.com/panpf/switch-button/releases)`（不带v）`

最低支持 `Android2.2 API 7`

#### 在布局中使用

```xml
<?xml version="1.0" encoding="utf-8"?>
<me.panpf.switchbutton.SwitchButton
    android:id="@+id/switch"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="接受推送"/>
```

#### 在代码中使用

SwitchButton 继承自 CompoundButton ，因此你可以像使用 CheckBox 那样设置状态和监听

一些可用的方法：

* setDrawables(Drawable, Drawable, Drawable, Drawable)：设置图片
* setDrawableResIds(int, int, int, int)：设置图片ID
* setWithTextInterval(int)：设置标题和按钮的间距，默认为 16dp
* setDuration(int)：设置动画持续时间，单位毫秒，默认为 200
* setMinChangeDistanceScale(float)：设置滑动有效距离比例，默认为 0.2。例如按钮宽度为 100，比例为 0.2，那么只有当滑动距离大于等于 (100*0.2) 才会切换状态，否则就回滚

#### 自定义图片资源

SwitchButton 由四张图片构成

* frameDrawable：框架图片，决定按钮的大小以及显示区域
* stateDrawable：状态图片，显示开启或关闭状态
* stateMaskDrawable：状态图片遮罩层，用于让状态图片按照遮罩层的形状显示
* sliderDrawable：滑块图片

第一种方式你可以通过 SwitchButton 的自定义属性设置资源图片，如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<me.panpf.switchbutton.SwitchButton
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/switch"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="接受推送"
    app:frameDrawable="@drawable/switch_frame"
    app:stateDrawable="@drawable/selector_switch_state"
    app:stateMaskDrawable="@drawable/switch_state_mask"
    app:sliderDrawable="@drawable/selector_switch_slider"/>
```

第二种方式是通过上面提到的 setDrawables(Drawable, Drawable, Drawable, Drawable) 或 setDrawableResIds(int, int, int, int) 方法设置资源图片

### License

    Copyright (C) 2017 Peng fei Pan <sky@panpf.me>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
