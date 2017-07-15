# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\UNKNOWN\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static *** CREATOR;
}

-dontwarn android.support.design.**
-dontwarn com.google.android.gms.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-dontwarn jcifs.http.NetworkExplorer
-dontwarn android.support.v4.**
-dontwarn android.support.v7.**
-dontwarn com.facebook.**
-dontwarn com.soundcloud.android.**
-dontwarn com.google.firebase.**
-dontwarn com.github.clans.**

-dontnote com.google.android.gms.**
-dontnote android.support.design.**
-dontnote android.support.v4.**
-dontnote android.support.v7.**
-dontnote com.facebook.**
-dontnote com.soundcloud.android.**
-dontnote com.google.firebase.**
-dontnote com.github.clans.**

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.apache.**
-dontwarn org.w3c.dom.**

-keep class  com.myapp.unknown.iNTCON.Acheivement.AchievementListProvider { *; }

-keep class  com.myapp.unknown.iNTCON.Campaign.CampaignDetailProvider { *; }
-keep class  com.myapp.unknown.iNTCON.Campaign.KeysAndGroupKeys { *; }
-keep class  com.myapp.unknown.iNTCON.Campaign.URLSpanNoUnderline { *; }

-keep class  com.myapp.unknown.iNTCON.DataResource.DataResourceItemProvider { *; }
-keep class  com.myapp.unknown.iNTCON.DataResource.DataResourceProvider { *; }

-keep class  com.myapp.unknown.iNTCON.InstRequest.CoursesOffered { *; }
-keep class  com.myapp.unknown.iNTCON.InstRequest.FieldDetail { *; }

-keep class  com.myapp.unknown.iNTCON.NavigationView.FAQClass { *; }
-keep class  com.myapp.unknown.iNTCON.NavigationView.GroupList { *; }
-keep class  com.myapp.unknown.iNTCON.NavigationView.GroupRequest { *; }
-keep class  com.myapp.unknown.iNTCON.NavigationView.UserRequestDetailClass { *; }
-keep class  com.myapp.unknown.iNTCON.NavigationView.UserGroupListClass { *; }

-keep class  com.myapp.unknown.iNTCON.Notice.NoticeListProvider { *; }
-keep class  com.myapp.unknown.iNTCON.Notice.NoticeChatListProvider { *; }
-keep class  com.myapp.unknown.iNTCON.Notice.My_Notice { *; }

-keep class  com.myapp.unknown.iNTCON.OtherClasses.AvatarImageBehavior { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.BoolClass { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.BugReportClass { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.CheckableRelativeLayout { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.CustomList { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.CustomListSingleOnly { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.DialogHandler { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.GlobalNames { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.GroupProfile { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.ImageSaver { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.Like_Interested_Going_Bool { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.Like_Interested_Going_Cnt { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.NetworkConnectionCheck { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.OurClient { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.SquareImageView { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.ThreeHFourthWImageView { *; }
-keep class  com.myapp.unknown.iNTCON.OtherClasses.UserProfile { *; }

-keep class  com.myapp.unknown.iNTCON.ViewHolders.** { *; }

-keep class  com.myapp.unknown.iNTCON.Preferences.AllPreferences { *; }

-keep class  com.myapp.unknown.iNTCON.SQLiteDataBase.** { *; }

-keep public class com.google.ads.**{
       public *;
    }