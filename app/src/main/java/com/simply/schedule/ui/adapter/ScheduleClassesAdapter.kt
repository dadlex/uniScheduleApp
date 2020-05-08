package com.simply.schedule.adapter

import android.animation.*
import android.content.Context
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.simply.schedule.R
import com.simply.schedule.network.ScheduleClass
import org.joda.time.LocalTime

class ScheduleClassesAdapter(val context: Context, list: List<ScheduleClass>?) :
    ListRecyclerViewAdapter<ScheduleClassesAdapter.ViewHolder, ScheduleClass>(list, object : IdGetter<ScheduleClass> {
        override fun getId(item: ScheduleClass): Long {
            return item.id!!
        }
    }), View.OnCreateContextMenuListener {

    var position: Int = RecyclerView.NO_POSITION
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_class, parent, false)
        val holder = ViewHolder(view)

        holder.clMain.also {
            it.setOnClickListener { toggleCard(holder) }
            it.setOnLongClickListener {
                position = holder.adapterPosition
                false
            }
            it.setOnCreateContextMenuListener(this)
        }
        return holder
    }

    private fun toggleCard(holder: ViewHolder) {
        if (holder.clExpandable.visibility != View.VISIBLE) {
            expandCard(holder)
        } else {
            collapseCard(holder)
        }
    }

    private fun expandCard(holder: ViewHolder) {
        if (holder.expandCollapseAnimatorSet.isRunning) {
            return
        }

        val v = holder.clExpandable
        v.measure(
            View.MeasureSpec.makeMeasureSpec(v.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val slideAnimator = ValueAnimator.ofInt(0, v.measuredHeight).apply {
            addUpdateListener { animation ->
                v.layoutParams.height = animation.animatedValue as Int
                v.requestLayout()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    v.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                }

                override fun onAnimationStart(animation: Animator) {
                    v.visibility = View.VISIBLE
                }
            })
        }
        val contentAlphaAnimator = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f)
        val arrowRotationAnimator =
            ObjectAnimator.ofFloat(holder.ivExpandArrow, "rotation", 0f, 180f)

        holder.expandCollapseAnimatorSet = AnimatorSet().apply {
            play(slideAnimator)
                .with(contentAlphaAnimator)
                .with(arrowRotationAnimator)
            duration = 300
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun collapseCard(holder: ViewHolder) {
        if (holder.expandCollapseAnimatorSet.isRunning) {
            return
        }

        val v = holder.clExpandable

        val slideAnimator = ValueAnimator.ofInt(v.height, 0).apply {
            addUpdateListener { animation ->
                v.layoutParams.height = animation.animatedValue as Int
                v.requestLayout()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    v.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    v.visibility = View.GONE
                }
            })
        }
        val alphaAnimator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f)
        val rotationAnimator = ObjectAnimator.ofFloat(holder.ivExpandArrow, "rotation", 180f, 360f)

        holder.expandCollapseAnimatorSet = AnimatorSet().apply {
            play(slideAnimator)
                .with(alphaAnimator)
                .with(rotationAnimator)
            duration = 300
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ScheduleClass) {
        holder.tvTimeStart.text = LocalTime.parse(item.timeStart).toString("HH:mm")
        holder.tvTimeEnd.text = LocalTime.parse(item.timeEnd).toString("HH:mm")
        holder.tvTitle.text = item.subject.title
        holder.tvType.text = item.type.title
//        holder.colorView.imageTintList =
//            ColorStateList.valueOf(Color.parseColor(item.subject.color))

        if (item.teacher != null) {
            holder.llTeacherRow.visibility = View.VISIBLE
            holder.tvTeacher.text = item.teacher.name
        } else {
            holder.llTeacherRow.visibility = View.GONE
        }

        if (item.location != null) {
            holder.llLocationRow.visibility = View.VISIBLE
            holder.tvLocation.text = item.location
        } else {
            holder.llLocationRow.visibility = View.GONE
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu, view: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        //menu.add(Menu.NONE, R.id.cm_edit, Menu.NONE, R.string.context_menu_edit);
        //menu.add(Menu.NONE, R.id.cm_remove, Menu.NONE, R.string.context_menu_remove) TODO: uncomment when canary bug will be fixed
        menu.add(Menu.NONE, 3, Menu.NONE, R.string.context_menu_remove)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var expandCollapseAnimatorSet = AnimatorSet()

        val tvTimeStart: TextView = v.findViewById(R.id.tvClassStartTime)
        val tvTimeEnd: TextView = v.findViewById(R.id.tvClassEndTime)
        val tvTitle: TextView = v.findViewById(R.id.tvSubject)
        val tvType: TextView = v.findViewById(R.id.tvClassType)
        val tvLocation: TextView = v.findViewById(R.id.tvClassLocation)
        val tvTeacher: TextView = v.findViewById(R.id.tvClassTeacher)
        val ivExpandArrow: ImageView = v.findViewById(R.id.ivExpandIcon)
        val colorView: ImageView = v.findViewById(R.id.ivSubjectColor)

        val clMain: ConstraintLayout = v.findViewById(R.id.clMainContent)
        val clExpandable: ConstraintLayout = v.findViewById(R.id.clExpandableContent)

        val llLocationRow: LinearLayout = v.findViewById(R.id.llLocationRow)
        val llTeacherRow: LinearLayout = v.findViewById(R.id.llTeacherRow)
    }
}
