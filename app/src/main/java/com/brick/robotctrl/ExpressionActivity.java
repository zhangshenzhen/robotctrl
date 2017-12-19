package com.brick.robotctrl;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.presentation.ExpressionPresentation;

public class ExpressionActivity extends com.rg2.activity.BaseActivity  {
	private static final String TAG = "ExpressionActivity";
   private ExpressionPresentation mexpressionPresentation;
	private static GifView gifView;
	private int index = 0;
//	UserTimer userTimer = null;
	private static int currentIndex = -1;
	private GestureDetector mGestureDetector;
	private int screenWidth;
	private int screenHeight;

	// DDMS
  /*  public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Debug.stopMethodTracing();
    }*/

	enum EXPRESSION {
		机器人愤怒(R.drawable.fennu, "fennu", 0),
		机器人嘟嘴(R.drawable.duzui, "duzui", 1),
		机器人惊讶(R.drawable.jingya, "jingya", 2),
		机器人花痴(R.drawable.huachi, "huachi", 3),
		机器人可怜(R.drawable.kelian, "kelian", 4),
		机器人可爱(R.drawable.keai, "keshui", 5),
		机器人哭泣(R.drawable.kuqi, "kuqi", 6),
		机器人调皮(R.drawable.tiaopi, "tiaopi", 7),
		机器人委屈(R.drawable.weiqu, "weiqu", 8),
		机器人微笑(R.drawable.weixiao, "weixiao", 9),
		机器人郁闷(R.drawable.yumen, "yumen", 10),
		机器人充电(R.drawable.chongdian, "chongdian", 11);
//		机器人得意(R.drawable.deyi, "deyi", 0),
//		机器人尴尬(R.drawable.ganga, "ganga", 1),
//		机器人好奇(R.drawable.haoqi, "haoqi", 2),
//		机器人怀疑(R.drawable.huaiyi, "huaiyi", 3),
//		机器人坏笑(R.drawable.huaixiao, "huaixiao", 4),
//		机器人惊讶(R.drawable.jingya, "jingya", 5),
//		机器人开心(R.drawable.kaixin, "kaixin", 6),
//		机器人瞌睡(R.drawable.keshui, "keshui", 7),
//		机器人可爱(R.drawable.keai, "keai", 8),
//		机器人可怜(R.drawable.kelian, "kelian", 9),
//		机器人哭泣(R.drawable.kuqi, "kuqi", 10),
//		机器人愤怒(R.drawable.fennu, "fennu", 11),
//		机器人亲亲(R.drawable.qinqin, "qinqin", 12),
//		机器人撒娇(R.drawable.sajiao, "sajiao", 13),
//		机器人调皮(R.drawable.tiaopi, "tiaopi", 14),
//		机器人委屈(R.drawable.weiqu, "weiqu", 15),
//		机器人温怒(R.drawable.wennu, "wennu", 16),
//		机器人荫郁(R.drawable.yinyu, "yinyu", 17),
//		机器人责问(R.drawable.zewen, "zewen", 18),
//		机器人眨眼(R.drawable.zhayan, "zhayan", 19),
//		机器人专注(R.drawable.zhuanzhu, "zhuanzhu", 20);

		private int id;
		private String name;
		private int index;
		EXPRESSION(int id, String name, int index) {
			this.id = id;
			this.name = name;
			this.index = index;
		}
		public static int getExpressionSize() {
			int ExpressionSize = 1;
			for ( EXPRESSION exp: EXPRESSION.values()) {
				ExpressionSize++;
			}
			return ExpressionSize;
		}
		public static EXPRESSION getExpression( int index ) {
			for ( EXPRESSION exp: EXPRESSION.values()) {
				if ( index == exp.index ) {
					return exp;
				}
			}
			return EXPRESSION.机器人嘟嘴;//return EXPRESSION.机器人可爱;
		}
		public static EXPRESSION getExpression( String name ) {
			for ( EXPRESSION exp: EXPRESSION.values()) {
				if ( name.equals(exp.name) ) {
					return exp;
				}
			}
			return EXPRESSION.机器人嘟嘴;
		}
	}

