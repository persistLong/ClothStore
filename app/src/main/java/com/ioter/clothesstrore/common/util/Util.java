package com.ioter.clothesstrore.common.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;

import java.util.HashMap;
import java.util.Locale;

public class Util
{


    public static byte[] hexStringToBytes(String hexString)
    {
        if (hexString != null && !hexString.equals(""))
        {
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];

            for (int i = 0; i < length; ++i)
            {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }

            return d;
        } else
        {
            return null;
        }
    }

    public static short[] hexStringToShorts(String hexString)
    {
        if (hexString != null && !hexString.equals(""))
        {
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            short[] d = new short[length];

            for (int i = 0; i < length; ++i)
            {
                int pos = i * 2;
                d[i] = (short) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }

            return d;
        } else
        {
            return null;
        }
    }

    private static byte charToByte(char c)
    {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * byte[]数组转换为16进制的字符串
     *
     * @param data 要转换的字节数组
     * @return 转换后的结果
     */
    public static final String byteArrayToHexString(byte[] data)
    {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data)
        {
            int v = b & 0xff;
            if (v < 16)
            {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.getDefault());
    }

    /**
     * 把byte数组转化成2进制字符串
     *
     * @param bArr
     * @return
     */
    public static String getBinaryStrFromByteArr(byte[] bArr)
    {
        String result = "";
        for (byte b : bArr)
        {
            result += getBinaryStrFromByte(b);
        }
        return result;
    }

    /**
     * 把byte转化成2进制字符串
     *
     * @param b
     * @return
     */
    public static String getBinaryStrFromByte(byte b)
    {
        String result = "";
        byte a = b;
        ;
        for (int i = 0; i < 8; i++)
        {
            byte c = a;
            a = (byte) (a >> 1);// 每移一位如同将10进制数除以2并去掉余数。
            a = (byte) (a << 1);
            if (a == c)
            {
                result = "0" + result;
            } else
            {
                result = "1" + result;
            }
            a = (byte) (a >> 1);
        }
        return result;
    }


    public static Bitmap getVideoThumbnail(String url)
    {
        Bitmap bitmap = null;
        //MediaMetadataRetriever 是android中定义好的一个类，提供了统一
        //的接口，用于从输入的媒体文件中取得帧和元数据；
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try
        {
            //根据文件路径获取缩略图
            retriever.setDataSource(url, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        } finally
        {
            retriever.release();
        }
        return bitmap;
    }


    public static Bitmap createBitmapFromVideoPath(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 12) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }
}
