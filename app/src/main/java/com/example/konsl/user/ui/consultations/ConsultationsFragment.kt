package com.example.konsl.user.ui.consultations

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.konsl.R
import com.example.konsl.adapter.ConsultationAdapter
import com.example.konsl.user.ui.consultations.request.RequestConsultationActivity
import kotlinx.android.synthetic.main.fragment_consultations.*

class ConsultationsFragment : Fragment(), View.OnClickListener {

    private lateinit var consultationsViewModel: ConsultationsViewModel
    private lateinit var consultationAdapter: ConsultationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consultations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        consultationAdapter = ConsultationAdapter()
        consultationAdapter.notifyDataSetChanged()

        rvConsultations.layoutManager = LinearLayoutManager(context)
        rvConsultations.adapter = consultationAdapter

        consultationsViewModel = ViewModelProvider(this).get(ConsultationsViewModel::class.java)
        consultationsViewModel.loadConsultations()

        consultationsViewModel.getConsultations().observe(viewLifecycleOwner, Observer { consultations ->
            consultations?.let {
                progressBarConsultations.visibility = View.INVISIBLE
                if(it.isNotEmpty()){
                    rvConsultations.visibility = View.VISIBLE
                    consultationAdapter.setData(it)
                } else {
                    layoutNoConsultation.visibility = View.VISIBLE
                }
            }
        })

        fabRequestConsultation.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fabRequestConsultation -> {
                val intent = Intent(context, RequestConsultationActivity::class.java)
                startActivity(intent)
            }
        }
    }
}