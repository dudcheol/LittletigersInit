package com.example.parkyoungcheol.littletigersinit.Navigation.SNS

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.parkyoungcheol.littletigersinit.Model.ContentDTO
import com.example.parkyoungcheol.littletigersinit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class AddPhotoActivity : AppCompatActivity() {

    val PICK_IMAGE_FROM_ALBUM = 0

    var photoUri: Uri? = null

    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        // Firebase storage
        storage = FirebaseStorage.getInstance()
        // Firebase Database
        firestore = FirebaseFirestore.getInstance()
        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        addphoto_image.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
            overridePendingTransition(R.anim.push_up_in, R.anim.non_anim)
        }

        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }

        toolbar_btn_back2.setOnClickListener { onBackPressed() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            //이미지 선택시
            if (resultCode == Activity.RESULT_OK) {
                //이미지뷰에 이미지 세팅
                println(data!!.data)
                photoUri = data!!.data
                addphoto_image.setImageURI(data!!.data)
            } else {
                finish()
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
        overridePendingTransition(R.anim.non_anim, R.anim.push_down_out)
    }

    fun contentUpload() {
        progress_bar.visibility = View.VISIBLE

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_.png"


        val storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener { taskSnapshot ->
            progress_bar.visibility = View.GONE

            Toast.makeText(this, getString(R.string.upload_success),
                    Toast.LENGTH_SHORT).show()

            // taskSnapshot을 통해 사진을 올린 url을 받아옴
            val uri = taskSnapshot.metadata!!.reference!!.downloadUrl

            // 사진이 잘 올라갔다는 콜백메시지를 받으면 양식들을 contentDTO에 넣음
            uri.addOnSuccessListener { Uri ->
                val contentDTO = ContentDTO()

                Log.v("uri_test", uri.toString())
                //이미지 주소
                contentDTO.imageUrl = uri.result.toString()
                Log.v("uri.result_test", contentDTO.imageUrl.toString())
                //유저의 UID
                contentDTO.uid = auth?.currentUser?.uid
                //게시물의 설명
                contentDTO.explain = addphoto_edit_explain.text.toString()
                //유저의 아이디
                contentDTO.userId = auth?.currentUser?.email
                //게시물 업로드 시간
                contentDTO.timestamp = System.currentTimeMillis()

                //게시물을 데이터를 생성 및 엑티비티 종료
                firestore?.collection("images")?.document()?.set(contentDTO)

                setResult(Activity.RESULT_OK)
                finish()
            }
        }
                ?.addOnFailureListener {
                    progress_bar.visibility = View.GONE

                    Toast.makeText(this, getString(R.string.upload_fail),
                            Toast.LENGTH_SHORT).show()
                }
    }


}
