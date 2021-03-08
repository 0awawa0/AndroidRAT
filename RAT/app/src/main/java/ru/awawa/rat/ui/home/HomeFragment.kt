package ru.awawa.rat.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.awawa.rat.Application
import ru.awawa.rat.R
import ru.awawa.rat.server.Interactor
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

        val etIp = root.findViewById<EditText>(R.id.etIp)
        val etPort = root.findViewById<EditText>(R.id.etPort)

        root.findViewById<Button>(R.id.btConnect).setOnClickListener {
            Interactor.init(etIp.text.toString(), etPort.text.toString().toInt())
            Interactor.sendToken()
        }

        return root
    }
}