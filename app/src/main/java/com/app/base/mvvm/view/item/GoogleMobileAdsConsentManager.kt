package com.app.base.mvvm.view.item

import android.app.Activity
import android.content.Context
import com.app.base.mvvm.R
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm.OnConsentFormDismissedListener
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

class GoogleMobileAdsConsentManager private constructor(context: Context) {
  internal val consentInformation: ConsentInformation =
    UserMessagingPlatform.getConsentInformation(context)

  fun interface OnConsentGatheringCompleteListener {
    fun consentGatheringComplete(error: FormError?)
  }

  val canRequestAds: Boolean
    get() = consentInformation.canRequestAds()

  val isPrivacyOptionsRequired: Boolean
    get() =
      consentInformation.privacyOptionsRequirementStatus ==
        ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

  fun gatherConsent(activity: Activity, onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener) {
    // For testing purposes, you can force a DebugGeography of EEA or NOT_EEA.
    val testDevices = activity.resources.getStringArray(R.array.device_id).toMutableList()

    val debugSettingBuilder =
      ConsentDebugSettings.Builder(activity)
    // .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
    //  .addTestDeviceHashedId(testDevices)
    // .addTestDeviceHashedId(testDevices[0])

    testDevices.forEach {
      debugSettingBuilder.addTestDeviceHashedId(it)
    }

    val debugSettings = debugSettingBuilder.build()

    val params = ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build()

    // Requesting an update to consent information should be called on every app launch.
    consentInformation.requestConsentInfoUpdate(
      activity,
      params,
      {
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
          // Consent has been gathered.
          onConsentGatheringCompleteListener.consentGatheringComplete(formError)
        }
      },
      { requestConsentError ->
        onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError)
      }
    )
  }

  /** Helper method to call the UMP SDK method to show the privacy options form. */
  fun showPrivacyOptionsForm(activity: Activity, onConsentFormDismissedListener: OnConsentFormDismissedListener) {
    UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
  }

  companion object {
    @Volatile
    private var instance: GoogleMobileAdsConsentManager? = null

    fun getInstance(context: Context) = instance
      ?: synchronized(this) {
        instance ?: GoogleMobileAdsConsentManager(context).also { instance = it }
      }
  }
}
