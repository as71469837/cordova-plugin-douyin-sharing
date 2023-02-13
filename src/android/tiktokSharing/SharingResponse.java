package huayu.cordova.plugin.share.douyin;

import org.json.JSONException;
import org.json.JSONObject;


public final class SharingResponse<T>{

    public String RequestAction;

    public boolean IsSuccess;

    public T ReturnValue= null ;

    public String ErrorMessage="";

    public SharingResponse(String action, boolean isSuccess) {
        RequestAction = action;
        IsSuccess = isSuccess;
      }


    public SharingResponse(String action,String errorMessage) {
        RequestAction = action;
        IsSuccess = false;
        ErrorMessage=errorMessage;
      }

      public SharingResponse(String action, boolean isSuccess, T returnValue) {
        RequestAction = action;
        IsSuccess = isSuccess;
        ReturnValue = returnValue;
      }

  public JSONObject ConvertToJson() throws JSONException {
    JSONObject jsonObject=new JSONObject();
    jsonObject.put("requestAction",this.RequestAction);
    jsonObject.put("isSuccess",this.IsSuccess);
    jsonObject.put("returnValue",this.ReturnValue);
    jsonObject.put("errorMessage",this.ErrorMessage);
    return  jsonObject;
  }
}
