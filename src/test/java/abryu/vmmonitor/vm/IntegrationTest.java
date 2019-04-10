package abryu.vmmonitor.vm;

import static org.junit.Assert.*;

import abryu.vmmonitor.vm.objects.*;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@Category(IntegrationTest.class)
public class IntegrationTest {

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
    action = new UserAction(null, Constants.ACTION_CREATE, Constants.VM_TYPE_LARGE);
  }

  @Test
  public void testDowngrade() {
    action = new UserAction("5643291244625920", Constants.ACTION_CREATE, Constants.VM_TYPE_BASIC);
    VmDashboardEntity vmDashboardEntity = controller.downgrade(username, action);
    System.out.println(controller.getVM(username,"5643291244625920").toString());
  }


  @Ignore
  public void testWorkflow() throws InterruptedException {

    VmDashboardEntity vmDashboardEntity = controller.create(username, action);

    assertEquals(Constants.STATUS_CREATED, vmDashboardEntity.getVm().getStatus());
    assertEquals(username, vmDashboardEntity.getVm().getOwner());
    assertNotNull(vmDashboardEntity.getVm().getVmId());
    assertEquals(action.getVmType(), vmDashboardEntity.getVm().getVmType());
    assertEquals(1, vmDashboardEntity.getHistories().size());

    action.setVmId(vmDashboardEntity.getVm().getVmId());

    vmDashboardEntity = controller.start(username, action);

    assertEquals(Constants.STATUS_RUNNING, vmDashboardEntity.getVm().getStatus());
    assertEquals(username, vmDashboardEntity.getVm().getOwner());
    assertNotNull(vmDashboardEntity.getVm().getVmId());
    assertEquals(action.getVmType(), vmDashboardEntity.getVm().getVmType());
    assertEquals(2, vmDashboardEntity.getHistories().size());

    Thread.sleep(60 * 1000);

    vmDashboardEntity = controller.stop(username, action);

    assertEquals(Constants.STATUS_CREATED, vmDashboardEntity.getVm().getStatus());
    assertEquals(username, vmDashboardEntity.getVm().getOwner());
    assertNotNull(vmDashboardEntity.getVm().getVmId());
    assertEquals(action.getVmType(), vmDashboardEntity.getVm().getVmType());
    assertEquals(3, vmDashboardEntity.getHistories().size());
    assertTrue(vmDashboardEntity.getHistories().get(0).getBalance() > 0);

    action.setVmType(Constants.VM_TYPE_ULTRA);

    vmDashboardEntity = controller.upgrade(username, action);

    assertEquals(username, vmDashboardEntity.getVm().getOwner());
    assertNotNull(vmDashboardEntity.getVm().getVmId());
    assertEquals(action.getVmType(), vmDashboardEntity.getVm().getVmType());
    assertTrue(vmDashboardEntity.getHistories().size() > 1);
    assertEquals(Constants.VM_TYPE_ULTRA, vmDashboardEntity.getVm().getVmType());
    assertEquals(Constants.STATUS_RUNNING, vmDashboardEntity.getVm().getStatus());
    assertTrue(vmDashboardEntity.getHistories().get(0).getBalance() > 0.0);

    Thread.sleep(60 * 1000);

    action.setVmType(Constants.VM_TYPE_LARGE);

    vmDashboardEntity = controller.downgrade(username, action);

    assertEquals(username, vmDashboardEntity.getVm().getOwner());
    assertNotNull(vmDashboardEntity.getVm().getVmId());
    assertEquals(action.getVmType(), vmDashboardEntity.getVm().getVmType());
    assertTrue(vmDashboardEntity.getHistories().size() > 1);
    assertEquals(Constants.VM_TYPE_LARGE, vmDashboardEntity.getVm().getVmType());
    assertEquals(Constants.STATUS_RUNNING, vmDashboardEntity.getVm().getStatus());
    assertTrue(vmDashboardEntity.getHistories().get(0).getBalance() > 0);

    vmDashboardEntity = controller.getVM(username, String.valueOf(action.getVmId()));
    System.out.println(vmDashboardEntity.toString());

  }


}
