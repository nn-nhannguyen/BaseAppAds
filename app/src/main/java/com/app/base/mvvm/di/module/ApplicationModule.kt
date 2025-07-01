package com.app.base.mvvm.di.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.app.base.mvvm.BuildConfig
import com.app.base.mvvm.data.local.AppDatabase
import com.app.base.mvvm.data.local.AppDbHelper
import com.app.base.mvvm.data.local.DbHelper
import com.app.base.mvvm.network.api.ApiHelper
import com.app.base.mvvm.network.api.ApiHelperImpl
import com.app.base.mvvm.network.api.ApiService
import com.app.base.mvvm.repository.AppSettingsRepository
import com.app.base.mvvm.repository.AppSettingsRepositoryInterface
import com.app.base.mvvm.utils.ConstantUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

  @Provides
  fun provideContext(application: Application): Context = application.applicationContext

  @Provides
  fun provideBaseUrl() = BuildConfig.BASE_URL

  @Provides
  @Singleton
  fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
//            .addInterceptor(
//                HTTPcURLLoggingInterceptor(
//                    curlOutputType = HTTPcURLLoggingInterceptor.CurlOutputType.MultiLine,
//                    isJsonPrettyPrint = true
//                )
//            )
      .build()
  } else {
    OkHttpClient
      .Builder()
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient, url: String): Retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(createMoshiBuilder()))
    .baseUrl(url)
    .client(okHttpClient)
    .build()

  @Provides
  @Singleton
  fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

  @Provides
  @Singleton
  fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

  @Provides
  @Singleton
  fun provideDbHelper(dbHelper: AppDbHelper): DbHelper = dbHelper

  @Provides
  @Singleton
  fun provideAppDatabase(application: Application): AppDatabase {
        /*  return Room.databaseBuilder(context, AppDatabase::class.java, dbName).fallbackToDestructiveMigration()
                  .build()*/
    return Room.databaseBuilder(
      application,
      AppDatabase::class.java,
      ConstantUtil.DATABASE_NAME
    )
      .fallbackToDestructiveMigration()
      .build()
  }

  private fun createMoshiBuilder(): Moshi {
    val moshi = Moshi.Builder()
    moshi.add(KotlinJsonAdapterFactory())
    return moshi.build()
  }

  @Provides
  @Singleton
  fun provideAppSettingRepository(@ApplicationContext context: Context): AppSettingsRepositoryInterface {
    return AppSettingsRepository(context)
  }
}
