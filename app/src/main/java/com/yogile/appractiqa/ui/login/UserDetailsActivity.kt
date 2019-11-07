package com.yogile.appractiqa.ui.login

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop.Options
import com.yogile.appractiqa.R
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_user_details.*
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class UserDetailsActivity : AppCompatActivity() {
    private lateinit var photoFile:File
    private var resultUri:Uri? = null
    private val p = "com.yogile.appractiqa"
    private val IMAGE_REQUEST = 2
    private val CAMERA_REQUEST = 1
    private val GALLERY_REQUEST = 0
    private val CAMERA_ACTION = "$p.intent.action.CAMERA"
    private val GALLERY_ACTION = "$p.intent.action.GALLERY"
    private val GALLERY_PACKEGE_NAME = "$p.Gallery"
    private val CAMERA_PACKEGE_NAME = "$p.Camera"
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private val mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
    private var fileUri: Uri? = null
    private var anim:MyAnimationDrawable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if(intent!=null){
            if(intent.action!=null){
                retrieveData()
                doAction()
            }
        }

        val options = Options()
        options.setCircleDimmedLayer(true);
        userLogo.setOnClickListener {
            pickImage()
        }

        back.setOnClickListener {
            onBackPressed()
        }
        container.doOnLayout {
            anim = MyAnimationDrawable(container.width)
            container.background = anim
            anim!!.start()
            anim!!.setFrame(intent.getIntExtra("angle",0))
        }
        cont.setOnClickListener { view ->
            cont.isEnabled = false
            anim?.setSpeed(30)
            if(password.text.toString().equals(confirm_password.text.toString()) && password.text.toString().length>6&&!age.text.toString().isEmpty()&&!first_name.text.toString().isEmpty()&&!last_name.text.toString().isEmpty()){
                mAuth?.createUserWithEmailAndPassword(intent.getStringExtra("email"),password.text.toString())
                    ?.addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            if (resultUri != null) {
                                val imgRef =
                                    mStorageRef.child("images/" + mAuth?.currentUser?.uid.toString() + ".jpg")
                                val uploadTask = resultUri?.let { imgRef.putFile(it) }
                                val urlTask = uploadTask?.continueWithTask { task ->
                                    if (!task.isSuccessful) {
                                        snackbarError(view, "Error while uploading your logo.\nTry again later")
                                    }
                                    imgRef.downloadUrl
                                }?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        addToFireStore(task,view)
                                    }
                                    else {
                                        snackbarError(view, "Error while uploading your logo.\nTry again later")
                                    }
                                }
                            }
                            else{
                                val task: Task<Uri>? = null
                                addToFireStore(task,view)

                            }
                        }
                        else{
                            val task: Task<Uri>? = null
                            addToFireStore(task,view)

                        }
                    }
            }
            else{
                if(age.text.toString().isEmpty()||first_name.text.toString().isEmpty()||last_name.text.toString().isEmpty()){
                    snackbarError(view, "Some fields are missing...")

                }
                else if(password.text.toString().length<7){
                    snackbarError(view, "Password need to be 7 or more letters")
                }
                else{
                    snackbarError(view, "Passwords don't match...")
                }
            }

        }

    }


    
    private fun snackbarError(view: View, errorString:String){
        Snackbar.make(view, errorString,Snackbar.LENGTH_LONG).setAction("Action",null).show()
        anim?.setSpeed(1)
        cont.isEnabled = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            resultUri = UCrop.getOutput(data!!)
            userLogo.setImageURI(resultUri)
        }
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            val uri = photoFile.absoluteFile.toUri()
            cropCircle(uri,uri)
        }
        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST){
            val uri = data?.data
            val photoFile = createImageFile()
            photoFile.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it)
            }
            cropCircle(uri,photoFile.absoluteFile.toUri())
            
        }
    }
    private fun doAction(){
        if(intent.action.equals(CAMERA_ACTION)) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Create the File where the photo should go
                photoFile = createImageFile()
                // Continue only if the File was successfully created
                photoFile.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.android.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                    }
                }
            }
        }
        else if(intent.action.equals(GALLERY_ACTION)){
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent,GALLERY_REQUEST)
        }
    }

    override fun onBackPressed() {
        val i = Intent(this,LoginActivity::class.java)
        startActivity(i)
    }
    private fun putData(i:Intent):Intent{
        i.putExtra("angle", anim?.getFrame())
        i.putExtra("first",first_name.text.toString())
        i.putExtra("last",last_name.text.toString())
        i.putExtra("age",age.text.toString())
        i.putExtra("pass",password.text.toString())
        i.putExtra("conf",confirm_password.text.toString())
        i.putExtra("email",intent.getStringExtra("email"))
        if(resultUri!=null){
            i.putExtra("image", resultUri)
        }
        return i
    }
    private fun retrieveData(){
        password.setText(intent.getStringExtra("pass"))
        confirm_password.setText(intent.getStringExtra("conf"))
        age.setText(intent.getStringExtra("age"))
        last_name.setText(intent.getStringExtra("last"))
        first_name.setText(intent.getStringExtra("first"))
        if(intent.extras?.get("image")!=null){
            userLogo.setImageURI(intent.extras?.get("image") as Uri?)
            resultUri = intent.extras?.get("image") as Uri?
        }
    }
    private fun cropCircle(srcUri:Uri?,dstUri:Uri){
        val options = UCrop.Options()
        options.setCircleDimmedLayer(true)
        options.setHideBottomControls(true)
        options.withAspectRatio(1f,1f)
        options.setCropGridColor(Color.parseColor("#00000000"))
        options.setCropFrameColor(Color.parseColor("#00000000"))
        if (srcUri != null) {
            UCrop.of(srcUri,dstUri).withOptions(options).start(this)
        }
    }
    lateinit var currentPhotoPath: String
    @Throws(IOException::class)
        private fun createImageFile(): File {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }
        }
    fun addToFireStore(task1:Task<Uri>?,view: View) {
        var downloadUri = "https://firebasestorage.googleapis.com/v0/b/appractiqa.appspot.com/o/images%2Fuser.png?alt=media&token=a4b47682-76d4-4d25-8725-ab96b1bc3a19"
        if (task1 != null) {
            downloadUri = task1.result.toString()
        }
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userD = HashMap<String, Any>()
        userD.put(
            "name",
            first_name.text.toString() + " " + last_name.text.toString()
        )
        userD.put("logo", downloadUri)
        userD.put("age", age.text.toString().toInt())
        db.collection("users")
            .document(mAuth?.currentUser?.uid.toString()).set(userD)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("withInfo")
                        .build()

                    mAuth?.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(
                                    Intent(
                                        this,
                                        CodeActivity::class.java
                                    )
                                )
                            }
                            else{
                                snackbarError(view, "Something went wrong.\nTry again later")
                            }
                        }

                } else {
                    snackbarError(view, "Error while creating your account.\nTry again later")
                }
            }
    }
    private fun pickImage(){
        val intentList = ArrayList<Intent>()
        //Camera
        var camIntent = Intent(CAMERA_ACTION)
        camIntent.component = ComponentName(this,CAMERA_PACKEGE_NAME)
        intentList.add(camIntent)
        //Gallery
        var galIntent = Intent(GALLERY_ACTION)
        galIntent.component = ComponentName(this, GALLERY_PACKEGE_NAME)
        intentList.add(galIntent)

        if(intentList.isEmpty())
            Toast.makeText(this, "No apps can perform this action", Toast.LENGTH_LONG).show();
        else {
            val chooserIntent = Intent.createChooser(intentList.removeAt(intentList.size-1), "Choose photo source");//this removes 'Always' & 'Only once' buttons
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(Array<Parcelable>(intentList.size) {
                intentList[it]
            }))
            camIntent = putData(camIntent)
            galIntent = putData(galIntent)


            startActivityForResult(chooserIntent, IMAGE_REQUEST)
        }
    }
}

