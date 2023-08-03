package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

    public class DFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            TextView date;
            TextView url;
            TextView title;
            Button alldone;

            View view = inflater.inflate(R.layout.dfragment,
                    container, false);


            alldone= view.findViewById(R.id.detailsButton);
            alldone.setOnClickListener(click -> {
                Intent dateIntent = new Intent(getActivity(), DatePick.class);
                startActivity(dateIntent);
            });

            date = view.findViewById(R.id.fragmentDate);
            url = view.findViewById(R.id.fragmentUrl);
            title = view.findViewById(R.id.fragmentTitle);

            Bundle b = getArguments();
            String imageDate = b.getString("DATE");
            String imageUrl = b.getString("URL");
            String imageTitle = b.getString("TITLE");

            date.setText(imageDate);
            title.setText(imageTitle);

            SpannableString spannableString = new SpannableString(imageUrl);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            };
            spannableString.setSpan(clickableSpan, 0, imageUrl.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            url.setText(spannableString);
            url.setMovementMethod(LinkMovementMethod.getInstance());

            return view;
        }


    }

