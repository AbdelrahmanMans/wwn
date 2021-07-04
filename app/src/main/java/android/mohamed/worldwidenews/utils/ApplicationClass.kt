package android.mohamed.worldwidenews.utils

import android.app.Application
import android.mohamed.worldwidenews.koin.module
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ApplicationClass: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ApplicationClass)
            modules(listOf(module))

        }
    }
}