	/*public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.gif);
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）
		Log.e("TAG" + "  getDefaultDisplay", "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);
	    Intent intent = getIntent();
  //   	index = intent.getStringExtra("index");
 //    Log.e("express","................"+express);
		index = intent.getIntExtra("index", 6);
  //	  userTimer = new UserTimer();
		gifView = (GifView) findViewById(R.id.gif2);
  // 	gifView.setOnClickListener(this);
		gifView.setGifImageType(GifImageType.COVER);
		//屏幕适配;
//     gifView.setShowDimension(screenWidth, screenHeight);
		  changeExpression(index);
		mGestureDetector = new GestureDetector(this, new ExGestureListener());
	}*/
	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.gif);

		screenWidth = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）
		Log.e("TAG" + "  getDefaultDisplay", "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);
		    Intent intent = getIntent();
		//   	index = intent.getStringExtra("index");
		//    Log.e("express","................"+express);
		index = intent.getIntExtra("index",1);
		 Log.i("express","........<...>........"+index);
		//	  userTimer = new UserTimer();
		gifView = (GifView) findViewById(R.id.gif2);
		// 	gifView.setOnClickListener(this);
		gifView.setGifImageType(GifImageType.COVER);
		//屏幕适配;
//     gifView.setShowDimension(screenWidth, screenHeight);

		 changeExpression(index);
	//	mGestureDetector = new GestureDetector(this, new ExGestureListener());
	}
	@Override
	protected void updatePresentation() {
		// Log.d(TAG, "updatePresentation: ");
		//得到当前route and its presentation display
		MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
				MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
		Display presentationDisplay =  route  !=  null ? route.getPresentationDisplay() : null;
		// 注释 : Dismiss the current presentation if the display has changed.
		if (mexpressionPresentation != null && mexpressionPresentation.getDisplay() !=  presentationDisplay) {
			mexpressionPresentation.dismiss();
			mexpressionPresentation = null;
		}
		if (mexpressionPresentation == null &&  presentationDisplay != null) {
			// Initialise a new Presentation for the Display
			mexpressionPresentation = new ExpressionPresentation(this,  presentationDisplay);
			//把当前的对象引用赋值给BaseActivity中的引用;

				// Log.d(TAG, "updatePresentation: this: "+ this.toString());
			mexpressionPresentation.setOnDismissListener(mOnDismissListener);

			// Try to show the presentation, this might fail if the display has
			// gone away in the mean time
			try {
				mexpressionPresentation.show();
			} catch (WindowManager.InvalidDisplayException ex) {
				// Couldn't show presentation - display was already removed
				// Log.d(TAG, "updatePresentation: failed");
				mexpressionPresentation = null;
			}
		}
	}
/*
* 暂时不用先屏蔽掉*/
	@Override
	protected void initData() {
		gifView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent( ExpressionActivity.this , FunctionSelectActivity.class));
			}
		});
	}

	@Override    //触摸表情返回到选择界面
	protected void initEvent() {

	}

	@Override
	protected void initViewData() {
	}

	int doX,  doY;
	int movX,  movY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		if (mGestureDetector.onTouchEvent(event))
//			return true;

		return super.onTouchEvent(event);
	}

	/** 手势结束 */
	private void endGesture() {
	}

