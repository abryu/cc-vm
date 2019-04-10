package abryu.vmmonitor.vm;

import abryu.vmmonitor.vm.objects.UsageHistory;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import java.util.List;

public interface UsageHistoryRepository extends DatastoreRepository<UsageHistory, Long> {


  List<UsageHistory> findAllByAssociatedVmId(Long vmId);

  List<UsageHistory> findAllByAssociatedVmIdOrderByTimestampDesc(Long vmid);



}
