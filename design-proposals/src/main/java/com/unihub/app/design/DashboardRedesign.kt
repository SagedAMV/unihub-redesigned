package com.unihub.app.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * مقترح الشاشة الرئيسية الجديدة — «مركز اليوم» بشاشة هاتف عمودية.
 *
 * التحول الجوهري عن التصميم الحالي: بدل عمود واحد طويل مزدحم (بطاقة اللقطة
 * فأربع بطاقات إحصاء فثلاث قوائم متتابعة فبلاطات إجراءات)، سرد رأسي واضح
 * الترتيب على غرار تطبيقات الجوال الحديثة: ماذا يحدث «الآن» في بطاقة بطلة
 * متدرجة تُقرأ من نظرة، ثم لمحة رقمية مدمجة (أقراص قابلة للنقر تغني عن صف
 * الإجراءات المكرر)، ثم «خط اليوم» الزمني الذي يدمج المحاضرات والمهام في
 * مسار واحد يجيب سؤال «ماذا بعد؟»، وأخيراً عدّادات الامتحانات القادمة —
 * كل ذلك ضمن طية واحدة بلا تمرير، مع الحفاظ على هوية الزمرد الهادئة.
 */
@Composable
fun DashboardRedesign(
    greeting: String = SampleData.GREETING,
    date: String = SampleData.DATE,
    nextLecture: SampleLecture = SampleData.nextLecture,
    todayLectures: List<SampleLecture> = SampleData.todayLectures,
    tasks: List<SampleTask> = SampleData.tasks,
    exams: List<SampleExam> = SampleData.exams,
    stats: List<Triple<String, String, Color>> = SampleData.stats
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderRow(greeting, date)
        NowNextHero(nextLecture, todayLectures)
        // أقراص الإحصاءات قابلة للنقر (اختصارات لوجهاتها) فتغني عن صف
        // إجراءات سريعة منفصل — أقل واجهة تحقق الغرض.
        StatsRow(stats)
        DayTimelineCard(lectures = todayLectures, tasks = tasks)
        UpcomingExamsCard(exams)
    }
}

@Composable
private fun HeaderRow(greeting: String, date: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        HeaderIconBadge(icon = Icons.Outlined.Cloud, badgeCount = 3)
        Spacer(Modifier.width(6.dp))
        HeaderIconBadge(icon = Icons.Outlined.Settings)
    }
}

/** زر أيقونة دائري بنبرة سطح ناعمة بدل زر الإطار العريض القديم */
@Composable
private fun HeaderIconBadge(icon: ImageVector, badgeCount: Int? = null) {
    Box {
        Surface(
            onClick = { },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        if (badgeCount != null) {
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(SemanticDanger)
                    .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontSize = 9.sp
                )
            }
        }
    }
}

/**
 * البطاقة البطلة «الآن والتالي»: تدرج زمردي هادئ يحمل المحاضرة الجارية/التالية
 * بحجم يقرأ من نظرة، وشارة زجاجية فاتحة في المقدمة، وتحتها محاضرتا اليوم
 * التاليتان خلف فاصل زجاجي رفيع — أقصى استفادة من عرض الهاتف الضيق.
 */
@Composable
private fun NowNextHero(next: SampleLecture, upcoming: List<SampleLecture>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(Sage, SageDeep),
                    start = Offset(x = 330f, y = 0f),
                    end = Offset(x = 0f, y = 420f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            GlassChip(
                if (next.ongoing) "جارية الآن · تنتهي بعد 25 دقيقة"
                else "تبدأ ${next.timeFrom}",
                emphasized = true
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = next.subject,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeroMeta(Icons.Outlined.Schedule, "${next.timeFrom} – ${next.timeTo}")
                HeroMeta(null, next.room)
                HeroMeta(null, next.doctor)
            }

            Spacer(Modifier.height(10.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.18f))
            )
            Spacer(Modifier.height(8.dp))

            upcoming.take(2).forEach { lecture ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lecture.timeFrom,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MintSoft
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(
                        Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = lecture.subject,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = lecture.room,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/** شارة زجاجية على البطاقة المتدرجة — أبيض شفاف فوق الزمرد */
@Composable
private fun GlassChip(text: String, emphasized: Boolean = false) {
    val container = if (emphasized) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.13f)
    Surface(shape = RoundedCornerShape(50), color = container) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Medium,
            color = Color.White,
            maxLines = 1
        )
    }
}

@Composable
private fun HeroMeta(icon: ImageVector?, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** صف الإحصاءات: أقراص مدمجة بلا حدود — أخف من بطاقات الإحصاء الأربع القديمة */
@Composable
private fun StatsRow(stats: List<Triple<String, String, Color>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stats.forEach { (value, label, tint) ->
            Surface(
                onClick = { },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.weight(1f)
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
    }
}

/**
 * «خط اليوم»: دمج محاضرات اليوم ومهامه في مسار زمني واحد بسكة رفيعة
 * ونقاط ملونة حسب النوع — إجابة على سؤال «ماذا بعد؟» الذي كانت تجيب عنه
 * ثلاث قوائم منفصلة في التصميم القديم، والمتأخر يظهر بلون الخطر فوراً.
 */
@Composable
private fun DayTimelineCard(lectures: List<SampleLecture>, tasks: List<SampleTask>) {
    data class Entry(
        val title: String,
        val trailing: String,
        val tint: Color,
        val isNow: Boolean
    )
    val entries = listOf(
        Entry(
            lectures[0].subject,
            lectures[0].timeFrom,
            MaterialTheme.colorScheme.primary,
            isNow = true
        ),
        Entry(
            tasks[1].title,
            if (tasks[1].overdue) "متأخرة" else tasks[1].dueLabel,
            SemanticDanger,
            isNow = false
        ),
        Entry(
            lectures[1].subject,
            lectures[1].timeFrom,
            SemanticInfo,
            isNow = false
        ),
        Entry(
            tasks[0].title,
            tasks[0].dueLabel,
            SemanticWarning,
            isNow = false
        )
    )

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            SectionLabel("خط اليوم")
            Spacer(Modifier.height(8.dp))
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
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Row(
                        modifier = Modifier
                            .weight(1f)
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
                        Pill(entry.trailing, entry.tint.copy(alpha = 0.12f), entry.tint)
                    }
                }
            }
        }
    }
}

/** بطاقة الامتحانات: عدّاد أيام بارز بجانب كل امتحان مع تمييز القريب بلون الخطر */
@Composable
private fun UpcomingExamsCard(exams: List<SampleExam>) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("امتحانات قادمة", Modifier.weight(1f))
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
                        tint = if (exam.daysRemaining <= 3) SemanticDanger else MaterialTheme.colorScheme.tertiary,
                        backgroundColor = if (exam.daysRemaining <= 3)
                            SemanticDanger.copy(alpha = 0.12f)
                        else
                            MaterialTheme.colorScheme.tertiaryContainer,
                        size = 30.dp,
                        iconSize = 16.dp
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
                    val danger = exam.daysRemaining <= 3
                    Pill(
                        text = "بعد ${exam.daysRemaining} أيام",
                        containerColor = if (danger) SemanticDanger.copy(alpha = 0.13f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (danger) SemanticDanger else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
