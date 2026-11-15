package com.unihub.app.feature.planner

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unihub.app.feature.planner.exams.ExamsTab
import com.unihub.app.feature.planner.notes.NotesTab
import com.unihub.app.feature.planner.schedule.ScheduleTab
import com.unihub.app.feature.planner.tasks.TasksTab
import com.unihub.app.ui.navigation.PlannerTab
import kotlinx.coroutines.launch

/**
 * شاشة المخطط: تبويبات المهام/الجدول/الملاحظات/الامتحانات.
 * كل تبويب له ViewModel مستقل — على عكس الشاشة "الموحّدة" في التطبيق المرجعي
 * التي كانت تعيد تركيب كل شيء معاً في ملف واحد من 1100 سطر.
 *
 * التصميم الجديد: شريط التبويبات العلوي الضخم صار رقائق أقسام أفقية قابلة
 * للتمرير (بديل هاتفي رفيع يستهلك سطراً واحداً بدل شريط كامل الأبعاد)،
 * والقسم النشط يمتلئ بلون الحاوية الأساسي ويُبرز بأيقونته الممتلئة.
 * التنقل بطريقتين معاً: الضغط على الرقاقة أو السحب أفقياً بين الصفحات
 * (HorizontalPager) — والمؤشر يتزامن مع الصفحة بالاتجاهين.
 */
@Composable
fun PlannerScreen(initialTab: PlannerTab) {
    val tabs = PlannerTab.entries
    var selectedIndex by rememberSaveable { mutableIntStateOf(initialTab.ordinal) }

    // حالة الصفحات: rememberPagerState تحفظ الصفحة الحالية عبر rememberSaveable
    // داخلياً، فتصمد أمام تدوير الشاشة وموت العملية.
    val pagerState = rememberPagerState(
        initialPage = initialTab.ordinal,
        pageCount = { tabs.size }
    )
    val scope = rememberCoroutineScope()

    // صفحة ← مؤشر: عند استقرار السحب على صفحة جديدة نتبنّاها في المؤشر
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (selectedIndex != page) selectedIndex = page
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PlannerSectionTabs(
            sections = tabs.map { it.toSection() },
            selectedIndex = selectedIndex,
            onSelect = { index ->
                // رقاقة ← صفحة: الضغط يمرّر الصفحة بأنيميشن انزلاقي
                selectedIndex = index
                scope.launch { pagerState.animateScrollToPage(index) }
            }
        )

        // السحب الأفقي بين الأقسام — يحترم اتجاه التخطيط (RTL) تلقائياً
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (tabs[page]) {
                PlannerTab.TASKS -> TasksTab()
                PlannerTab.SCHEDULE -> ScheduleTab()
                PlannerTab.NOTES -> NotesTab()
                PlannerTab.EXAMS -> ExamsTab()
            }
        }
    }
}

/** تعريف قسم المخطط: تسميته + أيقونته المفرغة والممتلئة */
private data class PlannerSection(
    val label: String,
    val icon: ImageVector,
    val activeIcon: ImageVector
)

private fun PlannerTab.toSection(): PlannerSection = when (this) {
    PlannerTab.TASKS -> PlannerSection("المهام", Icons.Outlined.Checklist, Icons.Filled.CheckCircle)
    PlannerTab.SCHEDULE -> PlannerSection("الجدول", Icons.Outlined.CalendarMonth, Icons.Outlined.CalendarMonth)
    PlannerTab.NOTES -> PlannerSection("الملاحظات", Icons.Outlined.FormatListBulleted, Icons.Outlined.FormatListBulleted)
    PlannerTab.EXAMS -> PlannerSection("الامتحانات", Icons.Outlined.EventAvailable, Icons.Outlined.EventAvailable)
}

/**
 * رقائق الأقسام الأفقية: شريط واحد رفيع يبدأ من اليمين (RTL) ويمتد بتمرير
 * أفقي إن ضاقت الشاشة — بديل هاتفي لشريط التبويبات العلوي الضخم.
 */
@Composable
private fun PlannerSectionTabs(
    sections: List<PlannerSection>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
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
                    onClick = { onSelect(index) },
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
