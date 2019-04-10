package abryu.vmmonitor.vm.objects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

public class Constants {

  public static final double PRICE_BASIC = 0.05;
  public static final double PRICE_LARGE = 0.1;
  public static final double PRICE_ULTRA = 0.15;

  public static final String STATUS_CREATED = "CREATED";
  public static final String STATUS_RUNNING = "RUNNING";
  public static final String STATUS_DELETED = "DELETED";

  public static final String ACTION_CREATE = "CREATE";
  public static final String ACTION_START = "START";
  public static final String ACTION_STOP = "STOP";
  public static final String ACTION_DELETE = "DELETE";
  public static final String ACTION_UPGRADE = "UPGRADE";
  public static final String ACTION_DOWNGRADE = "DOWNGRADE";

  public static final String VM_TYPE_BASIC = "BASIC";
  public static final String VM_TYPE_LARGE = "LARGE";
  public static final String VM_TYPE_ULTRA = "ULTRA";

  public static Timestamp getCurrentTimeStamp() {
    return new Timestamp(System.currentTimeMillis());
  }

  public static double getCostPerMin(String vmType) {
    if (vmType.equals(VM_TYPE_BASIC))
      return PRICE_BASIC;
    if (vmType.equals(VM_TYPE_LARGE))
      return PRICE_LARGE;
    return PRICE_ULTRA;
  }

  public static double calculateCost(UsageHistory last, UsageHistory thisTime) {
    Timestamp old = last.getTimestamp();
    Timestamp current = thisTime.getTimestamp();
    Long diff = (current.getTime() - old.getTime()) / 60000;
    return diff * Constants.getCostPerMin(last.getVmType());
  }

  public static double calculateCost(UsageHistory last, Timestamp thisTime) {
    Timestamp old = last.getTimestamp();
    Long diff = (thisTime.getTime() - old.getTime()) / 60000;
    return diff * Constants.getCostPerMin(last.getVmType());
  }

  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
}
