package com.github.mattiadellepiane.gnssraw.ui.main.tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mattiadellepiane.gnssraw.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout layout;

    public FilesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FilesFragment newInstance(/*String param1, String param2*/) {
        /*FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;*/
        return new FilesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_files, container, false);

        layout = fragment.findViewById(R.id.filesLinearLayout);

        List<File> files = new ArrayList<>(Arrays.asList(getFiles()));
        Collections.sort(files, Comparator.comparing(File::lastModified));
        Collections.sort(files, Collections.reverseOrder());

        for (File p : files) {
            TextView v = new TextView(getContext());
            v.setText(p.getName());
            v.setPadding(0, 50, 0, 50);
            View divider = new View(getContext());

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1);

            divider.setBackgroundColor(getResources().getColor(R.color.dividerColor, getContext().getTheme()));
            divider.setLayoutParams(lp);
            v.setOnClickListener(view ->{
                openFile(p.getName());
            });
            layout.addView(v);
            layout.addView(divider);
        }

        return fragment;
    }

    public File[] getFiles()
    {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + getString(R.string.app_documents_folder));
        return directory.listFiles(File::isFile);
    }

    public void openFile(String filename) {
        //Uri uri = Uri.fromFile(new File(getString(R.string.app_documents_folder), filename));

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
       // intent.setDataAndType(uri, "text/plain");
        //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Uri file = FileProvider.getUriForFile(
                getContext(),
                getActivity().getApplicationContext()
                        .getPackageName() + ".provider", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + getString(R.string.app_documents_folder), filename));
        intent.setDataAndType(file, "text/plain");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }

    public void addFileView(String currentFileName) {
        TextView v = new TextView(getContext());
        v.setText(currentFileName);
        v.setPadding(0, 50, 0, 50);
        View divider = new View(getContext());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);

        divider.setBackgroundColor(getResources().getColor(R.color.dividerColor, getContext().getTheme()));
        divider.setLayoutParams(lp);
        v.setOnClickListener(view ->{
            openFile(currentFileName);
        });
        layout.addView(divider, 0);
        layout.addView(v, 0);
    }
}