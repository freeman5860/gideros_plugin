package com.giderosmobile.android.plugins.samsung;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class VerificationVO
{
    private String mItemId;
    private String mItemName;
    private String mItemDesc;
    private String mPurchaseDate;
    private String mPaymentId;
    private String mPaymentAmount;
    private String mStatus;
    
    public VerificationVO( String strJson )
    {
        try
        {
            JSONObject jObject = new JSONObject( strJson );
            
            Log.i("VerificationVO",strJson);
            
            mItemId        = jObject.getString( "itemId" );
            mItemName      = jObject.getString( "itemName" );
            mItemDesc      = jObject.getString( "itemDesc" );
            mPurchaseDate  = jObject.getString( "purchaseDate" );
            mPaymentId     = jObject.getString( "paymentId" );
            mPaymentAmount = jObject.getString( "paymentAmount" );
            mStatus        = jObject.getString( "status" );
        }
        catch( JSONException e )
        {
            e.printStackTrace();
        }
    }

    public String getmItemId()
    {
        return mItemId;
    }

    public void setmItemId(String mItemId)
    {
        this.mItemId = mItemId;
    }

    public String getmItemName()
    {
        return mItemName;
    }

    public void setmItemName(String mItemName)
    {
        this.mItemName = mItemName;
    }

    public String getmItemDesc()
    {
        return mItemDesc;
    }

    public void setmItemDesc(String mItemDesc)
    {
        this.mItemDesc = mItemDesc;
    }

    public String getmPurchaseDate()
    {
        return mPurchaseDate;
    }

    public void setmPurchaseDate(String mPurchaseDate)
    {
        this.mPurchaseDate = mPurchaseDate;
    }

    public String getmPaymentId()
    {
        return mPaymentId;
    }

    public void setmPaymentId(String mPaymentId)
    {
        this.mPaymentId = mPaymentId;
    }

    public String getmPaymentAmount()
    {
        return mPaymentAmount;
    }

    public void setmPaymentAmount(String mPaymentAmount)
    {
        this.mPaymentAmount = mPaymentAmount;
    }

    public String getmStatus()
    {
        return mStatus;
    }

    public void setmStatus(String mStatus)
    {
        this.mStatus = mStatus;
    }

    public String dump()
    {
        String dump = null;
        
        dump = "mItemId        : " + mItemId        + "\n" +
               "mItemName      : " + mItemName      + "\n" +
               "mItemDesc      : " + mItemDesc      + "\n" +
               "mPurchaseDate  : " + mPurchaseDate  + "\n" +
               "mPaymentId     : " + mPaymentId     + "\n" +
               "mPaymentAmount : " + mPaymentAmount + "\n" +
               "mStatus        : " + mStatus;
        
        return dump;
    }
}