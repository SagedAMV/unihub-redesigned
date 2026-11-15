package com.unihub.app.feature.settings

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unihub.app.core.prefs.AutoBackupPreferences
import com.unihub.app.core.prefs.AutoBackupSettings
import com.unihub.app.core.prefs.ThemeMode
import com.unihub.app.ui.components.ChoiceChips
import com.unihub.app.ui.components.ConfirmDialog
import com.unihub.app.ui.components.Field
import com.unihub.app.ui.components.UiMessagesHost
import com.unihub.app.ui.theme.MintContainerDark
import com.unihub.app.ui.theme.MintSoft
import com.unihub.app.ui.theme.NightSurface
import com.unihub.app.ui.theme.NightSurfaceVariant
import com.unihub.app.ui.theme.PaperSurface
import com.unihub.app.ui.theme.PaperSurfaceVariant
import com.unihub.app.ui.theme.Sage
import com.unihub.app.ui.theme.SageContainer
import com.unihub.app.ui.theme.Sand
import com.unihub.app.ui.theme.SemanticDanger
import com.unihub.app.ui.theme.SemanticWarning
import com.unihub.app.ui.theme.Slate
import com.unihub.app.ui.theme.SlateContainer
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * شاشة الإعدادات بالتصميم الجديد — بنية «قائمة فلوحة» الهاتفية.
 *
 * التحول عن التصميم السابق: قائمة طويلة متتابعة تُقرأ كلها تمريراً بلا سياق.
 * الآن: فئات الإعدادات في قائمة مدمجة واضحة الحدود أعلى الشاشة (والضغط على
 * فئة ينزلق إلى قسمها)، وكل فئة لوحة بطاقة مستقلة تحتها. لوحة «المظهر»
 * تحول الاختيار من أزرار راديو نصية إلى بطاقات معاينة مصغّرة مرسومة تُظهر
 * شكل التطبيق في كل وضع (فاتح/داكن/تلقائي) قبل اختياره، مع مفتاح Material You
 * ولوحة ألوان الهوية.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenBackup: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    UiMessagesHost(viewModel.messenger, snackbarHostState)

    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val useDynamicColor by viewModel.useDynamicColor.collectAsStateWithLifecycle()

    var showClearConfirm by remember { mutableStateOf(false) }

    // النسخ الاحتياطي التلقائي: الإعدادات الحية + منتقي مجلد النظام (SAF)
    val autoBackup by viewModel.autoBackupSettings.collectAsStateWithLifecycle()
    val backupFolderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri -> uri?.let(viewModel::onBackupFolderSelected) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 14.dp, vertical = 10.dp
            )
        ) {
            // قائمة الفئات — الضغط على فئة ينزلق إلى لوحتها
            item(key = "categories") {
                SettingsCategoryList(
                    onCategory = { index -> scope.launch { listState.animateScrollToItem(index) } }
                )
            }

            // ── لوحة المظهر: بطاقات معاينة السمة + Material You + لوحة الهوية ──
            item(key = "appearance") {
                AppearancePanel(
                    themeMode = themeMode,
                    useDynamicColor = useDynamicColor,
                    onThemeMode = viewModel::setThemeMode,
                    onDynamicColor = viewModel::setDynamicColor
                )
            }

            // ── لوحة النسخ الاحتياطي التلقائي ──
            item(key = "backup") {
                BackupPanel(
                    autoBackup = autoBackup,
                    onPickFolder = { backupFolderLauncher.launch(null) },
                    onClearFolder = viewModel::clearBackupFolder,
                    onIntervalDays = viewModel::setIntervalDays,
                    onCustomDaysInput = viewModel::setCustomDaysInput,
                    onBackupOnChange = viewModel::setBackupOnChange,
                    onBackupNow = viewModel::backupNow,
                    onError = { viewModel.messenger.notifyError(it) }
                )
            }

            // ── لوحة البيانات ──
            item(key = "data") {
                DataPanel(
                    onOpenBackup = onOpenBackup,
                    onClearAll = { showClearConfirm = true }
                )
            }

            // ── لوحة حول التطبيق ──
            item(key = "about") {
                AboutPanel()
            }
        }

        if (showClearConfirm) {
            ConfirmDialog(
                title = "مسح جميع البيانات؟",
                message = "سيُحذف كل شيء نهائياً: المجلدات، الملفات المستوردة، المهام، الملاحظات، الامتحانات والجدول. لا يمكن التراجع.",
                confirmLabel = "مسح الكل",
                onConfirm = {
                    viewModel.clearAllData()
                    showClearConfirm = false
                },
                onDismiss = { showClearConfirm = false }
            )
        }
    }
}

private data class SettingsCategory(
    val label: String,
    val caption: String,
    val icon: ImageVector,
    val tint: Color
)

