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

import android.app.Activity;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TextView;

public class CustomPicker1 extends Activity {

    private NumberPicker numberPicker1;
    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_picker_1);
        numberPicker1= (NumberPicker) findViewById(R.id.numberPicker1);
        textView1 = (TextView) findViewById(R.id.textView1);
        final String values[] = { "परीक्षण", "测试", "テスト", "kupima", "การทดสอบ" };

        numberPicker1.setMinValue(0);
        numberPicker1.setMaxValue(values.length - 1);
        numberPicker1.setDisplayedValues(values);

        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                textView1.setText("Value: " + values[newVal]);
            }
        };

        numberPicker1.setOnValueChangedListener(myValChangedListener);
    }
}
