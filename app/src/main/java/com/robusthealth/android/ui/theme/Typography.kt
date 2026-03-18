package com.robusthealth.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.GoogleFont.Provider
import androidx.compose.ui.unit.sp
import com.robusthealth.android.R

private val provider = Provider(
	providerAuthority = "com.google.android.gms.fonts",
	providerPackage = "com.google.android.gms",
	certificates = R.array.com_google_android_gms_fonts_certs
)

private val sora = FontFamily(Font(GoogleFont("Sora"), provider))
private val manrope = FontFamily(Font(GoogleFont("Manrope"), provider))

val Typography = Typography(
	displayLarge = TextStyle(
		fontFamily = sora,
		fontWeight = FontWeight.Bold,
		fontSize = 42.sp,
		letterSpacing = (-0.5).sp
	),
	headlineLarge = TextStyle(
		fontFamily = sora,
		fontWeight = FontWeight.SemiBold,
		fontSize = 32.sp,
		letterSpacing = (-0.25).sp
	),
	titleLarge = TextStyle(
		fontFamily = sora,
		fontWeight = FontWeight.SemiBold,
		fontSize = 22.sp,
		letterSpacing = 0.sp
	),
	bodyLarge = TextStyle(
		fontFamily = manrope,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		letterSpacing = 0.1.sp
	),
	bodyMedium = TextStyle(
		fontFamily = manrope,
		fontWeight = FontWeight.Normal,
		fontSize = 14.sp,
		letterSpacing = 0.1.sp
	),
	labelLarge = TextStyle(
		fontFamily = manrope,
		fontWeight = FontWeight.SemiBold,
		fontSize = 14.sp,
		letterSpacing = 0.5.sp
	)
)
