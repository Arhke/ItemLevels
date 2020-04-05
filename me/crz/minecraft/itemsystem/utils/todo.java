package me.crz.minecraft.itemsystem.utils;

@interface Todo {

public enum Priority {LOW, MEDIUM, HIGH}

public enum Status {STARTED, NOT_STARTED}

String author() default "Yash";

Priority priority() default Priority.LOW;

Status status() default Status.NOT_STARTED;

}
