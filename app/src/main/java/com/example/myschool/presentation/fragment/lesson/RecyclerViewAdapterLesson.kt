package com.example.myschool.presentation.fragment.lesson

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myschool.R
import com.example.myschool.databinding.LessonItemBinding


class RecyclerViewAdapterLesson(private val onItemClicked: (Int) -> Unit) :
    RecyclerView.Adapter<RecyclerViewAdapterLesson.HolderLesson>() {

    private var lessonList = ArrayList<ModelLesson>()

    class HolderLesson(item: View) : RecyclerView.ViewHolder(item) {

        private val binding = LessonItemBinding.bind(item)

        fun bind(modelLesson: ModelLesson) = with(binding) {
            textViewLessonID.text = modelLesson.id
            textViewLesson.text = modelLesson.name
            textViewGrade.text = modelLesson.grade
            textViewAverage.text = modelLesson.average
            textViewInfo.text = modelLesson.info

            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderLesson {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lesson_item, parent, false)
        return HolderLesson(view)
    }

    override fun onBindViewHolder(holder: HolderLesson, position: Int) {
        holder.bind(lessonList[position])
        holder.itemView.setOnClickListener {
            it.setBackgroundColor(ContextCompat.getColor(it.context, R.color.color_primary))
            onItemClicked(lessonList[position].id.toInt())
        }
    }

    override fun getItemCount(): Int {
        return lessonList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setLessonList(newLessonList: ArrayList<ModelLesson>) {
        lessonList = newLessonList
        notifyDataSetChanged()
    }
}