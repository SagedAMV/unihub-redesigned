package com.unihub.app.design

import androidx.compose.ui.graphics.Color

/**
 * بيانات نموذجية ثابتة (بلا ViewModels ولا Room) تحاكي طالباً جامعياً
 * حقيقياً — لقطة معروضة لغرض تقييم التصميم فقط، وهي مقصودة الثبات حتى
 * تكون صور الاختبار حتمية قابلة للمقارنة.
 */

data class SampleLecture(
    val subject: String,
    val timeFrom: String,
    val timeTo: String,
    val room: String,
    val doctor: String,
    val ongoing: Boolean = false
)

data class SampleTask(
    val title: String,
    val description: String,
    val priority: SamplePriority,
    val dueLabel: String,
    val overdue: Boolean = false,
    val done: Boolean = false
)

enum class SamplePriority(val label: String) {
    HIGH("عالية"),
    MEDIUM("متوسطة"),
    LOW("منخفضة")
}

data class SampleExam(
    val subject: String,
    val type: String,
    val room: String,
    val daysRemaining: Int
)

data class SampleFile(
    val name: String,
    val meta: String,
    val kind: SampleFileKind,
    val favorite: Boolean = false
)

enum class SampleFileKind(val label: String, val tint: Color) {
    PDF("PDF", ClayError),
    DOC("مستند", Slate),
    IMAGE("صورة", SemanticSuccess),
    AUDIO("صوت", Sand),
    SLIDES("عرض", SemanticWarning)
}

data class SampleFolder(
    val name: String,
    val count: Int,
    val tint: Color
)

object SampleData {

    const val GREETING = "مساء الخير، ساجد"
    const val DATE = "الجمعة، 14 نوفمبر 2026"

    val nextLecture = SampleLecture(
        subject = "برمجة الأجهزة المحمولة",
        timeFrom = "10:30 ص",
        timeTo = "12:00 م",
        room = "قاعة B-204",
        doctor = "د. أحمد عبدالله",
        ongoing = true
    )

    val todayLectures = listOf(
        SampleLecture("قواعد البيانات", "12:15 م", "13:45 م", "قاعة A-110", "د. منى حسن"),
        SampleLecture("الذكاء الاصطناعي", "2:00 م", "3:30 م", "معمل C-3", "د. خالد يوسف"),
        SampleLecture("أمن المعلومات", "4:00 م", "5:00 م", "قاعة D-201", "د. سارة العلي")
    )

    val tasks = listOf(
        SampleTask(
            title = "تسليم مشروع التخرج — الفصل الثالث",
            description = "رفع الكود والتقرير على المنصة",
            priority = SamplePriority.HIGH,
            dueLabel = "غداً"
        ),
        SampleTask(
            title = "حل واجب الشبكات — الأسئلة 4-9",
            description = "من كتاب Tanenbaum الفصل الثالث",
            priority = SamplePriority.MEDIUM,
            dueLabel = "الأحد",
            overdue = true
        ),
        SampleTask(
            title = "مراجعة فصل قواعد البيانات",
            description = "الاستعداد لامتحان نصف الفصل",
            priority = SamplePriority.MEDIUM,
            dueLabel = "بعد يومين"
        ),
        SampleTask(
            title = "حجز موعد الإرشاد الأكاديمي",
            description = "اختيار مقررات الفصل القادم",
            priority = SamplePriority.LOW,
            dueLabel = "الأسبوع القادم"
        ),
        // منجزات الأسبوع — لعرض حالة «منجزة» (شطب + خانة ممتلئة) في شاشة المهام
        SampleTask(
            title = "قراءة فصل الشبكات الثالث",
            description = "من كتاب Tanenbaum قبل الواجب",
            priority = SamplePriority.MEDIUM,
            dueLabel = "منذ يومين",
            done = true
        ),
        SampleTask(
            title = "تسليم تقرير المعمل الثاني",
            description = "قياس زمن الاستجابة في الشبكة",
            priority = SamplePriority.HIGH,
            dueLabel = "منذ 3 أيام",
            done = true
        )
    )

    val exams = listOf(
        SampleExam("قواعد البيانات", "نصف فصل", "قاعة L-101", 3),
        SampleExam("الذكاء الاصطناعي", "نهائي", "القاعة الكبرى", 12)
    )

    val stats = listOf(
        Triple("4", "مهام معلقة", SemanticWarning),
        Triple("2", "امتحان قادم", SemanticDanger),
        Triple("18", "ملاحظة", SemanticInfo),
        Triple("46", "ملفاً", SemanticSuccess)
    )

    val folders = listOf(
        SampleFolder("السنة الرابعة — الفصل الأول", 24, Color(0xFF4E7D6E)),
        SampleFolder("الشبكات", 12, Color(0xFF5B6478)),
        SampleFolder("مشاريع التخرج", 8, Color(0xFF8A5E5E)),
        SampleFolder("صور المراجع", 18, Color(0xFF5E7A8A))
    )

    val recentFiles = listOf(
        SampleFile("محاضرة 09 — أمان التطبيقات", "PDF · 2.4 ميغابايت · أمس", SampleFileKind.PDF, favorite = true),
        SampleFile("ملخص الشبكات — الفصل الثالث", "مستند · 318 كيلوبايت · أمس", SampleFileKind.DOC),
        SampleFile("لوحة الجدول الزمني", "صورة · 1.1 ميغابايت · قبل يومين", SampleFileKind.IMAGE),
        SampleFile("تسجيل محاضرة الذكاء الاصطناعي", "صوت · 26 ميغابايت · قبل 3 أيام", SampleFileKind.AUDIO),
        SampleFile("عرض مناقشة المشروع", "عرض · 6.8 ميغابايت · قبل 4 أيام", SampleFileKind.SLIDES)
    )

    const val STORAGE_USED_LABEL = "1.2 غيغابايت من 5 غيغابايت"
    const val STORAGE_FRACTION = 0.24f
}
