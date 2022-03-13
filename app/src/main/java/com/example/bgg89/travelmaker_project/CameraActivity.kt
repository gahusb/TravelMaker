package com.example.bgg89.travelmaker_project

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.Toast

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.support.v7.app.AppCompatActivity

/**
 * Created by bgg89 on 2018-12-09.
 */

class CameraActivity : AppCompatActivity() {

    private var mCurrentPhotoPath: String? = null // 현재 사용중인 사진의 경로를 담을 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 저장공간 permission 체크
        val permissionWriteStorage = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val permissionCamera = ContextCompat.checkSelfPermission(applicationContext, CAMERA)
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED || permissionCamera != PackageManager.PERMISSION_GRANTED) {
            if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@CameraActivity, arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA), REQUEST_EXTERNAL_STORAGE)
            }
        } else {
            try {
                captureImageAddToGallery()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // 저장공간 permission 체크
        val permissionWriteStorage = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val permissionCamera = ContextCompat.checkSelfPermission(applicationContext, CAMERA)
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED || permissionCamera != PackageManager.PERMISSION_GRANTED) {
            if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@CameraActivity, arrayOf(WRITE_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE)
            }
            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@CameraActivity, arrayOf(CAMERA), REQUEST_EXTERNAL_STORAGE)
            }

        } else {
            try {
                captureImageAddToGallery()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 권한 허용 성공시!
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    try {
                        captureImageAddToGallery()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } else {
                    // 권한 거부
                    Toast.makeText(applicationContext, "권한이 없어 카메라를 실행하지 못합니다.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    // 경로 저장
    @Throws(IOException::class)
    private fun catptureImageDirSetup(): File {
        // 파일 이름 설정
        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis())
        val imageFileName = "$fileName.jpg"

        val stroageDir = File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Dulle/" + imageFileName)

        mCurrentPhotoPath = stroageDir.absolutePath

        return stroageDir
    }

    // 캡쳐사진 저장 dispatchTakePictureIntent
    @Throws(IOException::class)
    private fun captureImageAddToGallery() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        if (cameraIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = catptureImageDirSetup()
            } catch (ex: IOException) {
                Toast.makeText(applicationContext, "사진찍기에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }

            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", catptureImageDirSetup())
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

//    override fun onBackPressed() {
//        val resultIntent = Intent(this@CameraActivity, MainActivity::class.java)
//        setResult(1, resultIntent)
//        super.onBackPressed()
//    }
    // ActivityResult = 가져온 사진 뿌리기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(applicationContext, "카메라가 취소되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val uri = Uri.parse(mCurrentPhotoPath)

                    if (uri != null) {

                        MediaScannerConnection.scanFile(applicationContext, arrayOf(uri.path), null) { path, uri ->
                            Log.i("ExternalStorage", "Scanned $path:")
                            Log.i("ExternalStorage", "-> uri=$uri")
                        }
                    }
                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

                    val f = File(mCurrentPhotoPath!!)
                    val contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", f)
                    mediaScanIntent.data = contentUri
                    // 동기화화
                    this.sendBroadcast(mediaScanIntent)
                    Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {

        private val REQUEST_EXTERNAL_STORAGE = 12
        private val REQUEST_CAMERA = 14

        private val REQUEST_IMAGE_CAPTURE = 2001
    }
}