package dev.lvhung.vimlessmotions.inlineFindMotion

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class InlineFindPrevAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        InlineFindActionTypedHandler.process(InlineFindActionTypedHandler.DIRECTION_LEFT)
    }
}
