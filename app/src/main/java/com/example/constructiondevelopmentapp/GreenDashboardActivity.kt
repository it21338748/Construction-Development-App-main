package com.example.constructiondevelopmentapp
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.*
import android.Manifest


class GreenDashboardActivity : AppCompatActivity() {

    //private lateinit var materialBtn: Button
    private lateinit var materialImage: ImageView
    private lateinit var ReportImage: ImageView
    private lateinit var reportBtn: Button
    private lateinit var infoImage: ImageView
    private lateinit var infoBtn: Button
    private lateinit var serviceImage: ImageView
    private lateinit var serviceBtn: Button


    private var mInterstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.hide()
        loadInterAd()

        val materialBtn : Button = findViewById(R.id.materialBtn)

        //materialBtn = findViewById(R.id.materialBtn)
        materialImage = findViewById(R.id.materialImage)
        ReportImage=findViewById(R.id.ReportImage)
        reportBtn= findViewById(R.id.reportBtn)
        infoImage= findViewById(R.id.infoImage)
        infoBtn= findViewById(R.id.infoBtn)
        serviceBtn= findViewById(R.id.serviceBtn)
        serviceImage= findViewById(R.id.serviceImage)


        /*materialBtn.setOnClickListener {
            showInterAds()
        }*/

        serviceBtn.setOnClickListener {
            val intent = Intent(this, ServiceMainActivity::class.java)
            startActivity(intent)
        }

        serviceImage.setOnClickListener {
            val intent = Intent(this, ServiceMainActivity::class.java)
            startActivity(intent)
        }

        materialBtn.setOnClickListener {
            showInterAds()
            val intent = Intent(this, MaterialActivity::class.java)
            startActivity(intent)
        }

        materialImage.setOnClickListener{
            val intent = Intent(this,MaterialActivity::class.java)
            startActivity(intent)
        }

        reportBtn.setOnClickListener {
            val intent = Intent(this, RatingReportActivity::class.java)
            startActivity(intent)
        }

        ReportImage.setOnClickListener {
            val intent = Intent(this, RatingReportActivity::class.java)
            startActivity(intent)
        }

        infoImage.setOnClickListener {
            val intent = Intent(this, MoreDetailsActivity::class.java)
            startActivity(intent)
        }

        infoBtn.setOnClickListener {
            showInterAds1()
            val intent = Intent(this,MoreDetailsActivity::class.java)
            startActivity(intent)
        }



    }

    private fun showInterAds1() {

        if(mInterstitialAd !=null){

            mInterstitialAd?.fullScreenContentCallback=object : FullScreenContentCallback(){
                override fun onAdClicked() {
                    super.onAdClicked()
                }
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    val intent = Intent(this@GreenDashboardActivity,GreenDashboardActivity::class .java)
                    startActivity(intent)
                }
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                }
                override fun onAdImpression() {
                    super.onAdImpression()
                }
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                }
            }
            mInterstitialAd?.show(this)
        }else{
            val intent = Intent(this,MaterialActivity::class .java)
            startActivity(intent)
        }

    }

    private fun showInterAds() {

        if(mInterstitialAd !=null){

            mInterstitialAd?.fullScreenContentCallback=object : FullScreenContentCallback(){
                override fun onAdClicked() {
                    super.onAdClicked()
                }
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    val intent = Intent(this@GreenDashboardActivity,MaterialActivity::class .java)
                    startActivity(intent)
                }
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                }
                override fun onAdImpression() {
                    super.onAdImpression()
                }
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                }
            }
            mInterstitialAd?.show(this)
        }else{
            val intent = Intent(this,MaterialActivity::class .java)
            startActivity(intent)
        }
    }

    private fun loadInterAd() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }
}