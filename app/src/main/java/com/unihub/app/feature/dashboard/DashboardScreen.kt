package com.unihub.app.feature.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unihub.app.core.common.DateFormats
import com.unihub.app.core.common.Formatters
import com.unihub.app.core.common.NextLecture
import com.unihub.app.core.common.NextLectureResolver
import com.unihub.app.data.local.entity.ExamEntity
import com.unihub.app.data.local.entity.LectureEntity
import com.unihub.app.data.local.entity.TaskEntity
import com.unihub.app.ui.components.UiMessagesHost
import com.unihub.app.ui.navigation.PlannerTab
import com.unihub.app.ui.theme.SemanticDanger
import com.unihub.app.ui.theme.SemanticInfo
import com.unihub.app.ui.theme.SemanticSuccess
import com.unihub.app.ui.theme.SemanticWarning
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * الشاشة الرئيسية الجديدة — «مركز اليوم» (تصميم الهاتف العمودي المعتمد).
 *
 * التحول عن التصميم السابق: بدل بطاقة اللقطة المقسومة نصفين وقوائم متتابعة
 * طويلة، سرد رأسي واضح الترتيب: ماذا يحدث «الآن» في بطاقة بطلة متدرجة تُقرأ
 * من نظرة، ثم لمحة رقمية مدمجة (أقراص إحصاء قابلة للنقر تغني عن صف الإجراءات
 * السريعة المكرر)، ثم «خط اليوم» الزمني الذي يدمج محاضرات اليوم ومهامه
 * المستحقة في مسار واحد يجيب سؤال «ماذا بعد؟»، وأخيراً عدّادات الامتحانات
 * القادمة — كل ذلك ضمن طية واحدة بلا تمرير على أغلب الأجهزة.
 */
@Composable
fun DashboardScreen(
    onOpenPlanner: (PlannerTab) -> Unit,
    onOpenFiles: (Long?) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    UiMessagesHost(viewModel.messenger, snackbarHostState)

    val nextLecture by viewModel.nextLecture.collectAsStateWithLifecycle()
    val todayLectures by viewModel.todayLectures.collectAsStateWithLifecycle()
    val dueSoonTasks by viewModel.dueSoonTasks.collectAsStateWithLifecycle()
    val upcomingExams by viewModel.upcomingExams.collectAsStateWithLifecycle()
    val pendingTaskCount by viewModel.pendingTaskCount.collectAsStateWithLifecycle()
    val upcomingExamCount by viewModel.upcomingExamCount.collectAsStateWithLifecycle()
    val noteCount by viewModel.noteCount.collectAsStateWithLifecycle()
    val fileCount by viewModel.fileCount.collectAsStateWithLifecycle()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeaderRow(onOpenSettings)

            NowNextHero(
                next = nextLecture,
                todayLectures = todayLectures,
                onOpenSchedule = { onOpenPlanner(PlannerTab.SCHEDULE) }
            )

            // أقراص الإحصاءات قابلة للنقر (اختصارات لوجهاتها) فتغني عن صف
            // إجراءات سريعة منفصل — أقل واجهة تحقق الغرض
            StatsRow(
                pendingTaskCount = pendingTaskCount,
                upcomingExamCount = upcomingExamCount,
                noteCount = noteCount,
                fileCount = fileCount,
                onOpenTasks = { onOpenPlanner(PlannerTab.TASKS) },
                onOpenExams = { onOpenPlanner(PlannerTab.EXAMS) },
                onOpenNotes = { onOpenPlanner(PlannerTab.NOTES) },
                onOpenFiles = { onOpenFiles(null) }
            )

            DayTimelineCard(
                todayLectures = todayLectures,
                dueTasks = dueSoonTasks.filterNot { it.isDone },
                currentLectureId = nextLecture
                    ?.takeIf { it.dayOffset == 0 }
                    ?.lecture
                    ?.id,
                onOpenSchedule = { onOpenPlanner(PlannerTab.SCHEDULE) },
                onOpenTasks = { onOpenPlanner(PlannerTab.TASKS) }
            )

            if (upcomingExams.isNotEmpty()) {
                UpcomingExamsCard(
                    exams = upcomingExams.take(3),
                    onOpenExams = { onOpenPlanner(PlannerTab.EXAMS) }
                )
            }
        }
    }
}

