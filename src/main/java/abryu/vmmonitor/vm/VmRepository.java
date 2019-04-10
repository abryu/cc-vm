package abryu.vmmonitor.vm;

import abryu.vmmonitor.vm.objects.VM;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import java.util.List;

public interface VmRepository extends DatastoreRepository<VM, Long> {

  List<VM> findVMSByOwner(String owner);

  VM findVMByVmId(Long vmid);

}
