package dev.lvhung.vimlessmotions.base

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.editor.actionSystem.TypedActionHandler

abstract class MotionHandler : TypedActionHandler {
    private val escActionHandler: EditorActionHandler = object : EditorActionHandler() {
        override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
            dispose()
        }
    }

    private var defaultRawTypedHandler: TypedActionHandler? = null
    private var defaultEscActionHandler: EditorActionHandler? = null

    protected fun init() {
        val typedAction = TypedAction.getInstance()
        val actionManager = EditorActionManager.getInstance()

        defaultRawTypedHandler = typedAction.rawHandler
        typedAction.setupRawHandler(this)

        defaultEscActionHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ESCAPE)
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, escActionHandler)
    }

    protected fun dispose() {
        TypedAction.getInstance().setupRawHandler(defaultRawTypedHandler!!)
        EditorActionManager.getInstance().setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, defaultEscActionHandler!!)
    }
}
