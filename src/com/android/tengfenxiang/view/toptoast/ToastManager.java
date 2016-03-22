package com.android.tengfenxiang.view.toptoast;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.WeakHashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * @author Evgeny Shishkin
 */
class ToastManager extends Handler {

	private static final int MESSAGE_DISPLAY = 0xc2007;
	private static final int MESSAGE_ADD_VIEW = 0xc20074dd;
	private static final int MESSAGE_REMOVE = 0xc2007de1;

	private static WeakHashMap<Activity, ToastManager> sManagers;
	private static ReleaseCallbacks sReleaseCallbacks;

	private final Queue<TopToast> msgQueue;
	private Animation inAnimation, outAnimation;

	private ToastManager() {
		msgQueue = new LinkedList<TopToast>();
	}

	/**
	 * @return A {@link ToastManager} instance to be used for given
	 *         {@link android.app.Activity}.
	 */
	static synchronized ToastManager obtain(Activity activity) {
		if (sManagers == null) {
			sManagers = new WeakHashMap<Activity, ToastManager>(1);
		}
		ToastManager manager = sManagers.get(activity);
		if (manager == null) {
			manager = new ToastManager();
			ensureReleaseOnDestroy(activity);
			sManagers.put(activity, manager);
		}

		return manager;
	}

	static void ensureReleaseOnDestroy(Activity activity) {
		if (SDK_INT < ICE_CREAM_SANDWICH) {
			return;
		}
		if (sReleaseCallbacks == null) {
			sReleaseCallbacks = new ReleaseCallbacksIcs();
		}
		sReleaseCallbacks.register(activity.getApplication());
	}

	static synchronized void release(Activity activity) {
		if (sManagers != null) {
			final ToastManager manager = sManagers.remove(activity);
			if (manager != null) {
				manager.clearAllMsg();
			}
		}
	}

	static synchronized void clearAll() {
		if (sManagers != null) {
			final Iterator<ToastManager> iterator = sManagers.values()
					.iterator();
			while (iterator.hasNext()) {
				final ToastManager manager = iterator.next();
				if (manager != null) {
					manager.clearAllMsg();
				}
				iterator.remove();
			}
			sManagers.clear();
		}
	}

	/**
	 * Inserts a {@link TopToast} to be displayed.
	 * 
	 * @param appMsg
	 */
	void add(TopToast appMsg) {
		msgQueue.add(appMsg);
		if (inAnimation == null) {
			inAnimation = AnimationUtils.loadAnimation(appMsg.getActivity(),
					android.R.anim.fade_in);
		}
		if (outAnimation == null) {
			outAnimation = AnimationUtils.loadAnimation(appMsg.getActivity(),
					android.R.anim.fade_out);
		}
		displayMsg();
	}

	/**
	 * Removes all {@link TopToast} from the queue.
	 */
	void clearMsg(TopToast appMsg) {
		if (msgQueue.contains(appMsg)) {
			// Avoid the message from being removed twice.
			removeMessages(MESSAGE_DISPLAY, appMsg);
			removeMessages(MESSAGE_ADD_VIEW, appMsg);
			removeMessages(MESSAGE_REMOVE, appMsg);
			msgQueue.remove(appMsg);
			removeMsg(appMsg);
		}
	}

	/**
	 * Removes all {@link TopToast} from the queue.
	 */
	void clearAllMsg() {
		if (msgQueue != null) {
			msgQueue.clear();
		}
		removeMessages(MESSAGE_DISPLAY);
		removeMessages(MESSAGE_ADD_VIEW);
		removeMessages(MESSAGE_REMOVE);
	}

	/**
	 * Displays the next {@link TopToast} within the queue.
	 */
	private void displayMsg() {
		if (msgQueue.isEmpty()) {
			return;
		}
		// First peek whether the AppMsg is being displayed.
		final TopToast appMsg = msgQueue.peek();
		final Message msg;
		if (!appMsg.isShowing()) {
			// Display the AppMsg
			msg = obtainMessage(MESSAGE_ADD_VIEW);
			msg.obj = appMsg;
			sendMessage(msg);
		} else {
			msg = obtainMessage(MESSAGE_DISPLAY);
			sendMessageDelayed(msg,
					appMsg.getDuration() + inAnimation.getDuration()
							+ outAnimation.getDuration());
		}
	}

	/**
	 * Removes the {@link TopToast}'s view after it's display duration.
	 * 
	 * @param appMsg
	 *            The {@link TopToast} added to a {@link ViewGroup} and should
	 *            be removed.s
	 */
	private void removeMsg(final TopToast appMsg) {
		clearMsg(appMsg);
		final View view = appMsg.getView();
		ViewGroup parent = ((ViewGroup) view.getParent());
		if (parent != null) {
			outAnimation.setAnimationListener(new OutAnimationListener(appMsg));
			view.startAnimation(outAnimation);
			if (appMsg.isFloating()) {
				// Remove the AppMsg from the view's parent.
				parent.removeView(view);
			} else {
				appMsg.getView().setVisibility(View.INVISIBLE);
			}
		}

		Message msg = obtainMessage(MESSAGE_DISPLAY);
		sendMessage(msg);
	}

	private void addMsgToView(TopToast appMsg) {
		View view = appMsg.getView();
		if (view.getParent() == null) {
			appMsg.getActivity().addContentView(view, appMsg.getLayoutParams());
		}
		view.startAnimation(inAnimation);
		if (view.getVisibility() != View.VISIBLE) {
			view.setVisibility(View.VISIBLE);
		}
		final Message msg = obtainMessage(MESSAGE_REMOVE);
		msg.obj = appMsg;
		sendMessageDelayed(msg, appMsg.getDuration());
	}

	@Override
	public void handleMessage(Message msg) {
		final TopToast appMsg;
		switch (msg.what) {
		case MESSAGE_DISPLAY:
			displayMsg();
			break;
		case MESSAGE_ADD_VIEW:
			appMsg = (TopToast) msg.obj;
			addMsgToView(appMsg);
			break;
		case MESSAGE_REMOVE:
			appMsg = (TopToast) msg.obj;
			removeMsg(appMsg);
			break;
		default:
			super.handleMessage(msg);
			break;
		}
	}

	private static class OutAnimationListener implements
			Animation.AnimationListener {

		private final TopToast appMsg;

		private OutAnimationListener(TopToast appMsg) {
			this.appMsg = appMsg;
		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (!appMsg.isFloating()) {
				appMsg.getView().setVisibility(View.GONE);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	}

	interface ReleaseCallbacks {
		void register(Application application);
	}

	@TargetApi(ICE_CREAM_SANDWICH)
	static class ReleaseCallbacksIcs implements ActivityLifecycleCallbacks,
			ReleaseCallbacks {
		private WeakReference<Application> mLastApp;

		public void register(Application app) {
			if (mLastApp != null && mLastApp.get() == app) {
				return; // Already registered with this app
			} else {
				mLastApp = new WeakReference<Application>(app);
			}
			app.registerActivityLifecycleCallbacks(this);
		}

		@Override
		public void onActivityDestroyed(Activity activity) {
			release(activity);
		}

		@Override
		public void onActivityCreated(Activity activity,
				Bundle savedInstanceState) {
		}

		@Override
		public void onActivityStarted(Activity activity) {
		}

		@Override
		public void onActivityResumed(Activity activity) {
		}

		@Override
		public void onActivityPaused(Activity activity) {
		}

		@Override
		public void onActivityStopped(Activity activity) {
		}

		@Override
		public void onActivitySaveInstanceState(Activity activity,
				Bundle outState) {
		}
	}
}