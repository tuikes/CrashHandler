CrashHandlerLib
======
一个基于UncaughtExceptionHandler实现的Android奔溃日志捕获依赖库，使用该库可以显性的将奔溃日志展示出来，有助于开发以及测试人员在工作中及时定位奔溃问题.同时支持将奔溃日志分享到微信、QQ等第三方。

使用手册
======

step 1.在根目录 build.gradle 上添加配置
--------
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
step 2.在当前module的build.gradle添加如下依赖
--------
	dependencies {
		com.github.CrazyCoder01:CrashHandler:v1.9'
	}
	
step 3.在Application中完成初始化
--------
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            CrashManager.getInstance().init(this);
        }
    }
    
效果演示
======
step 1.测试代码源码
--------
类型为TextView的result控件没有findViewById导致奔溃

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = findViewById(R.id.test);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("hello world");
            }
        });
    }
  
  
step 2.运行查看效果
--------
![ABC](https://github.com/tuikes/MarkdownPhotos/blob/master/crashHandlerLibTest.gif) 
