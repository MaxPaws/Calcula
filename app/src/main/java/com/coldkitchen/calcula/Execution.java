package com.coldkitchen.calcula;
import android.app.Activity;

import java.math.*;
import java.util.Scanner;
import java.util.Arrays;

public class Execution extends Activity {
    //method for '^' calculations:
    private static long[] Powr (long Digits[], Character Signs[]){

        //for counting, which numbers from Digits array to add/subtract in each step in	Signs array:
        long count=0;

        for (long i=0; i<Signs.length; i++)
        {

            //if current char in Signs array is not a digit - I can	do operations that this	sign means,
            // replacing empty spaces in array with zeros, after it:
            if(!Character.isDigit(Signs[(int) i])){
                count++;

                if (Signs[(int) i]=='^'){
                    Digits[(int) (count-1)] = (int)Math.pow(Digits[(int) (count-1)], Digits[(int) count]);
                    Digits[(int) count]=0;
                }
            }
        }
        return Digits;
    }

    //method for '*' and '/' calculations:
    private static long[] MulDiv (long Digits[], Character Signs[]){

        //for counting, wich numbers from Digits array to add/ subtract in each step in Signs array:
        long count=0;

        for (long i=0; i<Signs.length; i++)
        {
            //if current char in Signs array is not a digit - I can do operations
            // that this sign means, replacing empty spaces in array with zeros, after it:
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
        return Digits;
    }

    //method for '+' and '-' calculations:
    private static long AddSub (long Digits[], Character Signs[]){

        //for counting, which numbers from Digits array to add/subtract in each step in Signs array:
        long count=0;

        //add first element of all numbers to the result variable, to do next operations basing on it:
        long result = Digits[0];
        System.out.println("result:"+result);

        for (int i=0; i<Signs.length; i++)
        {
            //if current char in Signs array is not a digit - I can do operations
            //that this sign means, adding a number next to it to the result variable:
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
    private static long[] ZeroSort (long Arr[]) {

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
        return Arr;
    }

    public static long MakeMagic(String vvod){

        if (vvod.length() >= Long.MAX_VALUE)
            return 0;
        else {

            //////////public static void main(String[] args) {

            long result = 0;

            ///////////Scanner sc = new Scanner(System.in);

            //take input into a String, to understand size of future array:
            //////////String vvod;
        /*try {
            vvod = sc.nextLine();
            vvod.replaceAll("\\s","");
        }
        catch (Exception e){
            System.out.println("I need a valid input!\n");
            vvod="0";
        }*/

            //new empty array for a digits:
            long arr[] = new long[vvod.length()];

            //variable for counting index:
            long arrIndex = 0;

            //new empty array for +-*/ signs:
            Character chararr[] = new Character[vvod.length()];
            long charrIndex = 0;

            //for "(...)" elements:
            boolean youcanttouchthis = false;


            //filling the empty array(arr) by numbers from input string: asking if there is a digit,
            //then punch characters (digits) into one string until it ends with some other char, to
            // understand that this is all Number that I can add it to 	my empty array, and,
            // at the same time, filling empty array of notDigits(chararr) with -,+,* and /:

            String temp = "0";

            for (long i = 0; i < arr.length; i++) {
                if (Character.isDigit(vvod.charAt((int) i))) {
                    temp += vvod.charAt((int) i);

                    //in empty spaces - filling Character array with 0 to avoid 'Null's:
                    chararr[(int) charrIndex] = '0';
                    charrIndex++;
                } else if (Character.isLetter(vvod.charAt((int) i))) {
                    chararr[(int) charrIndex] = '0';
                    charrIndex++;
                } else if (vvod.charAt((int) i) == '(') {
                    youcanttouchthis = true;
                    break;
                } else {
                    arr[(int) arrIndex] = Long.valueOf(temp);
                    arrIndex++;
                    temp = "";
                    chararr[(int) charrIndex] = vvod.charAt((int) i);
                    charrIndex++;
                }
            }

            if (youcanttouchthis)
                System.out.println("Avoid this '()' elements for now, I hope you can do it on your own.");
            else {

                //the last digit I got - is still in the "temp"	variable,
                //so I need to add it in array of digits:
                arr[(int) arrIndex] = Long.valueOf(temp);

                System.out.println("Array with digits from input: " + Arrays.toString(arr));

                System.out.println("Array with signs from input: " + Arrays.toString(chararr));

                Powr(arr, chararr);

                //now I can do calculations in right order, searching for only signs that I need.
                // '*' and '/' first:
                MulDiv(arr, chararr);

                //to avoid invalid operations with empty spaces (Zeros),
                //created after "MulDiv", I can place a digits first, in input order,
                //and do calculations with them:
                ZeroSort(arr);

                //finally, the second operation that calculate '+' and '-' in digits array:
                result = AddSub(arr, chararr);

                /////// System.out.println("\nResult: "+result);

            }
        return result;
        }
    }
}