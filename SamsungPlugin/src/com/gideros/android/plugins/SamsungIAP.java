package com.gideros.android.plugins;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.RejectedExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gideros.android.plugins.samsung.ErrorVO;
import com.gideros.android.plugins.samsung.PurchaseVO;
import com.gideros.android.plugins.samsung.SamsungIapHelper;
import com.gideros.android.plugins.samsung.SamsungIapHelper.OnIapBindListener;
import com.gideros.android.plugins.samsung.VerificationVO;

public class SamsungIAP {
	private static final String TAG = SamsungIAP.class.getSimpleName();

	/** result of IAPService initialization */
	private static ErrorVO mErrorVO = null;

	/** loading progress dialog */
	private static ProgressDialog mProgressDialog = null;

	// Item Group ID of 3rd Party Application
	// ========================================================================
	private static String mItemGroupId = null;
	// ========================================================================

	// Purchase target item ID
	// ========================================================================
	private static String mItemId = null;
	// ========================================================================

	// AsyncTask for IAPService Initialization
	// ========================================================================
	private static InitTask mInitTask = null;
	// ========================================================================

	// Communication Helper between IAPService and 3rd Party Application
	// ========================================================================
	private static SamsungIapHelper mSamsungIapHelper = null;
	// ========================================================================

	// 결제 유효성 확인
	// verify payment result by server
	// ========================================================================
	private static VerifyClientToServer mVerifyClientToServer = null;
	// ========================================================================

	private static WeakReference<Activity> sActivity;

	public static void onCreate(Activity activity) {
		sActivity = new WeakReference<Activity>(activity);
	}
	
	public static void onDestroy(){
		// unbound IAPService
		if(mSamsungIapHelper != null){
			mSamsungIapHelper.dispose();
		}
		
		if(mInitTask != null){
			if(mInitTask.getStatus() != Status.FINISHED){
				mInitTask.cancel(true);
			}
		}
		
		if(mVerifyClientToServer != null){
			if(mVerifyClientToServer.getStatus() != Status.FINISHED){
				mVerifyClientToServer.cancel(true);
			}
		}
	}

