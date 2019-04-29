package abryu.vmmonitor.vm;

import abryu.vmmonitor.vm.objects.*;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static abryu.vmmonitor.vm.objects.Constants.calculateCost;
import static abryu.vmmonitor.vm.objects.Constants.getCurrentTimeStamp;

@RequestMapping("/")
@RestController
@Service
public class Controller {

  Logger logger = LogManager.getLogger(Controller.class);

  private final VmRepository vmRepository;
  private final UsageHistoryRepository usageHistoryRepository;

  public Controller(VmRepository vmRepository, UsageHistoryRepository usageHistoryRepository) {
    this.vmRepository = vmRepository;
    this.usageHistoryRepository = usageHistoryRepository;
  }


  @GetMapping("/{username}/health")
  public String getHealth(@PathVariable final String username) {

    try {
      Thread.sleep(1000 * 20);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    logger.info(this.getClass().getName() + "getHealth " + username);

    return "health check : Version built on 4.12 - 13:00";
  }

  @GetMapping("/{username}")
  public List<VmDashboardEntity> getVMs(@PathVariable final String username) {

    logger.info(this.getClass().getName() + "getVMs " + username);

    List<VmDashboardEntity> virtualMachines = new ArrayList<>();
    List<VM> vms = this.vmRepository.findVMSByOwner(username);
    for (VM vm : vms) {
      List<UsageHistory> histories = this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId());
      virtualMachines.add(new VmDashboardEntity(vm, histories));
    }
    return virtualMachines;
  }

