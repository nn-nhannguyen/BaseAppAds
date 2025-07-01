package com.app.base.mvvm.di.module

import android.util.Log
import com.app.base.mvvm.di.module.HTTPcURLLoggingInterceptor.LogLevel
import java.nio.charset.Charset
import java.util.*
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.GzipSource
import org.json.JSONObject

/**
 * OkHttpClient Implementation class of [Interceptor] that makes the log output method of
 *
 * @constructor
 *
 * デフォルトでは [LogLevel] は Verbose, [curlOutputType] は SingleLine, [isJsonPrettyPrint] は false
 *
 * @param logLevel Log output level setting
 * @param curlOutputType Set the output method of the curl command
 * @param isJsonPrettyPrint Set the output method of the curl command
 */
class HTTPcURLLoggingInterceptor(
  @SuppressWarnings("WeakerAccess") var logLevel: LogLevel = LogLevel.Verbose,
  @SuppressWarnings("WeakerAccess") var curlOutputType: CurlOutputType = CurlOutputType.SingleLine,
  @SuppressWarnings("WeakerAccess") var isJsonPrettyPrint: Boolean = false
) : Interceptor {
  private val separator = " ================================================================== "

  /** curl コマンドの出力方式を設定する */
  enum class CurlOutputType {
    /** curl コマンドを一行で出力する */
    SingleLine,

    /** curl コマンドを複数行で出力する */
    MultiLine
  }

  /** ログ出力の設定 */
  enum class LogLevel {
    /** ログを curl コマンドとレスポンスボディのみ出力 */
    Silent,

    /** ログを全て出力 */
    Verbose
  }

  private companion object {
    // Charset は使い回す
    val UTF8: Charset = Charset.forName("UTF-8")
    const val TAG = "API-LOG"
  }

  // HttpLoggingInterceptor の Logger そのまま
  private fun log(message: String) = Platform.get().log(message, Platform.INFO, null)

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val response = chain.proceed(request)

    log(buildRequestLog(response.request))

    if (logLevel == LogLevel.Verbose) {
      log(buildRequestHeaderLog(response.request, response))
      log(buildResponseLog(response))
    }

    generateLog(response)
    return response
  }

  private fun generateLog(response: Response) {
    val stringBuilder = StringBuilder()
    val endpoint = "Endpoints:${response.request.url}" +
      "\n$separator" +
      "\nRequest: \n" + buildRequestLog(response.request) +
      "\n$separator"

    val responseCode = response.code
    val statusData = "\nHTTP Status Code: $responseCode \n$separator"
    val durationData =
      "\nDuration: ${response.receivedResponseAtMillis - response.sentRequestAtMillis} ms \n$separator"
    val responseData = "\nResponse: ${buildResponseBodyLog(response)} \n$separator"

    stringBuilder.append(endpoint)
    stringBuilder.append(statusData)
    stringBuilder.append(durationData)
    stringBuilder.append(responseData)

    when (responseCode) {
      in 0..399 -> Log.d(TAG, stringBuilder.toString())
      in 400..499 -> Log.w(TAG, stringBuilder.toString())
      else -> Log.e(TAG, stringBuilder.toString())
    }
  }

  private fun buildRequestLog(request: Request): String {
    val requestBody = request.body
    val url = request.url
    val method = request.method
    val curl = mutableListOf(if (logLevel == LogLevel.Silent) "curl" else "curl -v")

    if (method.uppercase(Locale.getDefault()) != "GET") {
      curl.add("-X $method")
    }

    if (url.username.isNotEmpty() && url.password.isNotEmpty()) {
      curl.add("-u ${url.username}:${url.password}")
    }

    for (headerName in request.headers.names()) {
      val headerValues = request.headers.values(headerName)
      for (headerValue in headerValues) {
        curl.add("-H \"$headerName: ${headerValue.replace("\"", "\\\"")}\"")
      }
    }

    if (requestBody != null) {
      val buffer = Buffer()
      requestBody.writeTo(buffer)
      val body = buffer.readString(UTF8)
      curl.add("-d \"${body.replace("\"", "\\\"")}\"")
    }

    curl.add("\"$url\"")

    return when (curlOutputType) {
      CurlOutputType.SingleLine -> curl.joinToString(" ")
      CurlOutputType.MultiLine -> curl.joinToString(" \\\n\t")
    }
  }

  private fun buildRequestHeaderLog(request: Request, response: Response): String {
    val headers =
      mutableListOf("> ${request.method} ${request.url.encodedPath} ${response.protocol}")

    headers.add("> Host: ${request.url.host}")
    headers.add("> User-Agent: ${okhttp3.internal.userAgent}")

    for (headerName in response.request.headers.names()) {
      val headerValues = response.request.headers.values(headerName)

      for (headerValue in headerValues) {
        headers.add("> $headerName: $headerValue")
      }
    }

    headers.add(">\n")

    return headers.joinToString("\n")
  }

  private fun buildResponseLog(response: Response): String {
    val responseHeaders = mutableListOf("< ${response.protocol} ${response.code}")

    for (headerName in response.headers.names()) {
      val headerValues = response.headers.values(headerName)
      for (headerValue in headerValues) {
        responseHeaders.add("< $headerName: $headerValue")
      }
    }

    responseHeaders.add("<")

    return responseHeaders.joinToString("\n")
  }

  private fun buildResponseBodyLog(response: Response): String? {
    val responseBody = response.body ?: return null
    val source = responseBody.source()
    val contentType = responseBody.contentType()
    val isJson = contentType?.let { it.subtype == "json" }
    val charset = contentType?.charset() ?: UTF8

    source.request(Long.MAX_VALUE)

    // 一応レスポンスが gzip 圧縮されていた場合のデコード処理
    val buffer = if ("gzip".equals(response.header("Content-Encoding"), ignoreCase = true)) {
      GzipSource(source.buffer.clone()).use {
        val buffer = Buffer()
        buffer.writeAll(it)

        return@use buffer
      }
    } else {
      source.buffer.clone()
    }

    if (isPlainText(buffer)) {
      val body = buffer.readString(charset)

      // json をパースして再度文字列にして pretty print
      return if (isJsonPrettyPrint && isJson == true) {
        kotlin.runCatching { JSONObject(body).toString(2) }.getOrElse {
          body
        }
      } else {
        body
      }
    }

    return null
  }

  // HttpLoggingInterceptor のプレインテキスト判定方法と同じ
  private fun isPlainText(buffer: Buffer): Boolean {
    return kotlin.runCatching {
      val prefix = Buffer()

      buffer.copyTo(prefix, 0, if (buffer.size < 64) buffer.size else 64)
      repeat(16) {
        if (prefix.exhausted()) {
          return@repeat
        }
        val codePoint = prefix.readUtf8CodePoint()

        if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
          return@runCatching false
        }
      }
      return@runCatching true
    }.getOrElse {
      return@getOrElse false
    }
  }
}
