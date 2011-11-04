package ch.ethz.inf.vs.android.g54.a3;

import ch.ethz.inf.vs.android.g54.a3.ChatManager.MessageHandler;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

public class ChatActivity extends Activity {

	ChatManager chat = null;
	MessageHandler h = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);

		chat = ChatManager.getInstance();
		h = chat.getHandler();
		h.setUiActivity(this);
	}

	/** Called when the activity is unloaded. */
	@Override
	public void onDestroy() {
		chat.disconnect();
		super.onDestroy();
	}

	public void onDeregisterClick(View w) {
		finish();
	}

	public void onSendClick(View w) {
		EditText input = (EditText) findViewById(R.id.txt_msg);
		String msg = input.getEditableText().toString();
		chat.sendMsg(msg);
	}
}
