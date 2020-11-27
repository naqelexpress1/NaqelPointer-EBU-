package com.naqelexpress.naqelpointer.Activity.Login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
//import android.support.v7.app.AppCompatActivity
import android.telecom.TelecomManager
import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
import com.naqelexpress.naqelpointer.R

/**
 * Created by Hasna on 3/7/19.
 */
class DefaultDialer : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SET_DEFAULT_DIALER = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)
        checkDefaultDialer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SET_DEFAULT_DIALER -> checkSetDefaultDialerResult(resultCode)
        }
    }

    private fun checkDefaultDialer() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        val isAlreadyDefaultDialer = packageName == telecomManager.defaultDialerPackage
        if (isAlreadyDefaultDialer) {
            val intent = Intent(this, SplashScreenActivity::class.java)

            startActivity(intent)
            return
        }

        val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
        startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
    }

    private fun checkSetDefaultDialerResult(resultCode: Int) {
        val message = when (resultCode) {
            RESULT_OK -> Intent(this, SplashScreenActivity::class.java);


            RESULT_CANCELED -> "User declined request to become default dialer"
            else -> "Unexpected result code $resultCode"
        }

        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }
}