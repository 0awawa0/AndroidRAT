package ru.awawa.rat.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.awawa.rat.BuildConfig
import ru.awawa.rat.R
import ru.awawa.rat.service.RatForegroundService
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
            ContextCompat.startForegroundService(requireContext(), Intent(requireContext(), RatForegroundService::class.java))
        }

        btStop.setOnClickListener {
            requireActivity().stopService(Intent(requireContext(), RatForegroundService::class.java))
        }
        return root
    }
}