package com.example.odootest.viewmodel;

public abstract class LoginResult {
    public static class Success extends LoginResult {
        public int uid;
        public Success(int uid) { this.uid = uid; }
    }
    public static class Error extends LoginResult {
        public String message;
        public Error(String message) { this.message = message; }
    }
}