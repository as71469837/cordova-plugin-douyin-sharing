package huayu.cordova.plugin.share.douyin;


import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.base.ImageAlbumObject;
import com.bytedance.sdk.open.aweme.base.ImageObject;
import com.bytedance.sdk.open.aweme.base.MediaContent;
import com.bytedance.sdk.open.aweme.base.MicroAppInfo;
import com.bytedance.sdk.open.aweme.base.VideoObject;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;

import com.bytedance.sdk.open.douyin.DouYinOpenConfig;
import com.bytedance.sdk.open.douyin.ShareToContact;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import com.bytedance.sdk.open.douyin.model.ContactHtmlObject;
import com.bytedance.sdk.open.douyin.model.OpenRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * This class echoes a string called from JavaScript.
 */
public class TiktokShare extends CordovaPlugin {

  public  static DouYinOpenApi CurrentDouYinOpenApi;
  private CallbackContext currentCallbackContext;
  private Activity currentActivity;//当前加载此插件的Activity;
  private String clientKey; //抖音申请的clientkey;
  private boolean isInited = false;//是否已经初始化;
  private DouYinOpenApi douYinOpenApi;
  private String userInfoScope = "user_info";
  private String currentAction;//当前请求的Action
  private ArrayList<String> fileUriList = new ArrayList<>();
  private boolean useFileProvider = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? true : false;
  private  String callerLocalActivity="huayu.cordova.plugin.share.douyin.AuthResponseActivity";//回调的本地Activity

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    currentCallbackContext = callbackContext;
    currentAction = action;
    if (action.equals("init")) {
      String clientKey =args.optJSONObject(0)!=null? args.optString(0,""):null;
      if(args.optJSONObject(1)!=null){
        String temp= args.optString(1,null);
        if(temp!=null && !temp.isEmpty()){
          this.callerLocalActivity=temp;
        }
      }
      init(clientKey, callbackContext);
      return true;
    } else if (action.equals("requestAuth")) {
      requestAuth(callbackContext);
      return true;
    } else if (action.equals("shareToEditPage")) {
      cordova.requestPermissions(this, Options.Request_Permissions_Code,Options.Required_Permissions);
      return true;
    } else if (action.equals("shareToPublishPage")) {
      cordova.requestPermissions(this, Options.Request_Permissions_Code,Options.Required_Permissions);
      return true;
    } else if (action.equals("shareImageToContacts")) {
      cordova.requestPermissions(this, Options.Request_Permissions_Code,Options.Required_Permissions);
      return true;
    }  else if (action.equals("shareHtmlToContacts")) {
      if(args.length()<5){
        callbackContext.error("参数有误，请检查相关代码");
      }
      String htmlUri = args.getString(0);
      String htmlDiscription=args.getString(1);
      String title=args.getString(2);
      String thumbUrl=args.optString(3,null);
      shareHtmlToContacts(htmlUri,htmlDiscription,title,thumbUrl,callbackContext);
      return true;
    } else if (action.equals("openDouYinCapturePage")) {
      String shareId =args.length()>0? args.optString(0,null):null;
      openDouYinCapturePage(shareId,callbackContext);
      return true;
    }else  if(action.equals("openDouYinMiniProgramCapturePage")){
      if(args.length()<5){
        callbackContext.error("参数有误，请检查相关代码");
      }
      String appId = args.getString(0);
      String appTitle=args.getString(1);
      String appUrl=args.getString(2);
      String appDiscription=args.optString(3,null);
      openDouYinMiniProgramCapturePage(appId,appTitle,appUrl,appDiscription,callbackContext);
      return true;
    }
    return false;
  }

  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
    throws JSONException {
    if (requestCode == Options.Request_Permissions_Code) {
      for (int r : grantResults) {
        if (r == PackageManager.PERMISSION_DENIED) {
          currentCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
          return;
        }
      }
      if (checkInitState()) {
        if (currentAction.equals("shareToEditPage")) {
          openSystemGallery(Options.Action_Type_ShareToEditPage);
//          shareToEditPage(currentCallbackContext);
        } else if (currentAction.equals("shareToPublishPage")) {
          openSystemGallery(Options.Action_Type_ShareToPublishPage);
//          shareToPublishPage(currentCallbackContext);
        } else if (currentAction.equals("shareImageToContacts")) {
          openSystemGallery(Options.Action_Type_ShareImageToContacts);
//          shareImageToContacts(currentCallbackContext);
        }
      }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (resultCode == Activity.RESULT_OK) {
      Uri uri = intent.getData();
      switch (requestCode)
      {
        case Options.Request_Photo_Gallery_Code+Options.Action_Type_ShareToEditPage:
          fileUriList.add(huayu.cordova.plugin.share.douyin.UriUtil.convertUriToPath(cordova.getContext(), uri));
          shareToEditPage(currentCallbackContext);
          break;
        case Options.Request_Photo_Gallery_Code+Options.Action_Type_ShareToPublishPage:
          fileUriList.add(huayu.cordova.plugin.share.douyin.UriUtil.convertUriToPath(cordova.getContext(), uri));
          shareToPublishPage(currentCallbackContext);
          break;
        case Options.Request_Photo_Gallery_Code+Options.Action_Type_ShareImageToContacts:
          fileUriList.add(huayu.cordova.plugin.share.douyin.UriUtil.convertUriToPath(cordova.getContext(), uri));
          shareImageToContacts(currentCallbackContext);
          break;
        default:
          break;
      }
    } else if (resultCode == Activity.RESULT_CANCELED) {

    }
  }

  /**
   * 初始化
   *
   * @param clientKey       抖音开放平台申请的key
   * @param callbackContext
   */
  private void init(String clientKey, CallbackContext callbackContext) {
    if (clientKey==null||clientKey.isEmpty()) {
      callbackContext.error("请传入正确的clientkey，如果没有需前往抖音开放平台申请");
    }
    this.currentActivity = cordova.getActivity();
    this.clientKey = clientKey;
//    DouYinOpenApiFactory.init(new DouYinOpenConfig(this.clientKey));
//    this.douYinOpenApi = DouYinOpenApiFactory.create(this.currentActivity);

    this.douYinOpenApi = DouYinOpenApiFactory.create(this.currentActivity, new DouYinOpenConfig(this.clientKey));
    huayu.cordova.plugin.share.douyin.AuthResponseActivity.RequestAuthActivity = this.currentActivity;
    CurrentDouYinOpenApi=this.douYinOpenApi;
    this.isInited = true;
    callbackContext.success(clientKey);
  }

  private boolean checkInitState() {
    if (!this.isInited) {
      currentCallbackContext.error("未初始化");
      return false;
    }
    return true;
  }

  private boolean checkInitState(CallbackContext callbackContext) {
    if (!this.isInited) {
      callbackContext.error("未初始化");
      return false;
    }
    return true;
  }

  /**
   * 发送授权请求
   *
   * @param callbackContext
   */
  private void requestAuth(CallbackContext callbackContext) {
    if (!checkInitState(callbackContext)) {
      return;
    }
    Authorization.Request request = new Authorization.Request();
    request.callerLocalEntry=this.callerLocalActivity;
    request.scope = userInfoScope;                          // 用户授权时必选权限
    request.optionalScope0 = "mobile";     // 用户授权时可选权限（默认选择）
    //request.optionalScope0 = mOptionalScope1;    // 用户授权时可选权限（默认不选）
    request.state = "requestAuth";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。

    boolean tempResult = douYinOpenApi.authorize(request);               // 优先使用抖音app进行授权，如果抖音app因版本或者其他原因无法授权，则使用wap页授权
    if (tempResult) {
      callbackContext.success();
    } else {
      callbackContext.error("发送授权请求失败");
    }
  }

  /**
   * 分享到编辑页
   *
   * @param callbackContext
   */
  private void shareToEditPage(CallbackContext callbackContext) {
    Share.Request request = new Share.Request();
    request.callerLocalEntry=this.callerLocalActivity;

//    //旧分享单图/多图
//    ImageObject imageObject = new ImageObject();
//    imageObject.mImagePaths = fileUriList;
//    MediaContent mediaContent = new MediaContent();
//    mediaContent.mMediaObject = imageObject;
//    request.mMediaContent = mediaContent;

//
    //新分享单图/多图 支持开启图集模式
    //需升级到0.1.9.4以上
    boolean isImageAlbum = false; //是否开启图集模式
    ImageAlbumObject imageAlbumObject = new ImageAlbumObject(fileUriList,isImageAlbum);
    MediaContent mediaContent = new MediaContent();
    mediaContent.mMediaObject = imageAlbumObject;
    request.mMediaContent = mediaContent;
//
//    // 分享视频
//    VideoObject videoObject = new VideoObject();
//    videoObject.mVideoPaths = fileUriList;
//    MediaContent videoContent = new MediaContent();
//    videoContent.mMediaObject = videoObject;
//    request.mMediaContent = videoContent;
//
//    // 分享混合内容（要求SDK版本至少为0.1.7.0，抖音版本至少为17.4）
//    if (douYinOpenApi.isAppSupportMixShare()) {
//      MixObject mixObject = new MixObject();
//      mixObject.mMediaPaths = fileUriList;
//      MediaContent mixContent = new MediaContent();
//      mixContent.mMediaObject = mixObject;
//      request.mMediaContent = mixContent;
//    }
    douYinOpenApi.share(request);
  }

  /**
   * 分享到发布页
   *
   * @param callbackContext
   */
  private void shareToPublishPage(CallbackContext callbackContext) {
    // 初始化资源路径，路径请提供绝对路径.demo里有获取绝对路径的util代码
    Share.Request request = new Share.Request();
    request.callerLocalEntry=this.callerLocalActivity;

    // 只能分享一个视频
    VideoObject videoObject = new VideoObject();
    videoObject.mVideoPaths = fileUriList;

    MediaContent content = new MediaContent();
    content.mMediaObject = videoObject;
    request.mMediaContent = content;
    if (douYinOpenApi.isAppSupportShareToPublish()) {
      request.shareToPublish = true;
    }

    // 调起分享
    douYinOpenApi.share(request);
  }

  /**
   * 分享图片给联系人，暂时只支持单张本地图片
   *
   * @param callbackContext
   */
  private void shareImageToContacts(CallbackContext callbackContext) {
    ShareToContact.Request request = new ShareToContact.Request();
    request.callerLocalEntry=this.callerLocalActivity;
    // 注意： 只能传入一张图片，目前只支持单图且为本地图片
    // 分享单图
    ImageObject imageObject = new ImageObject();
    imageObject.mImagePaths = fileUriList;
    MediaContent mediaContent = new MediaContent();
    mediaContent.mMediaObject = imageObject;
    request.mMediaContent = mediaContent;
// 判断是否可以分享到联系人
    if (douYinOpenApi.isAppSupportShareToContacts()) {
      // 调起分享
      douYinOpenApi.shareToContacts(request);
    } else {
      callbackContext.error("当前抖音版本不支持");
    }
  }


  /**
   * 分享网页给联系人
   * @param htmlUri
   * @param htmlDiscription
   * @param title
   * @param thumbUrl
   * @param callbackContext
   */
  private void shareHtmlToContacts(String htmlUri, String htmlDiscription, String title, String thumbUrl, CallbackContext callbackContext) {

    if(htmlUri==null || htmlUri.isEmpty()){
      callbackContext.error("分享的网页地址链接不能为空");
    }
    if(htmlDiscription==null ||htmlDiscription.isEmpty()){
      callbackContext.error("分享的网页描述不能为空");
    }
    if(title==null ||title.isEmpty()){
      callbackContext.error("分享的网页标题不能为空");
    }
    ShareToContact.Request request = new ShareToContact.Request();
    request.callerLocalEntry=this.callerLocalActivity;

    // 分享html
    ContactHtmlObject htmlObject = new ContactHtmlObject();
// 你的html链接（必填）
    htmlObject.setHtml(htmlUri);
// 你的html描述（必填）
    htmlObject.setDiscription(htmlDiscription);
// 你的html  title（必填）
    htmlObject.setTitle(title);
    if (title!=null && !thumbUrl.isEmpty()) {
      // 你的html的封面图(远程图片) （选填，若不填，则使用开放平台官网申请时上传的图标）
      htmlObject.setThumbUrl("thumbUrl");
    }
    request.htmlObject = htmlObject;
  // 调起分享
    if (douYinOpenApi.isAppSupportShareToContacts()) {
      douYinOpenApi.shareToContacts(request);
      callbackContext.success();
    } else {
      callbackContext.error("当前抖音版本不支持");
    }
  }

  /**
   * 打开抖音拍摄也
   * @param shareId
   * @param callbackContext
   */
  private  void openDouYinCapturePage(String shareId,CallbackContext callbackContext){
    OpenRecord.Request request = new OpenRecord.Request();

    // 添加hashtag
    ArrayList<String> tags = new ArrayList<>();
    if (tags.size() > 0) {
      request.mHashTagList = tags;
    }
    // 填写你的shareId，如果无则不填写
    if(shareId!=null ||!shareId.isEmpty()){
      request.mState =shareId;
    }

    if(douYinOpenApi !=null&&douYinOpenApi.isSupportOpenRecordPage()) {
      // 判断抖音版本是否支持打开抖音拍摄页
      douYinOpenApi.openRecordPage(request);
    }else{
      callbackContext.error("当前抖音版本不支持");
    }
  }


  /**
   * 打开抖音小程序拍摄页
   * @param appId
   * @param appTitle
   * @param appUrl
   * @param appDescription
   * @param callbackContext
   */
  private  void openDouYinMiniProgramCapturePage(String appId, String appTitle, String appUrl, String appDescription,CallbackContext callbackContext){
    OpenRecord.Request request = new OpenRecord.Request();

    // 添加hashtag
    ArrayList<String> tags = new ArrayList<>();
    if (tags.size() > 0) {
      request.mHashTagList = tags;
    }
    if(appId==null || appId.isEmpty()){
      callbackContext.error("小程序ID不能为空");
    }
    if(appTitle==null ||appTitle.isEmpty()){
      callbackContext.error("小程序标题不能为空");
    }
    if(appUrl==null ||appUrl.isEmpty()){
      callbackContext.error("小程序Url不能为空");
    }
    // 如有需求可添加小程序，需申请小程序权限
    MicroAppInfo mMicroInfo = new MicroAppInfo();
    mMicroInfo.setAppTitle(appTitle);
    mMicroInfo.setAppId(appId);
    mMicroInfo.setAppUrl(appUrl);
    if(appDescription!=null ||!appDescription.isEmpty()){
      mMicroInfo.setDescription(appDescription);
    }
    request.mMicroAppInfo = mMicroInfo;

    // 填写你的shareid，如果无则不填写
    request.mState ="state";
    if(douYinOpenApi !=null&&douYinOpenApi.isSupportOpenRecordPage()) {
      // 判断抖音版本是否支持打开抖音拍摄页
      douYinOpenApi.openRecordPage(request);
    }else{
      callbackContext.error("当前抖音版本不支持");
    }
  }

  /**
   * 打开系统相册
   */
  private void openSystemGallery(int actionType) {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
//    intent.setType("/*");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
    } else {
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    }
    int requestCode=Options.Request_Photo_Gallery_Code+actionType;
    cordova.startActivityForResult(this, intent, requestCode);
  }

  private ArrayList<String> convert2FileProvider() {
    ArrayList<String> result = new ArrayList<>();
    for (String path : fileUriList) {
      try {
        String[] uriParts = path.split("\\.");
        if (uriParts.length > 0) {
          Context context = cordova.getContext();
          String suffix = uriParts[uriParts.length - 1];
          File file = new File(context.getExternalFilesDir(null), "/newMedia");
          file.mkdirs();
          File tempFile = File.createTempFile("share_", "." + suffix, file);
          if (copyFile(new File(path), tempFile)) {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", tempFile);
            context.grantUriPermission("com.ss.android.ugc.aweme", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            result.add(uri.toString());
          }
        }
      } catch (Exception e) {

      }
    }
    return result;
  }

  private boolean copyFile(File src, File dest) {
    boolean result = false;
    if (src == null || dest == null) {
      return false;
    }
    if (dest.exists()) {
      dest.delete();
    }

    FileChannel srcChannel = null;
    FileChannel destChannel = null;
    try {
      dest.createNewFile();
      srcChannel = new FileInputStream(src).getChannel();
      destChannel = new FileOutputStream(dest).getChannel();
      srcChannel.transferTo(0, srcChannel.size(), destChannel);
      result = true;
    } catch (Exception e) {

    } finally {
      try {
        if (srcChannel != null) {
          srcChannel.close();
        }
        if (srcChannel != null) {
          destChannel.close();
        }
      } catch (Exception e) {
      }
    }

    return result;
  }


}
