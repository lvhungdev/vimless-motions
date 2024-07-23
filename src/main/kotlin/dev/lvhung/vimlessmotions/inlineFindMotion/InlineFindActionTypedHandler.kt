package dev.lvhung.vimlessmotions.inlineFindMotion

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.util.TextRange

object InlineFindActionTypedHandler : TypedActionHandler {
    const val DIRECTION_RIGHT = 1
    const val DIRECTION_LEFT = -1

    private var rawTypedHandler: TypedActionHandler? = null
    private var direction = DIRECTION_RIGHT
    private var charTyped: Char? = null

    override fun execute(editor: Editor, charTyped: Char, dataContext: DataContext) {
        this.charTyped = charTyped

        findNext(editor)
    }

    fun process(direction: Int) {
        val typedAction = TypedAction.getInstance()
        rawTypedHandler = typedAction.rawHandler
        typedAction.setupRawHandler(this)

        this.direction = direction
    }

    fun findNext(editor: Editor) {
        find(editor, direction)
    }

    fun findPrevious(editor: Editor) {
        find(editor, direction * -1)
    }

    private fun find(editor: Editor, direction: Int) {
        if (charTyped == null) return

        val textRange = getTextRange(editor, direction)
        val text = editor.document.getText(textRange)

        val index = if (direction == DIRECTION_RIGHT) text.indexOfFirst { m -> m == charTyped }
        else text.indexOfLast { m -> m == charTyped }

        if (index != -1) {
            editor.caretModel.moveToOffset(textRange.startOffset + index + 1)
        }

        TypedAction.getInstance().setupRawHandler(rawTypedHandler!!)
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
}