package com.example.kotlinexample

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.TextView
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AcquireTokenSilentParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication
import com.microsoft.identity.client.IPublicClientApplication.IMultipleAccountApplicationCreatedListener
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalDeclinedScopeException
import com.microsoft.identity.client.exception.MsalException


class Authentication(context: Context) {
    lateinit var client: IMultipleAccountPublicClientApplication;
    private val scopes = arrayOf<String>(
        "https://emeaalpha.onmicrosoft.com/authorizer-service/read"
    );
    var accessToken: String = "";
    var idToken: String = "";

    init {
        PublicClientApplication.createMultipleAccountPublicClientApplication(
            context, R.raw.auth_config,
            object : IMultipleAccountApplicationCreatedListener {
                override fun onCreated(application: IMultipleAccountPublicClientApplication) {
                    client = application
                }

                override fun onError(exception: MsalException) {
                    //Log Exception Here
                    exception.message?.let { Log.e("Auth", it) }
                }
            }
        )
    }

    private fun authCallBack (textView: TextView): AuthenticationCallback {
        return object: AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                val account = authenticationResult.account;
                accessToken = authenticationResult.accessToken;
                idToken = if (account.idToken is String) {
                    account.idToken!!;
                } else {
                    "";
                }
                Log.i("Auth", authenticationResult.toString());
                Log.i("Auth accessToken", accessToken);
                Log.i("Auth ID Token", idToken)
                textView.text = "Signed In"
            }
            override fun onError(msalException: MsalException) {
                msalException.message?.let { Log.e("Auth Error", it) };
                textView.text = "Something went wrong"
            }
            override fun onCancel() {
                Log.i("Auth", "User canceled flow")
                textView.text = "Not signed In"
            }
        }
    }

    fun signIn(activity: Activity, textView: TextView) {
        if(client.accounts.isEmpty()) {
            val params = AcquireTokenParameters.Builder()
                .withScopes(scopes.toMutableList())
                .fromAuthority("https://emeaalpha.b2clogin.com/tfp/emeaalpha.onmicrosoft.com/b2c_1a_signup_signin_generic")
                .withCallback(authCallBack(textView))
                .build();

            client.acquireToken(params)
        } else {
            this.refreshToken(textView)
        }
    }

    fun refreshToken(textView: TextView) {
        if(client.accounts.isNotEmpty()) {
            val params = AcquireTokenSilentParameters.Builder()
                .withScopes(scopes.toMutableList())
                .fromAuthority("https://emeaalpha.b2clogin.com/tfp/emeaalpha.onmicrosoft.com/b2c_1a_signup_signin_generic")
                .withCallback(authCallBack(textView))
                .forAccount(client.accounts[0])
                .build();
            client.acquireTokenSilentAsync(params);
        }
    }
}
