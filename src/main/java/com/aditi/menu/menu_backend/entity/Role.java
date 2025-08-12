package com.aditi.menu.menu_backend.entity;

public class Role {
    public static final int TESTER = 1;
    public static final int USER = 2;
    public static final int ADMIN = 3;

    public static String toString(int role) {
        switch (role) {
            case TESTER:
                return "TESTER";
            case USER:
                return "USER";
            case ADMIN:
                return "ADMIN";
            default:
                throw new IllegalArgumentException("Invalid role value: " + role);
        }
    }

    public static int fromString(String role) {
        switch (role.toUpperCase()) {
            case "TESTER":
                return TESTER;
            case "USER":
                return USER;
            case "ADMIN":
                return ADMIN;
            default:
                throw new IllegalArgumentException("Invalid role string: " + role);
        }
    }
}
