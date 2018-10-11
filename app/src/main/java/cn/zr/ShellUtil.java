package cn.zr;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * modified form Trinea
 *
 * @author trinea
 * @date 2014-12-10
 */
public class ShellUtil {

    private static final String TAG = "ShellUtil";

    /**
     * check whether has root permission
     */
    public static boolean hasRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true);
    }

    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
    }

    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     * {@link CommandResult#result} is -1, there maybe some excepiton.
     *
     * @param commands     command array
     * @param isRoot       whether need to run with root
     * @param needResponse whether need result msg
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean needResponse) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        ArrayList<String> successMsg = null;
        ArrayList<String> errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }

                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_LINE_END);

            os.writeBytes(COMMAND_EXIT);
            os.flush();

            /*os.writeBytes(COMMAND_LINE_END);
            os.flush();*/

            result = process.waitFor();
            if (needResponse) {

                String s;


                Log.d(TAG, "->1" + process.exitValue());

                if (process.exitValue() == 0) {
                    successMsg = new ArrayList<String>();
                    successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));


                    Log.d(TAG, "->2");

                    while ((s = successResult.readLine()) != null) {
                        Log.d(TAG, "successResult.readLine()-->" + s + "<");
                        successMsg.add(s);
                    }


                } else {
                    errorMsg = new ArrayList<String>();
                    errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    /*while ((s = errorResult.readLine()) != null) {
                        Log.d(TAG, "errorResult.readLine()-->" + s+"<");
                        errorMsg.add(s);
                    }*/

                    s = errorResult.readLine();
                    Log.d(TAG, "errorResult.readLine()-->" + s + "<");
                    errorMsg.add(s);

                }

                Log.d(TAG, "->3");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errorResult != null) {
                    errorResult.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }

        }
        return new CommandResult(result, successMsg, errorMsg);
    }

    public static class CommandResult {

        public int result;
        public ArrayList<String> responseMsg;
        public ArrayList<String> errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, ArrayList<String> responseMsg, ArrayList<String> errorMsg) {
            this.result = result;
            this.responseMsg = responseMsg;
            this.errorMsg = errorMsg;
        }
    }

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";


}
