package cash.andrew.hodlr.config

/**
 * Crypto currencies that are supported by coinbase for purchase
 * with [FiatCurrency].
 *
 * Note: Some of these assets are not available in the sandbox. Use
 * `holdr products` to see the list of assets that are actually available
 * to you.
 */
enum class CryptoCurrency {
  BTC,
  ETH,
  XRP,
  LTC,
  BHC,
  EOS,
  XLM,
  ETC,
  LINK,
  REP,
  ZRX
}
