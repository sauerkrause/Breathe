package se.kr4u.breathe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionAdapter(private val dataSession: Array<Session>) : RecyclerView.Adapter<SessionAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val inhaleDuration: TextView
        val exhaleDuration: TextView
        val repetitions: TextView
        init {
            inhaleDuration = view.findViewById(R.id.inhale_duration)
            exhaleDuration = view.findViewById(R.id.exhale_duration)
            repetitions = view.findViewById(R.id.repetitions)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.session_row_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.inhaleDuration.text = dataSession[position].inhaleDuration.toString()
        holder.exhaleDuration.text = dataSession[position].exhaleDuration.toString()
        holder.repetitions.text = dataSession[position].repetitions.toString()
    }

    override fun getItemCount(): Int {
        return dataSession.size
    }
}