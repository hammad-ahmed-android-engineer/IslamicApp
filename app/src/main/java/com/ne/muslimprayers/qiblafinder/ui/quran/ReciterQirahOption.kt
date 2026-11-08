package com.ne.muslimprayers.qiblafinder.ui.quran

data class ReciterQirahOption(
    val id: String,
    val name: String,
    val arabicName: String,
    val riwayah: String,
    val bitRate: String = "128"
)

val AVAILABLE_RECITERS = listOf(
    ReciterQirahOption("ar.alafasy", "Mishary Rashid Alafasy", "مشاري راشد العفاسي", "Hafs 'an 'Asim", "128"),
    ReciterQirahOption("ar.abdulbasitmurattal", "Abdul Basit Abdul Samad", "عبد الباسط عبد الصمد", "Murattal - Hafs", "192"),
    ReciterQirahOption("ar.abdurrahmaansudais", "Abdur-Rahman As-Sudais", "عبد الرحمن السديس", "Imam Masjid al-Haram", "192"),
    ReciterQirahOption("ar.saudalshuraim", "Saud Al-Shuraim", "سعود الشريم", "Imam Masjid al-Haram", "64"),
    ReciterQirahOption("ar.mahermuaiqly", "Maher Al-Muaiqly", "ماهر المعيقلي", "Imam Masjid al-Haram", "128"),
    ReciterQirahOption("ar.minshawi", "Mohamed Siddiq El-Minshawi", "محمد صديق المنشاوي", "Murattal - Hafs", "128"),
    ReciterQirahOption("ar.hudhaify", "Ali Al-Hudhaify", "علي الحذيفي", "Imam Masjid an-Nabawi", "128"),
    ReciterQirahOption("ar.husary", "Mahmoud Khalil Al-Husary", "محمود خليل الحصري", "Murattal - Hafs", "128")
)
