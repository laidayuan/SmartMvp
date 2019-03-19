package com.dada.marsframework.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;


import com.dada.marsframework.base.FrameworkConfig;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by laidayuan on 2018/2/11.
 */

public class FileUtils {

//    public final static String DEFAULT_FOLDER = "ka";
//
//    public static final String pmFolder = DEFAULT_FOLDER + File.separator;
//
//    //app 的文件默认路径，需要存储权限
//    public static final String appSdPath = Environment.getExternalStorageDirectory()
//            + File.separator + pmFolder;

    //app 下载路径
    private static final String pmDownloadsDir = getSDCardRootPath() + "downloads" + File.separator;

    //app 日志路径
    private static final String pmLogDir = getSDCardRootPath() + "logs" + File.separator;

    //app 图片下载路径
    private static final String pmImagesDir = getSDCardRootPath() + "images" + File.separator;

    //app 缓存路径
    private static final String pmCachesDir = getSDCardRootPath() + "caches" + File.separator;

    //字体路径
    public static final String FONT_FOLDER = getSDCardRootPath() + "fonts" + File.separator;

    //不需要存储权限的app私有空间,文件会随app卸载而清除
    public static File getAppFilePath(Context appContext) {
        String pmFolder = FrameworkConfig.getDefaultFolder() + File.separator;
        File file = appContext.getExternalFilesDir(pmFolder);
        return file;
    }

