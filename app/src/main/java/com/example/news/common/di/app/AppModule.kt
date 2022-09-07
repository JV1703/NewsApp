package com.example.news.common.di.app

import android.content.Context
import android.telephony.TelephonyManager
import com.example.news.NewsApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext app: Context): NewsApplication =
        (app as NewsApplication)

    @Provides
    fun provideTelephonyManager(@ApplicationContext app: Context): TelephonyManager =
        app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

}