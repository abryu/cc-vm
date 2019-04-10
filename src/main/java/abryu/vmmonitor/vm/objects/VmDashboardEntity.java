package abryu.vmmonitor.vm.objects;

import java.sql.Timestamp;
import java.util.List;

public class VmDashboardEntity {

  public VmDashboardEntity(VM vm, List<UsageHistory> histories) {
    this.vm = vm;
    this.histories = histories;
    calculateTotalBalance();
  }

  public VM getVm() {
    return vm;
  }

  public void setVm(VM vm) {
    this.vm = vm;
  }

  public List<UsageHistory> getHistories() {
    return histories;
  }

  public void setHistories(List<UsageHistory> histories) {
    this.histories = histories;
  }

  private VM vm;
  private List<UsageHistory> histories;

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

    sb.append("The VM id is " + vm.getVmId());
    sb.append("\nThe VM status is " + vm.getStatus());
    sb.append("\nThe VM type is " + vm.getVmType());
    sb.append("\nThe VM balance is " + vm.getBalance());

    sb.append("\nFor each usage history , \n");

    if (this.histories == null)
      return sb.toString();

    for (UsageHistory e : this.histories) {
      sb.append(e.toString());
      sb.append("\n");
    }

    return sb.toString();

  }

  private void calculateTotalBalance() {

    UsageHistory latest = this.histories.get(0);
    double total = 0.0;

    if (latest.getAction().equals(Constants.ACTION_START)) {
      total += latest.getBalance();
      total += Constants.calculateCost(latest, new Timestamp(System.currentTimeMillis()));
      this.vm.setBalance(Constants.round(total, 2));
      return;
    } else {
      this.vm.setBalance(Constants.round(latest.getBalance(), 2));
    }

  }

}
