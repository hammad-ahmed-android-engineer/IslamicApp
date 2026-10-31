package com.ne.muslimprayers.qiblafinder.ui.quran

data class ReciterQirahOption(
    val id: String,
    val name: String,
    val riwayah: String, // Qir'ah / Riwayah (e.g. Hafs, Warsh)
    val bitRate: String = "128"
)

val AVAILABLE_RECITERS = listOf(
    ReciterQirahOption("ar.alafasy", "Mishary Rashid Alafasy", "Hafs 'an 'Asim"),
    ReciterQirahOption("ar.abdulbasitmurattal", "Abdul Basit Abdul Samad", "Murattal - Hafs"),
    ReciterQirahOption("ar.abdurrahmaansudais", "Abdur-Rahman As-Sudais", "Hafs 'an 'Asim"),
    ReciterQirahOption("ar.saudalshuraim", "Saud Al-Shuraim", "Hafs 'an 'Asim"),
    ReciterQirahOption("ar.mahermuaiqly", "Maher Al-Muaiqly", "Hafs 'an 'Asim"),
    ReciterQirahOption("ar.minshawi", "Mohamed Siddiq El-Minshawi", "Hafs 'an 'Asim"),
    ReciterQirahOption("ar.hudhaify", "Ali Al-Hudhaify", "Hafs 'an 'Asim"),
    ReciterQirahOption("ar.warsh", "Yassin Al-Jazaery", "Warsh 'an Nafi'")
)
