package com.coldkitchen.calcula;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int spinnerPos;
    private boolean Checker;
    private ArrayList<String> actions = new ArrayList<>();
    private CharSequence formulaSet;
    private boolean formulaSetChecker = false;
    private boolean thereIsEq = false;

    //A method for save the state of Formulas Spinner:
    //Метод для сохранения текущего состояния спиннера с формулами:
    private void saveArrayList(ArrayList<String> list) {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder sb = new StringBuilder();
        for (String s : list) sb.append(s).append("<s>");
        sb.delete(sb.length() - 3, sb.length());
        editor.putString("Formulas", sb.toString()).apply();
    }

    //A method for save the state of values for Formulas according to names in the Spinner:
    //Метод для сохранения текущего состояния значений формул,
    //в соответствии с их именами в спиннере:
    private void saveValue(String name, CharSequence value) {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder sb = new StringBuilder();
        sb.append(value).append("<s>");
        sb.delete(sb.length() - 3, sb.length());
        editor.putString(name, sb.toString()).apply();
    }

    //A method for load the state of Formulas Spinner:
    //Метод для загрузки состояния спиннера с формулами:
    private ArrayList<String> loadArrayList() {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String[] strings = prefs.getString("Formulas", "").split("<s>");
        return new ArrayList<>(Arrays.asList(strings));
    }

    //A method for load the state of Formulas values:
    //Метод для загрузки состояния значений для формул:
    private String loadValue(String name) {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return prefs.getString(name, "");
    }

    //A method showing new dialog window for a new formula creation:
    //Метод для создания нового всплывающего диалогового окна с созданием новой формулы:
    private void dialogInputSave(final CharSequence val){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New formula");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Adding a new named row to the Spinner:
                //Добавление в спиннер новой строги:
                actions.add(input.getText().toString());

                //Saving our Spinner immediately after adding new row:
                //Сохранение спиннера непосредственно после добавления в него новой строки:
                saveArrayList(actions);
                if (formulaSetChecker)

                    //If formula added in Formula Set - save it, otherwise save val,
                    //which  is a text from our main TextView:
                    //Если формула добавлена в переменную Formula Set - сохранить её оттуда,
                    //в противном случае - сохранить переменную val, в которую возвращён текст
                    //из основного поля TextView:
                    saveValue(input.getText().toString(), formulaSet);
                else
                    saveValue(input.getText().toString(), val);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //A method showing new dialog window with an image and link to a site by the button:
    //Метод для создания нового всплывающего диалогового окна с изображением и ссылкой на сайт
    //по нажатию кнопки:
    private void breadWindow(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);

        builder.setPositiveButton("There you go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://taplink.cc/redredstripe"));
                startActivity(intent);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.go_pro_dialog_layout, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                ImageView image = dialog.findViewById(R.id.goProDialogImage);
                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.pix_bread_im);
                assert image != null;
                float imageWidthInPX = (float)image.getWidth();

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                        Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
                image.setLayoutParams(layoutParams);
            }
        });
    }

    //A method needed for the Result button (=). Checking if there is a digit in the end of
    //input string so we can start clean calculations, also, for erase operation, checking for
    //remaining '=' symbol:
    //Метод необходимый для кнопки получения результата (=). Выполняет проверку, является ли
    //последний символ строки, взятой из основного поля ввода, цифрой, чтобы вычысления
    //прошли успешно. Также, для кнопки удаления символов, производится проверка на наличие в
    //строке символа '=':
    private boolean fieldDigitCheck(TextView inField){
        String fieldText = inField.getText().toString();
        boolean isThereDig = false;

        for (int i = 0; i < fieldText.length(); i++){
            if (fieldText.charAt(i) != '='){
                if (Character.isDigit(fieldText.charAt(fieldText.length()-1))) {

                    //there is a digit in the end,
                    //символ в конце - цифра,
                    isThereDig = true;

                    //there is no '=' symbol yet,
                    //символа '=' до сих пор не обнаружено,
                    thereIsEq = false;

                    //and we can push our buttons
                    //и мы можем нажимать на кнопки калькулятора
                    Checker = true;
                }
            } else {
                isThereDig = false;
                thereIsEq = true;
                Checker = false;


                //'=' founded and we can not do calculations yet
                //'=' найден в строке - вычисления производить пока нельзя
                break;
            }}

        return isThereDig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Loading Spinner formulas state, if this is a first run - adding to the Spinner new rows:
        //Загрузка состояния формул в спиннере, если это первый запуск - заполнение спиннера
        //новыми полями:
        actions = loadArrayList();
        if (actions.size()<=1) {
            //Empty element for none selection:
            //Пустой элемент для состояния покоя:
            actions.add(0,"");
            //Formulas save element:
            //Элемент для сохранения новых формул:
            actions.add(1,"Save...");
            actions.remove(2);
        }
        final Spinner spinner = findViewById(R.id.actions);
        final CustomAdapter adapter = new CustomAdapter(this, android.R.layout.simple_spinner_item, actions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Formulas");

        final TextView field = findViewById(R.id.inputWindow);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);
        Button btn0 = findViewById(R.id.btn0);
        Button btnPlus = findViewById(R.id.ButPlus);
        Button btnMin = findViewById(R.id.ButMin);
        Button btnMult = findViewById(R.id.ButMult);
        Button btnDiv = findViewById(R.id.ButDiv);
        Button result = findViewById(R.id.ButResult);
        Button erase = findViewById(R.id.butErase);
        final Button bread = findViewById(R.id.bread);
        final Button formulaDelete = findViewById(R.id.btnDelete);
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        field.setSelected(true);
        field.setMovementMethod(new ScrollingMovementMethod());


        btn1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                //Setting the Spinner to the empty position [0] to let user choose any row again
                //if already selected one:
                //Установка спиннера в пустую позицию [0] для возможности повторного выбора любых
                //строк спиннера:
                spinner.setSelection(0);
                view.startAnimation(animAlpha);
                if (Checker && !thereIsEq)
                    field.setText(field.getText() + "1");
                else {
                    field.setText("1");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                view.startAnimation(animAlpha);
                if (Checker && !thereIsEq)
                    field.setText(field.getText() + "2");
                else {
                    field.setText("2");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                view.startAnimation(animAlpha);
                if (Checker && !thereIsEq)
                    field.setText(field.getText() + "3");
                else {
                    field.setText("3");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                view.startAnimation(animAlpha);
                if (Checker && !thereIsEq)
                    field.setText(field.getText() + "4");
                else {
                    field.setText("4");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                view.startAnimation(animAlpha);
                if (Checker && !thereIsEq)
                    field.setText(field.getText() + "5");
                else {
                    field.setText("5");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                view.startAnimation(animAlpha);
                if (Checker && !thereIsEq)
                    field.setText(field.getText() + "6");
                else {
                    field.setText("6");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                view.startAnimation(animAlpha);
                if (Checker && !thereIsEq)
                    field.setText(field.getText() + "7");
                else {
                    field.setText("7");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                view.startAnimation(animAlpha);
                if (Checker && !thereIsEq)
                    field.setText(field.getText() + "8");
                else {
                    field.setText("8");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                view.startAnimation(animAlpha);
                if (Checker && !thereIsEq)
                    field.setText(field.getText() + "9");
                else {
                    field.setText("9");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btn0.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                view.startAnimation(animAlpha);
                if (Checker && !thereIsEq)
                    field.setText(field.getText() + "0");
                else {
                    field.setText("0");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                if (Checker && !thereIsEq) {
                    if (fieldDigitCheck(field))
                        field.setText(field.getText() + "+");}
                else {
                    field.setText("");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btnMin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                if (Checker && !thereIsEq) {
                    if (fieldDigitCheck(field))
                        field.setText(field.getText() + "-");}
                else {
                    field.setText("");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btnMult.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                if (Checker && !thereIsEq) {
                    if (fieldDigitCheck(field))
                        field.setText(field.getText() + "*");}
                else {
                    field.setText("");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        btnDiv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                if (Checker && !thereIsEq) {
                    if (fieldDigitCheck(field))
                        field.setText(field.getText() + "/");}
                else {
                    field.setText("");
                    Checker = true;
                    thereIsEq = false;
                    formulaSetChecker = false;
                }
                bread.setVisibility(View.INVISIBLE);
            }
        });

        //Erasing by cutting original string from TextView:
        //Стирание символов путём обрезки исходной строки из основного поля ввода TextView:
        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                spinner.setSelection(0);
                    String text = field.getText().toString();
                    if (text.length() > 0)
                    field.setText(text.substring(0, text.length() - 1));
                fieldDigitCheck(field);
                bread.setVisibility(View.INVISIBLE);
            }
        });

        //If final string in our TextView is clear for our Calculations - do the work
        //and output it with '=':
        //Если текущая строка в TextView проходит проверку и готова к вычислениям - выполняем и
        //выводим в неё же результат с добавлением символа '=':
        result.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                spinner.setSelection(0);
                formulaDelete.setVisibility(View.INVISIBLE);
                view.startAnimation(animAlpha);
                if (fieldDigitCheck(field) && Checker) {

                    //Saving original text if User will save this formula after we change it:
                    //Сохранение текущей строки в переменную formulaSet, чтобы воспользоваться ею
                    //если пользователь решит сохранить вормулу после выведения в строку результата:
                    formulaSet = field.getText();
                    formulaSetChecker = true;

                    //Making main calculations and outputting the result after ' = ':
                    //Выполнение вычислений и вывод результата с добавлением '=':
                    field.setText(field.getText() + " = " + Execution.MakeMagic(field.getText().toString()));
                    Checker = false;

                    //Saving result to the clipboard:
                    //Сохранение результата в буфер обмена:
                    ClipData clip = ClipData.newPlainText("Result", field.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Copied to clipboard!", Toast.LENGTH_SHORT);
                    toast.show();
                    bread.setVisibility(View.VISIBLE);
                }
                else{
                    Checker = true;
                }
            }
        });

        formulaDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actions.remove(spinnerPos);
                formulaDelete.setVisibility(View.INVISIBLE);
                saveArrayList(actions);
                field.setText("");
            }
        });

        bread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               breadWindow();
            }
        });

        //Spinner for Formulas:
        //Спиннер для формул:
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0){
                        formulaDelete.setVisibility(View.INVISIBLE);
                    }
                    else if (position == 1) {
                        formulaDelete.setVisibility(View.INVISIBLE);
                        spinner.setSelection(0);

                        //Updating Spinner after making manually changes:
                        //Обновление спиннера после внесения изменений:
                        adapter.getView(position, view, parent);
                        dialogInputSave(field.getText());
                    }
                    else {
                        spinnerPos = position;
                        formulaDelete.setVisibility(View.VISIBLE);
                        field.setText(loadValue(parent.getSelectedItem().toString()));
                        adapter.getView(position, view, parent);
                        bread.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
        });
    }
}