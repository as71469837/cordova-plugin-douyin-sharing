package huayu.cordova.plugin.share.douyin;

import android.Manifest;

import java.util.Arrays;

public class Options {
  /**
   * 需要的权限
   */
  public static final String[] Required_Permissions = new String[]{
    Manifest.permission.INTERNET,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
  };
  /**
   * 请求权限时使用的代码
   */
  public static final int Request_Permissions_Code = 121212;

  /**
   * 请求系统相册时使用的代码
   */
  public static final int Request_Photo_Gallery_Code = 1231;

  /**
   * 初始化时对应的动作类型
   */
  public static final int Action_Type_Init = 1;

  /**
   * 请求授权时对应的动作类型
   */
  public static final int Action_Type_RequestAuth = 10;
  /**
   * 分享到编辑页时对应的动作类型
   */
  public static final int Action_Type_ShareToEditPage = 20;
  /**
   * 分享到发布页时对应的动作类型
   */
  public static final int Action_Type_ShareToPublishPage = 21;
  /**
   * 分享图片到联系人时对应的动作类型
   */
  public static final int Action_Type_ShareImageToContacts = 25;
  /**
   * 分享网页到联系人时对应的动作类型
   */
  public static final int Action_Type_ShareHtmlToContacts = 26;
  /**
   * 打开抖音拍摄页时对应的动作类型
   */
  public static final int Action_Type_OpenDouYinCapturePage = 50;
}
