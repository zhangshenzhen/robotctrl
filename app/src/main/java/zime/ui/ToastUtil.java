package zime.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class ToastUtil {

	private static Toast toast;

	public static void showTextLong(Context context, String text) {
		if (toast == null) {

			toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		}
		else
		{
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_LONG);
		}

		View view = toast.getView();
		view.setBackgroundResource(0);
		toast.setGravity(Gravity.TOP | Gravity.RIGHT , 0, 0);
		toast.setView(view);

		toast.show();
	}

	public static void showTextShort(Context context, String text) {
		if (toast == null) {

			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);

		}
		else
		{
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.show();
	}

	public static void cancelToast() {
		if (toast != null) {

			toast.cancel();

		}
	}

}
