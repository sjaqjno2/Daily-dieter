package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;


/**
 * A login screen that offers login via username.
 */
public class ChatProcessActivity extends Activity {


    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat_process);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        enterRoom();
        attemptLogin();
//        Button signInButton = (Button) findViewById(R.id.sign_in_button);
//        signInButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("attemptLogin", "success");
//                attemptLogin();
//            }
//        });

        mSocket.on("login", onLogin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.off("login", onLogin);
    }

    private String loadToken() {
        SharedPreferences pref = getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }
    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void enterRoom() {
        SharedPreferences pref = getSharedPreferences("Group", Activity.MODE_PRIVATE);
        String groupName = pref.getString("name", null);

        mSocket.emit("enterGroupRoom", groupName);
    }

    private void attemptLogin() {

        String token = loadToken();

        // perform the user login attempt.
        mSocket.emit("getUserInfo", token);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            Log.d("emit","success");
            int numUsers;
            String username;
            try {
                numUsers = data.getInt("numUsers");
                username = data.getString("username");
            } catch (JSONException e) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("username", username);
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };
}