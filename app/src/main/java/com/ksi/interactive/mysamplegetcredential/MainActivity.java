package com.ksi.interactive.mysamplegetcredential;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, View.OnClickListener {
    private GoogleApiClient apiClient;
    private final int PHONE_HINT = 0, EMAIL_HINT = 1;
    public static final String TAG=MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t);
        initApiClient();
        findViewById(R.id.email).setOnClickListener(this);
        findViewById(R.id.phone).setOnClickListener(this);

    }
    private void initApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
    }
    //region interface google
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("GoogleApiClient", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GoogleApiClient", "onConnectionSuspended:" + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("GoogleApiClient", "onConnectionFailed:" + connectionResult);
    }
    //endregion
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EMAIL_HINT:
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    EditText   email=  (EditText)findViewById(R.id.editEmail);
                    EditText   name=  (EditText)findViewById(R.id.editfristName);
                    EditText   family=  (EditText)findViewById(R.id.editLast);
                    email.setText(credential.getId());
                    name.setText(credential.getName());
                    family.setText(credential.getFamilyName()+" "+credential.getGivenName());
                    Toast.makeText(this, credential.getId()+"==="+credential.getName(),
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onActivityResult: "+credential.getName()+credential.getId());
                }
                break;
            case PHONE_HINT:
                if (resultCode == RESULT_OK) {
                   Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    Toast.makeText(this, credential.getId(),
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onActivityResult: "+credential.getName());
                   // etPhone.setText(credential.getId());
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.email){
            HintRequest hintRequest = new HintRequest.Builder()
                    .setEmailAddressIdentifierSupported(true)
                    .setAccountTypes(IdentityProviders.GOOGLE)
            .setPhoneNumberIdentifierSupported(true)
                    .build();
            PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest);
            try {
                startIntentSenderForResult(intent.getIntentSender(), EMAIL_HINT, null, 0, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            //===========================//
            HintRequest hintRequest = new HintRequest.Builder()
                    .setPhoneNumberIdentifierSupported(true)
                  // .setAccountTypes(IdentityProviders.GOOGLE)
                    .build();
            PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest);
            try {
                startIntentSenderForResult(intent.getIntentSender(), PHONE_HINT, null, 0, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //============================//
        }
    }
}
