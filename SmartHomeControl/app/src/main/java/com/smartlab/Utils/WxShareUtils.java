package com.smartlab.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

public class WxShareUtils {
    public static final String APP_ID = "wx9152fc292f3b251f";

    public enum WxShareType {
        TO_PEOPLE, TO_FRIEND_CIRCLE
    }

    public static void shareImage(Context context, Bitmap thumbBmp, WxShareType type) {
        // 通过appId得到IWXAPI这个对象
        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, APP_ID);
        // 检查手机或者模拟器是否安装了微信
        if (!wxapi.isWXAppInstalled()) {
            return;
        }
//        Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.add_device);

        // 初始化一个WXWebpageObject对象
        WXImageObject imagePageObject = new WXImageObject(thumbBmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imagePageObject;
//设置缩略图
        msg.thumbData = btmToByteArray(thumbBmp);
        thumbBmp.recycle();

//构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "睿管家";
        req.message = msg;
        if (type == WxShareType.TO_PEOPLE) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
//调用api接口，发送数据到微信
        wxapi.sendReq(req);
    }

    private static byte[] btmToByteArray(Bitmap bmp) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 10,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    public static Bitmap getImageFromView(Activity activity) {
        View dView = activity.getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        return Bitmap.createBitmap(dView.getDrawingCache());
    }
}
