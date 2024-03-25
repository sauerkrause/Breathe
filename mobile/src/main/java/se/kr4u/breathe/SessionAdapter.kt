package se.kr4u.breathe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class SessionAdapter : ListAdapter<Session, SessionAdapter.ViewHolder>(SessionsComparator()) {
    var onItemClick: ((Session, View) -> Unit)? = null
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val inhaleDuration: TextView = view.findViewById(R.id.inhale_duration)
        private val exhaleDuration: TextView = view.findViewById(R.id.exhale_duration)
        private val repetitions: TextView = view.findViewById(R.id.repetitions)
        private val startSession: MaterialButton = view.findViewById(R.id.start_session)
        lateinit var sessionEntity: Session
        var onItemClick: ((Session, View) -> Unit)? = null

        init {
            startSession.setOnClickListener {
                when (it.id) {
                    R.id.start_session -> {
                        onItemClick?.invoke(sessionEntity, it.parent as View)
                    }
                    else -> {}
                }
            }
        }
        fun bind(session: Session?) {
            inhaleDuration.text = session?.inhaleDuration.toString()
            exhaleDuration.text = session?.exhaleDuration.toString()
            repetitions.text = session?.repetitions.toString()
            sessionEntity = session!!
        }

        companion object {
            fun create(parent: ViewGroup): SessionAdapter.ViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.session_row_item, parent, false)
                return SessionAdapter.ViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(viewGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.onItemClick = onItemClick
    }
}

class SessionsComparator : DiffUtil.ItemCallback<Session>() {
    override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem.exhaleDuration == newItem.exhaleDuration && oldItem.inhaleDuration == newItem.inhaleDuration && oldItem.repetitions == newItem.repetitions
    }
}