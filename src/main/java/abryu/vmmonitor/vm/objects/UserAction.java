package abryu.vmmonitor.vm.objects;

public class UserAction {

  public UserAction() {

  }

  public UserAction(String vmId, String vmOperation, String vmType) {
    if (vmId == null || vmId.length() == 0)
      this.vmId = null;
    else
      this.vmId = Long.valueOf(vmId);
    this.vmOperation = vmOperation;
    this.vmType = vmType;
  }

  public Long getVmId() {

    return vmId;
  }

  public void setVmId(Long vmId) {
    this.vmId = vmId;
  }

  public String getVmOperation() {
    return vmOperation;
  }

  public void setVmOperation(String vmOperation) {
    this.vmOperation = vmOperation;
  }

  public String getVmType() {
    return vmType;
  }

  public void setVmType(String vmType) {
    this.vmType = vmType;
  }

  private Long vmId;
  private String vmOperation;
  private String vmType;
}
