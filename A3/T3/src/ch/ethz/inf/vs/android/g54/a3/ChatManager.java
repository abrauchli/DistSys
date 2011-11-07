package ch.ethz.inf.vs.android.g54.a3;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Management singleton class responsible for
 *  - Connection setup
 *  - Listener thread (inner class)
 *  - Message handling (inner class)
 */
public class ChatManager {
	// Singleton
	private static ChatManager instance;
	public static ChatManager getInstance() {
		if (instance == null) {
			instance = new ChatManager();
		}
		return instance;
	}

	/** Tag applied to messages to identify group messages */
	final String MESSAGE_TAG = "[g54]";
	/** Whitelisted non group senders */
	final String[] SENDER_WHITELIST = { "Server", "QuestionBot", "AnswerBot" };
	final String SERVER = "vswot.inf.ethz.ch";
	/** Max. Message size */
	final int MESSAGE_BUFFER_SIZE = 2048;

	String user = "Llama";
	DatagramSocket sockCmd = null;
	ChatThread chatThread = null;

	MessageHandler handler = new MessageHandler();

	String clockIdx;
	final Map<String, Integer> clocks = new HashMap<String, Integer>();
	final Map<String, String> clients = new HashMap<String, String>();

	private ChatManager() {
		try {
			// Setup the connection
			InetAddress adrServer = InetAddress.getByName(SERVER);
			sockCmd = new DatagramSocket(4000);
			sockCmd.setSoTimeout(10 * 1000); // read timeout of 10s
			sockCmd.connect(adrServer, 4000); // set default socket for send/recv

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public MessageHandler getHandler() {
		return this.handler;
	}

	/**
	 * Thread responsible for receiving messages and passing them
	 * on to the handler
	 */
	class ChatThread extends Thread {
		boolean initShutdown = false;
		DatagramSocket sockMsg = null;
		MessageHandler handler;

		public ChatThread(MessageHandler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			initShutdown = false;
			try {
				sockMsg = new DatagramSocket(4001);
				sockMsg.setSoTimeout(1000);
				int keepalive = 20;

				while (!initShutdown) {
					try {
						byte[] msg = new byte[MESSAGE_BUFFER_SIZE];
						DatagramPacket pkt = new DatagramPacket(msg, msg.length);
						sockMsg.receive(pkt); // blocking read
						Message m = handler.obtainMessage();
						Bundle b = new Bundle();
						b.putString("msg", new String(pkt.getData()));
						m.setData(b);
						m.sendToTarget();
					} catch (InterruptedIOException e) {
						// receive hit the timeout
						if (--keepalive == 0) {
							// send a little info every now and then to prevent
							// the server from capping us
							incClockTick();
							execCmd(cmdMsg("ping", clocks), false);
							keepalive = 20;
						}
					}
				}
				sockMsg.close();

			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void initShutdown() {
			initShutdown = true;
		}
	}

	/**
	 * Class responsible for handling incoming and outgoing messages
	 * Make sure to initialize it correctly by setting the uiActivity
	 * before using it
	 */
	class MessageHandler extends Handler {
		private ChatActivity uiActivity;
		private ArrayAdapter<String> msgs;
		private LinkedList<JSONObject> delayed = new LinkedList<JSONObject>();
		private ListView view;

		public void setUiActivity(ChatActivity uiActivity) {
			this.uiActivity = uiActivity;
			msgs = new ArrayAdapter<String>(uiActivity, R.layout.li_msg);
			view = (ListView) uiActivity.findViewById(R.id.list_view_messages);
			view.setAdapter(msgs);
		}

		public void clearMessages() {
			assert (view != null);
			msgs.clear();
			delayed.clear();
		}

		/** Display a message to the user */
		public void deliverMessage(String sender, String msg) {
			assert (view != null);
			String text = sender + ": " + msg;
			msgs.add(text);
			view.smoothScrollToPosition(msgs.getCount());
		}

		/**
		 * Handle a message deciding whether it should be
		 * delivered now or later or filtered out completely
		 */
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (uiActivity == null) {
				return;
			}
			try {
				JSONObject o = new JSONObject(msg.getData().getString("msg"));
				if (o.has("sender")) {
					// only filter messages if a sender is set (local msgs dont have it)
					if (!acceptMessage(o)) {
						return; // discard the message
					}
					if (isDeliverable(o)) {
						// Message is deliverable
						String text = o.getString("text");
						deliverMessage(o.getString("sender"), text);
						// Try to deliver pending messages
						dequeueMessages();
					}
				} else {
					String text = o.getString("text");
					deliverMessage(user, text);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Filter deciding whether to filter out a message or not
		 */
		private boolean acceptMessage(JSONObject o) throws JSONException {
			// filter on message tags
			if (o.has("tag")) {
				if (o.get("tag").equals(MESSAGE_TAG)) {
					return true;
				}
			}
			// filter whitelisted senders
			String sender = o.getString("sender");
			for (String s : SENDER_WHITELIST) {
				if (s.equals(sender)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Decide whether the message is deliverable or has to be queued
		 * waiting for prior messages
		 */
		private boolean isDeliverable(JSONObject msgObject) throws JSONException {
			JSONObject vecTime = msgObject.getJSONObject("time_vector");
			String sender = msgObject.getString("sender");

			int idx = sender.equals("QuestionBot") ? 1
					: sender.equals("AnswerBot") ? 2
					: msgObject.has("index") ? msgObject.getInt("index")
					: -1;

			boolean enqueue = false;
			int newVal = -1;

			@SuppressWarnings("unchecked")
			Iterator<String> i = vecTime.keys();
			while (i.hasNext()) {
				String s = i.next();
				if (clocks.containsKey(s)) {
					// We already have the clock
					int key = Integer.parseInt(s);
					if (idx < 0)
						continue;
					int ours = clocks.get(s);
					int theirs = vecTime.getInt(s);
					if (theirs > ours) {
						if (idx == key && theirs - ours == 1) {
							// sender incremented his clock
							// update ours if there's not another msg to wait for
							newVal = theirs;
						} else {
							// put it in the queue and wait for missing msgs
							enqueue = true;
						}
					}
				} else {
					// There's a new clock
					clocks.put(s, vecTime.getInt(s));
				}
			}

			if (enqueue) {
				delayed.add(msgObject);
			} else if (newVal > -1) {
				clocks.put(Integer.toString(idx), newVal);
			}
			return !enqueue;
		}

		/** Try to deliver pending messages */
		public void dequeueMessages() throws JSONException {
			// loop over all pending messages
			for (int n = 0; n < delayed.size(); ++n) {
				JSONObject o = delayed.get(n);
				String sender = o.getString("sender");
				JSONObject vecTime = o.getJSONObject("time_vector");

				// Get the clock index of the sender
				int idx = sender.equals("QuestionBot") ? 1
					: sender.equals("AnswerBot") ? 2
					: o.has("index") ? o.getInt("index")
					: -1;
				@SuppressWarnings("unchecked")
				Iterator<String> j = vecTime.keys();
				int newVal = -1;
				boolean abort = false;

				// Loop over clock indices of incoming message
				while (j.hasNext()) {
					String s = j.next();
					int key = Integer.parseInt(s);
					if (idx < 0)
						continue; // This index belongs to a discarded client, skip it
					int ours = clocks.get(s);
					int theirs = vecTime.getInt(s);
					if (idx == key && theirs - ours == 1) {
						newVal = theirs; // Candidate for delivery
					} else if (theirs - ours > 0){
						abort = true; // Cannot be delivered yet
						break;
					}
				}

				if (!abort && newVal > -1) {
					// Message is now deliverable
					clocks.put(Integer.toString(idx), newVal);
					delayed.remove(n);
					n = 0; // restart looping
					deliverMessage(o.getString("sender"), o.getString("text"));
				}
			}
		}
	}

	private String cmdReg(String user) {
		JSONObject o = new JSONObject();
		try {
			o.put("cmd", "register");
			o.put("user", user);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();
	}

	private String cmdDereg() {
		JSONObject o = new JSONObject();
		try {
			o.put("cmd", "deregister");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();
	}

	private String cmdInfo() {
		JSONObject o = new JSONObject();
		try {
			o.put("cmd", "info");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();
	}

	private String cmdMsg(String text, Map<String, Integer> clocks) {
		JSONObject o = new JSONObject();
		incClockTick();
		JSONObject t = new JSONObject(clocks);
		try {
			o.put("cmd", "message");
			o.put("text", text);
			o.put("tag", MESSAGE_TAG);
			o.put("index", clockIdx);
			o.put("time_vector", t);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();
	}

	private String cmdGetClients() {
		JSONObject o = new JSONObject();
		try {
			o.put("cmd", "get_clients");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();

	}
	
	/** Send a JSON command to the server */
	private JSONObject execCmd(String cmd, boolean receive) {
		try {
			byte[] req = cmd.getBytes();
			DatagramPacket pkt = new DatagramPacket(req, req.length);
			sockCmd.send(pkt);

			byte[] ans = new byte[MESSAGE_BUFFER_SIZE];
			pkt = new DatagramPacket(ans, ans.length);
			if (receive) {
				// Wait for an answer
				sockCmd.receive(pkt); // blocking read
				return new JSONObject(new String(pkt.getData()));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Send a new text message to the server */
	public void sendMsg(String text) {
		String json = cmdMsg(text, clocks); // create json string with msg and clocks
		execCmd(json, false);
		Message m = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putString("msg", json);
		m.setData(b);
		m.sendToTarget();
	}

	/** Increase our clock */
	private void incClockTick() {
		clocks.put(clockIdx, clocks.get(clockIdx) + 1);
	}

	@SuppressWarnings("unchecked")
	public boolean connect() {
		try {
			// register
			JSONObject o = execCmd(cmdReg(user), true);
			// answer:
			// {"index":3,"time_vector":{"3":0,"2":70,"1":71,"0":74},"success":"reg_ok"}
			if (o == null || !o.getString("success").equals("reg_ok"))
				return false;

			// get clocks (from register answer)
			clockIdx = Integer.toString(o.getInt("index"));
			JSONObject v = o.getJSONObject("time_vector");
			Iterator<String> i = v.keys();
			while (i.hasNext()) {
				// Redundant but will trigger a NumberFormatException if not a number
				Integer c = Integer.decode(i.next());
				clocks.put(c.toString(), v.getInt(c.toString()));
			}

			/*
			// get client list
			o = execCmd(cmdGetClients(), true).getJSONObject("clients");
			// answer: {"clients":
			// {"/129.132.75.130":"QuestionBot","/129.132.252.221":"AnswerBot","/77.58.228.17":"willi"}
			// }
			Iterator<String> s = o.keys();
			while (s.hasNext()) {
				String c = s.next();
				clients.put(c, (String) o.getString(c));
			}
			*/

			chatThread = new ChatThread(handler); // Create listener thread
			chatThread.start(); // Start listening for messages
			return true;
		} catch (JSONException e) {
			// Something bad happened.
			e.printStackTrace();
			return false;
		}
	}

	/** Disconnect from server */
	public void disconnect() {
		if (chatThread.isAlive()) {
			chatThread.initShutdown();
			try {
				chatThread.join(); // Wait for listener thread to terminate

				JSONObject o = execCmd(cmdDereg(), true); // answer {"success":"dreg_ok"}
				assert (o.getString("success").equals("dreg_ok"));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		handler.clearMessages(); // Clear all messages, readying for next run
	}

	/** Set a username. Do this before connecting */
	public void setUser(String user) {
		this.user = user;
	}
}
