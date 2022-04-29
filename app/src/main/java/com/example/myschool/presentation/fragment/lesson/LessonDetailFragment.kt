package com.example.myschool.presentation.fragment.lesson

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myschool.databinding.FragmentLessonDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LessonDetailFragment(private val itemId: Int) : Fragment() {

    private lateinit var binding: FragmentLessonDetailBinding

    private val viewModelLessonDetail by viewModel<ViewModelLessonDetail>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLessonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(idItem: Int) = LessonDetailFragment(idItem)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModelLessonDetail.getLessonDetail(itemId)

        viewModelLessonDetail.lessonDetail.observe(viewLifecycleOwner) {
            binding.tvLessonDetailName.text = it["name"]
            binding.tvMarks.text = it["marks"]
            binding.tvMarkCount.text = it["count"]
            binding.tvMarkAverage.text = it["average"]
            binding.tvInfo.text = it["info"]
        }
    }
}