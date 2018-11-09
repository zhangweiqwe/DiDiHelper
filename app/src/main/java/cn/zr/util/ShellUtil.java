package cn.zr.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * modified form Trinea
 *
 * @author trinea
 * @date 2014-12-10
 */
public class ShellUtil {

    private static final String TAG = "ShellUtil";
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * check whether has root permission
     */

    public static void exec(String command) {
        exec(command, true, null);
    }

    public static void exec(String command, OnResultListener onResultListener) {
        exec(command, true, onResultListener);
    }

    public static void exec(String command, boolean isRoot) {
        exec(command, isRoot, null);
    }

    public static void exec(String command, boolean isRoot, final OnResultListener onResultListener) {
        if (command == null || command.length() == 0) {
            new Throwable("nonsupport");
        }

        Log.d(TAG, command);
        Process process = null;
        DataOutputStream dataOutputStream = null;
        try {

            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.write(command.getBytes());
            dataOutputStream.writeBytes(COMMAND_LINE_END);
            dataOutputStream.writeBytes(COMMAND_EXIT);
            dataOutputStream.flush();
            dataOutputStream.close();
            executorService.submit(new ResultReader(onResultListener, process, false));
            executorService.submit(new ResultReader(onResultListener, process, true));
            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            process.destroy();
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "-->exec end");
    }

    public static class Result {

        public int result;
        public List<String> data;


        public Result(int result, List<String> data) {
            this.result = result;
            this.data = data;
        }
    }

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    public interface OnResultListener {
        void onResult(Result result);
    }

    static class ResultReader implements Runnable {
        private OnResultListener onResultListener;
        private Process process;
        private boolean isError;

        public ResultReader(OnResultListener onResultListener, Process process, boolean isError) {
            this.onResultListener = onResultListener;
            this.process = process;
            this.isError = isError;

        }

        @Override
        public void run() {
            InputStream in;
            if (isError) {
                in = process.getErrorStream();
            } else {
                in = process.getInputStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            List<String> data = new ArrayList<>();

            try {
                String line = null;
                while ((line = br.readLine()) != null) {
                    data.add(line);
                    Log.d(TAG, "-->" + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                process.destroy();
                int exitValue = process.exitValue();

                if (onResultListener != null) {
                    if(isError){
                        if(exitValue!=0){
                            onResultListener.onResult(new Result(exitValue, data));
                        }
                    }else {
                        onResultListener.onResult(new Result(exitValue, data));
                    }
                }
                Log.d(TAG, "--> exitValue" + exitValue);

                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }





}
