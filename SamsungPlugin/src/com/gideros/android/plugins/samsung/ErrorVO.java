package com.gideros.android.plugins.samsung;

public class ErrorVO
{
    private int      mErrorCode           = 0;
    private String   mErrorString         = "";
    private String   mExtraString         = "";             
    
    public int getErrorCode()
    {
        return mErrorCode;
    }
    
    public void setErrorCode( int _errorCode )
    {
        mErrorCode = _errorCode;
    }
    
    public String getErrorString()
    {
        return mErrorString;
    }
    
    public void setErrorString( String _errorString )
    {
        mErrorString = _errorString;
    }
    
    public String getExtraString()
    {
        return mExtraString;
    }
    
    public void setExtraString( String _extraString )
    {
        mExtraString = _extraString;
    }
    
    public String dump()
    {
        String result = "";
        
        result = "mErrorCode    : " + mErrorCode    +  "\n" +
                 "mErrorString  : " + mErrorString  +  "\n" +
                 "mExtraString  : " + mExtraString;
                 
        return result;
    }
}
