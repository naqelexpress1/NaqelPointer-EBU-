package com.naqelexpress.naqelpointer

import android.app.Dialog
import android.app.PendingIntent.getActivity
import retrofit2.Call
import android.os.Bundle
import android.util.Log
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject
import android.widget.Toast
import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.naqelexpress.naqelpointer.Chat.ProgressDialog
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBar = ProgressBar(this)
        progressBar.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val linearLayout = findViewById<LinearLayout>(R.id.rootContainer)
        // Add ProgressBar to LinearLayout
        //linearLayout?.addView(progressBar)
        dialog = ProgressDialog.progressDialog(this)
        dialog.show()
        createNewUser(GlobalVar.GV().EmployID.toString())

        /* button_login.setOnClickListener {
             if (text_user_name.text.isNotEmpty()) {
                 createNewUser(text_user_name.text.toString())
             } else {
                 Toast.makeText(this@MainActivity, "Please enter a username", Toast.LENGTH_LONG).show()
             }
         }*/
    }

    private fun createNewUser(userName: String) {
        val jsonObject = JSONObject()
        jsonObject.put("username", userName)

        RetrofitClient().getClient().createUser(userName).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                Log.d("TAG", t.toString())
                dialog.dismiss()
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (response!!.code() == 200) {
                    startActivity(Intent(this@MainActivity, RoomsListActivity::class.java)
                            .putExtra("extra", userName))
                    finish()
                    dialog.dismiss()
                }
            }
        })
    }

}