/** الترويسة: التحية والتاريخ + زر إعدادات دائري بنبرة سطح ناعمة */
@Composable
private fun HeaderRow(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = Formatters.greeting(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = LocalDate.now().format(
                    DateTimeFormatter.ofPattern("EEEE، d MMMM yyyy", Locale("ar"))
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        Surface(
            onClick = onOpenSettings,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "الإعدادات",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * البطاقة البطلة «الآن والتالي»: تدرج عميق بهوية التطبيق يحمل المحاضرة الجارية/التالية
 * بحجم يقرأ من نظرة، وشارة زجاجية فاتحة في المقدمة بحالتها الحية، وتحتها محاضرتا
 * اليوم التاليتان خلف فاصل زجاجي رفيع. عند فراغ الجدول تتحول لدعوة لبنائه.
 */
@Composable
private fun NowNextHero(
    next: NextLecture?,
    todayLectures: List<LectureEntity>,
    onOpenSchedule: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val deepPrimary = lerp(primary, Color.Black, 0.22f)
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(primary, deepPrimary),
                    start = Offset(x = 330f, y = 0f),
                    end = Offset(x = 0f, y = 420f)
                )
            )
            .clickable(
                onClickLabel = "عرض الجدول الأسبوعي",
                role = Role.Button,
                onClick = onOpenSchedule
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (next == null) {
                // حالة الجدول الفارغ — بطاقة دعوة لبناء الجدول بدل مساحة صامتة
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = onPrimary.copy(alpha = 0.8f),
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "جدولك فارغ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = onPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "أضف محاضراتك من تبويب الجدول لتظهر مواعيد يومك هنا",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onPrimary.copy(alpha = 0.8f)
                )
            } else {
                GlassChip(lectureStatusLabel(next), emphasized = true)

                Spacer(Modifier.height(12.dp))
                Text(
                    text = next.lecture.subject,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HeroMeta(Icons.Outlined.Schedule, lectureTimeRange(next.lecture))
                    HeroMeta(null, next.lecture.room.takeIf { it.isNotBlank() }?.let { "قاعة $it" })
                    HeroMeta(null, next.lecture.doctor.takeIf { it.isNotBlank() })
                }

                val upcoming = todayLectures
                    .filter { it.id != next.lecture.id }
                    .sortedBy { DateFormats.parseTimeOrNull(it.timeFrom)?.let { t -> t.hour * 60 + t.minute } }
                    .take(2)

                if (upcoming.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(onPrimary.copy(alpha = 0.18f))
                    )
                    Spacer(Modifier.height(10.dp))

                    upcoming.forEach { lecture ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = DateFormats.formatTime12(lecture.timeFrom),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = onPrimary.copy(alpha = 0.95f)
                            )
                            Spacer(Modifier.width(10.dp))
                            Box(
                                Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(onPrimary.copy(alpha = 0.5f))
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = lecture.subject,
                                style = MaterialTheme.typography.bodyMedium,
                                color = onPrimary.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (lecture.room.isNotBlank()) {
                                Text(
                                    text = lecture.room,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = onPrimary.copy(alpha = 0.6f),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** سطر الحالة الحي للمحاضرة الأقرب — الأوقات 12-ساعية بصيغة ص/م */
private fun lectureStatusLabel(next: NextLecture): String = when {
    next.status == NextLecture.Status.ONGOING && next.lecture.timeTo.isNotBlank() ->
        "جارية الآن · تنتهي ${DateFormats.formatTime12(next.lecture.timeTo)}"
    next.status == NextLecture.Status.ONGOING -> "جارية الآن"
    next.dayOffset == 0 ->
        "تبدأ ${DateFormats.formatTime12(next.lecture.timeFrom)} · " +
            NextLectureResolver.formatCountdown(next)
    else -> "${NextLectureResolver.dayLabel(next.dayOffset)} · " +
        "تبدأ ${DateFormats.formatTime12(next.lecture.timeFrom)}"
}

private fun lectureTimeRange(lecture: LectureEntity): String =
    if (lecture.timeTo.isNotBlank()) {
        "${DateFormats.formatTime12(lecture.timeFrom)} – ${DateFormats.formatTime12(lecture.timeTo)}"
    } else {
        DateFormats.formatTime12(lecture.timeFrom)
    }

/** شارة زجاجية على البطاقة المتدرجة — لون onPrimary شفاف فوق التدرج */
@Composable
private fun GlassChip(text: String, emphasized: Boolean) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val container = if (emphasized) onPrimary.copy(alpha = 0.20f)
    else onPrimary.copy(alpha = 0.12f)
    Surface(shape = RoundedCornerShape(50), color = container) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Medium,
            color = onPrimary,
            maxLines = 1
        )
    }
}

@Composable
private fun HeroMeta(icon: androidx.compose.ui.graphics.vector.ImageVector?, text: String?) {
    if (text == null) return
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = onPrimary.copy(alpha = 0.75f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = onPrimary.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** صف الإحصاءات: أقراص مدمجة بلا حدود — أخف من بطاقات الإحصاء الأربع القديمة */
@Composable
private fun StatsRow(
    pendingTaskCount: Int,
    upcomingExamCount: Int,
    noteCount: Int,
    fileCount: Int,
    onOpenTasks: () -> Unit,
    onOpenExams: () -> Unit,
    onOpenNotes: () -> Unit,
    onOpenFiles: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatDisc(
            value = pendingTaskCount.toString(),
            label = "مهام معلقة",
            tint = SemanticWarning,
            onClick = onOpenTasks,
            modifier = Modifier.weight(1f)
        )
        StatDisc(
            value = upcomingExamCount.toString(),
            label = "امتحان قادم",
            tint = SemanticDanger,
            onClick = onOpenExams,
            modifier = Modifier.weight(1f)
        )
        StatDisc(
            value = noteCount.toString(),
            label = "ملاحظة",
            tint = SemanticInfo,
            onClick = onOpenNotes,
            modifier = Modifier.weight(1f)
        )
        StatDisc(
            value = fileCount.toString(),
            label = "ملف",
            tint = SemanticSuccess,
            onClick = onOpenFiles,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatDisc(
    value: String,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = tint
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

/** مدخل واحد في «خط اليوم» — محاضرة أو مهمة مستحقة */
private data class TimelineEntry(
    val title: String,
    val trailing: String,
    val tint: Color,
    val isNow: Boolean,
    val onClick: () -> Unit
)

/**
 * «خط اليوم»: دمج محاضرات اليوم ومهامه المستحقة في مسار زمني واحد بسكة رفيعة
 * ونقاط ملونة حسب النوع — إجابة على سؤال «ماذا بعد؟» الذي كانت تجيب عنه
 * قائمتان منفصلتان في التصميم السابق، والمتأخرة تظهر بلون الخطر فوراً.
 */
@Composable
private fun DayTimelineCard(
    todayLectures: List<LectureEntity>,
    dueTasks: List<TaskEntity>,
    currentLectureId: Long?,
    onOpenSchedule: () -> Unit,
    onOpenTasks: () -> Unit
) {
    // المحاضرات بترتيب وقتها ثم المهام المستحقة (المتأخرة أولاً)
    val entries = buildList {
        todayLectures
            .sortedBy { DateFormats.parseTimeOrNull(it.timeFrom)?.let { t -> t.hour * 60 + t.minute } }
            .forEach { lecture ->
                add(
                    TimelineEntry(
                        title = lecture.subject,
                        trailing = DateFormats.formatTime12(lecture.timeFrom),
                        tint = MaterialTheme.colorScheme.primary,
                        isNow = lecture.id == currentLectureId,
                        onClick = onOpenSchedule
                    )
                )
            }
        dueTasks
            .sortedWith(compareByDescending<TaskEntity> { it.isOverdue }.thenBy { it.dueDate })
            .forEach { task ->
                add(
                    TimelineEntry(
                        title = task.title,
                        trailing = if (task.isOverdue) "متأخرة"
                        else DateFormats.friendlyDueLabel(task.dueDate) ?: "مطلوبة",
                        tint = if (task.isOverdue) SemanticDanger else SemanticWarning,
                        isNow = false,
                        onClick = onOpenTasks
                    )
                )
            }
    }.take(5)

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "خط اليوم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "عرض الكل",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(8.dp))

            if (entries.isEmpty()) {
                Text(
                    text = "يومك صافٍ — لا محاضرات ولا مهام مستحقة اليوم",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            } else {
                entries.forEachIndexed { index, entry ->
                    Row {
                        // سكة زمنية رفيعة: نقطة يصلها خط بالتالية
                        Column(
                            modifier = Modifier.width(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(16.dp)
                            ) {
                                if (entry.isNow) {
                                    Box(
                                        Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(entry.tint.copy(alpha = 0.25f))
                                    )
                                    Box(
                                        Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(entry.tint)
                                    )
                                } else {
                                    Box(
                                        Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(entry.tint.copy(alpha = 0.75f))
                                    )
                                }
                            }
                            if (index < entries.lastIndex) {
                                Box(
                                    Modifier
                                        .width(2.dp)
                                        .height(24.dp)
                                        .background(
                                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                        )
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(
                                    onClickLabel = "عرض التفاصيل",
                                    role = Role.Button,
                                    onClick = entry.onClick
                                )
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            TintPill(entry.trailing, entry.tint)
                        }
                    }
                }
            }
        }
    }
}

/** شارة حبة (pill) بلون دلالي شفاف — النسخة المحلية من TintChip بأسلوب التصميم الجديد */
@Composable
private fun TintPill(text: String, tint: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .background(tint.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 2.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = tint,
        maxLines = 1
    )
}

/** بطاقة الامتحانات: عدّاد أيام بارز بجانب كل امتحان مع تمييز القريب بلون الخطر */
@Composable
private fun UpcomingExamsCard(
    exams: List<ExamEntity>,
    onOpenExams: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = "عرض الامتحانات",
                role = Role.Button,
                onClick = onOpenExams
            )
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "امتحانات قادمة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "عرض الكل",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(4.dp))
            exams.forEach { exam ->
                // سطر واحد لكل امتحان: المادة + شارة العد التنازلي — التفاصيل
                // (النوع والقاعة) مكانها تبويب الامتحانات، والرئيسية للعدّ فقط
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconDisc(
                        icon = Icons.Outlined.School,
                        tint = if ((exam.daysRemaining ?: 99) <= 3) SemanticDanger
                        else MaterialTheme.colorScheme.tertiary,
                        backgroundColor = if ((exam.daysRemaining ?: 99) <= 3)
                            SemanticDanger.copy(alpha = 0.12f)
                        else
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = exam.subject,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    TintPill(
                        text = dayCountLabel(exam.daysRemaining),
                        tint = if ((exam.daysRemaining ?: 99) <= 3) SemanticDanger
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/** قرص أيقونة دائري بلون ناعم — بديل أرقى للمربع الملون */
@Composable
private fun IconDisc(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
    }
}

/** تسمية الأيام النسبية للامتحان: اليوم / غداً / بعد يومين / بعد N أيام */
private fun dayCountLabel(days: Long?): String = when (days) {
    null -> "قريباً"
    0L -> "اليوم"
    1L -> "غداً"
    2L -> "بعد يومين"
    else -> "بعد $days أيام"
}
