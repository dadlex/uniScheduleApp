package com.simply.schedule.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simply.schedule.R
import com.simply.schedule.ScheduleDbHelper
import com.simply.schedule.adapter.CursorRecyclerViewAdapter

class SubjectsDialogBottomSheet : BottomSheetDialogFragment() {

    var listener: OnSubjectSetListener? = null
    var allowCreateNewSubject = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_bottom_sheet_subjects, container, false)

        val subjects = ScheduleDbHelper(context!!).fetchSubjects()
        view.findViewById<RecyclerView>(R.id.rvSubjects).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = SubjectsAdapter(subjects)
        }
        if (subjects.count == 0) {
            view.findViewById<View>(R.id.emptyView).visibility = View.VISIBLE
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val parent = view!!.parent as View
        val behavior = (parent.layoutParams as CoordinatorLayout.LayoutParams)
            .behavior as BottomSheetBehavior
        val appBarLayout = (view as View).findViewById<AppBarLayout>(R.id.app_bar)
        val toolbar = appBarLayout.findViewById<Toolbar>(R.id.toolbar)
        val navigationIcon = toolbar.navigationIcon

        toolbar.navigationIcon = null
        toolbar.setNavigationOnClickListener {
            dialog?.cancel()
        }

        if (allowCreateNewSubject) {
            val addMenuItemId = 0
            toolbar.menu
                .add(Menu.NONE, addMenuItemId, Menu.NONE, R.string.button_add_subject)
                .setIcon(R.drawable.ic_add_black_24dp)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    addMenuItemId -> {
                        listener?.onCreateNewSubject()
                        dismiss()
                        true
                    }
                    else -> false
                }
            }
        }

        val fadeOutAnimator = ObjectAnimator.ofFloat(appBarLayout, "alpha", 1f, 0f).also {
            it.duration = 150
            it.interpolator = DecelerateInterpolator()
            it.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    when (behavior.state) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            toolbar.navigationIcon = navigationIcon
                            ViewCompat.setElevation(appBarLayout, 6f)
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            toolbar.navigationIcon = null
                            ViewCompat.setElevation(appBarLayout, 0f)
                        }
                    }
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })
        }
        val fadeInAnimator = ObjectAnimator.ofFloat(appBarLayout, "alpha", 0f, 1f).apply {
            duration = 150
            interpolator = DecelerateInterpolator()
        }
        val animatorSet = AnimatorSet().apply {
            play(fadeOutAnimator).before(fadeInAnimator)
        }

        val canTakeFullScreen by lazy {
            val content = view as View
            val offset = (content.parent.parent as CoordinatorLayout).height - content.height
            offset <= 0
        }
        var isToolbarInFullMode = false
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (!isToolbarInFullMode && canTakeFullScreen) {
                            animatorSet.start()
                            isToolbarInFullMode = true
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (isToolbarInFullMode && canTakeFullScreen) {
                            animatorSet.start()
                            isToolbarInFullMode = false
                        }
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    interface OnSubjectSetListener {
        fun onSubjectSet(id: Long)
        fun onCreateNewSubject()
    }

    inner class SubjectsAdapter(cursor: Cursor?) :
        CursorRecyclerViewAdapter<SubjectsAdapter.ViewHolder>(cursor) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_subject, parent, false)
            val holder = ViewHolder(view)

            holder.itemView.setOnClickListener {
                listener?.onSubjectSet(getItemId(holder.adapterPosition))
                dialog?.dismiss()
            }

            return holder
        }

        override fun onBindViewHolder(holder: ViewHolder, cursor: Cursor) {
            with(holder) {
                tvSubject.text = cursor.getString(cursor.getColumnIndex("title"))
                ivColor.imageTintList = ColorStateList.valueOf(
                    Color.parseColor(
                        cursor.getString(cursor.getColumnIndex("color"))
                    )
                )
            }
        }

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvSubject: TextView = v.findViewById(R.id.tvTitle)
            val ivColor: ImageView = v.findViewById(R.id.ivColor)
        }
    }
}
