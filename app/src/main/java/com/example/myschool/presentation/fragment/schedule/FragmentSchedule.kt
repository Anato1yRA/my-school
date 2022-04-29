package com.example.myschool.presentation.fragment.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myschool.databinding.FragmentScheduleBinding

class FragmentSchedule : Fragment() {

    private lateinit var binding: FragmentScheduleBinding

//    private val vmReport by viewModel<VMReport>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = FragmentSchedule()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        binding.calendar.
//        binding.rvReport.layoutManager = LinearLayoutManager(context)
//
//        vmReport.report.observe(viewLifecycleOwner) {
//            if (it != null) {
//                val adapter = RVReportAdapter()
//                adapter.setReportList(it as ArrayList<Map<String, String>>)
//                binding.rvReport.adapter = adapter
//            }
//        }
    }
}