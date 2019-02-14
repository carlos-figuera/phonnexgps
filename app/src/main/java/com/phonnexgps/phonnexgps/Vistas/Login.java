package com.phonnexgps.phonnexgps.Vistas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.phonnexgps.phonnexgps.R;
import com.phonnexgps.phonnexgps.presentador.Presentador_Login;
import com.phonnexgps.phonnexgps.presentador.Presentador_spinner;

import java.util.HashMap;

public class Login extends AppCompatActivity implements View.OnClickListener {
    Button Btn_ingresar;
    Presentador_Login presentador_login;
    Presentador_spinner presentador_spinner;
    private EditText Edit_email, Edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        presentador_login = new Presentador_Login(Login.this);
        presentador_spinner = new Presentador_spinner(Login.this);
        Edit_email = (EditText) findViewById(R.id.Edit_email);
        Edit_password = (EditText) findViewById(R.id.Edit_password);
        Btn_ingresar = (Button) findViewById(R.id.Btn_ingresar);
        Btn_ingresar.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Btn_ingresar:
                if (presentador_login.verificacion(Edit_email, Edit_password) == false) {

                    if (presentador_spinner.isOnline(Login.this) == true) {
                        presentador_login.Login(datos());
                    }
                    else{
                        Toast.makeText(this, "No tienes conexion a internet activa los datos o conecta a un wifi.", Toast.LENGTH_SHORT).show();
                    }

                }

                break;
        }
    }

    // headers.put("username", "FIGUERAC21");
    //  headers.put("password", "123456");
    public HashMap datos() {
        HashMap headers = new HashMap();
        headers.put("username", Edit_email.getText().toString().trim());
        headers.put("password", Edit_password.getText().toString().trim());
        return headers;
    }

}