/*	private class ExGestureListener extends GestureDetector.SimpleOnGestureListener {
		long[] mHitsL = new long[5];
		long[] mHitsR = new long[5];

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			Log.d(TAG, "onTouch: x:" + x + "y:" + y);

			if (y < screenHeight / 2) {
				if (x < screenWidth / 2) {
					System.arraycopy(mHitsL, 1, mHitsL, 0, mHitsL.length - 1);
					mHitsL[mHitsL.length - 1] = SystemClock.uptimeMillis();
					//Log.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
					if (mHitsL[0] >= (SystemClock.uptimeMillis() - 3000)) {
						//Log.d(TAG,"onPreferenceClick:进入");
						startActivity(new Intent().setClass(ExpressionActivity.this, SettingsActivity.class));
					}
				} else {
					System.arraycopy(mHitsR, 1, mHitsR, 0, mHitsR.length - 1);
					mHitsR[mHitsR.length - 1] = SystemClock.uptimeMillis();
					//Log.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
					if (mHitsR[0] >= (SystemClock.uptimeMillis() - 3000)) {
						//Log.d(TAG,"onPreferenceClick:进入");
						startActivity(new Intent().setClass(ExpressionActivity.this, AboutActivity.class));
					}
				}
			}
			return true;
		}
	}*/

	public static void changeExpression(int index) {
		Log.d(TAG, "changeExpression: current expression:" + currentIndex + "\tset expression:" + index);
		if ( currentIndex != index ) {
			if (index > EXPRESSION.getExpressionSize()-2) {
				//如果表情角标大于表情枚举个数，从0开始一次递增显示表情

				index = index -(EXPRESSION.getExpressionSize()-1);
				Log.d(TAG, EXPRESSION.getExpressionSize()+"changeExpression 大于枚举长度: ..... " + index);
			   }

				System.gc();
				gifView.setGifImage(EXPRESSION.getExpression(index).id);
				gifView.showAnimation();
				currentIndex = index;
				//Glide.with(RobotApplication.getAppContext()).load(EXPRESSION.getExpression(index).id).into(gifView);
				Log.d(TAG, "changeExpression: ..... " + index);
		}else {
			Log.d(TAG, "changeExpression等于当前表情编号: ..... " + index);
		}
	}

	public void onClick(View v) {
		//clearTimerCount();
		int index = currentIndex;
		index++;
		if ( index >= EXPRESSION.getExpressionSize())
		 index = 0;
		/*//如果表情角标大于表情美剧个数，从0开始一次递增显示表情
		index = index - EXPRESSION.getExpressionSize();*/
		Log.d(TAG, "index - EXPRESSION.getExpressionSize(): ..... " + index);
		changeExpression(index);
	  }

	public static void startAction(Context context, int index) {
		Intent changeMotionIntent = new Intent();
		changeMotionIntent.setClass(context, ExpressionActivity.class);
		changeMotionIntent.putExtra("index", index);
		context.startActivity(changeMotionIntent);
	}
	// 接收尚未写好，需要再try中判断传递的参数是什么类型
	public static void startAction(Context context, String expName) {
		Intent changeMotionIntent = new Intent();
		changeMotionIntent.setClass(context, ExpressionActivity.class);
		changeMotionIntent.putExtra("expName", expName);
		context.startActivity(changeMotionIntent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//updatePresentation();
		Log.i(TAG, "onRestart:updatePresentation 执行了");
	}

	@Override
	protected void onResume() {
		super.onResume();
		//updatePresentation();//父类中已经被调用了;
		Log.i(TAG, "onResume:updatePresentation 执行了");
	}

	@Override
	protected void onPause() {
		super.onPause();
		if ( mexpressionPresentation!= null) {
			mexpressionPresentation.dismiss();
			mexpressionPresentation = null;
		}
		Log.i(TAG, "onPause:updatePresentation 执行了");
	}

	@Override
    protected void onStop() {
        currentIndex = -1;		// restart expression
        super.onStop();
		if ( mexpressionPresentation!= null) {
			mexpressionPresentation.dismiss();
			mexpressionPresentation = null;
		}
        Log.i(TAG, "updatePresentation onStop"+mexpressionPresentation);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "updatePresentation onDestroy"+mexpressionPresentation);
	}
}
