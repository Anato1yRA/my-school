package com.example.myschool.presentation.fragment.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myschool.databinding.FragmentReportBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentReport : Fragment() {

    private lateinit var binding: FragmentReportBinding

    private val vmReport by viewModel<VMReport>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = FragmentReport()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvReport.layoutManager = LinearLayoutManager(context)

        vmReport.report.observe(viewLifecycleOwner) {
            if (it != null) {
                val adapter = RVReportAdapter()
                adapter.setReportList(it as ArrayList<Map<String, String>>)
                binding.rvReport.adapter = adapter
            }
        }
    }
}