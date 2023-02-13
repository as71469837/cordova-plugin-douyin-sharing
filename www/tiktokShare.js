var exec = require('cordova/exec');

var tiktokShare = {
    //初始化
    init: function (
        clientKey,//抖音申请的clientkey,不能为空
        callerActitvity,//回调的本地Actitvity,可为空
        success,
        error) {
        exec(success, error, 'TiktokShare', 'init', [clientKey, callerActitvity]);
    },

    //发送授权请求
    requestAuth: function (success, error) {
        cordova.require('cordova/channel').onCordovaReady.subscribe(function () {
            exec(success, error, 'TiktokShare', 'requestAuth', []);
        });
    },

    //分享到编辑页
    shareToEditPage(success, error) {
        cordova.require('cordova/channel').onCordovaReady.subscribe(function () {
            exec(success, error, 'TiktokShare', 'shareToEditPage', []);
        });
    },
    //分享到发布页
    shareToPublishPage(success, error) {
        cordova.require('cordova/channel').onCordovaReady.subscribe(function () {
            exec(success, error, 'TiktokShare', 'shareToPublishPage', []);
        });
    },

    //分享图片给联系人，暂时只支持单张图片
    shareImageToContacts(success, error) {
        cordova.require('cordova/channel').onCordovaReady.subscribe(function () {
            exec(success, error, 'TiktokShare', 'shareImageToContacts', []);
        });
    },

    //分享网页给联系人
    shareHtmlToContacts: function (
        htmlUri,//分享的网页地址链接,不能为空
        discription,//分享的网页描述,不能为空
        title,//分享的网页标题,不能为空
        thumbUrl,//分享的网页封面图，可以为空
        success, error) {
        cordova.require('cordova/channel').onCordovaReady.subscribe(function () {
            exec(success, error, 'TiktokShare', 'shareHtmlToContacts', [htmlUri, discription, title, thumbUrl]);
        });
    },

    //打开抖音拍摄页
    openDouYinCapturePage: function (success, error) {
        cordova.require('cordova/channel').onCordovaReady.subscribe(function () {
            exec(success, error, 'TiktokShare', 'openDouYinCapturePage', []);
        });
    }

};

module.exports = tiktokShare;
