package com.noknown.framework.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;

import com.noknown.framework.common.util.StringUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.noknown.framework.common.util.algo.MD5Util;

public class FileUtil {
    private static final String defaultBasePath = "/mnt/sure/yb";
    private static final String uploadDir = "/upload/";

    /**
     * 获取文件的的MD5值
     * @param file
     * @return
     */
    public static String getFileMD5Code(File file){
        try {
            return MD5Util.getSignature(file);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件新名字
     *
     * @return 文件新名字
     */
    private static String getNewFileName() {

        java.util.Date dt = new java.util.Date(System.currentTimeMillis());
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String time = fmt.format(dt);

        return time;
    }

    /**
     * 获取文件名后缀
     *
     * @param realFileName 文件原始名字
     * @return 文件名后缀
     */
    private static String getFileExtension(String realFileName) {
        int idx = realFileName.lastIndexOf(".");
        //文件后缀  
        String extention = realFileName.substring(idx);

        return extention;
    }

    /**
     * 上传文件
     *
     * @param request     请求
     * @param fileKeyName 文件key名称
     * @param userName    用户名
     * @param module      所属模块
     * @param fileType    文件类型
     * @return 文件路径
     */
    public static String fileUpload(HttpServletRequest request, String fileKeyName,
                                    String basePath, String userName, String module, String fileType) {
        String filePath = "";
        if (basePath == null || basePath.equals(""))
            basePath = defaultBasePath;

        String fileDir = basePath + uploadDir + userName + "/" + module + "/" + fileType + "/";
        String upDir = uploadDir + userName + "/" + module + "/" + fileType + "/";

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(fileKeyName);
        String realFileName = file.getOriginalFilename();
        String extension = getFileExtension(realFileName);
        String file_name = getNewFileName();
        String newFileName = file_name + extension;

        File folder = new File(fileDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File uploadFile = new File(fileDir + newFileName);

        try {
            file.getFileItem().write(uploadFile);
            filePath = upDir + newFileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }

    /**
     * 下载网络文件
     *
     * @param httpUrl  网络地址
     * @param saveFile 保存的路径
     */
    public static boolean httpDownload(String httpUrl, String saveFile) {
        // 下载网络文件
        int byteread = 0;

        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return false;
        }

        FileOutputStream fs = null;

        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();

            File file = new File(saveFile);
            String pp = file.getParent();

            File fpp = new File(pp);
            if (!fpp.exists()) {
                fpp.mkdirs();
            }

            fs = new FileOutputStream(saveFile);

            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fs != null)
                    fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /** 
     * 复制单个文件 
     *  
     * @param srcFileName 
     *            待复制的文件名 
     * @param destFileName
     *            目标文件名 
     * @param overlay 
     *            如果目标文件存在，是否覆盖 
     * @return 如果复制成功返回true，否则返回false 
     */  
    public static boolean copyFile(String srcFileName, String destFileName,  
            boolean overlay) {  
        File srcFile = new File(srcFileName);  
  
        // 判断源文件是否存在  
        if (!srcFile.exists()) {  
            return false;  
        } else if (!srcFile.isFile()) {  
            return false;  
        }  
  
        // 判断目标文件是否存在  
        File destFile = new File(destFileName);  
        if (destFile.exists()) {  
            // 如果目标文件存在并允许覆盖  
            if (overlay) {  
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件  
                new File(destFileName).delete();  
            }  
        } else {  
            // 如果目标文件所在目录不存在，则创建目录  
            if (!destFile.getParentFile().exists()) {  
                // 目标文件所在目录不存在  
                if (!destFile.getParentFile().mkdirs()) {  
                    // 复制文件失败：创建目标文件所在目录失败  
                    return false;  
                }  
            }  
        }  
  
        // 复制文件  
        int byteread = 0; // 读取的字节数  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(srcFile);  
            out = new FileOutputStream(destFile);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            return true;  
        } catch (FileNotFoundException e) {  
            return false;  
        } catch (IOException e) {  
            return false;  
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /** 
     * 复制整个目录的内容 
     *  
     * @param srcDirName 
     *            待复制目录的目录名 
     * @param destDirName 
     *            目标目录名 
     * @param overlay 
     *            如果目标目录存在，是否覆盖 
     * @return 如果复制成功返回true，否则返回false 
     */  
    public static boolean copyDirectory(String srcDirName, String destDirName,  
            boolean overlay) {  
        // 判断源目录是否存在  
        File srcDir = new File(srcDirName);  
        if (!srcDir.exists()) {  
            return false;  
        } else if (!srcDir.isDirectory()) {  
            return false;  
        }  
  
        // 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符  
        if (!destDirName.endsWith(File.separator)) {  
            destDirName = destDirName + File.separator;  
        }  
        File destDir = new File(destDirName);  
        // 如果目标文件夹存在  
        if (destDir.exists()) {  
            // 如果允许覆盖则删除已存在的目标目录  
            if (overlay) {  
                new File(destDirName).delete();  
            } else {  
                return false;  
            }  
        } else {  
            // 创建目的目录  
            System.out.println("目的目录不存在，准备创建。。。");  
            if (!destDir.mkdirs()) {  
                System.out.println("复制目录失败：创建目的目录失败！");  
                return false;  
            }  
        }  
  
        boolean flag = true;  
        File[] files = srcDir.listFiles();  
        for (int i = 0; i < files.length; i++) {  
            // 复制文件  
            if (files[i].isFile()) {  
                flag = copyFile(files[i].getAbsolutePath(),  
                        destDirName + files[i].getName(), overlay);  
                if (!flag)  
                    break;  
            } else if (files[i].isDirectory()) {  
                flag = copyDirectory(files[i].getAbsolutePath(),  
                        destDirName + files[i].getName(), overlay);  
                if (!flag)  
                    break;  
            }  
        }  
        if (!flag) {  
            return false;  
        } else {  
            return true;  
        }  
    }  
    
    /**
     * 压缩文件/目录
     *
     * @param zipFileName 压缩后文件
     * @param inputFile   带压缩文件/目录
     * @throws Exception
     */
    public static void zip(String zipFileName, File inputFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip(out, inputFile, "");
        out.close();
    }

    /**
     * 压缩文件/目录
     *
     * @param base
     * @throws Exception
     */
    public static void zip(ZipOutputStream out, File f, String base) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(f);
            int b;
            System.out.println(base);
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            in.close();
        }
    }


    /**
     * 解压缩zip包  返回 map<压缩包中的压缩文件名，该文件被解压保存的路径>
     *
     * @param zipFilePath        zip文件的全路径
     * @param unzipFilePath      解压后的文件保存的路径
     * @param includeZipFileName 解压后的文件保存的路径是否包含压缩文件的文件名。true-包含；false-不包含
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> unzip(String zipFilePath, String unzipFilePath, boolean includeZipFileName) throws Exception {
        Map<String, String> map = new HashMap<String, String>();

        if (StringUtil.isBlank(zipFilePath) || StringUtil.isBlank(unzipFilePath)) {
            throw new ServiceUnavailableException("压缩文件路径、解压路径不能为空");
        }
        File zipFile = new File(zipFilePath);
        //如果解压后的文件保存路径包含压缩文件的文件名，则追加该文件名到解压路径  
        if (includeZipFileName) {
            String fileName = zipFile.getName();
            if (StringUtil.isNotBlank(fileName)) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            unzipFilePath = unzipFilePath + File.separator + fileName;
        }
        //创建解压缩文件保存的路径  
        File unzipFileDir = new File(unzipFilePath);
        if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
            unzipFileDir.mkdirs();
        }

        //开始解压  
        ZipEntry entry = null;
        String entryFilePath = null, entryDirPath = null;
        File entryFile = null, entryDir = null;
        int index = 0, count = 0, bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        //循环对压缩包里的每一个文件进行解压       
        Boolean ispackage = true;
        while (entries.hasMoreElements()) {
            if (ispackage) {
                entries.nextElement();
                ispackage = false;
            }
            entry = entries.nextElement();
            //构建压缩包中一个文件解压后保存的文件全路径  
            String fileName = entry.getName();//entry.getKey()=压缩包名+"/"+文件名
            String[] str = fileName.replaceAll("\\\\", "/").split("/");
            if (str.length > 1) {
                fileName = str[1];
            }

            entryFilePath = unzipFilePath + File.separator + fileName;

            map.put(fileName, entryFilePath);

            //构建解压后保存的文件夹路径  
            index = entryFilePath.lastIndexOf(File.separator);
            if (index != -1) {
                entryDirPath = entryFilePath.substring(0, index);
            } else {
                entryDirPath = "";
            }
            entryDir = new File(entryDirPath);
            //如果文件夹路径不存在，则创建文件夹  
            if (!entryDir.exists() || !entryDir.isDirectory()) {
                entryDir.mkdirs();
            }

            //创建解压文件  
            entryFile = new File(entryFilePath);

            if (entryFile.exists()) {
                //检测文件是否允许删除，如果不允许删除，将会抛出SecurityException  
                SecurityManager securityManager = new SecurityManager();
                securityManager.checkDelete(entryFilePath);
                //删除已存在的目标文件  
                entryFile.delete();
            }

            //写入文件  
            bos = new BufferedOutputStream(new FileOutputStream(entryFile));
            bis = new BufferedInputStream(zip.getInputStream(entry));
            while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
                bos.write(buffer, 0, count);
            }
            bos.flush();
            bos.close();
        }
        zip.close();
        return map;
    }


    public static File saveFile(String filePath, MultipartFile file) {
        // 判断文件是否为空
        if (!file.isEmpty()) {
            try {
                filePath = filePath + file.getOriginalFilename();
                File saveDir = new File(filePath);
                if (!saveDir.getParentFile().exists())
                    saveDir.getParentFile().mkdirs();
                // 转存文件
                file.transferTo(saveDir);
                return saveDir;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param filePath 指定的文件路径
     * @param isNew    true：新建、false：不新建
     * @return 存在返回TRUE，不存在返回FALSE
     */
    public static boolean isExist(String filePath, boolean isNew) {
        File file = new File(filePath);
        if (!file.exists() && isNew) {
            return file.mkdirs();    //新建文件路径
        }
        return false;
    }

    /**
     * 获取文件名，构建结构为 prefix + yyyyMMddHH24mmss + 10位随机数 + suffix + .type
     *
     * @param type   文件类型
     * @param prefix 前缀
     * @param suffix 后缀
     */
    public static String getFileName(String type, String prefix, String suffix) {
        String date = DateUtil.getCurrentTime("yyyyMMddHH24mmss");   //当前时间
        String random = RandomUtils.generateNumberString(10);   //10位随机数

        //返回文件名
        return prefix + date + random + suffix + "." + type;
    }

    /**
     * 获取文件名，文件名构成:当前时间 + 10位随机数 + .type
     *
     * @param type 文件类型
     */
    public static String getFileName(String type) {
        return getFileName(type, "", "");
    }

    /**
     * 获取文件名，文件构成：当前时间 + 10位随机数
     */
    public static String getFileName() {
        String date = DateUtil.getCurrentTime("yyyyMMddHH24mmss");   //当前时间
        String random = RandomUtils.generateNumberString(10);   //10位随机数

        //返回文件名
        return date + random;
    }

    public static boolean delFile(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = delFile(dir.getPath() + File.separator + aChildren);
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


}
