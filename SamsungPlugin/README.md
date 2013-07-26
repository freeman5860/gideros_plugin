Samsung IAP plugin 

===================================================================================
beta version 2013.07.26

===================================================================================

1.copy files in libs to target libs
2.copy IAPConnector.aidl and IAPServiceCallback.aidl to the same package
3.copy "com\giderosmobile\android\plugins\samsung" folder and its files to the same package
4.add System.loadLibrary("gsamsungiap"); in target activity
5.add "com.giderosmobile.android.plugins.samsung.GSamsungIAP", in target activity

===================================================================================