
Android 13 Aosp Settings Android Studio版本

Settings相关源码

Settings
https://android.googlesource.com/platform/packages/apps/Settings/+/refs/heads/android13-release


SettingsIntelligence
https://android.googlesource.com/platform/packages/apps/SettingsIntelligence/+/refs/heads/android13-release


frameworks/base/packages/SettingsLib
https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android13-release


新建 Settings Moudle, 将源码相关文件挪过去。


新建 SettingsIntelligence Library, 将源码相关文件(src, res, AndroidManifest)挪过去。


新建 SettingsLib Library, 将源码相关文件(src, res, AndroidManifest)挪过去。


SettingsLib 被拆成了以下模块，真是个体力活啊~~o.o

include ':SettingsLib'
include ':SettingsLib:ActionBarShadow'
include ':SettingsLib:ActionButtonsPreference'
include ':SettingsLib:ActivityEmbedding'
include ':SettingsLib:AdaptiveIcon'
include ':SettingsLib:AppPreference'
include ':SettingsLib:BannerMessagePreference'
include ':SettingsLib:BarChartPreference'
include ':SettingsLib:ButtonPreference'
include ':SettingsLib:CollapsingToolbarBaseActivity'
include ':SettingsLib:DisplayDensityUtils'
include ':SettingsLib:EmergencyNumber'
include ':SettingsLib:EntityHeaderWidgets'
include ':SettingsLib:FooterPreference'
include ':SettingsLib:HelpUtils'
include ':SettingsLib:IllustrationPreference'
include ':SettingsLib:LayoutPreference'
include ':SettingsLib:MainSwitchPreference'
include ':SettingsLib:ProgressBar'
include ':SettingsLib:RadioButtonPreference'
include ':SettingsLib:RestrictedLockUtils'
include ':SettingsLib:SchedulesProvider'
include ':SettingsLib:search'
include ':SettingsLib:SearchProvider'
include ':SettingsLib:SearchWidget'
include ':SettingsLib:SelectorWithWidgetPreference'
include ':SettingsLib:SettingsSpinner'
include ':SettingsLib:SettingsTheme'
include ':SettingsLib:SettingsTransition'
include ':SettingsLib:Tile'
include ':SettingsLib:TopIntroPreference'
include ':SettingsLib:TwoTargetPreference'
include ':SettingsLib:UsageProgressBarPreference'
include ':SettingsLib:Utils'


参考Android.bp 所依赖的library,  从Aosp/out/ 目录下通过 find . -name "***.jar" 查找到jar包Copy进去。


    static_libs: [
        "androidx.annotation_annotation",
        "androidx.legacy_legacy-support-v4",
        "androidx.recyclerview_recyclerview",
        "androidx.preference_preference",
        "androidx.appcompat_appcompat",
        "androidx.lifecycle_lifecycle-runtime",
        "androidx.mediarouter_mediarouter-nodeps",
        "com.google.android.material_material",
        "iconloader",
        "WifiTrackerLibRes",
        "SettingsLibHelpUtils",
        "SettingsLibRestrictedLockUtils",
        "SettingsLibActionBarShadow",
        "SettingsLibAppPreference",
        "SettingsLibSearchWidget",
        "SettingsLibSettingsSpinner",
        "SettingsLibIllustrationPreference",
        "SettingsLibLayoutPreference",
        "SettingsLibMainSwitchPreference",
        "SettingsLibActionButtonsPreference",
        "SettingsLibEntityHeaderWidgets",
        "SettingsLibBarChartPreference",
        "SettingsLibProgressBar",
        "SettingsLibAdaptiveIcon",
        "SettingsLibRadioButtonPreference",
        "SettingsLibSelectorWithWidgetPreference",
        "SettingsLibDisplayDensityUtils",
        "SettingsLibUtils",
        "SettingsLibEmergencyNumber",
        "SettingsLibTopIntroPreference",
        "SettingsLibBannerMessagePreference",
        "SettingsLibFooterPreference",
        "SettingsLibUsageProgressBarPreference",
        "SettingsLibCollapsingToolbarBaseActivity",
        "SettingsLibTwoTargetPreference",
        "SettingsLibSettingsTransition",
        "SettingsLibButtonPreference",
        "setupdesign",
        "zxing-core-1.7",
    ],

      static_libs: [
        "androidx.annotation_annotation",
        "androidx.lifecycle_lifecycle-common",
        "androidx.legacy_legacy-support-v4",
        "androidx.lifecycle_lifecycle-runtime",
        "androidx.recyclerview_recyclerview",
        "androidx.preference_preference",
        "androidx.appcompat_appcompat",
        "androidx.legacy_legacy-preference-v14",
        "SettingsLib",
    ],