	public static void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		switch (requestCode) {
		case SamsungIapHelper.REQUEST_CODE_IS_IAP_PAYMENT: {
			if (null == data) {
				break;
			}

			Bundle extras = data.getExtras();

			String itemId = "";
			String thirdPartvName = "";

			// payment success : 0
			// payment cancel: 1
			int statusCode = 1;

			String errorString = "";
			PurchaseVO purchaseVO = null;

			// 1.if there is bundle passed from IAP
			if (null != extras) {
				thirdPartvName = extras
						.getString(SamsungIapHelper.KEY_NAME_THIRD_PARTY_NAME);

				statusCode = extras
						.getInt(SamsungIapHelper.KEY_NAME_STATUS_CODE);

				errorString = extras
						.getString(SamsungIapHelper.KEY_NAME_ERROR_STRING);

				itemId = extras.getString(SamsungIapHelper.KEY_NAME_ITEM_ID);

				purchaseVO = new PurchaseVO(
						extras.getString(SamsungIapHelper.KEY_NAME_RESULT_OBJECT));
			}
			// 2.if there is no bundle passed from IAP
			else {
				mSamsungIapHelper.showIapDialog(sActivity.get(),
						"Payment error",
						"The payment was not processed successfully.", true,
						null);
			}

			// 3. if payment was not cancelled
			if (Activity.RESULT_OK == resultCode) {
				// a.if payment succeed
				if (statusCode == SamsungIapHelper.IAP_ERROR_NONE) {
					safeVerifyClientToServerTask(purchaseVO);
				}
				// b.payment failed
				else {
					mSamsungIapHelper.showIapDialog(sActivity.get(),
							"Payment error", "errorString: " + errorString,
							true, null);
				}
			}
			// 4. if payment was cancelled
			else if (Activity.RESULT_CANCELED == resultCode) {
				mSamsungIapHelper.showIapDialog(sActivity.get(),
						"Payment canceled", "statusCode: " + statusCode, true,
						null);
			}

			break;
		}

		// 2. treat result of SamsungAccount authentication
		case SamsungIapHelper.REQUEST_CODE_IS_ACCOUNT_CERTIFICATION: {
			// 1)if samsungAccount authentication is succeed
			if (Activity.RESULT_OK == resultCode) {
				// start binding and initialization for IAPService
				bindIapService();
			}
			// 2) if samsungAccount authentication is cancelled
			else if (Activity.RESULT_CANCELED == resultCode) {
				// dismiss progress dialog for SamsungAccount Authentication
				dismissProgressDialog(mProgressDialog);

				mSamsungIapHelper.showIapDialog(sActivity.get(),
						"SamsungAccount authentication",
						"SamsungAccount authentication has been cancelled",
						true, null);
			}
			break;
		}
		}
	}

	public static void purchaseItem(String itemGroupId, String itemId) {
		// 1.store Item Group Id and Item Id
		mItemGroupId = itemGroupId;
		mItemId = itemId;

		// 2.show progress dialog for IAPService Initialization
		mProgressDialog = showProgressDialog(sActivity.get());

		// 3.create SamsungIapHelper Instance
		mSamsungIapHelper = new SamsungIapHelper(sActivity.get());

		// 4.samsungaccount authentication process
		if (true == mSamsungIapHelper.isInstalledIapPackage(sActivity.get())) {
			// If IAP package installed in your device is valid
			if (true == mSamsungIapHelper.isValidIapPackage(sActivity.get())) {
				mSamsungIapHelper.startAccountActivity(sActivity.get());
			}
			// if IAP package installed in your device is not valid
			else {
				mSamsungIapHelper
						.showIapDialog(
								sActivity.get(),
								"In-app purchase",
								"IAP Application installed in your device is not valid!!",
								true, null);
			}
		} else {
			mSamsungIapHelper.installIapPackage(sActivity.get());
		}
	}

	/**
	 * bind IAPService. If IAPService properly bound, initIAP() method is called
	 * to initialize IAPService.
	 */
	public static void bindIapService() {
		// 1.Test Success Mode:1,COMMERCIAL MODE:0,Test Fail Mode:-1
		mSamsungIapHelper.setMode(1);

		// 2.bind to IAPService
		mSamsungIapHelper.bindIapService(new OnIapBindListener() {

			@Override
			public void onBindIapFinished(int result) {
				// 1.If successfully bound IAPService
				if (result == SamsungIapHelper.IAP_RESPONSE_RESULT_OK) {
					// initialize IAPService
					initIAP();
				}
				// 2. If IAPService is not bound correctly
				else {
					dismissProgressDialog(mProgressDialog);

					mSamsungIapHelper.showIapDialog(sActivity.get(),
							"In-app purchase",
							"In-app Purchase Service Bind failed.", true, null);
				}
			}
		});
	}

	/**
	 * Initialize IAPService
	 */
	public static void initIAP() {
		safeInitTask();
	}

	private static void safeInitTask() {
		try {
			if (mInitTask != null && mInitTask.getStatus() != Status.FINISHED) {
				mInitTask.cancel(true);
			}

			mInitTask = new InitTask();
			mInitTask.execute();
		} catch (RejectedExecutionException e) {
			Log.e(TAG, "safeInitTask\n" + e.toString());
		} catch (Exception e) {
			Log.e(TAG, "safeInitTask\n" + e.toString());
		}
	}

	private static class InitTask extends AsyncTask<String, Object, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				// initialize IAPService by calling init() method of IAPService
				mErrorVO = mSamsungIapHelper.init();
				return true;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// 1.dismiss progress dialog for IAPService Initialization
			dismissProgressDialog(mProgressDialog);

			// 2.initTask returned true
			if (true == result) {
				// 1) if initialization is completed successfully
				if (mErrorVO.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE) {
					// a.Toast for initialization success
					Toast.makeText(
							sActivity.get(),
							"SamsungAccount authentication and in-app billing initialization was successful.",
							Toast.LENGTH_SHORT).show();

					// b.call purchaseMethodListActivity of IAP
					mSamsungIapHelper.startPurchase(sActivity.get(),
							SamsungIapHelper.REQUEST_CODE_IS_IAP_PAYMENT,
							mItemGroupId, mItemId);
				}
				// 2) if the IAP applicatio needs to be upgraded
				else if (mErrorVO.getErrorCode() == SamsungIapHelper.IAP_ERROR_NEED_APP_UPGRADE) {
					Runnable oKBtnRunnable = new Runnable() {

						@Override
						public void run() {
							Intent intent = new Intent();
							intent.setData(Uri.parse(mErrorVO.getErrorString()));
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							try {
								sActivity.get().startActivity(intent);
							} catch (ActivityNotFoundException e) {
								Log.e(TAG, e.toString());
							}
						}
					};

					mSamsungIapHelper.showIapDialog(sActivity.get(),
							"In-app purchase",
							"In-app Purchase upgrade is required.", true,
							oKBtnRunnable);

					Log.e(TAG, mErrorVO.getErrorString());
				}
				// 3) if the IAPService failed to initialize
				else {
					mSamsungIapHelper.showIapDialog(sActivity.get(),
							"In-app purchase",
							"Failed to initialize the in-app purchase.", true,
							null);

					Log.e(TAG, mErrorVO.getErrorString());
				}
			}
			// 3.InitTask returned false
			else {
				Log.e(TAG, "InitTask false...");

				mSamsungIapHelper
						.showIapDialog(sActivity.get(), "In-app purchase",
								"Failed to initialize the in-app purchase.",
								true, null);
			}
		}
	}

	private static void safeVerifyClientToServerTask(PurchaseVO _purchaseVO) {
		try {
			if (mVerifyClientToServer != null
					&& mVerifyClientToServer.getStatus() != Status.FINISHED) {
				mVerifyClientToServer.cancel(true);
			}

			mVerifyClientToServer = new VerifyClientToServer(_purchaseVO);
			mVerifyClientToServer.execute();
		} catch (RejectedExecutionException e) {
			Log.e(TAG, "safeInitTask()\n" + e.toString());
		} catch (Exception e) {
			Log.e(TAG, "safeInitTask()\n" + e.toString());
		}
	}

	/**
	 * verify purchased result for a more secure transaction we recommend to
	 * verify from your server to IAP server.
	 */
	private static class VerifyClientToServer extends AsyncTask<Void, Void, Boolean> {
		PurchaseVO mPurchaseVO = null;
		VerificationVO mVerificationVO = null;

		public VerifyClientToServer(PurchaseVO _purchasedVO) {
			mPurchaseVO = _purchasedVO;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if (mPurchaseVO == null
					|| true == TextUtils.isEmpty(mPurchaseVO.getVerifyUrl())
					|| true == TextUtils.isEmpty(mPurchaseVO.getPurchaseId())
					|| true == TextUtils.isEmpty(mPurchaseVO.getPaymentId())) {
				this.cancel(true);
			}

			mProgressDialog = showProgressDialog(sActivity.get());
		}

		@Override
		protected void onCancelled() {
			dismissProgressDialog(mProgressDialog);
			super.onCancelled();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				StringBuffer strUrl = new StringBuffer();
				strUrl.append(mPurchaseVO.getVerifyUrl());
				strUrl.append("&purchaseID=" + mPurchaseVO.getPurchaseId());
				
				int retryCount = 0;
				String strResponse = null;
				
				do{
					strResponse = getHttpGetData(strUrl.toString(),10000,10000);
					retryCount++;
				}while(retryCount < 3 && true == TextUtils.isEmpty(strResponse));
				
				if(strResponse == null || TextUtils.isEmpty(strResponse)){
					return false;
				}else{
					mVerificationVO = new VerificationVO(strResponse);
					
					if(mVerificationVO != null && 
							true == "true".equals(mVerificationVO.getmStatus()) && 
							true == mPurchaseVO.getPaymentId().equals(
									mVerificationVO.getmPaymentId())){
						return true;
					}else{
						return false;
					}
				}
			}catch(Exception e){
				Log.e(TAG,e.toString());
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			dismissProgressDialog(mProgressDialog);
			
			if(true == result){
				mSamsungIapHelper.showIapDialog(sActivity.get(), 
						"Payment success", 
						"paymentID:	" + mPurchaseVO.getPaymentId(), 
						true, null);
			}else{
				mSamsungIapHelper.showIapDialog(sActivity.get(), 
						"Payment error", 
						"paymentID:	" + mPurchaseVO.getPaymentId(), 
						true, null);
			}
		}
		
		private String getHttpGetData(final String _strUrl,final int _connTimeout,final int _readTimeout){
			String strResult = null;
			URLConnection con = null;
			HttpURLConnection httpConnection = null;
			BufferedInputStream bis = null;
			ByteArrayOutputStream buffer = null;
			
			try{
				URL url = new URL(_strUrl);
				con = url.openConnection();
				con.setConnectTimeout(10000);
				con.setReadTimeout(10000);
				
				httpConnection = (HttpURLConnection)con;
				httpConnection.setRequestMethod("GET");
				httpConnection.connect();
				
				int responseCode = httpConnection.getResponseCode();
				
				if(responseCode == 200){
					bis = new BufferedInputStream(httpConnection.getInputStream(), 4096);
					buffer = new ByteArrayOutputStream(4096);
					
					byte [] bData = new byte [4096];
					int nRead;
					
					while((nRead = bis.read(bData, 0, 4096)) != -1){
						buffer.write(bData,0,nRead);
					}
					
					buffer.flush();
					
					strResult = buffer.toString();
				}
			}catch(Exception e){
				Log.e(TAG,e.toString());
			}finally{
				if(bis != null){
					try{
						bis.close();
					}catch(Exception e){
					}
				}
				if(buffer != null){
					try{buffer.close();}catch(IOException e){}
				}
				con = null;
				httpConnection = null;
			}
			
			return strResult;
		}
	}
	
	public static ProgressDialog showProgressDialog(Context _context){
		return ProgressDialog.show(_context, "", "Waiting...",true);
	}
	
	public static void dismissProgressDialog(ProgressDialog _progressDialog){
		try{
			if(null != _progressDialog && _progressDialog.isShowing()){
				_progressDialog.dismiss();
			}
		}catch(Exception e){
			Log.e(TAG,e.toString());
		}
	}
}
