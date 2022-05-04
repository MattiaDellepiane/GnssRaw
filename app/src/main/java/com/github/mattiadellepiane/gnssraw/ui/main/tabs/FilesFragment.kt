package com.github.mattiadellepiane.gnssraw.ui.main.tabs

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.github.mattiadellepiane.gnssraw.R
import com.github.mattiadellepiane.gnssraw.data.SharedData
import android.widget.TextView
import android.widget.LinearLayout
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.util.Arrays
import android.os.Environment
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import android.view.Gravity
import androidx.core.content.ContextCompat
import android.util.TypedValue
import android.content.DialogInterface
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import java.util.function.Consumer

class FilesFragment : Fragment() {
    private var layout: LinearLayout? = null
    private var selected: MutableList<String?>? = null
    private var imgBtns: MutableList<ImageButton>? = null
    private var linLayouts: MutableList<LinearLayout>? = null
    private var isSelecting = false
    private var focusView: View? = null
    var clearSelection: FloatingActionButton? = null
    var deleteSelection: FloatingActionButton? = null
    var shareSelection: FloatingActionButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragment = inflater.inflate(R.layout.fragment_files, container, false)
        selected = ArrayList()
        imgBtns = ArrayList()
        linLayouts = ArrayList()
        clearSelection = fragment.findViewById(R.id.clearSelection)
        deleteSelection = fragment.findViewById(R.id.deleteSelection)
        shareSelection = fragment.findViewById(R.id.shareSelection)
        focusView = fragment.findViewById(R.id.focusView)
        clearSelection!!.setOnClickListener(View.OnClickListener { view: View? -> clearSelection() })
        deleteSelection!!.setOnClickListener(View.OnClickListener { view: View? -> showDeleteSelectedFilesDialog() })
        shareSelection!!.setOnClickListener(View.OnClickListener { view: View? -> shareSelection() })
        layout = fragment.findViewById(R.id.filesLinearLayout)
        val f = files
        if (f != null) {
            val files: List<File> = ArrayList(Arrays.asList(*f))
            Collections.sort(files, Comparator.comparing { obj: File -> obj.lastModified() })
            files.forEach(Consumer { p: File -> addFileView(p.name) })
        }
        SharedData.instance.filesFragment = this
        return fragment
    }

    val files: Array<File>
        get() {
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + getString(R.string.app_documents_folder))
            return directory.listFiles { obj: File -> obj.isFile }
        }

    fun openFile(filename: String?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val file = FileProvider.getUriForFile(
                requireContext(),
                requireActivity().applicationContext
                        .packageName + ".provider", File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + getString(R.string.app_documents_folder), filename))
        intent.setDataAndType(file, "text/plain")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    fun addFileView(fileName: String?) {
        if (context == null) return
        //Linear layout containing name of file and button for options
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        linearLayout.gravity = Gravity.CENTER
        linearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_background))
        linLayouts!!.add(linearLayout)
        val v = TextView(context)
        v.text = fileName
        val selectableItemBackground = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, selectableItemBackground, true)
        v.setBackgroundResource(selectableItemBackground.resourceId)
        v.isClickable = true
        v.setPadding(0, 70, 0, 70)
        //Divide file views with a line
        val divider = View(context)
        val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 5)
        divider.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dividerColor))
        divider.layoutParams = lp
        //More vert icon - file options
        val imgBtn = ImageButton(context)
        imgBtn.setImageResource(R.drawable.file_options)
        imgBtn.setPadding(50, 50, 50, 50)
        imgBtn.setBackgroundResource(selectableItemBackground.resourceId)
        imgBtn.setOnClickListener { view: View? -> showFileOptions(fileName) }
        imgBtns!!.add(imgBtn)
        v.setOnClickListener { view: View? ->
            if (isSelecting) {
                if (selected!!.contains(fileName)) {
                    selected!!.remove(fileName)
                    linearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_background))
                    return@setOnClickListener
                }
                selected!!.add(fileName)
                linearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
                return@setOnClickListener
            }
            openFile(fileName)
        }
        v.setOnLongClickListener { view: View? ->
            if (!isSelecting) {
                hideFileOptions()
                clearSelection!!.visibility = View.VISIBLE
                deleteSelection!!.visibility = View.VISIBLE
                shareSelection!!.visibility = View.VISIBLE
                selected!!.add(fileName)
                linearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
                isSelecting = true
                focusView!!.visibility = View.VISIBLE
            }
            true
        }
        //Set tags used for removing views when the user deletes a file
        imgBtn.tag = fileName + "_imgBtn"
        linearLayout.tag = fileName + "_layout"
        divider.tag = fileName + "_divider"

        //Add the previous created views to the layout
        linearLayout.addView(imgBtn)
        linearLayout.addView(v, 0)
        layout!!.addView(divider, 0)
        layout!!.addView(linearLayout, 0)
    }

    private fun hideFileOptions() {
        imgBtns!!.forEach(Consumer { imgBtn: ImageButton -> imgBtn.visibility = View.INVISIBLE })
    }

    private fun showFileOptions() {
        imgBtns!!.forEach(Consumer { imgBtn: ImageButton -> imgBtn.visibility = View.VISIBLE })
    }

    private fun showDeleteFileDialog(fileName: String?) { //Called on long click upon a file name
        AlertDialog.Builder(context)
                .setTitle("Delete file")
                .setMessage("Are you sure you want to delete '$fileName'?")
                .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int -> deleteFile(fileName) } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    private fun showFileOptions(fileName: String?) { //Show bottom sheet file options
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_fileoptions)
        val fileTextView = bottomSheetDialog.findViewById<TextView>(R.id.filename_placeholder)
        val share = bottomSheetDialog.findViewById<LinearLayout>(R.id.share)
        val delete = bottomSheetDialog.findViewById<LinearLayout>(R.id.delete)
        fileTextView!!.text = fileName

        //Set listeners
        share!!.setOnClickListener { view: View? ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            val file = FileProvider.getUriForFile(
                    requireContext(),
                    requireActivity().applicationContext
                            .packageName + ".provider", File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + getString(R.string.app_documents_folder), fileName))
            sendIntent.putExtra(Intent.EXTRA_STREAM, file)
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
            bottomSheetDialog.dismiss()
        }
        delete!!.setOnClickListener { view: View? ->
            showDeleteFileDialog(fileName)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun clearSelection() {
        isSelecting = false
        selected!!.clear()
        focusView!!.visibility = View.INVISIBLE
        clearSelection!!.visibility = View.INVISIBLE
        deleteSelection!!.visibility = View.INVISIBLE
        shareSelection!!.visibility = View.INVISIBLE
        //Rimuovere colore di background a tutti i file
        linLayouts!!.forEach(Consumer { lay: LinearLayout -> lay.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.design_default_color_background)) })
        showFileOptions()
    }

    private fun deleteSelection() {
        for (file in selected!!) {
            deleteFile(file)
        }
        clearSelection()
    }

    private fun shareSelection() {
        val uris = ArrayList<Uri>()
        for (file in selected!!) {
            val f = FileProvider.getUriForFile(
                    requireContext(),
                    requireActivity().applicationContext
                            .packageName + ".provider", File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + getString(R.string.app_documents_folder), file))
            uris.add(f)
        }
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND_MULTIPLE
        sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        sendIntent.type = "text/plain"
        clearSelection()
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun deleteFile(fileName: String?) {
        val ll = requireView().findViewWithTag<LinearLayout>(fileName + "_layout")
        val divider = requireView().findViewWithTag<View>(fileName + "_divider")
        if (File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + getString(R.string.app_documents_folder), fileName).delete()) {
            layout!!.removeView(ll)
            layout!!.removeView(divider)
            linLayouts!!.remove(ll)
            for (imgBtn in imgBtns!!) {
                if (imgBtn.tag.toString().equals(fileName + "_imgBtn", ignoreCase = true)) {
                    imgBtns!!.remove(imgBtn)
                    break
                }
            }
        } else {
            Snackbar.make(requireView(), "Error deleting the file", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showDeleteSelectedFilesDialog() { //Called on long click upon a file name
        AlertDialog.Builder(context)
                .setTitle("Delete files")
                .setMessage("Are you sure you want to delete the selected files?")
                .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int -> deleteSelection() } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }
}