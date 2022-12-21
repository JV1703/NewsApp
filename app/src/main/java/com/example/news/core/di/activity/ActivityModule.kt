package com.example.news.core.di.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    fun appCompatActivity(activity: Activity): AppCompatActivity = activity as AppCompatActivity

    @Provides
    fun fragmentManager(activity: AppCompatActivity) = activity.supportFragmentManager

//    @Provides
//    fun telephoneManager(activity: AppCompatActivity): TelephonyManager = activity.getSystemService(
//        Context.TELEPHONY_SERVICE
//    ) as TelephonyManager

}