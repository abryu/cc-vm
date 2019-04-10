package abryu.vmmonitor.vm.objects;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "vm")
public class VM {

  public VM(String owner, String vmType) {
    this.owner = owner;
    this.balance = 0;
    this.vmType = vmType;
    this.status = Constants.STATUS_CREATED;
  }

  public Long getVmId() {
    return vmId;
  }

  public void setVmId(Long vmId) {
    this.vmId = vmId;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getVmType() {
    return vmType;
  }

  public void setVmType(String vmType) {
    this.vmType = vmType;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }


  @Id
  private Long vmId;
  private String owner;
  private String vmType;
  private double balance;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  private String status;


}