    public static boolean createPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return true;
    }

    public static File getLogFile(String fileNmae) {
        String path = pmLogDir;
        createPath(path);
        File file = new File(path + fileNmae);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getSDCardRootPath() {
        String path = Environment.getExternalStorageDirectory() + File.separator + FrameworkConfig.getDefaultFolder() + File.separator;
        createPath(path);

        return path;
    }

    public static String getDownloadPath() {
        String path = pmDownloadsDir;
        createPath(path);

        return path;
    }

    public static String getImagesPath() {
        String path = pmImagesDir;
        createPath(path);

        return path;
    }

    public static String getCachesPath() {
        String path = pmCachesDir;
        createPath(path);

        return path;
    }

    /**
     * 检测SD卡是否存在
     */
    public static boolean checkSDcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 安装apk
     *
     * @param context
     * @param filePath
     *
     */
    public static void installApk(Context context, String filePath) {
        if (context == null || StringUtils.isEmpty(filePath)) {
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            LogUtils.e(filePath + " file no exists!");
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory("android.intent.category.DEFAULT");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String authority = context.getPackageName() + ".fileProvider";
                Uri contentUri = FileProvider.getUriForFile(context, authority, file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String isFileExist(String strFile) {
        String strRet = null;
        boolean bRet = false;
        if (StringUtils.isNotEmpty(strFile)) {
            File file = new File(strFile);
            if (file != null && file.exists()) {
                bRet = true;
                strRet = strFile;
            }

            if (!bRet) {
                String strPath = strFile.substring(4);
                File f = new File(strPath);
                if (f != null && f.exists()) {
                    bRet = true;
                    strRet = strPath;
                }
            }
        }

        return strRet;
    }

    public static String copyAssetFile(Context c, String fileName) {
        String savePath = getCachesPath() + fileName;
        if (isFileExist(savePath) != null) {

            return savePath;
        }

        AssetManager assetManager = c.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(fileName);
            savePath = saveRAFile(inputStream, fileName);
            LogUtils.e("copyAssetFile ()  savePath = " + savePath);
        } catch (IOException e) {
            LogUtils.e( e.getMessage());
        }

        return savePath;
    }


    private static final int IO_BUFFER_SIZE = 8 * 1024;

    public static String saveRAFile(InputStream is, String fileName) {
        BufferedRandomAccessFile brafWriteFile = null;
        try {
            String savePath = getCachesPath() + fileName;

            brafWriteFile = new BufferedRandomAccessFile(savePath, "rw", 13);
            if (brafWriteFile != null) {
                byte data[] = new byte[IO_BUFFER_SIZE];
                int bytesRead = 0;
                long start = System.currentTimeMillis();
                int nHashRead = 0;
                while ((bytesRead = is.read(data)) != -1) {
                    nHashRead += bytesRead;
                    brafWriteFile.writeData(data, 0, bytesRead);
                }

                if (is != null) {
                    is.close();
                    is = null;
                }

                data = null;
            }

            return savePath;
        } catch (Exception e) {
            LogUtils.e("", "saveRAFile()  Exception e = " + e);
        } finally {
            try {
                if (brafWriteFile != null) {
                    brafWriteFile.close();
                    brafWriteFile = null;
                }
            } catch (IOException e) {
                LogUtils.e("", "saveRAFile()  Exception e1 = " + e);
            }
        }

        return null;
    }


    public static void deleteDir(String filepath) {
        try {
            File f = new File(filepath);// 定义文件路径
            if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
                if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
                    f.delete();
                } else {// 若有则把文件放进数组，并判断是否有下级目录
                    File delFile[] = f.listFiles();
                    int i = delFile.length;
                    for (int j = 0; j < i; j++) {
                        if (delFile[j].isDirectory()) {
                            deleteDir(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                        } else {
                            delFile[j].delete();// 删除文件
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileName(String pathandname) {
        if (pathandname == null || pathandname.length() == 0) {
            return null;
        }

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1 && start < end
                && end < pathandname.length()) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }
    }


    public static String getNameFromUrl(String url) {
        if (url == null || url.length() == 0) {
            return null;
        }

        int start = url.lastIndexOf("/");

        if (start != -1 ) {
            return url.substring(start + 1);
        } else {
            return null;
        }
    }

    public static boolean deleteFile(String fullFileName) {
        boolean bRet = false;
        if (isFileExist(fullFileName) != null) {
            File file = new File(fullFileName);
            if (file.exists()) {
                file.delete();
                bRet = true;
            }
        }

        return bRet;
    }


    public static void writeJsonFile(String fileName, String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            return;
        }

        if (fileName.lastIndexOf(".json") == -1) {
            fileName += ".json";
        }

        String strFilePath = getCachesPath() + fileName;

        File file = new File(strFilePath);
        if (file != null && file.exists()) {
            file.delete();
            file = null;
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(strFilePath);
            PrintWriter out = new PrintWriter(fw);
            out.write(jsonString);
            out.println();
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    public static String readJsonFile(String fileName) {
        if (fileName.lastIndexOf(".json") == -1) {
            fileName += ".json";
        }

        String strFilePath = getCachesPath() + fileName;
        File file = new File(strFilePath);
        //LogUtil.e("", "readJsonFile " + strFilePath);
        if (!file.exists()) {
            return null;
        }

        String data = readFile(strFilePath);

        return data;
    }

    public static boolean deleteJsonFile(String fileName) {
        boolean bRet = false;
        String strFilePath = getCachesPath() + fileName;
        bRet = deleteFile(strFilePath);

        return bRet;
    }


    /**
     * 从文件中读取文本
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (Exception e) {
            throw new RuntimeException(FileUtils.class.getName()
                    + "readFile---->" + filePath + " not found");
        }
        return inputStream2String(is);
    }

    /**
     * 从assets中读取文本
     *
     * @param name
     * @return
     */
    public static String readFileFromAssets(Context context, String name) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(name);
        } catch (Exception e) {
            throw new RuntimeException(FileUtils.class.getName()
                    + ".readFileFromAssets---->" + name + " not found");
        }

        return inputStream2String(is);
    }

    /**
     * 输入流转字符串
     *
     * @param is
     * @return 一个流中的字符串
     */
    public static String inputStream2String(InputStream is) {
        if (null == is) {
            return null;
        }
        StringBuilder resultSb = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            resultSb = new StringBuilder();
            String len;
            while (null != (len = br.readLine())) {
                resultSb.append(len);
            }
        } catch (Exception ex) {
        } finally {
            closeIO(is);
        }
        return null == resultSb ? null : resultSb.toString();
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                throw new RuntimeException(
                        FileUtils.class.getClass().getName(), e);
            }
        }
    }


    public static boolean saveBitmapToDisk(Bitmap bitmap, String fileName) {
        boolean bRet = false;
        FileOutputStream fis = null;
        String dfullName = fileName;
        try {
            File file = new File(dfullName);
            // if(!file.getParentFile().exists()) {
            // file.getParentFile().mkdirs();
            // }

            if (file.exists()) {
                file.delete();
            }

            try {

                fis = new FileOutputStream(dfullName);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fis);// 把数据写入文件

                bRet = true;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("utils", "e1 = " + e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("utils", "e2 = " + e);
        } finally {
            if (fis != null) {
                try {
                    fis.flush();
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    LogUtils.e("utils", "e = " + e);
                }
            }
        }

        return bRet;
    }
}
