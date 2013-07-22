package com.gideros.android.plugins.samsung;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemVO 
{
    private String mItemId;             
    private String mItemName;           
    private Double mItemPrice;          
    private String mItemPriceString;
    private String mCurrencyUnit;       
    private String mItemDesc;           
    private String mItemImageUrl;       
    private String mItemDownloadUrl;    
    private String mReserved1;          
    private String mReserved2;          
    private String mType;               
    
    public ItemVO()
    {
    }

    public ItemVO( String mJsonItem )
    {
        try
        {
            JSONObject jObject = new JSONObject( mJsonItem );
            mItemId = jObject.getString( "mItemId" );
            mItemName = jObject.getString( "mItemName" );
            mItemPrice = Double.parseDouble( ( jObject.getString( "mItemPrice" ) ) );
            mCurrencyUnit = jObject.getString( "mCurrencyUnit" );
            mItemDesc = jObject.getString( "mItemDesc" );
            mItemImageUrl = jObject.getString( "mItemImageUrl" );
            mItemDownloadUrl = jObject.getString( "mItemDownloadUrl" );
            mReserved1 = jObject.getString( "mReserved1" );
            mReserved2 = jObject.getString( "mReserved2" );
            mItemPriceString = jObject.getString( "mItemPriceString" );
            mType = jObject.getString( "mType" );
        }
        catch ( JSONException e )
        {
            e.printStackTrace();
        }
    }

    public String getItemId()
    {
        return mItemId;
    }

    public void setItemId( String itemId )
    {
        this.mItemId = itemId;
    }

    public String getItemName()
    {
        return mItemName;
    }

    public void setItemName( String itemName )
    {
        this.mItemName = itemName;
    }

    public Double getItemPrice()
    {
        return mItemPrice;
    }

    public void setItemPrice( Double itemPrice )
    {
        this.mItemPrice = itemPrice;
    }
    
    public String getItemPriceString()
    {
        return mItemPriceString;
    }

    public void setItemPriceString( String itemPriceString )
    {
        this.mItemPriceString = itemPriceString;
    }

    public String getCurrencyUnit()
    {
        return mCurrencyUnit;
    }

    public void setCurrencyUnit( String currencyUnit )
    {
        this.mCurrencyUnit = currencyUnit;
    }

    public String getItemDesc()
    {
        return mItemDesc;
    }

    public void setItemDesc( String itemDesc )
    {
        this.mItemDesc = itemDesc;
    }

    public String getItemImageUrl()
    {
        return mItemImageUrl;
    }

    public void setItemImageUrl( String itemImageUrl )
    {
        this.mItemImageUrl = itemImageUrl;
    }

    public String getItemDownloadUrl()
    {
        return mItemDownloadUrl;
    }

    public void setItemDownloadUrl( String itemDownloadUrl )
    {
        this.mItemDownloadUrl = itemDownloadUrl;
    }

    public String getReserved1()
    {
        return mReserved1;
    }

    public void setReserved1( String reserved1 )
    {
        this.mReserved1 = reserved1;
    }

    public String getReserved2()
    {
        return mReserved2;
    }

    public void setReserved2( String reserved2 )
    {
        this.mReserved2 = reserved2;
    }
    
    public String getType()
    {
        return mType;
    }

    public void setType( String type )
    {
        this.mType = type;
    }
    
    public String dump()
    {
        String dump = null;
        
        dump = "mItemId : " + getItemId() + "\n" + 
        "itemName         : " + getItemName() + "\n" +
        "itemPrice        : " + getItemPrice() + "\n" +
        "currencyUnit     : " + getCurrencyUnit() + "\n" +
        "itemDesc         : " + getItemDesc() + "\n" +
        "itemImageUrl     : " + getItemImageUrl() + "\n" +
        "itemDownloadUrl  : " + getItemDownloadUrl() + "\n" +
        "reserved1        : " + getReserved1() + "\n" +
        "reserved2        : " + getReserved2() + "\n" + 
        "type             : " + getType();

        return dump;
    }
}