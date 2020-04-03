package com.example.haveyou;

import com.google.firebase.firestore.DocumentSnapshot;

public class list_row_data {

    public String name;
    boolean checked;

    list_row_data(String name, boolean checked) {
        this.name = name;
        this.checked = checked;

    }

    public list_row_data(DocumentSnapshot document) {
    }


    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return checked;
    }
}
