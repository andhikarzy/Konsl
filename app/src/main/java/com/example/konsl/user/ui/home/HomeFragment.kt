package com.example.konsl.user.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.example.konsl.LoginActivity
import com.example.konsl.R
import com.example.konsl.adapter.ArticleAdapter
import com.example.konsl.adapter.TutorialAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var tutorialAdapter: TutorialAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var educationsSkeleton: RecyclerViewSkeletonScreen
    private lateinit var tutorialSkeleton: RecyclerViewSkeletonScreen
    private lateinit var nextConsultationTimeDiffSkeleton: ViewSkeletonScreen
    private lateinit var nextConsultationDateSkeleton: ViewSkeletonScreen
    private lateinit var nextConsultationTimeSkeleton: ViewSkeletonScreen

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        articleAdapter = ArticleAdapter()
        articleAdapter.notifyDataSetChanged()

        rvArticles.layoutManager = LinearLayoutManager(context)
        rvArticles.adapter = articleAdapter
        rvArticles.isNestedScrollingEnabled = false

        tutorialAdapter = TutorialAdapter()
        tutorialAdapter.notifyDataSetChanged()

        rvTutorials.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTutorials.adapter = tutorialAdapter

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.loadArticles()
        homeViewModel.loadTutorials()
        homeViewModel.loadNextConsultationInfo()

        educationsSkeleton = Skeleton.bind(rvArticles)
                .adapter(articleAdapter)
                .load(R.layout.skeleton_item_article)
                .shimmer(true)
                .duration(500)
                .count(2)
                .show()
        homeViewModel.getArticles().observe(viewLifecycleOwner, Observer { articles ->
            articles?.let {
                educationsSkeleton.hide()
                articleAdapter.setData(it)
            }
        })
        tutorialSkeleton = Skeleton.bind(rvTutorials)
                .adapter(tutorialAdapter)
                .load(R.layout.item_tutorial)
                .shimmer(true)
                .duration(500)
                .count(2)
                .show()
        homeViewModel.getTutorials().observe(viewLifecycleOwner, Observer { tutorials ->
            tutorials?.let {
                tutorialSkeleton.hide()
                tutorialAdapter.setData(it)
            }
        })
        nextConsultationTimeDiffSkeleton = Skeleton.bind(tvNextConsultationTimeDiff)
                .load(R.layout.skeleton_next_consultation_time_diff)
                .shimmer(true)
                .duration(500)
                .show()
        homeViewModel.getNextConsultationTimeDiff().observe(viewLifecycleOwner, Observer { date ->
            date?.let {
                nextConsultationTimeDiffSkeleton.hide()
                tvNextConsultationTimeDiff.text = if(it != "-") getString(R.string.time_remaining, it) else getString(R.string.nothing)
            }
        })
        nextConsultationDateSkeleton = Skeleton.bind(tvNextConsultationDate)
                .load(R.layout.skeleton_next_consultation_date)
                .shimmer(true)
                .duration(500)
                .show()
        homeViewModel.getNextConsultationDate().observe(viewLifecycleOwner, Observer { counselor ->
            counselor?.let {
                nextConsultationDateSkeleton.hide()
                tvNextConsultationDate.text = if(it != "-") it else getString(R.string.nothing)
            }
        })
        nextConsultationTimeSkeleton = Skeleton.bind(tvNextConsultationTime)
                .load(R.layout.skeleton_next_consultation_time)
                .shimmer(true)
                .duration(500)
                .show()
        homeViewModel.getNextConsultationTime().observe(viewLifecycleOwner, Observer { time ->
            time?.let {
                nextConsultationTimeSkeleton.hide()
                tvNextConsultationTime.text = if(it != "-") it else getString(R.string.nothing)
            }
        })

        setHasOptionsMenu(true)

        tvMoreEducations.setOnClickListener(this)
        tvMoreConsultations.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menuLogout){
            mAuth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvMoreEducations -> {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_educations)
            }
            R.id.tvMoreConsultations -> {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_consultations)
            }
        }
    }
}