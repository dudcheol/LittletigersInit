package com.example.parkyoungcheol.littletigersinit.Navigation.SNS

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.parkyoungcheol.littletigersinit.Model.AlarmDTO
import com.example.parkyoungcheol.littletigersinit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.android.synthetic.main.fragment_alarm.view.*
import kotlinx.android.synthetic.main.item_comment.view.*
import java.util.*

class AlarmFragment : Fragment() {

    var alarmSnapshot: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)
        view.alarmframgent_recyclerview.adapter = AlarmRecyclerViewAdapter()
        view.alarmframgent_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class AlarmRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val alarmDTOList = ArrayList<AlarmDTO>()

        init {

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            println(uid)
            FirebaseFirestore.getInstance()
                    .collection("alarms")
                    .whereEqualTo("destinationUid", uid)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        alarmDTOList.clear()
                        if (querySnapshot == null) return@addSnapshotListener
                        for (snapshot in querySnapshot?.documents!!) {
                            alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                        }
                        alarmDTOList.sortByDescending { it.timestamp }
                        notifyDataSetChanged()
                    }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)

            if (alarmDTOList != null) {
                showHide(sad)
                showHide(no_alarm)
            }

            return CustomViewHolder(view)
        }

        fun showHide(view: View) {
            view.visibility = if (view.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.GONE
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val profileImage = holder.itemView.commentviewitem_imageview_profile
            val commentTextView = holder.itemView.commentviewitem_textview_profile

            FirebaseFirestore.getInstance().collection("profileImages")
                    .document(alarmDTOList[position].uid!!).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val url = task.result?.get("image")
                            activity?.let {
                                Glide.with(it)
                                        .load(url)
                                        .apply(RequestOptions().circleCrop())
                                        .into(profileImage)
                            }
                        }
                    }

            when (alarmDTOList[position].kind) {
                0 -> {
                    val str_0 = alarmDTOList[position].userId + getString(R.string.alarm_favorite)
                    commentTextView.text = str_0
                }

                1 -> {
                    val str_1 = alarmDTOList[position].userId + getString(R.string.alarm_who) + alarmDTOList[position].message + getString(R.string.alarm_comment)
                    commentTextView.text = str_1
                }

                2 -> {
                    val str_2 = alarmDTOList[position].userId + getString(R.string.alarm_follow)
                    commentTextView.text = str_2
                }
            }
        }

        override fun getItemCount(): Int {

            return alarmDTOList.size
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }
}