package com.finder.calculator.errors;

public class NoPathException extends Exception {

  public String msg;

  public NoPathException(String msg) {
    super(msg);
    this.msg = msg;
  }
}
