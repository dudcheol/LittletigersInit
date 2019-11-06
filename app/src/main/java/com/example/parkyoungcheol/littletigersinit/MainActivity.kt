package com.example.parkyoungcheol.littletigersinit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.parkyoungcheol.littletigersinit.Chat.ChatMainActivity
import com.example.parkyoungcheol.littletigersinit.Navigation.AR.ARmessageActivity
import com.example.parkyoungcheol.littletigersinit.Navigation.SNS.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    val PICK_PROFILE_FROM_ALBUM = 10
    var backKeyPressedTime = 0L
    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress_bar.visibility = View.VISIBLE

        // Bottom Navigation View
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.selectedItemId = R.id.action_home


        // Bottom navi 사용자 프로필 이미지 가져옴
        user = FirebaseAuth.getInstance().currentUser
        FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(user!!.uid)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot?.data == null) {
                        profileImg?.visibility = View.GONE
                        bottom_navigation.menu.getItem(4).setIcon(R.drawable.ic_account)
                    } else {
                        profileImg?.visibility = View.VISIBLE
                        bottom_navigation.menu.getItem(4).setIcon(null)
                        val url = documentSnapshot?.data!!["image"]

                        Glide.with(this)
                                .load(url)
                                .apply(RequestOptions().circleCrop())
                                .into(profileImg)
                    }
                }

        // 앨범 접근 권한 요청
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        //푸시토큰 서버 등록
        registerPushToken()

        ARbtn.setOnClickListener {
            val intent_AR = Intent(this, ar_mainActivity::class.java)
            startActivity(intent_AR)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        ChatBtn.setOnClickListener {
            val intent_CHAT = Intent(this, ChatMainActivity::class.java)
            startActivity(intent_CHAT)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        ARmessageBtn.setOnClickListener {
            val intent_ARmsg = Intent(this, ARmessageActivity::class.java)
            startActivity(intent_ARmsg)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    fun registerPushToken() {
        var pushToken = FirebaseInstanceId.getInstance().token
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var map = mutableMapOf<String, Any>()
        map["pushtoken"] = pushToken!!
        FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
    }

    fun setToolbarDefault() {
        toolbar_title_image.visibility = View.VISIBLE
        ARmessageBtn.visibility = View.VISIBLE
        ARbtn.visibility = View.VISIBLE
        ChatBtn.visibility = View.VISIBLE
        toolbar_btn_back.visibility = View.GONE
        toolbar_username.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - backKeyPressedTime < 1500) {
            // 뒤로가기 버튼을 누른지 1.5초 이상 지난 경우
            finish()
        }
        val mySnackbar: Snackbar = Snackbar.make(findViewById(R.id.nav_coord_layout_in_main),
                "'뒤로가기'버튼을 한번 더 누르면 종료됩니다.", Snackbar.LENGTH_SHORT)
        mySnackbar.show()

        //Toast.makeText(this, "'뒤로가기' 버튼을 한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show()
        backKeyPressedTime = System.currentTimeMillis()
    }

    override fun onDestroy() {
        super.onDestroy()

        val prefs: SharedPreferences = getSharedPreferences("sFile", 0)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.commit()

        Log.i("destroy", "SharedPreferences 데이터 삭제")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setToolbarDefault()
        when (item.itemId) {
            R.id.action_home -> {

                val detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, detailViewFragment)
                        .commit()

                val colorMatrix = ColorMatrix()
                colorMatrix.setSaturation(0.0f)
                val filter = ColorMatrixColorFilter(colorMatrix)
                profileImg.colorFilter = filter
                return true
            }
            R.id.action_search -> {
                val gridFragment = GridFragment()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_content, gridFragment)
                        .commit()

                val colorMatrix = ColorMatrix()
                colorMatrix.setSaturation(0.0f)
                val filter = ColorMatrixColorFilter(colorMatrix)
                profileImg.colorFilter = filter
                return true
            }
            R.id.action_add_photo -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    bottom_navigation.menu.findItem(0)?.isChecked = true
                    startActivity(Intent(this, AddPhotoActivity::class.java))
                    overridePendingTransition(R.anim.push_up_in, R.anim.non_anim)
                } else {
                    Toast.makeText(this, "스토리지 읽기 권한이 없습니다.", Toast.LENGTH_LONG).show()
                }
                val colorMatrix = ColorMatrix()
                colorMatrix.setSaturation(0.0f)
                val filter = ColorMatrixColorFilter(colorMatrix)
                profileImg.colorFilter = filter
                return true
            }
            R.id.action_favorite_alarm -> {
                val alarmFragment = AlarmFragment()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_content, alarmFragment)
                        .commit()

                val colorMatrix = ColorMatrix()
                colorMatrix.setSaturation(0.0f)
                val filter = ColorMatrixColorFilter(colorMatrix)
                profileImg.colorFilter = filter
                return true
            }
            R.id.action_account -> {
                val userFragment = UserFragment()
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val bundle = Bundle()
                bundle.putString("destinationUid", uid)
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, userFragment)
                        .commit()

                profileImg.clearColorFilter()

                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // 앨범에서 Profile Image 사진 선택시 호출 되는 부분분
        if (requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {

            var imageUri = data?.data


            val uid = FirebaseAuth.getInstance().currentUser!!.uid //파일 업로드
            //사진을 업로드 하는 부분  userProfileImages 폴더에 uid에 파일을 업로드함
            FirebaseStorage
                    .getInstance()
                    .reference
                    .child("userProfileImages")
                    .child(uid)
                    .putFile(imageUri!!)
                    .addOnCompleteListener { task ->
                        //val url = task.result!!.downloadUrl.toString()
                        val uri = task.result!!.metadata!!.reference!!.downloadUrl.addOnSuccessListener { Uri ->
                            val url = Uri.toString()
                            val map = HashMap<String, Any>()
                            map["image"] = url
                            FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)
                        }
                    }
        }

    }
}
