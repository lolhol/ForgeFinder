package com.finder.ct;

public enum CTPortCommMsgType {
  READY("ready"),
  START_PATHING_POS("start_pathing_pos"),
  START_PATHING_BLOCK("start_pathing_block"),
  ABORT_PATHING("abort_pathing"),
  GET_PATHING_PROGRESS("get_pathing_progress"),
  GET_PATH("get_path"),
  UNREADABLE("");

  public final String msg;

  CTPortCommMsgType(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return msg;
  }
}
