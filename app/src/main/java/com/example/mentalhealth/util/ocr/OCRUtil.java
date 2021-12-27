package com.example.mentalhealth.util.ocr;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;


import com.example.mentalhealth.MyApplication;
import com.example.mentalhealth.bean.OCRResult;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;

/**
 * OCR识别
 */
public class OCRUtil {
    private static final String NAME = "123.jpg";


    /**
     * 通用文字识别
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static OCRResult accurateBasic(String filePath) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
        try {
            //临时文件
            File tmpFile = new File(MyApplication.myApplication.getFilesDir() + "/" + NAME);
            if (tmpFile.exists()) {
                tmpFile.delete();//存在即删除
            }
            sizeCompress(BitmapFactory.decodeFile(filePath), tmpFile);//压缩大小
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(MyApplication.myApplication.getFilesDir() + "/" + NAME);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();//获取token

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            return new Gson().fromJson(result, OCRResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 尺寸压缩（通过缩放图片像素来减少图片占用内存大小）
     *
     * @param bmp
     * @param file
     */

    public static void sizeCompress(Bitmap bmp, File file) {
        // 尺寸压缩倍数,值越大，图片尺寸越小
        if (bmp.getWidth() >= 4096 || bmp.getHeight() >= 4096) {
            int ratio = 4;
            // 压缩Bitmap到对应尺寸
            Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
            canvas.drawBitmap(bmp, null, rect, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 把压缩后的数据存放到baos中
            result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 把压缩后的数据存放到baos中
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}