  @GetMapping("/{username}/{vmid}")
  public VmDashboardEntity getVM(@PathVariable final String username, @PathVariable final String vmid) {
    logger.info(this.getClass().getName() + "getVM " + username);
    VM vm = this.vmRepository.findById(Long.valueOf(vmid)).orElseThrow(() -> new RuntimeException("VM not exist"));
    if (vm == null)
      return null;
    List<UsageHistory> events = this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId());
    return new VmDashboardEntity(vm, events);
  }

  @PostMapping("/{username}/create")
  public VmDashboardEntity create(@PathVariable final String username, @RequestBody UserAction userAction) {
    logger.info(this.getClass().getName() + "create " + username);
    VM vm = this.vmRepository.save(new VM(username, userAction.getVmType()));
    this.usageHistoryRepository.save(
            new UsageHistory(vm.getVmId(), getCurrentTimeStamp(), vm.getVmType(), Constants.ACTION_CREATE));
    return new VmDashboardEntity(vm, this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId()));
  }


  @PostMapping("/{username}/start")
  public VmDashboardEntity start(@PathVariable final String username, @RequestBody UserAction action) {
    logger.info(this.getClass().getName() + "start " + username);
    VM vm = this.vmRepository.findById(action.getVmId()).orElseThrow(() -> new RuntimeException("VM not exist"));

    if (vm.getStatus().equals(Constants.STATUS_DELETED))
      return new VmDashboardEntity(vm, this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId()));

    if (vm.getStatus().equals(Constants.STATUS_RUNNING)) {
      return new VmDashboardEntity(vm, this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId()));
    }
    vm.setStatus(Constants.STATUS_RUNNING);
    vm = this.vmRepository.save(vm);
    UsageHistory last = getLatestHistoryOfStart(vm.getVmId());
    UsageHistory usageHistory = new UsageHistory(vm.getVmId(), getCurrentTimeStamp(), vm.getVmType(), Constants.ACTION_START);
    usageHistory.setBalance(last.getBalance());
    this.usageHistoryRepository.save(usageHistory);
    return new VmDashboardEntity(vm, this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId()));
  }

  @PostMapping("/{username}/stop")
  public VmDashboardEntity stop(@PathVariable final String username, @RequestBody UserAction action) {
    logger.info(this.getClass().getName() + "stop " + username);
    VM vm = this.vmRepository.findById(action.getVmId()).orElseThrow(() -> new RuntimeException("VM not exist"));
    if (!vm.getStatus().equals(Constants.STATUS_RUNNING)) {
      return new VmDashboardEntity(vm, this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId()));
    }
    vm.setStatus(Constants.STATUS_CREATED);
    vm = this.vmRepository.save(vm);
    UsageHistory usage = new UsageHistory(vm.getVmId(),
            getCurrentTimeStamp(),
            vm.getVmType(),
            Constants.ACTION_STOP);
    UsageHistory last = getLatestHistoryOfStart(vm.getVmId());
    usage.setBalance(calculateCost(last, usage) + last.getBalance());
    this.usageHistoryRepository.save(usage);
    return new VmDashboardEntity(vm, this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId()));
  }


  @PostMapping("/{username}/delete")
  public VmDashboardEntity delete(@PathVariable final String username, @RequestBody UserAction action) {
    logger.info(this.getClass().getName() + "delete " + username);
    VM vm = this.vmRepository.findById(action.getVmId()).orElseThrow(() -> new RuntimeException("VM not exist"));
    if (vm.getStatus().equals(Constants.STATUS_DELETED))
      return new VmDashboardEntity(vm, this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId()));
    if (vm.getStatus().equals(Constants.STATUS_RUNNING)) {
      stop(username, action);
      return delete(username, action);
    }
    vm.setStatus(Constants.STATUS_DELETED);
    vm = this.vmRepository.save(vm);
    UsageHistory usage = new UsageHistory(vm.getVmId(),
            getCurrentTimeStamp(),
            vm.getVmType(),
            Constants.ACTION_DELETE);
    UsageHistory last = getLatestHistoryOfStart(vm.getVmId());
    usage.setBalance(last.getBalance());
    this.usageHistoryRepository.save(usage);
    return new VmDashboardEntity(vm, this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId()));
  }

  @PostMapping("/{username}/downgrade")
  public VmDashboardEntity downgrade(@PathVariable final String username, @RequestBody UserAction action) {
    logger.info(this.getClass().getName() + "downgrade " + username);
    VM vm = this.vmRepository.findById(action.getVmId()).orElseThrow(() -> new RuntimeException("VM not exist"));
    if (vm.getVmType().equals(Constants.VM_TYPE_BASIC) || vm.getVmType().equals(action.getVmOperation()))
      return new VmDashboardEntity(vm, this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId()));
    if (vm.getStatus().equals(Constants.STATUS_RUNNING)) {
      vm = this.stop(username, action).getVm();
    }
    UsageHistory usage = new UsageHistory(vm.getVmId(),
            getCurrentTimeStamp(),
            vm.getVmType(),
            Constants.ACTION_DOWNGRADE);
    UsageHistory last = getLatestHistoryOfStart(vm.getVmId());
    usage.setBalance(last.getBalance());
    this.usageHistoryRepository.save(usage);
    vm.setVmType(action.getVmType());
    vm.setStatus(Constants.STATUS_CREATED);
    this.vmRepository.save(vm);
    return this.start(username, action);
  }

  @PostMapping("/{username}/upgrade")
  public VmDashboardEntity upgrade(@PathVariable final String username, @RequestBody UserAction action) {
    logger.info(this.getClass().getName() + "upgrade " + username);
    VM vm = this.vmRepository.findById(action.getVmId()).orElseThrow(() -> new RuntimeException("VM not exist"));
    if (vm.getVmType().equals(Constants.VM_TYPE_ULTRA) || vm.getVmType().equals(action.getVmOperation()))
      return new VmDashboardEntity(vm, this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vm.getVmId()));

    if (vm.getStatus().equals(Constants.STATUS_RUNNING)) {
      vm = this.stop(username, action).getVm();
    }

    UsageHistory usage = new UsageHistory(vm.getVmId(),
            getCurrentTimeStamp(),
            vm.getVmType(),
            Constants.ACTION_UPGRADE);
    UsageHistory last = getLatestHistoryOfStart(vm.getVmId());
    usage.setBalance(last.getBalance());
    this.usageHistoryRepository.save(usage);
    vm.setVmType(action.getVmType());
    this.vmRepository.save(vm);

    return this.start(username, action);

  }

  public UsageHistory getLatestHistoryOfStart(Long vmid) {
    List<UsageHistory> histories = this.usageHistoryRepository.findAllByAssociatedVmIdOrderByTimestampDesc(vmid);
    return histories.get(0);
  }
}
