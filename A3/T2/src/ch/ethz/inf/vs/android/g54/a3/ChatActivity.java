package ch.ethz.inf.vs.android.g54.a3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ChatActivity extends Activity {

	ChatManager chat = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);

		chat = ChatManager.getInstance();
	}

	/** Called when the activity is unloaded. */
	@Override
	public void onDestroy() {
		chat.disconnect();
		super.onDestroy();
	}

	public void onDeregisterClick(View w) {
		chat.disconnect();
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

	public void onSendClick(View w) {
		EditText input = (EditText) findViewById(R.id.txt_msg);
		String msg = input.getEditableText().toString();
		chat.sendMsg(msg);
	}
}
