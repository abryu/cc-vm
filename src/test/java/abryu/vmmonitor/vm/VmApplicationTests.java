package abryu.vmmonitor.vm;

import static org.junit.Assert.*;

import abryu.vmmonitor.vm.objects.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VmApplicationTests {

  @Autowired
  private VmRepository vmRepository;
  @Autowired
  private UsageHistoryRepository usageHistoryRepository;

  private String username = "user1";
  private Controller controller = null;
  private UserAction action;

  @Before
  public void setup() {
    controller = new Controller(vmRepository, usageHistoryRepository);
    action = new UserAction("5124827589378048", Constants.ACTION_CREATE, Constants.VM_TYPE_LARGE);
  }

  @Test
  public void testCreate() {

    VmDashboardEntity vmDashboardEntity = controller.create(username, action);
    assertEquals(Constants.STATUS_CREATED, vmDashboardEntity.getVm().getStatus());
    assertEquals(username, vmDashboardEntity.getVm().getOwner());
    assertNotNull(vmDashboardEntity.getVm().getVmId());
    assertEquals(action.getVmType(), vmDashboardEntity.getVm().getVmType());
    assertNotNull(vmDashboardEntity.getHistories());
    System.out.println(vmDashboardEntity.toString());
    action.setVmId(vmDashboardEntity.getVm().getVmId());
  }

  @Test
  public void testStart() {
    VmDashboardEntity vmDashboardEntity = controller.start(username, action);
    assertEquals(Constants.STATUS_RUNNING, vmDashboardEntity.getVm().getStatus());
    assertEquals(username, vmDashboardEntity.getVm().getOwner());
    assertNotNull(vmDashboardEntity.getVm().getVmId());
    //assertEquals(action.getVmType(), vmDashboardEntity.getVm().getVmType());
    assertTrue(vmDashboardEntity.getHistories().size() > 1);
    System.out.println(vmDashboardEntity.toString());
  }

  @Test
  public void testStop() {
    VmDashboardEntity vmDashboardEntity = controller.stop(username, action);
    assertEquals(Constants.STATUS_CREATED, vmDashboardEntity.getVm().getStatus());
    assertEquals(username, vmDashboardEntity.getVm().getOwner());
    assertNotNull(vmDashboardEntity.getVm().getVmId());
    assertTrue(vmDashboardEntity.getHistories().size() > 1);
    System.out.println(vmDashboardEntity.toString());
  }

  @Test
  public void testUpgrade() {
    action.setVmType(Constants.VM_TYPE_LARGE);
    VmDashboardEntity vmDashboardEntity = controller.upgrade(username, action);
    assertEquals(username, vmDashboardEntity.getVm().getOwner());
    assertNotNull(vmDashboardEntity.getVm().getVmId());
    assertEquals(action.getVmType(), vmDashboardEntity.getVm().getVmType());
    assertTrue(vmDashboardEntity.getHistories().size() > 1);
    //assertEquals(Constants.VM_TYPE_LARGE, vmDashboardEntity.getVm().getVmType());
    assertEquals(Constants.STATUS_RUNNING, vmDashboardEntity.getVm().getStatus());
    System.out.println(vmDashboardEntity.toString());
  }

  @Test
  public void testDowngrade() {
    action.setVmType(Constants.VM_TYPE_BASIC);
    VmDashboardEntity vmDashboardEntity = controller.downgrade(username, action);
    //assertEquals(Constants.STATUS_RUNNING, vmDashboardEntity.getVm().getStatus());
    assertEquals(username, vmDashboardEntity.getVm().getOwner());
    assertNotNull(vmDashboardEntity.getVm().getVmId());
    assertEquals(action.getVmType(), vmDashboardEntity.getVm().getVmType());
    assertTrue(vmDashboardEntity.getHistories().size() > 1);
    //assertEquals(Constants.VM_TYPE_BASIC, vmDashboardEntity.getVm().getVmType());
    assertEquals(Constants.STATUS_RUNNING, vmDashboardEntity.getVm().getStatus());
    System.out.println(vmDashboardEntity.toString());
  }

  @Test
  public void testGetAll() {
    List<VmDashboardEntity> vms = controller.getVMs(username);
    for (VmDashboardEntity vm : vms) {
      System.out.println(vm.toString());
      System.out.println();
    }
  }

  @Test
  public void testGetVM() {
    VmDashboardEntity vmDashboardEntity = controller.getVM(username, String.valueOf(action.getVmId()));
    System.out.println(vmDashboardEntity.toString());
  }


}
