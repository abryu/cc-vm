package abryu.vmmonitor.vm.objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;


@Entity(name = "history")
public class UsageHistory {

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAssociatedVmId() {

    return associatedVmId;
  }

  public void setAssociatedVmId(Long associatedVmId) {
    this.associatedVmId = associatedVmId;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public String getVmType() {
    return vmType;
  }

  public void setVmType(String vmType) {
    this.vmType = vmType;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public UsageHistory(Long associatedVmId, Timestamp timestamp, String vmType, String action) {
    this.associatedVmId = associatedVmId;
    this.timestamp = timestamp;
    this.vmType = vmType;
    this.action = action;
    this.balance = 0;
  }

  @Id
  private Long id;

  private Long associatedVmId;

  private Timestamp timestamp;

  private String vmType;

  private String action;

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  private double balance;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.associatedVmId + "," + timestamp + "," + vmType + "," + action + "," + balance);
    return sb.toString();
  }

}
