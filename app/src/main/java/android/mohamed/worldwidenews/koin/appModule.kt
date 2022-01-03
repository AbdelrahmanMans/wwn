package android.mohamed.worldwidenews.koin

import android.mohamed.worldwidenews.repository.NewsRepository
import android.mohamed.worldwidenews.room.NewsDataBase
import android.mohamed.worldwidenews.utils.ApplicationClass
import android.mohamed.worldwidenews.utils.Constants
import android.mohamed.worldwidenews.utils.Constants.DATABASE_NAME
import android.mohamed.worldwidenews.viewModels.NewsViewModel
import androidx.room.Room
import android.mohamed.worldwidenews.retrofit.NewsApi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//module for dependency injection
val module = module {
    //makes one object of retrofit
    single { provideRetrofitInstance() }
    //makes one object of database 
    single { provideDataBase() }
    single { ApplicationClass() }
    //makes one object of database dao
    single { provideNewsDao(get()) }
    //makes one object of repository
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
