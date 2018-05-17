package ga.jarza.hundbot.main.commands;

import java.time.Instant;

public class AfkObject {

  public Instant ldt;
  public String t;

  public AfkObject(Instant ldt, String t){
    this.ldt = ldt;
    this.t = t;
  }
}
