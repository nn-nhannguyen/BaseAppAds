package com.app.base.mvvm.arch.extensions

import android.content.res.Resources
import android.util.TypedValue

val Float.dp get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
val Float.sp get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)
