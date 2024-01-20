package com.finder.calculator.errors;

public class NoPathException extends Exception {

  public String msg;
  public int nodesConsidered = 0;

  public NoPathException(String msg, int nodesConsidered) {
    super(msg);
    this.msg = msg;
    this.nodesConsidered = nodesConsidered;
  }
}
