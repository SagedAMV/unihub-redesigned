package com.unihub.app.feature.planner.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unihub.app.core.common.DateFormats
import com.unihub.app.data.local.entity.TaskEntity
import com.unihub.app.data.local.entity.TaskPriority
import com.unihub.app.ui.components.AppSheet
import com.unihub.app.ui.components.ChoiceChips
import com.unihub.app.ui.components.ConfirmDialog
import com.unihub.app.ui.components.DateField
import com.unihub.app.ui.components.EmptyState
import com.unihub.app.ui.components.Field
import com.unihub.app.ui.components.UiMessagesHost
import com.unihub.app.ui.theme.SemanticDanger
import com.unihub.app.ui.theme.SemanticSuccess
import com.unihub.app.ui.theme.SemanticWarning

/**
 * تبويب المهام بالتصميم الجديد (تصميم الهاتف العمودي المعتمد).
 *
 * التحول عن التصميم السابق: كان يستهلك الثلث الأعلى قبل أول مهمة (حقل بحث
 * كامل العرض + رقائق مرشحات + زر عائم يغطي آخر الصفوف). الآن: سطر ترويسة
 * واحد يجمع العنوان والعدّاد وزر بحث دائري وزر «مهمة» ممتلئاً (بديل الزر
 * العائم)، وبطاقة «تقدم هذا الأسبوع» بشريط إنجاز مدمج، ورقائق مرشحات
 * بعدّادات، وبطاقات مهام بخانة إنجاز دائرية مرسومة وشريط أولوية ووسم
 * استحقاق ملون بالحالة — فتبدأ المهام أعلى الشاشة مباشرة.
 */
