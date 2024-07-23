package dev.lvhung.vimlessmotions.inlineFindMotion

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class BackwardInlineFindAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        InlineFindActionTypedHandler.findPrevious(editor)
    }
}