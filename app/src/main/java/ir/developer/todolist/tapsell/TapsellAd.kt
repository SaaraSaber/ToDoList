package ir.developer.todolist.tapsell

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import ir.developer.todolist.R
import ir.developer.todolist.global.Utils
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusBannerType
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel


class TapsellAd(val context: Activity) {
    fun connectToTapsell() {
        try {
            TapsellPlus.initialize(
                context as Activity?,
                Utils.TAPSELL_KEY,
                object : TapsellPlusInitListener {
                    override fun onInitializeSuccess(adNetworks: AdNetworks) {
                        Log.d("onInitializeSuccess", adNetworks.name)
                    }

                    override fun onInitializeFailed(
                        adNetworks: AdNetworks,
                        adNetworkError: AdNetworkError
                    ) {
                        Log.e(
                            "onInitializeFailed",
                            "ad network: ${adNetworks.name}, error: ${adNetworkError.errorMessage}"
                        )
                    }
                })
        } catch (t: Throwable) {
            Toast.makeText(context, "خطایی رخ داده است", Toast.LENGTH_LONG).show()
        }

    }

    fun requestStandardBannerAd() {
        try {
            TapsellPlus.requestStandardBannerAd(
                context, Utils.ZONE_ID_STANDARD_BANNER,
                TapsellPlusBannerType.BANNER_320x50,
                object : AdRequestCallback() {
                    override fun response(tapsellPlusAdModel: TapsellPlusAdModel) {
                        super.response(tapsellPlusAdModel)
                        //Ad is ready to show
                        //Put the ad's responseId to your responseId variable
                        val standardBannerResponseId = tapsellPlusAdModel.responseId
                        showStandardBannerAd(standardBannerResponseId = standardBannerResponseId)
                    }
                    override fun error(message: String?) {
                        Log.i("error", "error: $message")
                    }
                })
        } catch (t: Throwable) {
            Log.i("error", "error1: $t")
        }
    }

    private val adContainer: RelativeLayout = context.findViewById(R.id.standardBanner)

    private fun showStandardBannerAd(standardBannerResponseId: String) {
        try {
            TapsellPlus.showStandardBannerAd(context, standardBannerResponseId,
                adContainer,
                object : AdShowListener() {
                    override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel) {
                        super.onOpened(tapsellPlusAdModel)
                    }
                    override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel) {
                        super.onError(tapsellPlusErrorModel)
                    }
                })
        } catch (t: Throwable) {
            Toast.makeText(context, "خطایی رخ داده است", Toast.LENGTH_LONG).show()
        }
    }


    fun requestVideoAd() {
        try {
            TapsellPlus.requestInterstitialAd(
                context as Activity?,
                Utils.TAPSELL_GIFT_VIDEO_KEY,
                object : AdRequestCallback() {
                    override fun response(tapsellPlusAdModel: TapsellPlusAdModel) {
                        super.response(tapsellPlusAdModel)

                        // آگهی آماده نمایش است
                        // مقدار responseId آگهی را در متغیر خود ذخیره کنید
                        val rewardedResponseId = tapsellPlusAdModel.responseId
                        showVideoAd(context, rewardedResponseId)
//                        viewModelMusic.setAdPlaying(true)
                    }

                    override fun error(message: String?) {
                        Log.e("TapsellError", message ?: "Unknown error")
                    }
                }
            )
        } catch (t: Throwable) {
            Toast.makeText(context, "خطایی رخ داده است", Toast.LENGTH_LONG).show()
        }
    }

    private fun showVideoAd(context1: Context, responseId: String) {
        try {
            TapsellPlus.showInterstitialAd(
                context1 as Activity?, responseId,
                object : AdShowListener() {
                    override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel) {
                        super.onOpened(tapsellPlusAdModel)
                        Log.i("showAd", "onOpened: ")
                    }

                    override fun onClosed(tapsellPlusAdModel: TapsellPlusAdModel) {
                        super.onClosed(tapsellPlusAdModel)
//                        viewModelMusic.setAdPlaying(false)
                        Log.i("showAd", "onClosed: ")
                    }

                    override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel) {
                        super.onError(tapsellPlusErrorModel)
//                        viewModelMusic.setAdPlaying(false)
                        Log.i("showAd", "onError: ")
                    }
                }
            )
        } catch (t: Throwable) {
//            viewModelMusic.setAdPlaying(false)
            Toast.makeText(context, "خطایی رخ داده است", Toast.LENGTH_LONG).show()
        }
    }


}