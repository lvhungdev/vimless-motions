package dev.lvhung.vimlessmotions.inlineFindMotion

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.util.TextRange

object InlineFindActionTypedHandler : TypedActionHandler {
    const val DIRECTION_RIGHT = 1
    const val DIRECTION_LEFT = -1

    private val escActionHandler: EditorActionHandler = object : EditorActionHandler() {
        override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
            unregisterHandlers()
        }
    }

    private var defaultRawTypedHandler: TypedActionHandler? = null
    private var defaultEscActionHandler: EditorActionHandler? = null
    private var direction = DIRECTION_RIGHT
    private var selectionAnchorOffset: Int = -1
    private var charTyped: Char? = null

    override fun execute(editor: Editor, charTyped: Char, dataContext: DataContext) {
        this.charTyped = charTyped

        findNext(editor)
    }

    fun process(direction: Int) {
        registerHandlers()

        this.direction = direction
    }

    fun findNext(editor: Editor) {
        if (charTyped == null) return

        find(editor, direction)
        unregisterHandlers()
    }

    fun findPrevious(editor: Editor) {
        if (charTyped == null) return

        find(editor, direction * -1)
        unregisterHandlers()
    }

    private fun find(editor: Editor, direction: Int) {
        val textRange = getTextRange(editor, direction)
        val text = editor.document.getText(textRange)

        val index = if (direction == DIRECTION_RIGHT) text.indexOfFirst { m -> m == charTyped }
        else text.indexOfLast { m -> m == charTyped }

        if (index == -1) return

        val caret = editor.caretModel.primaryCaret
        val newOffset = textRange.startOffset + index + 1

        caret.moveToOffset(newOffset)

        if (!caret.hasSelection()) return

        if (selectionAnchorOffset != caret.selectionStart && selectionAnchorOffset != caret.selectionEnd) {
            selectionAnchorOffset = if (direction == DIRECTION_RIGHT) caret.selectionStart else caret.selectionEnd
        }
        caret.setSelection(newOffset, selectionAnchorOffset)
    }

    private fun getTextRange(editor: Editor, direction: Int): TextRange {
        val document = editor.document
        val offset = editor.caretModel.offset
        val currentLine = document.getLineNumber(offset)

        if (direction == DIRECTION_RIGHT) {
            val endLineOffset = document.getLineEndOffset(currentLine)
            return TextRange(offset, endLineOffset)
        } else {
            val startLineOffset = document.getLineStartOffset(currentLine)
            return TextRange(startLineOffset, offset - 1)
        }
    }

    private fun registerHandlers() {
        val typedAction = TypedAction.getInstance()
        val actionManager = EditorActionManager.getInstance()

        defaultRawTypedHandler = typedAction.rawHandler
        typedAction.setupRawHandler(this)

        defaultEscActionHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ESCAPE)
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, escActionHandler)
    }

    private fun unregisterHandlers() {
        TypedAction.getInstance().setupRawHandler(defaultRawTypedHandler!!)
        EditorActionManager.getInstance().setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, defaultEscActionHandler!!)
    }
}