/** قائمة الفئات: صف لكل فئة بأيقونة ملونة ووصف مختصر وسهم ينزلق للوحهتها */
@Composable
private fun SettingsCategoryList(onCategory: (Int) -> Unit) {
    val categories = listOf(
        SettingsCategory("المظهر", "السمة والألوان", Icons.Filled.Palette, Sage),
        SettingsCategory("النسخ الاحتياطي", "تلقائي وسحابي", Icons.Outlined.Backup, Sand),
        SettingsCategory("البيانات", "تصدير ومسح", Icons.Outlined.Storage, SemanticWarning),
        SettingsCategory("حول التطبيق", "الإصدار 1.0.0", Icons.Filled.Info, SemanticDanger)
    )
    // فهرس كل فئة داخل LazyList: 1 = المظهر، 2 = النسخ، 3 = البيانات، 4 = حول
    val sectionIndices = listOf(1, 2, 3, 4)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        categories.forEachIndexed { index, category ->
            Surface(
                onClick = { onCategory(sectionIndices[index]) },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(category.tint.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = category.tint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = category.label,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = category.caption,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/** غلاف موحد للوحات الفئات: عنوان + بطاقة بحواف دائرية وحد رفيع */
@Composable
private fun CategoryPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                content = content
            )
        }
    }
}

