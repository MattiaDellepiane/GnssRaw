package com.github.mattiadellepiane.gnssraw.ui.main.tabs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilesFragment extends Fragment {

    private LinearLayout layout;

    public FilesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_files, container, false);

        layout = fragment.findViewById(R.id.filesLinearLayout);
        File[] f = getFiles();
        if(f != null) {
            List<File> files = new ArrayList<>(Arrays.asList(f));
            Collections.sort(files, Comparator.comparing(File::lastModified));
            files.forEach(p -> addFileView(p.getName()));
        }
        SharedData.getInstance().setFilesFragment(this);
        return fragment;
    }

    public File[] getFiles()
    {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + getString(R.string.app_documents_folder));
        return directory.listFiles(File::isFile);
    }

    public void openFile(String filename) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri file = FileProvider.getUriForFile(
                getContext(),
                getActivity().getApplicationContext()
                        .getPackageName() + ".provider", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + getString(R.string.app_documents_folder), filename));
        intent.setDataAndType(file, "text/plain");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }

    public void addFileView(String fileName) {
        //Linear layout containing name of file and button for options
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setGravity(Gravity.CENTER);
        TextView v = new TextView(getContext());
        v.setText(fileName);
        TypedValue selectableItemBackground = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, selectableItemBackground, true);
        v.setBackgroundResource(selectableItemBackground.resourceId);
        v.setClickable(true);
        v.setPadding(0, 70, 0, 70);
        //Divide file views with a line
        View divider = new View(getContext());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 5);

        divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dividerColor));
        divider.setLayoutParams(lp);
        v.setOnClickListener(view ->{
            openFile(fileName);
        });
        v.setOnLongClickListener(view -> {
            showDeleteFileDialog(fileName);
            return true;
        });
        //More vert icon - file options
        ImageButton imgBtn = new ImageButton(getContext());
        imgBtn.setImageResource(R.drawable.file_options);
        imgBtn.setPadding(50,50,50,50);
        imgBtn.setBackgroundResource(selectableItemBackground.resourceId);
        imgBtn.setOnClickListener(view -> {
            showFileOptions(fileName);
        });
        //Set tags used for removing views when the user deletes a file
        linearLayout.setTag(fileName + "_layout");
        divider.setTag(fileName + "_divider");

        //Add the previous created views to the layout
        linearLayout.addView(imgBtn);
        linearLayout.addView(v, 0);
        layout.addView(divider, 0);
        layout.addView(linearLayout, 0);
    }

    private void showDeleteFileDialog(String fileName) { //Called on long click upon a file name
        new AlertDialog.Builder(getContext())
                .setTitle("Delete file")
                .setMessage("Are you sure you want to delete '" + fileName + "'?")

                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteFile(fileName);
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showFileOptions(String fileName){ //Show bottom sheet file options
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_fileoptions);

        TextView fileTextView = bottomSheetDialog.findViewById(R.id.filename_placeholder);
        LinearLayout share = bottomSheetDialog.findViewById(R.id.share);
        LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);
        fileTextView.setText(fileName);

        //Set listeners
        share.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            Uri file = FileProvider.getUriForFile(
                    getContext(),
                    getActivity().getApplicationContext()
                            .getPackageName() + ".provider", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + getString(R.string.app_documents_folder), fileName));
            sendIntent.putExtra(Intent.EXTRA_STREAM, file);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            bottomSheetDialog.dismiss();
        });

        delete.setOnClickListener(view -> {
            showDeleteFileDialog(fileName);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void deleteFile(String fileName){
        LinearLayout ll = getView().findViewWithTag(fileName + "_layout");
        View divider = getView().findViewWithTag(fileName + "_divider");
        if(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + getString(R.string.app_documents_folder), fileName).delete()) {
            layout.removeView(ll);
            layout.removeView(divider);
        }
        else{
            Snackbar.make(this.getView(), "Error deleting the file", Snackbar.LENGTH_LONG).show();
        }
    }
}