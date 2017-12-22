package AmazonS3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.*;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description :
 * author : jiadu
 * date : 2017/7/4
 * Copyright (c) , IsoftStone All Right reserved.
 */
public class S3Test {

    private static final String accessKey = "AKIAPKAEHKORANLTKUVA";
    private static final String secrectKey = "0PHAsTNEF4kPXYC1cfmnLXpldSIfk9utf5/rmD7F";
    private volatile static AmazonS3 conn = null;

    public static AmazonS3 getInstance() {
        if (conn == null) {
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secrectKey);
//            ClientConfiguration clientConfig = new ClientConfiguration();
//            clientConfig.setProtocol(Protocol.HTTP);
            conn = new AmazonS3Client(credentials);
//            conn.setEndpoint("218.17.158.244:33404");
            com.amazonaws.regions.Region region = com.amazonaws.regions.Region.getRegion(Regions.CN_NORTH_1);
            conn.setRegion(region);
        }
        return conn;
    }

    //创建bucket,不支持大写的名称
    @Test
    public void testCreateBucket() {
        AmazonS3 s3 = S3Test.getInstance();
        String bucket_name = "material";
        Bucket b = null;
        if (s3.doesBucketExist(bucket_name)) {
            System.out.format("Bucket %s already exists.\n", bucket_name);
            b = getBucket(bucket_name);
        } else {
            try {
                b = s3.createBucket(bucket_name);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static Bucket getBucket(String bucket_name) {
        AmazonS3 s3 = S3Test.getInstance();
        Bucket named_bucket = null;
        List<Bucket> buckets = s3.listBuckets();

        for (Bucket b : buckets) {
            if (b.getName().equals(bucket_name)) {
                named_bucket = b;
            }
        }
        return named_bucket;
    }

    //查询bucket列表
    @Test
    public void testqueryListBucket() {
        List<Bucket> buckets = S3Test.getInstance().listBuckets();
//        String bucketLocation = S3Test.getInstance().getBucketLocation(new GetBucketLocationRequest("firstbucket"));
//        System.out.println("bucket location = " + bucketLocation);
        for (Bucket bucket : buckets) {
            System.out.println("createDate =" + bucket.getCreationDate() + ",name =" + bucket.getName() + ",owner =" + bucket.getOwner());
        }
    }


    //创建目录
    @Test
    public void createpath() {
        String bucketName = "firstbucket";
        String folderName = "logs";
        String parentName = "detail_1";
        if (bucketName == null || folderName == null) {
            return;
        }
        String key = parentName + folderName + "/";
        ByteArrayInputStream local = new ByteArrayInputStream("".getBytes());
        PutObjectResult result = S3Test.getInstance().putObject(bucketName, key, local, new ObjectMetadata());

    }

    //清空
    @Test
    public void clearBucket() {
        //firstbucket fileresource
        String bucket_name = "fileresource";
        deletObjectInBucket(bucket_name);
    }

    //删除bucket列表
    @Test
    public void deleteBucket() {
        String bucket_name = "firstbucket";
        deletObjectInBucket(bucket_name);
        AmazonS3 s3 = S3Test.getInstance();
        s3.deleteBucket(bucket_name);
    }

    private void deletObjectInBucket(String bucket_name) {
        try {
            AmazonS3 s3 = S3Test.getInstance();
            ObjectListing object_listing = s3.listObjects(bucket_name);
            while (true) {//删除有内容的桶
                for (Iterator<?> iterator =
                     object_listing.getObjectSummaries().iterator();
                     iterator.hasNext(); ) {
                    S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
                    s3.deleteObject(bucket_name, summary.getKey());
                }
                // more object_listing to retrieve?
                if (object_listing.isTruncated()) {
                    object_listing = s3.listNextBatchOfObjects(object_listing);
                } else {
                    break;
                }
            }
            ;
            System.out.println(" - removing versions from bucket");
            VersionListing version_listing = s3.listVersions(
                    new ListVersionsRequest().withBucketName(bucket_name));
            while (true) {
                for (Iterator<?> iterator =
                     version_listing.getVersionSummaries().iterator();
                     iterator.hasNext(); ) {
                    S3VersionSummary vs = (S3VersionSummary) iterator.next();
                    s3.deleteVersion(
                            bucket_name, vs.getKey(), vs.getVersionId());
                }
                if (version_listing.isTruncated()) {
                    version_listing = s3.listNextBatchOfVersions(
                            version_listing);
                } else {
                    break;
                }
            }
            System.out.println(" OK, bucket ready to clear!");
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    //上传file(一次传输完毕)
    @Test
    public void putObject() {
        AmazonS3 s3 = S3Test.getInstance();
        String bucket_name = "firstbucket";
        String file_path = "D:\\server.xml";
        String key_name = "detail/logs/server.xml";
        System.out.format("Uploading %s to S3 bucket %s...\n", file_path, bucket_name);
        try {
            File file = new File(file_path);
            try {
                FileInputStream fileInputStream = new FileInputStream(file_path);
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucket_name, key_name, fileInputStream, new ObjectMetadata());
//                s3.putObject(bucket_name, key_name, new File(file_path));
                s3.putObject(putObjectRequest);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Test
    public void getDirectList() {
        AmazonS3 s3 = S3Test.getInstance();
        String bucket_name = "taihe-copyright-log";
        String pathPrefix = "pushlogs/openapi-sdkpushlogs/";
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.DAY_OF_MONTH, -4);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//        String statisticsTime = simpleDateFormat.format(c.getTime());//20171225
//        pathPrefix = pathPrefix + statisticsTime + "/" + statisticsTime;
        ObjectListing ol = s3.listObjects(bucket_name, pathPrefix);
        String prefix = ol.getPrefix();
        int count = 0;
        do {
            List<S3ObjectSummary> list = ol.getObjectSummaries();
            for (S3ObjectSummary s3ObjectSummary : list) {
                System.out.println(s3ObjectSummary.getKey());
                count++;
            }
//            List<String> commomprefix = ol.getCommonPrefixes();
//            for (String comp : commomprefix) {
//                String dirName = comp.substring(prefix == null ? 0 : prefix.length(), comp.length() - 1);
//                System.out.printf(dirName);
//            }
            ol = s3.listNextBatchOfObjects(ol);
        } while (ol.isTruncated());
        System.out.println("count>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + count);
    }

    /**
     * 生成文件url
     */
    @Test
    public void getDownloadUrl() {
        AmazonS3 s3 = S3Test.getInstance();
        String bucketName = "firstbucket";
        String objectName = "axis2";
        if (bucketName.isEmpty() || objectName.isEmpty()) {
            return;
        }
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
        System.out.println(s3.generatePresignedUrl(request));
    }

    //列出所有对象
    @Test
    public void listObjects() {
        AmazonS3 s3 = S3Test.getInstance();
        //fileresource      firstbucket
        String bucket_name = "firstbucket";
        ObjectListing ol = s3.listObjects(bucket_name);
        List<S3ObjectSummary> objects = ol.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }

//        String prefix ="";
//        boolean isDelimiter = false;
//        ListObjectsRequest objectsRequest = new ListObjectsRequest().withBucketName(bucket_name);
//        if (prefix != null && !prefix.isEmpty()) {
//            objectsRequest.setPrefix(prefix);
//        }
//        if(isDelimiter){
//            objectsRequest.setDelimiter("/");
//        }
//        ObjectListing object = s3.listObjects(objectsRequest);
//
//        for (S3ObjectSummary os : object.getObjectSummaries()) {
//            System.out.println("* " + os.getKey());
//        }

        //获取流
//        GetObjectRequest rangeObjectRequest = new GetObjectRequest(
//                bucket_name, "档案管理_录入.html");
//        rangeObjectRequest.setRange(0, 10); // retrieve 1st 11 bytes.
//        S3Object objectPortion = s3.getObject(rangeObjectRequest);
//
//        InputStream objectData = objectPortion.getObjectContent();
//// Process the objectData stream.
//        try {
//            objectData.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void copyObjectSimple(String from_bucket, String from_key, String to_bucket, String to_key) {
        System.out.println("Copying s3 object: " + from_key);
        System.out.println("      from bucket: " + from_bucket);
        System.out.println("     to s3 object: " + to_bucket);
        System.out.println("        in bucket: " + to_key);
        TransferManager xfer_mgr = new TransferManager(S3Test.getInstance());
        try {
            Copy xfer = xfer_mgr.copy(from_bucket, from_key, to_bucket, to_key);
//                showTransferProgress(xfer);
            waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    @Test
    public void copy() {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    Copy <s3_src> <s3_dest>\n\n" +
                "Where:\n" +
                "    s3_src  - the source (bucket/key) of the object to copy.\n\n" +
                "    s3_dest - the destination of the object. A key name is optional.\n" +
                "              If a destination key name is not given, the object\n" +
                "              will be copied with the same name.\n\n" +
                "Examples:\n" +
                "    Copy my_photos/cat_happy.png public_photos/funny_cat.png\n" +
                "    Copy my_photos/cat_sad.png public_photos\n\n";
        String[] args = null;
        args = new String[]{"fileresource/one_userID/", "firstbucket"};
        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // only the first '/' character is of interest to get the bucket name.
        // Subsequent ones are part of the key name.
        String src[] = args[0].split("/", 2);
        String dst[] = args[1].split("/", 2);

        if (src.length < 2) {
            System.out.println("I need both a bucket and key name to copy!");
            System.out.println(USAGE);
            System.exit(1);
        }

        if (dst.length < 2) {
            copyObjectSimple(src[0], src[1], dst[0], src[1]);
        } else {
            copyObjectSimple(src[0], src[1], dst[0], dst[1]);
        }
    }

    //下载对象
    @Test
    public void downObject() {
        AmazonS3 s3 = S3Test.getInstance();
        String bucket_name = "fileresource";
        String key_name = "oneuserID/";
        System.out.format("Downloading %s from S3 bucket %s...\n", key_name, bucket_name);
        try {
            S3Object o = s3.getObject(bucket_name, key_name);
            InputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File("D:\\oneuserID"));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    //复制文件
    @Test
    public void copyByXfer() {
        String[] args = null;
        final String USAGE = "\n" +
                "Usage:\n" +
                "    Copy <s3_src> <s3_dest>\n\n" +
                "Where:\n" +
                "    s3_src  - the source (bucket/key) of the object to copy.\n\n" +
                "    s3_dest - the destination of the object. A key name is optional.\n" +
                "              If a destination key name is not given, the object\n" +
                "              will be copied with the same name.\n\n" +
                "Examples:\n" +
                "    Copy my_photos/cat_happy.png public_photos/funny_cat.png\n" +
                "    Copy my_photos/cat_sad.png public_photos\n\n";
        args = new String[]{"bk01/apache-ant-1.9.8-bin.zip", "bk01/apache-ant-1.9.8-bin.zip"};
        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // only the first '/' character is of interest to get the bucket name.
        // Subsequent ones are part of the key name.
        String src[] = args[0].split("/", 2);
        String dst[] = args[1].split("/", 2);

        if (src.length < 2) {
            System.out.println("I need both a bucket and key name to copy!");
            System.out.println(USAGE);
            System.exit(1);
        }

        if (dst.length < 2) {
            copyObjectSimple(src[0], src[1], dst[0], src[1]);
        } else {
            copyObjectSimple(src[0], src[1], dst[0], dst[1]);
        }
    }

    public static void downloadDir(String bucket_name, String key_prefix,
                                   String dir_path, boolean pause) {
        System.out.println("downloading to directory: " + dir_path +
                (pause ? " (pause)" : ""));

        TransferManager xfer_mgr = new TransferManager(S3Test.getInstance());
        try {
            MultipleFileDownload xfer = xfer_mgr.downloadDirectory(
                    bucket_name, key_prefix, new File(dir_path));
            // loop with Transfer.isDone()
            showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    public static void downloadFile(String bucket_name, String key_name,
                                    String file_path, boolean pause) {
        System.out.println("Downloading to file: " + file_path +
                (pause ? " (pause)" : ""));

        File f = new File(file_path);
        TransferManager xfer_mgr = new TransferManager(S3Test.getInstance());
        try {
            Download xfer = xfer_mgr.download(bucket_name, key_name, f);
            // loop with Transfer.isDone()
            showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    @Test
    public void downloadByXfer() {
        String[] args = null;
        final String USAGE = "\n" +
                "Usage:\n" +
                "    XferMgrDownload [--recursive] [--pause] <s3_path> <local_paths>\n\n" +
                "Where:\n" +
                "    --recursive - Only applied if local_path is a directory.\n" +
                "                  Copies the contents of the directory recursively.\n\n" +
                "    --pause     - Attempt to pause+resume the download. This may not work for\n" +
                "                  small files.\n\n" +
                "    s3_path     - The S3 (bucket/path) to download the file(s) from. This can be\n" +
                "                  a single object or a set of files that share a common prefix.\n\n" +
                "                  * If the path ends with a '/', it is assumed to be a *path prefix*,\n" +
                "                    and all objects that share the same prefix will be downloaded to\n" +
                "                    the directory given in local_path.\n" +
                "                  * Otherwise, the S3 path is assumed to refer to an object, which\n" +
                "                    will be downloaded to the file name given in local_path.\n\n" +
                "    local_path  - The local path to use to download the object(s) specified in\n" +
                "                  s3_path.\n" +
                "                  * If s3_path ends with a '/', then local_path *must* refer to a\n" +
                "                    local directory. It will be created if it doesn't already\n" +
                "                    exist.\n" +
                "                  * Otherwise, local_path is scanned to see if it's a directory or\n" +
                "                    file. If it's a file, the specified file name will be used for\n" +
                "                    the object in s3_path. If it's a directory, the file in s3_path\n" +
                "                    will be downloaded into that directory. If the path doesn't exist\n" +
                "                    or is empty, then a file will be created with the object key name\n" +
                "                    in s3_path.\n\n" +
                "Examples:\n" +
                "    XferMgrDownload public_photos/cat_happy.png\n" +
                "    XferMgrDownload public_photos/ my_photos\n\n";
        args = new String[]{"--recursive", "fileresource/oneuserID/two/three/", "E:\\data"};
        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        int cur_arg = 0;
        boolean recursive = false;
        boolean pause = false;

        // first, parse any switches
        while (args[cur_arg].startsWith("--")) {
            if (args[cur_arg].equals("--recursive")) {
                recursive = true;
            } else if (args[cur_arg].equals("--pause")) {
                pause = true;
            } else {
                System.out.println("Unknown argument: " + args[cur_arg]);
                System.out.println(USAGE);
                System.exit(1);
            }
            cur_arg += 1;
        }

        // only the first '/' character is of interest to get the bucket name.
        // Subsequent ones are part of the key name.
        String s3_path[] = args[cur_arg].split("/", 2);
        String bucket_name = s3_path[0];
        String key_name = s3_path[1];
        boolean s3_path_is_prefix = (key_name.lastIndexOf('/') == key_name.length() - 1);
        cur_arg += 1;


        // The final argument is either a local directory or file to copy to.
        // If there is no final arg, use the key (object) name as the local file
        // name.
        String local_path = ((cur_arg < args.length) ? args[cur_arg] : key_name);
        File f = new File(local_path);
        if (f.isFile() && s3_path_is_prefix) {
            System.out.format(
                    "You can't copy an S3 prefix (%) into a single file!\n",
                    key_name);
            System.exit(1);
        }

        // If the path already exists, print a warning.
        if (f.exists()) {
            System.out.println("The local path already exists: " + local_path);
            String a = System.console().readLine("Do you want to overwrite it anyway? (yes/no): ");
            if (!a.toLowerCase().equals("yes")) {
                System.out.println("Aborting download!");
                System.exit(0);
            }
        } else if (s3_path_is_prefix) {
            try {
                f.mkdir();
            } catch (Exception e) {
                System.out.println("Couldn't create destination directory!");
                System.exit(1);
            }
        }

        // Assume that the path exists, do the download.
        if (s3_path_is_prefix) {
            downloadDir(bucket_name, key_name, local_path, false);
        } else {
            downloadFile(bucket_name, key_name, local_path, false);
        }
    }

    //使用TransferManager上传
    @Test
    public void uploadByXfer() {
        String[] args = null;
        final String USAGE = "\n" +
                "Usage:\n" +
                "    XferMgrUpload [--recursive] [--pause] <s3_path> <local_paths>\n\n" +
                "Where:\n" +
                "    --recursive - Only applied if local_path is a directory.\n" +
                "                  Copies the contents of the directory recursively.\n\n" +
                "    --pause     - Attempt to pause+resume the upload. This may not work for\n" +
                "                  small files.\n\n" +
                "    s3_path     - The S3 destination (bucket/path) to upload the file(s) to.\n\n" +
                "    local_paths - One or more local paths to upload to S3. These can be files\n" +
                "                  or directories. Globs are permitted (*.xml, etc.)\n\n" +
                "Examples:\n" +
                "    XferMgrUpload public_photos/cat_happy.png my_photos/funny_cat.png\n" +
                "    XferMgrUpload public_photos my_photos/cat_sad.png\n" +
                "    XferMgrUpload public_photos my_photos/cat*.png\n" +
                "    XferMgrUpload public_photos my_photos\n\n";
        args = new String[]{"--recursive", "--pause", "firstbucket", "E:\\tool"};
        long start = System.currentTimeMillis();
        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        int cur_arg = 0;
        boolean recursive = false;
        boolean pause = false;

        // first, parse any switches
        while (args[cur_arg].startsWith("--")) {
            if (args[cur_arg].equals("--recursive")) {
                recursive = true;
            } else if (args[cur_arg].equals("--pause")) {
                pause = true;
            } else {
                System.out.println("Unknown argument: " + args[cur_arg]);
                System.out.println(USAGE);
                System.exit(1);
            }
            cur_arg += 1;
        }

        // only the first '/' character is of interest to get the bucket name.
        // Subsequent ones are part of the key name.
        String s3_path[] = args[cur_arg].split("/", 2);
        cur_arg += 1;

        // Any remaining args are assumed to be local paths to copy.
        // They may be directories, arrays, or a mix of both.
        ArrayList<String> dirs_to_copy = new ArrayList<String>();
        ArrayList<String> files_to_copy = new ArrayList<String>();

        while (cur_arg < args.length) {
            // check to see if local path is a directory or file...
            File f = new File(args[cur_arg]);
            if (f.exists() == false) {
                System.out.println("Input path doesn't exist: " + args[cur_arg]);
                System.exit(1);
            } else if (f.isDirectory()) {
                dirs_to_copy.add(args[cur_arg]);
            } else {
                files_to_copy.add(args[cur_arg]);
            }
            cur_arg += 1;
        }

        String bucket_name = s3_path[0];
        String key_prefix = null;
        if (s3_path.length > 1) {
            key_prefix = s3_path[1];
        }

        // Upload any directories in the list.
        for (String dir_path : dirs_to_copy) {
            uploadDir(dir_path, bucket_name, key_prefix, recursive, pause);
        }

        // If there's more than one file in the list, upload it as a file list.
        // Otherwise, upload it as a single file.
        if (files_to_copy.size() > 1) {
            uploadFileList(files_to_copy.toArray(new String[0]), bucket_name,
                    key_prefix, pause);
        } else if (files_to_copy.size() == 1) {
            uploadFile(files_to_copy.get(0), bucket_name, key_prefix, pause);
        } // else: nothing to do.
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
    }

    public static void uploadDir(String dir_path, String bucket_name,
                                 String key_prefix, boolean recursive, boolean pause) {
        System.out.println("directory: " + dir_path + (recursive ?
                " (recursive)" : "") + (pause ? " (pause)" : ""));

        TransferManager xfer_mgr = new TransferManager(S3Test.getInstance());
        try {
            MultipleFileUpload xfer = xfer_mgr.uploadDirectory(bucket_name,
                    key_prefix, new File(dir_path), recursive);
            // loop with Transfer.isDone()

            showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
//            waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    private static <T extends Transfer> void waitForCompletion(T xfer) {
        try {
            xfer.waitForCompletion();
        } catch (AmazonServiceException e) {
            System.err.println("Amazon service error: " + e.getMessage());
            System.exit(1);
        } catch (AmazonClientException e) {
            System.err.println("Amazon client error: " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println("Transfer interrupted: " + e.getMessage());
            System.exit(1);
        }
    }

    private static <T extends Transfer> void showTransferProgress(T xfer) {
        // print the transfer's human-readable description
        System.out.println(xfer.getDescription());
        // print an empty progress bar...
        printProgressBar(0.0);
        // update the progress bar while the xfer is ongoing.
        do {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }
            // Note: so_far and total aren't used, they're just for
            // documentation purposes.
            TransferProgress progress = xfer.getProgress();
            long so_far = progress.getBytesTransferred();
            long total = progress.getTotalBytesToTransfer();
            double pct = progress.getPercentTransferred();
//            eraseProgressBar();
            printProgressBar(pct);
        } while (xfer.isDone() == false);
        // print the final state of the transfer.
        Transfer.TransferState xfer_state = xfer.getState();
        System.out.println(": " + xfer_state);

    }

    // erases the progress bar.
    public static void eraseProgressBar() {
        // erase_bar is bar_size (from printProgressBar) + 4 chars.
        final String erase_bar = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
        System.out.format(erase_bar);
    }

    public static void printProgressBar(double pct) {
        System.out.println();
        BigDecimal b = new BigDecimal(pct);
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.format("  %s", f1);
    }


    public static void uploadFileList(String[] file_paths, String bucket_name,
                                      String key_prefix, boolean pause) {
        System.out.println("file list: " + Arrays.toString(file_paths) +
                (pause ? " (pause)" : ""));
        // convert the file paths to a list of File objects (required by the
        // uploadFileList method)
        ArrayList<File> files = new ArrayList<File>();
        for (String path : file_paths) {
            files.add(new File(path));
        }

        TransferManager xfer_mgr = new TransferManager(S3Test.getInstance());
        try {
            MultipleFileUpload xfer = xfer_mgr.uploadFileList(bucket_name,
                    key_prefix, new File("."), files);
            // loop with Transfer.isDone()
            showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    public static void uploadFile(String file_path, String bucket_name,
                                  String key_prefix, boolean pause) {
        System.out.println("file: " + file_path +
                (pause ? " (pause)" : ""));

        String key_name = null;
        if (key_prefix != null) {
            key_name = key_prefix + '/' + file_path;
        } else {
            key_name = file_path;
        }

        File f = new File(file_path);
        TransferManager xfer_mgr = new TransferManager(S3Test.getInstance());
        try {
            Upload xfer = xfer_mgr.upload(bucket_name, key_name, f);
            // loop with Transfer.isDone()
            showTransferProgress(xfer);
            //  or block with Transfer.waitForCompletion()
            waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    //分段上传（分段最少为5M，最后一个分段不算）
    @Test
    public void uploadByParts() {
        AmazonS3 s3Client = S3Test.getInstance();
//        s3Client.setRegion(Region.US_West.toAWSRegion());
        String existingBucketName = "firstbucket";
        String keyName = "firstBigData";
        String filePath = "E:\\diligrp-dataplatform-web.jar";//
        // Create a list of UploadPartResponse objects. You get one of these for
        // each part upload.
        List<PartETag> partETags = new ArrayList<PartETag>();
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
                existingBucketName, keyName);
        InitiateMultipartUploadResult initResponse =
                s3Client.initiateMultipartUpload(initRequest);

        File file = new File(filePath);
        long contentLength = file.length();
        long partSize = 1024 * 1024 * 5; // Set part size to 5 MB.
        try {
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Last part can be less than 5 MB. Adjust part size.
                partSize = Math.min(partSize, (contentLength - filePosition));

                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(existingBucketName).withKey(keyName)
                        .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize).withGeneralProgressListener(new com.amazonaws.event.ProgressListener() {

                            @Override
                            public void progressChanged(ProgressEvent progressEvent) {
                                System.out.println(progressEvent.getBytesTransferred());
                            }
                        });

                // Upload part and add response to our list.
                partETags.add(s3Client.uploadPart(uploadRequest).getPartETag());

                filePosition += partSize;
            }
            // Step 3: Complete.
            CompleteMultipartUploadRequest compRequest = new
                    CompleteMultipartUploadRequest(existingBucketName,
                    keyName,
                    initResponse.getUploadId(),
                    partETags);

            s3Client.completeMultipartUpload(compRequest);
        } catch (Exception e) {
            s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    existingBucketName, keyName, initResponse.getUploadId()));
        }
    }

    //查询分段信息
    @Test
    public void queryParts() {
        String existingBucketName = "firstbucket";
        AmazonS3 s3Client = S3Test.getInstance();
        ListMultipartUploadsRequest allMultpartUploadsRequest =
                new ListMultipartUploadsRequest(existingBucketName);
        MultipartUploadListing multipartUploadListing =
                s3Client.listMultipartUploads(allMultpartUploadsRequest);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (MultipartUpload m : multipartUploadListing.getMultipartUploads()) {
//            s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
//                    existingBucketName, "firstBigData", m.getUploadId()));
            System.out.println(m.getKey() + "," + m.getUploadId() + "," + simpleDateFormat.format(m.getInitiated()));
        }
    }

    @Test
    public void testByte() {
        try {
            InputStream inStream = new FileInputStream(new File("D:\\azkaban-2.5.0.jar"));
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();
            System.out.println(in2b.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void execute() {
//        //生成的ZIP文件名为Demo.zip
//        String tmpFileName = "Demo.zip";
//        byte[] buffer = new byte[1024];
//        String filePath = "D:\\";
//        String strZipPath = "D:\\" + tmpFileName;
//        try {
//            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
//                    strZipPath));
//            // 需要同时下载的两个文件result.txt ，source.txt
//            File[] file1 = { new File("D:\\temp"),
//                    new File("E:\\tool\\2.crt") };
//            for (int i = 0; i < file1.length; i++) {
//                FileInputStream fis = new FileInputStream(file1[i]);
//                out.putNextEntry(new ZipEntry(file1[i].getName()));
//                //设置压缩文件内的字符编码，不然会变成乱码
//                out.setEncoding("UTF-8");
//                int len;
//                // 读入需要下载的文件的内容，打包到zip文件
//                while ((len = fis.read(buffer)) > 0) {
//                    out.write(buffer, 0, len);
//                }
//                out.closeEntry();
//                fis.close();
//            }
//            out.close();
////            this.downFile(getResponse(), tmpFileName);
//        } catch (Exception e) {
////            Log.error("文件下载出错", e);
//        }
//    }
//
//
//    /**
//     * 创建ZIP文件
//     */
//    @Test
//    public void createZip() {
//        String sourcePath = "D:\\temp";
//        String zipPath = "D:\\Demo.zip";
//        FileOutputStream fos = null;
//        ZipOutputStream zos = null;
//        try {
//            fos = new FileOutputStream(zipPath);
//            zos = new ZipOutputStream(fos);
//            zos.setEncoding("gbk");//此处修改字节码方式。
//            //createXmlFile(sourcePath,"293.xml");
//            writeZip(new File(sourcePath), "", zos);
//        } catch (FileNotFoundException e) {
////            log.error("创建ZIP文件失败",e);
//        } finally {
//            try {
//                if (zos != null) {
//                    zos.close();
//                }
//            } catch (IOException e) {
////                log.error("创建ZIP文件失败",e);
//            }
//
//        }
//    }
//
//    private static void writeZip(File file, String parentPath, ZipOutputStream zos) {
//        if(file.exists()){
//            if(file.isDirectory()){//处理文件夹
//                parentPath+=file.getName()+File.separator;
//                File [] files=file.listFiles();
//                if(files.length != 0)
//                {
//                    for(File f:files){
//                        writeZip(f, parentPath, zos);
//                    }
//                }
//                else
//                {       //空目录则创建当前目录
//                    try {
//                        zos.putNextEntry(new ZipEntry(parentPath));
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }else{
//                FileInputStream fis=null;
//                try {
//                    fis=new FileInputStream(file);
//                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
//                    zos.putNextEntry(ze);
//                    byte [] content=new byte[1024];
//                    int len;
//                    while((len=fis.read(content))!=-1){
//                        zos.write(content,0,len);
//                        zos.flush();
//                    }
//
//                } catch (FileNotFoundException e) {
////                    log.error("创建ZIP文件失败",e);
//                } catch (IOException e) {
////                    log.error("创建ZIP文件失败",e);
//                }finally{
//                    try {
//                        if(fis!=null){
//                            fis.close();
//                        }
//                    }catch(IOException e){
////                        log.error("创建ZIP文件失败",e);
//                    }
//                }
//            }
//        }
//    }
}
