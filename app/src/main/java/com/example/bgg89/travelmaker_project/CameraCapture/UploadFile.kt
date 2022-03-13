package com.example.bgg89.travelmaker_project.CameraCapture

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by bgg89 on 2018-12-09.
 */

class UploadFile(internal var context: Context // 생성자 호출 시
) : AsyncTask<String, String, String>() {
    internal var mProgressDialog: ProgressDialog? = null // 진행 상태 다이얼로그
    internal var fileName: String? = null // 파일 위치

    internal var conn: HttpURLConnection? = null
    internal var dos: DataOutputStream? = null

    internal var lineEnd = "\r\n"
    internal var twoHyphens = "--"
    internal var boundary = "*****"

    internal var bytesRead: Int = 0
    internal var bytesAvailable: Int = 0
    internal var bufferSize: Int = 0
    internal var buffer: ByteArray? = null
    internal var maxBufferSize = 1024
    internal var sourceFile: File? = null
    internal var serverResponseCode: Int = 0
    internal var TAG = "FileUpload"

    fun setPath(uploadFilePath: String) {
        this.fileName = uploadFilePath
        this.sourceFile = File(uploadFilePath)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mProgressDialog = ProgressDialog(context)
        mProgressDialog?.setTitle("Loading...")
        mProgressDialog?.setMessage("Image uploading...")
        mProgressDialog?.setCanceledOnTouchOutside(false)
        mProgressDialog?.isIndeterminate = false
        mProgressDialog?.show()
    }

    override fun doInBackground(vararg strings: String): String? {
        if (!sourceFile!!.isFile) {
            // 해당 위치의 파일이 있는지 검사
            Log.e(TAG, "sourceFile($fileName) is Not A File")
            return null
        } else {
            val success = "Success"
            Log.i(TAG, "sourceFile($fileName) is A File")
            try {
                val fileInputStream = FileInputStream(sourceFile)

                val url = URL(strings[0])
                Log.i("string[0]", strings[0])

                // Open a HTTP connection to the URL
                conn = url.openConnection() as HttpURLConnection
                conn!!.doInput = true
                conn!!.doOutput = true
                conn!!.useCaches = false
                conn!!.requestMethod = "POST"
                conn!!.setRequestProperty("Connection", "Keep-Alive")
                conn!!.setRequestProperty("ENCTYPE", "multipart/form-data")
                conn!!.setRequestProperty("Content-Type", "multipart/from-data;boundary=$boundary")
                conn!!.setRequestProperty("uploaded_file", fileName)
                Log.i(TAG, "fileName: $fileName")

                dos = DataOutputStream(conn!!.outputStream)

                dos!!.writeBytes(twoHyphens + boundary + lineEnd)
                dos!!.writeBytes("Content-Disposition: form-data; name=\"data1\"$lineEnd")
                dos!!.writeBytes(lineEnd)
                dos!!.writeBytes("newImage")
                dos!!.writeBytes(lineEnd)

                dos!!.writeBytes(twoHyphens + boundary + lineEnd)
                dos!!.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"$fileName\"$lineEnd")
                dos!!.writeBytes(lineEnd)

                bytesAvailable = fileInputStream.available()

                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                buffer = ByteArray(bufferSize)

                bytesRead = fileInputStream.read(buffer, 0, bufferSize)

                while (bytesRead > 0) {
                    dos!!.write(buffer, 0, bufferSize)
                    bytesAvailable = fileInputStream.available()
                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                }

                dos!!.writeBytes(lineEnd)
                dos!!.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

                serverResponseCode = conn!!.responseCode
                val serverResponseMessage = conn!!.responseMessage

                Log.i(TAG, "[UploadImageToServer] HTTP Response is : $serverResponseMessage: $serverResponseCode")

                if (serverResponseCode == 200) {

                }
                val reader = BufferedReader(InputStreamReader(conn!!.inputStream, "UTF-8"))
//                var reader: BufferedReader? = null
//                reader = BufferedReader(InputStreamReader(conn!!.inputStream, "UTF-8"))
//                var line: String? = null
//                while ({ line = reader.readLine(); line }() != null) {
//                    Log.i("Upload State", line)
//                }
                var line : String?
                do {
                    line = reader.readLine()
                    if (line == null)
                        break
                    Log.i("Upload State", line)
                } while (true)

                fileInputStream.close()
                dos!!.flush()
                dos!!.close()
            } catch (e: Exception) {
                Log.e("$TAG Error", e.toString())
            }

            return success
        }
    }
}