解决编译报错

1. attrs.xml
   Found item Attr/messageText more than one time


将属性定义在外部，内部只声明引用

![attrs属性重复.png](Png%2Fattrs%CA%F4%D0%D4%D6%D8%B8%B4.png)


2. string.xml  
   Found item String/sdcard_unmount more than one time.
   Probably you want to define a new flavor for building multiple APKs with different values for the same resource


build.gradle目前尚不支持product属性，无法通过此属性来区分，所以出现资源重复，只能保留一项，


![string_product.png](Png%2Fstring_product.png)


3. \Settings\SettingsIntelligence\src\main\java\com\android\settings\intelligence\search\IntentSearchViewHolder.java:26: error: package com.android.settings.intelligence.nano does not exist


import com.android.settings.intelligence.nano.SettingsIntelligenceLogProto;
//import com.android.settings.intelligence.nano.SettingsIntelligenceLogProto;
import com.android.settings.intelligence.SettingsIntelligenceLogProto;


详情可参考:  [https://blog.csdn.net/Sqq_yj/article/details/144033113?spm=1001.2014.3001.5501](https://blog.csdn.net/Sqq_yj/article/details/144033113?spm=1001.2014.3001.5501)


4. \Settings\SettingsIntelligence\src\main\java\com\android\settings\intelligence\search\IntentSearchViewHolder.java:46: error: package SettingsIntelligenceLogProto does not exist
   return SettingsIntelligenceLogProto.SettingsIntelligenceEvent.CLICK_SEARCH_RESULT;


       //return SettingsIntelligenceLogProto.SettingsIntelligenceEvent.CLICK_SEARCH_RESULT;
     return SettingsIntelligenceLogProto.SettingsIntelligenceEvent.EventType.CLICK_SEARCH_RESULT.getNumber();`


5. \Settings\SettingsIntelligence\src\main\java\com\android\settings\intelligence\search\indexing\XmlParserUtils.java:41: error: cannot find symbol
   R.styleable.Preference_android_key);
   ^
   symbol:   variable Preference_android_key
   location: class styleable


    public static String getDataKey(Context context, AttributeSet attrs) {
        return getData(context, attrs,
                R.styleable.Preference,
               /* R.styleable.Preference_android_key*/
                androidx.preference.R.styleable.Preference_key);
    }


6. \Settings\SettingsLib\src\main\java\com\android\settingslib\accessibility\AccessibilityUtils.java:32: error: package com.android.internal does not exist
   import com.android.internal.R;


    Aosp编译整包生成framework.jarCopy出来
    out/target/common/obj/JAVA_LIBRARIES/framework_intermediates/classes.jar
    
    compileOnly files('libs\\framework.jar')


7. \Settings\SettingsLib\src\main\java\com\android\settingslib\Utils.java:51: error: package com.android.launcher3.icons.BaseIconFactory does not exist
   import com.android.launcher3.icons.BaseIconFactory.IconOptions;

       //Aosp编译出的iconloader.jar
       //通过 find . -name "iconloader.jar" 查找
       compileOnly files('libs\\iconloader.jar')`


8. \Settings\SettingsLib\src\main\java\com\android\settingslib\widget\IllustrationPreference.java:40: error: package com.airbnb.lottie does not exist
   import com.airbnb.lottie.LottieAnimationView;

        implementation 'com.airbnb.android:lottie:3.1.0'

9. \Settings\SettingsLib\src\main\java\com\android\settingslib\datetime\ZoneGetter.java:35: error: package com.android.i18n.timezone does not exist
   import com.android.i18n.timezone.CountryTimeZones;
![core-icu4j.png](Png%2Fcore-icu4j.png)
![find-core-icu4j.png](Png%2Ffind-core-icu4j.png)

       // 在Aosp源码中搜索 com.android.i18n 关联 java_libary: core-icu4j.
       implementation files('libs\\core-icu4j.jar')


10. \Settings\SettingsLib\src\main\java\com\android\settingslib\activityembedding\ActivityEmbeddingUtils.java:28: error: package androidx.window.embedding does not exist
    import androidx.window.embedding.SplitController;

         implementation 'androidx.window:window:1.3.0'


     最后，在Aosp源码根目录下执行脚本，添加上系统签名
     java -Djava.library.path="out/host/linux-x86/lib64" -jar out/host/linux-x86/framework/signapk.jar build/target/product/security/platform.x509.pem build/target/product/security/platform.pk8 unsign_app.apk sign_app.apk
