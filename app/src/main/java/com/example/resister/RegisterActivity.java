package com.example.resister;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import com.example.resister.R;
import com.example.resister.Request.RegisterRequest;
import com.example.resister.Request.ValidateRequest;

public class RegisterActivity extends AppCompatActivity {
    private ArrayAdapter adapter;
    private String userID;
    private String userPassword;
    private String userGender;
    private String username;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText etId = findViewById(R.id.etId);
        final EditText etPasswd = findViewById(R.id.etPasswd);
        final EditText etName = findViewById(R.id.etName);
        final Button btnValidate = findViewById(R.id.btnValidate);
        RadioGroup rgroupGender = findViewById(R.id.rgroupGender);
        int genderGrpID = rgroupGender.getCheckedRadioButtonId();
        if (genderGrpID != -1) {
            userGender = ((RadioButton) findViewById(genderGrpID)).getText().toString();
        } else {
            userGender = ""; // 또는 null, 또는 기본값
        }
        rgroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton genderBtn = findViewById(i);
                userGender = genderBtn.getText().toString();
            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = etId.getText().toString();
                if (userID.trim().equals("")) {
                    Toast.makeText(RegisterActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    validate = false;
                    etId.setFocusable(true);
                    return;
                }
                if (validate) return;


                Response.Listener responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean newID = jsonResponse.getBoolean("newId");
                            Log.d("mytest", jsonResponse.toString());
                            if (newID) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                etId.setEnabled(false);
                                etId.setBackgroundColor(Color.GRAY);
                                btnValidate.setBackgroundColor(Color.GRAY);
                                validate = true;
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validteRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validteRequest);
            }
        });
        Button btnMembership = findViewById(R.id.btnMembership);
        btnMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = etId.getText().toString();
                String userPassword = etPasswd.getText().toString();
                String userName = etName.getText().toString();

                //TODO. validate 값에 따라 중복체크 요청 후 종료 처리
                if (userID.equals("") || userPassword.equals("") || userName.equals("")
                       || userGender.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("모든 정보를 입력해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                Response.Listener responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원으로 등록되었습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                finish();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 등록에 실패했습니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userGender, userName, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}

