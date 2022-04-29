package com.example.myschool.presentation.fragment.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myschool.R
import com.example.myschool.databinding.FragmentLessonBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentLesson : Fragment() {

    private lateinit var binding: FragmentLessonBinding

    private val viewModelLesson by viewModel<ViewModelLesson>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLessonBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = FragmentLesson()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewLesson.layoutManager = LinearLayoutManager(context)

        viewModelLesson.lesson.observe(viewLifecycleOwner) {
            if (it != null) {
                val adapter = RecyclerViewAdapterLesson { itemId ->
                    openFragment(LessonDetailFragment.newInstance(itemId))
                }

                adapter.setLessonList(it as ArrayList<ModelLesson>)

                binding.recyclerViewLesson.adapter = adapter
            }
        }
    }

    // Подключаем нужный фрагмент
    private fun openFragment(fragment: Fragment) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.frameLayoutMain, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}