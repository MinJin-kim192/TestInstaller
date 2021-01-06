package com.utinfra.minjin.testinstaller

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_INSTALL_PERMISSION = 10
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apkPath = filesDir.absolutePath + "/app.apk"
        if (!File(apkPath).exists()) {
            copyApkToAppFolder()
        }
        try {
            installApk()
            if (packageManager.canRequestPackageInstalls()) {
                installApk()
            } else {
                val intent = Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, REQUEST_INSTALL_PERMISSION)
            }
        } catch (e: Exception) {
            Log.d("로그","e $e")
        }

    }

    private fun installApk() {
        val apkPath = filesDir.absolutePath + "/app.apk"
        val apkUri =
                FileProvider.getUriForFile(
                        applicationContext,
                        BuildConfig.APPLICATION_ID + ".fileprovider", File(apkPath)
                )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        startActivity(intent)
    }


    private fun copyApkToAppFolder() {
        val inputStream = assets.open("app.apk")
        val outPath = filesDir.absolutePath + "/app.apk"
        val outputStream = FileOutputStream(outPath)
        while (true) {
            val data = inputStream.read()
            if (data == -1) {
                break
            }
            outputStream.write(data)
        }
        inputStream.close()
        outputStream.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INSTALL_PERMISSION) {
            if (packageManager.canRequestPackageInstalls()) {
                installApk()
            }
        }
    }


}