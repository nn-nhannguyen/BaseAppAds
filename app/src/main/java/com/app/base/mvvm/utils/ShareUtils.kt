package com.app.base.mvvm.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.View
import com.app.base.mvvm.R
import com.app.base.mvvm.view.dialog.AlertDialog
import com.google.android.material.snackbar.Snackbar

object ShareUtils {
  fun shareText(context: Context, content: String?) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/plain"
    // share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
    share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
    share.putExtra(Intent.EXTRA_TEXT, content)
    context.startActivity(
      Intent.createChooser(
        share,
        context.getString(R.string.text_share_with_app)
      )
    )
  }

  fun sendTextWithApp(context: Context, view: View, content: String?, packageName: String) {
    if (!TextUtils.isEmpty(content)) {
      val intent = Intent()
      intent.action = Intent.ACTION_SEND
      intent.type = "text/plain"
      intent.putExtra(Intent.EXTRA_TEXT, content)
      intent.setPackage(packageName)
      try {
        context.startActivity(intent)
      } catch (e: ActivityNotFoundException) {
        Snackbar.make(context, view, context.getString(R.string.text_request_login), 1500)
          .show()
      }
    }
  }

  fun sendSMS(context: Context, content: String?) {
    if (!TextUtils.isEmpty(content)) {
      try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.putExtra("sms_body", content)
        intent.type = "vnd.android-dir/mms-sms"
        context.startActivity(intent)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  fun sendMail(context: Context) {
    AlertDialog.Builder(context)
      .setCanceledOnTouchOutside(false)
      .setMessage(context.getString(R.string.text_please_feedback))
      .setNegativeButtonText(
        context.getString(R.string.text_feedback),
        object : AlertDialog.OnClickListener {
          override fun onClick(v: View) {
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + BuildConfig.MAIL_SUPPORT))
//            intent.putExtra(
//              Intent.EXTRA_SUBJECT, context.getString(R.string.text_feedback_context)
//                  + " " + context.getString(R.string.app_name)
//            )
//            intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.text_feedback_context))
//            context.startActivity(intent)
            sendMail(
              context,
              context.getString(R.string.text_feedback) +
                " " + context.getString(R.string.app_name),
              context.getString(R.string.text_feedback)
            )
          }
        }
      )
      .setPositiveButtonText(context.getString(R.string.text_cancel), null)
      .create().show()
  }

  /**
   * Send email
   *
   * @param context context
   */
  fun sendMail(context: Context?, subject: String?, content: String?) {
    if (context == null) {
      return
    }
    try {
      val address = "@mail"
      val cc = ""
      val uri = Uri.parse("mailto:$address")
      val intent = Intent(Intent.ACTION_SENDTO, uri)
      intent.putExtra(Intent.EXTRA_CC, arrayOf(cc))
      intent.putExtra(Intent.EXTRA_SUBJECT, subject)
      intent.putExtra(Intent.EXTRA_TEXT, content)
      context.startActivity(
        Intent.createChooser(
          intent,
          context.getString(R.string.share_by_email)
        )
      )
    } catch (ex: java.lang.Exception) {
      showErrorEmail(context)
    }
  }

  private fun showErrorEmail(context: Context) {
    AlertDialog.Builder(context)
      .setCanceledOnTouchOutside(false)
      .setMessage(R.string.can_not_send_email)
      .setPositiveButtonText(R.string.text_ok, null)
      .create().show()
  }
}
