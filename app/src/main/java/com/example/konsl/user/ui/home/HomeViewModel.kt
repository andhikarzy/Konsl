package com.example.konsl.user.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.konsl.model.Article
import com.example.konsl.model.Consultation
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel : ViewModel() {
    companion object{
        const val TYPE_EDUCATION = "edukasi"
        const val TYPE_TUTORIAL = "tutorial"
        const val STATUS_WAITING_FOR_CONFIRMATION = "menunggu konfirmasi"
        const val STATUS_CONFIRMED = "terkonfirmasi"
        const val STATUS_DONE = "selesai"
        const val STATUS_WAITING_FOR_CONTINUE_CONFIRMATION = "menunggu konfirmasi konsultasi lanjutan"
    }
    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val listArticles = MutableLiveData<ArrayList<Article>>()
    private val listTutorials = MutableLiveData<ArrayList<Article>>()
    private val nextConsultationTimeDiff = MutableLiveData<String>()
    private val nextConsultationDate = MutableLiveData<String>()
    private val nextConsultationTime = MutableLiveData<String>()

    fun loadArticles(){
        db.collection("articles")
            .whereEqualTo("tag", TYPE_EDUCATION)
            .limit(2)
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.w(this::class.java.simpleName, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val listItems = ArrayList<Article>()
                for (document in value) {
                    val article = Article(
                            id = document.id,
                            title = document.data["title"] as String,
                            thumbnailUrl = document.data["thumbnail_url"] as String,
                            content = document.data["content"] as String,
                    )
                    listItems.add(article)
                    Log.d(this::class.java.simpleName, "${document.id} => ${document.data}")
                }
                listArticles.postValue(listItems)
            }
    }

    fun loadTutorials(){
        db.collection("articles")
                .whereEqualTo("tag", TYPE_TUTORIAL)
                .addSnapshotListener { value, e ->
                    if (e != null || value == null) {
                        Log.w(this::class.java.simpleName, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    val listItems = ArrayList<Article>()
                    for (document in value) {
                        val article = Article(
                                id = document.id,
                                title = document.data["title"] as String,
                                thumbnailUrl = document.data["thumbnail_url"] as String,
                                content = document.data["content"] as String,
                        )
                        listItems.add(article)
                        Log.d(this::class.java.simpleName, "${document.id} => ${document.data}")
                    }
                    listTutorials.postValue(listItems)
                }
    }

    fun loadNextConsultationInfo(){
        db.collection("consultations")
                .whereEqualTo("user_id", mAuth.uid)
                .whereEqualTo("status", STATUS_CONFIRMED)
                .orderBy("time_accepted", Query.Direction.DESCENDING)
                .addSnapshotListener { value, e ->
                    if (e != null || value == null) {
                        Log.w(this::class.java.simpleName, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    val listItems = ArrayList<Consultation>()
                    for (doc in value) {
                        val consultation = Consultation(
                                id = doc.id,
                                userName = doc.data["user_name"] as String,
                                userId = doc.data["user_id"] as String,
                                problem = doc.data["problem"] as String,
                                effort = doc.data["effort"] as String,
                                obstacle = doc.data["obstacle"] as String,
                                status = doc.data["status"] as String,
                                timeRequest = doc.data["time_request"] as String,
                                genderRequest = doc.data["gender_request"] as String,
                                createdAt = doc.data["created_at"] as Timestamp,
                                timeAccepted = doc.data["time_accepted"] as Timestamp?,
                                counselorId = doc.data["counselor_id"] as String?,
                                counselorName = doc.data["counselor_name"] as String?,
                        )
                        listItems.add(consultation)
                    }
                    if(listItems.size > 0){
                        val nextConsultation = listItems[0]
                        val localeByLanguageTag = Locale.forLanguageTag("id")
                        val timeMessages = TimeAgoMessages.Builder().withLocale(localeByLanguageTag).build()
                        val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())
                        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        nextConsultationTimeDiff.postValue(TimeAgo.using(nextConsultation.timeAccepted!!.toDate().time, timeMessages).capitalize(Locale.getDefault()))
                        nextConsultationDate.postValue(dateFormat.format(nextConsultation.timeAccepted!!.toDate()))
                        nextConsultationTime.postValue(timeFormat.format(nextConsultation.timeAccepted!!.toDate()))
                    } else {
                        nextConsultationTimeDiff.postValue("-")
                        nextConsultationDate.postValue("-")
                        nextConsultationTime.postValue("-")
                    }
                }
    }

    fun getArticles() : LiveData<ArrayList<Article>> {
        return listArticles
    }

    fun getTutorials() : LiveData<ArrayList<Article>> {
        return listTutorials
    }

    fun getNextConsultationTimeDiff(): LiveData<String>{
        return nextConsultationTimeDiff
    }

    fun getNextConsultationDate(): LiveData<String>{
        return nextConsultationDate
    }

    fun getNextConsultationTime(): LiveData<String>{
        return nextConsultationTime
    }
}