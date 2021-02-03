package com.wei.productivity.exception;

public class TimeBlockNotExistException extends Exception {
    public TimeBlockNotExistException(String block_id) {
        String message = String.format("Time Block of id %s not exist", block_id);
    }
}
