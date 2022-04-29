package com.example.myschool.presentation.fragment.report

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myschool.R
import com.example.myschool.databinding.ReportItemBinding

class RVReportAdapter : RecyclerView.Adapter<RVReportAdapter.HolderReport>() {

    private var reportList = ArrayList<Map<String, String>>()

    class HolderReport(item: View) : RecyclerView.ViewHolder(item) {

        private val binding = ReportItemBinding.bind(item)

        fun bind(report: Map<String, String>) = with(binding) {
            tvReportDate.text = "${report["date"]}"
            tvReportMessage.text = "${report["text"]}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderReport {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_item, parent, false)
        return HolderReport(view)
    }

    override fun onBindViewHolder(holder: HolderReport, position: Int) {
        holder.bind(reportList[position])
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setReportList(newReportList: ArrayList<Map<String, String>>) {
        reportList = newReportList
        notifyDataSetChanged()
    }
}