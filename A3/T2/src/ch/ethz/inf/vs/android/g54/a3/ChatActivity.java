package ch.ethz.inf.vs.android.g54.a3;

import android.app.Activity;
import android.os.Bundle;

public class ChatActivity extends Activity {

	ChatManager chat = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// chat = new ChatManager();
	}

	/** Called when the activity is unloaded. */
	@Override
	public void onDestroy() {
		// chat.disconnect();
		super.onDestroy();
	}
}
