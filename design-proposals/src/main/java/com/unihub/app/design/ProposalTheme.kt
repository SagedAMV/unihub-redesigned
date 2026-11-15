package com.unihub.app.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * هوية UniHub البصرية نفسها (من app/ui/theme) منسوخة إلى الوحدة لتعمل
 * مقترحات إعادة التصميم بنفس الألوان والخطوط والأشكال — فتُقيَّم التصاميم
 * الجديدة بمعزل عن بنية الشاشات القديمة لا بمعزل عن هوية التطبيق.
 */
private val LightColors = lightColorScheme(
    primary = Sage,
    onPrimary = SageOn,
    primaryContainer = SageContainer,
    onPrimaryContainer = SageOnContainer,
    secondary = Sand,
    onSecondary = Color.White,
    secondaryContainer = SandContainer,
    onSecondaryContainer = SandOnContainer,
    tertiary = Slate,
    onTertiary = Color.White,
    tertiaryContainer = SlateContainer,
    onTertiaryContainer = SlateOnContainer,
    error = ClayError,
    onError = Color.White,
    errorContainer = ClayErrorContainer,
    onErrorContainer = ClayOnErrorContainer,
    background = PaperBackground,
    onBackground = InkOnPaper,
    surface = PaperSurface,
    onSurface = InkOnPaper,
    surfaceVariant = PaperSurfaceVariant,
    onSurfaceVariant = InkMuted,
    outline = PaperOutline,
    outlineVariant = Color(0xFFD8DCD3),
    inverseSurface = Color(0xFF333B37),
    inverseOnSurface = Color(0xFFECF0EC),
    inversePrimary = MintSoft
)

private val DarkColors = darkColorScheme(
    primary = MintSoft,
    onPrimary = MintOnSoft,
    primaryContainer = MintContainerDark,
    onPrimaryContainer = SageContainer,
    secondary = SandSoft,
    onSecondary = SandOnSoft,
    secondaryContainer = SandContainerDark,
    onSecondaryContainer = SandContainer,
    tertiary = SlateSoft,
    onTertiary = SlateOnSoft,
    tertiaryContainer = SlateContainerDark,
    onTertiaryContainer = SlateContainer,
    error = ClayErrorDark,
    onError = ClayOnErrorContainer,
    errorContainer = ClayErrorContainerDark,
    onErrorContainer = ClayOnErrorContainer,
    background = NightBackground,
    onBackground = LightOnNight,
    surface = NightSurface,
    onSurface = LightOnNight,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = LightMuted,
    outline = NightOutline,
    outlineVariant = Color(0xFF3A423E),
    inverseSurface = Color(0xFFE0E5E1),
    inverseOnSurface = Color(0xFF2A312D),
    inversePrimary = Sage
)

/** نفس أشكال التطبيق: زوايا دائرية سخية تعطي الإحساس الهادئ الحديث */
val ProposalShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * سمة المقترحات. الوضع الداكن هنا صريح (بلا قراءة النظام) حتى تُلتقط صور
 * الوضعين من نفس الاختبار بتحكم كامل.
 */
@Composable
fun ProposalTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = ProposalTypography,
        shapes = ProposalShapes,
        content = content
    )
}

/** درجة أغمق من الزمردي للبطاقة البطلة (تدرّج هادئ لا يخالف الهوية) */
val SageDeep = Color(0xFF1C4A3A)
