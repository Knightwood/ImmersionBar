package android.accompanist.dialoghelper.dialog_fragment

import android.accompanist.dialoghelper.component_dialog.BasicFullScreenComponentDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * 状态栏沉浸、全屏处理逻辑与FullScreenDialogFragment1是一样的，
 * 只不过这里将逻辑放到了Dialog中而不是DialogFragment。
 *
 * @constructor Creates a new FullScreenDialogFragment2
 */
open class FullScreenDialogFragment2: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BasicFullScreenComponentDialog(requireContext())
    }

}

