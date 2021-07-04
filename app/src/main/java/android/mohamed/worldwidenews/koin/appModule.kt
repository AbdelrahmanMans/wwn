package android.mohamed.worldwidenews.koin

import android.mohamed.worldwidenews.repository.NewsRepository
import android.mohamed.worldwidenews.room.NewsDataBase
import android.mohamed.worldwidenews.utils.ApplicationClass
import android.mohamed.worldwidenews.utils.Constants
import android.mohamed.worldwidenews.utils.Constants.DATABASE_NAME
import android.mohamed.worldwidenews.viewModels.NewsViewModel
import androidx.room.Room
import com.example.newsproject.Api.NewsApi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val module = module {
    single { provideRetrofitInstance() }
    single { provideDataBase() }
    single { ApplicationClass() }
    single { provideNewsDao(get()) }
    single { NewsRepository(get(), get()) }
    viewModel { NewsViewModel(get(), get()) }
}

fun provideNewsDao(dataBase: NewsDataBase) = dataBase.newsDao()

private fun Scope.provideDataBase() = Room.databaseBuilder(
    androidApplication(),
    NewsDataBase::class.java,
    DATABASE_NAME
).build()


private fun provideRetrofitInstance(): NewsApi {
    return Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApi::class.java)
}
