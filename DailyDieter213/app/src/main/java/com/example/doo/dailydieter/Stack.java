package com.example.doo.dailydieter;

public class Stack{

    Object[] stack; //스택의 본체

    int stackSize; //스택의 크기

    int sp;   //스택 포인터



    static int DEFAULT_STACK_SIZE = 100; //기본 스택의 크기



    /**

     * 스택을 생성한다(크기는 DEFAULT_STACK_SIZE )

     */

    public Stack(){ //기본 스택

        this(DEFAULT_STACK_SIZE);

    }





    /**

     * 크기를 지정하여 스택 생성

     * @param size 스택의 크기

     */



    public Stack(int size) {

        stack = new Object[size];

        stackSize = size;

        sp = 0;

    }



    /*

     * 에러 처리

     * 메시지 s 를 표시하고 프로그렘을 종료시킨다.

     */

    private void error(String s){

        System.err.println(s);

        System.exit(1);

    }



    /*

     * 스택의 내용을 모두 버린다.

     */

    public void clear(){

        sp = 0; //스택 포인터를 0으로 한다.

    }



    /*

     * 스택에 데이터를 쌓는다

     *

     * @param x 쌓을 데이터

     */



    public void push(Object x){

        if(sp >= stackSize){

            error("Stack overflow");

        }

        stack[sp++] = x;

    }



    /*

     * 스택에서 데이터를 꺼낸다

     *

     * @return 스택에서 꺼낸 데이터

     */

    public Object pop(){

        if(sp <= 0){

            error("Stadck underflow");

        }

        return stack[--sp];

    }



    /*

     * 스택이 비어있는지 조사한다.

     *

     * @return 비어 있다면 true, 비어있지 않다면 false를 반환

     */

    public boolean isEmpty(){

        return sp == 0;

    }



    /*

     * 스택의 내용을 문자열로 반환

     *

     * @return 스택의 내용

     */

    public String toString(){

        String s;

        s = "MyStack=[";



        for(int i = 0; i < sp; i++){

            s = s + stack[i];

            if(i < sp - 1){

                s = s + ",";

            }

        }

        s = s + "]";

        return s;



    }



}