package com.serogen.christmasaddon;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.serogen.christmasaddon", "com.serogen.christmasaddon.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.serogen.christmasaddon", "com.serogen.christmasaddon.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.serogen.christmasaddon.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp = null;
public anywheresoftware.b4a.objects.ScrollViewWrapper _scrollview1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public com.serogen.christmasaddon.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 25;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 26;BA.debugLine="Activity.LoadLayout(\"Christmas_addon\")";
mostCurrent._activity.LoadLayout("Christmas_addon",mostCurrent.activityBA);
 //BA.debugLineNum = 27;BA.debugLine="bmp.InitializeSample(File.DirAssets, \"icon.png\",4";
mostCurrent._bmp.InitializeSample(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"icon.png",(int) (48),(int) (48));
 //BA.debugLineNum = 28;BA.debugLine="BuildScreen";
_buildscreen();
 //BA.debugLineNum = 29;BA.debugLine="ShowDialog";
_showdialog();
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 57;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _buildscreen() throws Exception{
anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
anywheresoftware.b4a.objects.ImageViewWrapper _imageview2 = null;
 //BA.debugLineNum = 37;BA.debugLine="Sub BuildScreen()";
 //BA.debugLineNum = 38;BA.debugLine="Dim ImageView1 As ImageView";
_imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Dim ImageView2 As ImageView";
_imageview2 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Button1.Initialize(\"Button1\")";
mostCurrent._button1.Initialize(mostCurrent.activityBA,"Button1");
 //BA.debugLineNum = 41;BA.debugLine="If GetDefaultLanguage=\"ru\" Then Button1.Text=\"Уст";
if ((_getdefaultlanguage()).equals("ru")) { 
mostCurrent._button1.setText((Object)("Установить"));}
else {
mostCurrent._button1.setText((Object)("Install"));};
 //BA.debugLineNum = 42;BA.debugLine="ImageView1.Initialize(\"\")";
_imageview1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 43;BA.debugLine="ImageView1.Gravity=Gravity.FILL";
_imageview1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 44;BA.debugLine="ImageView1.Bitmap=LoadBitmap(File.DirAssets, \"img";
_imageview1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"img_46002.png").getObject()));
 //BA.debugLineNum = 45;BA.debugLine="ImageView2.Initialize(\"\")";
_imageview2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 46;BA.debugLine="ImageView2.Gravity=Gravity.FILL";
_imageview2.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 47;BA.debugLine="If GetDefaultLanguage=\"ru\" Then ImageView2.Bitmap";
if ((_getdefaultlanguage()).equals("ru")) { 
_imageview2.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Description.png").getObject()));}
else {
_imageview2.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Description2.png").getObject()));};
 //BA.debugLineNum = 48;BA.debugLine="ScrollView1.Width=100%x";
mostCurrent._scrollview1.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 49;BA.debugLine="ScrollView1.Height=100%y";
mostCurrent._scrollview1.setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 50;BA.debugLine="ScrollView1.Panel.Width=100%x";
mostCurrent._scrollview1.getPanel().setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 51;BA.debugLine="ScrollView1.Panel.Height=200%y";
mostCurrent._scrollview1.getPanel().setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (200),mostCurrent.activityBA));
 //BA.debugLineNum = 52;BA.debugLine="ScrollView1.Panel.AddView(ImageView1,0,0,100%x,77";
mostCurrent._scrollview1.getPanel().AddView((android.view.View)(_imageview1.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (77),mostCurrent.activityBA));
 //BA.debugLineNum = 53;BA.debugLine="ScrollView1.Panel.AddView(Button1,15%x,200%y-17%x";
mostCurrent._scrollview1.getPanel().AddView((android.view.View)(mostCurrent._button1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (15),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (200),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (17),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (70),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (15),mostCurrent.activityBA));
 //BA.debugLineNum = 54;BA.debugLine="ScrollView1.Panel.AddView(ImageView2,0,77%x,100%x";
mostCurrent._scrollview1.getPanel().AddView((android.view.View)(_imageview2.getObject()),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (77),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (200),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (96),mostCurrent.activityBA)));
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 64;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 65;BA.debugLine="CopyFiles";
_copyfiles();
 //BA.debugLineNum = 66;BA.debugLine="End Sub";
