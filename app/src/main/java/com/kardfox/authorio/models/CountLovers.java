package com.kardfox.authorio.models;

public class CountLovers {
    public UserModel[] lovers;
    public int subscribe;
    public int mirror;

    @Override
    public String toString() {
        return String.format("CountLovers(%d, ..., %s)", lovers.length, subscribe > 0);
    }
}
