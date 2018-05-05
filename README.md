## QRCodeView
基于 com.google.zxing 开发，用于简化 Android 设备扫描、生成二维码等操作。

## 前言
[QRCodeView](https://github.com/0xZhangKe/QRCodeView) 基于 Google  的 [zxing](https://github.com/zxing/zxing) 进行二次开发，简化二维码的相关操作、优化识别速度。由于整个 zxing 框架中不仅包含了二维码识别相关的代码，还包括其他格式如条形码等等的代码，体量较大，实际开发中一般只需要使用二维码，故本项目中删除了不必要的代码。本项目不仅提供了使用摄像头预览实时识别二维码，还有如生成一张二维码图片，生成一张带有 logo 的二维码图片等等。
**注意：此项目只支持二维码，不支持其他格式的条码！**
## 效果图
<img src="http://img.blog.csdn.net/20180102153219279?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzg3Mjg1Nw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" alt="预览图"Swidth=400 height=600  align=center/>
## 使用方式
使用起来很简单，先看一下布局文件：
```
<com.zhangke.qrcodeview.QRCodeView
    android:id="@+id/qr_code_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:showSlider="true"
    app:sliderColor="@android:color/holo_green_dark"
    app:showPoint="true"
    app:pointColor="@color/colorAccent"
    app:frameColor="@android:color/white"/>
```
再看下 java 代码：
```
package com.zhangke.qrcodeview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import com.zhangke.qrcodeview.QRCodeView;
import com.zhangke.qrcodeview.smaple.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private QRCodeView qrCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrCodeView = (QRCodeView) findViewById(R.id.qr_code_view);
        qrCodeView.setOnQRCodeListener(new QRCodeView.OnQRCodeRecognitionListener() {
            @Override
            public void onQRCodeRecognition(Result result) {
                Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeView.startPreview();
    }

    @Override
    protected void onPause() {
        qrCodeView.stopPreview();
        super.onPause();
    }
}

```
是不是很简单？就是根据声明周期初始化一下，设置个回调就 OK 了，对了别忘记添加权限：
```
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
```
## 将 QRCodeView 添加到自己的项目中
首先打开 QRCodeView 项目：
https://github.com/0xZhangKe/QRCodeView

其中有两个 model：lib 和 sample ，sample 是使用的案例，不会使用的可以参照其中的代码。lib 即核心代码，可以将整个项目 clone 到本地，然后将 lib 添加到自己的项目中，然后设置好依赖就行了。
或者也可以直接将 lib 下面的代码复制到你自己的项目中。
## 类文档
### QRCodeViewView
**包名：**
```
com.zhangke.qrcodeview
```
**描述：**
二维码预览 View。


**XML attributes：**
| 参数名 | 介绍 |
| ------| ------ |
| app:facing | 前置或后置摄像头 |
| app:showFrame | 是否显示边框，默认显示 |
| app:frameColor | 边框颜色，默认为白色 |
| app:showPoint | 是否绘制二维码可能出现区域的点，默认不显示 |
| app:pointColor | 点的颜色，默认为红色 |
| app:showSlider | 是否开启扫描动画，默认显示 |
| app:sliderColor | 扫描条的颜色，默认绿色 |<p></br>



**Public constructors**
```
public QRCodeView(Context context)
public QRCodeView(Context context, AttributeSet attrs)
public QRCodeView(Context context, AttributeSet attrs, int defStyleAttr)
```
**Public methods**

name|  describe
------| ------
void startPreview() | 开始预览，一般在 onResume() 中调用
void stopPreview() | 停止预览，一般在 onPause() 中调用
boolean isPreview() | 当前是否正在预览
Camera getCamera() | 获取 Camera 对象
void setOnQRCodeListener(OnQRCodeRecognitionListener onQRCodeListener) | 设置二维码识别监听器



### OnQRCodeRecognitionListener
**包名：**
```
com.zhangke.qrcodeview.QRCodeViewView
```
**描述：**
二维码识别监听器
name|  describe
------| ------
void onQRCodeRecognition(Result result) | 识别到二维码时会回调此方法



### QRCodeUtil
**包名：**
```
com.zhangke.qrcodeview
```
**描述：**
二维码相关工具类。
name|  describe
------| ------
static Bitmap createQRCode(String text) throws WriterException | 根据文本创建二维码图片
static Bitmap createQRCode(String text, Bitmap logo) | 根据文本及 logo 创建一个带有 logo 的二维码图片
static String decodeQRCode(Bitmap bitmap) | 识别一张二维码图片，返回其中的文本
