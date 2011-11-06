package ch.ethz.inf.vs.android.g54.a3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	ChatManager chat = null;

	public void onConnectClick(View v) {
		EditText txt = (EditText) findViewById(R.id.txt_username);
		String s = txt.getEditableText().toString();
		if (s.length() > 3) {
			chat.setUser(s);
			if (chat.connect()) {
				Intent i = new Intent(this, ChatActivity.class);
				startActivity(i);
			} else {
				Toast t = Toast.makeText(this, "Couldn't connect", Toast.LENGTH_SHORT);
				t.show();
			}
		} else {
			Toast t = Toast.makeText(this, "Invalid user name", Toast.LENGTH_SHORT);
			t.show();
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		chat = ChatManager.getInstance();
	}

	/** Called when the activity is unloaded. */
	@Override
	public void onDestroy() {
		chat.disconnect();
		super.onDestroy();
	}
}