/** لوحة المظهر: بطاقات المعاينة + مفتاح Material You + لوحة ألوان الهوية */
@Composable
private fun AppearancePanel(
    themeMode: ThemeMode,
    useDynamicColor: Boolean,
    onThemeMode: (ThemeMode) -> Unit,
    onDynamicColor: (Boolean) -> Unit
) {
    CategoryPanel(title = "المظهر") {
        Text(
            text = "المظهر",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "بطاقات المعاينة تعرض النتيجة قبل الاعتماد",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )

        Spacer(Modifier.height(10.dp))

        // بطاقات المعاينة الثلاث — ملصقة على عرض الهاتف
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemePreviewCard(
                label = "فاتح",
                icon = Icons.Filled.LightMode,
                selected = themeMode == ThemeMode.LIGHT,
                swatchBackground = PaperSurface,
                swatchHeader = Sage,
                swatchBar = PaperSurfaceVariant,
                swatchTile = SageContainer,
                onClick = { onThemeMode(ThemeMode.LIGHT) },
                modifier = Modifier.weight(1f)
            )
            ThemePreviewCard(
                label = "داكن",
                icon = Icons.Filled.DarkMode,
                selected = themeMode == ThemeMode.DARK,
                swatchBackground = NightSurface,
                swatchHeader = MintSoft,
                swatchBar = NightSurfaceVariant,
                swatchTile = MintContainerDark,
                onClick = { onThemeMode(ThemeMode.DARK) },
                modifier = Modifier.weight(1f)
            )
            ThemePreviewCard(
                label = "تلقائي",
                icon = Icons.Outlined.AutoAwesome,
                selected = themeMode == ThemeMode.SYSTEM,
                swatchBackground = PaperSurfaceVariant,
                swatchHeader = Slate,
                swatchBar = PaperSurface,
                swatchTile = SlateContainer,
                onClick = { onThemeMode(ThemeMode.SYSTEM) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        // صف الألوان الديناميكية
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "ألوان Material You",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            "من خلفية جهازك — أندرويد 12 أو أحدث"
                        } else {
                            "متوفرة على أندرويد 12 فما فوق فقط"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Switch(
                    checked = useDynamicColor,
                    onCheckedChange = onDynamicColor,
                    enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // لوحة ألوان الهوية — الخمسة الألوان الأساسية للتطبيق بأسمائها
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "لوحة الهوية",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple(Sage, "الزمردي", "الأساسي"),
                        Triple(Sand, "الرملي", "الثانوي"),
                        Triple(Slate, "النيلي", "الثالثي"),
                        Triple(SemanticWarning, "العسلي", "تنبيه"),
                        Triple(SemanticDanger, "الطوبي", "خطر")
                    ).forEach { (color, name, _) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

/** لوحة النسخ الاحتياطي التلقائي: المجلد + الفاصل + نسخ عند التعديل + نسخ الآن */
@Composable
private fun BackupPanel(
    autoBackup: AutoBackupSettings?,
    onPickFolder: () -> Unit,
    onClearFolder: () -> Unit,
    onIntervalDays: (Int) -> Unit,
    onCustomDaysInput: (String) -> Unit,
    onBackupOnChange: (Boolean) -> Unit,
    onBackupNow: () -> Unit,
    onError: (String) -> Unit
) {
    CategoryPanel(title = "النسخ الاحتياطي التلقائي") {
        // مجلد النسخ: اختيار عبر النظام + تغذية راجعة حقيقية عن الوصول
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "مجلد النسخ التلقائي",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (autoBackup?.isFolderConfigured == true) {
                        "تم تحديد مجلد نسخ الاحتياطية ✓"
                    } else {
                        "لم يتم تحديد مجلد نسخ الاحتياطية"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (autoBackup?.isFolderConfigured == true) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            if (autoBackup?.isFolderConfigured == true) {
                TextButton(onClick = onClearFolder) { Text("إلغاء") }
            }
            FilledTonalButton(onClick = onPickFolder) {
                Text(if (autoBackup?.isFolderConfigured == true) "تغيير" else "اختيار مجلد")
            }
        }

        // فاصل النسخ: يومي / 3 أيام / أسبوعي / شهري / يوم مخصص
        val intervalDays = autoBackup?.intervalDays
            ?: AutoBackupPreferences.DEFAULT_INTERVAL_DAYS
        val chipIndex = when (intervalDays) {
            1 -> 0
            3 -> 1
            7 -> 2
            30 -> 3
            else -> 4
        }
        Text(
            "كل كم يُعمل النسخ التلقائي؟",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(Modifier.height(8.dp))
        ChoiceChips(
            labels = listOf("يومي", "كل 3 أيام", "أسبوعي", "شهري", "مخصص"),
            selectedIndex = chipIndex,
            onSelect = { index ->
                when (index) {
                    0 -> onIntervalDays(1)
                    1 -> onIntervalDays(3)
                    2 -> onIntervalDays(7)
                    3 -> onIntervalDays(30)
                    else -> {
                        // «مخصص»: يطبق القيمة الحالية في خانة اليوم المخصص
                        val custom = autoBackup?.customDaysInput?.toIntOrNull()
                        if (custom != null && custom in AutoBackupPreferences.MIN_INTERVAL_DAYS..AutoBackupPreferences.MAX_INTERVAL_DAYS) {
                            onIntervalDays(custom)
                        } else {
                            onError("اكتب عدد الأيام في الخانة أولاً (1-365)")
                        }
                    }
                }
            }
        )

        // خانة اليوم المخصص — تظهر عند اختيار «مخصص» أو وجود قيمة مخصصة
        if (chipIndex == 4) {
            Spacer(Modifier.height(8.dp))
            Field(
                label = "عدد الأيام المخصص (1-365)",
                value = autoBackup?.customDaysInput.orEmpty(),
                onValueChange = onCustomDaysInput,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        // نسخة بعد كل عملية تعديل (تجربة أن النسخ يعمل)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("نسخة بعد كل عملية تعديل", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "للتجربة: تُحدَّث نسخة «آخر حفظ» بعد أي تعديل على بياناتك",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = autoBackup?.backupOnChange ?: false,
                onCheckedChange = onBackupOnChange
            )
        }

        // نسخ الآن + آخر نتيجة
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = onBackupNow,
            enabled = autoBackup?.isFolderConfigured == true
        ) {
            Text("نسخ احتياطي الآن")
        }
        val lastAt = autoBackup?.lastBackupAt ?: 0L
        if (lastAt > 0L) {
            // عرض طابع آخر محاولة نسخ بالنظام 12-ساعي (ص/م) المعتمد في التطبيق كله
            val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd h:mm a", Locale.getDefault()) }
            Text(
                text = "آخر محاولة: ${dateFormat.format(Date(lastAt))}" +
                    autoBackup?.lastBackupMessage?.takeIf { it.isNotBlank() }?.let { " — $it" }.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

/** لوحة البيانات: النسخ الاحتياطي والاستعادة + مسح جميع البيانات */
@Composable
private fun DataPanel(
    onOpenBackup: () -> Unit,
    onClearAll: () -> Unit
) {
    CategoryPanel(title = "البيانات") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenBackup)
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Backup,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("النسخ الاحتياطي والاستعادة", style = MaterialTheme.typography.bodyLarge)
                Text(
                    // النسخة أرشيف ZIP يتضمن محتوى الملفات الفعلي
                    "تصدير بياناتك إلى أرشيف نسخة احتياطية أو استعادتها",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClearAll)
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "مسح جميع البيانات",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    "يحذف كل المجلدات والملفات والمهام والملاحظات نهائياً",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** لوحة حول التطبيق: هوية التطبيق وإصداره */
@Composable
private fun AboutPanel() {
    CategoryPanel(title = "حول التطبيق") {
        Text(
            text = "UniHub — رفيقك الجامعي لإدارة الملفات والمحاضرات والمهام والملاحظات والامتحانات. " +
                "يعمل بالكامل دون إنترنت وتبقى بياناتك على جهازك.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "الإصدار 1.0.0",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * بطاقة معاينة السمة: لوحة مصغّرة مرسومة برأس ملون وشريط وبلاطة تحاكي شكل
 * التطبيق داخل الوضع المقابل، وعلامة صح على البطاقة المحددة.
 */
@Composable
private fun ThemePreviewCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    swatchBackground: Color,
    swatchHeader: Color,
    swatchBar: Color,
    swatchTile: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // لوحة المعاينة المصغرة
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(swatchBackground)
                    .padding(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(swatchHeader)
                )
                Spacer(Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(9.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(swatchBar)
                    )
                    Box(
                        Modifier
                            .weight(1f)
                            .height(9.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(swatchTile)
                    )
                    Box(
                        Modifier
                            .weight(1f)
                            .height(9.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(swatchBar)
                    )
                }
                Spacer(Modifier.height(5.dp))
                Box(
                    Modifier
                        .width(38.dp)
                        .height(9.dp)
                        .clip(RoundedCornerShape(50))
                        .background(swatchTile)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "محدد",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
                Spacer(Modifier.width(5.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(Modifier.width(2.dp))
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
