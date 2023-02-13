package huayu.cordova.plugin.share.douyin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.ShareToContact;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import com.bytedance.sdk.open.douyin.model.OpenRecord;

public class AuthResponseActivity extends Activity implements IApiEventHandler {

  /**
   * 发起授权请求的Activity
   **/
  public static Activity RequestAuthActivity;//发起授权请求的Activity

  DouYinOpenApi douYinOpenApi;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    douYinOpenApi = TiktokShare.CurrentDouYinOpenApi;
    douYinOpenApi.handleIntent(getIntent(), this);
  }

  @Override
  public void onReq(BaseReq req) {
    int shareType=req.getType();
    switch (shareType)
    {
      case CommonConstants.ModeType.SEND_AUTH_REQUEST:

        break;

      case CommonConstants.ModeType.SHARE_CONTENT_TO_TT:
        break;
      case CommonConstants.ModeType.SHARE_TO_CONTACTS:
        break;
      case CommonConstants.ModeType.OPEN_RECORD_REQUEST:
        break;
      case CommonConstants.ModeType.OPEN_RECORD_RESPONSE:

        break;
      case CommonConstants.ModeType.OPEN_COMMON_ABILITY_REQUEST:
        break;

    }
  }

  @Override
  public void onResp(BaseResp resp) {
    int shareType=resp.getType();
    Intent intent = null;
    if(!resp.isSuccess()){
      String errorMessage="error code:" + resp.errorCode + " error Msg:" + resp.errorMsg;
      Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
      Log.e("TiktokShare",errorMessage);
    }
    switch (shareType)
    {
      case CommonConstants.ModeType.SEND_AUTH_REQUEST:
        break;
      case CommonConstants.ModeType.SEND_AUTH_RESPONSE:
        Authorization.Response authorizationResponse = (Authorization.Response) resp;
        intent = null;
        if (resp.isSuccess()) {

          Toast.makeText(this, "授权成功，获得权限：" + authorizationResponse.grantedPermissions,
            Toast.LENGTH_LONG).show();

        }
        break;
      case CommonConstants.ModeType.SHARE_CONTENT_TO_TT:
        break;
      case CommonConstants.ModeType.SHARE_CONTENT_TO_TT_RESP:
        Share.Response shareResponse = (Share.Response) resp;
          Toast.makeText(this, " code：" + shareResponse.errorCode + " 文案：" + shareResponse.errorMsg, Toast.LENGTH_SHORT).show();
          intent = new Intent(this, RequestAuthActivity.getClass());
          startActivity(intent);

        break;
      case CommonConstants.ModeType.SHARE_TO_CONTACTS:
        break;
      case CommonConstants.ModeType.SHARE_TO_CONTACT_RESP:
        ShareToContact.Response shareToContactResponse=(ShareToContact.Response) resp;
        break;
      case CommonConstants.ModeType.OPEN_RECORD_REQUEST:
        break;
      case CommonConstants.ModeType.OPEN_RECORD_RESPONSE:
        OpenRecord.Response OpenRecordResponse=(OpenRecord.Response )resp;

        break;
      case CommonConstants.ModeType.OPEN_COMMON_ABILITY_REQUEST:
        break;
      case CommonConstants.ModeType.OPEN_COMMON_ABILITY_RESPONSE:
        break;

    }

  }

  @Override
  public void onErrorIntent(Intent intent) {
    // 错误数据
    Toast.makeText(this, "Intent出错", Toast.LENGTH_LONG).show();
  }
}