@Composable
fun TasksTab(viewModel: TasksViewModel = hiltViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    UiMessagesHost(viewModel.messenger, snackbarHostState)

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val filter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val stats by viewModel.taskStats.collectAsStateWithLifecycle()
    val overdueCount by viewModel.overdueCount.collectAsStateWithLifecycle()
    val (totalCount, pendingCount, doneCount) = stats

    var sheetTask by remember { mutableStateOf<TaskEntity?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<TaskEntity?>(null) }
    var searchActive by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // سطر الترويسة: العنوان والعدّاد + بحث دائري + زر إضافة ممتلئ
            TasksHeaderRow(
                totalCount = totalCount,
                overdueCount = overdueCount,
                searchActive = searchActive,
                onToggleSearch = {
                    searchActive = !searchActive
                    if (!searchActive) viewModel.setSearchQuery("")
                },
                onAdd = {
                    sheetTask = null
                    showSheet = true
                }
            )

            // حقل البحث — يظهر فقط عند تفعيل زر البحث في الترويسة
            AnimatedVisibility(
                visible = searchActive,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ابحث في المهام…") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }

            // بطاقة تقدم الأسبوع — تختفي عند فراغ قائمة المهام كلية
            if (totalCount > 0) {
                Spacer(Modifier.height(8.dp))
                WeekProgressCard(doneCount = doneCount, totalCount = totalCount)
            }

            Spacer(Modifier.height(8.dp))

            StatusFilterChips(
                selectedFilter = filter,
                totalCount = totalCount,
                pendingCount = pendingCount,
                doneCount = doneCount,
                onSelect = { viewModel.setFilter(it) }
            )

            Spacer(Modifier.height(8.dp))

            if (tasks.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.CheckCircle,
                    // الفراغات وحدها لا تُعد بحثاً فعالاً — نفس دلالة القص في مرشح ViewModel
                    title = if (searchQuery.trim().isNotBlank()) "لا نتائج للبحث"
                    else when (filter) {
                        TaskFilter.PENDING -> "لا مهام قيد التنفيذ"
                        TaskFilter.DONE -> "لا مهام منجزة بعد"
                        TaskFilter.ALL -> "لا مهام بعد"
                    },
                    subtitle = if (searchQuery.trim().isNotBlank()) {
                        "جرّب كلمات بحث أخرى أو غيّر المرشح"
                    } else {
                        "أضف مهمة من زر «مهمة» وحدد أولويتها وموعد استحقاقها"
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                // عند عرض «الكل»: المنجزة تنتقل لقسم منفصل أسفل القائمة
                val pending = tasks.filterNot { it.isDone }
                val done = tasks.filter { it.isDone }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(pending, key = { it.id }) { task ->
                        TaskRowNew(
                            task = task,
                            onToggle = { viewModel.toggle(task) },
                            onEdit = {
                                sheetTask = task
                                showSheet = true
                            },
                            onLongPress = { deleteTarget = task }
                        )
                    }
                    if (done.isNotEmpty()) {
                        item(key = "done_section") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "منجزة",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "إخفاء",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        items(done, key = { it.id }) { task ->
                            TaskRowNew(
                                task = task,
                                onToggle = { viewModel.toggle(task) },
                                onEdit = {
                                    sheetTask = task
                                    showSheet = true
                                },
                                onLongPress = { deleteTarget = task }
                            )
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }

        if (showSheet) {
            TaskSheet(
                editing = sheetTask,
                onDismiss = { showSheet = false },
                onSave = { title, description, priority, dueDate ->
                    viewModel.save(sheetTask, title, description, priority, dueDate)
                    showSheet = false
                },
                onDelete = sheetTask?.let { existing ->
                    {
                        deleteTarget = existing
                        showSheet = false
                    }
                }
            )
        }

        deleteTarget?.let { task ->
            ConfirmDialog(
                title = "حذف المهمة؟",
                message = "ستُحذف \"${task.title}\" مع تذكيرها المجدول.",
                onConfirm = {
                    viewModel.delete(task)
                    deleteTarget = null
                },
                onDismiss = { deleteTarget = null }
            )
        }
    }
}

/** سطر الترويسة: العنوان والعدّاد يميناً + بحث دائري وزر «مهمة» ممتلئ يساراً */
@Composable
private fun TasksHeaderRow(
    totalCount: Int,
    overdueCount: Int,
    searchActive: Boolean,
    onToggleSearch: () -> Unit,
    onAdd: () -> Unit
) {
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
                text = if (overdueCount > 0) {
                    "$totalCount مهام · منها $overdueCount متأخرة"
                } else {
                    "$totalCount مهام · كل شيء منتظم"
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // زر بحث دائري مدمج — يفعل حقل البحث المتوسع تحته
        Surface(
            onClick = onToggleSearch,
            shape = CircleShape,
            color = if (searchActive) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface,
            border = if (searchActive) null
            else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "بحث",
                    tint = if (searchActive) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // زر الإضافة في الترويسة — بديل الزر العائم الذي يغطي آخر الصفوف
        Surface(
            onClick = onAdd,
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

/**
 * بطاقة تقدم الأسبوع: شريط إنجاز + خلاصة مكتوبة — بطاقة مدمجة تحل محل
 * بطاقة الأولويات الافتتاحية الضخمة، تختصر الحالة في سطرين.
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
                    text = "تقدمك",
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
            LinearProgressIndicator(
                progress = { if (totalCount == 0) 0f else doneCount.toFloat() / totalCount },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
                strokeCap = StrokeCap.Round
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (doneCount == 0) {
                    "لم تُنجز أي مهمة بعد — ابدأ بأول واحدة"
                } else if (pendingCount == 0) {
                    "أنجزت كل مهامك — عمل رائع!"
                } else {
                    "أنجزت $doneCount مهام وبقي $pendingCount — واصِ التقدم"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

/** رقائق مرشحات الحالة بعدّاداتها — شريط أفقي رفيع قابل للتمرير */
@Composable
private fun StatusFilterChips(
    selectedFilter: TaskFilter,
    totalCount: Int,
    pendingCount: Int,
    doneCount: Int,
    onSelect: (TaskFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf(
            TaskFilter.ALL to "الكل" to totalCount,
            TaskFilter.PENDING to "قيد التنفيذ" to pendingCount,
            TaskFilter.DONE to "منجزة" to doneCount
        ).forEach { (filterAndLabel, count) ->
            val (filterValue, label) = filterAndLabel
            val selected = filterValue == selectedFilter
            Surface(
                onClick = { onSelect(filterValue) },
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
 * بطاقة المهمة الجديدة: خانة إنجاز دائرية مرسومة (حلقة تتحول لقرص ممتلئ بعلامة
 * صح)، عنوان ووصف بقلص سطر واحد، وشريط أولوية مع وسم استحقاق واضح الحالة
 * اللونية (متأخرة = أحمر). نقرة = تعديل، نقرة مطولة = حذف.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskRowNew(
    task: TaskEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onLongPress: () -> Unit
) {
    val priorityTint = when (task.priority) {
        TaskPriority.HIGH -> SemanticDanger
        TaskPriority.MEDIUM -> SemanticWarning
        TaskPriority.LOW -> SemanticSuccess
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.isOverdue) SemanticDanger.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onEdit, onLongClick = onLongPress)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // خانة إنجاز دائرية مخصصة — أضغط عليها يقلب حالة المهمة
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isDone) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                    .combinedClickable(
                        onClick = onToggle,
                        onClickLabel = if (task.isDone) "إرجاع المهمة" else "إنجاز المهمة"
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isDone) {
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
                    color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
                val dueLabel = DateFormats.friendlyDueLabel(task.dueDate)
                if (dueLabel != null || task.isOverdue) {
                    DuePill(
                        text = if (task.isOverdue) "متأخرة" else dueLabel ?: "مطلوبة",
                        overdue = task.isOverdue
                    )
                }
            }
        }
    }
}

/** وسم الاستحقاق — أحمر دلالي عند التأخير، محايد هادئ وإلا */
@Composable
private fun DuePill(text: String, overdue: Boolean) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                if (overdue) SemanticDanger.copy(alpha = 0.13f)
                else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(50)
            )
            .padding(horizontal = 9.dp, vertical = 2.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = if (overdue) SemanticDanger
        else MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1
    )
}

@Composable
private fun TaskSheet(
    editing: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, priority: TaskPriority, dueDate: String?) -> Unit,
    onDelete: (() -> Unit)?
) {
    var title by remember { mutableStateOf(editing?.title ?: "") }
    var description by remember { mutableStateOf(editing?.description ?: "") }
    var priority by remember { mutableStateOf(editing?.priority ?: TaskPriority.MEDIUM) }
    var dueDate by remember { mutableStateOf(editing?.dueDate) }
    var titleError by remember { mutableStateOf<String?>(null) }

    AppSheet(
        title = if (editing == null) "مهمة جديدة" else "تعديل المهمة",
        onDismiss = onDismiss,
        actions = {
            if (onDelete != null) {
                TextButton(onClick = onDelete) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            }
            TextButton(onClick = onDismiss) { Text("إلغاء") }
            FilledTonalButton(onClick = {
                if (title.isBlank()) {
                    titleError = "عنوان المهمة مطلوب"
                } else {
                    onSave(title, description, priority, dueDate)
                }
            }) { Text("حفظ") }
        }
    ) {
        Field(
            label = "عنوان المهمة",
            value = title,
            onValueChange = {
                title = it
                titleError = null
            },
            errorText = titleError
        )
        Field(
            label = "وصف (اختياري)",
            value = description,
            onValueChange = { description = it },
            singleLine = false
        )
        Text("الأولوية", style = MaterialTheme.typography.labelLarge)
        ChoiceChips(
            labels = TaskPriority.entries.map { it.label },
            selectedIndex = TaskPriority.entries.indexOf(priority),
            onSelect = { priority = TaskPriority.entries[it] }
        )
        DateField(
            label = "موعد الاستحقاق (اختياري)",
            valueIso = dueDate,
            onPick = { dueDate = it },
            onClear = { dueDate = null }
        )
    }
}
