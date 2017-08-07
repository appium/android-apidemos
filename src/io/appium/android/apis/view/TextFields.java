/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appium.android.apis.view;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import io.appium.android.apis.R;

import java.lang.CharSequence;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.EditText;


/**
 * A gallery of basic controls: Button, EditText, RadioButton, Checkbox,
 * Spinner. This example uses the light theme.
 */
public class TextFields extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_fields);

        EditText passwordInput = (EditText)findViewById(R.id.edit1);
        final TextView passwordOutput = (TextView)findViewById(R.id.edit1Text);
        passwordInput.addTextChangedListener(new TextWatcher () {
            @Override
            public void afterTextChanged (Editable s) {}

            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {}

            // We only care about the text as it is entered, so only watching
            // for text change. The rest of the watcher functions can be ignored
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                passwordOutput.setText(s);
            }
        });
    }
}
