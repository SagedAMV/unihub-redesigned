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
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * مقترح شاشة الإعدادات الجديدة بشاشة هاتف عمودية.
 *
 * التحول الجوهري: الإعدادات الحالية قائمة طويلة متتابعة (المظهر فالنسخ
 * الاحتياطي فإدارة المستخدمين فالتخزين…) تُقرأ كلها تمريراً بلا سياق.
 * المقترح يتبنى بنية «قائمة فلوحة» الهاتفية: فئات الإعدادات في قائمة
 * مدمجة واضحة الحدود، ولوحة تفصيل الفئة النشطة («المظهر») تفتح تحتها
 * مباشرة — فالعين ترى الفئات ونتيجة اختيارها في طية واحدة. لوحة المظهر
 * تحول الاختيار من أزرار راديو نصية إلى بطاقات معاينة مصغّرة مرسومة تُظهر
 * شكل التطبيق في كل وضع قبل اختياره.
 */
@Composable
fun SettingsRedesign() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "الإعدادات",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(10.dp))
        SettingsCategoryList()
        Spacer(Modifier.height(10.dp))
        AppearanceDetailPanel()
    }
}

private data class SettingsCategory(
    val label: String,
    val caption: String,
    val icon: ImageVector,
    val tint: Color
)

/** قائمة الفئات: صف لكل فئة بأيقونة ملونة ووصف مختصر والفئة النشطة مميزة */
@Composable
private fun SettingsCategoryList() {
    val categories = listOf(
        SettingsCategory("المظهر", "السمة والألوان", Icons.Filled.Palette, Sage),
        SettingsCategory("النسخ الاحتياطي", "تلقائي وسحابي", Icons.Filled.Backup, Slate),
        SettingsCategory("إدارة المستخدمين", "صلاحيات السحابة", Icons.Filled.AdminPanelSettings, Sand),
        SettingsCategory("التخزين", "الملفات المحلية", Icons.Filled.Storage, SemanticInfo),
        SettingsCategory("حول التطبيق", "الإصدار 1.3.1", Icons.Filled.Info, SemanticSuccess)
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        categories.forEachIndexed { index, category ->
            val selected = index == 0
            Surface(
                onClick = { },
                shape = RoundedCornerShape(16.dp),
                color = if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
                border = if (selected) null else androidx.compose.foundation.BorderStroke(
                    1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                ),
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
                            tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else category.tint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = category.label,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = category.caption,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    if (selected) {
                        Icon(
                            Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/** لوحة تفصيل «المظهر»: بطاقات معاينة السمة + مفتاح Material You + اللوحة */
@Composable
private fun AppearanceDetailPanel() {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
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
                    selected = true,
                    swatchBackground = PaperSurface,
                    swatchHeader = Sage,
                    swatchBar = PaperSurfaceVariant,
                    swatchTile = SageContainer,
                    modifier = Modifier.weight(1f)
                )
                ThemePreviewCard(
                    label = "داكن",
                    icon = Icons.Filled.DarkMode,
                    selected = false,
                    swatchBackground = NightSurface,
                    swatchHeader = MintSoft,
                    swatchBar = NightSurfaceVariant,
                    swatchTile = MintContainerDark,
                    modifier = Modifier.weight(1f)
                )
                ThemePreviewCard(
                    label = "تلقائي",
                    icon = Icons.Outlined.AutoAwesome,
                    selected = false,
                    swatchBackground = PaperSurfaceVariant,
                    swatchHeader = Slate,
                    swatchBar = PaperSurface,
                    swatchTile = SlateContainer,
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
                            text = "من خلفية جهازك — أندرويد 12 أو أحدث",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Switch(
                        checked = false,
                        onCheckedChange = { },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // لوحة الألوان
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
                            Triple(Sand, "الطيني", "الثانوي"),
                            Triple(Slate, "النيلي", "الثالثي"),
                            Triple(SemanticWarning, "العسلي", "تنبيه"),
                            Triple(SemanticDanger, "الطوبي", "خطر")
                        ).forEach { (color, name, role) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.surface,
                                            CircleShape
                                        )
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
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { },
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
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
