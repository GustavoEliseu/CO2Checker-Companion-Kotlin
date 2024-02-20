package com.gustavo.cocheckercompaniomkotlin.di


import com.gustavo.cocheckercompaniomkotlin.data.remote.sensor.Api
import com.gustavo.cocheckercompaniomkotlin.data.remote.sensor.SensorRepository
import com.gustavo.cocheckercompaniomkotlin.model.domain.CheckIfSensorConnectionUseCase
import com.gustavo.cocheckercompaniomkotlin.model.domain.SendWifiToSensorUseCase
import com.gustavo.cocheckercompaniomkotlin.utils.ESP_WIFI_TIMEOUT
import com.gustavo.cocheckercompanionkotlin.BuildConfig
import com.gustavo.cocheckercompanionkotlin.BuildConfig.ESP_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {



    @Provides
    @Singleton
    fun okhttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
         return OkHttpClient.Builder()
            .writeTimeout(ESP_WIFI_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ESP_WIFI_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(ESP_WIFI_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }

    @Provides
    @Singleton
    fun providesSensorRepository(api: Api): SensorRepository{
        return SensorRepository(api)
    }
    @Provides
    @Singleton
    fun getInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message ->
            Timber.i(message)
        }
        interceptor.level = BuildConfig.INTERCEPTOR_LEVEL
        return interceptor
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi) : Retrofit{
        return Retrofit.Builder()
            .baseUrl(ESP_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    @Provides
    internal fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(customDateAdapter)
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
            return chain.proceed(request.build())
        }
    }
}