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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * مقترح شاشة الملفات الجديدة بشاشة هاتف عمودية.
 *
 * التحول الجوهري: التصميم الحالي يعرض شبكة مجلدات ثم قائمة ملفات في عمود
 * واحد طويل متتابع، وأزرار الإجراءات تسبح في الزوايا، ولا مؤشر للتخزين.
 * المقترح يرتب المحتوى بمنطق «ابحث ثم تصفّح ثم التقط»: حقل بحث حبة في
 * المقدمة، ثم مقياس مساحة السحابة بارزاً حيث يراه الطالب، ثم المجلدات
 * الملونة في شبكة 2×2 تختصر الارتفاع، وأخيراً قائمة «الأحدث» بقرص نوع
 * ملوّن يُعرّف الملف قبل قراءة اسمه.
 */
@Composable
fun FilesRedesign(
    folders: List<SampleFolder> = SampleData.folders,
    files: List<SampleFile> = SampleData.recentFiles,
    storageLabel: String = SampleData.STORAGE_USED_LABEL,
    storageFraction: Float = SampleData.STORAGE_FRACTION
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SearchField()
        StorageCard(storageLabel, storageFraction)
        FoldersSection(folders)
        RecentFilesHeader(files.size)
        files.forEach { file -> FileRowNew(file) }
    }
}

/** حقل بحث حبة في مقدمة الشاشة — البحث أولاً في مدير ملفات حديث */
@Composable
private fun SearchField() {
    OutlinedTextField(
        value = "",
        onValueChange = { },
        placeholder = {
            Text(
                "ابحث في الملفات…",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}

/** مقياس مساحة السحابة: قرص سحابة + النسبة بارزة + شريط تقدم — بطاقة واحدة مضغوطة */
@Composable
private fun StorageCard(storageLabel: String, storageFraction: Float) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconDisc(
                    icon = Icons.Outlined.Cloud,
                    tint = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    size = 36.dp,
                    iconSize = 18.dp
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "مساحة السحابة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = storageLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Text(
                    text = "${(storageFraction * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { storageFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

/** قسم المجلدات: شبكة 2×2 ببطاقات ملونة — كل المجلدات بلا تمرير */
@Composable
private fun FoldersSection(folders: List<SampleFolder>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionLabel("المجلدات", Modifier.weight(1f))
            Text(
                text = "${folders.size} مجلدات",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(6.dp))
        folders.chunked(2).forEach { rowFolders ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowFolders.forEach { folder -> FolderCard(folder, Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun FolderCard(folder: SampleFolder, modifier: Modifier = Modifier) {
    Surface(
        onClick = { },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Folder,
                contentDescription = null,
                tint = folder.tint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${folder.count} ملفاً",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** ترويسة قائمة الأحدث: العنوان + العدّاد + مفاتيح تبديل العرض */
@Composable
private fun RecentFilesHeader(fileCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "الأحدث",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.width(8.dp))
        Pill(
            text = "$fileCount ملفاً",
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { }, modifier = Modifier.size(30.dp)) {
            Icon(
                Icons.Filled.ViewAgenda,
                contentDescription = "عرض قائمة",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        IconButton(onClick = { }, modifier = Modifier.size(30.dp)) {
            Icon(
                Icons.Filled.GridView,
                contentDescription = "عرض شبكة",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun fileIcon(kind: SampleFileKind): ImageVector = when (kind) {
    SampleFileKind.PDF -> Icons.Filled.PictureAsPdf
    SampleFileKind.DOC -> Icons.AutoMirrored.Filled.Article
    SampleFileKind.IMAGE -> Icons.Filled.Image
    SampleFileKind.AUDIO -> Icons.Filled.Audiotrack
    SampleFileKind.SLIDES -> Icons.Filled.Slideshow
}

/** صف ملف جديد: قرص نوع ملوّن يختصر نوع الملف، ووسم مفضل نجمي واضح */
@Composable
private fun FileRowNew(file: SampleFile) {
    Surface(
        onClick = { },
        shape = RoundedCornerShape(15.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // قرص نوع الملف
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .padding(0.dp)
                    .background(file.kind.tint.copy(alpha = 0.13f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = fileIcon(file.kind),
                    contentDescription = file.kind.label,
                    tint = file.kind.tint,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = file.meta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            if (file.favorite) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "مفضل",
                    tint = SemanticWarning,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(Modifier.width(8.dp))
            }

            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "خيارات",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}
