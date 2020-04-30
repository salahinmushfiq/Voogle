package com.ngsoftworks.hotelchain.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileDownloader {
    private static final int MEGABYTE = 1024 * 1024;
    public Context cont;

    private boolean checkPermissions() {

        return false;
    }

    FileDownloader(Context context) {
        cont = context;
    }

    public String copyDirorfileFromAssetManager(String arg_assetDir) throws IOException {
        File sd_path = cont.getFilesDir();
        String dest_dir_path = sd_path + ""/* + addLeadingSlash(arg_destinationDir)*/;
        File dest_dir = new File(dest_dir_path);

        createDir(dest_dir);

        AssetManager asset_manager = cont.getApplicationContext().getAssets();
        String[] files = asset_manager.list(arg_assetDir);

        for (int i = 0; i < files.length; i++) {

            String abs_asset_file_path = addTrailingSlash(arg_assetDir) + files[i];
            String sub_files[] = asset_manager.list(abs_asset_file_path);

            if (sub_files.length == 0) {
                // It is a file
                //String dest_file_path = addTrailingSlash(dest_dir_path) + files[i];
                copyAssetFile(abs_asset_file_path, files[i] + "");
            } else {
                // It is a sub directory
                Log.d("fuck", "it was a fucking directory");
//                copyDirorfileFromAssetManager(abs_asset_file_path, addTrailingSlash(arg_destinationDir) + files[i]);
            }
        }

        return dest_dir_path;
    }


    public void copyAssetFile(String assetFilePath, String destinationFilePath) throws IOException {
        InputStream in = cont.getApplicationContext().getAssets().open(assetFilePath);
//        OutputStream out = new FileOutputStream(destinationFilePath);
        OutputStream out = cont.openFileOutput(destinationFilePath, Context.MODE_PRIVATE);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);
        in.close();
        out.close();
    }

    public String addTrailingSlash(String path) {
        if (path.charAt(path.length() - 1) != '/') {
            path += "/";
        }
        return path;
    }

    public String addLeadingSlash(String path) {
        if (path.charAt(0) != '/') {
            path = "/" + path;
        }
        return path;
    }

    public void createDir(File dir) throws IOException {
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new IOException("Can't create directory, a file is in the way");
            }
        } else {
            dir.mkdirs();
            if (!dir.isDirectory()) {
                throw new IOException("Unable to create directory");
            }
        }
    }

    public static void downloadFile(final String fileUrl, final String directory, final Context context, final int index, final int totalCount, final boolean debug) {


        Thread x = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(fileUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    if (debug)
                        Log.d("downloading url", fileUrl);
                    InputStream inputStream = urlConnection.getInputStream();
                    FileOutputStream fileOutputStream = context.openFileOutput(directory, Context.MODE_PRIVATE);
                    int totalSize = urlConnection.getContentLength();
                    Intent intent = new Intent();
                    Intent intentX = new Intent();
                    intent.setAction("MAXIMUS_PRINS");
                    intentX.setAction("MAXIMUS_MONS");
                    int total = 0;
                    int progress = 0;
                    byte[] buffer = new byte[MEGABYTE * 2];
                    int bufferLength = 0;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, bufferLength);
                        total += bufferLength;
                        progress = (int) (((double) total / (double) totalSize) * 100);
                        if (debug)
                            Log.d("calcProgg", "" + progress + " " + total);
                        intent.putExtra("prog", progress);
                        context.sendBroadcast(intent);
                    }
                    intentX.putExtra("honker", true);
                    intentX.putExtra("image", directory);
                    intentX.putExtra("count", index);
                    intentX.putExtra("totalCount", totalCount);
                    context.sendBroadcast(intentX);
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        x.setName("Thread " + index);
        x.start();

    }
}