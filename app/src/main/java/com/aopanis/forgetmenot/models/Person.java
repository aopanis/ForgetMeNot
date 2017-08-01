package com.aopanis.forgetmenot.models;

import com.tzutalin.dlib.VisionDetRet;

import java.util.List;

/**
 * Created by aopan on 7/10/2017.
 */

public class Person {

    private String name;
    private List<VisionDetRet> face;

    public Person(List<VisionDetRet> face, String name){
        this.name = name;
        this.face = face;
    }


}
