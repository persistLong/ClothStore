package com.ioter.clothesstrore.common.util;

import android.content.res.AssetManager;
import android.os.Environment;

import com.ioter.clothesstrore.AppApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * 文件处理工具类
 * 
 */
public class FileUtil
{
    private final static String TAG = "FileUtil";

    /**
     * 拷贝文件
     * 
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     */
    public static boolean copyFile(String srcPath, String destPath)
    {
        if (srcPath == null || srcPath.trim().length() < 1 || destPath == null || destPath.trim().length() < 1)
        {
            return false;
        }
        return copyFile(new File(srcPath), new File(destPath));
    }

    /**
     * 拷贝文件
     * 
     * @param srcFile 源文件
     * @param destFile 目标文件
     */
    public static boolean copyFile(File srcFile, File destFile)
    {
        if (srcFile == null || !srcFile.exists() || destFile == null)
        {
            return false;
        }
        FileInputStream fileInputStream = null;
        DataInputStream dataInputStream = null;
        FileOutputStream fileOutputStream = null;
        DataOutputStream dataOutputStream = null;
        try
        {
            if (destFile.exists())
            {
                destFile.delete();
            }
            fileInputStream = new FileInputStream(srcFile);
            dataInputStream = new DataInputStream(fileInputStream);
            fileOutputStream = new FileOutputStream(destFile);
            dataOutputStream = new DataOutputStream(fileOutputStream);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = dataInputStream.read(buffer)) != -1)
            {
                dataOutputStream.write(buffer, 0, len);
            }
            dataOutputStream.flush();
            return true;
        } catch (Exception e)
        {
            return false;
        } finally
        {
            if (dataOutputStream != null)
            {
                try
                {
                    dataOutputStream.close();
                }
                catch (IOException e)
                {
                }
            }
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                }
                catch (IOException e)
                {
                }
            }
            if (dataInputStream != null)
            {
                try
                {
                    dataInputStream.close();
                }
                catch (IOException e)
                {
                }
            }
            if (fileInputStream != null)
            {
                try
                {
                    fileInputStream.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }

    /**
     * 把二进制数组保存成SD卡文件
     * 
     * @param array
     * @param file
     * @return true表示保存成功
     */
    public static boolean saveFile(byte[] array, File file)
    {
        if (array == null || array.length < 1 || file == null)
        {
            return false;
        }
        FileOutputStream fileOutputStream = null;
        try
        {
            if (file.exists())
            {
                file.delete();
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(array);
            fileOutputStream.flush();
            return true;
        } catch (OutOfMemoryError e)
        {
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                } catch (IOException e2)
                {
                }
                fileOutputStream = null;
            }
            try
            {
                if (file.exists())
                {
                    file.delete();
                }
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(array);
                fileOutputStream.flush();
                return true;
            } catch (Throwable e2)
            {
                return false;
            }
        } catch (Exception e)
        {
            return false;
        } finally
        {
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                } catch (IOException e)
                {
                }
                fileOutputStream = null;
            }
        }
    }
    
    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath)
    {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try
        {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1024];
            int len;
            while ((len = fis.read(b)) != -1)
            {
                bos.write(b, 0, len);
            }
            bos.flush();
            return bos.toByteArray();
        }
        catch (Exception e)
        {
            return null;
        }
        finally
        {
            if (bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (IOException e1)
                {
                }
            }
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }

    public static boolean writeBytes(String filePath, byte[] buffer)
    {
        OutputStream out = null;
        FileOutputStream fileOut = null;
        try
        {
            File file = new File(filePath);
            if (file.exists())
            {
                file.delete();
            }
            file.createNewFile();

            fileOut = new FileOutputStream(file);

            out = new BufferedOutputStream(fileOut);
            out.write(buffer);
            out.flush();
            return true;
        } catch (Exception e)
        {
            DebugUtil.printe(TAG, "writeBytes , Exception:" + e);
            return false;
        } finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                } catch (IOException e)
                {
                }
            }
            if (fileOut != null)
            {
                try
                {
                    fileOut.close();
                } catch (IOException e)
                {
                }
            }
        }
    }

    /**
     * 获得文件大小的显示
     * 
     * @param size
     * @return
     */
    public static String getFileSizeString(long size)
    {
        StringBuilder sb = new StringBuilder();
        long a;
        long b;
        if (size > 1 * 1024 * 1024 * 1024)
        {
            // 大于1G
            a = size / 1024 / 1024 / 1024;
            b = (size % (1024 * 1024 * 1024)) / (1024 * 1024 * 1024 / 1000);
            b = (((b % 10) < 5) ? (b / 10) : (b / 10 + 1));
            if (b == 100)
            {
                b = 99;
            }
            String bStr = ((b >= 10) ? (b + "") : ("0" + b));
            sb.append(a).append(".").append(bStr).append("GB");
        } else if (size > 1 * 1024 * 1024)
        {
            // 大于1M
            a = size / 1024 / 1024;
            b = (size % (1024 * 1024)) / (1024 * 1024 / 1000);
            b = (((b % 10) < 5) ? (b / 10) : (b / 10 + 1));
            if (b == 100)
            {
                b = 99;
            }
            String bStr = ((b >= 10) ? (b + "") : ("0" + b));
            sb.append(a).append(".").append(bStr).append("MB");
        } else if (size > 1024)
        {
            a = size / 1024;
            sb.append(a).append("KB");
        } else
        {
            sb.append(size).append("B");
        }
        return sb.toString();
    }
    
    public static byte[] readBytes(String fileName)
    {
        RandomAccessFile accessFile = null;
        FileChannel fc = null;
        try
        {
            accessFile = new RandomAccessFile(fileName, "r");
            if (accessFile != null)
            {
                fc = accessFile.getChannel();
                MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
                byte[] result = new byte[(int) fc.size()];
                if (byteBuffer.remaining() > 0)
                {
                    byteBuffer.get(result, 0, byteBuffer.remaining());
                }
                return result;
            }
        } catch (IOException e)
        {
        } finally
        {
            if (fc != null)
            {
                try
                {
                    fc.close();
                } catch (IOException e)
                {
                }
            }
            if (accessFile != null)
            {
                try
                {
                    accessFile.close();
                } catch (IOException e)
                {
                }
            }
        }
        return null;
    }

    /**
     * the traditional io way
     * 
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(String filename) throws IOException
    {

        File f = new File(filename);
        if (!f.exists())
        {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try
        {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size)))
            {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e)
        {
            e.printStackTrace();
            throw e;
        } finally
        {
            try
            {
                in.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    public static void nioTransferCopy(String source, String target)
    {
        File srcFile = new File(source);
        File targetFile = new File(target);
        nioTransferCopy(srcFile, targetFile);
    }

    public static void nioTransferCopy(File source, File target)
    {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try
        {
            target.deleteOnExit();
            target.createNewFile();

            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e)
        {
            DebugUtil.printe("Exception", "Exception:" + e);
        } finally
        {
            if (inStream != null)
            {
                try
                {
                    inStream.close();
                } catch (IOException e)
                {
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                }
            }
            if (outStream != null)
            {
                try
                {
                    outStream.close();
                } catch (IOException e)
                {
                }
            }
            if (out != null)
            {
                try
                {
                    out.close();
                } catch (IOException e)
                {
                }
            }
        }
    }

    public static long getFileSize(String path)
    {
        long size = 0;
        File file = new File(path);
        if (file.exists())
        {
            FileInputStream fis = null;
            try
            {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception e)
            {
            } finally
            {
                if (fis != null)
                {
                    try
                    {
                        fis.close();
                    }
                    catch (IOException e)
                    {
                    }
                }
            }
        }
        return size;
    }

    public static final byte[] File2byteByAssert(String fileName)
    {
        if (fileName == null)
        {
            return null;
        }
        AssetManager am = AppApplication.getApplication().getResources().getAssets();
        try
        {
            InputStream is = am.open(fileName);
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = is.read(buff, 0, 100)) > 0)
            {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();

            is.close();
            return in2b;
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 删除文件和文件目录下的所有文件
     * 
     * @param filePath 文件路径
     */
    public static void deleteFile(String filePath)
    {
        if (filePath == null || filePath.trim().length() < 1)
        {
            return;
        }
        deleteFile(new File(filePath));
    }

    /**
     * 删除文件和文件目录下的所有文件
     * 
     * @param file
     */
    public static void deleteFile(File file)
    {
        if (file == null || !file.exists())
        {
            return;
        }
        if (file.isDirectory())
        {
            File[] fileArray = file.listFiles();
            if (fileArray != null)
            {
                for (File itemFile : fileArray)
                {
                    deleteFile(itemFile);
                }
            }
        }
        file.delete();
    }

    /**
     * 删除指定目录下的所有文件
     * 
     * @param file
     */
    public static void deleteDirectoryFile(File file)
    {
        if (file == null || !file.exists() || !file.isDirectory())
        {
            return;
        }
        File[] fileArray = file.listFiles();
        if (fileArray != null)
        {
            for (File itemFile : fileArray)
            {
                deleteFile(itemFile);
            }
        }
    }

    /**
     * 清除所有内置缓存数据
     */
    public static void cleanInternalCache()
    {
        deleteDirectoryFile(AppApplication.getApplication().getCacheDir());
    }

    /**
     * 清除所有外置缓存数据
     */
    public static void cleanExternalCache()
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            deleteDirectoryFile(AppApplication.getApplication().getExternalCacheDir());
        }
    }

    /**
     * 清除所有SharePrefence数据
     */
    public static void cleanSharePrefence()
    {
        AppApplication app = AppApplication.getApplication();
        File file = new File("/data/data/" + app.getPackageName() + "/shared_prefs");
        deleteDirectoryFile(file);
    }

    /**
     * 清除所有数据库数据
     */
    public static void cleanDatabases()
    {
        AppApplication app = AppApplication.getApplication();
        File file = new File("/data/data/" + app.getPackageName() + "/databases");
        deleteDirectoryFile(file);
    }


}