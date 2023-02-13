package huayu.cordova.plugin.share.douyin;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;


public final class SharingRequest{

    public String Action;

    public int ActionType;

    public CallbackContext CurrentCallbackContext;

    public JSONObject Param;


    public  static  class Manager{
      private static HashMap<UUID,SharingRequest> RequestCache=new HashMap<UUID,SharingRequest>();

      public static UUID AddCache(SharingRequest request){
        UUID uuid= UUID.randomUUID();
        RequestCache.put(uuid,request);
        return  uuid;
      }

      public static SharingRequest GetCache(UUID cacheKey){
        if(RequestCache.containsKey(cacheKey)){
          SharingRequest temp= RequestCache.get(cacheKey);
          RequestCache.remove(cacheKey);
          return temp;
        }
        return null;
      }
    }



}





