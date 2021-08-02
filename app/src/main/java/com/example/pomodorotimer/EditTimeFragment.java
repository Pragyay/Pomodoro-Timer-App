package com.example.pomodorotimer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditTimeFragment extends Fragment {

    Button applyButton;
    EditText editBreakTime;
    EditText editPomodoroTime;

    int newPomodoroTime,newBreakTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_time, container, false);

        applyButton = view.findViewById(R.id.button2);
        editBreakTime = view.findViewById(R.id.editBreakTime);
        editPomodoroTime = view.findViewById(R.id.editPomodoroTime);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String PomodoroTime = editPomodoroTime.getText().toString();
                String BreakTime = editBreakTime.getText().toString();

                newPomodoroTime = Integer.parseInt(PomodoroTime) * 60;
                newBreakTime = Integer.parseInt(BreakTime) * 60;

                if(Integer.parseInt(PomodoroTime) >60 | Integer.parseInt(BreakTime) >60 ){
                    Toast.makeText(getContext(),"Enter time less than 60 minutes",Toast.LENGTH_SHORT).show();

                }else if(PomodoroTime.matches("") | BreakTime.matches("")) {
                    Toast.makeText(getContext(),"Enter time",Toast.LENGTH_SHORT).show();

                //if one field empty and one > 60, app crashes.

                }else{
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("pomodoroTime",newPomodoroTime);
                    bundle.putInt("breakTime",newBreakTime);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            }
        });

        return view;


    }
}