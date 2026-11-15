package com.unihub.app.design

import androidx.compose.foundation.background
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * مقترح شاشة المخطط الجديدة (تبويب المهام) بشاشة هاتف عمودية.
 *
 * التحول الجوهري: التصميم الحالي يستهلك الثلث الأعلى من الشاشة قبل أول
 * مهمة (بطاقة افتتاحية كبيرة + حقل بحث كامل العرض + رقائق مرشحات + شريط
 * تبويبات ضخم)، والزر العائم يغطي آخر الصفوف. المقترح يرتب العناصر بمنطق
 * تناقص الأهمية: تبويبات أقسام المخطط الأربعة في سطر رقائق واحد رفيع، ثم
 * سطر واحد يجمع العنوان والعدّاد وزر الإضافة والبحث، ثم رقائق مرشحات
 * الحالة أفقية — فتبدأ المهام أعلى الشاشة مباشرة، وزر الإضافة في الترويسة
 * بدل زر عائم يغطي القائمة.
 */
@Composable
fun TasksRedesign(
    tasks: List<SampleTask> = SampleData.tasks,
    selectedFilter: Int = 0
) {
    val pending = tasks.filter { !it.done }
    val done = tasks.filter { it.done }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // تبويبات أقسام المخطط: رقائق قابلة للتمرير — بديل هاتفي لشريط التبويبات الضخم
        PlannerSectionTabs(selectedIndex = 0)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            TasksHeaderRow(pending.size, done.size)
            Spacer(Modifier.height(8.dp))

            WeekProgressCard(doneCount = done.size, totalCount = tasks.size)

            Spacer(Modifier.height(8.dp))
            StatusFilterChips()

            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                pending.forEach { task -> TaskRowNew(task) }
                if (done.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionLabel("منجزة", Modifier.weight(1f))
                        Text(
                            text = "إخفاء",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                done.forEach { task -> TaskRowNew(task) }
            }
        }
    }
}

/**
 * بطاقة تقدم الأسبوع: شريط إنجاز + خلاصة مكتوبة — بديل هاتفي مدمج لبطاقة
 * «لوحة الأولويات» الافتتاحية الضخمة في التصميم الحالي، يختصر الحالة في سطرين.
 */
@Composable
private fun WeekProgressCard(doneCount: Int, totalCount: Int) {
    val pendingCount = totalCount - doneCount
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "تقدم هذا الأسبوع",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$doneCount من $totalCount",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            androidx.compose.material3.LinearProgressIndicator(
                progress = { if (totalCount == 0) 0f else doneCount.toFloat() / totalCount },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "أنجزت $doneCount مهام وبقي $pendingCount — واصِ التقدم",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

/** تبويبات أقسام المخطط الأربعة: رقائق أفقية بحد فاصل سليم — تبدأ من اليمين (RTL) */
@Composable
private fun PlannerSectionTabs(selectedIndex: Int) {
    val sections = listOf(
        TabSection("المهام", Icons.Outlined.Checklist, Icons.Filled.CheckCircle),
        TabSection("الجدول", Icons.Outlined.CalendarMonth, Icons.Outlined.CalendarMonth),
        TabSection("الملاحظات", Icons.Outlined.FormatListBulleted, Icons.Outlined.FormatListBulleted),
        TabSection("الامتحانات", Icons.Outlined.EventAvailable, Icons.Outlined.EventAvailable)
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sections.forEachIndexed { index, section ->
                val selected = index == selectedIndex
                Surface(
                    onClick = { },
                    shape = RoundedCornerShape(50),
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (selected) section.activeIcon else section.icon,
                            contentDescription = section.label,
                            tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = section.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private data class TabSection(
    val label: String,
    val icon: ImageVector,
    val activeIcon: ImageVector
)

/** سطر العنوان: العنوان والعدّاد + بحث دائري + زر إضافة ممتلئ — كله سطر واحد */
@Composable
private fun TasksHeaderRow(taskCount: Int, doneCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "المهام",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "$taskCount مهام · منها مهمة متأخرة",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // زر بحث دائري مدمج
        Surface(
            onClick = { },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "بحث",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // زر الإضافة في الترويسة — بديل الزر العائم الذي يغطي القوائم
        Surface(
            onClick = { },
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(36.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    Icons.Filled.AddTask,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = "مهمة",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

/** رقائق مرشحات الحالة: تمرير أفقي — شريط واحد رفيع بدل رقائق ملتفة على سطرين */
@Composable
private fun StatusFilterChips() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf("الكل" to 6, "قيد التنفيذ" to 4, "منجزة" to 2, "متأخرة" to 1).forEach { (label, count) ->
            val selected = label == "الكل"
            Surface(
                onClick = { },
                shape = RoundedCornerShape(50),
                color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.surface,
                border = if (selected) null
                else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * صف المهمة الجديد: خانة اختيار دائرية مرسومة (حلقة تتحول لقرص ممتلئ بعلامة
 * صح)، وشريط أولوية رفيع على حافة الصف، ووسم استحقاق واضح الحالة اللونية.
 */
@Composable
private fun TaskRowNew(task: SampleTask) {
    val priorityTint = when (task.priority) {
        SamplePriority.HIGH -> SemanticDanger
        SamplePriority.MEDIUM -> SemanticWarning
        SamplePriority.LOW -> SemanticSuccess
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.overdue) SemanticDanger.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // خانة إنجاز دائرية مخصصة
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.done) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.done) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "منجزة",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.done) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.done) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Flag,
                        contentDescription = null,
                        tint = priorityTint,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = task.priority.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = priorityTint,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Pill(
                    text = if (task.overdue) "متأخرة" else task.dueLabel,
                    containerColor = if (task.overdue) SemanticDanger.copy(alpha = 0.13f)
                    else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (task.overdue) SemanticDanger
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
