package com.yogile.appractiqa

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

class Image : AppCompatActivity() {
    private  val p = "com.yogile.appractiqa"
    private val CAMERA_REQUEST = 1
    private val GALLERY_REQUEST = 0
    private val CAMERA_ACTION = "$p.intent.action.CAMERA"
    private val GALLERY_ACTION = "$p.intent.action.GALLERY"
    private val GALLERY_PACKEGE_NAME = "$p.GALLERY"
    private val CAMERA_PACKEGE_NAME = "$p.CAMERA"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        if(intent != null){
            if(intent.action!=null){
                if(intent.action.equals(CAMERA_ACTION)){
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        takePictureIntent.resolveActivity(packageManager)?.also {
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                        }
                    }
                }
                else if(intent.action.equals(GALLERY_ACTION)){
                    setResult(GALLERY_REQUEST)
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode.equals(CAMERA_REQUEST) && resultCode.equals(Activity.RESULT_OK)){
            val imageUri = data?.extras?.get("data") as Uri
            val i = Intent()
            i.putExtra("uri",imageUri)
            setResult(CAMERA_REQUEST,i)
            finish()
        }
    }
}
