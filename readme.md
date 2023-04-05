Install Instructions:

1. Download and install android studio 
2. Download and install git
3. Clone the repository
4. Open the project folder in android studio

Using MSAL: 

Make sure to add the following to your AndroidManifest.xml file:
```xml
<activity
    android:name="com.microsoft.identity.client.BrowserTabActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:host="<REDIRECT_HOST>"
            android:path="<REDIRECT_PATH>"
            android:scheme="<REDIRECT_SCHEME>" />
    </intent-filter>
</activity>
```
In this example the redirect path configured in the app registration portal is com.example.rnauthdemo://oauth/redirect so we set <REDIRECT_HOST> = "oauth" and <REDIRECT_PATH> = "/redirect" and <REDIRECT_SCHEME> = "com.example.rnauthdemo".

In the app/build.gradle file, add the following to the dependencies section:
implementation 'com.microsoft.identity.client:msal:3.0.+'

And in the settings.gradle file, add the following under dependenciesResolutionManagement.repositories and pluginManagement.repositories:
```
maven {
    url 'https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1'
}
```
We also need to add a config file for the MSAL plugin. 
Create a file called msal_config.json in the android/app/src/main/res/raw folder.
Add the following to the file:
```json
{
  "client_id" : "<CLIENT_ID>",
  "redirect_uri" : "<REDIRECT_URI>",
  "broker_redirect_uri_registered": false,
  "account_mode": "MULTIPLE",
  "authorities": [{
    "type": "B2C",
    "authority_url": "https://<TENANT_NAME>.b2clogin.com/tfp/<TENANT_NAME>.onmicrosoft.com/<USER_FLOW_NAME>",
    "default": true
  },{...}, ...
  ]
}
```
You can add as many authorities as you have user flows. Make sure to include account_mode: "MULTIPLE" otherwise you will not be able to use msal for b2c.
Redirect URI should be the same as configured in the app registration portal, and the same as the one in the AndroidManifest.xml file.
In our example, TENANT_NAME is emeaalpha. All of these details can be found in the app registration portal.

We create an object to manage the MSAL plugin in the Authentication.kt file. 
MSAL will manage our b2c authentication with a ```com.microsoft.identity.client.IMultipleAccountPublicClientApplication```
To initialize the object, we pass the auth_config.json file to the constructor, as well as a callback listener for when the object is created.
You can create the object created callback with ```com.microsoft.identity.client.IPublicClientApplication.IMultipleAccountApplicationCreatedListener```

To sign a user in, you can call the aquireToken or aquireTokenSilent methods on the object.
First you must create aquire token parameters object with ```com.microsoft.identity.client.AcquireTokenParameters``` (or AcquireTokenSilentParameters)
With that you can set the scopes, authority, and callback listener, as well as other parameters.
Then you can call the aquireToken or aquireTokenSilent methods on the object.

See Authentication.kt for more details.

Note about threading: Some of the MSAL methods cannot be called on the main thread, for example in line 69 of Authentication.kt,
we call client.accounts on the main thread, which will throw an exception. To fix this, we create a new thread to sign the users in. 