return "";
}
public static String  _copyfiles() throws Exception{
String _targetdir2 = "";
 //BA.debugLineNum = 68;BA.debugLine="Sub CopyFiles";
 //BA.debugLineNum = 69;BA.debugLine="Dim TargetDir2 As String = File.DirRootExternal &";
_targetdir2 = anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/games/com.mojang/behavior_packs/";
 //BA.debugLineNum = 70;BA.debugLine="Try";
try { //BA.debugLineNum = 71;BA.debugLine="If File.Exists(TargetDir2, \"\") = False Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_targetdir2,"")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 72;BA.debugLine="If GetDefaultLanguage=\"ru\" Then Msgbox(\"Майнкраф";
if ((_getdefaultlanguage()).equals("ru")) { 
anywheresoftware.b4a.keywords.Common.Msgbox("Майнкрафт не установлен на вашем устройстве","Ошибка",mostCurrent.activityBA);}
else {
anywheresoftware.b4a.keywords.Common.Msgbox("Minecraft is not installed on your device","Error",mostCurrent.activityBA);};
 //BA.debugLineNum = 73;BA.debugLine="Return";
if (true) return "";
 }else {
 //BA.debugLineNum = 75;BA.debugLine="If File.Exists(TargetDir2 & \"Behaviour_Christmas_";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_targetdir2+"Behaviour_Christmas_Addon","")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 76;BA.debugLine="MakeDirs";
_makedirs();
 }else {
 //BA.debugLineNum = 78;BA.debugLine="If GetDefaultLanguage=\"ru\" Then Msgbox(\"Аддон у";
if ((_getdefaultlanguage()).equals("ru")) { 
anywheresoftware.b4a.keywords.Common.Msgbox("Аддон уже распакован","",mostCurrent.activityBA);}
else {
anywheresoftware.b4a.keywords.Common.Msgbox("Addon already unpacked","",mostCurrent.activityBA);};
 };
 };
 } 
       catch (Exception e58) {
			processBA.setLastException(e58); //BA.debugLineNum = 82;BA.debugLine="If GetDefaultLanguage=\"ru\" Then Msgbox(\"Что-то п";
if ((_getdefaultlanguage()).equals("ru")) { 
anywheresoftware.b4a.keywords.Common.Msgbox("Что-то пошло не так :( Вот в чём проблема "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA))+". С Новым Годом!",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);}
else {
anywheresoftware.b4a.keywords.Common.Msgbox("Something went wrong:(. That's the problem: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA))+" I wish you Merry Christmas and Happy New Year!",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);};
 };
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _getdefaultlanguage() throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
 //BA.debugLineNum = 31;BA.debugLine="Sub GetDefaultLanguage As String";
 //BA.debugLineNum = 32;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 33;BA.debugLine="r.Target=r.RunStaticMethod(\"java.util.Locale\",\"ge";
_r.Target = _r.RunStaticMethod("java.util.Locale","getDefault",(Object[])(anywheresoftware.b4a.keywords.Common.Null),(String[])(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 34;BA.debugLine="Return r.RunMethod(\"getLanguage\")";
if (true) return BA.ObjectToString(_r.RunMethod("getLanguage"));
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 20;BA.debugLine="Dim bmp As Bitmap";
mostCurrent._bmp = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim ScrollView1 As ScrollView";
mostCurrent._scrollview1 = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="End Sub";
return "";
}
public static String  _makedirs() throws Exception{
String _dir = "";
byte _i = (byte)0;
anywheresoftware.b4a.objects.ProgressBarWrapper _pbar = null;
 //BA.debugLineNum = 86;BA.debugLine="Sub MakeDirs";
 //BA.debugLineNum = 87;BA.debugLine="Dim Dir As String = File.DirRootExternal & \"/game";
_dir = anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/games/com.mojang/behavior_packs/";
 //BA.debugLineNum = 88;BA.debugLine="Dim I As Byte";
_i = (byte)0;
 //BA.debugLineNum = 89;BA.debugLine="Dim PBar As ProgressBar";
_pbar = new anywheresoftware.b4a.objects.ProgressBarWrapper();
 //BA.debugLineNum = 90;BA.debugLine="PBar.Initialize(\"\")";
_pbar.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 91;BA.debugLine="ScrollView1.Panel.AddView(PBar,0,200%y-5%x,100%x,";
mostCurrent._scrollview1.getPanel().AddView((android.view.View)(_pbar.getObject()),(int) (0),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (200),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 92;BA.debugLine="File.MakeDir(Dir, \"Behaviour_Christmas_Addon\")";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir,"Behaviour_Christmas_Addon");
 //BA.debugLineNum = 93;BA.debugLine="File.Copy(File.DirAssets,\"pack_icon_b.png\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"pack_icon_b.png",_dir+"Behaviour_Christmas_Addon","pack_icon.png");
 //BA.debugLineNum = 94;BA.debugLine="File.Copy(File.DirAssets,\"pack_manifest_b.json\",D";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"pack_manifest_b.json",_dir+"Behaviour_Christmas_Addon","pack_manifest.json");
 //BA.debugLineNum = 96;BA.debugLine="File.MakeDir(Dir & \"Behaviour_Christmas_Addon\",\"e";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Behaviour_Christmas_Addon","entities");
 //BA.debugLineNum = 97;BA.debugLine="File.Copy(File.DirAssets,\"pig_e.json\",Dir & \"Beha";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"pig_e.json",_dir+"Behaviour_Christmas_Addon/entities","pig.json");
 //BA.debugLineNum = 98;BA.debugLine="File.Copy(File.DirAssets,\"snowgolem.json\",Dir & \"";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"snowgolem.json",_dir+"Behaviour_Christmas_Addon/entities","snowgolem.json");
 //BA.debugLineNum = 99;BA.debugLine="PBar.Progress=10*I";
_pbar.setProgress((int) (10*_i));
 //BA.debugLineNum = 100;BA.debugLine="I=I+1";
_i = (byte) (_i+1);
 //BA.debugLineNum = 101;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 102;BA.debugLine="File.MakeDir(Dir & \"Behaviour_Christmas_Addon\",\"l";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Behaviour_Christmas_Addon","loot_tables");
 //BA.debugLineNum = 103;BA.debugLine="File.Copy(File.DirAssets,\"empty.json\",Dir & \"Beha";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"empty.json",_dir+"Behaviour_Christmas_Addon/loot_tables","empty.json");
 //BA.debugLineNum = 104;BA.debugLine="File.MakeDir(Dir & \"Behaviour_Christmas_Addon/loo";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Behaviour_Christmas_Addon/loot_tables","entities");
 //BA.debugLineNum = 105;BA.debugLine="File.Copy(File.DirAssets,\"pig_l.json\",Dir & \"Beha";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"pig_l.json",_dir+"Behaviour_Christmas_Addon/loot_tables/entities","pig.json");
 //BA.debugLineNum = 107;BA.debugLine="Dir = File.DirRootExternal & \"/games/com.mojang/r";
_dir = anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/games/com.mojang/resource_packs/";
 //BA.debugLineNum = 109;BA.debugLine="File.MakeDir(Dir, \"Resource_Christmas_Addon\")";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir,"Resource_Christmas_Addon");
 //BA.debugLineNum = 110;BA.debugLine="File.Copy(File.DirAssets,\"pack_icon.png\",Dir & \"R";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"pack_icon.png",_dir+"Resource_Christmas_Addon","pack_icon.png");
 //BA.debugLineNum = 111;BA.debugLine="File.Copy(File.DirAssets,\"pack_manifest.json\",Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"pack_manifest.json",_dir+"Resource_Christmas_Addon","pack_manifest.json");
 //BA.debugLineNum = 112;BA.debugLine="File.Copy(File.DirAssets,\"sounds.json\",Dir & \"Res";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"sounds.json",_dir+"Resource_Christmas_Addon","sounds.json");
 //BA.debugLineNum = 113;BA.debugLine="File.Copy(File.DirAssets,\"terrain_texture.json\",D";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"terrain_texture.json",_dir+"Resource_Christmas_Addon","terrain_texture.json");
 //BA.debugLineNum = 114;BA.debugLine="PBar.Progress=10*I";
_pbar.setProgress((int) (10*_i));
 //BA.debugLineNum = 115;BA.debugLine="I=I+1";
_i = (byte) (_i+1);
 //BA.debugLineNum = 116;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 117;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon\",\"fo";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon","font");
 //BA.debugLineNum = 118;BA.debugLine="File.Copy(File.DirAssets,\"glyph_04.png\",Dir & \"Re";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"glyph_04.png",_dir+"Resource_Christmas_Addon/font","glyph_04.png");
 //BA.debugLineNum = 120;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon\",\"mo";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon","models");
 //BA.debugLineNum = 121;BA.debugLine="File.Copy(File.DirAssets,\"mobs.json\",Dir & \"Resou";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"mobs.json",_dir+"Resource_Christmas_Addon/models","mobs.json");
 //BA.debugLineNum = 123;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon\",\"te";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon","texts");
 //BA.debugLineNum = 124;BA.debugLine="File.Copy(File.DirAssets,\"en_GB.lang\",Dir & \"Reso";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"en_GB.lang",_dir+"Resource_Christmas_Addon/texts","en_GB.lang");
 //BA.debugLineNum = 125;BA.debugLine="File.Copy(File.DirAssets,\"en_US.lang\",Dir & \"Reso";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"en_US.lang",_dir+"Resource_Christmas_Addon/texts","en_US.lang");
 //BA.debugLineNum = 126;BA.debugLine="File.Copy(File.DirAssets,\"ru_RU.lang\",Dir & \"Reso";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ru_RU.lang",_dir+"Resource_Christmas_Addon/texts","ru_RU.lang");
 //BA.debugLineNum = 127;BA.debugLine="PBar.Progress=10*I";
_pbar.setProgress((int) (10*_i));
 //BA.debugLineNum = 128;BA.debugLine="I=I+1";
_i = (byte) (_i+1);
 //BA.debugLineNum = 129;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 130;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon\",\"te";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon","textures");
 //BA.debugLineNum = 131;BA.debugLine="File.Copy(File.DirAssets,\"flame_atlas.png\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"flame_atlas.png",_dir+"Resource_Christmas_Addon/textures","flame_atlas.png");
 //BA.debugLineNum = 132;BA.debugLine="File.Copy(File.DirAssets,\"flipbook_textures.json\"";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"flipbook_textures.json",_dir+"Resource_Christmas_Addon/textures","flipbook_textures.json");
 //BA.debugLineNum = 134;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures","blocks");
 //BA.debugLineNum = 135;BA.debugLine="File.Copy(File.DirAssets,\"bed_feet_end.png\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bed_feet_end.png",_dir+"Resource_Christmas_Addon/textures/blocks","bed_feet_end.png");
 //BA.debugLineNum = 136;BA.debugLine="File.Copy(File.DirAssets,\"bed_feet_side.png\",Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bed_feet_side.png",_dir+"Resource_Christmas_Addon/textures/blocks","bed_feet_side.png");
 //BA.debugLineNum = 137;BA.debugLine="File.Copy(File.DirAssets,\"bed_feet_top.png\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bed_feet_top.png",_dir+"Resource_Christmas_Addon/textures/blocks","bed_feet_top.png");
 //BA.debugLineNum = 138;BA.debugLine="File.Copy(File.DirAssets,\"bed_head_end.png\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bed_head_end.png",_dir+"Resource_Christmas_Addon/textures/blocks","bed_head_end.png");
 //BA.debugLineNum = 139;BA.debugLine="File.Copy(File.DirAssets,\"bed_head_side.png\",Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bed_head_side.png",_dir+"Resource_Christmas_Addon/textures/blocks","bed_head_side.png");
 //BA.debugLineNum = 140;BA.debugLine="File.Copy(File.DirAssets,\"bed_head_top.png\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bed_head_top.png",_dir+"Resource_Christmas_Addon/textures/blocks","bed_head_top.png");
 //BA.debugLineNum = 141;BA.debugLine="File.Copy(File.DirAssets,\"chest_front.png\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"chest_front.png",_dir+"Resource_Christmas_Addon/textures/blocks","chest_front.png");
 //BA.debugLineNum = 142;BA.debugLine="File.Copy(File.DirAssets,\"chest_side.png\",Dir & \"";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"chest_side.png",_dir+"Resource_Christmas_Addon/textures/blocks","chest_side.png");
 //BA.debugLineNum = 143;BA.debugLine="File.Copy(File.DirAssets,\"chest_top.png\",Dir & \"R";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"chest_top.png",_dir+"Resource_Christmas_Addon/textures/blocks","chest_top.png");
 //BA.debugLineNum = 144;BA.debugLine="File.Copy(File.DirAssets,\"door_acacia_upper.png\",";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"door_acacia_upper.png",_dir+"Resource_Christmas_Addon/textures/blocks","door_acacia_upper.png");
 //BA.debugLineNum = 145;BA.debugLine="File.Copy(File.DirAssets,\"door_birch_upper.png\",D";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"door_birch_upper.png",_dir+"Resource_Christmas_Addon/textures/blocks","door_birch_upper.png");
 //BA.debugLineNum = 146;BA.debugLine="File.Copy(File.DirAssets,\"door_dark_oak_upper.png";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"door_dark_oak_upper.png",_dir+"Resource_Christmas_Addon/textures/blocks","door_dark_oak_upper.png");
 //BA.debugLineNum = 147;BA.debugLine="File.Copy(File.DirAssets,\"door_iron_upper.png\",Di";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"door_iron_upper.png",_dir+"Resource_Christmas_Addon/textures/blocks","door_iron_upper.png");
 //BA.debugLineNum = 148;BA.debugLine="File.Copy(File.DirAssets,\"door_jungle_upper.png\",";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"door_jungle_upper.png",_dir+"Resource_Christmas_Addon/textures/blocks","door_jungle_upper.png");
 //BA.debugLineNum = 149;BA.debugLine="File.Copy(File.DirAssets,\"door_spruce_upper.png\",";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"door_spruce_upper.png",_dir+"Resource_Christmas_Addon/textures/blocks","door_spruce_upper.png");
 //BA.debugLineNum = 150;BA.debugLine="File.Copy(File.DirAssets,\"door_wood_upper.png\",Di";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"door_wood_upper.png",_dir+"Resource_Christmas_Addon/textures/blocks","door_wood_upper.png");
 //BA.debugLineNum = 151;BA.debugLine="File.Copy(File.DirAssets,\"double_plant_grass_bott";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"double_plant_grass_bottom.tga",_dir+"Resource_Christmas_Addon/textures/blocks","double_plant_grass_bottom.tga");
 //BA.debugLineNum = 152;BA.debugLine="File.Copy(File.DirAssets,\"double_plant_grass_top.";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"double_plant_grass_top.tga",_dir+"Resource_Christmas_Addon/textures/blocks","double_plant_grass_top.tga");
 //BA.debugLineNum = 153;BA.debugLine="File.Copy(File.DirAssets,\"fire_0.png\",Dir & \"Reso";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fire_0.png",_dir+"Resource_Christmas_Addon/textures/blocks","fire_0.png");
 //BA.debugLineNum = 154;BA.debugLine="File.Copy(File.DirAssets,\"fire_0_placeholder.png\"";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fire_0_placeholder.png",_dir+"Resource_Christmas_Addon/textures/blocks","fire_0_placeholder.png");
 //BA.debugLineNum = 155;BA.debugLine="File.Copy(File.DirAssets,\"fire_1.png\",Dir & \"Reso";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fire_1.png",_dir+"Resource_Christmas_Addon/textures/blocks","fire_1.png");
 //BA.debugLineNum = 156;BA.debugLine="File.Copy(File.DirAssets,\"fire_1_placeholder.png\"";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fire_1_placeholder.png",_dir+"Resource_Christmas_Addon/textures/blocks","fire_1_placeholder.png");
 //BA.debugLineNum = 157;BA.debugLine="File.Copy(File.DirAssets,\"fire_layer_0.png.mcmeta";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fire_layer_0.png.mcmeta",_dir+"Resource_Christmas_Addon/textures/blocks","fire_layer_0.png.mcmeta");
 //BA.debugLineNum = 158;BA.debugLine="File.Copy(File.DirAssets,\"fire_layer_1.png.mcmeta";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fire_layer_1.png.mcmeta",_dir+"Resource_Christmas_Addon/textures/blocks","fire_layer_1.png.mcmeta");
 //BA.debugLineNum = 159;BA.debugLine="File.Copy(File.DirAssets,\"glass.png\",Dir & \"Resou";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"glass.png",_dir+"Resource_Christmas_Addon/textures/blocks","glass.png");
 //BA.debugLineNum = 160;BA.debugLine="File.Copy(File.DirAssets,\"grass_side.tga\",Dir & \"";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"grass_side.tga",_dir+"Resource_Christmas_Addon/textures/blocks","grass_side.tga");
 //BA.debugLineNum = 161;BA.debugLine="File.Copy(File.DirAssets,\"grass_top.png\",Dir & \"R";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"grass_top.png",_dir+"Resource_Christmas_Addon/textures/blocks","grass_top.png");
 //BA.debugLineNum = 162;BA.debugLine="File.Copy(File.DirAssets,\"leaves_acacia.tga\",Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_acacia.tga",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_acacia.tga");
 //BA.debugLineNum = 163;BA.debugLine="File.Copy(File.DirAssets,\"leaves_acacia_opaque.pn";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_acacia_opaque.png",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_acacia_opaque.png");
 //BA.debugLineNum = 164;BA.debugLine="File.Copy(File.DirAssets,\"leaves_big_oak.tga\",Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_big_oak.tga",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_big_oak.tga");
 //BA.debugLineNum = 166;BA.debugLine="File.Copy(File.DirAssets,\"leaves_birch.tga\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_birch.tga",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_birch.tga");
 //BA.debugLineNum = 167;BA.debugLine="File.Copy(File.DirAssets,\"leaves_birch_opaque.png";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_birch_opaque.png",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_birch_opaque.png");
 //BA.debugLineNum = 168;BA.debugLine="File.Copy(File.DirAssets,\"leaves_jungle.tga\",Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_jungle.tga",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_jungle.tga");
 //BA.debugLineNum = 169;BA.debugLine="File.Copy(File.DirAssets,\"leaves_jungle_opaque.pn";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_jungle_opaque.png",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_jungle_opaque.png");
 //BA.debugLineNum = 170;BA.debugLine="File.Copy(File.DirAssets,\"leaves_oak.tga\",Dir & \"";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_oak.tga",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_oak.tga");
 //BA.debugLineNum = 171;BA.debugLine="File.Copy(File.DirAssets,\"leaves_oak_opaque.png\",";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_oak_opaque.png",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_oak_opaque.png");
 //BA.debugLineNum = 172;BA.debugLine="File.Copy(File.DirAssets,\"leaves_spruce.tga\",Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_spruce.tga",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_spruce.tga");
 //BA.debugLineNum = 173;BA.debugLine="File.Copy(File.DirAssets,\"leaves_spruce_opaque.pn";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaves_spruce_opaque.png",_dir+"Resource_Christmas_Addon/textures/blocks","leaves_spruce_opaque.png");
 //BA.debugLineNum = 174;BA.debugLine="File.Copy(File.DirAssets,\"sea_lantern.png\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"sea_lantern.png",_dir+"Resource_Christmas_Addon/textures/blocks","sea_lantern.png");
 //BA.debugLineNum = 175;BA.debugLine="File.Copy(File.DirAssets,\"tallgrass.tga\",Dir & \"R";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"tallgrass.tga",_dir+"Resource_Christmas_Addon/textures/blocks","tallgrass.tga");
 //BA.debugLineNum = 176;BA.debugLine="File.Copy(File.DirAssets,\"vine.png\",Dir & \"Resour";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vine.png",_dir+"Resource_Christmas_Addon/textures/blocks","vine.png");
 //BA.debugLineNum = 177;BA.debugLine="File.Copy(File.DirAssets,\"grass_side_carried.png\"";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"grass_side_carried.png",_dir+"Resource_Christmas_Addon/textures/blocks","grass_side_carried.png");
 //BA.debugLineNum = 178;BA.debugLine="PBar.Progress=10*I";
_pbar.setProgress((int) (10*_i));
 //BA.debugLineNum = 179;BA.debugLine="I=I+1";
_i = (byte) (_i+1);
 //BA.debugLineNum = 180;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 181;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures","colormap");
 //BA.debugLineNum = 182;BA.debugLine="File.Copy(File.DirAssets,\"birch.png\",Dir & \"Resou";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"birch.png",_dir+"Resource_Christmas_Addon/textures/colormap","birch.png");
 //BA.debugLineNum = 183;BA.debugLine="File.Copy(File.DirAssets,\"evergreen.png\",Dir & \"R";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"evergreen.png",_dir+"Resource_Christmas_Addon/textures/colormap","evergreen.png");
 //BA.debugLineNum = 184;BA.debugLine="File.Copy(File.DirAssets,\"foliage.png\",Dir & \"Res";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"foliage.png",_dir+"Resource_Christmas_Addon/textures/colormap","foliage.png");
 //BA.debugLineNum = 185;BA.debugLine="File.Copy(File.DirAssets,\"grass.png\",Dir & \"Resou";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"grass.png",_dir+"Resource_Christmas_Addon/textures/colormap","grass.png");
 //BA.debugLineNum = 187;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures","entity");
 //BA.debugLineNum = 188;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures/entity","chest");
 //BA.debugLineNum = 189;BA.debugLine="File.Copy(File.DirAssets,\"double_normal.png\",Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"double_normal.png",_dir+"Resource_Christmas_Addon/textures/entity/chest","double_normal.png");
 //BA.debugLineNum = 190;BA.debugLine="File.Copy(File.DirAssets,\"normal.png\",Dir & \"Reso";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"normal.png",_dir+"Resource_Christmas_Addon/textures/entity/chest","normal.png");
 //BA.debugLineNum = 191;BA.debugLine="PBar.Progress=10*I";
_pbar.setProgress((int) (10*_i));
 //BA.debugLineNum = 192;BA.debugLine="I=I+1";
_i = (byte) (_i+1);
 //BA.debugLineNum = 193;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 194;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures/entity","pig");
 //BA.debugLineNum = 195;BA.debugLine="File.Copy(File.DirAssets,\"pig.png\",Dir & \"Resourc";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"pig.png",_dir+"Resource_Christmas_Addon/textures/entity/pig","pig.png");
 //BA.debugLineNum = 197;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures/entity","skulls");
 //BA.debugLineNum = 198;BA.debugLine="File.Copy(File.DirAssets,\"creeper.png\",Dir & \"Res";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"creeper.png",_dir+"Resource_Christmas_Addon/textures/entity/skulls","creeper.png");
 //BA.debugLineNum = 199;BA.debugLine="File.Copy(File.DirAssets,\"skeleton.png\",Dir & \"Re";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"skeleton.png",_dir+"Resource_Christmas_Addon/textures/entity/skulls","skeleton.png");
 //BA.debugLineNum = 200;BA.debugLine="File.Copy(File.DirAssets,\"wither_skeleton.png\",Di";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"wither_skeleton.png",_dir+"Resource_Christmas_Addon/textures/entity/skulls","wither_skeleton.png");
 //BA.debugLineNum = 201;BA.debugLine="File.Copy(File.DirAssets,\"zombie.png\",Dir & \"Reso";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"zombie.png",_dir+"Resource_Christmas_Addon/textures/entity/skulls","zombie.png");
 //BA.debugLineNum = 202;BA.debugLine="PBar.Progress=10*I";
_pbar.setProgress((int) (10*_i));
 //BA.debugLineNum = 203;BA.debugLine="I=I+1";
_i = (byte) (_i+1);
 //BA.debugLineNum = 204;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 205;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures","gui");
 //BA.debugLineNum = 206;BA.debugLine="File.Copy(File.DirAssets,\"gui.png\",Dir & \"Resourc";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"gui.png",_dir+"Resource_Christmas_Addon/textures/gui","gui.png");
 //BA.debugLineNum = 208;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures","items");
 //BA.debugLineNum = 209;BA.debugLine="File.Copy(File.DirAssets,\"bed.png\",Dir & \"Resourc";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bed.png",_dir+"Resource_Christmas_Addon/textures/items","b ed.png");
 //BA.debugLineNum = 210;BA.debugLine="File.Copy(File.DirAssets,\"egg_pig.png\",Dir & \"Res";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"egg_pig.png",_dir+"Resource_Christmas_Addon/textures/items","egg_pig.png");
 //BA.debugLineNum = 211;BA.debugLine="File.Copy(File.DirAssets,\"painting.png\",Dir & \"Re";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"painting.png",_dir+"Resource_Christmas_Addon/textures/items","painting.png");
 //BA.debugLineNum = 212;BA.debugLine="File.Copy(File.DirAssets,\"skull_creeper.png\",Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"skull_creeper.png",_dir+"Resource_Christmas_Addon/textures/items","skull_creeper.png");
 //BA.debugLineNum = 213;BA.debugLine="File.Copy(File.DirAssets,\"skull_skeleton.png\",Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"skull_skeleton.png",_dir+"Resource_Christmas_Addon/textures/items","skull_skeleton.png");
 //BA.debugLineNum = 214;BA.debugLine="File.Copy(File.DirAssets,\"skull_wither.png\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"skull_wither.png",_dir+"Resource_Christmas_Addon/textures/items","skull_wither.png");
 //BA.debugLineNum = 215;BA.debugLine="File.Copy(File.DirAssets,\"skull_zombie.png\",Dir &";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"skull_zombie.png",_dir+"Resource_Christmas_Addon/textures/items","skull_zombie.png");
 //BA.debugLineNum = 217;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures","models");
 //BA.debugLineNum = 218;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures/models","armor");
 //BA.debugLineNum = 219;BA.debugLine="File.Copy(File.DirAssets,\"chain_1.png\",Dir & \"Res";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"chain_1.png",_dir+"Resource_Christmas_Addon/textures/models/armor","chain_1.png");
 //BA.debugLineNum = 220;BA.debugLine="File.Copy(File.DirAssets,\"diamond_1.png\",Dir & \"R";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"diamond_1.png",_dir+"Resource_Christmas_Addon/textures/models/armor","diamond_1.png");
 //BA.debugLineNum = 221;BA.debugLine="File.Copy(File.DirAssets,\"gold_1.png\",Dir & \"Reso";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"gold_1.png",_dir+"Resource_Christmas_Addon/textures/models/armor","gold_1.png");
 //BA.debugLineNum = 222;BA.debugLine="File.Copy(File.DirAssets,\"iron_1.png\",Dir & \"Reso";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"iron_1.png",_dir+"Resource_Christmas_Addon/textures/models/armor","iron_1.png");
 //BA.debugLineNum = 223;BA.debugLine="File.Copy(File.DirAssets,\"leather_1.tga\",Dir & \"R";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leather_1.tga",_dir+"Resource_Christmas_Addon/textures/models/armor","leather_1.tga");
 //BA.debugLineNum = 224;BA.debugLine="PBar.Progress=10*I";
_pbar.setProgress((int) (10*_i));
 //BA.debugLineNum = 225;BA.debugLine="I=I+1";
_i = (byte) (_i+1);
 //BA.debugLineNum = 226;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 227;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures","painting");
 //BA.debugLineNum = 228;BA.debugLine="File.Copy(File.DirAssets,\"kz.png\",Dir & \"Resource";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"kz.png",_dir+"Resource_Christmas_Addon/textures/painting","kz.png");
 //BA.debugLineNum = 229;BA.debugLine="File.MakeDir(Dir & \"Resource_Christmas_Addon/text";
anywheresoftware.b4a.keywords.Common.File.MakeDir(_dir+"Resource_Christmas_Addon/textures","particle");
 //BA.debugLineNum = 230;BA.debugLine="File.Copy(File.DirAssets,\"particles.png\",Dir & \"R";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"particles.png",_dir+"Resource_Christmas_Addon/textures/particle","particles.png");
 //BA.debugLineNum = 231;BA.debugLine="If GetDefaultLanguage=\"ru\" Then ToastMessageShow(";
if ((_getdefaultlanguage()).equals("ru")) { 
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Готово",anywheresoftware.b4a.keywords.Common.True);}
else {
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Done",anywheresoftware.b4a.keywords.Common.True);};
 //BA.debugLineNum = 232;BA.debugLine="PBar.RemoveView";
_pbar.RemoveView();
 //BA.debugLineNum = 233;BA.debugLine="If GetDefaultLanguage=\"ru\" Then Msgbox(\"Теперь за";
if ((_getdefaultlanguage()).equals("ru")) { 
anywheresoftware.b4a.keywords.Common.Msgbox("Теперь запустите Minecraft и импортируйте аддон","",mostCurrent.activityBA);}
else {
anywheresoftware.b4a.keywords.Common.Msgbox("Now run Minecraft and import the addon","",mostCurrent.activityBA);};
 //BA.debugLineNum = 234;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _showdialog() throws Exception{
anywheresoftware.b4a.agraham.dialogs.InputDialog.CustomDialog _cd = null;
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
anywheresoftware.b4a.objects.ImageViewWrapper _img = null;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bgnd = null;
anywheresoftware.b4a.objects.LabelWrapper _lbl1 = null;
int _ii = 0;
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 236;BA.debugLine="Sub ShowDialog";
 //BA.debugLineNum = 237;BA.debugLine="Dim cd As CustomDialog";
_cd = new anywheresoftware.b4a.agraham.dialogs.InputDialog.CustomDialog();
 //BA.debugLineNum = 238;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 239;BA.debugLine="pnl.Initialize(\"pnl\")";
_pnl.Initialize(mostCurrent.activityBA,"pnl");
 //BA.debugLineNum = 240;BA.debugLine="Dim img As ImageView";
_img = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 241;BA.debugLine="img.Initialize(\"\")";
_img.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 242;BA.debugLine="Dim bgnd As BitmapDrawable";
_bgnd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 243;BA.debugLine="bgnd.Initialize(LoadBitmap(File.DirAssets, \"he";
_bgnd.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"head.png").getObject()));
 //BA.debugLineNum = 244;BA.debugLine="img.Background = bgnd";
_img.setBackground((android.graphics.drawable.Drawable)(_bgnd.getObject()));
 //BA.debugLineNum = 245;BA.debugLine="Dim lbl1 As Label";
_lbl1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 246;BA.debugLine="lbl1.Initialize(\"\")";
_lbl1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 247;BA.debugLine="lbl1.TextColor = Colors.White";
_lbl1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 248;BA.debugLine="lbl1.TextSize = 20";
_lbl1.setTextSize((float) (20));
 //BA.debugLineNum = 249;BA.debugLine="pnl.AddView(img,0, 0, 77%x, 20%y)";
_pnl.AddView((android.view.View)(_img.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (77),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (20),mostCurrent.activityBA));
 //BA.debugLineNum = 250;BA.debugLine="pnl.AddView(lbl1, 0, 20%y, 77%x, 50%y)";
_pnl.AddView((android.view.View)(_lbl1.getObject()),(int) (0),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (20),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (77),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 251;BA.debugLine="cd.AddView(pnl, 5%x, 0%y, 77%x, 70%y)";
_cd.AddView((android.view.View)(_pnl.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (77),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (70),mostCurrent.activityBA));
 //BA.debugLineNum = 252;BA.debugLine="Dim ii As Int";
_ii = 0;
 //BA.debugLineNum = 253;BA.debugLine="If GetDefaultLanguage=\"ru\" Then";
if ((_getdefaultlanguage()).equals("ru")) { 
 //BA.debugLineNum = 254;BA.debugLine="lbl1.Text = \"Внимание! Данное приложение устарел";
_lbl1.setText((Object)("Внимание! Данное приложение устарело. Рекомендуем Вам скачать обновлённую версию приложения 'Новогодний Аддон 2'. В новой версии исправлены многие ошибки, а сам аддон значительно улучшен"));
 //BA.debugLineNum = 255;BA.debugLine="ii = cd.Show(\"Доступна новая версия!\", \"Перейти\"";
_ii = _cd.Show("Доступна новая версия!","Перейти","Отмена","",mostCurrent.activityBA,(android.graphics.Bitmap)(mostCurrent._bmp.getObject()));
 }else {
 //BA.debugLineNum = 257;BA.debugLine="lbl1.Text = \"Attention! This application is depr";
_lbl1.setText((Object)("Attention! This application is deprecated. We recommend that you download the updated version of the application 'Christmas Add-on 2'. In the new version many bugs are fixed, And the addon itself Is significantly improved"));
 //BA.debugLineNum = 258;BA.debugLine="ii = cd.Show(\"A new version is available!\", \"Go\"";
_ii = _cd.Show("A new version is available!","Go","Cancel","",mostCurrent.activityBA,(android.graphics.Bitmap)(mostCurrent._bmp.getObject()));
 };
 //BA.debugLineNum = 261;BA.debugLine="If ii = -1 Then";
if (_ii==-1) { 
 //BA.debugLineNum = 262;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 263;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"http://play.google.";
_i.Initialize(_i.ACTION_VIEW,"http://play.google.com/store/apps/details?id=com.nesterov.christmasaddon");
 //BA.debugLineNum = 264;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 };
 //BA.debugLineNum = 266;BA.debugLine="End Sub";
return "";
}
}
