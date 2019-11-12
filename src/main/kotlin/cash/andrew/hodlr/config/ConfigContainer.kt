package cash.andrew.hodlr.config

data class ConfigContainer(
    val coinbaseConfig: CoinbaseConfig,
    val loggingConfig: LoggingConfig,
    val recurringPurchaseConfig: RecurringPurchaseConfig,
    val sandbox: Boolean,
    val telegramConfig: TelegramConfig? = null
)