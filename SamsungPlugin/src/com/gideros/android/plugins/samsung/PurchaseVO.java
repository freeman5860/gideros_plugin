package com.gideros.android.plugins.samsung;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateFormat;
import android.util.Log;

public class PurchaseVO extends ItemVO
{
    private String mPaymentId;
    private String mPurchaseDate;
    private String mPurchaseId;
    private String mVerifyUrl;
    
    public PurchaseVO( String mJsonItem )
    {
        try
        {
            Log.i("PurchaseVO",mJsonItem);
            
            JSONObject jObject = new JSONObject( mJsonItem );
            mPaymentId = jObject.getString( "mPaymentId" );
            mPurchaseId = jObject.getString( "mPurchaseId" );

            setItemId( jObject.getString( "mItemId" ) );
            setItemName( jObject.getString( "mItemName" ) );
            setItemPrice( Double.parseDouble( ( jObject.getString( "mItemPrice" ) ) ) );
            setItemPriceString( jObject.getString( "mItemPriceString" ) );
            setCurrencyUnit( jObject.getString( "mCurrencyUnit" ) );
            setItemDesc( jObject.getString( "mItemDesc" ) );
            setItemImageUrl( jObject.getString( "mItemImageUrl" ) );
            setItemDownloadUrl( jObject.getString( "mItemDownloadUrl" ) );
            setReserved1( jObject.getString( "mReserved1" ) );
            setReserved2( jObject.getString( "mReserved2" ) );
            setVerifyUrl(jObject.getString( "mVerifyUrl" ) );
        
            long timeInMillis = Long.parseLong( jObject.getString( "mPurchaseDate" ) );
            
            mPurchaseDate = DateFormat.format( "yyyy.MM.dd hh:mm:ss", 
                                               timeInMillis ).toString();
        }
        catch ( JSONException e )
        {
            e.printStackTrace();
        }
    }

    public String getVerifyUrl()
    {
        return mVerifyUrl;
    }
    
    public void setVerifyUrl(String _verifyUrl)
    {
        this.mVerifyUrl = _verifyUrl;
    }
    
    public String getPaymentId()
    {
        return mPaymentId;
    }

    public void setPaymentId( String mPaymentId )
    {
        this.mPaymentId = mPaymentId;
    }

    public String getPurchaseDate()
    {
        return mPurchaseDate;
    }

    public void setPurchaseDate( String mPurchaseDate )
    {
        this.mPurchaseDate = mPurchaseDate;
    }

    public String getPurchaseId()
    {
        return mPurchaseId;
    }

    public void setPurchaseId( String purchaseId )
    {
        this.mPurchaseId = purchaseId;
    }
    
    public String dump()
    {
        String dump = null;
        
        dump = "paymentID : " + getPaymentId() + "\n" + 
        "itemDesc         : " + getItemDesc() + "\n" +
        "itemPrice        : " + getItemPrice() + "\n" +
        "currencyUnit     : " + getCurrencyUnit() + "\n" +
        "itemImageUrl     : " + getItemImageUrl() + "\n" +
        "itemName         : " + getItemName() + "\n" +
        "purchaseDate     : " + getPurchaseDate() + "\n" +
        "purchaseId       : " + getPurchaseId() + "\n" +
        "reserved1        : " + getReserved1() + "\n" +
        "reserved2        : " + getReserved2() + "\n" + 
        "itemDownloadUrl  : " + getItemDownloadUrl() + "\n" +        
        "itemId           : " + getItemId() + "\n" +
        "itemPriceString  : " + getItemPriceString() + "\n";

        return dump;
    }
}