Samsung IAP plugin 

===================================================================================
beta version 2013.07.26

===================================================================================

1.add <uses-permission android:name="com.sec.android.iap.permission.BILLING"/> in AndroidManifest.xml

2.copy files in libs to target libs

3.copy IAPConnector.aidl and IAPServiceCallback.aidl to the same package

4.copy "com\giderosmobile\android\plugins\samsung" folder and its files to the same package

5.add System.loadLibrary("gsamsungiap"); in target activity

6.add "com.giderosmobile.android.plugins.samsung.GSamsungIAP", in target activity


===================================================================================