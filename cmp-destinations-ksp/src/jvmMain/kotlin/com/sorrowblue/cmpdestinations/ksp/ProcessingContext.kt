package com.sorrowblue.cmpdestinations.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated

class ProcessingContext(private val logger: KSPLogger) {

    // 次のラウンドに処理を持ち越すシンボル（未解決シンボルなど）のリスト。
    private val deferredSymbolsList = mutableListOf<KSAnnotated>()

    /** 遅延させるシンボルのリストを追加する。 */
    fun addDeferredSymbols(symbols: Collection<KSAnnotated>) {
        deferredSymbolsList.addAll(symbols)
        logger.info("ProcessingContext: Added ${symbols.size} deferred symbols.")
    }

    /**
     * 収集された遅延シンボルのリストを取得する。 このリストは `SymbolProcessor.process` の戻り値として使われる。
     *
     * @return 遅延シンボルの不変リスト。
     */
    val deferredSymbols: List<KSAnnotated>
        get() = deferredSymbolsList.toList() // 不変リストとして返す

    /** コンテキストをリセットする必要がある場合に備えてクリアメソッドを提供（通常はラウンドごとに新しいインスタンスが使われる）。 */
    fun clear() {
        deferredSymbolsList.clear()
        logger.info("ProcessingContext: Cleared.")
    }
}
