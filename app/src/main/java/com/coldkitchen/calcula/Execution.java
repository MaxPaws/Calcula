package com.coldkitchen.calcula;
import android.app.Activity;
import java.util.Arrays;

public class Execution extends Activity {

    //method for '^' calculations. In current interpretation of calculator I dropped it
    //метод для возведения в степень. В данной интерпретации калькулятора я её реализовывать не стал
    private static void Powr (long[] Digits, Character[] Signs){

        //for putting digits and symbols in their right places:
        //для исключения потери правильности расположения мест цифр/символов:
        long count=0;

        for (long i=0; i<Signs.length; i++)
        {

            //if current char in Signs array is not a digit - I can	do operations that this	sign means,
            // replacing empty spaces in array with zeros, after it:
            //если текущий символ в массиве Signs не является цифрой - его можно применять для вычислений,
            // заменяя пустые места в массиве нулями:
            if(!Character.isDigit(Signs[(int) i])){
                count++;

                if (Signs[(int) i]=='^'){
                    Digits[(int) (count-1)] = (int)Math.pow(Digits[(int) (count-1)], Digits[(int) count]);
                    Digits[(int) count]=0;
                }
            }
        }
    }

    //method for '*' and '/' calculations:
    //метод для умножения/деления:
    private static void MulDiv (long[] Digits, Character[] Signs){

        //for putting digits and symbols in their right places:
        //для исключения потери правильности расположения мест цифр/символов:
        long count=0;

        for (long i=0; i<Signs.length; i++)
        {
            //if current char in Signs array is not a digit - I can do operations
            // that this sign means, replacing empty spaces in array with zeros, after it:
            //если текущий символ в массиве Signs не является цифрой - его можно применять для вычислений,
            // заменяя пустые места в массиве нулями:
            if(!Character.isDigit(Signs[(int) i])){
                count++;

                if (Signs[(int) i]=='*'){
                    Digits[(int) count]*=Digits[(int) (count-1)];
                    Digits[(int) (count-1)]=0;
                }
                else
                if (Signs[(int) i]=='/'){
                    Digits[(int) count]=Digits[(int) (count-1)]/Digits[(int) count];
                    Digits[(int) (count-1)]=0;
                }
            }
        }
    }

    //method for '+' and '-' calculations:
    //метод для сложения/вычитания
    private static long AddSub (long[] Digits, Character[] Signs){

        //for putting digits and symbols in their right places:
        //для исключения потери правильности расположения мест цифр/символов:
        long count=0;

        //add first element of all numbers to the result variable, to do next operations basing on it:
        //добавление первого элемента в массиве чисел в переменную result, чтобы совершать дальнейшие
        //операции основываясь на нём:
        long result = Digits[0];
        System.out.println("result:"+result);

        for (int i=0; i<Signs.length; i++)
        {
            //if current char in Signs array is not a digit - I can do operations
            //that this sign means, adding a number next to it to the result variable:
            //если текущий символ в массиве Signs не является цифрой - его можно применять для вычислений,
            // заменяя пустые места в массиве нулями:
            if(!Character.isDigit(Signs[(int) i])){
                if (Signs[(int) i]=='+'){
                    count++;
                    result+=Digits[(int) count];
                }
                else
                if (Signs[(int) i]=='-'){
                    count++;
                    result-=Digits[(int) count];
                    System.out.println("result after -:"+result);
                }
            }
        }
        return result;
    }

    //method for pick numbers out of zeros among them, to make calculations in correct order:
    //метод для выборки ненужных пустых мест (нулей) из остальных чисел, чтобы установить правильный
    //порядок чисел для дальнейших вычислений:
    private static void ZeroSort (long[] Arr) {

        for (long j=0; j<Arr.length; j++)				 {
            if (Arr[(int) j]==0)
            {
                for (long i=j; i<Arr.length; i++)
                {
                    if (Arr[(int) i]!=0)
                    {
                        Arr[(int) j]=Arr[(int) j]^Arr[(int) i];
                        Arr[(int) i]=Arr[(int) j]^Arr[(int) i];
                        Arr[(int) j]=Arr[(int) j]^Arr[(int) i];
                        break;
                    }}}}
    }

    public static long MakeMagic(String vvod){

        vvod.length();
        long result = 0;

        //new empty array for a digits:
        //новый пустой массив для цифр:
        long[] arr = new long[vvod.length()];

        //variable for counting index:
        //переменная для счёта индекса:
        long arrIndex = 0;

        //new empty array for +-*/ signs:
        //новый пустой массив для символов +/*-:
        Character[] chararr = new Character[vvod.length()];
        long charrIndex = 0;

        StringBuilder temp = new StringBuilder("0");

        for (long i = 0; i < arr.length; i++) {
            if (Character.isDigit(vvod.charAt((int) i))) {
                temp.append(vvod.charAt((int) i));

                //in empty spaces - filling Character array with 0 to avoid 'Null's:
                //в пустых местах - заполняем массив Character нулями, для избежания значений Null:
                chararr[(int) charrIndex] = '0';
                charrIndex++;
            } else if (Character.isLetter(vvod.charAt((int) i))) {
                chararr[(int) charrIndex] = '0';
                charrIndex++;
            } else {
                arr[(int) arrIndex] = Long.parseLong(temp.toString());
                arrIndex++;
                temp = new StringBuilder();
                chararr[(int) charrIndex] = vvod.charAt((int) i);
                charrIndex++;
            }
        }

        //the last digit I got - is still in the "temp"	variable,
        //so I need to add it in array of digits:
        //последняя цифр всё еще в переменной temp, нужно добавить её к общему массиву цифр:
        arr[(int) arrIndex] = Long.parseLong(temp.toString());

        //in current interpretation of calculator there is no pow
        //функцию возведения в степень в данной интерпретации калькулятора я реализовывать не стал
        //Powr(arr, chararr);

        //now I can do calculations in right order, searching for only signs that I need.
        // '*' and '/' first:
        //теперь можно делать вычисления в верном порядке, находя для этого только те символы,
        //что необходимы.
        //для начала умножение и деление:
        MulDiv(arr, chararr);

        //to avoid invalid operations with empty spaces (Zeros),
        //created after "MulDiv", I can place a digits first, in input order,
        //and do calculations with them:
        //воизбежание ошибочных операций с пустыми местами (нулями), разованными после операций
        //умножения/деления, мы можем поместить цифры в начало, в изначальном порядке,
        //и продолжить вычисления с ними:
        ZeroSort(arr);

        //finally, the second operation that calculate '+' and '-' in digits array:
        //наконец, второй этап вычислений, сложение/вычитание, в массиве цифр:
        result = AddSub(arr, chararr);

        return result;
    }
}