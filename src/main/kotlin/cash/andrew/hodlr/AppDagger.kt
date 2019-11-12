package cash.andrew.hodlr

import cash.andrew.hodlr.config.ConfigContainer
import cash.andrew.hodlr.config.ConfigModule
import cash.andrew.hodlr.config.RecurringPurchaseConfig
import cash.andrew.hodlr.http.HttpModule
import cash.andrew.hodlr.http.coinbase.CoinbaseModule
import cash.andrew.hodlr.http.coinbase.CoinbaseRepository
import cash.andrew.hodlr.http.telegram.TelegramModule
import cash.andrew.hodlr.logging.LogLevel
import cash.andrew.hodlr.logging.Logger
import cash.andrew.hodlr.logging.LoggingModule
import cash.andrew.hodlr.schedulers.RecurringDepositTaskExecutor
import cash.andrew.hodlr.schedulers.RecurringPurchaseTaskExecutor
import cash.andrew.hodlr.util.ShutdownHook
import cash.andrew.hodlr.util.shutDown
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import java.time.Clock
import javax.inject.Singleton

@Singleton
@Component(modules = [
  AppModule::class,
  ConfigModule::class,
  HttpModule::class,
  CoinbaseModule::class,
  TelegramModule::class,
  LoggingModule::class
])
interface AppComponent {
  val coinbaseRepository: CoinbaseRepository
  val recurringPurchaseConfig: RecurringPurchaseConfig
  val logger: Logger
  val shutdownHook: ShutdownHook
  val recurringDepositTaskExecutor: RecurringDepositTaskExecutor
  val recurringPurchaseTaskExecutor: RecurringPurchaseTaskExecutor

  @Component.Builder
  interface Builder {
    @BindsInstance fun configContainer(configContainer: ConfigContainer): Builder
    @BindsInstance fun logLevel(logLevel: LogLevel): Builder
    fun build(): AppComponent
  }
}

@Module
class AppModule {
  @Provides
  @Singleton
  fun provideClock(): Clock = Clock.systemDefaultZone()

  @Provides
  @Singleton
  fun provideShutdownHook(okHttpClient: OkHttpClient): ShutdownHook = object: ShutdownHook {
    override fun invoke() {
      okHttpClient.shutDown()
    }
  }
}

// hack to put all the red underline squigllz from IntelliJ into one file.
val componentBuilder: AppComponent.Builder get() = DaggerAppComponent.builder()
