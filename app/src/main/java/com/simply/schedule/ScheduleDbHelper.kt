package com.simply.schedule

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.joda.time.*
import org.joda.time.format.ISODateTimeFormat

fun CharSequence.trimToNull(): CharSequence? {
    val trimmed = trim()
    return if (trimmed.isNotEmpty()) trimmed else null
}

fun String.trimToNull(): String? = (this as CharSequence).trimToNull()?.toString()

class ScheduleDbHelper(private val context: Context) :
    SQLiteOpenHelper(context, "Schedule", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = ON;")
        db.execSQL(
            """
            CREATE TABLE subjects (
            _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            title TEXT UNIQUE NOT NULL,
            colorId INTEGER NOT NULL,

            FOREIGN KEY (colorId) REFERENCES colors(_id)
            ON DELETE CASCADE ON UPDATE CASCADE);"""
        )
        db.execSQL(
            """
            CREATE TABLE teachers (
            _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            name TEXT UNIQUE NOT NULL,
            phone TEXT,
            email TEXT);"""
        )
        db.execSQL(
            """
            CREATE TABLE classTypes (
            _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            title TEXT UNIQUE NOT NULL,
            isCustom TEXT NOT NULL DEFAULT 'Y')"""
        )
        db.execSQL(
            """
            CREATE TABLE classes (
            _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            subjectId INTEGER NOT NULL,
            typeId INTEGER NOT NULL DEFAULT 1,
            teacherId INTEGER,
            location TEXT,

            FOREIGN KEY (subjectId) REFERENCES subjects(_id)
            ON DELETE CASCADE ON UPDATE CASCADE,
            FOREIGN KEY (teacherId) REFERENCES teachers(_id)
            ON DELETE SET NULL ON UPDATE CASCADE,
            FOREIGN KEY (typeId) REFERENCES classTypes(_id)
            ON DELETE SET DEFAULT ON UPDATE CASCADE);"""
        )
        db.execSQL(
            """
            CREATE TABLE times (
            _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            classId INTEGER NOT NULL,
            period TEXT,
            daysOfWeek TEXT,
            dateStart TEXT,
            dateEnd TEXT,
            timeStart TEXT NOT NULL,
            timeEnd TEXT NOT NULL,

            FOREIGN KEY (classId) REFERENCES classes(_id)
            ON DELETE CASCADE ON UPDATE CASCADE);"""
        )
        db.execSQL(
            """
            CREATE TABLE tasks (
            _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            description TEXT,
            priority INTEGER NOT NULL DEFAULT 0,
            isCompleted TEXT NOT NULL DEFAULT 'N',
            classId INTEGER,
            dueDate TEXT,
            completedAt TEXT,
            createdAt TEXT NOT NULL,

            FOREIGN KEY (classId) REFERENCES classes(_id)
            ON DELETE CASCADE ON UPDATE CASCADE);"""
        )
        db.execSQL(
            """
            CREATE TABLE colors (
            _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            color TEXT UNIQUE);"""
        )

        ContentValues().apply {
            put("isCustom", "N")

            for (type in context.resources.getStringArray(R.array.default_class_types)) {
                put("title", type)
                db.insertOrThrow("classTypes", null, this)
            }
        }

        ContentValues().apply {
            for (color in context.resources.getStringArray(R.array.default_subject_colors)) {
                put("color", color)
                db.insertOrThrow("colors", null, this)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

//    @Throws(SQLException::class)
//    fun addSubject(title: String, colorId: Long): Long {
//        val db = writableDatabase
//        val cv = ContentValues().apply {
//            put("title", title)
//            put("colorId", colorId)
//        }
//        val id = db.insertOrThrow("subjects", null, cv)
//        db.close()
//        return id
//    }

//    fun addClassType(title: String): Long {
//        val db = writableDatabase
//        val cv = ContentValues()
//        cv.put("title", title)
//        val id = db.insertOrThrow("classTypes", null, cv)
//        db.close()
//        return id
//    }
//
//    fun getSubject(id: Long): Cursor = readableDatabase.rawQuery(
//        """
//        SELECT subjects._id, title, colors.color FROM subjects
//        INNER JOIN colors ON subjects.colorId = colors._id
//        WHERE subjects._id = ? LIMIT 1""", arrayOf(id.toString())
//    )

//    fun addTeacher(name: String, phone: String?, email: String?): Long {
//        val db = writableDatabase
//        val cv = ContentValues().apply {
//            put("name", name)
//            put("phone", phone)
//            put("email", email)
//        }
//        val teacherId = db.insertOrThrow("teachers", null, cv)
//        db.close()
//        return teacherId
//    }

//    fun getTeacher(id: Long): Cursor = readableDatabase.rawQuery(
//        """
//        SELECT * FROM teachers
//        WHERE _id = ? LIMIT 1""", arrayOf(id.toString())
//    )
//
//    fun getClassType(id: Long): Cursor = readableDatabase.rawQuery(
//        """
//        SELECT * FROM classTypes
//        WHERE _id = ? LIMIT 1""", arrayOf(id.toString())
//    )

//    fun removeTeacher(id: Long) =
//        writableDatabase.delete("teachers", "_id = ?", arrayOf(id.toString()))

//    fun removeClass(id: Long) =
//        writableDatabase.delete("classes", "_id = ?", arrayOf(id.toString()))

//    fun fetchClasses(date: LocalDate): Cursor {
//        val dateDOW = date.dayOfWeek
//
//        val db = readableDatabase
//        val args = arrayOf(format(date), "%$dateDOW%")
//        val cursor = db.rawQuery(
//            """
//            SELECT classId, dateStart, period FROM times
//            WHERE dateStart <= ?
//            AND (dateEnd >= ?1 OR dateEnd IS NULL)
//            AND (daysOfWeek LIKE ?2 OR daysOfWeek IS NULL)""", args
//        )
//
//        val classIdColumn = cursor.getColumnIndex("classId")
//        val dateStartColumn = cursor.getColumnIndex("dateStart")
//        val periodColumn = cursor.getColumnIndex("period")
//
//        val classIds = arrayListOf<Long>()
//        var startDate: LocalDate
//        var recurrence: Period?
//        while (cursor.moveToNext()) {
//            startDate = LocalDate.parse(cursor.getString(dateStartColumn))
//            recurrence = if (!cursor.isNull(periodColumn)) {
//                Period.parse(cursor.getString(periodColumn))
//            } else null
//
//            if (isOccurrence(date, startDate, recurrence)) {
//                classIds.add(cursor.getLong(classIdColumn))
//            }
//        }
//        cursor.close()
//
//        return db.rawQuery(
//            """
//            SELECT classes._id,
//            subjects.title AS subject,
//            colors.color AS color,
//            classTypes.title AS type,
//            times.timeStart, timeEnd,
//            location,
//            teachers.name AS teacher FROM classes
//            INNER JOIN subjects ON classes.subjectId = subjects._id
//            INNER JOIN colors ON subjects.colorId = colors._id
//            INNER JOIN times ON times.classId = classes._id
//            INNER JOIN classTypes ON classes.typeId = classTypes._id
//            LEFT JOIN teachers ON classes.teacherId = teachers._id
//            WHERE classes._id IN (${format(classIds)})
//            ORDER BY timeStart ASC""", null
//        )
//    }

//    fun fetchSubjects(): Cursor = readableDatabase.rawQuery(
//        """
//        SELECT subjects._id, title, colors.color FROM subjects
//        INNER JOIN colors ON subjects.colorId = colors._id""", null
//    )

//    fun fetchClassTypes(): Cursor = readableDatabase.rawQuery(
//        "SELECT * FROM classTypes", null
//    )

//    fun fetchTeachers(): Cursor = readableDatabase.rawQuery(
//        "SELECT * FROM teachers ORDER BY name", null
//    )

//    private fun isOccurrence(date: LocalDate, start: LocalDate, recurrence: Period?): Boolean =
//        date.isEqual(
//            if (recurrence == null) start
//            else this.getClosestFutureOccurrence(date, start, recurrence)
//        )
//
//    private fun getClosestFutureOccurrence(
//        date: LocalDate,
//        start: LocalDate,
//        recurrence: Period
//    ): LocalDate {
//        var shiftedStart = start
//        if (recurrence.weeks != 0) {
//            val dateDOW = date.dayOfWeek
//            val startDOW = shiftedStart.dayOfWeek
//            // set startFrom's dow to be equal to date's dow, to add recurrence period correctly
//            if (startDOW < dateDOW) {
//                shiftedStart = shiftedStart.plusDays(dateDOW - startDOW)
//            } else if (startDOW > dateDOW) {
//                shiftedStart = shiftedStart.minusDays(startDOW - dateDOW)
//            }
//        }
//
//        var occurrence = shiftedStart
//
//        val distance = Days.daysBetween(shiftedStart, date).days
//        if (distance > 0) {
//            val factor = distance / Days.standardDaysIn(recurrence).days
//            if (factor > 0) {
//                val quickAdvance = recurrence.multipliedBy(factor)
//                occurrence = shiftedStart.plus(quickAdvance)
//            }
//        }
//
//        while (occurrence.isBefore(date)) {
//            occurrence = occurrence.plus(recurrence)
//        }
//
//        return occurrence
//    }

//    fun fetchClass(id: Long?): Cursor = readableDatabase.rawQuery(
//        """
//        SELECT classes._id,
//        subjects.title AS subject,
//        classTypes.title AS type,
//        times.timeStart, timeEnd,
//        location,
//        teachers.name AS teacher FROM classes
//        INNER JOIN subjects ON classes.subjectId = subjects._id
//        AND classes._id = ?
//        INNER JOIN times ON times.classId = classes._id
//        INNER JOIN classTypes ON classes.typeId = classTypes._id
//        LEFT JOIN teachers ON classes.teacherId = teachers._id
//        ORDER BY timeStart""", arrayOf(id.toString())
//    )

    fun fetchTasks(): Cursor = readableDatabase.rawQuery(
        """
        SELECT tasks._id, tasks.title, description, priority,
        isCompleted, dueDate,
        subjects.title AS subject,
        colors.color AS subjectColor,
        classTypes.title AS classType FROM tasks
        LEFT JOIN classes ON tasks.classId = classes._id
        LEFT JOIN classTypes ON classes.typeId = classTypes._id
        LEFT JOIN subjects ON classes.subjectId = subjects._id
        LEFT JOIN colors ON subjects.colorId = colors._id
        WHERE isCompleted = 'N'
        ORDER BY dueDate IS NOT NULL DESC, dueDate ASC, priority DESC""", null
    )

//    fun fetchTasks(classId: Long?): Cursor = readableDatabase.query(
//        "tasks", null, "classId = ? AND isCompleted = 'N'",
//        arrayOf(classId.toString()), null, null, null
//    )

    fun fetchColors(): Cursor = readableDatabase.rawQuery("SELECT * FROM colors", null)

    fun markTaskCompleted(id: Long) {
        val cv = ContentValues().apply {
            put("isCompleted", "Y")
            put("completedAt", format(LocalDateTime.now()))
        }
        writableDatabase.update("tasks", cv, "_id = ?", arrayOf(id.toString()))
    }

    fun markTaskNotCompleted(id: Long) {
        val cv = ContentValues().apply {
            put("isCompleted", "N")
            putNull("completedAt")
        }
        writableDatabase.update("tasks", cv, "_id = ?", arrayOf(id.toString()))
    }

    companion object {

        const val INVALID_ID: Long = -1

        fun format(collection: Iterable<*>): String = collection.joinToString(",")

        fun format(date: LocalDate): String = date.toString()

        fun format(time: LocalTime): String = time.toString("HH:mm")

        fun format(dateTime: LocalDateTime): String =
            ISODateTimeFormat.dateTimeNoMillis().print(dateTime)

        fun getShortDate(date: LocalDate): String = date.toString("dd.MM.yy")

        fun getSimpleDate(date: LocalDate): String =
            if (LocalDate.now().year == date.year) {
                date.toString("d MMMM")
            } else {
                date.toString("d MMMM YYYY")
            }

        fun getDateRelativeToToday(context: Context, date: LocalDate): String {
            val now = LocalDate.now()
            val period = Period.fieldDifference(now, date)
            val weeks = date.weekOfWeekyear - now.weekOfWeekyear

            val r = context.resources

            if (period.years == 0) {
                when (period.days) {
                    0 -> return r.getString(R.string.today)
                    1 -> return r.getString(R.string.tomorrow)
                    -1 -> return r.getString(R.string.yesterday)
                }

                when (weeks) {
                    0 -> return date.toString("EEEE").capitalize()
                    1 -> return r.getStringArray(R.array.next_dows)[date.dayOfWeek - 1]
                    2, 3, 4 -> return r.getQuantityString(R.plurals.in_weeks, weeks, weeks)

                    -1 -> return r.getStringArray(R.array.last_dows)[date.dayOfWeek - 1]
                    -2, -3, -4 -> return r.getQuantityString(R.plurals.weeks_ago, -weeks, -weeks)
                }

                return when (period.months) {
                    1 -> r.getString(R.string.next_month)
                    -1 -> r.getString(R.string.last_month)
                    else -> date.toString("MMMM").capitalize()
                }
            }

            return date.toString("MMMM yyyy").capitalize()
        }
    }
}
