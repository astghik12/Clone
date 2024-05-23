package org.example;

import clone.CloneUtil;
import entity.Man;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final CloneUtil cloneUtil = new CloneUtil();

        // Test deep copying of primitive types
        int primitiveInt = 42;
        int copiedInt = cloneUtil.cloneObject(primitiveInt);
        System.out.println("Primitive int: " + primitiveInt);
        System.out.println("Copied int: " + copiedInt);

        // Test deep copying of wrapper types
        Integer wrapperInteger = 42;
        Integer copiedWrapperInteger = cloneUtil.cloneObject(wrapperInteger);
        System.out.println("Wrapper Integer: " + wrapperInteger);
        System.out.println("Copied Wrapper Integer: " + copiedWrapperInteger);

        // Test deep copying of custom objects
        List<String> books = new ArrayList<>(Arrays.asList("Book 1", "Book 2", "Book 3"));
        Man originalMan = new Man("John", 30, books);
        Man copiedMan = cloneUtil.cloneObject(originalMan);
        System.out.println("Original Man: " + originalMan.getName() + ", " + originalMan.getAge() + ", " + originalMan.getFavoriteBooks());
        System.out.println("Copied Man: " + copiedMan.getName() + ", " + copiedMan.getAge() + ", " + copiedMan.getFavoriteBooks());

        // Modify the copied object to ensure deep copying
        copiedMan.setName("Peter");
        copiedMan.setAge(40);
        copiedMan.getFavoriteBooks().add("Book 4");

        // Verify that modifications to the copied object do not affect the original
        System.out.println("Original Man after modification: " + originalMan.getName() + ", " + originalMan.getAge() + ", " + originalMan.getFavoriteBooks());
        System.out.println("Copied Man after modification: " + copiedMan.getName() + ", " + copiedMan.getAge() + ", " + copiedMan.getFavoriteBooks());
    }
}