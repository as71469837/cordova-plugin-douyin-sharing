package huaya.cordova.plugin.share.douyin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;

public class AuthResponActitvity extends Activity implements IApiEventHandler {

  /**
   * 发起授权请求的Actitvity
   **/
  public static Activity RequestAuthActitvity;//发起授权请求的Actitvity
  DouYinOpenApi douYinOpenApi;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    douYinOpenApi = DouYinOpenApiFactory.create(this);
    douYinOpenApi.handleIntent(getIntent(), this);
  }

  @Override
  public void onReq(BaseReq req) {

  }

  @Override
  public void onResp(BaseResp resp) {
    if (resp.getType() == CommonConstants.ModeType.SHARE_CONTENT_TO_TT_RESP) {
      Share.Response response = (Share.Response) resp;
      Toast.makeText(this, " code：" + response.errorCode + " 文案：" + response.errorMsg, Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(this, RequestAuthActitvity.getClass());
      startActivity(intent);
    } else if (resp.getType() == CommonConstants.ModeType.SEND_AUTH_RESPONSE) {
      Authorization.Response response = (Authorization.Response) resp;
      Intent intent = null;
      if (resp.isSuccess()) {

        Toast.makeText(this, "授权成功，获得权限：" + response.grantedPermissions,
          Toast.LENGTH_LONG).show();

      }
    }

  }

  @Override
  public void onErrorIntent(Intent intent) {
    // 错误数据
    Toast.makeText(this, "Intent出错", Toast.LENGTH_LONG).show();
  }
}
