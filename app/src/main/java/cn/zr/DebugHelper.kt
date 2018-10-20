package cn.zr

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class DebugHelper {
    companion object {
        private const val TAG = "DebugHelper"
        private const val DIRECTORY_PATH = "/data/local2/"

        fun init(){
            exec("cd data\nmkdir local2\nchmod 777 local2")
        }

        fun copyAssistFile(context: Context, fileName: String, filePath: String) {


            val inS = context.applicationContext.assets.open("$filePath$fileName")
            val out = FileOutputStream(File("$DIRECTORY_PATH$fileName"))

            val buffer = ByteArray(1024)
            var len = inS.read(buffer)

            while (len != -1) {
                out.write(buffer, 0, len);
                len = inS.read(buffer)//自增
            }
            out.flush()


            inS?.apply {
                close()
            }
            out?.apply {
                close()
            }

            exec("cd $DIRECTORY_PATH\nchmod -R 777 $fileName")
        }



        fun tempExec() {
            //exec("cd $DIRECTORY_PATH\n./test.so")
            exec("cd $DIRECTORY_PATH\n./zr.so com.eatbeancar.user ${DIRECTORY_PATH}share.so")
        }

        private fun exec(command:String ){
            ShellUtil.execCommand(command,true).also {

                StringBuilder().apply {

                    append(command+"\n")
                    it.responseMsg?.forEach {

                        append(it + "\n")

                    }

                    it.errorMsg?.forEach {
                        append(it + "\n")
                    }

                    append(it.result)
                    Log.d(TAG, toString())
                }

            }
        }
    }


}