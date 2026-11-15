plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.roborazzi)
}

// وحدة مقترحات إعادة التصميم (Modern Redesign Proposals).
// شاشات Compose الجديدة هنا مستقلة عن ViewModels/Hilt بُنيةً (بيانات نموذجية
// ثابتة) حتى يمكن التقاط صورها من اختبارات JVM بلا محاكي عبر Robolectric +
// Roborazzi بمحرك الرسم الأصلي (Native Skia / RNG) — وهو الإعداد المطلوب:
// SDK 35، شاشة هاتف عمودية 360dp×800dp بكثافة mdpi، واتجاه RTL بالعربية.

android {
    namespace = "com.unihub.app.design"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests {
            // Robolectric يحتاج موارد Android (سلاسل/ألوان/خطوط) داخل اختبارات JVM
            isIncludeAndroidResources = true
            all { test ->
                // رفع الشاشات بمحرك Skia الأصلي يستهلك ذاكرة أكبر من اختبارات
                // الوحدة العادية؛ 2GB كومة تكفي 360×800 mdpi بهامش مريح.
                test.maxHeapSize = "2048m"
                // جرة android-all يبحث عنها Robolectric وقت التشغيل؛ نوجهه إلى
                // مرآة Google GCS نفسها المستخدمة في Gradle تحسباً لأي تقييد
                // معدل من Maven Central على بيئات CI.
                test.systemProperty(
                    "robolectric.dependency.repo.url",
                    "https://maven-central.storage-download.googleapis.com/maven2"
                )
            }
        }
    }
}

dependencies {
    // Compose بنفس BOM الخاص بالتطبيق — هوية بصرية واحدة
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.animation)

    // اختبارات الالتقاط: Robolectric (SDK 35) + Roborazzi + قواعد Compose
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
    // جرة بيئة أندرويد الكاملة لـ SDK 35: على مسار «التشغيل» فقط (لا الترجمة،
    // وإلا رأى مترجم كوتلن الأعضاء المُجهَّزة $$robo$ في أصناف أندرويد) —
    // وجودها وقت التشغيل يجعل Robolectric يستعملها مباشرة بدل تنزيلها من الشبكة.
    testRuntimeOnly("org.robolectric:android-all-instrumented:15-robolectric-12650502-i7")
}
