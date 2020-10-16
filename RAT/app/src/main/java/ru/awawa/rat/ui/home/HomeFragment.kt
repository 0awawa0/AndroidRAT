package ru.awawa.rat.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ru.awawa.rat.R
import ru.awawa.rat.worker.BackgroundWorker
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private var workId: UUID? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val btStart: Button = root.findViewById(R.id.btStart)
        val btStop: Button = root.findViewById(R.id.btStop)

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        btStart.setOnClickListener {
            val request = OneTimeWorkRequestBuilder<BackgroundWorker>().build()
            this.workId = request.id
            WorkManager.getInstance(this.requireContext()).enqueue(request)
        }

        btStop.setOnClickListener {
            if (this.workId != null) {
                WorkManager.getInstance(this.requireContext()).cancelWorkById(this.workId!!)
            }
        }
        return root
    }
}