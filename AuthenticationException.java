package org.example;

public class AuthenticationException extends Exception{
    @java.io.Serial
    private static final long serialVersionUID = -1L;

    public AuthenticationException(){
        super();
    }

    public AuthenticationException(String message){
        super(message);
    }
}
