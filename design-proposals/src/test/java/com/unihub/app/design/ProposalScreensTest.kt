package com.unihub.app.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.github.takahirom.roborazzi.captureRoboImage
import android.view.Window
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * اختبارات التقاط صور مقترحات إعادة التصميم — بلا محاكي إطلاقاً.
 *
 * الإعداد الإجباري المعتمد:
 * - [Config.sdk] = 35: البيئة تعمل على Android 15 (SDK 35).
 * - [GraphicsMode] NATIVE: الرسم بمحرك Skia الأصلي (RNG) لا بمحاكاة Java،
 *   فتخرج الصور بواقعية الجهاز الحقيقي (تدرجات، ظلال، تشكيل عربي).
 * - qualifiers = "ar-ldrtl-w360dp-h800dp-mdpi": لغة عربية واتجاه RTL وشاشة
 *   هاتف عمودية 360dp × 800dp (نسبة 20:9 الحديثة) بكثافة mdpi (كل dp =
 *   بكسل واحد، فمقاس الصورة 360×800). الترتيب إلزامي حسب قواعد مؤهلات
 *   موارد أندرويد: اللغة ثم الاتجاه ثم الأبعاد ثم الكثافة. الاتجاه RTL
 *   يُفرض إضافياً عبر CompositionLocalProvider ليطابق سلوك التطبيق على
 *   جهاز عربي.
 *
 * التشغيل (تسجيل الصور):
 *   ./gradlew :design-proposals:recordRoborazziDebug
 * والمخرجات في design-proposals/build/outputs/roborazzi/
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "ar-ldrtl-w360dp-h800dp-mdpi")
class ProposalScreensTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ProposalCaptureActivity>()

    private fun capture(
        fileName: String,
        darkTheme: Boolean = false,
        content: @Composable () -> Unit
    ) {
        // نافذة الاختبار تأتي بثيم النظام الافتراضي الذي يركّب شريط إجراءات
        // داكناً 48dp (PhoneWindow يقرأ سمات الثيم عند إنشائه قبل أي setTheme)
        // ويُلاءم أشرطة النظام فتقصّ الشاشة. المعالجة قبل ضبط المحتوى
        // (وقبل تركيب الديكور في أول setContentView):
        // 1) إسقاط شريط العنوان/الإجراءات وإخفاء أي نسخة مركّبة منه.
        // 2) تعطيل ملاءمة أشرطة النظام وتصفير لونيهما.
        // فيمتد الجذر لكامل 360×800 ويظهر إطار الجهاز (شريط الحالة والإيماءات
        // الذي يرسمه DeviceFrame) دون أي طبقة داكنة.
        composeRule.runOnUiThread {
            val activity = composeRule.activity
            val window = activity.window
            runCatching { window.requestFeature(Window.FEATURE_NO_TITLE) }
            activity.actionBar?.hide()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
        }
        composeRule.setContent {
            ProposalTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    DeviceFrame { content() }
                }
            }
        }
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$fileName")
    }

    @Test
    fun dashboardLight() = capture("dashboard_light.png") {
        DashboardRedesign()
    }

    @Test
    fun dashboardDark() = capture("dashboard_dark.png", darkTheme = true) {
        DashboardRedesign()
    }

    @Test
    fun tasksPlanner() = capture("tasks_planner_light.png") {
        TasksRedesign()
    }

    @Test
    fun files() = capture("files_light.png") {
        FilesRedesign()
    }

    @Test
    fun settingsLight() = capture("settings_light.png") {
        SettingsRedesign()
    }

    @Test
    fun settingsDark() = capture("settings_dark.png", darkTheme = true) {
        SettingsRedesign()
    }

    @Test
    fun auth() = capture("auth_light.png") {
        AuthRedesign()
    }
}
